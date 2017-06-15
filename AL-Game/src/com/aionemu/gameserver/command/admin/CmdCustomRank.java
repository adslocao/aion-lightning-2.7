package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.custom.CustomPlayerRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdCustomRank extends BaseCommand {

	public void execute(Player player, String... params) {
		if (params.length < 2) {
			PacketSendUtility.sendMessage(player, "Vous etes de rank ["+player.getCustomPlayerRank().getRank()+"] a "+player.getCustomPlayerRank().getPoints()+"/"+CustomPlayerRank.NB_PTS_BY_RANK);
			return;
		}

		if(player.getAccessLevel() > 1){

			Player target = AutoTarget(player, false);
			if(target == null){
				PacketSendUtility.sendMessage(player, "Invalid Target");
				return;
			}
			if(params[0].equalsIgnoreCase("addPts"))
				target.getCustomPlayerRank().addPts(Integer.parseInt(params[1]));
			else if(params[0].equalsIgnoreCase("setPts"))
				target.getCustomPlayerRank().setPts(Integer.parseInt(params[1]));
			else if(params[0].equalsIgnoreCase("setRank"))
				target.getCustomPlayerRank().setRank(Integer.parseInt(params[1]));
			else
				return;
			PacketSendUtility.sendMessage(target, "Votre Rank a ete edite");
		}
	}
	
}
