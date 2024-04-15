@echo off

where java > nul 2>&1

if errorlevel 1 (
    echo Could not find java.
    pause
    exit %errorlevel%
)

if not exist .\target\data-conversion-1.0.0-jar-with-dependencies.jar (
    where mvn > nul 2>&1
    if errorlevel 1 (
        echo Could not find mvn.
        pause
        exit %errorlevel%
    )
    call mvn package
    if errorlevel 1 (
        echo Failed to make JAR.
        pause
        exit %errorlevel%
    )
)

@echo on
java -jar .\target\data-conversion-1.0.0-jar-with-dependencies.jar sample.db import export result.txt
@echo off

pause
exit %errorlevel%
