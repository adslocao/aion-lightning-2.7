package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author Wakizashi
 */
public class CmdNoexp extends BaseCommand {


	public void execute(Player player, String... params) {
		if (player.getCommonData().getNoExp()) {
			player.getCommonData().setNoExp(false);
			PacketSendUtility.sendMessage(player, "Experience rewards are reactivated !");
		}
		else {
			player.getCommonData().setNoExp(true);
			PacketSendUtility.sendMessage(player, "Experience rewards are desactivated !");
		}
	}
	
}
