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
package quest.sanctum;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/**
 * @author Gigi, kale
 */
public class _1947ALuckyDay extends QuestHandler {

	private final static int questId = 1947;

	public _1947ALuckyDay() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203795).addOnQuestStart(questId);
		qe.registerQuestNpc(203795).addOnTalkEvent(questId);// Demodocos
		qe.registerQuestNpc(798012).addOnTalkEvent(questId);// Yiehmonerk
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE)// TODO: If cube size is bigger than 5
		{
			switch (env.getDialog()) {
				case START_DIALOG:
					return sendQuestDialog(env, 1011);
				case ASK_ACCEPTION:
					return sendQuestDialog(env, 4);
				case ACCEPT_QUEST:
					env.setQuestId(questId);
					QuestService.startQuest(env);
					return sendQuestDialog(env, 1003);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798012:// Yiehmonerk
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 2375);
						case SELECT_REWARD:
							if (var == 0) {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD && targetId == 798012)
			return sendQuestEndDialog(env);
		return false;
	}
}
