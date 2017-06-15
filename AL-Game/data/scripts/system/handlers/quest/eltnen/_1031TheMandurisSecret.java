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
package quest.eltnen;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * Talk with Aurelius (203902). Hunt Manduri (6): 210771, 210758, 210763, 210764, 210759, 210770 Report to Aurelius.
 * Talk with Archelaos (203936). Find Paper Glider (700179). Find Melginie (204043). Escort Melginie to Celestine
 * (204030). Talk with Celestine. Report to Aurelius.
 * 
 * @author Xitanium
 * @reworked vlog
 */
public class _1031TheMandurisSecret extends QuestHandler {

	private final static int questId = 1031;
	private final static int[] mob_ids = { 210771, 210758, 210763, 210764, 210759, 210770 };
	private final static int[] npc_ids = { 203902, 203936, 700179, 204043, 204030 };

	public _1031TheMandurisSecret() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc : npc_ids)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		for (int mob_id : mob_ids)
			qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
		qe.registerQuestNpc(204043).addOnLostTargetEvent(questId);
		qe.registerQuestNpc(204043).addOnReachTargetEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1300, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203902: // Aurelius
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							if (var == 7)
								return sendQuestDialog(env, 1352);
						case STEP_TO_1:
							return defaultCloseDialog(env, 0, 1); // 1
						case STEP_TO_2:
							return defaultCloseDialog(env, 7, 8); // 8
					}
					break;
				case 203936: // Archelaos
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 8)
								return sendQuestDialog(env, 1693);
						case STEP_TO_3:
							return defaultCloseDialog(env, 8, 9); // 9
					}
					break;
				case 700179: // Paper Glider
					if (var == 9) {
						switch (env.getDialog()) {
							case USE_OBJECT:
								return sendQuestDialog(env, 2034);
							case STEP_TO_4: {
								changeQuestStep(env, 9, 10, false); // 10
								return sendQuestDialog(env, 0);
							}
						}
					}
					break;
				case 204043: // Melginie
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 10)
								return sendQuestDialog(env, 2375);
						case STEP_TO_5:
							return defaultStartFollowEvent(env, 204030, 10, 11); // 11
					}
					break;
				case 204030: // Celestine
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 12)
								return sendQuestDialog(env, 3057);
						case STEP_TO_7:
							return defaultCloseDialog(env, 12, 12, true, false); // reward
					}

			}
		}
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203902) // Aurelius
			{
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 3398);
				else
					return sendQuestEndDialog(env);
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

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId) {
			case 210771:
			case 210758:
			case 210763:
			case 210764:
			case 210759:
			case 210770:
				if (var >= 1 && var <= 6) {
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 11, 12, false); // 12
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 11, 10, false); // 10
	}
}
