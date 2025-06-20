@echo off
REM Windows 版本的 package.sh
cd /d %~dp0\..\web
if exist dist rmdir /s /q dist
cd ..
set NODE_OPTIONS=--openssl-legacy-provider
mvn clean package -DskipTests -Dmaven.npm.skip=false 