echo ready to stop server $(cd "$(dirname "$0")"; pwd)
$(cd "$(dirname "$0")"; pwd)/jvm/linux/bin/java -javaagent:$(cd "$(dirname "$0")"; pwd)/console.jar -cp $(cd "$(dirname "$0")"; pwd)/console.jar com.x.server.console.swapcommand.Exit
echo 'check server stoped wait 2s-10s'
sleep 2
PID=`ps -ef | grep "$(cd "$(dirname "$0")"; pwd)"  | grep -v grep | awk '{print $2}'`
if [ "X$PID" != "X" ]
then
        sleep 8
        if [ "X$PID" != "X" ]
        then
                echo ready to kill server $PID
                kill -9 $PID
        fi
fi
echo 'server stoped ready to start'
$(cd "$(dirname "$0")"; pwd)/start_linux.sh
