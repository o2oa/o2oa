#!/bin/bash
# Copyright (c) http://www.o2oa.net/
current_dir="$(cd "$(dirname "$0")" && pwd)"

# 只排除非 ASCII（中文/表情等），- _ 不会受影响
if printf '%s' "$current_dir" | LC_ALL=C grep -q '[^ -~]'; then
  echo "❌ 检测到当前目录包含非 ASCII 字符(如中文/表情)"
  echo "   请将程序放到纯英文路径下运行"
  echo "   当前目录：$current_dir"
  exit 1
fi

cd "${current_dir}" || exit 1t 1

bash "${current_dir}/servers/aiagent/stop-linux-x64.sh"
bash "${current_dir}/stop_linux.sh"
