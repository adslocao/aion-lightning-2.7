package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.utils.PacketSendUtility;


public class CmdMorph extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params == null || params.length != 1) {
			showHelp(admin);
			return;
		}
		
		 Player target = admin;
			int param = 0;

			if (admin.getTarget() instanceof Player)
				target = (Player) admin.getTarget();

			if (!("cancel").startsWith(params[0].toLowerCase())) {
				try {
					param = Integer.parseInt(params[0]);

				}
				catch (NumberFormatException e) {
					PacketSendUtility.sendMessage(admin, "Parameter must be an integer, or cancel.");
					return;
				}
			}

			if ((param != 0 && param < 200000) || param > 298021) {
				PacketSendUtility.sendMessage(admin, "Something wrong with the NPC Id!");
				return;
			}

			target.setTransformedModelId(param);
			PacketSendUtility.broadcastPacketAndReceive(target, new SM_TRANSFORM(target, true));

			if (param == 0) {
				if (target.equals(admin)) {
					PacketSendUtility.sendMessage(target, "Morph successfully cancelled.");
				}
				else {
					PacketSendUtility.sendMessage(target, "Your morph has been cancelled by " + admin.getName() + ".");
					PacketSendUtility.sendMessage(admin, "You have cancelled " + target.getName() + "'s morph.");
				}
			}
			else {
				if (target.equals(admin)) {
					PacketSendUtility.sendMessage(target, "Successfully morphed to npcId " + param + ".");
				}
				else {
					PacketSendUtility.sendMessage(target, admin.getName() + " morphs you into an NPC form.");
					PacketSendUtility.sendMessage(admin, "You morph " + target.getName() + " to npcId " + param + ".");
				}
			}
		}

}