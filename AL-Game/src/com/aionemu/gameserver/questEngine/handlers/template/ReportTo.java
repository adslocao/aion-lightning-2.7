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
package com.aionemu.gameserver.questEngine.handlers.template;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke Like: Sleeping on the Job quest.
 */
public class ReportTo extends QuestHandler {

	private final int questId;
	private final int startNpc;
	private final int startNpc2;
	private final int endNpc;
	private final int endNpc2;
	private final int itemId;

	/**
	 * @param questId
	 * @param startNpc
	 * @param endNpc
	 */
	public ReportTo(int questId, int startNpc, int startNpc2, int endNpc, int endNpc2, int itemId) {
		super(questId);
		this.startNpc = startNpc;
		if (startNpc2 != 0) {
			this.startNpc2 = startNpc2;
		}
		else {
			this.startNpc2 = this.startNpc;
		}
		this.endNpc = endNpc;
		if (endNpc2 != 0) {
			this.endNpc2 = endNpc2;
		}
		else {
			this.endNpc2 = this.endNpc;
		}
		this.questId = questId;
		this.itemId = itemId;
	}

	@Override
	public void register() {
		if (startNpc != 0) {
			qe.registerQuestNpc(startNpc).addOnQuestStart(questId);
			qe.registerQuestNpc(startNpc).addOnTalkEvent(questId);
		}
		if (startNpc2 != 0) {
			qe.registerQuestNpc(startNpc2).addOnQuestStart(questId);
			qe.registerQuestNpc(startNpc2).addOnTalkEvent(questId);
		}
		qe.registerQuestNpc(endNpc).addOnTalkEvent(questId);
		if (endNpc2 != 0) {
			qe.registerQuestNpc(endNpc2).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpc == 0 || targetId == startNpc || targetId == startNpc2) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1011);
					}
					case ACCEPT_QUEST: {
						if (itemId != 0) {
							if (giveQuestItem(env, itemId, 1)) {
								return sendQuestStartDialog(env);
							}
							return false;
						}
						else {
							return sendQuestStartDialog(env);
						}
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == startNpc || targetId == startNpc2) {
				if (dialog == QuestDialog.FINISH_DIALOG) {
					return sendQuestSelectionDialog(env);
				}
			}
			else if (targetId == endNpc || targetId == endNpc2) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_REWARD: {
						if (itemId != 0) {
							if (player.getInventory().getItemCountByItemId(itemId) < 1) {
								return sendQuestSelectionDialog(env);
							}
						}
						removeQuestItem(env, itemId, 1);
						qs.setQuestVar(1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == endNpc || targetId == endNpc2) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
