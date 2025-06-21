@echo off
chcp 65001 >nul
set JAVA_OPTS=-Dfile.encoding=UTF-8
set JAR_PATH=target\chat-client-1.0-SNAPSHOT-jar-with-dependencies.jar

echo 正在启动聊天客户端...
echo.

if not exist %JAR_PATH% (
    echo 错误：未找到 %JAR_PATH%
    echo 请先运行 mvn clean package -DskipTests 编译项目
    pause
    exit /b 1
)

echo 找到jar包：%JAR_PATH%
echo 启动参数：%JAVA_OPTS%
echo.

java %JAVA_OPTS% -jar %JAR_PATH%

if %ERRORLEVEL% neq 0 (
    echo.
    echo 程序启动失败，错误代码：%ERRORLEVEL%
    echo 请检查：
    echo 1. Java版本是否为17或以上
    echo 2. 是否安装了JavaFX
    echo 3. 服务器是否已启动
    echo.
    pause
) else (
    echo.
    echo 程序正常退出
    pause
) 