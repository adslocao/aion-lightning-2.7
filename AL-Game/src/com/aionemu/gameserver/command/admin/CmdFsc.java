package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Luno
 */
public class CmdFsc extends BaseCommand {
	

	public void execute(Player admin, String... params) {
		if (params.length < 4) {
			showHelp(admin);
			return;
		}

		int id = Integer.decode(params[1]);
		String format = "";

		if (params.length > 2)
			format = params[2];

		SM_CUSTOM_PACKET packet = new SM_CUSTOM_PACKET(id);

		int i = 0;
		for (char c : format.toCharArray()) {
			packet.addElement(PacketElementType.getByCode(c), params[i + 2]);
			i++;
		}
		PacketSendUtility.sendPacket(admin, packet);
	}
}
