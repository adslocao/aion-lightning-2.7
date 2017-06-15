package com.aionemu.gameserver.command.admin;

import java.util.concurrent.Future;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/**
 * @author Watson
 */
public class CmdGag extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 2) {
			showHelp(admin);
			return;
		}

		String name = Util.convertName(params[1]);
		final Player player = World.getInstance().findPlayer(name);
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "Player " + name + " was not found!");
			return;
		}

		int time = 0;
		if (params.length > 2)
			time = ParseInteger(params[2]);

		player.setGagged(true);
		if (time != 0) {
			Future<?> task = player.getController().getTask(TaskId.GAG);
			if (task != null)
				player.getController().cancelTask(TaskId.GAG);
			player.getController().addTask(TaskId.GAG, ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					player.setGagged(false);
					PacketSendUtility.sendMessage(player, "You have been ungagged");
				}
			}, time * 60000L));
		}
		
		if (GSConfig.ENABLE_CHAT_SERVER) {
			long chatserverGagTime = System.currentTimeMillis() + time * 60 * 1000;
			ChatServer.getInstance().sendPlayerGagPacket(player.getObjectId(), chatserverGagTime);
		}

		PacketSendUtility.sendMessage(player, "You have been gagged" + (time != 0 ? " for " + time + " minutes" : ""));

		PacketSendUtility.sendMessage(admin, "Player " + name + " gagged" + (time != 0 ? " for " + time + " minutes" : ""));
	}
}
