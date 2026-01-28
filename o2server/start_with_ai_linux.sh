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

cd "${current_dir}" || exit 1

# aiServer：不需要日志 -> 丢弃输出，后台启动
bash "${current_dir}/servers/aiagent/start-linux-x64.sh" </dev/null >/dev/null 2>&1 &

# 主服务：前台运行（你还能在控制台看到它的输出）
bash "${current_dir}/start_linux.sh"
