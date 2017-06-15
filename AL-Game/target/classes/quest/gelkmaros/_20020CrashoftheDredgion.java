/*
 * This file is part of Aion Lightning <aion-lightning.org>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.gelkmaros;

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
 * @author Gigi
 * @modified vlog
 */
public class _20020CrashoftheDredgion extends QuestHandler {

	private final static int questId = 20020;
	private final static int[] npcs = { 799225, 799226, 799239, 798703 };

	public _20020CrashoftheDredgion() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(799342).addOnKillEvent(questId);
		qe.registerQuestItem(182207600, questId);
		qe.registerOnEnterZone(ZoneName.COWARDS_COVE_220070000, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		QuestDialog dialog = env.getDialog();
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 799225: { // Richelle
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 799226: { // Valetta
					switch (dialog) {
						case START_DIALOG: {
							switch (var) {
								case 1: {
									return sendQuestDialog(env, 1352);
								}
								case 2: {
									return sendQuestDialog(env, 1693);
								}
								case 5: {
									return sendQuestDialog(env, 2716);
								}
								case 7: {
									return sendQuestDialog(env, 3398);
								}
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case CHECK_COLLECTED_ITEMS: {
							return checkQuestItems(env, 2, 3, false, 10000, 10001); // 3
						}
						case STEP_TO_6: {
							return defaultCloseDialog(env, 5, 6); // 6
						}
						case STEP_TO_8: {
							return defaultCloseDialog(env, 7, 8); // 8
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 799239: { // Vellun
					switch (dialog) {
						case START_DIALOG: {
							if (var == 3) {
								return sendQuestDialog(env, 2034);
							}
						}
						case STEP_TO_4: {
							return defaultCloseDialog(env, 3, 4); // 4
						}
					}
					break;
				}
				case 798703: { // Heszti
					switch (dialog) {
						case START_DIALOG: {
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case STEP_TO_5: {
							return defaultCloseDialog(env, 4, 5, 182207600, 1, 0, 0); // 5
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799226) { // Valetta
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					if (var == 7)
						playQuestMovie(env, 552);
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
			if (player.isInsideZone(ZoneName.DF4_ITEMUSEAREA_Q20020)) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 6, 7, false)); // 7
			}
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		Player player = env.getPlayer();
		if (player == null)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (zoneName == ZoneName.COWARDS_COVE_220070000) {
				int var = qs.getQuestVarById(0);
				if (var == 8) {
					changeQuestStep(env, 8, 8, true); // reward
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20000, true);
	}
}
