@echo off
for %%i in ("./*.jar") do ( set jarName=%%~nxi )
java -jar %jarName%