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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.Collection;

import javolution.util.FastList;

import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_ABYSS_ARTIFACT_INFO3 extends AionServerPacket {

	private Collection<SiegeLocation> locations;
	private int loc;
	private int state;

	public SM_ABYSS_ARTIFACT_INFO3(Collection<SiegeLocation> collection) {
		this.locations = collection;
	}

	public SM_ABYSS_ARTIFACT_INFO3(int loc, int state) {
		this.loc = loc;
		this.state = state;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (locations != null) {
			FastList<ArtifactLocation> validLocations = new FastList<ArtifactLocation>();
			for (SiegeLocation loc : locations) {
				if ((loc.getLocationId() >= 1011) && (loc.getLocationId() < 2000)) {
					if (loc instanceof ArtifactLocation)
						validLocations.add((ArtifactLocation) loc);
				}
			}
			writeH(validLocations.size());
			for (ArtifactLocation loc : validLocations) {
				writeD(loc.getLocationId() * 10 + 1);
				writeC(0);
				writeD(0);
			}
		}
		else {
			writeH(1);
			writeD(loc * 10 + 1);
			writeC(state);
			writeD(0);
		}
	}
}
