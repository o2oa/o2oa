current_dir=$(cd "$(dirname "$0")"; pwd);
if [ -d ${current_dir}/local/update ]; then
	if [ ! -d ${current_dir}/commons ]; then
	sudo mkdir ${current_dir}/commons
	fi
	if [ ! -d ${current_dir}/config ]; then
		sudo mkdir ${current_dir}/config
	fi
	if [ ! -d ${current_dir}/configSample ]; then
		sudo mkdir ${current_dir}/configSample
	fi
	if [ ! -d ${current_dir}/local ]; then
		sudo mkdir ${current_dir}/local
	fi
	if [ ! -d ${current_dir}/local ]; then
		sudo mkdir ${current_dir}/local
	fi
	if [ ! -d ${current_dir}/localSample ]; then
		sudo mkdir ${current_dir}/localSample
	fi
	if [ ! -d ${current_dir}/jvm ]; then
		sudo mkdir ${current_dir}/jvm
	fi
	if [ ! -d ${current_dir}/servers ]; then
		sudo mkdir ${current_dir}/servers
	fi
	if [ ! -d ${current_dir}/store ]; then
		sudo mkdir ${current_dir}/store
	fi
	if [ -f ${current_dir}/local/update/o2server/version.o2 ]; then
		if [ -d ${current_dir}/local/update/o2server/configSample ]; then
			sudo cp -Rf ${current_dir}/local/update/o2server/configSample ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/localSample ]; then
			sudo cp -Rf ${current_dir}/local/update/o2server/localSample ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/commons ]; then
			sudo cp -Rf ${current_dir}/local/update/o2server/commons ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/jvm ]; then
			sudo cp -Rf ${current_dir}/local/update/o2server/jvm ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/servers ]; then
			sudo cp -Rf ${current_dir}/local/update/o2server/servers ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/store ]; then
			sudo cp -Rf ${current_dir}/local/update/o2server/store ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/console.jar ]; then
			sudo cp -f ${current_dir}/local/update/o2server/console.jar ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/index.html ]; then
			sudo cp -f ${current_dir}/local/update/o2server/index.html ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/src.zip ]; then
			sudo cp -f ${current_dir}/local/update/o2server/src.zip ${current_dir}/
		fi
		if [ -f ${current_dir}/start_windows.bat ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/start_windows_debug.bat ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_windows_debug.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_windows.bat ]; then
			sudo cp -f ${current_dir}/local/update/o2server/stop_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/console_windows.bat ]; then
			sudo cp -f ${current_dir}/local/update/o2server/console_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/service_windows.bat ]; then
			sudo cp -f ${current_dir}/local/update/o2server/service_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/start_linux.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_linux.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_linux_debug.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_linux_debug.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_linux.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/stop_linux.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/console_linux.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/console_linux.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_macos.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_macos.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_macos_debug.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_macos_debug.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_macos.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/stop_macos.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/console_macos.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/console_macos.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_aix.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_aix.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_aix_debug.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/start_aix_debug.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_aix.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/stop_aix.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/console_aix.sh ]; then
			sudo cp -f ${current_dir}/local/update/o2server/console_aix.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_raspberrypi.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_raspberrypi.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_raspberrypi_debug.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_raspberrypi_debug.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_raspberrypi.sh ]; then
			cp -f ${current_dir}/local/update/o2server/stop_raspberrypi.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/console_raspberrypi.sh ]; then
			cp -f ${current_dir}/local/update/o2server/console_raspberrypi.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_neokylin_loongson.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_neokylin_loongson.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_neokylin_loongson_debug.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_neokylin_loongson_debug.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_neokylin_loongson.sh ]; then
			cp -f ${current_dir}/local/update/o2server/stop_neokylin_loongson.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/console_neokylin_loongson.sh ]; then
			cp -f ${current_dir}/local/update/o2server/console_neokylin_loongson.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_kylinos_phytium.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_kylinos_phytium.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/start_kylinos_phytium_debug.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_kylinos_phytium_debug.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/stop_kylinos_phytium.sh ]; then
			cp -f ${current_dir}/local/update/o2server/stop_kylinos_phytium.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/console_kylinos_phytium.sh ]; then
			cp -f ${current_dir}/local/update/o2server/console_kylinos_phytium.sh ${current_dir}/
		fi
		sudo cp ${current_dir}/local/update/o2server/version.o2 ${current_dir}/
		sudo rm -Rf ${current_dir}/local/update
	fi
fi
sudo setsid ${current_dir}/jvm/raspberrypi/bin/java -server -Xms2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -jar ${current_dir}/console.jar