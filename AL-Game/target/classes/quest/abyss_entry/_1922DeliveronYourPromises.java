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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique. If not, see <http://www.gnu.org/licenses/>.
 */
package quest.abyss_entry;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Hellboy, aion4Free
 * @modified Gigi
 * @reworked vlog On 2.5 the quest has dialogs only for one choice (underground arena)
 */
public class _1922DeliveronYourPromises extends QuestHandler {

	private final static int questId = 1922;
	private int choice = 0;

	public _1922DeliveronYourPromises() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerQuestNpc(203830).addOnTalkEvent(questId);
		qe.registerQuestNpc(203901).addOnTalkEvent(questId);
		// qe.registerQuestNpc(210802).addOnKillEvent(questId);
		// qe.registerQuestNpc(210794).addOnKillEvent(questId);
		// qe.registerQuestNpc(210791).addOnKillEvent(questId);
		// qe.registerQuestNpc(210781).addOnKillEvent(questId);
		qe.registerQuestNpc(203764).addOnTalkEvent(questId);
		// qe.registerQuestNpc(700368).addOnTalkEvent(questId);
		// qe.registerQuestNpc(700369).addOnTalkEvent(questId);
		qe.registerQuestNpc(213582).addOnKillEvent(questId);
		qe.registerQuestNpc(213580).addOnKillEvent(questId);
		qe.registerQuestNpc(213581).addOnKillEvent(questId);
		// qe.registerQuestNpc(700264).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203830: { // Fuchsia
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
							else if (var == 4) {
								return sendQuestSelectionDialog(env);
							}
						}
						case STEP_TO_12: {
							choice = 1;
							return defaultCloseDialog(env, 0, 4); // 4
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
						// case STEP_TO_11:
						// return defaultCloseDialog(env, 0, 1); // 1
						// case STEP_TO_13:
						// return defaultCloseDialog(env, 0, 9); // 9
						// case CHECK_COLLECTED_ITEMS:
						// return sendQuestDialog(env, 2375);
						// case SELECT_ACTION_1013:
						// qs.setQuestVar(0);
						// updateQuestStatus(env);
						// return sendQuestDialog(env, 1013);
					}
					break;
				}
				case 203901: { // Telemachus
					switch (env.getDialog()) {
						case USE_OBJECT: {
							// if (var == 1)
							// return sendQuestDialog(env, 1352);
							// else if (var == 2)
							// return sendQuestDialog(env, 3398);
							if (var == 7) {
								return sendQuestDialog(env, 3739);
							}
							// else if (var == 9) {
							// if (QuestService.collectItemCheck(env, true)) {
							// qs.setStatus(QuestStatus.REWARD);
							// updateQuestStatus(env);
							// return sendQuestDialog(env, 7);
							// }
							// else
							// return sendQuestDialog(env, 4080);
							// }
						}
						// case STEP_TO_2:
						// return defaultCloseDialog(env, 1, 2); // 2
						case SELECT_REWARD:
							// if (var == 2) {
							// qs.setStatus(QuestStatus.REWARD);
							// updateQuestStatus(env);
							// return sendQuestDialog(env, 5);
							// }
							if (var == 7) {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 6);
							}
					}
					break;
				}
				case 203764: { // Epeios
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 4) {
								return sendQuestDialog(env, 1693);
							}
							else if (qs.getQuestVarById(4) == 10) {
								return sendQuestDialog(env, 2034);
							}
						}
						case STEP_TO_3: {
							WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310080000);
							InstanceService.registerPlayerWithInstance(newInstance, player);
							TeleportService.teleportTo(player, 310080000, newInstance.getInstanceId(), 276, 293, 163, 3000, true);
							changeQuestStep(env, 4, 5, false); // 5
							return closeDialogWindow(env);
						}
						case STEP_TO_4: {
							qs.setQuestVar(7);
							updateQuestStatus(env);
							return defaultCloseDialog(env, 7, 7); // 7
						}
					}
					break;
				}
				// case 700264: {
				// switch (env.getDialog()) {
				// case USE_OBJECT:
				// if (var == 9) {
				// return true; // loot
				// }
				// }
				// }
				// break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) { // Telemachus
				return sendQuestEndDialog(env, choice);
			}
		}
		return false;
	}

	// @Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 5) {
				int var4 = qs.getQuestVarById(4);
				int[] mobs = { 213580, 213581, 213582 };
				if (var4 < 9) {
					return defaultOnKillEvent(env, mobs, 0, 9, 4); // 4: 1 - 9
				}
				else if (var4 == 9) {
					defaultOnKillEvent(env, mobs, 9, 10, 4); // 4: 10
					QuestService.questTimerEnd(env);
					TeleportService.teleportTo(player, 110010000, 1466.036f, 1337.2749f, 566.41583f, 86);
					return true;
				}
			}
		}

		// int targetId = 0;
		// int var = 0;
		// if (env.getVisibleObject() instanceof Npc)
		// targetId = ((Npc) env.getVisibleObject()).getNpcId();
		// switch (targetId) {
		// case 210802:
		// var = qs.getQuestVarById(1);
		// if (var < 3) {
		// qs.setQuestVarById(1, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 210794:
		// var = qs.getQuestVarById(1);
		// if (var < 3) {
		// qs.setQuestVarById(1, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 210791:
		// var = qs.getQuestVarById(2);
		// if (var < 3) {
		// qs.setQuestVarById(2, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 210781:
		// var = qs.getQuestVarById(3);
		// if (var < 3) {
		// qs.setQuestVarById(3, var + 1);
		// updateQuestStatus(env);
		// }
		// break;
		// case 213580:
		// case 213581:
		// case 213582:
		// var = qs.getQuestVarById(4);
		// if (var < 9) {
		// qs.setQuestVarById(4, var + 1);
		// updateQuestStatus(env);
		// }
		// else if (var == 9) {
		// QuestService.questTimerEnd(env);
		// // movie
		// TeleportService.teleportTo(player, 110010000, 1466.036f, 1337.2749f, 566.41583f, 86);
		// qs.setQuestVarById(4, 10);
		// updateQuestStatus(env);
		// }
		// break;
		// }
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var4 = qs.getQuestVarById(4);
			if (var4 < 10) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				TeleportService.teleportTo(player, 110010000, 1466.036f, 1337.2749f, 566.41583f, 86);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var4 = qs.getQuestVars().getVarById(4);
			if (var == 5 && var4 != 10) {
				if (player.getWorldId() != 310080000) {
					QuestService.questTimerEnd(env);
					qs.setQuestVar(4);
					updateQuestStatus(env);
					return true;
				}
				else {
					QuestService.questTimerStart(env, 240);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1921);
	}
}
