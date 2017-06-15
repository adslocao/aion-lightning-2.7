package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;


public class CmdCooldown extends BaseCommand {
	
		public void execute(Player player, String... params) {
		if (player.isCoolDownZero()) {
			PacketSendUtility.sendMessage(player, "Cooldown time of all skills has been recovered.");
			player.setCoolDownZero(false);
		}
		else {
			PacketSendUtility.sendMessage(player, "Cooldown time of all skills is set to 0.");
			player.setCoolDownZero(true);
		}
	}

}
