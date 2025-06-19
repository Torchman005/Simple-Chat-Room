package com.jinyu.dbexecute;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

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
                "userid varchar(32) primary key," +
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
            // 用户名不存在
            return false;
        }
        //验证密码
        return BCrypt.checkpw(pwd, storedHash);

    }

    public String getPasswordHashByUsername(Connection conn,String userId) {
        // 编写获取密码hash密文方法
        String sql = "select pwd from users where userid = ?;";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,userId);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getString("pwd");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
