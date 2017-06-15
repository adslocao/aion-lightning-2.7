/*
 * This file is part of Aion Lightning <aion-lightning.org>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Gigi
 */
public class _20022SpreadingAsmodaesReach extends QuestHandler {

	private final static int questId = 20022;
	private final static int[] npcs = { 799226, 799282, 700704, 700703, 700701, 700702 };

	public _20022SpreadingAsmodaesReach() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(216102).addOnKillEvent(questId);
		qe.registerQuestNpc(216103).addOnKillEvent(questId);
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		qe.registerQuestItem(182207609, questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20000, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799226) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case STEP_TO_1:
						return defaultCloseDialog(env, 0, 1); // 1
				}
			}
			else if (targetId == 799282) {
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 1352);
						else if (var == 2) {
							long itemCount1 = player.getInventory().getItemCountByItemId(182207605);
							long itemCount2 = player.getInventory().getItemCountByItemId(182207606);
							long itemCount3 = player.getInventory().getItemCountByItemId(182207607);
							if (itemCount1 > 9 && itemCount2 > 9 && itemCount3 > 9)
								return sendQuestDialog(env, 1693);
							else
								return sendQuestDialog(env, 10001);
						}
						else if (var == 3)
							return sendQuestDialog(env, 10000);
						else if (var == 4 || var == 260)
							return sendQuestDialog(env, 2375);
						else if (var == 7)
							return sendQuestDialog(env, 3398);
						else if (var == 9)
							return sendQuestDialog(env, 4080);
					case STEP_TO_2:
						return defaultCloseDialog(env, 1, 2); // 2
					case CHECK_COLLECTED_ITEMS:
						qs.setQuestVarById(0, 3); // 3
						updateQuestStatus(env);
						removeQuestItem(env, 182207605, 10);
						removeQuestItem(env, 182207606, 10);
						removeQuestItem(env, 182207607, 10);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					case STEP_TO_5:
						if (!player.getInventory().isFull()) {
							return defaultCloseDialog(env, 4, 5, 182207608, 2, 0, 0); // 5
						}
					case STEP_TO_8:
						return defaultCloseDialog(env, 7, 8, 182207609, 1, 0, 0); // 8
					case SET_REWARD:
						return defaultCloseDialog(env, 9, 9, true, false); // reward
				}
			}
			else if (targetId == 700704) {
				if (qs.getQuestVarById(0) == 2 && env.getDialog() == QuestDialog.USE_OBJECT) {
					return true; // loot
				}
			}
			else if (targetId == 700703) {
				if (qs.getQuestVarById(0) == 2 && env.getDialog() == QuestDialog.USE_OBJECT && targetId == 700703) {
					return true; // loot
				}
			}
			else if (targetId == 700701) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return useQuestObject(env, 5, 6, false, 0, 0, 0, 182207608, 1); // 6
				}
			}
			else if (targetId == 700702) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return useQuestObject(env, 6, 7, false, 0, 0, 0, 182207608, 1); // 7
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799226) {
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		int var1 = qs.getQuestVarById(1);
		int var = qs.getQuestVarById(0);

		switch (targetId) {
			case 216102:
			case 216103:
				if (var == 3 && var1 < 4) {
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
				}
				else if (var == 3 && var1 == 4) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
				}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		int id = item.getItemTemplate().getTemplateId();

		if (player.getWorldId() != 220070000)
			return HandlerResult.UNKNOWN;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return HandlerResult.UNKNOWN;
		final int var = qs.getQuestVarById(0);

		if (id == 182207609 && var == 8) {
			if (MathUtil.isInSphere(player, 285.17746f, 1534.7837f, 356.52f, 10)) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 8, 9, false, 553)); // 9
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300426));
				return HandlerResult.SUCCESS;
			}
		}
		return HandlerResult.FAILED;
	}
}
