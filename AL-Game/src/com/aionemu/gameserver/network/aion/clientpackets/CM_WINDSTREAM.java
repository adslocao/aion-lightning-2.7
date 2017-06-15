/**
 * This file is part of aion-lightning <aion-lightning.org>
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WINDSTREAM;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_WINDSTREAM extends AionClientPacket {

	private final Logger log = LoggerFactory.getLogger(CM_WINDSTREAM.class);
	int teleportId;
	int distance;
	int state;

	public CM_WINDSTREAM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		teleportId = readD(); //typical teleport id (ex : 94001 for talloc hallow in inggison)
		distance = readD();	 // 600 for talloc.
		state = readD(); // 0 or 1.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		switch(state)
		{
			case 0:
			case 4:
			case 8:
				//TODO:	Find in which cases second variable is 0 & 1
				//		Jego's example packets had server refuse with 0 and client kept retrying.
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM(state, 1));
				break;
			case 1:
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.WINDSTREAM, teleportId, distance), true);
				player.setEnterWindstream(1);
				player.setInWindstream(true);
				break;
			case 2:
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.WINDSTREAM_END, 0, 0), true);			
				PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM(state,1));
				player.setInWindstream(false);
				break;
			case 7:
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.WINDSTREAM_BOOST, 0, 0), true);
				player.setEnterWindstream(7);
				break;
			default:
				log.error("Unknown Windstream state #" + state + " was found!" );
		}
	}
}
