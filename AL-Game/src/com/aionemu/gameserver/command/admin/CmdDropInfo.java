package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;



public class CmdDropInfo extends BaseCommand {

	

	
	public void execute(Player player, String... params) {
		NpcDrop npcDrop = null;
		NpcTemplate npcTemplate = null;
		int npcId = 0;
		if (params.length > 0) {
			npcId = Integer.parseInt(params[0]);
			npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
			if (npcTemplate == null){
				PacketSendUtility.sendMessage(player, "Incorrect npcId: "+ npcId);
				return;
			}
			npcDrop = npcTemplate.getNpcDrop();
		}
		else {
			VisibleObject visibleObject = player.getTarget();

			if (visibleObject == null) {
				PacketSendUtility.sendMessage(player, "You should target some NPC first !");
				return;
			}

			if (visibleObject instanceof Npc) {
				npcDrop = ((Npc)visibleObject).getNpcDrop();
				npcTemplate = ((Npc) visibleObject).getObjectTemplate();
				npcId = ((Npc)visibleObject).getNpcId();
			}
		}
		if (npcDrop == null){
			PacketSendUtility.sendMessage(player, "No drops for the selected NPC");
			return;
		}
		
		int count = 0;
		PacketSendUtility.sendMessage(player, "[Drop Info for the specified NPC]\n");
		for (DropGroup dropGroup: npcDrop.getDropGroup()){
			PacketSendUtility.sendMessage(player, "DropGroup: "+ dropGroup.getGroupName());
			for (Drop drop : dropGroup.getDrop()){
				float chance = drop.modifRatio(drop.getItemId(), npcId) * dropGroup.getDropModifier() * player.getRates().getDropRate();
				chance = Math.min(100, chance);
				
				if(chance > 0){
					PacketSendUtility.sendMessage(player, "[item:" + drop.getItemId() + "]" + "	Rate: " + chance);
					count ++;
				}else{
					PacketSendUtility.sendMessage(player, "ERROR item:" + drop.getItemId() + " REPORT TO GM PLZ");
				}
			}
		}
		PacketSendUtility.sendMessage(player, count + " drops available for the selected NPC");
	}

	
}
