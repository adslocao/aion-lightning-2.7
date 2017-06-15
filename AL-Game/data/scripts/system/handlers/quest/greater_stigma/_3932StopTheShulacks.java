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
package quest.greater_stigma;

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
 * @author kecimis
 */
public class _3932StopTheShulacks extends QuestHandler {

	private final static int questId = 3932;
	private final static int[] npc_ids = { 203711, 204656 };

	/**
	 * 203711 - Miriya 204656 - Maloren
	 */

	public _3932StopTheShulacks() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203711).addOnQuestStart(questId);// Miriya
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203711)// Miriya
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);

			}
			return false;
		}

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203711)// Miriya
			{
				return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {

			if (targetId == 203711 && var == 1)// Miriya
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 2375);
					case CHECK_COLLECTED_ITEMS:
						if (QuestService.collectItemCheck(env, true)) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestDialog(env, 2716);
				}

			}
			else if (targetId == 204656 && var == 0)// Maloren
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						return sendQuestDialog(env, 1352);
					case STEP_TO_1:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return true;
				}
			}

			return false;
		}
		return false;
	}
}
