#!/bin/bash
# Copyright (c) http://www.o2oa.net/
current_dir="$(
    cd "$(dirname "$0")"
    pwd
)"
cd ${current_dir}
echo "ready to stop o2server path: ${current_dir}"
${current_dir}/jvm/macos_java11/bin/java -cp ${current_dir}/console.jar com.x.server.console.swapcommand.Exit
sleep 10
PID=$(ps -ef | grep "${current_dir}/jvm/macosarm_java11/bin/java" | grep -v grep | awk '{print $2}')
if [ "X$PID" != "X" ]; then
    sleep 5
    if [ "X$PID" != "X" ]; then
        echo ready to kill server $PID
        kill -9 $PID
    fi
fi
