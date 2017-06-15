@ECHO off
TITLE Aion Lightning - Game Server Console
:START
CLS
IF "%MODE%" == "" (
CALL PanelGS.bat
)
ECHO Starting Aion Lightning Game Server in %MODE% mode.
JAVA %JAVA_OPTS% -ea -javaagent:./libs/{javaagentlib} -cp ./libs/*;AL-Game.jar com.aionemu.gameserver.GameServer
SET CLASSPATH=%OLDCLASSPATH%
IF ERRORLEVEL 2 GOTO START
IF ERRORLEVEL 1 GOTO ERROR
IF ERRORLEVEL 0 GOTO END
:ERROR
ECHO.
ECHO Game Server has terminated abnormaly!
ECHO.
PAUSE
EXIT
:END
ECHO.
ECHO Game Server is terminated!
ECHO.
PAUSE
EXIT