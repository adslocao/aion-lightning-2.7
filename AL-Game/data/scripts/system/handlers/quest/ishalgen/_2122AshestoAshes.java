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

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke
 * @modified Hellboy
 */
public class _2122AshestoAshes extends QuestHandler {

	private final static int questId = 2122;

	public _2122AshestoAshes() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203551).addOnTalkEvent(questId);
		qe.registerQuestNpc(700148).addOnTalkEvent(questId);
		qe.registerQuestNpc(730029).addOnTalkEvent(questId);
		qe.registerQuestItem(182203120, questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if (targetId == 0) {
			switch (env.getDialog()) {
				case ACCEPT_QUEST:
					QuestService.startQuest(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
					return true;
				case REFUSE_QUEST:
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
					return true;
			}
		}
		else if (targetId == 203551) {
			if (qs == null)
				return false;
			else if (qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
						break;
					case SELECT_ACTION_1012:
						if (var == 0)
							removeQuestItem(env, 182203120, 1);
						break;
					case STEP_TO_1:
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
				}
			}
			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == QuestDialog.USE_OBJECT)
					return sendQuestDialog(env, 2375);
				else
					return sendQuestEndDialog(env);
			}
		}
		else if (targetId == 700148) {
			if (qs != null && qs.getStatus() == QuestStatus.START)
				return true;
		}
		else if (targetId == 730029) {
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				switch (env.getDialog()) {
					case USE_OBJECT:
						if (player.getInventory().getItemCountByItemId(182203133) < 3) {
							sendQuestDialog(env, 1693);
							return false;
						}
						sendQuestDialog(env, 1352);
						return false;
					case STEP_TO_2:
						removeQuestItem(env, 182203133, 3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
						return true;
				}

			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (id != 182203120)
			return HandlerResult.UNKNOWN;
		PacketSendUtility.broadcastPacket(player,
			new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 20, 1, 0), true);
		if (qs == null || qs.getStatus() == QuestStatus.NONE)
			sendQuestDialog(env, 4);
		return HandlerResult.SUCCESS;
	}
}
