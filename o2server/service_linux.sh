current_dir="$(
    cd "$(dirname "$0")"
    pwd
)"
if [ -z "$1" ]; then
    echo "usage: ./service_linux_install.sh name [start.sh default is start_linux.sh]"
    exit
fi
name="$1"
scriptFile="start_linux.sh"
if [ -z "$2" ]; then
    echo "use ${current_dir}/${scriptFile} as start script."
else
    scriptFile="$2"
fi
if [ ! -f ${current_dir}/${scriptFile} ]; then
    echo "start script ${current_dir}/${scriptFile} not exist."
    exit
fi
servicefile="/etc/systemd/system/${name}.service"
echo "[Unit]" >${servicefile}
echo "Description=o2server name:${name}" >>${servicefile}
echo "Wants=network-online.target" >>${servicefile}
echo "After=network.target" >>${servicefile}
echo "[Service]" >>${servicefile}
echo "Type=simple" >>${servicefile}
echo "ExecStart=${current_dir}/start_linux.sh" >>${servicefile}
echo "ExecReload=${current_dir}/restart_linux.sh" >>${servicefile}
echo "ExecStop=${current_dir}/stop_linux.sh" >>${servicefile}
echo "[Install]" >>${servicefile}
echo "WantedBy=multi-user.target" >>${servicefile}
