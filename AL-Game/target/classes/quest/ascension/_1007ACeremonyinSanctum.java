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
package quest.ascension;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService;

/**
 * @author MrPoke + Dune11
 * @reworked vlog
 */
public class _1007ACeremonyinSanctum extends QuestHandler {

	private final static int questId = 1007;

	public _1007ACeremonyinSanctum() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 790001, 203725, 203752, 203758, 203759, 203760, 203761 };
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			return;
		}
		qe.registerOnLevelUp(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVars().getQuestVars();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 790001: { // Pernos
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							changeQuestStep(env, 0, 1, false); // 1
							TeleportService.teleportTo(player, 110010000, 1313f, 1512f, 568f, 0);
							return closeDialogWindow(env);
						}
					}
					break;
				}
				case 203725: { // Leah
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SELECT_ACTION_1353: {
							return playQuestMovie(env, 92);
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				}
				case 203752: { // Jucleas
					switch (dialog) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SELECT_ACTION_1694: {
							return playQuestMovie(env, 91);
						}
						case STEP_TO_3: {
							if (var == 2) {
								PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
								switch (playerClass) {
									case WARRIOR: {
										qs.setQuestVar(10);
										break;
									}
									case SCOUT: {
										qs.setQuestVar(20);
										break;
									}
									case MAGE: {
										qs.setQuestVar(30);
										break;
									}
									case PRIEST: {
										qs.setQuestVar(40);
										break;
									}
								}
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestSelectionDialog(env);
							}
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			switch (var) {
				case 10: {
					if (targetId == 203758) { // Macus
						switch (dialog) {
							case USE_OBJECT: {
								return sendQuestDialog(env, 2034);
							}
							default: {
								return sendQuestEndDialog(env, 0);
							}
						}
					}
					break;
				}
				case 20: {
					if (targetId == 203759) { // Eumelos
						switch (dialog) {
							case USE_OBJECT: {
								return sendQuestDialog(env, 2375);
							}
							default: {
								return sendQuestEndDialog(env, 1);
							}
						}
					}
					break;
				}
				case 30: {
					if (targetId == 203760) { // Bellia
						switch (dialog) {
							case USE_OBJECT: {
								return sendQuestDialog(env, 2716);
							}
							default: {
								return sendQuestEndDialog(env, 2);
							}
						}
					}
					break;
				}
				case 40: {
					if (targetId == 203761) { // Hygea
						switch (dialog) {
							case USE_OBJECT: {
								return sendQuestDialog(env, 3057);
							}
							default: {
								return sendQuestEndDialog(env, 3);
							}
						}
					}
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1006);
	}
}
