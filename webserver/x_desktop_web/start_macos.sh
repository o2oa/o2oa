current_dir=$(cd "$(dirname "$0")"; pwd);
if [ -d ${current_dir}/local/update ]; then
	if [ ! -d ${current_dir}/commons ]; then
	mkdir ${current_dir}/commons
	fi
	if [ ! -d ${current_dir}/config ]; then
		mkdir ${current_dir}/config
	fi
	if [ ! -d ${current_dir}/config/sample ]; then
		mkdir ${current_dir}/config/sample
	fi
	if [ ! -d ${current_dir}/local ]; then
		mkdir ${current_dir}/local
	fi
	if [ ! -d ${current_dir}/local ]; then
		mkdir ${current_dir}/local
	fi
	if [ ! -d ${current_dir}/local/sample ]; then
		mkdir ${current_dir}/local/sample
	fi
	if [ ! -d ${current_dir}/jvm ]; then
		mkdir ${current_dir}/jvm
	fi
	if [ ! -d ${current_dir}/servers ]; then
		mkdir ${current_dir}/servers
	fi
	if [ ! -d ${current_dir}/store ]; then
		mkdir ${current_dir}/store
	fi
	if [ -f ${current_dir}/local/update/o2server/version.o2 ]; then
		if [ -d ${current_dir}/local/update/o2server/config ]; then
			if [ -d ${current_dir}/local/update/o2server/config/sample ]; then
				cp -Rf ${current_dir}/local/update/o2server/config/sample ${current_dir}/config/
			fi
		fi
		if [ -d ${current_dir}/local/update/o2server/local ]; then
			if [ -d ${current_dir}/local/update/o2server/local/sample ]; then
				cp -Rf ${current_dir}/local/update/o2server/local/sample ${current_dir}/local/
			fi
		fi
		if [ -d ${current_dir}/local/update/o2server/commons ]; then
			cp -Rf ${current_dir}/local/update/o2server/commons ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/jvm ]; then
			cp -Rf ${current_dir}/local/update/o2server/jvm ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/servers ]; then
			cp -Rf ${current_dir}/local/update/o2server/servers ${current_dir}/
		fi
		if [ -d ${current_dir}/local/update/o2server/store ]; then
			cp -Rf ${current_dir}/local/update/o2server/store ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/console.jar ]; then
			cp -f ${current_dir}/local/update/o2server/console.jar ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/index.html ]; then
			cp -f ${current_dir}/local/update/o2server/index.html ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/start_windows.bat ]; then
			cp -f ${current_dir}/local/update/o2server/start_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/stop_windows.bat ]; then
			cp -f ${current_dir}/local/update/o2server/stop_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/console_windows.bat ]; then
			cp -f ${current_dir}/local/update/o2server/console_windows.bat ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/start_linux.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_linux.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/stop_linux.sh ]; then
			cp -f ${current_dir}/local/update/o2server/stop_linux.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/console_linux.sh ]; then
			cp -f ${current_dir}/local/update/o2server/console_linux.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/start_macos.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_macos.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/stop_macos.sh ]; then
			cp -f ${current_dir}/local/update/o2server/stop_macos.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/console_macos.sh ]; then
			cp -f ${current_dir}/local/update/o2server/console_macos.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/start_aix.sh ]; then
			cp -f ${current_dir}/local/update/o2server/start_aix.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/stop_aix.sh ]; then
			cp -f ${current_dir}/local/update/o2server/stop_aix.sh ${current_dir}/
		fi
		if [ -f ${current_dir}/local/update/o2server/console_aix.sh ]; then
			cp -f ${current_dir}/local/update/o2server/console_aix.sh ${current_dir}/
		fi
		cp ${current_dir}/local/update/o2server/version.o2 ${current_dir}/
		rm -Rf ${current_dir}/local/update
	fi
fi
sudo $(cd "$(dirname "$0")"; pwd)/jvm/macos/bin/java -Xms3g -XX:+UseConcMarkSweepGC -jar $(cd "$(dirname "$0")"; pwd)/console.jar