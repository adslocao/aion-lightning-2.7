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

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.models.Monster;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke
 * @reworked vlog
 */
public class MonsterHunt extends QuestHandler {

	private final int questId;
	private final int startNpc;
	private final int startNpc2;
	private final int endNpc;
	private final int endNpc2;
	private final FastMap<Integer, Monster> monsters;

	public MonsterHunt(int questId, int startNpc, int startNpc2, int endNpc, int endNpc2, FastMap<Integer, Monster> monsters) {
		super(questId);
		this.questId = questId;
		this.startNpc = startNpc;
		if (startNpc2 != 0) {
			this.startNpc2 = startNpc2;
		}
		else {
			this.startNpc2 = this.startNpc;
		}
		if (endNpc != 0) {
			this.endNpc = endNpc;
		}
		else {
			this.endNpc = startNpc;
		}
		if (endNpc2 != 0) {
			this.endNpc2 = endNpc2;
		}
		else {
			this.endNpc2 = this.endNpc;
		}
		this.monsters = monsters;
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
		for (int monsterId : monsters.keySet()) {
			qe.registerQuestNpc(monsterId).addOnKillEvent(questId);
		}
		if (endNpc != startNpc) {
			qe.registerQuestNpc(endNpc).addOnTalkEvent(questId);
		}
		if (endNpc2 != startNpc2) {
			qe.registerQuestNpc(endNpc2).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpc == 0 || targetId == startNpc || targetId == startNpc2) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			for (Monster mi : monsters.values()) {
				if (mi.getEndVar() > qs.getQuestVarById(mi.getVar())) {
					return false;
				}
			}
			if (targetId == endNpc || targetId == endNpc2) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1352);
				}
				else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
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

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			Monster m = monsters.get(env.getTargetId());
			if (m != null) {
				if (qs.getQuestVarById(m.getVar()) < m.getEndVar()) {
					qs.setQuestVarById(m.getVar(), qs.getQuestVarById(m.getVar()) + 1);
					
					//if is the last kill
					boolean complet = true;
					for(Monster mob : monsters.values())
						if(qs.getQuestVarById(mob.getVar()) < m.getEndVar()){
							complet = false;
							break;
						}
					
					if(complet){
						if(qs.getQuestVarById(m.getVar()) == m.getEndVar())
							qs.setStatus(QuestStatus.REWARD);
					}
                	updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}
