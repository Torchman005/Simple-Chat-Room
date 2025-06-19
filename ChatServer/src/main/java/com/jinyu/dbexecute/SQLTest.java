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
    }

}
