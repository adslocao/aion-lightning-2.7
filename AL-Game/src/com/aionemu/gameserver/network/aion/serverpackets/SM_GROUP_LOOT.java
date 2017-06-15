/**
 * This file is part of aion-unique <aion-unique.com>.
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

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Rhys2002
 */
public class SM_GROUP_LOOT extends AionServerPacket {

	private int groupId;
	private int index;
	private int unk2;
	private int itemId;
	private int unk3;
	private int lootCorpseId;
	private int distributionId;
	private int playerId;
	private long unk4;

	/**
	 * @param Player
	 *          Id must be 0 to start the Roll Options
	 */
	public SM_GROUP_LOOT(int groupId, int playerId, int itemId, int lootCorpseId, int distributionId, long unk4, int index) {
		this.groupId = groupId;
		this.index = index;
		this.unk2 = 1;
		this.itemId = itemId;
		this.unk3 = 0;
		this.lootCorpseId = lootCorpseId;
		this.distributionId = distributionId;
		this.playerId = playerId;
		this.unk4 = unk4;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(groupId);
		writeD(index);
		writeD(unk2);
		writeD(itemId);
		writeC(unk3);
		writeD(lootCorpseId);
		writeC(distributionId);
		writeD(playerId);
		writeQ(unk4); // unk
	}
}
