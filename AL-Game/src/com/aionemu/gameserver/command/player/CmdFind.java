package com.aionemu.gameserver.command.player;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdFind extends BaseCommand {

	public void execute(Player player, String... params) {
		if (params.length < 1 || params.length > 3) {
			PacketSendUtility.sendMessage(player, "Syntax : .find <objectID> <rateMin> <rateMax>");
			return;
		}

		List<String> legendaryMobs = new ArrayList<String>();
		List<String> heroMobs = new ArrayList<String>();
		List<String> eliteMobs = new ArrayList<String>();
		List<String> commonMobs = new ArrayList<String>();

		float rateMin = 0;
		float rateMax = 100;
		final int itemId = Integer.parseInt(params[0]);
		if (params.length >= 2) {
			rateMin = Float.parseFloat(params[1]);
		}
		if (params.length >= 3) {
			rateMax = Float.parseFloat(params[2]);
		}
		PacketSendUtility.sendMessage(player, "=============================\n" + "Searching [item: " + itemId + "] \n"
				+ "=============================\n");

		for (NpcDrop npcDrop : DataManager.NPC_DROP_DATA.getNpcDrop()) {
			for (DropGroup npcDropGroup : npcDrop.getDropGroup()) {
				for (Drop drop : npcDropGroup.getDrop()) {
					if (drop.getItemId() != itemId) {
						continue;
					}
					
					float chance = drop.modifRatio(drop.getItemId(), npcDrop.getNpcId()) * npcDropGroup.getDropModifier() * player.getRates().getDropRate();
					chance = Math.min(100, chance);
					
					if (chance < rateMin || chance > rateMax) {
						continue;
					}

					NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcDrop.getNpcId());
					String s = " npcId : " + npcDrop.getNpcId() + " | name : " + npcTemplate.getName() + " | chance : "
							+ chance;

					if (npcTemplate.getRating() == NpcRating.LEGENDARY) {
						legendaryMobs.add(s);
					}
					if (npcTemplate.getRating() == NpcRating.HERO) {
						heroMobs.add(s);
					}
					if (npcTemplate.getRating() == NpcRating.ELITE) {
						eliteMobs.add(s);
					}
					if (npcTemplate.getRating() == NpcRating.NORMAL) {
						commonMobs.add(s);
					}

				}
			}
		}

		printArray(player, legendaryMobs, "Legendary");
		printArray(player, heroMobs, "Heroic");
		printArray(player, eliteMobs, "Elite");
		printArray(player, commonMobs, "Normal");

		PacketSendUtility.sendMessage(player, "=============================\n" + "End of Results.");
	}

	private void printArray(Player p, List<String> array, String type) {
		if (array.size() == 0) {
			return;
		}
		PacketSendUtility.sendMessage(p, "\n=================== " + type + " ===================\n");
		for (String string : array) {
			PacketSendUtility.sendMessage(p, string);
		}
	}
}
