#!/bin/bash
# Copyright (c) http://www.o2oa.net/

current_dir="$(cd "$(dirname "$0")" && pwd)"
cd "${current_dir}" || exit 1

# aiServer：不需要日志 -> 丢弃输出，后台启动
bash "${current_dir}/servers/aiServer/start-linux-x64.sh" </dev/null >/dev/null 2>&1 &

# 主服务：前台运行（你还能在控制台看到它的输出）
bash "${current_dir}/start_linux.sh"