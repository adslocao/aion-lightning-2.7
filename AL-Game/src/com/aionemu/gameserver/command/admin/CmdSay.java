package com.aionemu.gameserver.command.admin;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdSay extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			showHelp(admin);
			return;
		}

		VisibleObject target = admin.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "You must select a target !");
			return;
		}
		
		String sMessage = StringUtils.join(params, " ", 0, params.length);

		if (target instanceof Player) {
			PacketSendUtility.broadcastPacket(((Player) target),
				new SM_MESSAGE(((Player) target), sMessage, ChatType.NORMAL), true);
		}
		else if (target instanceof Npc) {
			// admin is not right, but works
			PacketSendUtility.broadcastPacket(admin, new SM_MESSAGE(((Npc) target).getObjectId(), ((Npc) target).getName(),
				sMessage, ChatType.NORMAL), true);
		}
	}
}