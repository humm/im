@echo off
setlocal
pushd "%~dp0"
set repos_path="%cd%"
popd

call :find_and_pull "%repos_path%"
::echo finished. & pause>nul
echo finished.
goto :EOF

:find_and_pull
for /d %%i in (%1\*) do (
	cd %%i
	if exist .git (
		echo %%i
		echo start git pull.
		git pull
	) else (
		call :find_and_pull %%i
	)
)
goto :EOF

