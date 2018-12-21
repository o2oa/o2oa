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

@rem set DERBY_INSTALL=

@if "%DERBY_HOME%"=="" set DERBY_HOME=%DERBY_INSTALL%
@if "%DERBY_HOME%"=="" goto noderbyhome

@FOR %%X in ("%DERBY_HOME%") DO SET DERBY_HOME=%%~sX

set CLASSPATH=%DERBY_HOME%\lib\derby.jar;%DERBY_HOME%\lib\derbytools.jar;%DERBY_HOME%/lib/derbyoptionaltools.jar;%CLASSPATH%
@goto end

:noderbyhome
@echo DERBY_HOME or DERBY_INSTALL not set. Set one of these variables
@echo to the location of your Derby installation.
@goto end

:end
