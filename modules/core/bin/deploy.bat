@echo off
echo [INFO] deploy jar to center repository.

cd %~dp0
cd ..
call mvn clean deploy -Dmaven.test.skip=true
cd bin
pause