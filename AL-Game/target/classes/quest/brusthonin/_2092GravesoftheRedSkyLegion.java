/**
 * This file is part of Aion Core <aioncore.com>
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package quest.brusthonin;

import java.util.Collections;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;


/**
 *
 * Fixed for AXE by D3x
 * Adapted by Aion Phenix for Lightning 2.7
 * Improved by Metos
 *
 */
public class _2092GravesoftheRedSkyLegion extends QuestHandler {
	private VisibleObject spawn_Surdi;
	private VisibleObject spawn_Angeiya;
	private VisibleObject spawn_Erna;
	private VisibleObject spawn_Genta;
	private VisibleObject spawn_Sith;
	private VisibleObject spawn_Barache;
	private VisibleObject spawn_Bert;

    private final static int questId = 2092;
    private final static int[] npc_ids = {205150, 205188, 700394, 205190, 730156, 730158, 730159, 730160, 730161, 730162, 730163, 205208, 205209, 205210, 205211, 205212, 205213, 205214};

    public _2092GravesoftheRedSkyLegion() {
	
        super(questId);
		
		spawn_Surdi = null;
		spawn_Angeiya = null;
		spawn_Erna = null;
		spawn_Genta = null;
		spawn_Sith = null;
		spawn_Barache = null;
		spawn_Bert = null;
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(214402).addOnKillEvent(questId);
        qe.registerQuestNpc(214403).addOnKillEvent(questId);
        for (int npc_id : npc_ids)
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2091, true);
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
			case 214402:
			case 214403:
				if (var >= 6 && var < 20) {
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					return true;
				}
				else if (var == 20) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}

    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final Npc npc = (Npc) env.getVisibleObject();
		final int targetObjectId = env.getVisibleObject().getObjectId();
		
        if (qs == null)
            return false;

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205150) {
                if (env.getDialogId() == -1)
                    return sendQuestDialog(env, 10002);
                else return sendQuestEndDialog(env);
            }
            return false;
        } else if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        if (targetId == 205150) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 0)
                        return sendQuestDialog(env, 1011);
                case 1012:
                    PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 395));
                    break;
                case 10000:
                    if (var == 0) {
                        qs.setQuestVarById(0, var + 1);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
            }
        } else if (targetId == 205188) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 2)
                        return sendQuestDialog(env, 1693);
                case 10002:
                    if (var == 2) {
                        qs.setQuestVarById(0, var + 1);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
            }
        } else if (targetId == 205190) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 3)
                        return sendQuestDialog(env, 2034);
                    if (var == 4)
                        return sendQuestDialog(env, 2375);
                case 10003:
                    if (var == 3) {
                        qs.setQuestVarById(0, var + 1);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                case 10004:
                    if (var == 4) {
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                case 34:
                    if (var == 4) {
                        if (QuestService.collectItemCheck(env, true)) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            ItemService.addQuestItems(player, Collections.singletonList(new QuestItems(182209009, 1)));
                            return sendQuestDialog(env, 10000);
                        } else
                            return sendQuestDialog(env, 10001);
                    }
            }
		} else if (targetId == 730156) { // Sudri's Tombstone //ID 1
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 0) && (spawn_Surdi == null || !spawn_Surdi.isSpawned())) {
					spawn_Surdi = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205208, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Sudri's Ghost
				}
				return true;
			}						
        } else if (targetId == 730158) { // Angeiya's Tombstone
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 1) && (spawn_Angeiya == null || !spawn_Angeiya.isSpawned())) {
					spawn_Angeiya = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205209, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Angeiya's Ghost
				}
				return true;
			}						
        } else if (targetId == 730159) { // Erna's Tombstone
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 2) && (spawn_Erna == null || !spawn_Erna.isSpawned())) {
					spawn_Erna = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205210, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Erna's Ghost
				}
				return true;
			}						
        } else if (targetId == 730160) { // Genta's Tombstone
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 3) && (spawn_Genta == null || !spawn_Genta.isSpawned())) {
					spawn_Genta = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205211, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Genta's Ghost
				}
				return true;
			}						
        } else if (targetId == 730161) { // Sith's Tombstone
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 4) && (spawn_Sith == null || !spawn_Sith.isSpawned())) {
					spawn_Sith = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205212, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Sith's Ghost
				}
				return true;
			}						
        } else if (targetId == 730162) { // Barache's Tombstone
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 5) && (spawn_Barache == null || !spawn_Barache.isSpawned())) {
					spawn_Barache = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205213, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Barache's Ghost
				}
				return true;
			}						
        } else if (targetId == 730163) { // Bert's Tombstone
			if (var == 5) {
				if (!isAlreadyTalk(qs.getQuestVar2(), 6) && (spawn_Bert == null || !spawn_Bert.isSpawned())) {
					spawn_Bert = QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 
					205214, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); // Bert's Ghost
				}
				return true;
			}						
        } else if (targetId == 205208) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 2717);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 0);
						return true;
                    }
            }
        } else if (targetId == 205209) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 2802);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 1);
						return true;
                    }
            }
        } else if (targetId == 205210) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 2887);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 2);
                        return true;
                    }
            }
        } else if (targetId == 205211) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 2887);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 3);
                        return true;
                    }
            }
        } else if (targetId == 205212) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 2972);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 4);
                        return true;
                    }
            }
        } else if (targetId == 205213) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 3058);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 5);
                        return true;
                    }
            }
        } else if (targetId == 205214) {
            switch (env.getDialogId()) {
                case 26:
                    if (var == 5)
                        return sendQuestDialog(env, 3143);
                case 10005:
                    if (var == 5) {
                    	talkToGhost(qs, env, npc, player, 6);
                        return true;
                    }
            }
        } else if (targetId == 700394) {
            switch (env.getDialogId()) {
                case -1:
                    if (var == 1) {
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                Npc npc = (Npc) player.getTarget();
                                if (npc == null || npc.getObjectId() != targetObjectId)
                                    return;
                                PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
                                PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);

                                qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                                updateQuestStatus(env);
                            }

                        }, 3000);
                        return false;
                    }
            }
        }
        return false;
    }
    
    private boolean isAlreadyTalk(int qv2, int pnjId) {
    	boolean [] ps2 = new boolean[7];
    	
    	for (int i = 6; i >= 0; i--)
    		ps2[i] = qv2 >= Math.pow(2, i);
    	
    	return ps2[pnjId];
    }
    
    private void talkToGhost(QuestState qs, QuestEnv env, Npc npc, Player player, int pnjId) {
		int var = qs.getQuestVarById(0);
    	if (!isAlreadyTalk(qs.getQuestVar2(), pnjId) && var == 5){
    		qs.setQuestVar2((int)(qs.getQuestVar2() + Math.pow(2, pnjId)));
			qs.setQuestVarById(0, var);
		}
    	
    	// if (qs.getQuestVar2() == 0 || qs.getQuestVar2() == 1 || qs.getQuestVar2() == 2 || qs.getQuestVar2() == 4 || 
		// qs.getQuestVar2() == 8 || qs.getQuestVar2() == 16 || qs.getQuestVar2() == 32 || qs.getQuestVar2() == 64 ) {
			// ;
		// } else {
    		qs.setQuestVarById(0, var + 1);
    		updateQuestStatus(env);
    		removeQuestItem(env, 182209009, 1);
    	// }
    	
    	if (isAlreadyTalk(qs.getQuestVar2(), pnjId)) {
    		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
    		npc.getController().onDelete();
    	}
    	/*
        qs.setQuestVarById(0, var + 1);
        updateQuestStatus(env);
		removeQuestItem(env, 182209009, 1);
        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
		npc.getController().onDelete();
		*/
    }
}