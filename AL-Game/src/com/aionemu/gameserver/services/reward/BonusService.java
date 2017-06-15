/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.ItemGroupsData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.itemgroups.*;
import com.aionemu.gameserver.model.templates.quest.QuestBonuses;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.rewards.BonusType;
import com.aionemu.gameserver.model.templates.rewards.CraftItem;
import com.aionemu.gameserver.model.templates.rewards.IdReward;

/**
 * @author Rolandas
 */
public class BonusService {

	private static BonusService instance = new BonusService();
	private ItemGroupsData itemGroups = DataManager.ITEM_GROUPS_DATA;
	private static final Logger log = LoggerFactory.getLogger(BonusService.class);

	private BonusService() {
	}
	
	public static BonusService getInstance() {
		return instance;
	}

	public static BonusService getInstance(ItemGroupsData itemGroups) {
		instance.itemGroups = itemGroups;
		return instance;
	}

	public ItemGroup[] getGroupsByType(BonusType type) {
		switch (type) {
			case BOSS:
				return itemGroups.getBossGroups();
			case ENCHANT:
				return itemGroups.getEnchantGroups();
			case FOOD:
				return itemGroups.getFoodGroups();
			case GATHER:
				return (ItemGroup[]) ArrayUtils.addAll(itemGroups.getOreGroups(), itemGroups.getGatherGroups());
			case MANASTONE:
				return itemGroups.getManastoneGroups();
			case MEDICINE:
				return itemGroups.getMedicineGroups();
			case TASK:
				return itemGroups.getCraftGroups();
			case MOVIE:
				return null;
			default:
				log.warn("Bonus of type " + type + " is not implemented");
				return null;
		}
	}

	public ItemGroup getRandomGroup(ItemGroup[] groups) {
		float total = 0;
		for (ItemGroup gr : groups)
			total += gr.getChance();
		if (total == 0)
			return null;

		ItemGroup chosenGroup = null;
		if (groups != null) {
			int percent = 100;
			for (ItemGroup gr : groups) {
				float chance = getNormalizedChance(gr.getChance(), total);
				if (Rnd.get(0, percent) <= chance) {
					chosenGroup = gr;
					break;
				}
				else
					percent -= chance;
			}
		}
		return chosenGroup;
	}

	float getNormalizedChance(float chance, float total) {
		return chance * 100f / total;
	}

	public ItemGroup getRandomGroup(BonusType type) {
		return getRandomGroup(getGroupsByType(type));
	}

	public QuestItems getQuestBonus(Player player, QuestTemplate questTemplate) {
		List<QuestBonuses> bonuses = questTemplate.getBonus();
		if (bonuses.isEmpty())
			return null;
		// Only one
		QuestBonuses bonus = bonuses.get(0);
		if (bonus.getType() == BonusType.NONE)
			return null;

		switch (bonus.getType()) {
			case TASK:
				return getCraftBonus(player, questTemplate);
			case MANASTONE:
				return getManastoneBonus(player, bonus);
			case MOVIE:
				return null;
			default:
				log.warn("Bonus of type " + bonus.getType() + " is not implemented");
				return null;
		}
	}

	QuestItems getCraftBonus(Player player, QuestTemplate questTemplate) {
		ItemGroup[] groups = itemGroups.getCraftGroups();
		CraftGroup group = null;
		IdReward[] allRewards = null;

		while (groups != null && groups.length > 0 && group == null) {
			group = (CraftGroup) getRandomGroup(groups);
			if (group == null)
				break;
			allRewards = group.getRewards(questTemplate.getCombineSkill(), questTemplate.getCombineSkillPoint());
			if (allRewards.length == 0) {
				List<ItemGroup> temp = new ArrayList<ItemGroup>();
				Collections.addAll(temp, groups);
				temp.remove(group);
				group = null;
				groups = temp.toArray(new ItemGroup[0]);
			}
		}

		if (group == null) // probably all chances set to 0
			return null;
		List<IdReward> finalList = new ArrayList<IdReward>();

		for (int i = 0; i < allRewards.length; i++) {
			IdReward r = allRewards[i];
			if (!r.checkRace(player.getCommonData().getRace()))
				continue;
			finalList.add(r);
		}

		if (finalList.isEmpty())
			return null;

		int itemIndex = Rnd.get(finalList.size());
		int itemCount = 1;

		IdReward reward = finalList.get(itemIndex);
		if (reward instanceof CraftItem)
			itemCount = Rnd.get(3, 5);

		return new QuestItems(reward.getId(), itemCount);
	}

	QuestItems getManastoneBonus(Player player, QuestBonuses bonus) {
		ManastoneGroup group = (ManastoneGroup) getRandomGroup(BonusType.MANASTONE);
		IdReward[] allRewards = group.getRewards();
		List<IdReward> finalList = new ArrayList<IdReward>();
		for (int i = 0; i < allRewards.length; i++) {
			IdReward r = allRewards[i];
			ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(r.getId());
			if (bonus.getLevel() != template.getLevel())
				continue;
			finalList.add(r);
		}
		if (finalList.isEmpty())
			return null;

		int itemIndex = Rnd.get(finalList.size());
		IdReward reward = finalList.get(itemIndex);
		return new QuestItems(reward.getId(), 1);
	}
}
