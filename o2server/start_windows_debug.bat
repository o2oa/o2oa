@echo off
cd "%~dp0"
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
				echo update %~dp0%%D
				xcopy "%~dp0local\update\o2server\%%D" "%~dp0%%D" /S /Y /Q
			)	
		)
		for  %%F in (console.jar,index.html,src.zip) do (
			if exist "%~dp0local\update\o2server\%%F" (
				echo update %~dp0%%F
				copy "%~dp0local\update\o2server\%%F" "%~dp0"
			)	
		)
		for  %%A in (start,stop,restart,console,service) do (
			for  %%B in (_windows.bat,_windows_debug.bat,_linux.sh._linux_debug.sh,_linux_min.sh,_macosx64.sh,_macosx64_debug.sh,_macosarm.sh,_macosarm_debug.sh,_arm.sh,_arm_debug.sh,_mips.sh,_mips_debug.sh,_raspi.sh,_raspi_debug,_sw.sh,_sw_debug.sh) do (
				if exist "%~dp0local\update\o2server\%%A%%B" (
					echo update %~dp0%%A%%B
					copy "%~dp0local\update\o2server\%%A%%B" "%~dp0"
				)	
			)
		)
		echo update %~dp0version.o2.
		copy "%~dp0local\update\o2server\version.o2" "%~dp0"
		echo clean local\update directory.	
		rmdir /S/Q "%~dp0local\update"
		echo the update is complete, please restart the server.
		exit
	)
)
@echo on
"%~dp0jvm\windows_java11\bin\java" -javaagent:"%~dp0console.jar" -server -Xms2g -Xmx4g -Duser.timezone=GMT+08 -XX:+HeapDumpOnOutOfMemoryError -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=0.0.0.0:20000 -Djava.rmi.server.hostname=127.0.0.1 -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --module-path="%~dp0/commons/module_java11" --upgrade-module-path="%~dp0/commons/module_java11/compiler.jar;%~dp0/commons/module_java11/compiler-management.jar" -jar "%~dp0console.jar"
