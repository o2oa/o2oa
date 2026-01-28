@echo off
chcp 65001 >nul
rem Copyright (c) http://www.o2oa.net/
setlocal EnableExtensions EnableDelayedExpansion

set "current_dir=%~dp0"
cd /d "%current_dir%"

rem ---- 校验：目录必须为纯 ASCII（英文/数字/常见符号），否则退出 ----
rem 原理：若路径含中文等非 ASCII，通常其 8.3 短路径会出现 ~1 形式且与原路径不同
rem 兼容点：如果系统禁用了 8.3 短名，%%~sI 可能为空或等于原路径，下面会提示无法校验
set "cur_dir_no_slash=%current_dir:~0,-1%"

for %%I in ("%cur_dir_no_slash%") do (
  set "short_dir=%%~sI"
  set "long_dir=%%~fI"
)

if not defined short_dir (
  echo ❌ 无法获取路径短名(8.3),可能该卷禁用了 8.3 短名生成,无法可靠校验中文路径
  echo    请直接将程序放到纯英文目录下运行
  echo    当前目录：%current_dir%
  exit /b 1
)

rem 如果短路径与长路径不同，通常意味着包含非 ASCII（或包含需要短名表示的字符）
if /I not "!short_dir!"=="!long_dir!" (
  echo ❌ 检测到当前目录可能包含非 ASCII 字符(如中文/表情/特殊符号)
  echo    请将程序放到纯英文路径下运行
  echo    当前目录：%current_dir%
  exit /b 1
)

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
