package com.aionemu.gameserver.command.admin;

import java.lang.reflect.Field;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author SheppeR
 */
public class CmdChannel extends BaseCommand {

	
	public void execute(Player player, String... params) {
		Class<?> classToMofify = CustomConfig.class;
		Field someField;
		try {
			someField = classToMofify.getDeclaredField("FACTION_CMD_CHANNEL");
			if (params[1].equalsIgnoreCase("on") && !CustomConfig.FACTION_CMD_CHANNEL) {
				someField.set(null, Boolean.valueOf(true));
				PacketSendUtility.sendMessage(player, "The command .faction is ON.");
			}
			else if (params[1].equalsIgnoreCase("off") && CustomConfig.FACTION_CMD_CHANNEL) {
				someField.set(null, Boolean.valueOf(false));
				PacketSendUtility.sendMessage(player, "The command .faction is OFF.");
			}
			else
				showHelp(player);
		}
		catch (Exception e) {
			showHelp(player);
			return;
		}
	}
}
