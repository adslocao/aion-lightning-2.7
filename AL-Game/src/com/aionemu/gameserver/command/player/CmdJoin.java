package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.cqfd.events.CQFDEventStat;
import com.aionemu.gameserver.cqfd.events.impl.CQFDEventPvPAera;
import com.aionemu.gameserver.cqfd.events.impl.CQFDEventTvT;
import com.aionemu.gameserver.cqfd.events.task.impl.CQFDEventTaskRegister;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdJoin extends BaseCommand {
	
	public void execute(Player player, String... params) {
		if(params.length != 1){
			PacketSendUtility.sendMessage(player, "syntaxe : .join <tvt|pvp>");
			return;
		} else if(params[0].equalsIgnoreCase("tvt")){
			if(CQFDEventTvT.instance._eventStat == CQFDEventStat.REGISTRATION)
				CQFDEventTaskRegister.addPlayer(player);
			else
				PacketSendUtility.sendMessage(player, "Tvt registration are currently disabled");
		}else if(params[0].equalsIgnoreCase("pvp")){
			if(CQFDEventPvPAera.instance._eventStat == CQFDEventStat.REGISTRATION)
				CQFDEventPvPAera.instance.addPlayer(player, null);
			else
				PacketSendUtility.sendMessage(player, "PvPaera are currently disabled");
			
		}
	}
}
