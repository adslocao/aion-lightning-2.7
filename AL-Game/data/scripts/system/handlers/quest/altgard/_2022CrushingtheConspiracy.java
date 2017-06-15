/*
 * This file is part of aion-unique <aion-unique.org>.
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
package quest.altgard;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService;

/**
 * Talk with Suthran (203557). Go to Zemurru's Grave (220030000) and find the Abyss Gate. Then, go into the sealed area.
 * Disrupt the Gate Guardian Stone (700140), which maintains the Abyss Gate(700141). Defeat Kuninasha (214103). Destroy
 * the Abyss Gate(700141). Report the result to Suthran.
 * 
 * @author HGabor85
 * @modified Gigi
 * @reworked vlog
 */
public class _2022CrushingtheConspiracy extends QuestHandler {

	private final static int questId = 2022;
	private final static int[] npcs = { 203557, 700140, 700141 };

	public _2022CrushingtheConspiracy() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(154, questId);
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		qe.registerQuestNpc(214103).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203557: { // Suthran
					if (env.getDialog() == QuestDialog.START_DIALOG && var == 0) {
						return sendQuestDialog(env, 1011);
					}
					else if (env.getDialog() == QuestDialog.STEP_TO_1) {
						TeleportService.teleportTo(player, 220030000, 2453.1934f, 2555.148f, 316.267f, 0);
						changeQuestStep(env, 0, 1, false); // 1
						return closeDialogWindow(env);
					}
					else if (env.getDialogId() == 1013) {
						playQuestMovie(env, 66);
						return sendQuestDialog(env, 1013);
					}
					break;
				}
				case 700140: { // Gate Guardian Stone
					if (var == 2) {
						if (env.getDialog() == QuestDialog.USE_OBJECT) {
							QuestService.addNewSpawn(320030000, player.getInstanceId(), 214103, (float) 260.12, (float) 234.93,
								(float) 216.00, (byte) 90);
							return useQuestObject(env, 2, 3, false, true); // 3
						}
					}
				}
				case 700141: { //Abyss Gate
					if (var == 4) {
						if (env.getDialog() == QuestDialog.USE_OBJECT) {
							return playQuestMovie(env, 154);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203557) { // Suthran
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 1352);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 2 && player.getWorldId() != 320030000) {
				changeQuestStep(env, var, 1, false);
				return true;
			}
			else if (var == 1 && player.getWorldId() == 320030000) {
				changeQuestStep(env, 1, 2, false); // 2
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 214103, 3, 4); // 4
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 2) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId == 154) {
			changeQuestStep(env, 4, 4, true); // reward
			TeleportService.teleportTo(env.getPlayer(), 220030000, 2453.1934f, 2555.148f, 316.267f, 0);
			return true;
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] altgardQuests = {2012, 2011, 2013, 2014, 2016, 2015, 2021, 2017, 2018, 2019, 2020};
		return defaultOnZoneMissionEndEvent(env, altgardQuests);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] altgardQuests = {2012, 2011, 2013, 2014, 2016, 2015, 2021, 2017, 2018, 2019, 2020};
		return defaultOnLvlUpEvent(env, altgardQuests, true);
	}
}
