package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public class CmdUnBanMac extends BaseCommand {
	
	

	public void execute(Player player, String... params) {
		if (params == null || params.length < 2) {
			showHelp(player);
			return;
		}

		String address = params[1];
		boolean result = BannedMacManager.getInstance().unbanAddress(address,
			"uban;mac=" + address + ", " + player.getObjectId() + "; admin=" + player.getName());
		if (result)
			PacketSendUtility.sendMessage(player, "mac " + address + " has unbanned");
		else
			PacketSendUtility.sendMessage(player, "mac " + address + " is not banned");
	}
}

