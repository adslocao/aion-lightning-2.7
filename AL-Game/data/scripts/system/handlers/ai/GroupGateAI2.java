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
package ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
@AIName("groupgate")
public class GroupGateAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {

		boolean isMember = false;
		int creatorId = getCreatorId();
		if (player.getObjectId().equals(creatorId)) {
			isMember = true;
		}
		else if (player.isInGroup2()) {
			isMember = player.getPlayerGroup2().hasMember(creatorId);
		}

		if (isMember && player.getLevel() >= 10) {

			AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_GROUP_GATE_DO_YOU_ACCEPT_MOVE, 0,
				new AI2Request() {

					@Override
					public void acceptRequest(Creature requester, Player responder) {
						switch (getNpcId()) {
						// Group Gates
							case 749017:
								TeleportService.teleportTo(responder, 110010000, 1, 1444.9f, 1577.2f, 572.9f, 3000, true);
								break;
							case 749083:
								TeleportService.teleportTo(responder, 120010000, 1, 1657.5f, 1398.7f, 194.7f, 3000, true);
								break;
							// Binding Group Gates
							case 749131:
							case 749132:
								TeleportService.moveToBindLocation(responder, true);
								break;
						}
					}
				});

		}
		else {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CAN_NOT_USE_GROUPGATE_NO_RIGHT);
		}
	}

	@Override
	protected void handleDialogFinish(Player player) {
	}
}
