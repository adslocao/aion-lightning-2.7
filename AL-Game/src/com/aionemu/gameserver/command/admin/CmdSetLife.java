package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdSetLife extends BaseCommand {

	@Override
	public void execute(Player admin, String... params) {
		int percent = 100;
		
		if (params.length == 1) {
			percent = ParseInteger(params[0]);
		}
		
		if(percent == 0){
			percent = 100;
		}
			
		VisibleObject target = admin.getTarget();
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "No target selected");
			return;
		}

		if (!(target instanceof Npc)) {
			PacketSendUtility.sendMessage(admin, "Wrong target");
			return;
		}
		
		((Npc) target).getLifeStats().setCurrentHpPercent(percent);
	}

}
