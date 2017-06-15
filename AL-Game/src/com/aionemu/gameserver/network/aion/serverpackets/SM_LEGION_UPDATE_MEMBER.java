/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Simple
 */
public class SM_LEGION_UPDATE_MEMBER extends AionServerPacket {

	private static final byte OFFLINE = 0x00;
	private static final byte ONLINE = 0x01;
	private Player player;
	private int msgId;
	private String text;
	private byte isOnline;

	public SM_LEGION_UPDATE_MEMBER(Player player, int msgId, String text) {
		this.player = player;
		this.msgId = msgId;
		this.text = text;
		this.isOnline = player.isOnline() ? ONLINE : OFFLINE;
	}
	
	public SM_LEGION_UPDATE_MEMBER(Player player)
	{
		this.player = player;
		this.isOnline = OFFLINE;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getObjectId());
		writeC(player.getLegionMember().getRank().getRankId());
		writeC(player.getCommonData().getPlayerClass().getClassId());
		writeC(player.getLevel());
		writeD(player.getPosition().getMapId());
		writeC(isOnline);
		writeD(player.isOnline()? 0 :player.getLastOnline());
		writeD(msgId);
		writeS(text);
	}
}

// MAP ID: 90 9E 8E 06
// ONLINE: 00
// 00 00 00 00
// D8 74 7C 4B 00 00 4B 00 00

// MAP ID: 90 9E 8E 06
// ONLINE: 01
// 00 00 00 00
// 00 00 00 00

// MAP ID: 90 9E 8E 06
// ONLINE: 01
// 00 00 00 00
// 00 00 00 00
// 00 00

// ONLINE: 01
// UNK: 00 00 00 00
// MEMBER ID: 31 D7 13 00
// MEMBER NAME: 40 00 60 00 60 00 00 00
// but why is it longer?
