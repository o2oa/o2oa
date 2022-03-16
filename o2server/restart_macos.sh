current_dir="$(cd "$(dirname "$0")"; pwd)"
cd ${current_dir}
${current_dir}/stop_macos.sh
${current_dir}/start_macos.sh