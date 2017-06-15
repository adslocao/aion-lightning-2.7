/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet is used to teleport player
 * 
 * @author Luno , orz
 */
public class SM_TELEPORT_LOC extends AionServerPacket {

	private int mapId;
	private float x, y, z;
	private byte heading;
        private int type;

	public SM_TELEPORT_LOC(int mapId, float x, float y, float z,byte heading, int type) {
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
                this.type = type;
		this.heading = heading;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(type); // unk
		writeC(0x90); // unk
		writeC(0x9E); // unk
		writeD(mapId); // mapid
		writeF(x); // x
		writeF(y); // y
		writeF(z); // z
		writeC(heading); // headling
	}
}
