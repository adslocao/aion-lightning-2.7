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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Nephis
 * @reworked & modified Gigi
 */
public class _10022SupportTheInggisonOutpost extends QuestHandler {

	private final static int questId = 10022;
	private final static int[] npc_ids = { 798932, 798996, 203786, 204656, 798176, 798926, 700601 };

	public _10022SupportTheInggisonOutpost() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(215622).addOnKillEvent(questId);
		qe.registerQuestNpc(216784).addOnKillEvent(questId);
		qe.registerQuestNpc(215633).addOnKillEvent(questId);
		qe.registerQuestNpc(216731).addOnKillEvent(questId);
		qe.registerQuestNpc(215634).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10020, true);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int var3 = qs.getQuestVarById(3);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798926) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 798932) {
			switch (env.getDialog()) {
				case START_DIALOG:
					if (var == 0)
						return sendQuestDialog(env, 1011);
					else if (var == 11)
						return sendQuestDialog(env, 1608);
				case STEP_TO_1:
					return defaultCloseDialog(env, 0, 1); // 1
				case SET_REWARD:
					return defaultCloseDialog(env, 11, 11, true, false); // reward
			}
		}
		else if (targetId == 798996) {
			switch (env.getDialog()) {
				case START_DIALOG:
					if (var == 1)
						return sendQuestDialog(env, 1352);
					else if (qs.getQuestVarById(1) == 20 || qs.getQuestVarById(2) == 20 || qs.getQuestVarById(0) == 3)
						return sendQuestDialog(env, 2034);
					else if (var == 10)
						return sendQuestDialog(env, 4080);
				case STEP_TO_2:
					return defaultCloseDialog(env, 1, 2); // 2
				case STEP_TO_4:
					if (qs.getQuestVarById(1) == 20 || qs.getQuestVarById(2) == 20 || qs.getQuestVarById(0) == 3) {
						qs.setQuestVar(4);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					break;
				case STEP_TO_10:
					if (var == 10)
						return defaultCloseDialog(env, 10, 11); // 11
			}
		}
		else if (targetId == 203786) {
			switch (env.getDialog()) {
				case START_DIALOG:
					if (var == 4)
						return sendQuestDialog(env, 2375);
					else if (var == 7)
						return sendQuestDialog(env, 3398);
					else if (var == 8)
						return sendQuestDialog(env, 3739);
				case CHECK_COLLECTED_ITEMS:
					if (QuestService.collectItemCheck(env, true)) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					else
						return sendQuestDialog(env, 10001);
				case STEP_TO_8:
					if (var == 7)
						return defaultCloseDialog(env, 7, 8); // 8
					break;
				case STEP_TO_9:
					if (var == 8)
						return defaultCloseDialog(env, 8, 9); // 9
			}
		}
		else if (targetId == 204656) {
			switch (env.getDialog()) {
				case START_DIALOG:
					if (var == 5)
						return sendQuestDialog(env, 2716);
				case STEP_TO_6:
					if (var == 5)
						return defaultCloseDialog(env, 5, 6); // 6
			}
		}
		else if (targetId == 798176) {
			switch (env.getDialog()) {
				case START_DIALOG:
					if (var == 6)
						return sendQuestDialog(env, 3057);
				case STEP_TO_7:
					if (var == 6)
						return defaultCloseDialog(env, 6, 7); // 7
			}
		}
		else if (targetId == 700601) {
			if (var == 9 && env.getDialog() == QuestDialog.USE_OBJECT) {
				if (var3 < 9)
					return useQuestObject(env, var3, var3 + 1, false, 3, true); // 3: 1-9
				else if (var3 == 9) {
					useQuestObject(env, var3, var3, false, 3, true);
					qs.setQuestVar(10);
					updateQuestStatus(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId) {
			case 215622:
			case 216784:
				if (qs.getQuestVarById(1) < 20 && qs.getQuestVarById(0) == 2) {
					qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
					updateQuestStatus(env);
					return true;
				}

				else if (qs.getQuestVarById(1) == 19 && qs.getQuestVarById(2) == 20 && qs.getQuestVarById(0) == 2) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return true;
				}
				break;
			case 215633:
			case 216731:
			case 215634:
				if (qs.getQuestVarById(2) < 20 && qs.getQuestVarById(0) == 2) {
					qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
					updateQuestStatus(env);
					return true;
				}
				else if (qs.getQuestVarById(1) == 20 && qs.getQuestVarById(2) == 19 && qs.getQuestVarById(0) == 2) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}
}
