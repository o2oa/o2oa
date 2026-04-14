@echo off
chcp 65001 >nul
setlocal EnableExtensions EnableDelayedExpansion

set "current_dir=%~dp0"

set "cur_dir_no_slash=%current_dir:~0,-1%"

set "P=%cur_dir_no_slash%"
powershell -NoProfile -Command ^
  "if ($env:P -match '[\u4E00-\u9FFF]') { exit 1 } else { exit 0 }"

if errorlevel 1 (
  echo    "The current directory may contain non-ASCII characters (e.g., Chinese/emoji/special symbols)."
  exit /b 1
)

cd /d "%current_dir%"

start "" "%current_dir%servers\aiagent\start-windows-x64.bat"

call "%current_dir%start_windows.bat"

endlocal
