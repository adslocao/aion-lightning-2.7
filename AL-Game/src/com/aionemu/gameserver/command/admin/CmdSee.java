package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.utils.PacketSendUtility;

/*Syntax: //see */
public class CmdSee extends BaseCommand {
	
	

	public void execute(Player admin, String... params) {
		if (admin.getSeeState() < 2) {
			admin.setSeeState(CreatureSeeState.SEARCH2);
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
			PacketSendUtility.sendMessage(admin, "You got vision.");
		}
		else {
			admin.setSeeState(CreatureSeeState.NORMAL);
			PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
			PacketSendUtility.sendMessage(admin, "You lost vision.");
		}
	}

}