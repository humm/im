@echo off

:: 启动
set "searchDir=."
set "fileType=*.jar"
:: 查找指定类型文件
for /f %%F in ('dir /b /o-d /a-d "%searchDir%\%fileType%"') do (
    set "startFile=%%F"
    goto :over
)
:over

set location=%cd%\download\
md location
copy %startFile% location /y

im-tool-ui-ta -jar %startFile%

