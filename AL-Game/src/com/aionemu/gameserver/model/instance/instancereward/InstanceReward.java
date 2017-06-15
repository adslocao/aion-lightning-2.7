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
package com.aionemu.gameserver.model.instance.instancereward;

import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class InstanceReward<T extends InstancePlayerReward> {

	protected FastList<T> instanceRewards = new FastList<T>();
	private InstanceScoreType instanceScoreType = InstanceScoreType.START_PROGRESS;
	protected Integer mapId;
	protected int instanceId;

	public InstanceReward(Integer mapId, int instanceId) {
		this.mapId = mapId;
		this.instanceId = instanceId;
	}

	public FastList<T> getInstanceRewards() {
		return instanceRewards;
	}

	public boolean containPlayer(Player player) {
		for (InstancePlayerReward instanceReward : instanceRewards) {
			if (instanceReward.getOwner().equals(player.getObjectId())) {
				return true;
			}
		}
		return false;
	}

	public void removePlayerReward(T reward) {
		if (instanceRewards.contains(reward)) {
			instanceRewards.remove(reward);
		}
	}

	public InstancePlayerReward getPlayerReward(Player player) {
		for (InstancePlayerReward instanceReward : instanceRewards) {
			if (instanceReward.getOwner().equals(player.getObjectId())) {
				return instanceReward;
			}
		}
		return null;
	}

	public void addPlayerReward(T reward) {
		instanceRewards.add(reward);
	}

	public void setInstanceScoreType(InstanceScoreType instanceScoreType) {
		this.instanceScoreType = instanceScoreType;
	}

	public InstanceScoreType getInstanceScoreType() {
		return instanceScoreType;
	}

	public Integer getMapId() {
		return mapId;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public FastList<InstancePlayerReward> getPlayersInside() {
		return getPlayersInsideByRace(Race.PC_ALL);
	}

	public FastList<InstancePlayerReward> getPlayersInsideByRace(Race race) {
		FastList<InstancePlayerReward> playerRewards = new FastList<InstancePlayerReward>();
		for (InstancePlayerReward instanceReward : instanceRewards) {
			Player player = instanceReward.getPlayer();
			if (player != null && player.isOnline() && player.getInstanceId() == instanceId) {
				if (race == Race.PC_ALL || player.getRace().equals(race)) {
					playerRewards.add(instanceReward);
				}
			}
		}
		return playerRewards;
	}

	public boolean isRewarded() {
		return instanceScoreType.isEndProgress();
	}

	public boolean isPreparing() {
		return instanceScoreType.isPreparing();
	}

	public boolean isStartProgress() {
		return instanceScoreType.isStartProgress();
	}

	public void clear() {
		instanceRewards.clear();
	}

	protected InstanceReward<?> getInstanceReward()  {
		return this;
	}
}
