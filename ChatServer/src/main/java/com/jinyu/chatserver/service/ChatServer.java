package com.jinyu.chatserver.service;

import com.jinyu.chatcommon.Message;
import com.jinyu.chatcommon.MessageType;
import com.jinyu.chatcommon.User;
import com.jinyu.chatcommon.UserType;
import com.jinyu.dbexecute.SQLs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ChatServer {
    ServerSocket ss = null;
    SQLs sqls = new SQLs();
    // 将用户信息统一存放到项目根目录 Simple-Chat-Room-main/users.txt
    private static final String USER_FILE = "." + File.separator + "users.txt";
    private static Map<String, String> userMap = new HashMap<>();

    // 加载用户信息
    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length == 2)
                    userMap.put(arr[0], arr[1]);
            }
        } catch (Exception e) {
            /* 文件不存在时忽略 */ }
    }

    public ChatServer() throws Exception {
        try {
            // 启动推送新闻的线程
            new Thread(new SendNewsToAllService()).start();

            // 读取配置文件
            ClassLoader classLoader = ChatServer.class.getClassLoader();
            InputStream input = classLoader.getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            String sport = prop.getProperty("port");
            int port = Integer.parseInt(sport);
            System.out.println("服务端在" + port + "端口监听");
            ss = new ServerSocket(port);

            // 加载用户信息
            loadUsers();

            while (true) {
                Socket socket = ss.accept();// 监听客户端的连接，若没有则阻塞

                // 对象输入输出流读取用户对象
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User user = (User) ois.readObject();
                // 准备一个Message对象，用来回复客户端
                Message message = new Message();

                if (user.getUserType().equals(UserType.USER_LOGIN)) {
                    // 登录校验
                    if (sqls.login(user.getUserId(), user.getPwd())) {
                        message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                        // 加入线程
                        ServerConnectClientThread thread = new ServerConnectClientThread(socket, user.getUserId());
                        thread.start();
                        ClientThreadsManage.addServerConnectClientThread(user.getUserId(), thread);
                        // 将用户Id加入在线用户队列
                        OnlineUsers.addOnlineUsers(user.getUserId());
                        // 然后把message传给客户端
                        oos.writeObject(message);
                    } else {
                        // 登录失败
                        System.out.println("用户账号或密码不正确");
                        message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                        oos.writeObject(message);
                        socket.close();
                    }
                } else if (user.getUserType().equals(UserType.USER_REGISTER)) {
                    // 用来判断是否是用户名已存在错误
                    boolean ex = false;
                    // 判断是否登陆成功
                    boolean rg = false;
                    rg = sqls.register(ex,user.getUserId(), user.getPwd());
                    // 注册校验
                    if (rg == true && ex == false) {
                        message.setMesType(MessageType.MESSAGE_REGISTER_SUCCEED);
                    } else if(rg == false && ex == true){
                        message.setMesType(MessageType.MESSAGE_USERID_EXISTS);
                    } else {
                        message.setMesType(MessageType.MESSAGE_REGISTER_FAIL);
                    }
                    oos.writeObject(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ss != null)
                ss.close();
        }
    }
}