<h1 align="center">Simple-Chat-Room</h1>

<p align="center">基于socket通信技术（网络编程）实现的简单在线聊天室</p>

<div align="center">

## ✨功能：

本项目仿照QQ，简单实现了QQ的一些基本功能如私聊，群聊，文件发送等，旨在实现**轻量级**的聊天程序 😋


![show](https://github.com/Torchman005/Simple-chat-room/blob/main/show.gif)



![Static Badge](https://img.shields.io/badge/Language-Java-orange?style=plastic)
![Static Badge](https://img.shields.io/badge/Language-SQL-blue?style=plastic)

![Static Badge](https://img.shields.io/badge/API-javafx-purple?style=plastic)
![Static Badge](https://img.shields.io/badge/API-Junit-green?style=plastic)
![Static Badge](https://img.shields.io/badge/API-JDBC-yellow?style=plastic)



## 🎉技术栈：

Linux，JavaSE，mysql, Junit

## 🔧构建工具：

idea,vscode,navicat

## ✏外部依赖；

JUnit,JDBC,JavaFX（maven管理）

## 📌运行环境：

chatclient于客户端运行，chatserver于服务端运行，并且保持网络通畅（必要时手动配置数据库信息）

确保客户端与服务端都已安装JDK17并配置环境变量

## 🔑开始使用

**使用前请手动填写位于client端的.properties配置文件指定服务器公网ip**

**端口号默认为2323**

双击start.bat运行启动脚本

或者在项目文件夹中打开命令行运行以下指令：

服务端启动

```shell
java -jar ChatClient-1.0-SNAPSHOT.jar
```

客户端启动

```
java -jar ServerClient-1.0-SNAPSHOT.jar
```


## 🔔FAQ：

若遇到客户端无法连接到服务器，请检查服务器防火墙对应端口是否开放，或检查及配置服务器供应商的安全组规则
