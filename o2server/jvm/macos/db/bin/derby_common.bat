@echo off

@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at

@REM   http://www.apache.org/licenses/LICENSE-2.0

@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.

rem %~dp0 is expanded pathname of the current script under NT
set DEFAULT_DERBY_HOME=%~dp0..

if "%DERBY_HOME%"=="" set DERBY_HOME=%DEFAULT_DERBY_HOME%
set DEFAULT_DERBY_HOME=

set _USE_CLASSPATH=yes

rem Slurp the command line arguments. This loop allows for an unlimited number
rem of arguments (up to the command line limit, anyway).
set DERBY_CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
if ""%1""==""-noclasspath"" goto clearclasspath
set DERBY_CMD_LINE_ARGS=%DERBY_CMD_LINE_ARGS% %1
shift
goto setupArgs

rem here is there is a -noclasspath in the options
:clearclasspath
set _USE_CLASSPATH=no
shift
goto setupArgs

rem This label provides a place for the argument list loop to break out
rem and for NT handling to skip to.

:doneStart
rem check the value of DERBY_HOME
if exist "%DERBY_HOME%\lib\derby.jar" goto setLocalClassPath

:noDerbyHome
echo DERBY_HOME is set incorrectly or derby.jar could not be located. 
echo Please set the DERBY_HOME environment variable to the path where you installed Derby.
goto endcommon

:setLocalClassPath
set LOCALCLASSPATH=%DERBY_HOME%/lib/derby.jar;%DERBY_HOME%/lib/derbynet.jar;%DERBY_HOME%/lib/derbyclient.jar;%DERBY_HOME%/lib/derbytools.jar;%DERBY_HOME%/lib/derbyoptionaltools.jar

:checkJava
set _JAVACMD=%JAVACMD%

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe
goto endcommon

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=java.exe

:endcommon

