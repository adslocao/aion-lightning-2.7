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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Nephis
 * @reworked & modified Gigi, vlog
 */
public class _10023SullasStartlingDiscovery extends QuestHandler {

	private final static int questId = 10023;

	public _10023SullasStartlingDiscovery() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 798928, 798975, 798981, 730226, 730227, 730228, 798513, 798225, 798979, 798990, 730295, 700604,
			730229 };
		qe.registerOnEnterWorld(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(216531).addOnKillEvent(questId);
		qe.registerQuestItem(182206614, questId);
		qe.addHandlerSideQuestDrop(questId, 730229, 182206614, 1, 100, false);
		qe.registerGetingItem(182206614, questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 12) {
				if (player.getWorldId() == 300160000) {
					qs.setQuestVar(13);
					updateQuestStatus(env);
					return true;
				}
				else {
					if (player.getInventory().getItemCountByItemId(182206613) == 0) {
						return giveQuestItem(env, 182206613, 1);
					}
				}
			}
			else if (var >= 13 && var < 16) {
				if (player.getWorldId() != 300160000) {
					changeQuestStep(env, var, 12, false);
					if (player.getInventory().getItemCountByItemId(182206613) == 0) {
						giveQuestItem(env, 182206613, 1);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		QuestDialog dialog = env.getDialog();
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798928: { // Yulia
					switch (dialog) {
						case START_DIALOG: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 798975: { // Sulla
					switch (dialog) {
						case START_DIALOG: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 6) {
								return sendQuestDialog(env, 3057);
							}
							else if (var == 9) {
								return sendQuestDialog(env, 4080);
							}
						}
						case STEP_TO_2: {
							return defaultCloseDialog(env, 1, 2); // 2
						}
						case STEP_TO_7: {
							return defaultCloseDialog(env, 6, 7); // 7
						}
						case STEP_TO_10: {
							return defaultCloseDialog(env, 9, 10); // 10
						}
					}
					break;
				}
				case 798981: { // Philon
					switch (dialog) {
						case START_DIALOG: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case STEP_TO_3: {
							return defaultCloseDialog(env, 2, 3); // 3
						}
					}
					break;
				}
				case 798513: { // Machiah
					switch (dialog) {
						case START_DIALOG: {
							if (var == 7) {
								return sendQuestDialog(env, 3398);
							}
						}
						case STEP_TO_8: {
							return defaultCloseDialog(env, 7, 8); // 8
						}
					}
					break;
				}
				case 798225: { // Pyrrha
					switch (dialog) {
						case START_DIALOG: {
							if (var == 8) {
								return sendQuestDialog(env, 3739);
							}
						}
						case STEP_TO_9: {
							return defaultCloseDialog(env, 8, 9); // 9
						}
					}
					break;
				}
				case 798979: { // Gelon
					switch (dialog) {
						case START_DIALOG: {
							if (var == 10) {
								return sendQuestDialog(env, 1608);
							}
						}
						case STEP_TO_11: {
							return defaultCloseDialog(env, 10, 11); // 11
						}
					}
					break;
				}
				case 798990: { // Titus
					switch (dialog) {
						case START_DIALOG: {
							if (var == 11) {
								return sendQuestDialog(env, 1949);
							}
						}
						case STEP_TO_12: {
							return defaultCloseDialog(env, 11, 12, 182206613, 1, 0, 0); // 12
						}
					}
					break;
				}
				case 730295: { // Drakan Stone Statue
					switch (dialog) {
						case START_DIALOG: {
							if (var == 12) {
								return sendQuestDialog(env, 3995);
							}
						}
						case STEP_TO_13: {
							if (var == 12) {
								if (player.getInventory().getItemCountByItemId(182206613) > 0) {
									removeQuestItem(env, 182206613, 1);
									WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300160000);
									InstanceService.registerPlayerWithInstance(newInstance, player);
									TeleportService.teleportTo(player, 300160000, newInstance.getInstanceId(), 744.761f, 940.3738f,
										149.28333f, (byte) 91, 3000, true);
									return true;
								}
								else
									return sendQuestDialog(env, 10001);
							}
						}
					}
					break;
				}
				case 730229: { // Traveller's Bag
					if (dialog == QuestDialog.USE_OBJECT) {
						if (var == 15) {
							return true; // loot
						}
					}
					break;
				}
				case 730226: { // Western Petrified Mass
					if (var == 3 && dialog == QuestDialog.USE_OBJECT) {
						return useQuestObject(env, 3, 4, false, 0); // 4
					}
					break;
				}
				case 730227: { // Eastern Petrified Mass
					if (var == 4 && dialog == QuestDialog.USE_OBJECT) {
						return useQuestObject(env, 4, 5, false, 0); // 5
					}
					break;
				}
				case 730228: { // Southern Petrified Mass
					if (var == 5 && dialog == QuestDialog.USE_OBJECT) {
						return useQuestObject(env, 5, 6, false, 0); // 6
					}
					break;
				}
				case 700604: { // Hidden Switch
					if (var == 13 && dialog == QuestDialog.USE_OBJECT) {
						return useQuestObject(env, 13, 14, false, 0); // 14
					}
					break;
				}
				case 700603: { // Hidden Library Exit
					if (var == 16 && dialog == QuestDialog.USE_OBJECT) {
						TeleportService.teleportTo(player, 210050000, 349.332f, 1368.0781f, 336.43332f, 100);
						return true;
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798928) { // Yulia
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onGetItemEvent(QuestEnv env) {
		return defaultOnGetItemEvent(env, 15, 16, false); // 16
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		return  HandlerResult.fromBoolean(useQuestItem(env, item, 16, 16, true)); // reward
	}

	@Override
	public boolean onKillEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final int instanceId = player.getInstanceId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		final Npc npc = (Npc) env.getVisibleObject();

		switch (env.getTargetId()) {
			case 216531:
				if (qs.getQuestVarById(0) == 14 || qs.getQuestVarById(0) == 15) {
					QuestService.addNewSpawn(300160000, instanceId, 730229, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
					qs.setQuestVarById(0, 15);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1094, 10020 };
		return defaultOnLvlUpEvent(env, quests, true);
	}
}
