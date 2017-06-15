/*
 * This file is part of aion-unique <www.aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ExtractedItemsCollection;
import com.aionemu.gameserver.model.templates.item.RandomItem;
import com.aionemu.gameserver.model.templates.item.ResultedItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author oslo(a00441234)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DecomposeAction")
public class DecomposeAction extends AbstractItemAction {

	private static final Logger log = LoggerFactory.getLogger(DecomposeAction.class);

	private static final int USAGE_DELAY = 1000;
	private static Map<Integer, int[]> manastones = new HashMap<Integer, int[]>();
	static {
		manastones.put(10, new int[] { 167000226, 167000227, 167000228, 167000229, 167000230, 167000231, 167000232,
			167000233, 167000235 });
		manastones.put(20, new int[] { 167000258, 167000259, 167000260, 167000261, 167000263, 167000264, 167000265,
			167000267, 167000418, 167000419, 167000420, 167000421, 167000423, 167000424, 167000425, 167000427 });
		manastones.put(30, new int[] { 167000290, 167000291, 167000292, 167000293, 167000294, 167000295, 167000296,
			167000297, 167000299, 167000450, 167000451, 167000452, 167000453, 167000454, 167000455, 167000456, 167000457,
			167000459 });
		manastones.put(40, new int[] { 167000322, 167000323, 167000324, 167000325, 167000327, 167000328, 167000329,
			167000331, 167000482, 167000483, 167000484, 167000485, 167000487, 167000488, 167000489, 167000491, 167000539,
			167000540 });
		manastones.put(50, new int[] { 167000354, 167000355, 167000356, 167000357, 167000358, 167000359, 167000360,
			167000361, 167000363, 167000514, 167000515, 167000516, 167000517, 167000518, 167000519, 167000520, 167000521,
			167000522, 167000541, 167000542 });
		manastones.put(60, new int[] { 167000543, 167000544, 167000545, 167000546, 167000547, 167000548, 167000549,
			167000550, 167000551, 167000552, 167000553, 167000554, 167000555, 167000556, 167000557, 167000558, 167000560,
			167000561 });
	}

	private static Map<Race, int[]> chunkEarth = new HashMap<Race, int[]>(); 
	static { 
		chunkEarth.put(Race.ASMODIANS, new int[] {152000051, 152000052, 152000053, 152000451, 152000453, 152000551, 
			152000651, 152000751, 152000752, 152000753, 152000851, 152000852, 152000853, 152001051, 152001052,
			152000201, 152000102, 152000054, 152000055, 152000455, 152000457, 152000552, 152000652, 152000754,
			152000755, 152000854, 152000855, 152000102, 152000202, 152000056, 152000057, 152000459, 152000461,
			152000553, 152000653, 152000756, 152000757, 152000856, 152000857, 152000104, 152000204, 152000058, 
			152000059, 152000463, 152000465, 152000554, 152000654, 152000758, 152000759, 152000760, 152000858,
			152001053, 152000107, 152000207, 152003004, 152003005, 152003006, 152000061, 152000062, 152000063,
			152000468, 152000470, 152000556, 152000656, 152000657, 152000762, 152000763, 152000860, 152000861,
			152000862, 152001055, 152001056, 152000113, 152000117, 152000214, 152000606, 152000713,	152000811 });
	
		chunkEarth.put(Race.ELYOS, new int[] { 152000001, 152000002, 152000003, 152000401, 152000403, 152000501,
			152000601, 152000701, 152000702, 152000703, 152000801, 152000802, 152000803, 152001001, 152001002,
			152000101, 152000201, 152000004, 152000005, 152000405, 152000407, 152000502, 152000602, 152000704,
			152000705, 152000804, 152000805, 152000102, 152000202, 152000006, 152000007, 152000409, 152000411,
			152000503, 152000603, 152000706, 152000707, 152000806, 152000807, 152000104, 152000204, 152000008, 
			152000009, 152000413, 152000415, 152000504, 152000604, 152000708, 152000709, 152000710, 152000808,
			152001003, 152000107, 152000207, 152003004, 152003005, 152003006, 152000010, 152000011, 152000012,
			152000417, 152000419, 152000505, 152000605, 152000607, 152000711, 152000712, 152000809, 152000810,
			152000812, 152001004, 152001005, 152000113, 152000117, 152000214, 152000606, 152000713,	152000811 });
	}
	private static Map<Race, int[]> chunkSand = new HashMap<Race, int[]>();
	static {
		chunkSand.put(Race.ASMODIANS, new int[] { 152000452, 152000454, 152000301, 152000302, 152000303 , 152000456, 
		152000458, 152000103, 152000203, 152000304, 152000305, 152000306, 152000460, 152000462, 152000105,
		152000205, 152000307, 152000309, 152000311, 152000464, 152000466, 152000108, 152000208, 152000313,
		152000315, 152000317, 152000469, 152000471, 152000114, 152000215, 152000320, 152000322,	152000324 });

		chunkSand.put(Race.ELYOS, new int[] { 152000402, 152000404, 152000301, 152000302, 152000303, 152000406,
		152000408, 152000103, 152000203, 152000304, 152000305, 152000306, 152000410, 152000412, 152000105,
		152000205, 152000307, 152000309, 152000311, 152000414, 152000416, 152000108, 152000208, 152000313, 
		152000315, 152000317, 152000418, 152000420, 152000114, 152000215, 152000320, 152000322,	152000324 });
	}
	
	private static int[] chunkRock = { 152000106, 152000206, 152000308, 152000310, 152000312, 152000109, 
			152000209, 152000314, 152000316, 152000318, 152000115, 152000216, 152000219, 152000321,	152000323, 
			152000325 };
	
	private static int[] chunkGemstone = { 152000112, 152000213, 152000116, 152000212, 152000217, 152000326,
			152000327, 152000328 };

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		List<ExtractedItemsCollection> itemsCollections = DataManager.DECOMPOSABLE_ITEMS_DATA.getInfoByItemId(parentItem.getItemId());
		if (itemsCollections == null || itemsCollections.isEmpty()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_INVALID_STANCE(parentItem.getNameID()));
			return false;
		}
		if (player.getInventory().getFreeSlots() < calcMaxCountOfSlots(itemsCollections)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_INVENTORY_IS_FULL);
			return false;
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		player.getController().cancelUseItem();
		List<ExtractedItemsCollection> itemsCollections = DataManager.DECOMPOSABLE_ITEMS_DATA.getInfoByItemId(parentItem
			.getItemId());

		Collection<ExtractedItemsCollection> levelSuitableItems = filterItemsByLevel(player, itemsCollections);
		final ExtractedItemsCollection selectedCollection = selectItemByChance(levelSuitableItems);

		PacketSendUtility.broadcastPacketAndReceive(player,
			new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), USAGE_DELAY,
				0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				boolean validAction = postValidate(player, parentItem);
				if (validAction) {
					if (selectedCollection.getItems().size() > 0) {
						for (ResultedItem resultItem : selectedCollection.getItems()) {
							if (canAcquire(player, resultItem)) {
								ItemService.addItem(player, resultItem.getItemId(), resultItem.getResultCount());
							}
						}
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_SUCCEED(parentItem.getNameID()));
					}
					else if (selectedCollection.getRandomItems().size() > 0) {
						for (RandomItem randomItem : selectedCollection.getRandomItems()) {
							if (randomItem.getType() != null) {
								int randomId = 0;
								int i = 0;
								switch (randomItem.getType()) {
									case ENCHANTMENT: {
										do {
											int itemLvl = parentItem.getItemTemplate().getLevel();
											randomId = 166000000 + itemLvl + Rnd.get(15);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
									case MANASTONE: {
										int itemLvl = parentItem.getItemTemplate().getLevel();
										//TODO lvl 50 stack lvl 60 manastone?
										int[] stones = manastones.get(itemLvl);
										if (stones == null){
											log.warn("DecomposeAction random item id not found. "+parentItem.getItemTemplate().getTemplateId());
											return;
										}
										randomId = stones[Rnd.get(stones.length)];
											//randomId = Rnd.get(167000226, 167000561);
										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case ANCIENTITEMS: {
										do {
											randomId = Rnd.get(186000051, 186000069);
											i++;
											if (i > 50) {
												randomId = 0;
												log.warn("DecomposeAction random item id not found. " + parentItem.getItemId());
												break;
											}
										}
										while (!ItemService.checkRandomTemplate(randomId));
										break;
									}
									case CHUNK_EARTH: {
										int[] earth = chunkEarth.get(player.getRace());
										
										randomId = earth[Rnd.get(earth.length)];
										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_ROCK: {
										randomId = chunkRock[Rnd.get(chunkRock.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_GEMSTONE: {
										randomId = chunkGemstone[Rnd.get(chunkGemstone.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
									case CHUNK_SAND: {
										int[] sand = chunkSand.get(player.getRace());
									
										randomId = sand[Rnd.get(sand.length)];

										if(!ItemService.checkRandomTemplate(randomId)){
												log.warn("DecomposeAction random item id not found. " + randomId);
												return;
										}
										break;
									}
								}
								if (randomId != 0 && randomId != 167000524)
									ItemService.addItem(player, randomId, randomItem.getCount());
							}
						}
					}
				}
				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
					parentItem.getObjectId(), parentItem.getItemId(), 0, validAction ? 1 : 2, 0));
			}

			private boolean canAcquire(Player player, ResultedItem resultItem) {
				Race race = resultItem.getRace();
				if (race != Race.PC_ALL && !race.equals(player.getRace())) {
					return false;
				}
				return true;
			}

			boolean postValidate(Player player, Item parentItem) {
				if (!canAct(player, parentItem, targetItem)) {
					return false;
				}
				if (player.getLifeStats().isAlreadyDead() || !player.isSpawned()) {
					return false;
				}
				if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_NO_TARGET_ITEM);
					return false;
				}
				if (selectedCollection.getItems().isEmpty() && selectedCollection.getRandomItems().isEmpty()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_FAILED(parentItem.getNameID()));
					return false;
				}
				return true;
			}
		}, USAGE_DELAY));
	}

	/**
	 * Add to result collection only items wich suits player's level
	 */
	private Collection<ExtractedItemsCollection> filterItemsByLevel(Player player,
		List<ExtractedItemsCollection> itemsCollections) {
		int playerLevel = player.getLevel();
		Collection<ExtractedItemsCollection> result = new ArrayList<ExtractedItemsCollection>();
		for (ExtractedItemsCollection collection : itemsCollections) {
			if (collection.getMinLevel() > playerLevel) {
				continue;
			}
			if (collection.getMaxLevel() > 0 && collection.getMaxLevel() < playerLevel) {
				continue;
			}
			result.add(collection);
		}
		return result;
	}

	/**
	 * Select only 1 item based on chance attributes
	 */
	private ExtractedItemsCollection selectItemByChance(Collection<ExtractedItemsCollection> itemsCollections) {
		int sumOfChances = calcSumOfChances(itemsCollections);
		int currentSum = 0;
		int rnd = Rnd.get(0, sumOfChances - 1);
		ExtractedItemsCollection selectedCollection = null;
		for (ExtractedItemsCollection collection : itemsCollections) {
			currentSum += collection.getChance();
			if (rnd < currentSum) {
				selectedCollection = collection;
				break;
			}
		}
		return selectedCollection;
	}

	private int calcMaxCountOfSlots(Collection<ExtractedItemsCollection> itemsCollections) {
		int maxCount = 0;
		for (ExtractedItemsCollection collection : itemsCollections)
			if (collection.getItems().size() > maxCount)
				maxCount = collection.getItems().size();
		return maxCount;
	}

	private int calcSumOfChances(Collection<ExtractedItemsCollection> itemsCollections) {
		int sum = 0;
		for (ExtractedItemsCollection collection : itemsCollections)
			sum += collection.getChance();
		return sum;
	}
}
