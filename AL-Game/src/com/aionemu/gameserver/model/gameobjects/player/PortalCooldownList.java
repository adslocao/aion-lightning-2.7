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

import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PortalCooldownList {

	private Player owner;
	private FastMap<Integer, Long> portalCooldowns;

	/**
	 * @param owner
	 */
	PortalCooldownList(Player owner) {
		this.owner = owner;
	}

	/**
	 * @param worldId
	 ** @return
	 */
	public boolean isPortalUseDisabled(int worldId) {
		if (portalCooldowns == null || !portalCooldowns.containsKey(worldId))
			return false;

		Long coolDown = portalCooldowns.get(worldId);
		if (coolDown == null)
			return false;

		if (coolDown < System.currentTimeMillis()) {
			portalCooldowns.remove(worldId);
			return false;
		}

		return true;
	}

	/**
	 * @param worldId
	 * @return
	 */
	public long getPortalCooldown(int worldId) {
		if (portalCooldowns == null || !portalCooldowns.containsKey(worldId))
			return 0;

		return portalCooldowns.get(worldId);
	}

	public FastMap<Integer, Long> getPortalCoolDowns() {
		return portalCooldowns;
	}

	public void setPortalCoolDowns(FastMap<Integer, Long> portalCoolDowns) {
		this.portalCooldowns = portalCoolDowns;
	}

	/**
	 * @param worldId
	 * @param time
	 */
	public void addPortalCooldown(int worldId, long useDelay) {
		if (portalCooldowns == null) {
			portalCooldowns = new FastMap<Integer, Long>();
		}

		long nextUseTime = System.currentTimeMillis() + useDelay;
		portalCooldowns.put(worldId, nextUseTime);

		if(owner.isInTeam()){
			owner.getCurrentTeam().sendPacket(new SM_INSTANCE_INFO(owner, worldId));
			return;
		}
		PacketSendUtility.sendPacket(owner, new SM_INSTANCE_INFO(owner, worldId));
	}

	/**
	 * @param worldId
	 */
	public void removePortalCoolDown(int worldId) {
		if (portalCooldowns != null) {
			portalCooldowns.remove(worldId);
		}
	}

	/**
	 * @return
	 */
	public boolean hasCooldowns() {
		return portalCooldowns != null && portalCooldowns.size() > 0;
	}

	/**
	 * @return
	 */
	public int size() {
		return portalCooldowns != null ? portalCooldowns.size() : 0;
	}

}
