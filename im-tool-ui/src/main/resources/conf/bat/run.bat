@echo off

:: Æô¶¯
for %%i in ("./*.jar") do ( set jarName=%%~nxi )
tool-ui -jar %jarName%