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
package quest.ishalgen;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Mr. Poke
 */
public class _2007WheresRaeThisTime extends QuestHandler {

	private final static int questId = 2007;

	public _2007WheresRaeThisTime() {
		super(questId);
	}

	@Override
	public void register() {
		int[] talkNpcs = { 203516, 203519, 203539, 203552, 203554, 700085, 700086, 700087 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int id : talkNpcs)
			qe.registerQuestNpc(id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203516:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1011);
						case STEP_TO_1:
							if (var == 0) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								closeDialogWindow(env);
								return true;
							}
					}
					break;
				case 203519:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case STEP_TO_2:
							if (var == 1) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								closeDialogWindow(env);
								return true;
							}
					}
					break;
				case 203539:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 2)
								return sendQuestDialog(env, 1693);
						case SELECT_ACTION_1694:
							playQuestMovie(env, 55);
							break;
						case STEP_TO_3:
							if (var == 2) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								closeDialogWindow(env);
								return true;
							}
					}
					break;
				case 203552:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 3)
								return sendQuestDialog(env, 2034);
						case STEP_TO_4:
							if (var == 3) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								closeDialogWindow(env);
								return true;
							}
					}
					break;
				case 203554:
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 4)
								return sendQuestDialog(env, 2375);
							else if (var == 8)
								return sendQuestDialog(env, 2716);
						case STEP_TO_5:
							if (var == 4) {
								qs.setQuestVar(5);
								updateQuestStatus(env);
								closeDialogWindow(env);
								return true;
							}
							break;
						case STEP_TO_6:
							if (var == 8) {
								qs.setQuestVar(9);
								updateQuestStatus(env);
								qs.setQuestVar(8);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								closeDialogWindow(env);
								return true;
							}
					}
					break;
				case 700085:
					if (var == 5) {
						destroy(6, env);
						return false;
					}
					break;
				case 700086:
					if (var == 6) {
						destroy(7, env);
						return false;
					}
					break;
				case 700087:
					if (var == 7) {
						destroy(-1, env);
						return false;
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203516) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					playQuestMovie(env, 58);
					return sendQuestDialog(env, 3057);
				}
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = { 2006, 2005, 2004, 2003, 2002, 2001 };
		return defaultOnZoneMissionEndEvent(env, quests);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 2100, 2006, 2005, 2004, 2003, 2002, 2001 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	private void destroy(final int var, final QuestEnv env) {
		final int targetObjectId = env.getVisibleObject().getObjectId();

		final Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
		PacketSendUtility
			.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (player.getTarget().getObjectId() != targetObjectId)
					return;
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId),
					true);
				// sendEmotion(env, player, EmotionId.STAND, true);
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				switch (var) {
					case 6:
					case 7:
						qs.setQuestVar(var);
						break;
					case -1:
						playQuestMovie(env, 56);
						qs.setQuestVar(8);
						break;
				}
				updateQuestStatus(env);
			}
		}, 3000);
	}
}
