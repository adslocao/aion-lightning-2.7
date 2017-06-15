/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.SiegeService;
import java.util.Map;
import javolution.util.FastMap;

/**
 * @author xTz, Source
 */
public class SM_SHIELD_EFFECT extends AionServerPacket {

	private Map<Integer, SiegeLocation> locations;

	public SM_SHIELD_EFFECT() {
		locations = SiegeService.getInstance().getSiegeLocations();
	}

	public SM_SHIELD_EFFECT(int fortress) {
		locations = new FastMap<Integer, SiegeLocation>();
		locations.put(fortress, SiegeService.getInstance().getFortress(fortress));
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeH(locations.size());
		for (SiegeLocation loc : locations.values()) {
			writeD(loc.getLocationId());
			writeC(loc.isUnderShield() ? 1 : 0);
		}
	}

}