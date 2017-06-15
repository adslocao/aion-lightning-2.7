@ECHO off
TITLE Aion-Lightning - Urtem Login Server Console

:START
CLS

C:\java645\bin\java.exe  -Xms128m -Xmx128m -server -cp ./libs/*;AL-Login.jar com.aionlightning.loginserver.LoginServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Aion-Lightning - Login Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Aion-Lightning - Login Server is terminated!
ECHO.
PAUSE
EXIT