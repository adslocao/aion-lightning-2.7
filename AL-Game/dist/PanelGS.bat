@ECHO off
TITLE Aion Ascension - Game Server Console
:MENU
color F0
CLS
ECHO.
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                      Ascension Aion Control Panel                        ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO   ^|                                                                          ^|
ECHO   ^|    1 - Development                                       4 - Quit        ^|
ECHO   ^|    2 - Production                                                        ^|
ECHO   ^|    3 - Production X2                                                     ^|
ECHO   ^|                                                                          ^|
ECHO   ^*--------------------------------------------------------------------------^*
ECHO.
SET /P OPTION=Type your option and press ENTER: 
IF %OPTION% == 1 (
SET MODE=DEVELOPMENT
SET JAVA_OPTS=-Xms700m -Xmx1000m -Xdebug -Xrunjdwp:transport=dt_socket,address=8998,server=y,suspend=n -ea
CALL StartGS.bat
)
IF %OPTION% == 2 (
SET MODE=PRODUCTION
color F0
SET JAVA_OPTS=-Xms1536m -Xmx1536m -server
CALL StartGS.bat
)
IF %OPTION% == 3 (
SET MODE=PRODUCTION X2
SET JAVA_OPTS=-Xms3072m -Xmx3072m -server
CALL StartGS.bat
)
IF %OPTION% == 4 (
EXIT
)
IF %OPTION% GEQ 5 (
GOTO :MENU
)