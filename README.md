<h1 align="center">Simple-Chat-Room</h1>

<p align="center">A simple online chat room based on socket communication technology (network programming)</p>

<p align="center">基于socket通信技术（网络编程）实现的简单在线聊天室</p>

<div align="center">

## ✨function  功能:

This project is modeled on QQ, and simply implements some basic functions of QQ such as private chat, group chat, file sending, etc., aiming to achieve a lightweight chat program 😋

本项目仿照QQ，简单实现了QQ的一些基本功能如私聊，群聊，文件发送等，旨在实现**轻量级**的聊天程序 😋


![show](https://github.com/Torchman005/Simple-chat-room/blob/main/show.gif)



![Static Badge](https://img.shields.io/badge/Language-Java-orange?style=plastic)
![Static Badge](https://img.shields.io/badge/Language-SQL-blue?style=plastic)

![Static Badge](https://img.shields.io/badge/API-javafx-purple?style=plastic)
![Static Badge](https://img.shields.io/badge/API-Junit-green?style=plastic)
![Static Badge](https://img.shields.io/badge/API-JDBC-yellow?style=plastic)



## 🎉tech stack  技术栈:

Linux，JavaSE，mysql, Junit

## 🔧build tools  构建工具:

idea,vscode,navicat

## ✏external dependencies  外部依赖:

JUnit,JDBC,JavaFX（maven管理）

## 📌operating environment  运行环境:

ChatClient runs on the client side, ChatServer runs on the server side, and the network is kept unobstructed (manually configure database information if necessary)

chatclient于客户端运行，chatserver于服务端运行，并且保持网络通畅（必要时手动配置数据库信息）

Make sure that JDK17 is installed on both the client and the server and that environment variables are configured

确保客户端与服务端都已安装JDK17并配置环境变量

## 🔑get started  开始使用:

**Before use, manually enter the .properties configuration file on the client to specify the public IP address of the server**

**使用前请手动填写位于client端的.properties配置文件指定服务器公网ip**

**The default port number is 2323**

**端口号默认为2323**

Double-click start.bat run the startup script

双击start.bat运行启动脚本

Or open the command line in the project folder and run the following command:

或者在项目文件夹中打开命令行运行以下指令：

Server launch   服务端启动

```shell
java -jar ChatClient-1.0-SNAPSHOT.jar
```

Client launch   客户端启动

```
java -jar ServerClient-1.0-SNAPSHOT.jar
```


## 🔔FAQ:

If the client is unable to connect to the server, check whether the corresponding port of the server firewall is open, or check and configure the security group rules of the server vendor

若遇到客户端无法连接到服务器，请检查服务器防火墙对应端口是否开放，或检查及配置服务器供应商的安全组规则
