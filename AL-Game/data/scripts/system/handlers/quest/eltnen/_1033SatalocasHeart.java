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
 * @author Xitanium
 * @reworked vlog
 */
public class _1033SatalocasHeart extends QuestHandler {

	private final static int questId = 1033;

	public _1033SatalocasHeart() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203900).addOnTalkEvent(questId);
		qe.registerQuestNpc(203996).addOnTalkEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203900: { // Diomedes
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
				case 203996: { // Kimeia
					long drakeFangs = player.getInventory().getItemCountByItemId(182201019);
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 11) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1695: {
							playQuestMovie(env, 42);
							return sendQuestDialog(env, 1695);
						}
						case STEP_TO_3: {
							QuestService.questTimerStart(env, 180);
							return defaultCloseDialog(env, 1, 10); // 10
						}
						case SELECT_ACTION_2035: {
							if (drakeFangs < 5) {
								removeQuestItem(env, 182201019, drakeFangs);
								changeQuestStep(env, 11, 10, false);
								QuestService.questTimerStart(env, 180);
								return sendQuestDialog(env, 2035);
							}
							else if (drakeFangs >= 5 && drakeFangs < 7) {
								removeQuestItem(env, 182201019, drakeFangs);
								qs.setQuestVar(12);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 2120);
							}
							else if (drakeFangs >= 7) {
								removeQuestItem(env, 182201019, drakeFangs);
								qs.setQuestVar(13);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 2205);
							}
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203900) { // Diomedes
				if (dialog == QuestDialog.USE_OBJECT) {
					switch (var) {
						case 12: {
							return sendQuestDialog(env, 2716);
						}
						case 13: {
							return sendQuestDialog(env, 3057);
						}
					}
				}
				else {
					return sendQuestEndDialog(env, var - 12);
				}
			}
			else if (targetId == 203996) { // Kimeia
				if (dialog == QuestDialog.FINISH_DIALOG) {
					return sendQuestSelectionDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		changeQuestStep(env, 10, 11, false); // 11
		return true;
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
