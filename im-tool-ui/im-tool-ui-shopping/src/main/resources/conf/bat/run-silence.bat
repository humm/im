@echo off

:: 历史进程处理
tasklist | findstr tool-ui-shopping.exe>Nul
if errorlevel 1 (
	@echo 未发现历史进程
) else (
	@echo 停止历史进程
	taskkill /f /t /im tool-ui-shopping.exe
)

:: 自定义启动命令
if not exist "%JAVA_HOME%"\bin\tool-ui-shopping.exe (
	@echo 首次运行请用管理员启动
	copy "%JAVA_HOME%"\bin\java.exe %~dp0
	rename %~dp0\java.exe tool-ui-shopping.exe
	move %~dp0\tool-ui-shopping.exe "%JAVA_HOME%"\bin
)

:: 启动
:: for %%i in ("./*.jar") do ( set jarName=%%~nxi )
:: tool-ui-shopping -jar %jarName%

tool-ui-shopping -jar im-tool-ui-shopping-1.0.0.0.jar