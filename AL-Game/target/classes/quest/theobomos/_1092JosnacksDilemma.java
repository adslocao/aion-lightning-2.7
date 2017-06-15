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
package quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * Talk with Atropos (798155). Talk with Josnack (798206). Get rid of stones so they can restore the statue: Stone above
 * the Statue (700389) Stone on the Statue Platform (700388) Talk with Atropos. Destroy the Eternal Flames (700390), and
 * collect their Soul Pieces (182208012) (6) from the Burnt Zombies (214552). Take them to Atropos. Receive a reward
 * from Atropos.
 * 
 * @author Dune11
 * @reworked vlog
 */
public class _1092JosnacksDilemma extends QuestHandler {

	private final static int questId = 1092;
	private final static int[] npc_ids = { 798155, 798206, 700389, 700388, 700390 };

	public _1092JosnacksDilemma() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(700390).addOnKillEvent(questId);
		qe.registerOnDie(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1091, true);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798155)
				return sendQuestEndDialog(env);
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798155: // Atropos
					switch (env.getDialog()) {
						case START_DIALOG:
		                    if (var == 0)
		                        return sendQuestDialog(env, 1011);
		                    else if (var == 3)
		                        return sendQuestDialog(env, 2034);
		                    else if (var == 4)
		                        return sendQuestDialog(env, 2375);
		                case STEP_TO_1:
		                    if (var == 0) {
		                        qs.setQuestVarById(0, var + 1);
		                        updateQuestStatus(env);
		                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
		                        return true;
		                    }
		                case STEP_TO_4:
		                    if (var == 3) {
		                        qs.setQuestVarById(0, var + 1);
		                        updateQuestStatus(env);
		                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
		                        return true;
		                    }
		                case CHECK_COLLECTED_ITEMS:
		                    if (var == 4) {
		                        if (QuestService.collectItemCheck(env, true)) {
		                            qs.setStatus(QuestStatus.REWARD);
		                            updateQuestStatus(env);
		                            return sendQuestDialog(env, 5);
		                        } else
		                            return sendQuestDialog(env, 10001);
		                    }
					}
					break;
				case 798206: // Josnack
					switch (env.getDialog()) {
						case START_DIALOG:
							if (var == 1)
								return sendQuestDialog(env, 1352);
							if (var == 2)
								return sendQuestDialog(env, 1693);
						case SELECT_ACTION_1353:
							playQuestMovie(env, 364);
							break;
						case STEP_TO_2:
							if (var == 1) {
								defaultCloseDialog(env, 1, 2); // 2
								TeleportService.teleportTo(player, 210060000, 926, 3035, 186, 30);
								return true;
							}
					}
					break;
				case 700389: // Stone above the Statue
					switch (env.getDialog()) {
						case USE_OBJECT:
							if (var == 2 && qs.getQuestVarById(1) == 0) {
								qs.setQuestVarById(1, 1); // 1: 1
								updateQuestStatus(env);

								if (qs.getQuestVarById(2) == 1) {
									qs.setQuestVar(3); // 3
									updateQuestStatus(env);
								}
								return true;
							}
					}
					break;
				case 700388: // Stone on the Statue Platform
					switch (env.getDialog()) {
						case USE_OBJECT:
							if (var == 2 && qs.getQuestVarById(2) == 0) {
								qs.setQuestVarById(2, 1); // 2: 1
								if (qs.getQuestVarById(1) == 1) {
									qs.setQuestVar(3); // 3
									updateQuestStatus(env);
									return true;
								}
								updateQuestStatus(env);
								return true;
							}
					}
					break;
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		VisibleObject target = env.getVisibleObject();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			Storage bag = player.getInventory();
			if (target instanceof Npc) {
				Npc npc = (Npc) target;
				if(npc.getNpcId() == 700390 && var == 4 && (bag.getItemCountByItemId(182208012) < 6)) {
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 214552, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 214552, npc.getX() - 2, npc.getY() - 2, npc.getZ(), npc.getHeading());
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 214552, npc.getX() + 2, npc.getY(), npc.getZ(), npc.getHeading());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (var == 2) {
				qs.setQuestVarById(0, 1);
				qs.setQuestVarById(1, 0);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}
