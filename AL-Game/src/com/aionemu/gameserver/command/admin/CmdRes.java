package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RESURRECT;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdRes extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		Player player = AutoTarget(admin);

		if (!player.getLifeStats().isAlreadyDead()) {
			PacketSendUtility.sendMessage(admin, "Ce joueur et deja en vie.");
			return;
		}
		
		//PlayerReviveService.skillRevive(player);
		PacketSendUtility.sendMessage(admin, "Le joueur peut se rez.");
		player.setPlayerResActivate(true);
		PacketSendUtility.sendPacket(player, new SM_RESURRECT(admin, 9912));
	}
}
