/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.drop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.configs.custom.CustomDrop;
import com.aionemu.gameserver.configs.main.DropConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.NpcDropData;
import com.aionemu.gameserver.model.drop.Drop;
import com.aionemu.gameserver.model.drop.DropGroup;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.drop.NpcDrop;
import com.aionemu.gameserver.model.gameobjects.DropNpc;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.DropRewardEnum;

/**
 * @author xTz
 */
public class DropRegistrationService {
	Logger log = LoggerFactory.getLogger(DropRegistrationService.class);

	private Map<Integer, Set<DropItem>> currentDropMap = new FastMap<Integer, Set<DropItem>>().shared();
	private Map<Integer, DropNpc> dropRegistrationMap = new FastMap<Integer, DropNpc>().shared();

	public void registerDrop(Npc npc, Player player, Collection<Player> groupMembers) {
		registerDrop(npc, player, player.getLevel(), groupMembers);
	}

	private DropRegistrationService() {
		init();
	}

	public final void init() {
		NpcDropData npcDrop = DataManager.NPC_DROP_DATA;
		for (NpcDrop drop : npcDrop.getNpcDrop()) {
			NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(drop.getNpcId());
			if (npcTemplate == null) {
				continue;
			}
			if (npcTemplate.getNpcDrop() != null) {
				NpcDrop currentDrop = npcTemplate.getNpcDrop();
				// First clean.
				for (DropGroup dg : currentDrop.getDropGroup()) {
					Iterator<Drop> iter = dg.getDrop().iterator();
					while (iter.hasNext()) {
						Drop d = iter.next();
						for (DropGroup dg2 : drop.getDropGroup()) {
							for (Drop d2 : dg2.getDrop()) {
								if (d.getItemId() == d2.getItemId())
									iter.remove();
							}
						}
					}
				}
				List<DropGroup> list = new ArrayList<DropGroup>();
				for (DropGroup dg : drop.getDropGroup()) {
					boolean added = false;
					for (DropGroup dg2 : currentDrop.getDropGroup()) {
						if (dg2.getGroupName().equals(dg.getGroupName())) {
							dg2.getDrop().addAll(dg.getDrop());
							added = true;
						}
					}
					if (!added)
						list.add(dg);
				}
				if (!list.isEmpty()) {
					currentDrop.getDropGroup().addAll(list);
				}
			}
			else
				npcTemplate.setNpcDrop(drop);
		}
	}

