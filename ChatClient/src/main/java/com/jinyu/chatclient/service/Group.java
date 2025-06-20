package com.jinyu.chatclient.service;

import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.dbexecute.SQLs;
import com.jinyu.utils.Utility;

public class Group {
    SQLs sqLs = new SQLs();
    public void pullGroup(String senderId) {
        Message mes = new Message();

        System.out.println("群聊名？");
        String groupName = Utility.readString(20);
        mes.setGroupName(groupName);
        sqLs.insertGroupMembers(groupName, senderId);
        System.out.println("(输入ok结束输入)你要邀请谁进群？");
        while (true) {
            String userId = Utility.readString(20);
            if (userId.equals("ok")) {
                break;
            } else {
                sqLs.insertGroupMembers(groupName,userId);
            }
        }
        mes.setMesType(MessageType.MESSAGE_PULL_GROUP_MES);
        mes.setGroupMembers(sqLs.getGroupMembers(groupName));

        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    ClientConnServerThreadsManage.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(mes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
