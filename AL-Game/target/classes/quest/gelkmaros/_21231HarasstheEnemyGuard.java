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
package quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author AionPhenix 2.7
 */
public class _21231HarasstheEnemyGuard extends QuestHandler {

	private final static int questId = 21231;

	public _21231HarasstheEnemyGuard() {
		super(questId);
	}

	@Override
	public void register() {
		int[] mobs = { 257944, 257968, 258128, 257971, 258130, 257941, 257962, 257965, 257932, 257953, 257959, 257947, 257951, 257969, 257939, 257942, 257930, 257960, 257963, 257966, 257957, 257945 };
		for (int mob : mobs)
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		qe.registerQuestNpc(799363).addOnQuestStart(questId);
		qe.registerQuestNpc(799363).addOnTalkEvent(questId);
		qe.registerQuestNpc(799315).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        int targetId = 0;
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799363) {
                if (env.getDialogId() == 26)
                    return sendQuestDialog(env, 1011);
                else
                    return sendQuestStartDialog(env);
            }
		} else if (qs.getStatus() == QuestStatus.REWARD && targetId == 799315) {
            return sendQuestEndDialog(env);
        }
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int[] mobs = { 257944, 257968, 258128, 257971, 258130, 257941, 257962, 257965, 257932, 257953, 257959, 257947, 257951, 257969, 257939, 257942, 257930, 257960, 257963, 257966, 257957, 257945 };
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int targetId = env.getTargetId();
			int var = qs.getQuestVarById(0);

			switch (targetId) {
				case 257944:
				case 257968:
				case 258128:
				case 257971:
				case 258130:
				case 257941:
				case 257962:
				case 257965:
				case 257932:
				case 257953:
				case 257959:
				case 257947:
				case 257951:
				case 257969:
				case 257939:
				case 257942:
				case 257930:
				case 257960:
				case 257963:
				case 257966:
				case 257957:
				case 257945: {
					if (var >= 0 && var < 19) {
						return defaultOnKillEvent(env, mobs, 0, 19, 0); // 0: 19
					}
					else if (var == 19) {
						qs.setQuestVarById(0, var + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					break;
				}
			}
		}
		return false;
	}
}
