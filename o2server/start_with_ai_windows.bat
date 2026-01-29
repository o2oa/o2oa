@echo off
chcp 65001 >nul
setlocal EnableExtensions EnableDelayedExpansion

set "current_dir=%~dp0"

set "cur_dir_no_slash=%current_dir:~0,-1%"

for %%I in ("%cur_dir_no_slash%") do (
  set "short_dir=%%~sI"
  set "long_dir=%%~fI"
)

if not defined short_dir (
  echo    "Unable to get the short path (8.3). The volume may have 8.3 short-name generation disabled, so we cannot reliably validate paths containing Chinese characters."
  echo    "Please run the program from an ASCII-only directory."
  echo    "Current directory: %current_dir%"
  exit /b 1
)

if /I not "!short_dir!"=="!long_dir!" (
  echo    "The current directory may contain non-ASCII characters (e.g., Chinese/emoji/special symbols)."
  echo    "Please move the program to an ASCII-only directory and run it again."
  echo    "Current directory: %current_dir%"
  exit /b 1
)

cd /d "%current_dir%"

start "" "%current_dir%servers\aiagent\start-windows-x64.bat"

call "%current_dir%start_windows.bat"

endlocal
