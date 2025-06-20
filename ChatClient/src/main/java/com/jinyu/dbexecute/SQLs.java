package com.jinyu.dbexecute;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

public class SQLs {
    Properties prop = new Properties();
    String driver;
    String url;
    String username;
    String password;
    // 利用实例代码块加载配置文件信息
    {
        ClassLoader classLoader = SQLs.class.getClassLoader();
        InputStream input = classLoader.getResourceAsStream("config.properties");
        try {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        driver = prop.getProperty("driver");
        url = prop.getProperty("url");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
    }
    public boolean register(boolean ex,String userId, String pwd) throws SQLException {
        try {
            // 注册驱动
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // 获取数据库连接
        Connection conn = DriverManager.getConnection(url, username, password);
        Statement statement  = conn.createStatement();
        statement.execute("create database if not exists chat;");
        statement.execute("use chat;");
        statement.execute("create table if not exists users (" +
                "user_id varchar(32) primary key," +
                "pwd varchar(200) not null," +
                "create_at timestamp default current_timestamp" +
                ");");
        // 用户密码使用密文存储
        String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
        // 使用PreparedStatement防止sql注入
        String insertSql = "insert into users (userid,pwd) values(?,?);";
        try {
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, userId);
            pstmt.setString(2, hashedPwd);
            int jubge = pstmt.executeUpdate();
            statement.close();
            conn.close();
            return jubge > 0;
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062){
                //  判断用户名是否已存在
                System.out.println("用户名已存在");
                ex = true;
            } else {
                System.out.println("注册错误: " + e.getMessage());
            }
            return false;
        }

    }

    public boolean login(String userId,String pwd) throws SQLException {
        Connection conn = DriverManager.getConnection(url,username, password);
        Statement statement = conn.createStatement();
        statement.execute("use chat;");
        //  根据用户名获取密码hash密文
        String storedHash = getPasswordHashByUsername(conn,userId);
        if(storedHash == null){
            statement.close();
            conn.close();
            // 用户名不存在
            return false;
        }
        //验证密码
        statement.close();
        conn.close();
        return BCrypt.checkpw(pwd, storedHash);

    }

    public String getPasswordHashByUsername(Connection conn,String userId) {
        // 编写获取密码hash密文方法
        String sql = "select pwd from users where user_id = ?;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,userId);
            try(ResultSet rs = pstmt.executeQuery()){
                // 判断是否查询到对应密码
                if(rs.next()){
                    return rs.getString("pwd");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean buildGroup(String groupName){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement statement = conn.createStatement();
            statement.execute("use chat");
            // 创建群聊表
            String sql = "create table if not exists `groups` (" +
                    "group_name varchar(32) primary key not null comment '群聊名'," +
                    "build_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'" +
                    ");";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            // 创建群聊成员表
            String sql1 = "create table if not exists group_members (" +
                    "user_id varchar(32) primary key not null comment '成员名'," +
                    "group_name varchar(32) not null comment '所在群聊'," +
                    "join_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间'" +
                    ");";
            pstmt = conn.prepareStatement(sql1);
            pstmt.execute();
            // 在数据库中添加群
            // groups是sql关键字，应用`符号转义
            String insertSql = "insert into `groups` (group_name) values (?);";
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1,groupName);
            int jubge = pstmt.executeUpdate();
            pstmt.close();
            statement.close();
            conn.close();
            // 判断执行是否成功
            return jubge > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertGroupMembers(String groupName,String userId){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "insert into group_members (user_id, group_name) values (?,?);";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,userId);
            pstmt.setString(1,groupName);
            int jubge = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
//            判断执行是否成功
            return jubge > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isGroup(String groupName){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "select build_time where group_name = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,groupName);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return true;
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Queue<String> getGroupMembers(String groupName){
        Queue<String> members = new LinkedList<>();
        try {
            Connection conn = DriverManager.getConnection(url,username,password);
            Statement statement = conn.createStatement();
            statement.execute("use chat");
            String sql = "select user_id from group_members where group_name = ?;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,groupName);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String member = rs.getString("user_id");
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }
}
