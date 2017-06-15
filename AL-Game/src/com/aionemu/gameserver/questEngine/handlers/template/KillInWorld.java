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

import gnu.trove.list.array.TIntArrayList;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Standard xml-based handling for the DAILY quests with onKillInZone events
 * 
 * @author vlog
 */
public class KillInWorld extends QuestHandler {

	private final int questId;
	private final int endNpc;
	private final int endNpc2;
	private final int startNpc;
	private final int startNpc2;
	private final TIntArrayList worldIds;
	private final int killAmount;

	public KillInWorld(int questId, int endNpc, int endNpc2, int startNpc, int startNpc2, TIntArrayList worldIds, int killAmount) {
		super(questId);
		this.endNpc = endNpc;
		if (endNpc2 != 0) {
			this.endNpc2 = endNpc2;
		}
		else {
			if (startNpc2 != 0) {
				this.endNpc2 = startNpc2;
			}
			else {
				this.endNpc2 = endNpc;
			}
		}
		this.startNpc = startNpc;
		if (startNpc2 != 0) {
			this.startNpc2 = startNpc2;
		}
		else {
			this.startNpc2 = this.startNpc;
		}
		this.questId = questId;
		this.worldIds = worldIds;
		this.killAmount = killAmount;
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
		for (int i = 0; i < worldIds.size(); i++) {
			qe.registerOnKillInWorld(worldIds.get(i), questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		QuestDialog dialog = env.getDialog();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpc == 0 || targetId == startNpc || targetId == startNpc2) {
				switch (dialog) {
					case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					}
					case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == endNpc || targetId == endNpc2) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillInWorldEvent(QuestEnv env) {
		return defaultOnKillRankedEvent(env, 0, killAmount, true); // reward
	}
}
