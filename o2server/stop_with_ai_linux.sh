#!/bin/bash
# Copyright (c) http://www.o2oa.net/

current_dir="$(cd "$(dirname "$0")" && pwd)"
cd "${current_dir}" || exit 1

bash "${current_dir}/servers/aiagent/stop-linux-x64.sh"
bash "${current_dir}/stop_linux.sh"
