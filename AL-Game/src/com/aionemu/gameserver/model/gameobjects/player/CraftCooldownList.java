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
package com.aionemu.gameserver.model.gameobjects.player;

import javolution.util.FastMap;

/**
 * @author synchro2
 */
public class CraftCooldownList {

	//private Player owner;
	private FastMap<Integer, Long> craftCooldowns;

	CraftCooldownList(Player owner) {
		//this.owner = owner;
	}

	public boolean isCanCraft(int delayId) {
		if (craftCooldowns == null || !craftCooldowns.containsKey(delayId))
			return true;

		Long coolDown = craftCooldowns.get(delayId);
		if (coolDown == null)
			return true;

		if (coolDown < System.currentTimeMillis()) {
			craftCooldowns.remove(delayId);
			return true;
		}

		return false;
	}

	public long getCraftCooldown(int delayId) {
		if (craftCooldowns == null || !craftCooldowns.containsKey(delayId))
			return 0;

		return craftCooldowns.get(delayId);
	}

	public FastMap<Integer, Long> getCraftCoolDowns() {
		return craftCooldowns;
	}

	public void setCraftCoolDowns(FastMap<Integer, Long> craftCoolDowns) {
		this.craftCooldowns = craftCoolDowns;
	}

	public void addCraftCooldown(int delayId, int delay) {
		if (craftCooldowns == null) {
			craftCooldowns = new FastMap<Integer, Long>();
		}

		long nextUseTime = System.currentTimeMillis() + (delay * 1000);
		craftCooldowns.put(delayId, nextUseTime);
	}
}