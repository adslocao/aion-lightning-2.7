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

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author Mr. Poke
 * @modified Hellboy, Gigi
 */
public class _2002WheresRae extends QuestHandler {

	private int transformId = 210273;
	private final static int questId = 2002;
	private final static int[] npc_ids = { 203519, 203534, 203553, 700045, 203516, 203538, 205020 };

	public _2002WheresRae() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(210377).addOnKillEvent(questId);
		qe.registerQuestNpc(210378).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		QuestDialog dialog = env.getDialog();
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203519: {
					switch (dialog) {
						case START_DIALOG:
							if (var == 0)
								return sendQuestDialog(env, 1011);
						case STEP_TO_1:
							if (var == 0) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
				}
				case 203534: {
					switch (dialog) {
						case START_DIALOG:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case SELECT_ACTION_1353:
							playQuestMovie(env, 52);
							break;
						case STEP_TO_2:
							if (var == 1) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
				}
				case 790002: {
					switch (dialog) {
						case START_DIALOG:
							if (var == 2)
								return sendQuestDialog(env, 1693);
							else if (var == 10)
								return sendQuestDialog(env, 2034);
							else if (var == 11)
								return sendQuestDialog(env, 2375);
							else if (var == 12)
								return sendQuestDialog(env, 2462);
							else if (var == 13)
								return sendQuestDialog(env, 2716);
						case STEP_TO_3:
						case STEP_TO_4:
						case STEP_TO_6:
							if (var == 2 || var == 10) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
							else if (var == 13) {
								qs.setQuestVarById(0, 14);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
							break;
						case STEP_TO_5:
							if (var == 12 || var == 99) {
								qs.setQuestVar(99);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
								WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320010000);
								InstanceService.registerPlayerWithInstance(newInstance, player);
								TeleportService.teleportTo(player, 320010000, newInstance.getInstanceId(), 457.65f, 426.8f, 230.4f, 3000, true);
								return closeDialogWindow(env);
							}
						case CHECK_COLLECTED_ITEMS:
							if (var == 11) {
								if (QuestService.collectItemCheck(env, true)) {
									qs.setQuestVarById(0, 12);
									updateQuestStatus(env);
									return sendQuestDialog(env, 2461);
								}
								else
									return sendQuestDialog(env, 2376);
							}
					}
				}
					break;
				case 205020: // Hagen
					switch (dialog) {
						case START_DIALOG:
							if (qs.getQuestVars().getQuestVars() == 99) {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(3001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 3001, 0));
								ThreadPoolManager.getInstance().schedule(new Runnable(){
									@Override
									public void run()
									{
										qs.setQuestVar(13);
										updateQuestStatus(env);
										TeleportService.teleportTo(player, 220010000, 940.15f, 2295.64f, 265.7f, (byte) 43, 0);
									}
								}, 40000);
							}
							return true;
						default:
							return false;
					}
				case 700045:
					if (var == 11 && dialog == QuestDialog.USE_OBJECT) {
						SkillEngine.getInstance().getSkill(player, 8343, 1, player).useSkill();
						player.setTransformedModelId(transformId);
						PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, true));
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
						    public void run() {
						    player.getEffectController().unsetAbnormal(8388608);
						    player.setTransformedModelId(0);
						    PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, true));
						    }
					    }, 5000);
						return true;
					}
				case 203538:
					if (var == 14 && dialog == QuestDialog.USE_OBJECT) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						Npc npc = (Npc) env.getVisibleObject();
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 203553, npc.getX(), npc.getY(),
							npc.getZ(), npc.getHeading());
						npc.getController().onDie(null); // TODO check null or player
						playQuestMovie(env, 256);
						return true;
					}
					break;
				case 203553:
					switch (dialog) {
						case START_DIALOG:
							if (var == 15)
								return sendQuestDialog(env, 3057);
						case STEP_TO_7:
							if (var == 15) {
								env.getVisibleObject().getController().delete();
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203516) {
				if (dialog == QuestDialog.USE_OBJECT || dialog == QuestDialog.START_DIALOG)
					return sendQuestDialog(env, 3398);
				else if (dialog == QuestDialog.STEP_TO_8)
					return sendQuestDialog(env, 5);
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
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() != QuestStatus.START)
			return false;
		switch (targetId) {
			case 210377:
			case 210378:
				if (var >= 3 && var < 10) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
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
		return defaultOnLvlUpEvent(env, 2100, true);
	}
}
