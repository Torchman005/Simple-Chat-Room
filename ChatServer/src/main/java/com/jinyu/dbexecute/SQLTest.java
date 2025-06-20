package com.jinyu.dbexecute;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class SQLTest {
    public void Test() throws SQLException {
        String userId = "anno";
        String pwd = "123456";
        String groupName = "mygo";
        Properties prop = new Properties();
        String driver;
        String url;
        String username;
        String password;
        ClassLoader classLoader = SQLs.class.getClassLoader();
        InputStream input = classLoader.getResourceAsStream("config.properties");
        try {
            prop.load(input);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        driver = prop.getProperty("driver");
        url = prop.getProperty("url");
        username = prop.getProperty("username");
        password = prop.getProperty("password");
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
                "userid varchar(32) primary key," +
                "pwd varchar(200) not null," +
                "create_at timestamp default current_timestamp" +
                ");");
        // 用户密码使用密文存储
        String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
        // 使用PreparedStatement防止sql注入
        String insertSql = "insert into users (userid,pwd) values(?,?);";
        PreparedStatement pstmt = conn.prepareStatement(insertSql);
        pstmt.setString(1, userId);
        pstmt.setString(2, hashedPwd);
        statement.close();
        conn.close();

        try {
            conn = DriverManager.getConnection(url, username, password);
            statement = conn.createStatement();
            statement.execute("use chat");
            // 创建群聊表
            String sql1 = "create table if not exists `groups` (" +
                    "group_name varchar(32) primary key not null comment '群聊名'," +
                    "build_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'" +
                    ");";
            pstmt = conn.prepareStatement(sql1);
            pstmt.execute();
            // 创建群聊成员表
            String sql = "create table if not exists group_members (" +
                    "user_id varchar(32) primary key not null comment '成员名'," +
                    "group_name varchar(32) not null comment '所在群聊'," +
                    "join_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间'" +
                    ");";
            pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            // 在数据库中添加群
            String insertSql1 = "insert into `groups` (group_name) values(?);";
            pstmt = conn.prepareStatement(insertSql1);
            pstmt.setString(1,groupName);
            int jubge = pstmt.executeUpdate();
            System.out.println(jubge);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
