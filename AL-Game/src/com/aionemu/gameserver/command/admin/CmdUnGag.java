package com.aionemu.gameserver.command.admin;

import java.util.concurrent.Future;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * @author Watson
 */
public class CmdUnGag extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if (params == null || params.length < 2) {
			showHelp(admin);
			return;
		}

		String name = Util.convertName(params[1]);
		Player player = World.getInstance().findPlayer(name);
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			showHelp(admin);
			return;
		}

		player.setGagged(false);
		Future<?> task = player.getController().getTask(TaskId.GAG);
		if (task != null)
			player.getController().cancelTask(TaskId.GAG);
		PacketSendUtility.sendMessage(player, "You have been ungagged");
		PacketSendUtility.sendMessage(admin, "Player " + name + " ungagged");
	}
}

