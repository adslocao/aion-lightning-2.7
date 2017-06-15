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
package com.aionemu.gameserver.world.container;

import java.util.Map;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;

/**
 * @author Sarynth
 */
public class KiskContainer {

	private final Map<Integer, Kisk> kiskByPlayerObjectId = new FastMap<Integer, Kisk>().shared();

	public void add(Kisk kisk, Player player) {
		if (this.kiskByPlayerObjectId.put(player.getObjectId(), kisk) != null)
			throw new DuplicateAionObjectException();
	}

	public Kisk get(Player player) {
		return this.kiskByPlayerObjectId.get(player.getObjectId());
	}

	public void remove(Player player) {
		this.kiskByPlayerObjectId.remove(player.getObjectId());
	}

	/**
	 * @return
	 */
	public int getCount() {
		return this.kiskByPlayerObjectId.size();
	}
}
