package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdInfos extends BaseCommand {

	public void execute(Player player, String... params) {
		NpcDrop npcDrop = null;
		float rate = 0f;
		NpcTemplate npcTemplate = null;

		if (params.length > 0 && ParseFloat(params[0]) != 0f) {
			rate = ParseFloat(params[0]);
		}

		if (params.length > 0 && rate > 100) {
			int npcId = Integer.parseInt(params[0]);
			npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
			if (npcTemplate == null) {
				PacketSendUtility.sendMessage(player, "Incorrect npcId: " + npcId);
				return;
			}
			npcDrop = npcTemplate.getNpcDrop();
			rate = 0f;
		} else {
			VisibleObject visibleObject = player.getTarget();

			if (visibleObject == null) {
				PacketSendUtility.sendMessage(player, "You should target some NPC first !");
				return;
			}

			if (visibleObject instanceof Npc) {
				npcDrop = ((Npc) visibleObject).getNpcDrop();
				npcTemplate = ((Npc) visibleObject).getObjectTemplate();
			}
		}
		if (npcDrop == null) {
			PacketSendUtility.sendMessage(player, "No drops for the selected NPC");
			return;
		}
		displayDrop(player, npcDrop, rate, npcTemplate);

	}

	private void displayDrop(Player player, NpcDrop npcDrop, float rate, NpcTemplate npcTemplate) {
		int count = 0;
		PacketSendUtility.sendMessage(player, "[Drop Info for the specified NPC]\n");
		for (DropGroup dropGroup : npcDrop.getDropGroup()) {
			PacketSendUtility.sendMessage(player, "DropGroup: " + dropGroup.getGroupName());
			for (Drop drop : dropGroup.getDrop()) {

				if (drop.getChance() < rate)
					continue;

				// Add chanology baseDrop * droupGroupRateModifier *
				// playerRate (if not normal mob)
				float chance = drop.getChance() * dropGroup.getDropModifier()
						* (npcTemplate.getRating() == NpcRating.NORMAL ? 1 : player.getRates().getDropRate());
				chance = Math.min(100, chance);
						
				PacketSendUtility.sendMessage(player, "[item:" + drop.getItemId() + "]" + "  Rate: " + chance);
				count++;
			}
		}
		PacketSendUtility.sendMessage(player, count + " drops available for the selected NPC");
	}
}
