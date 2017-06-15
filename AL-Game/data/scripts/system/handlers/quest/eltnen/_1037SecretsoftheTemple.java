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
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Rhys2002
 * @reworked vlog
 */
public class _1037SecretsoftheTemple extends QuestHandler {

	private final static int questId = 1037;

	public _1037SecretsoftheTemple() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203965, 203967, 700151, 700154, 700150, 700153, 700152 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerGetingItem(182201027, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null) {
			return false;
		}

		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 203965: { // Castor
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
				case 203967: { // Axelion
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SELECT_ACTION_1694: {
							if (var == 2 && QuestService.collectItemCheck(env, true)) {
								return sendQuestDialog(env, 1694);
							}
							else {
								return sendQuestDialog(env, 1779);
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case STEP_TO_3: {
							giveQuestItem(env, 182201027, 1);
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
				case 700151: { // Flower Wall
					if (dialog == QuestDialog.USE_OBJECT) {
						changeQuestStep(env, 3, 4, false); // 4
						return true;
					}
					break;
				}
				case 700154: { // Lightning Wall
					if (dialog == QuestDialog.USE_OBJECT) {
						changeQuestStep(env, 4, 5, false); // 5
						return true;
					}
					break;
				}
				case 700150: { // Wave Wall
					if (dialog == QuestDialog.USE_OBJECT) {
						changeQuestStep(env, 5, 6, false); // 6
						return true;
					}
					break;
				}
				case 700153: { // Wind Wall
					if (dialog == QuestDialog.USE_OBJECT) {
						changeQuestStep(env, 6, 7, false); // 7
						return true;
					}
					break;
				}
				case 700152: { // Fire Wall
					if (dialog == QuestDialog.USE_OBJECT) {
						changeQuestStep(env, 7, 7, true); // reward
						removeQuestItem(env, 182201027, 1);
						return true;
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203965) { // Castor
				if (dialog == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 2034);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		return defaultOnGetItemEvent(env, 2, 3, false); // 3
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1300, true);
	}
}
