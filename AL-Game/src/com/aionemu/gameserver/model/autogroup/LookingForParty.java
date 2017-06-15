/**
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
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
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.autogroup;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xTz
 */
public class LookingForParty {

	private List<SearchInstance> searchInstances = new ArrayList<SearchInstance>();
	private boolean canRegister = true;
	private Player player;
	private long startEnterTime;

	public LookingForParty(Player player, byte instanceMaskId, EntryRequestType ert) {
		this.player = player;
		searchInstances.add(new SearchInstance(instanceMaskId, ert));
	}

	public List<Byte> getInstanceMaskIds() {
		List<Byte> instanceMaskIds = new ArrayList<Byte>();
		for (SearchInstance si : searchInstances) {
			instanceMaskIds.add(si.getInstanceMaskId());
		}
		return instanceMaskIds;
	}

	public int unregisterInstance(byte instanceMaskId) {
		for (SearchInstance si : searchInstances) {
			if (si.getInstanceMaskId() == instanceMaskId) {
				searchInstances.remove(si);
				return searchInstances.size();
			}
		}
		return searchInstances.size();
	}

	public void addInstanceMaskId(byte instanceMaskId, EntryRequestType ert) {
		searchInstances.add(new SearchInstance(instanceMaskId, ert));
	}

	public List<SearchInstance> getSearchInstances() {
		return searchInstances;
	}

	public SearchInstance getSearchInstance(byte instanceMaskId) {
		for (SearchInstance si : searchInstances) {
			if (si.getInstanceMaskId() == instanceMaskId ) {
				return si;
			}
		}
		return null;
	}

	public boolean isRegistredInstance(byte instanceMaskId) {
		for (SearchInstance si : searchInstances) {
			if (si.getInstanceMaskId() == instanceMaskId ) {
				return true;
			}
		}
		return false;
	}

	public boolean isInvited(byte instanceMaskId) {
		return getSearchInstance(instanceMaskId).isInvited();
	}

	public boolean isInvited() {
		for (SearchInstance si : searchInstances) {
			if (si.isInvited()) {
				return true;
			}
		}
		return false;
	}

	public boolean canRegister() {
		return canRegister;
	}

	public void setRejecRegistration(boolean canRegister) {
		this.canRegister = canRegister;
	}

	public void setInvited(byte instanceMaskId, boolean isInvited) {
		getSearchInstance(instanceMaskId).setInvited(isInvited);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setStartEnterTime() {
		startEnterTime = System.currentTimeMillis();
	}

	public boolean isOnStartEnterTask() {
		if (System.currentTimeMillis() - startEnterTime <= (120000 * 2)){
			return true;
		}
		return false;
	}
}