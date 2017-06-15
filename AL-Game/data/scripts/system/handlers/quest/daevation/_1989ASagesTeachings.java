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
package quest.daevation;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Rhys2002
 */
public class _1989ASagesTeachings extends QuestHandler {

	private final static int questId = 1989;

	public _1989ASagesTeachings() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203704).addOnQuestStart(questId);
		qe.registerQuestNpc(203705).addOnQuestStart(questId);
		qe.registerQuestNpc(203706).addOnQuestStart(questId);
		qe.registerQuestNpc(203707).addOnQuestStart(questId);
		qe.registerQuestNpc(203771).addOnQuestStart(questId);
		qe.registerQuestNpc(203771).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203771) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			PlayerClass playerClass = player.getCommonData().getPlayerClass();
			switch (targetId) {
				case 203704:// Boreas
					switch (env.getDialog()) {
						case START_DIALOG:
							if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
								return sendQuestDialog(env, 1352);
							else
								return sendQuestDialog(env, 1438);
						case STEP_TO_1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203705:// Jumentis
					switch (env.getDialog()) {
						case START_DIALOG:
							if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
								return sendQuestDialog(env, 1693);
							else
								return sendQuestDialog(env, 1779);
						case STEP_TO_1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203706:// Charna
					switch (env.getDialog()) {
						case START_DIALOG:
							if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
								return sendQuestDialog(env, 2034);
							else
								return sendQuestDialog(env, 2120);
						case STEP_TO_1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203707:// Thrasymedes
					switch (env.getDialog()) {
						case START_DIALOG:
							if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
								return sendQuestDialog(env, 2375);
							else
								return sendQuestDialog(env, 2461);
						case STEP_TO_1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203771:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 1)
								return sendQuestDialog(env, 2716);
							else if (var == 2)
								return sendQuestDialog(env, 3057);
							else if (var == 3) {
								if (player.getCommonData().getDp() < 4000)
									return sendQuestDialog(env, 3484);
								else
									return sendQuestDialog(env, 3398);
							}
							else if (var == 4) {
								if (player.getCommonData().getDp() < 4000)
									return sendQuestDialog(env, 3825);
								else
									return sendQuestDialog(env, 3739);
							}
						case SELECT_REWARD:
							if (var == 3) {
								playQuestMovie(env, 105);
								player.getCommonData().setDp(0);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
							else if (var == 4) {
								playQuestMovie(env, 105);
								player.getCommonData().setDp(0);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
							else
								return this.sendQuestEndDialog(env);
						case STEP_TO_2:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 3057);
						case STEP_TO_4:
							qs.setQuestVarById(0, 3);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						case STEP_TO_5:
							qs.setQuestVarById(0, 4);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203771)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
