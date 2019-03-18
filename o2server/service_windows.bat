@echo off
if not "%1" == "install" ( if not "%1" == "uninstall"  ( if not "%1" == "start" ( if not "%1" == "stop" (
echo     使用: service_windows.bat install ^| uninstall ^| start ^| stop
echo     依赖于 Microsoft.NET Framework 4
goto out
))))
echo ^<configuration^> > %~dp0local\service.xml
echo ^<id^>O2SERVER^<^/id^> >> %~dp0local\service.xml
echo ^<name^>O2SERVER SERVICE^<^/name^> >> %~dp0local\service.xml
echo ^<description^>O2OA平台服务.^<^/description^> >> %~dp0local\service.xml
echo ^<executable^>%~dp0start_windows.bat^</executable^> >> %~dp0local\service.xml
echo ^<log mode="none"^/^> >> %~dp0local\service.xml
echo ^</configuration^> >> %~dp0local\service.xml
copy %~dp0commons\winsw.exe %~dp0local\service.exe
%~dp0local\service.exe %1
:out