/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Nephis
 * @reworked & modified Gigi, vlog
 */
public class _10024WillTheAetherRain extends QuestHandler {

	private final static int questId = 10024;
	private final static int[] npc_ids = { 799020, 700605, 798970, 798979, 203793 };

	public _10024WillTheAetherRain() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182206620, questId);
		qe.registerQuestNpc(216498).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798970: { // Pomponia
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
							else if (var == 7) {
								return sendQuestDialog(env, 3398);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case STEP_TO_5: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
						case STEP_TO_8: {
							return defaultCloseDialog(env, 7, 8, 182206620, 1, 0, 0); // 8
						}
					}
					break;
				}
				case 798979: { // Gelon
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case STEP_TO_4: {
							return defaultCloseDialog(env, 3, 4); // 4
						}
					}
					break;
				}
				case 700605: { // Parchment Map
					switch (dialog) {
						case USE_OBJECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_3: {
							return defaultCloseDialog(env, 2, 3); // 3
						}
					}
					break;
				}
				case 203793: { // Daphnis
					switch (dialog) {
						case START_DIALOG: {
							if (var == 5) {
								return sendQuestDialog(env, 2716);
							}
							else if (var == 6) {
								return sendQuestDialog(env, 3057);
							}
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 5, 6, false, 10000, 10001); // 6
						}
						case STEP_TO_7: {
							return defaultCloseDialog(env, 6, 7); // 7
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 799020: { // Donikia
					switch (dialog) {
						case START_DIALOG: {
							if (var == 10) {
								return sendQuestDialog(env, 1608);
							}
						}
						case SET_REWARD: {
							return defaultCloseDialog(env, 10, 10, true, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798970) { // Pomponia
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.isInsideZone(ZoneName.HANARKAND_PRISON_ENTRANCE_210050000)) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 8, 9, false)); // 9
			}
		}
		return  HandlerResult.FAILED;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 216498, 9, 10); // 10
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10022, true);
	}
}
