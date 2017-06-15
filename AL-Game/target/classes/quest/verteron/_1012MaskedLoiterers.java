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
package quest.verteron;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Talk with Spiros (203111). Scout around Verteron Citadel (210030000) for suspicious strangers. Scouting completed!
 * Report back to Spiros. Collect the Revolutionary Symbol (182200010) (5) and take them to Spiros.
 * 
 * @author MrPoke, Dune11
 * @reworked vlog
 */
public class _1012MaskedLoiterers extends QuestHandler {

	private final static int questId = 1012;

	public _1012MaskedLoiterers() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203111).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.VERTERON_SWAMP_210030000, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1130, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203111) // Spiros
			{
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
						if (var == 2)
							return sendQuestDialog(env, 1352);
						if (var == 3)
							return sendQuestDialog(env, 1693);
					case STEP_TO_1:
						return defaultCloseDialog(env, 0, 1); // 1
					case STEP_TO_2:
						return defaultCloseDialog(env, 2, 3); // 3
					case CHECK_COLLECTED_ITEMS:
						return checkQuestItems(env, 3, 3, true, 5, 2034);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203111)
				return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName == ZoneName.VERTERON_SWAMP_210030000) {
			final Player player = env.getPlayer();
			if (player == null)
				return false;
			final QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs == null)
				return false;

			if (qs.getQuestVars().getQuestVars() == 1) {
				qs.setQuestVarById(0, 2); // 2
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}
