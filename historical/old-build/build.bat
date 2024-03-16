@echo off
REM Build Relay-JFC client and Relay-IRC engine
if not "%RELAYSRC%"=="" goto got_relaysrc
set RELAYSRC=.
:got_relaysrc
java -classpath "%CLASSPATH%;%RELAYSRC%\lib\jaxp-1.1.jar;%RELAYSRC%\lib\crimson-1.1.jar;%RELAYSRC%\lib\ant-1.3.jar" -Drelay.root=%RELAYSRC% org.apache.tools.ant.Main %VERBOSE% %1 %2 %3 

