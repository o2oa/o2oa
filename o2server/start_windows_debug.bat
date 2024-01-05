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
			for  %%B in (_windows.bat,_linux.sh,_linux_min.sh,_macos.sh,_arm.sh,_mips.sh,_raspi.sh,_aix.sh) do (
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
"%~dp0jvm\windows_java11\bin\java" -javaagent:"%~dp0console.jar" -server -Xms2g -Xmx4g -Dnashorn.args=--no-deprecation-warning --add-exports jdk.scripting.nashorn/jdk.nashorn.internal.runtime=ALL-UNNAMED --add-exports jdk.scripting.nashorn/jdk.nashorn.internal.runtime.arrays=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED -Duser.timezone=GMT+08 -XX:+HeapDumpOnOutOfMemoryError -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=0.0.0.0:20000 -Djava.rmi.server.hostname=127.0.0.1 -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI --module-path=${current_dir}/commons/module_java11 --upgrade-module-path=${current_dir}/commons/module_java11/compiler.jar:${current_dir}/commons/module_java11/compiler-management.jar -jar "%~dp0console.jar"
