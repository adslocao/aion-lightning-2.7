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


import javolution.util.FastMap;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author MrPoke
 */

public class SM_NEARBY_QUESTS extends AionServerPacket {

	private FastMap<Integer, Integer> questIds;

	public SM_NEARBY_QUESTS(FastMap<Integer, Integer> questIds) {
		this.questIds = questIds;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (questIds == null || con.getActivePlayer() == null)
			return;

		writeC(0);
		writeH(-questIds.size() & 0xFFFF);
		for (FastMap.Entry<Integer, Integer> e = questIds.head(), end = questIds.tail(); (e = e.getNext()) != end;) {
			writeH(e.getKey());
			writeH(e.getValue());
		}
		FastMap.recycle(questIds);
		questIds = null;
	}
}
