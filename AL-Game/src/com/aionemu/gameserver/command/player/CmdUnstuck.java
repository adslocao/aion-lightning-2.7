package com.aionemu.gameserver.command.player;

import java.util.Date;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

public class CmdUnstuck extends BaseCommand {
	
	

	public void execute(final Player player, String... params) {
		if (player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendMessage(player, "You can't use the unstuck command when you are dead.");
			return;
		}
		if (player.isInPrison()) {
			PacketSendUtility.sendMessage(player, "You can't use the unstuck command when you are in Prison.");
			return;
		}
		
		final long now = (new Date().getTime())/1000;
		
		if (player.getCommonData().getUnStuck() + CustomConfig.UNSTUCK_DELAY > now) {
			long cdTime = player.getCommonData().getUnStuck() - now + CustomConfig.UNSTUCK_DELAY;
			long min = (long)Math.floor(cdTime / 60);
			long sec = cdTime - min * 60;
			if (min > 0)
				PacketSendUtility.sendMessage(player, "You must wait " + min + ":" + sec + " before use this command again.");
			else
				PacketSendUtility.sendMessage(player, "You must wait " + sec + " seconds before use this command again.");
			return;
		}
		
		player.getController().cancelAllTasks();
		player.getMoveController().abortMove();
		
		PacketSendUtility.broadcastPacketAndReceive(player,
				new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), player.getObjectId(), 0, 180000, 0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				player.getCommonData().setUnStuck(now);
				TeleportService.moveToBindLocation(player, true);
			}
		}, 180000));
	}
}

