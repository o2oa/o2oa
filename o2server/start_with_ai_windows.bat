@echo off
chcp 65001 >nul
setlocal EnableExtensions

rem -----------------------------
rem 切到脚本所在目录（等价于 linux 的 current_dir）
rem -----------------------------
set "current_dir=%~dp0"
pushd "%current_dir%" || exit /b 1

rem -----------------------------
rem 1) aiServer: 后台启动 + 丢弃输出
rem -----------------------------
set "AIAGENT_SCRIPT=%current_dir%servers\aiagent\start-windows-x64.bat"
if exist "%AIAGENT_SCRIPT%" (
  rem 用 start + cmd /c 启动一个后台进程，并把 stdin/stdout/stderr 全部丢弃
  start "aiagent" /b cmd /c """"%AIAGENT_SCRIPT%"""" ^<NUL ^>NUL 2^>^&1
  ) else (
  echo [WARN] aiagent start script not found: "%AIAGENT_SCRIPT%"
)

rem -----------------------------
rem 2) 主服务：前台运行（保持当前控制台输出可见）
rem -----------------------------
set "MAIN_SCRIPT=%current_dir%start_windows.bat"

if exist "%MAIN_SCRIPT%" (
  call "%MAIN_SCRIPT%"
  ) else (
  echo [ERROR] main start script not found: "%MAIN_SCRIPT%"
  popd
  exit /b 1
)

popd
endlocal
