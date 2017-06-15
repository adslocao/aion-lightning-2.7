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
package com.aionemu.gameserver.questEngine.handlers.template;

import javolution.util.FastMap;

import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.questEngine.handlers.models.Monster;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.MathUtil;


/**
 * @author MrPoke
 *
 */
public class MentorMonsterHunt extends MonsterHunt {

	private int menteMinLevel;
	private int menteMaxLevel;
	private QuestTemplate qt;
	/**
	 * @param questId
	 * @param startNpc
	 * @param endNpc
	 * @param monsters
	 */
	public MentorMonsterHunt(int questId, int startNpc, int endNpc, FastMap<Integer, Monster> monsters, int menteMinLevel, int menteMaxLevel) {
		super(questId, startNpc, 0, endNpc, 0, monsters);
		this.menteMinLevel = menteMinLevel;
		this.menteMaxLevel = menteMaxLevel;
		this.qt = DataManager.QUEST_DATA.getQuestById(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if (qs != null && qs.getStatus() == QuestStatus.START){
			switch(qt.getMentorType()){
				case MENTOR:
					if (player.isMentor()) {
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()) {
							if (member.getLevel() >= menteMinLevel && member.getLevel() <= menteMaxLevel
								&& MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE) {
								return super.onKillEvent(env);
							}
						}
					}
					break;
				case MENTE:
					if (player.isInGroup2()){
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()){
							if (member.isMentor() && MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE)
								return super.onKillEvent(env);
						}
					}
			}
		}
		return false;
	}
}
