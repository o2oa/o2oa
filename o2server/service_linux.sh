current_dir="$(
    cd "$(dirname "$0")"
    pwd
)"
if [ -z "$1" ]; then
    echo "usage: ./service_linux.sh name [start.sh default is start_linux.sh]"
    exit
fi
name="$1"
scriptName="start_linux.sh"
if [ -z "$2" ]; then
    echo "use ${current_dir}/${scriptName} as start script."
else
    scriptName="$2"
fi
if [ ! -f ${current_dir}/${scriptName} ]; then
    echo "start script ${current_dir}/${scriptName} not exist."
    exit
fi
servicePath="/etc/systemd/system/${name}.service"
echo "[Unit]" >${servicePath}
echo "Description=o2server name:${name}" >>${servicePath}
echo "Wants=network-online.target" >>${servicePath}
echo "After=network.target" >>${servicePath}
echo "[Service]" >>${servicePath}
echo "Type=simple" >>${servicePath}
echo "ExecStart=${current_dir}/${scriptName}" >>${servicePath}
echo "ExecReload=${current_dir}/restart_linux.sh" >>${servicePath}
echo "ExecStop=${current_dir}/stop_linux.sh" >>${servicePath}
echo "[Install]" >>${servicePath}
echo "WantedBy=multi-user.target" >>${servicePath}
