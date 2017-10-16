@echo off
setlocal enabledelayedexpansion
for /f "delims=  tokens=1" %%i in ('netstat -aon ^| findstr "6379"') do (
set a=%%i
goto js
)
:js
taskkill /f /pid "!a:~71,5!"

c:
cd \Users\acer\Desktop\redis-3.2.0\redis-3.2.0
redis-server.exe redis_6379.conf