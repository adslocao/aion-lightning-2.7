@ECHO off
TITLE Aion-Lightning - Urtem Game Server Console
@ECHO off
TITLE Aion Lightning - Siel GameServer
C:\Program Files\Java\jre6\bin\java.exe %JAVA_OPTS% -ea -javaagent:./libs/al-commons-1.3.jar -cp ./libs/*;AL-Game.jar com.aionemu.gameserver.GameServer
SET MODE=PRODUCTION
SET CLASSPATH=%OLDCLASSPATH%
CALL StartGS.bat