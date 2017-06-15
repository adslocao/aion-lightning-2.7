/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.instance.playerreward;

import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 *
 * @author xTz
 */
public class CruciblePlayerReward extends InstancePlayerReward {

	private int spawnPosition;
	private boolean isRewarded = false;
	private int insignia;
	private boolean isPlayerLeave = false;

	public CruciblePlayerReward(Player player) {
		super(player);
	}

	public boolean isRewarded() {
		return isRewarded;
	}

	public void setRewarded() {
		isRewarded = true;
	}

	public void setInsignia(int insignia) {
		this.insignia = insignia;
	}

	public int getInsignia() {
		return insignia;
	}

	public void setSpawnPosition(int spawnPosition) {
		this.spawnPosition = spawnPosition;
	}

	public int getSpawnPosition() {
		return spawnPosition;
	}

	public boolean isPlayerLeave() {
		return isPlayerLeave;
	}

	public void setPlayerLeave() {
		isPlayerLeave = true;
	}
}
