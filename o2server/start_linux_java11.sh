#!/bin/bash
# Copyright (c) http://www.o2oa.net/
current_dir="$(cd "$(dirname "$0")"; pwd)"
if [ -d ${current_dir}/local/update ]; then
	if [ ! -d ${current_dir}/commons ]; then
	mkdir ${current_dir}/commons
	fi
	if [ ! -d ${current_dir}/config ]; then
		mkdir ${current_dir}/config
	fi
	if [ ! -d ${current_dir}/configSample ]; then
		mkdir ${current_dir}/configSample
	fi
	if [ ! -d ${current_dir}/local ]; then
		mkdir ${current_dir}/local
	fi
	if [ ! -d ${current_dir}/local ]; then
		mkdir ${current_dir}/local
	fi
	if [ ! -d ${current_dir}/localSample ]; then
		mkdir ${current_dir}/localSample
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
		updateDirs = "configSample localSample commons jvm servers store"
		for d in $updateDirs; do
			if [ -d ${current_dir}/local/update/o2server/$d ]; then
				cp -Rf ${current_dir}/local/update/o2server/$d  ${current_dir}/
			fi
		done
		updateFiles = "console.jar index.html src.zip"
		for f in $updateFiles; do
			if [ -f ${current_dir}/local/update/o2server/$f ]; then
				cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
			fi
		done
		if [ -f ${current_dir}/local/update/o2server/start_windows.bat ]; then
			updateFiles = "start_windows.bat start_windows_debug.bat stop_windows.bat console_windows.bat service_windows.bat start_windows_java11.bat start_windows_debug_java11.bat stop_windows_java11.bat console_windows.bat service_windows_java11.bat"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		if [ -f ${current_dir}/local/update/o2server/start_linux.sh ]; then
			updateFiles = "start_linux.sh start_linux_debug.sh stop_linux.sh console_linux.sh start_linux_java11.sh start_linux_debug_java11.sh stop_linux_java11.sh console_linux_java11.sh"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		if [ -f ${current_dir}/local/update/o2server/start_macos.sh ]; then
			updateFiles = "start_macos.sh start_macos_debug.sh stop_macos.sh console_macos.sh start_macos_java11.sh start_macos_debug_java11.sh stop_macos_java11.sh console_macos_java11.sh"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		if [ -f ${current_dir}/local/update/o2server/start_aix.sh ]; then
			updateFiles = "start_aix.sh start_aix_debug.sh stop_aix.sh console_aix.sh start_aix_java11.sh start_aix_debug_java11.sh stop_aix_java11.sh console_aix_java11.sh"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		if [ -f ${current_dir}/local/update/o2server/start_raspi.sh ]; then
			updateFiles = "start_raspi.sh start_raspi_debug.sh stop_raspi.sh console_raspi.sh start_raspi_java11.sh start_raspi_debug_java11.sh stop_raspi_java11.sh console_raspi_java11.sh"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		if [ -f ${current_dir}/local/update/o2server/start_mips.sh ]; then
			updateFiles = "start_mips.sh start_mips_debug.sh stop_mips.sh console_mips.sh start_mips_java11.sh start_mips_debug_java11.sh stop_mips_java11.sh console_mips_java11.sh"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		if [ -f ${current_dir}/local/update/o2server/start_arm.sh ]; then
			updateFiles = "start_arm.sh start_mips_arm.sh stop_arm.sh console_arm.sh start_arm_java11.sh start_mips_arm_java11.sh stop_arm_java11.sh console_arm_java11.sh"
			for f in $updateFiles; do
				if [ -f ${current_dir}/local/update/o2server/$f ]; then
					cp -f ${current_dir}/local/update/o2server/$f ${current_dir}/
				fi
			done
		fi
		cp ${current_dir}/local/update/o2server/version.o2 ${current_dir}/
		rm -Rf ${current_dir}/local/update
	fi
fi
setsid ${current_dir}/jvm/linux_java11/bin/java -Dnashorn.args=--no-deprecation-warning --add-exports jdk.scripting.nashorn/jdk.nashorn.internal.runtime=ALL-UNNAMED --add-exports jdk.scripting.nashorn/jdk.nashorn.internal.runtime.arrays=ALL-UNNAMED -javaagent:${current_dir}/console.jar=java11 -server -Djava.awt.headless=true -Xms2g -Duser.timezone=GMT+08 -XX:+HeapDumpOnOutOfMemoryError -jar ${current_dir}/console.jar