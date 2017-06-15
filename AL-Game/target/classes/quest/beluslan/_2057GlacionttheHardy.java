/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.beluslan;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * Talk with Chieftain Akagitan (204787). Talk with Delris (204784). Find Glaciont the Hardy (213730). Kill all the Ice
 * Petrahulks: Glaciont the Hardy (213730) (1), Frostfist (213788) (1), Iceback (213789) (1), Chillblow (213790) (1),
 * Snowfury (213791) (1). Talk with Chieftain Akagitan.
 * 
 * @author VladimirZ
 * @reworked vlog
 */
public class _2057GlacionttheHardy extends QuestHandler {

	private final static int questId = 2057;
	private final static int[] npc_ids = { 204787, 204784 };
	private final static int[] mob_ids = { 213730, 213788, 213789, 213790, 213791 };

	public _2057GlacionttheHardy() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204316, questId); // Fire Bomb
		for (int mob_id : mob_ids)
			qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 2056);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 2500, 2056 };
		return defaultOnLvlUpEvent(env, quests, true);
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

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204787) { // Chieftain Akagitan
				if (env.getDialog() == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == 1009)
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204787) { // Chieftain Akagitan
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012: {
						playQuestMovie(env, 246);
						return sendQuestDialog(env, 1012);
					}
					case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
				}
			}
			else if (targetId == 204784) { // Delris
				switch (env.getDialog()) {
					case START_DIALOG:
						if (var == 1)
							return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1354: {
						playQuestMovie(env, 247);
						return sendQuestDialog(env, 1354);
					}
					case STEP_TO_2: {
						return defaultCloseDialog(env, 1, 2, 182204316, 1, 0, 0); // 2
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int targetId = env.getTargetId();
			if ((targetId == 213730 || targetId == 213788 ||
					targetId == 213789 || targetId == 213790 ||
					targetId == 213791) && var == 3 && qs.getStatus() == QuestStatus.START) {
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				int var5 = qs.getQuestVarById(5);
				boolean killed = false;

				if (targetId == 213730 && var1 == 0)//Glaciont the Hardy
				{
					qs.setQuestVarById(1, 1);
					updateQuestStatus(env);
					killed = true;
				} else if (targetId == 213788 && var2 == 0)//Frostfist
				{
					qs.setQuestVarById(2, 1);
					updateQuestStatus(env);
					killed = true;
				} else if (targetId == 213789 && var3 == 0)//Iceback
				{
					qs.setQuestVarById(3, 1);
					updateQuestStatus(env);
					killed = true;
				} else if (targetId == 213790 && var4 == 0)//Chillblow
				{
					qs.setQuestVarById(4, 1);
					updateQuestStatus(env);
					killed = true;
				} else if (targetId == 213791 && var5 == 0)//Snowfury
				{
					qs.setQuestVarById(5, 1);
					updateQuestStatus(env);
					killed = true;
				}

				if (killed && (var1 + var2 + var3 + var4 + var5) == 4) {
					qs.setQuestVarById(0, 1090785347);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.DF3_ITEMUSEAREA_Q2057)) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false, 248)); // 3
		}
		return HandlerResult.FAILED;
	}
}
