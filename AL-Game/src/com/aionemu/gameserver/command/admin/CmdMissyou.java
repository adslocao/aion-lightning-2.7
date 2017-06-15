package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdMissyou extends BaseCommand {
	
	
	public void execute(Player player, String... params) {

		Player partner = player.findPartner();
		if (partner == null) {
			PacketSendUtility.sendMessage(player, "Not online.");
			return;
		}
		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}
		if (partner.getWorldId() == 510010000 || partner.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't teleported to " + partner.getName() +", your partner is on prison.");
			return;
		}

		TeleportService.teleportTo(player, partner.getWorldId(), partner.getInstanceId(), partner.getX(), partner.getY(),
			partner.getZ(), partner.getHeading(), 3000, true);
		PacketSendUtility.sendMessage(player, "Teleported to player " + partner.getName() + ".");
	}

}
