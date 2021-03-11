if exist "%~dp0local\update" (
	for  %%D in (commons,config,configSample,local,localSample,jvm,servers,store) do (
		if not exist "%~dp0%%D" (
			mkdir "%~dp0%%D"
		)	
	)
	if exist "%~dp0local\update\o2server\version.o2" (
		echo "update o2server."
		for  %%D in (commons,config,configSample,local,localSample,jvm,servers,store) do (
			if exist "%~dp0local\update\o2server\%%D" (
				echo "update %~dp0%%D"
				xcopy "%~dp0local\update\o2server\%%D" "%~dp0%%D" /S /Y /Q
			)	
		)
		for  %%F in (console.jar,index.html,src.zip) do (
			if exist "%~dp0local\update\o2server\%%F" (
				echo "update %~dp0%%F"
				copy "%~dp0local\update\o2server\%%F" "%~dp0"
			)	
		)
		for  %%A in (start,stop,restart,console,service) do (
			for  %%B in (_windows.bat,_linux.sh,_macos.sh,_arm.sh,_mips.sh,_raspi.sh,_aix.sh) do (
				if exist "%~dp0local\update\o2server\%%A%%B" (
					echo "update %~dp0%%A%%B"
					copy "%~dp0local\update\o2server\%%A%%B" "%~dp0"
				)	
			)
		)
		echo "update  ${current_dir}/version.o2."
		copy "%~dp0local\update\o2server\version.o2" "%~dp0"
		echo "clean local/update directory."		
		rmdir /S/Q "%~dp0local\update"
		echo "the update is complete, please restart the server."
		exit
	)
)
"%~dp0jvm\windows_java11\bin\java" -javaagent:"%~dp0console.jar=java11" -server -Xms2g -Duser.timezone=GMT+08 -XX:+HeapDumpOnOutOfMemoryError -jar "%~dp0console.jar"
