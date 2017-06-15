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
package quest.hidden_truth;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.SystemMessageId;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * Talk with Pernos (790001). Find Fissure of Destiny (700551) that connects to Karamatis (310010000, 52, 174, 229, 0)
 * and talk with Hermione (205119) (spawn). Proceed to Karamatis and defeat Orissan Legionary (50): Legionary (205018,
 * 205019, 205002, 205001, 205004, 205006), Archon legionary (205021, 205022). Defeat Orissan (215400) (spawn 310010000,
 * 182, 294, 296, 90) (1). Activate the Artifact of Memory (700552). Talk with Lephar (205118) (spawn). Report the
 * result to Fasimedes (203700) (110010000, 1867, 2068, 517).
 * 
 * @author vlog TODO: make retail-like
 */
public class _1099AnImportantChoice extends QuestHandler {

	private final static int questId = 1099;
	private final static int[] npcs = { 790001, 700551, 205119, 700552, 205118, 203700 };
	private final static int[] mobs = { 205018, 205019, 205002, 205001, 205004, 205006, 205021, 205022, 215400 };
	private int reward = 0;

	public _1099AnImportantChoice() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc_id : npcs) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
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
			switch (targetId) {
				case 790001: { // Pernos
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							if ((!giveQuestItem(env, 182206066, 1)) || (!giveQuestItem(env, 182206067, 1)))
								return false;
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 700551: { // Fissure of Destiny
					if (env.getDialog() == QuestDialog.USE_OBJECT && var == 1) {
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310010000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						TeleportService.teleportTo(player, 310010000, newInstance.getInstanceId(), 52, 174, 229, 3000, true);
						QuestService.addNewSpawn(310010000, newInstance.getInstanceId(), 205119, 53f, 175f, 229f, (byte) 0);
					}
					break;
				}
				case 205119: { // Hermione
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case STEP_TO_2: {
							QuestService.addNewSpawn(310010000, player.getInstanceId(), 215400, 182f, 294f, 296f, (byte) 90);
							QuestService.addNewSpawn(310010000, player.getInstanceId(), 700552, 183f, 295f, 296f, (byte) 0);
							return defaultCloseDialog(env, 1, 2, 182206058, 1, 182206066, 1); // 2
						}
					}
					break;
				}
				case 700552: { // Artifact of Memory
					if (env.getDialog() == QuestDialog.USE_OBJECT && var == 53) {
						QuestService.addNewSpawn(310010000, player.getInstanceId(), 205118, 180f, 292f, 295f, (byte) 90);
						return useQuestObject(env, 53, 54, false, 0, 0, 0, 182206058, 1, 0, false); // 54
					}
					break;
				}
				case 205118: { // Lephar
					switch (env.getDialog()) {
						case START_DIALOG: {
							if (var == 54) {
								return sendQuestDialog(env, 1352);
							}
						}
						case STEP_TO_6: {
							TeleportService.teleportTo(player, 310010000, 1, 1867f, 2068f, 517f, 3000, true);
							reward = 0;
							return defaultCloseDialog(env, 54, 54, true, false); // reward
						}
						case STEP_TO_7: {
							TeleportService.teleportTo(player, 310010000, 1, 1867f, 2068f, 517f, 3000, true);
							reward = 1;
							return defaultCloseDialog(env, 54, 54, true, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203700) { // Fasimedes
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					if (reward == 0) {
						return sendQuestDialog(env, 3057);
					}
					else if (reward == 1) {
						return sendQuestDialog(env, 3398);
					}
				}
				else {
					return sendQuestEndDialog(env, 5 + reward);
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
			if (var >= 2 && var < 52) {
				int[] npcIds = { 205018, 205019, 205002, 205001, 205004, 205006, 205021, 205022 };
				return defaultOnKillEvent(env, npcIds, 2, 52); // 2 - 52
			}
			else if (var == 52) {
				return defaultOnKillEvent(env, 215400, 52, 53); // 53
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 1) {
				changeQuestStep(env, var, 1, false); // 1
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() != 310010000) {
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var > 1) {
					changeQuestStep(env, var, 1, false); // 1
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1098);
	}
}
