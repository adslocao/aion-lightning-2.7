/*
 * This file is part of aion-lightning <aion-lightning.org>.
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
package com.aionemu.gameserver.model.templates.quest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.QuestStateList;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Checks quest start conditions, listed in quest_data.xml
 * 
 * @author antness
 * @reworked vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStartConditions")
public class XMLStartCondition {

	@XmlElement(name = "finished")
	protected List<FinishedQuestCond> finished;
	@XmlList
	@XmlElement(name = "unfinished", type = Integer.class)
	protected List<Integer> unfinished;
	@XmlList
	@XmlElement(name = "noacquired", type = Integer.class)
	protected List<Integer> noacquired;
	@XmlList
	@XmlElement(name = "acquired", type = Integer.class)
	protected List<Integer> acquired;
	@XmlList
	@XmlElement(name = "equipped", type = Integer.class)
	protected List<Integer> equipped;

	/** Check, if the player has finished listed quests */
	private boolean checkFinishedQuests(QuestStateList qsl) {
		if (finished != null && finished.size() > 0) {
			for (FinishedQuestCond fqc : finished) {
				int questId = fqc.getQuestId();
				int reward = fqc.getReward();
				QuestState qs = qsl.getQuestState(questId);
				if (qs == null || qs.getStatus() != QuestStatus.COMPLETE || !checkReward(questId, reward, qs.getReward())) {
					return false;
				}
				QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
				if (template != null && template.isRepeatable()) {
					if (qs.getCompleteCount() != template.getMaxRepeatCount()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/** Check, if the player has not finished listed quests */
	private boolean checkUnfinishedQuests(QuestStateList qsl) {
		if (unfinished != null && unfinished.size() > 0) {
			for (Integer questId : unfinished) {
				QuestState qs = qsl.getQuestState(questId);
				if (qs != null && qs.getStatus() == QuestStatus.COMPLETE)
					return false;
			}
		}
		return true;
	}

	/** Check, if the player has not acquired listed quests */
	private boolean checkNoAcquiredQuests(QuestStateList qsl) {
		if (noacquired != null && noacquired.size() > 0) {
			for (Integer questId : noacquired) {
				QuestState qs = qsl.getQuestState(questId);
				if (qs != null
					&& (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD || qs.getStatus() == QuestStatus.COMPLETE))
					return false;
			}
		}
		return true;
	}

	/** Check, if the player has acquired listed quests */
	private boolean checkAcquiredQuests(QuestStateList qsl) {
		if (acquired != null && acquired.size() > 0) {
			for (Integer questId : acquired) {
				QuestState qs = qsl.getQuestState(questId);
				if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.LOCKED)
					return false;
			}
		}
		return true;
	}

	private boolean checkEquippedItems(Player player) {
		if (equipped != null && equipped.size() > 0) {
			for (int itemId : equipped) {
				if (!player.getEquipment().getEquippedItemIds().contains(itemId)) {
					return false;
				}
			}
		}
		return true;
	}

	/** Check all conditions */
	public boolean check(Player player) {
		QuestStateList qsl = player.getQuestStateList();
		return checkFinishedQuests(qsl) && checkUnfinishedQuests(qsl) && checkAcquiredQuests(qsl)
			&& checkNoAcquiredQuests(qsl) && checkEquippedItems(player);
	}

	private boolean checkReward(int questId, int neededReward, int currentReward) {
		// Temporary exceptions-quests till abyss entry quests work with correct reward
		if (neededReward != currentReward && questId != 2947 && questId != 1922) {
			return false;
		}
		return true;
	}

	public List<FinishedQuestCond> getFinishedPreconditions() {
		return finished;
	}
}
