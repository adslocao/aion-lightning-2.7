package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Source
 */
public class CmdEnergyBuff extends BaseCommand {
	
	
	public void execute(Player player, String... params) {
		VisibleObject target = player.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(player, "No target selected");
			return;
		}

		Creature creature = (Creature) target;
		if (params == null || params.length < 1) {
			showHelp(player);
			return;
		}
		else if (target instanceof Player) {
			if (params[1].equals("repose")) {
				Player targetPlayer = (Player) creature;
				if (params[2].equals("info"))
					PacketSendUtility.sendMessage(player, "Current EoR: " + targetPlayer.getCommonData().getCurrentReposteEnergy() + "\n Max EoR: " + targetPlayer.getCommonData().getMaxReposteEnergy());
				else if (params[2].equals("add"))
					targetPlayer.getCommonData().addReposteEnergy(Long.parseLong(params[3]));
				else if (params[2].equals("reset"))
					targetPlayer.getCommonData().setCurrentReposteEnergy(0);
			}
			else if (params[1].equals("salvation")) {
				Player targetPlayer = (Player) creature;
				if (params[2].equals("info"))
					PacketSendUtility.sendMessage(player, "Current EoS: " + targetPlayer.getCommonData().getCurrentSalvationPercent());
				else if (params[2].equals("add"))
					targetPlayer.getCommonData().addSalvationPoints(Long.parseLong(params[3]));
				else if (params[2].equals("reset"))
					targetPlayer.getCommonData().resetSalvationPoints();
			}
			else if (params[1].equals("refresh")) {
				Player targetPlayer = (Player) creature;
				PacketSendUtility.sendPacket(targetPlayer, new SM_STATS_INFO(targetPlayer));
			}
		}
		else
			PacketSendUtility.sendMessage(player, "This is not player");
	}
}