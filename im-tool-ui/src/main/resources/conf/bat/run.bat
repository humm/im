@echo off

:: 启动
for %%i in ("./*.jar") do ( set jarName=%%~nxi )
java -jar %jarName%