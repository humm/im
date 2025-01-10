@echo off

:: 历史进程处理
tasklist | findstr tool-ui-ta.exe>Nul
if errorlevel 1 (
	@echo 未发现历史进程
) else (
	@echo 停止历史进程
	taskkill /f /t /im tool-ui-ta.exe
)

:: 自定义启动命令
if not exist "%JAVA_HOME%"\bin\tool-ui-ta.exe (
	@echo 首次运行请用管理员启动
	copy "%JAVA_HOME%"\bin\java.exe %~dp0
	rename %~dp0\java.exe tool-ui-ta.exe
	move %~dp0\tool-ui-ta.exe "%JAVA_HOME%"\bin
)

:: 启动
set "searchDir=."
set "fileType=*.jar"
:: 查找指定类型文件
for /f %%F in ('dir /b /o-d /a-d "%searchDir%\%fileType%"') do (
    set "startFile=%%F"
    goto :over
)
:over
tool-ui-ta -jar %startFile%

:: tool-ui-ta -jar im-tool-ui-ta-1.0.0.0.jar