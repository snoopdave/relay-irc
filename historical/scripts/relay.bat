@echo off
REM Batch file to run Relay-JFC on Windows
if not "%RELAYHOME%"=="" goto got_relayhome
echo Using RELAYHOME=.. to start Relay-JFC 
set RELAYHOME=..
:got_relayhome

REM save pre-existing classpath
set _CLASSPATH=%CLASSPATH%

set CLASSPATH=%RELAYHOME%\lib\relay-jfc.jar;%RELAYHOME%\lib\chatengine.jar;%RELAYHOME%\lib\jython.jar;%RELAYHOME%\scripts
REM set DEBUGARGS=-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5555
java %DEBUGARGS% -mx32m -classpath %CLASSPATH% org.relayirc.swingui.ChatApp -d %1 %2 %3

REM restore pre-existing classpath
set CLASSPATH=%_CLASSPATH%

