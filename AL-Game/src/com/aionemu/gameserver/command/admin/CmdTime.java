package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import com.aionemu.gameserver.spawnengine.DayTimeSpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
/*Syntax: //time < dawn | day | dusk | night | desired hour (number)*/

/*A day have only 24 hours!\n" + "Min value : 0 - Max value : 23 */

public class CmdTime extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if (params.length < 2) {
			showHelp(admin);
			return;
		}
	
			// Getting current hour and minutes
			int time = GameTimeManager.getGameTime().getHour();
			int min = GameTimeManager.getGameTime().getMinute();
			int hour;

			// If the given param is one of these four, get the correct hour...
			if (params[0].equals("night")) {
				hour = 22;
			}
			else if (params[0].equals("dusk")) {
				hour = 18;
			}
			else if (params[0].equals("day")) {
				hour = 9;
			}
			else if (params[0].equals("dawn")) {
				hour = 4;
			}
			else {
				// If not, check if the param is a number (hour)...
				try {
					hour = Integer.parseInt(params[0]);
				}
				catch (NumberFormatException e) {
					showHelp(admin);
					return;
				}

				// A day have only 24 hours!
				if (hour < 0 || hour > 23) {
					showHelp(admin);
					return;
				}
			}

			// Calculating new time in minutes...
			time = hour - time;
			time = GameTimeManager.getGameTime().getTime() + (60 * time) - min;

			// Reloading the time, restarting the clock...
			GameTimeManager.reloadTime(time);

			// Checking the new daytime
			GameTimeManager.getGameTime().calculateDayTime();

			World.getInstance().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_GAME_TIME());
				}
			});
			DayTimeSpawnEngine.spawnAll();

			PacketSendUtility.sendMessage(admin, "You changed the time to " + params[0].toString() + ".");
		}
}
