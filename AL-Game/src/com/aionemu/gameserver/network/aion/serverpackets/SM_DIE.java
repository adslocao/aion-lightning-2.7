/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
 * @author orz
 * @author Sarynth thx Rhys2002 for packets. :)
 */
public class SM_DIE extends AionServerPacket {

	private boolean hasRebirth;
	private boolean hasItem;
	private int remainingKiskTime;
	private int type = 0;

	public SM_DIE(boolean hasRebirth, boolean hasItem, int remainingKiskTime, int type) {
		this.hasRebirth = hasRebirth;
		this.hasItem = hasItem;
		this.remainingKiskTime = remainingKiskTime;
		this.type = type;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC((hasRebirth ? 1 : 0)); // skillRevive
		writeC((hasItem ? 1 : 0)); // itemRevive
		writeD(remainingKiskTime);
		writeC(type);
	}
}
