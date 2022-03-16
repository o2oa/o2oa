current_dir="$(cd "$(dirname "$0")"; pwd)"
cd ${current_dir}
${current_dir}/stop_aix.sh
${current_dir}/start_aix.sh