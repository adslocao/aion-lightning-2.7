package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdDispel extends BaseCommand {
	

	public void execute(Player admin, String... params) {
		
		VisibleObject obj = admin.getTarget();
		if(obj == null){
			PacketSendUtility.sendMessage(admin, "Invalid target");
		}
		if(obj instanceof Player){
			Player target = (Player) obj;
			target.getEffectController().removeAllEffects();
			PacketSendUtility.sendMessage(admin, target.getName() + " had all buff effects dispelled !");
		}
		else if(obj instanceof Creature){
			Creature target = (Creature) obj;
			target.getEffectController().removeAllEffects();
			PacketSendUtility.sendMessage(admin, target.getName() + " had all buff effects dispelled !");	
		}
		/*
		Player target = AutoTarget(admin, false);
		target.getEffectController().removeAllEffects();
		PacketSendUtility.sendMessage(admin, target.getName() + " had all buff effects dispelled !");
		*/
	}
}

