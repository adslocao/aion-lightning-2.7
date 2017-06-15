/*
 * This file is part of Quebec force.
 *
 * Quebec force is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Quebec force is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Quebec force.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.cloister_of_kaisinel;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author dta3000
 */
public class _10001BoundOfInggison extends QuestHandler {

	private final static int questId = 10001;

	public _10001BoundOfInggison() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798926).addOnQuestStart(questId); // outremus
		qe.registerQuestNpc(798926).addOnTalkEvent(questId);
		qe.registerQuestNpc(798600).addOnTalkEvent(questId); // eremitia
		qe.registerQuestNpc(798513).addOnTalkEvent(questId); // machiah
		qe.registerQuestNpc(203760).addOnTalkEvent(questId); // bellia
		qe.registerQuestNpc(203782).addOnTalkEvent(questId); // jhaelas
		qe.registerQuestNpc(798408).addOnTalkEvent(questId); // sibylle
		qe.registerQuestNpc(203709).addOnTalkEvent(questId); // Clym�ne
		qe.registerOnEnterWorld(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(501, questId);
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 7) {
				if (player.getWorldId() == 210050000) {
					return playQuestMovie(env, 501);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10000, true);
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 501)
			return false;
		Player player = env.getPlayer();
		if (player.getCommonData().getRace() != Race.ELYOS)
			return false;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		qs.setStatus(QuestStatus.REWARD);
		updateQuestStatus(env);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (targetId == 798926) {
			if (qs == null) {
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
			if (targetId == 798600 && var == 0) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else if (env.getDialog() == QuestDialog.STEP_TO_1) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 798513 && var == 1) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1352);
				else if (env.getDialog() == QuestDialog.STEP_TO_2) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 203760 && var == 2) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1693);
				else if (env.getDialog() == QuestDialog.STEP_TO_3) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 203782 && var == 3) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2034);
				else if (env.getDialog() == QuestDialog.STEP_TO_4) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 798408 && var == 4) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2375);
				else if (env.getDialogId() == 10004) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 203709 && var == 5) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 2716);
				else if (env.getDialogId() == 10005) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 798408 && var == 6) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 3057);
				else if (env.getDialogId() == 10006) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
			else if (targetId == 798408 && var == 7) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 3398);
				else if (env.getDialogId() == 10255) {
					TeleportService.teleportTo(player, 210050000, 1, 1313, 256, 592, (byte) 20, 3000, true);
					return true;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798926) {
				if (env.getDialogId() == -3)
					return sendQuestDialog(env, 3399);
				return sendQuestEndDialog(env);
			}else if(targetId == 798408){
				if(env.getDialogId() == 10009){
					TeleportService.teleportTo(player, 210050000, 1, 1313, 256, 592, (byte) 20, 3000, true);
					return true;
				}else{
					return sendQuestDialog(env, 3399);
				}
			}
		}
		return false;
	}
}