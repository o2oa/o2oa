@echo off
if exist "%~dp0local\update" (
	if not exist "%~dp0commons" (
		mkdir "%~dp0commons"
	)
	if not exist "%~dp0config" (
		mkdir "%~dp0config"
	)
	if not exist "%~dp0configSample" (
		mkdir "%~dp0configSample"
	)
	if not exist "%~dp0local" (
		mkdir "%~dp0local"
	)
	if not exist "%~dp0localSample" (
		mkdir "%~dp0localSample"
	)
	if not exist "%~dp0jvm" (
		mkdir "%~dp0jvm"
	)
	if not exist "%~dp0servers" (
		mkdir "%~dp0servers"
	)
	if not exist "%~dp0store" (
		mkdir "%~dp0store"
	)
	if exist "%~dp0local\update\o2server\version.o2" (
		if exist "%~dp0local\update\o2server\commons" (
			xcopy "%~dp0local\update\o2server\commons" "%~dp0commons" /S /Y /Q
		)
		if exist "%~dp0local\update\o2server\configSample" (
			xcopy "%~dp0local\update\o2server\configSample" "%~dp0configSample" /S /Y /Q
		)
		if exist "%~dp0local\update\o2server\localSample" (
			xcopy "%~dp0local\update\o2server\localSample" "%~dp0localSample" /S /Y /Q
		)
		if exist "%~dp0local\update\o2server\jvm" (
			xcopy "%~dp0local\update\o2server\jvm" "%~dp0jvm" /S /Y /Q
		)
		if exist "%~dp0local\update\o2server\servers" (
			xcopy "%~dp0local\update\o2server\servers" "%~dp0servers" /S /Y /Q
		)
		if exist "%~dp0local\update\o2server\store" (
			xcopy "%~dp0local\update\o2server\store" "%~dp0store" /S /Y /Q
		)
		if exist "%~dp0local\update\o2server\console.jar" (
			copy "%~dp0local\update\o2server\console.jar" "%~dp0"
		)
		if exist "%~dp0local\update\o2server\index.html" (
			copy "%~dp0local\update\o2server\index.html" "%~dp0"
		)
		if exist "%~dp0local\update\o2server\src.zip" (
			copy "%~dp0local\update\o2server\src.zip" "%~dp0"
		)
		if exist "%~dp0start_windows.bat" (
			copy "%~dp0local\update\o2server\start_windows.bat" "%~dp0"
		)
		if exist "%~dp0start_windows_debug.bat" (
			copy "%~dp0local\update\o2server\start_windows_debug.bat" "%~dp0"
		)
		if exist "%~dp0stop_windows.bat" (
			copy "%~dp0local\update\o2server\stop_windows.bat" "%~dp0"
		)
		if exist "%~dp0console_windows.bat" (
			copy "%~dp0local\update\o2server\console_windows.bat" "%~dp0"
		)
		if exist "%~dp0service_windows.bat" (
			copy "%~dp0local\update\o2server\service_windows.bat" "%~dp0"
		)
		if exist "%~dp0start_linux.sh" (
			copy "%~dp0local\update\o2server\start_linux.sh" "%~dp0"
		)
		if exist "%~dp0start_linux_debug.sh" (
			copy "%~dp0local\update\o2server\start_linux_debug.sh" "%~dp0"
		)
		if exist "%~dp0stop_linux.sh" (
			copy "%~dp0local\update\o2server\stop_linux.sh" "%~dp0"
		)
		if exist "%~dp0console_linux.sh" (
			copy "%~dp0local\update\o2server\console_linux.sh" "%~dp0"
		)
		if exist "%~dp0start_macos.sh" (
			copy "%~dp0local\update\o2server\start_macos.sh" "%~dp0"
		)
		if exist "%~dp0start_macos_debug.sh" (
			copy "%~dp0local\update\o2server\start_macos_debug.sh" "%~dp0"
		)
		if exist "%~dp0stop_macos.sh" (
			copy "%~dp0local\update\o2server\stop_macos.sh" "%~dp0"
		)
		if exist "%~dp0console_macos.sh" (
			copy "%~dp0local\update\o2server\console_macos.sh" "%~dp0"
		)
		if exist "%~dp0start_aix.sh" (
			copy "%~dp0local\update\o2server\start_aix.sh" "%~dp0"
		)
		if exist "%~dp0start_aix_debug.sh" (
			copy "%~dp0local\update\o2server\start_aix_debug.sh" "%~dp0"
		)
		if exist "%~dp0stop_aix.sh" (
			copy "%~dp0local\update\o2server\stop_aix.sh" "%~dp0"
		)
		if exist "%~dp0console_aix.sh" (
			copy "%~dp0local\update\o2server\console_aix.sh" "%~dp0"
		)
		if exist "%~dp0start_raspberrypi.sh" (
			copy "%~dp0local\update\o2server\start_raspberrypi.sh" "%~dp0"
		)
		if exist "%~dp0start_raspberrypi_debug.sh" (
			copy "%~dp0local\update\o2server\start_raspberrypi_debug.sh" "%~dp0"
		)
		if exist "%~dp0stop_raspberrypi.sh" (
			copy "%~dp0local\update\o2server\stop_raspberrypi.sh" "%~dp0"
		)
		if exist "%~dp0console_raspberrypi.sh" (
			copy "%~dp0local\update\o2server\console_raspberrypi.sh" "%~dp0"
		)
		if exist "%~dp0start_neokylin_loongson.sh" (
			copy "%~dp0local\update\o2server\start_neokylin_loongson.sh" "%~dp0"
		)
		if exist "%~dp0start_neokylin_loongson_debug.sh" (
			copy "%~dp0local\update\o2server\start_neokylin_loongson_debug.sh" "%~dp0"
		)
		if exist "%~dp0stop_neokylin_loongson.sh" (
			copy "%~dp0local\update\o2server\stop_neokylin_loongson.sh" "%~dp0"
		)
		if exist "%~dp0console_neokylin_loongson.sh" (
			copy "%~dp0local\update\o2server\console_neokylin_loongson.sh" "%~dp0"
		)
		if exist "%~dp0start_kylinos_phytium.sh" (
			copy "%~dp0local\update\o2server\start_kylinos_phytium.sh" "%~dp0"
		)
		if exist "%~dp0start_kylinos_phytium_debug.sh" (
			copy "%~dp0local\update\o2server\start_kylinos_phytium_debug.sh" "%~dp0"
		)
		if exist "%~dp0stop_kylinos_phytium.sh" (
			copy "%~dp0local\update\o2server\stop_kylinos_phytium.sh" "%~dp0"
		)
		if exist "%~dp0console_kylinos_phytium.sh" (
			copy "%~dp0local\update\o2server\console_kylinos_phytium.sh" "%~dp0"
		)
		copy "%~dp0local\update\o2server\version.o2" "%~dp0"
		rmdir /S/Q "%~dp0local\update"
	)
)
@echo on
"%~dp0jvm\windows\bin\java" -server -Xms2g -Xmx5g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -jar "%~dp0console.jar"
pause