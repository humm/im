@echo off

:: ����
for %%i in ("./*.jar") do ( set jarName=%%~nxi )
tool-ui -jar %jarName%