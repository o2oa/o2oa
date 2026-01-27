@echo off
chcp 65001 >nul
setlocal EnableExtensions

rem -----------------------------
rem 切到脚本所在目录（等价于 bash 的 current_dir + cd）
rem -----------------------------
set "current_dir=%~dp0"
pushd "%current_dir%" || exit /b 1

rem -----------------------------
rem 1) 停止 aiagent
rem -----------------------------
set "AIAGENT_STOP=%current_dir%servers\aiagent\stop-windows-x64.bat"
if exist "%AIAGENT_STOP%" (
  call "%AIAGENT_STOP%"
) else (
  echo [WARN] aiagent stop script not found: "%AIAGENT_STOP%"
)

rem -----------------------------
rem 2) 停止主服务
rem -----------------------------
set "MAIN_STOP=%current_dir%stop_windows.bat"

if exist "%MAIN_STOP%" (
  call "%MAIN_STOP%"
) else (
  echo [ERROR] main stop script not found: "%MAIN_STOP%"
  popd
  exit /b 1
)

popd
endlocal