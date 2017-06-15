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
package quest.miragent_holy_templar;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author Nanou
 */
public class _3933ClassPreceptorConsent extends QuestHandler {

	private final static int questId = 3933;

	public _3933ClassPreceptorConsent() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 203704, 203705, 203706, 203707, 203752, 203701 };
		qe.registerQuestNpc(203701).addOnQuestStart(questId);// Lavirintos
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		QuestDialog dialog = env.getDialog();

		// 0 - Start to Lavirintos
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203701) {
				if (dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				// 1 - Receive the signature of Boreas on the recommendation letter.
				case 203704:
					switch (dialog) {
						case START_DIALOG:
							return sendQuestDialog(env, 1011);
						case STEP_TO_1:
							return defaultCloseDialog(env, 0, 1);
					}
					// 2 - Receive the signature of Jumentis on the recommendation letter.
				case 203705:
					if (var == 1) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 1352);
							case STEP_TO_2:
								return defaultCloseDialog(env, 1, 2);
						}
					}
					// 3 - Receive the signature of Charna on the recommendation letter.
				case 203706:
					if (var == 2) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 1693);
							case STEP_TO_3:
								return defaultCloseDialog(env, 2, 3);
						}
					}
					// 4 - Receive the signature of Thrasymedes on the recommendation letter.
				case 203707:
					if (var == 3) {
						switch (dialog) {
							case START_DIALOG:
								return sendQuestDialog(env, 2034);
							case STEP_TO_4:
								return defaultCloseDialog(env, 3, 4);
						}
					}
					// 5 - Report the result to Lavirintos with the Oath Stone
				case 203752:
					switch (dialog) {
						case START_DIALOG: {
							if (var == 4) {
								return sendQuestDialog(env, 2375);
							}
						}
						case SET_REWARD: {
							if (player.getInventory().getItemCountByItemId(186000080) >= 1) {
								removeQuestItem(env, 186000080, 1);
								return defaultCloseDialog(env, 4, 4, true, false, 0);
							}
							else {
								return sendQuestDialog(env, 2461);
							}
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
					break;
					// No match
				default:
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203701) {
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
}
