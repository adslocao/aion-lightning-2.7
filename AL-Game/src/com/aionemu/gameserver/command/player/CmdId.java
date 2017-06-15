package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ferosia
 */

public class CmdId extends BaseCommand {
	
	public void execute(final Player player, String... params) {
		
		if (params.length > 1) {
			showHelp(player);
			return;
		}
		
		if(params.length == 0) {
			VisibleObject visibleObject = player.getTarget();
			if (visibleObject == null) {
				PacketSendUtility.sendMessage(player, "You should target some NPC first !");
				return;
			}

			if (visibleObject instanceof Npc) {
				Npc npc = (Npc) player.getTarget();
				PacketSendUtility.sendMessage(player, "ID of selected NPC : " + npc.getNpcId());
				return;
			}
			else {
				PacketSendUtility.sendMessage(player, "You should target some NPC first !");
				return;
			}
		}
		int itemId = 0;
		itemId = GetItemIDFromLinkOrID(params[0]);
		if (itemId == 0) {
			PacketSendUtility.sendMessage(player, "Incorrect linked item");
			return;
		}
		PacketSendUtility.sendMessage(player, "ID of [item:" + itemId + "] : " + itemId);
	}
}