package com.jinyu.chatserver.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Queue;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;

import javax.swing.*;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;
    private Groups groups = new Groups();

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(userId + "已连接并保持通信");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mes = (Message) ois.readObject();
                // 获取在线用户列表并且发给客户端
                if (mes.getMesType().equals(MessageType.MESSAGE_REQ_ONLINE_USERS)) {
                    System.out.println(mes.getSender() + "请求获取在线用户列表");
                    Queue<String> onlineUsers = ClientThreadsManage.getOnlineUsers();
                    System.out.println("服务端返回在线用户列表：" + onlineUsers);
                    Message mes2 = new Message();
                    mes2.setMesType(MessageType.MESSAGE_RET_ONLINE_USERS_LIST);
                    mes2.setOnlineUsers(onlineUsers);
                    mes2.setGetter(mes.getSender());

                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(mes2);
                } else if (mes.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    // 调用方法
                    ClientThreadsManage.removeSCCThread(mes.getSender());
                    System.out.println(userId + "退出登录");
                    OnlineUsers.deleteUser(userId);
                    socket.close(); // 关闭socket连接
                    break;
                } else if (mes.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    // 私聊转发
                    ObjectOutputStream oos = null;
                    try {
                        oos = new ObjectOutputStream(ClientThreadsManage.getServerConnectClientThread(mes.getGetter())
                                .getSocket().getOutputStream());
                        oos.writeObject(mes);// 若要离线留言，可发送给数据库
                    } catch (Exception e) {
                        System.out.println(mes.getGetter() + "不在线，无法私聊");
                    }
                } else if (mes.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    // 文件转发
                    ServerConnectClientThread thread = ClientThreadsManage
                            .getServerConnectClientThread(mes.getGetter());
                    ObjectOutputStream oos = new ObjectOutputStream(thread.getSocket().getOutputStream());
                    oos.writeObject(mes);
                } else if (mes.getMesType().equals(MessageType.MESSAGE_PULL_GROUP_MES)) {
                    // 拉群，并将群存储
                    groups.addGroup(mes.getGroupName());
                    // 新建群聊后，主动推送群聊列表给所有群成员
                    Queue<String> members = mes.getGroupMembers();
                    if (members != null) {
                        for (String member : members) {
                            groups.sqLs.insertGroupMembers(mes.getGroupName(), member);
                            sendGroupListToUser(member);
                        }
                    }
                } else if (mes.getMesType().equals(MessageType.MESSAGE_TO_GROUP_MES)) {
                    // 群发消息
                    // 判断是否有此群聊
                    if (groups.hasGroup(mes.getGroupName())) {
                        mes.setGroup(true);
                        Queue<String> group = groups.getGroup(mes.getGroupName());
                        Iterator<String> iterator = group.iterator();
                        while (iterator.hasNext()) {
                            String onlineUser = iterator.next();
                            if (!onlineUser.equals(mes.getSender())) {
                                // 排除发消息的用户
                                ObjectOutputStream oos = new ObjectOutputStream(ClientThreadsManage
                                        .getServerConnectClientThread(onlineUser).getSocket().getOutputStream());
                                oos.writeObject(mes);
                            }
                        }
                    } else {
                        // 将不存在群组的信息发送给客户端
                        System.out.println("不存在此群聊");
                        mes.setGroup(false);
                        ObjectOutputStream oos = new ObjectOutputStream(ClientThreadsManage
                                .getServerConnectClientThread(mes.getSender()).getSocket().getOutputStream());
                        oos.writeObject(mes);
                    }
                } else if (mes.getMesType().equals(com.jinyu.chatcommon.MessageType.MESSAGE_REQ_GROUP_LIST)) {
                    // 客户端请求群聊列表
                    sendGroupListToUser(mes.getSender());
                } else {
                    System.out.println("其他类型的信息，暂时不作处理");
                }
            } catch (IOException e) {
                // 处理客户端异常断开连接的情况
                System.out.println(userId + "退出登录");
                ClientThreadsManage.removeSCCThread(userId);
                OnlineUsers.deleteUser(userId);
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // 新增：推送群聊列表给指定用户
    private void sendGroupListToUser(String userId) {
        try {
            java.util.List<String> groupList = groups.getGroupsForUser(userId);
            Message groupListMsg = new Message();
            groupListMsg.setMesType(com.jinyu.chatcommon.MessageType.MESSAGE_RET_GROUP_LIST);
            groupListMsg.setGetter(userId);
            // 用content存储群聊名列表，逗号分隔
            groupListMsg.setContent(String.join(",", groupList));
            ObjectOutputStream oos = new ObjectOutputStream(
                    ClientThreadsManage.getServerConnectClientThread(userId).getSocket().getOutputStream());
            oos.writeObject(groupListMsg);
        } catch (Exception e) {
            System.out.println("推送群聊列表给" + userId + "失败: " + e.getMessage());
        }
    }
}
