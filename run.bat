@echo off

cd %~dp0

if not exist .\import md .\import
if not exist .\export md .\export

where java > nul 2>&1
if errorlevel 1 (
    call :die "Could not find java (Java ≧ 1.8)." %errorlevel%
)

setlocal enabledelayedexpansion
if not exist .\target\data-conversion-1.0.0-jar-with-dependencies.jar (
    where mvn > nul 2>&1
    if errorlevel 1 (
        call :die "Could not find mvn (Apache Maven)." !errorlevel!
    )
    call mvn package
    if errorlevel 1 (
        call :die "Failed to make JAR." !errorlevel!
    )
)
endlocal

@echo on
java -jar .\target\data-conversion-1.0.0-jar-with-dependencies.jar sample.db import export result.txt
@echo off

echo.
pause
exit

:die
echo %~1 >&2
echo.
pause
exit %2
