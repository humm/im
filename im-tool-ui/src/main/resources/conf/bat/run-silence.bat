@echo off

:: ��ʷ���̴���
tasklist | findstr tool-ui.exe>Nul
if errorlevel 1 (
	@echo δ������ʷ����
) else (
	@echo ֹͣ��ʷ����
	taskkill /f /t /im tool-ui.exe
)

:: �Զ�����������
if not exist "%JAVA_HOME%"\bin\tool-ui.exe (
	@echo �״��������ù���Աִ��
	copy "%JAVA_HOME%"\bin\java.exe %~dp0
	rename %~dp0\java.exe tool-ui.exe
	move %~dp0\tool-ui.exe "%JAVA_HOME%"\bin
)

:: ����
for %%i in ("./*.jar") do ( set jarName=%%~nxi )
tool-ui -jar %jarName%