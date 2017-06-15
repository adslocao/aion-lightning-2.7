package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

public class CmdKinah extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 2) {
			showHelp(admin);
			return;
        }
		
		long kinahCount = 0;
		Player player = null;
		
		if (params.length == 2) {
			player = World.getInstance().findPlayer(Util.convertName(params[0]));
			kinahCount = ParseLong(params[1]);
		}
		else {
			player = AutoTarget(admin, false);
	        kinahCount = ParseLong(params[0]);
		}
		
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "Joueur introuvable");
			return ;
		}
		
		if (kinahCount > 0) {
			player.getInventory().increaseKinah(kinahCount);
			if (admin == player)
				PacketSendUtility.sendMessage(admin, "Nouveau Solde de Kinah : " + admin.getInventory().getKinah());
			else {
				PacketSendUtility.sendMessage(admin, "Nouveau Solde de Kinah : " + player.getInventory().getKinah() + " pour " + player.getName());
				PacketSendUtility.sendMessage(player, "Vous avez " + player.getInventory().getKinah() + " Kinah");
			}
		}
		else if (kinahCount < 0) {
			player.getInventory().decreaseKinah(Math.abs(kinahCount));
			if (admin == player)
				PacketSendUtility.sendMessage(admin, "Nouveau Solde de Kinah : " + admin.getInventory().getKinah());
			else {
				PacketSendUtility.sendMessage(admin, "Nouveau Solde de Kinah : " + player.getInventory().getKinah() + " pour " + player.getName());
				PacketSendUtility.sendMessage(player, "Vous avez " + player.getInventory().getKinah() + " Kinah");
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "Solde de Kinah (" + player.getName() + ") : " + player.getInventory().getKinah());
			return ;
		}
	}
}

