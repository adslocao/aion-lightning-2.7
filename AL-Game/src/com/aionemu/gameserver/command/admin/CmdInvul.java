package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdInvul extends BaseCommand {
	
	
	public void execute(Player player, String... params) {
		if (player.isInvul()) {
			player.setInvul(false);
			PacketSendUtility.sendMessage(player, "God Mod Off.");
		}
		else {
			player.setInvul(true);
			PacketSendUtility.sendMessage(player, "God Mod On.");
		}
	}
}
