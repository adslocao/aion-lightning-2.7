package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

public class CmdStigma extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		if (params.length != 2) {
			showHelp(admin);
			return;
		}
		
		Player receiver = null;
		receiver = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (receiver == null) {
			PacketSendUtility.sendMessage(admin, "No player found with name " + params[0]);
			return;
		}
		
		if (params[1].equalsIgnoreCase("show")) {
			int stigmaSlot = receiver.getCommonData().getAdvencedStigmaSlotSize();
			PacketSendUtility.sendMessage(admin, params[0] + "'s size of Advanced Stigma Slot is : " + stigmaSlot);
		}
		else if (params[1].equalsIgnoreCase("add")) {
			if (StigmaService.extendAdvancedStigmaSlots(receiver, 1)) {
				PacketSendUtility.sendMessage(admin, "An Advanced Stigma slot has been added to : " + params[0]);
				PacketSendUtility.sendMessage(receiver, admin.getName() + " added an Advanced Stigma slot to you.");
				return;
			}
			else {
				PacketSendUtility.sendMessage(admin, params[0] + " has already max of Advanced Stigma slot");
				return;
			}
		}
		else {
			showHelp(admin);
		}
	}
}