@echo off
if exist "%~dp0local\update" (
	for  %%D in (commons,config,configSample,local,localSample,jvm,servers,store) do (
		if not exist "%~dp0%%D" (
			mkdir "%~dp0%%D"
		)	
	)
	if exist "%~dp0local\update\o2server\version.o2" (
		for  %%D in (commons,config,configSample,local,localSample,jvm,servers,store) do (
			if exist "%~dp0local\update\o2server\%%D" (
				xcopy "%~dp0local\update\o2server\%%D" "%~dp0%%D" /S /Y /Q
			)	
		)
		for  %%F in (console.jar,index.html,src.zip) do (
			if exist "%~dp0local\update\o2server\%%F" (
				copy "%~dp0local\update\o2server\%%F" "%~dp0"
			)	
		)
		for  %%A in (start,stop,console,service) do (
			for  %%B in (windows,linux,macos,raspi,arm,mips) do (
				for  %%C in (.bat,_java11.bat,.sh,_java.sh) do (
					if exist "%~dp0local\update\o2server\%%A_%%B%%C" (
						copy "%~dp0local\update\o2server\%%A_%%B%%C" "%~dp0"
					)	
				)	
			)
		)
		copy "%~dp0local\update\o2server\version.o2" "%~dp0"
		rmdir /S/Q "%~dp0local\update"
	)
)
@echo on
"%~dp0jvm\windows_java11\bin\java" -javaagent:"%~dp0console.jar=java11" -server -Xms2g -Duser.timezone=GMT+08 -XX:+HeapDumpOnOutOfMemoryError -jar "%~dp0console.jar"
pause