	/**
	 * After NPC dies, it can register arbitrary drop
	 */
	public void registerDrop(Npc npc, Player player, int heighestLevel, Collection<Player> groupMembers) {

		if (player == null) {
			return;
		}
		int npcObjId = npc.getObjectId();

		// Getting all possible drops for this Npc
		NpcDrop npcDrop = npc.getNpcDrop();
		Set<DropItem> droppedItems = new HashSet<DropItem>();
		int index = 1;
		int dropChance = 100;
		boolean isChest = npc.getAi2().getName().equals("chest");
		if (!DropConfig.DISABLE_DROP_REDUCTION && ((isChest && npc.getLevel() != 1 || !isChest))) {
			dropChance = DropRewardEnum.dropRewardFrom(npc.getLevel() - heighestLevel); // reduce chance depending on level
		}

		// Include also Boost Drop Rate skills ONLY for lonely players
		float boostDropRate = 1.0f;
		if (!player.isInGroup2() && !player.isInAlliance2()) {
			boostDropRate = player.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f;
		}
		
		float dropRate = player.getRates().getDropRate() * boostDropRate * dropChance / 100F;
		
		//custom drop
		
		if(CustomDrop.ENABLED && (!CustomDrop.ONLY_HIGH_LVL || heighestLevel <= npc.getLevel())){
			for(Drop drop : CustomDrop.getCustomDrops()){
				if(drop != null){
					if(Rnd.get() * 100 < drop.getChance()){
						DropItem dropitem = new DropItem(drop);
						dropitem.calculateCount();
						dropitem.setIndex(index++);
						droppedItems.add(dropitem);
						ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(drop.getItemId());
						if(itemTemplate == null)
							log.info("[CustomDrop] invalid itemId");
					}
				}
			}
		}
		
		
		if (npcDrop != null) {
			index = npcDrop.dropCalculator(droppedItems, index, dropRate, player.getRace());
		}

		

		
		// Updating current dropMap
		currentDropMap.put(npcObjId, droppedItems);



		
		// Distributing drops to players
		Collection<Player> dropPlayers = new ArrayList<Player>();
		Collection<Player> winningPlayers = new ArrayList<Player>();
		if (player.isInGroup2() || player.isInAlliance2()) {
			List<Integer> dropMembers = new ArrayList<Integer>();
			LootGroupRules lootGrouRules = player.getLootGroupRules();

			switch (lootGrouRules.getLootRule()) {
				case ROUNDROBIN:
					int size = groupMembers.size();
					if (size > lootGrouRules.getNrRoundRobin())
						lootGrouRules.setNrRoundRobin(lootGrouRules.getNrRoundRobin() + 1);
					else
						lootGrouRules.setNrRoundRobin(1);

					int i = 0;
					for (Player p : groupMembers) {
						i++;
						if (i == lootGrouRules.getNrRoundRobin()) {
							winningPlayers.add(p);
							setItemsToWinner(droppedItems, p.getObjectId());
							break;
						}
					}
					break;
				case FREEFORALL:
					winningPlayers = groupMembers;
					break;
				case LEADER:
					Player leader = player.isInGroup2() ? player.getPlayerGroup2().getLeaderObject()
							: player.getPlayerAlliance2().getLeaderObject();
					winningPlayers.add(leader);
					setItemsToWinner(droppedItems, leader.getObjectId());
					break;
			}

			for (Player member : winningPlayers) {
				dropMembers.add(member.getObjectId());
				dropPlayers.add(member);
			}
			DropNpc dropNpc = new DropNpc(npcObjId);
			dropRegistrationMap.put(npcObjId, dropNpc);
			dropNpc.setPlayersObjectId(dropMembers);
			dropNpc.setInRangePlayers(groupMembers);
			dropNpc.setGroupSize(groupMembers.size());
		}
		else {
			List<Integer> singlePlayer = new ArrayList<Integer>();
			singlePlayer.add(player.getObjectId());
			dropPlayers.add(player);
			dropRegistrationMap.put(npcObjId, new DropNpc(npcObjId));
			dropRegistrationMap.get(npcObjId).setPlayersObjectId(singlePlayer);
		}

		if (!player.isInAlliance2()) {
			index = QuestService.getQuestDrop(droppedItems, index, npc, groupMembers, player);
		}
		if (npc.getPosition().isInstanceMap()) {
			npc.getPosition().getWorldMapInstance().getInstanceHandler().onDropRegistered(npc);
		}
		npc.getAi2().onGeneralEvent(AIEventType.DROP_REGISTERED);

		for (Player p : dropPlayers) {
			PacketSendUtility.sendPacket(p, new SM_LOOT_STATUS(npcObjId, 0));
		}
		DropService.getInstance().scheduleFreeForAll(npcObjId);
	}

	public void setItemsToWinner(Set<DropItem> droppedItems, Integer obj) {
		for (DropItem dropItem : droppedItems) {
			dropItem.setPlayerObjId(obj);
		}
	}

	public DropItem regDropItem(int index, int playerObjId, int npcId, int itemId, int count) {
		DropItem item = new DropItem(new Drop(itemId, 1, 1, 100, false));
		item.setPlayerObjId(playerObjId);
		item.setCount(count);
		item.setIndex(index);
		return item;
	}

	/**
	 * @return dropRegistrationMap
	 */
	public Map<Integer, DropNpc> getDropRegistrationMap() {
		return dropRegistrationMap;
	}

	/**
	 * @return currentDropMap
	 */
	public Map<Integer, Set<DropItem>> geCurrentDropMap() {
		return currentDropMap;
	}

	public static DropRegistrationService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DropRegistrationService instance = new DropRegistrationService();
	}
}
