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
package com.aionemu.gameserver.model.team2.group;


import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.utils.idfactory.IDFactory;


/**
 * @author ATracer
 */
public class PlayerGroup extends TemporaryPlayerTeam<PlayerGroupMember> {

	private LootGroupRules lootGroupRules = new LootGroupRules();
	private final PlayerGroupStats playerGroupStats;
	private int groupType = 0x3F;
	private int instancePoints = 0;
	
	public PlayerGroup(PlayerGroupMember leader) {
		super(IDFactory.getInstance().nextId());
		this.playerGroupStats = new PlayerGroupStats(this);
		initializeTeam(leader);
	}

	@Override
	public void addMember(PlayerGroupMember member) {
		super.addMember(member);
		playerGroupStats.onAddPlayer(member);
		member.getObject().setPlayerGroup2(this);
	}

	@Override
	public void removeMember(PlayerGroupMember member) {
		super.removeMember(member);
		playerGroupStats.onRemovePlayer(member);
		member.getObject().setPlayerGroup2(null);
	}

	@Override
	public boolean isFull() {
		return size() == 6;
	}

	@Override
	public int getMinExpPlayerLevel() {
		return playerGroupStats.getMinExpPlayerLevel();
	}

	@Override
	public int getMaxExpPlayerLevel() {
		return playerGroupStats.getMaxExpPlayerLevel();
	}

	public final LootGroupRules getLootGroupRules() {
		return lootGroupRules;
	}
        
        
	public void setLootGroupRules(LootGroupRules lootGroupRules) {
		this.lootGroupRules = lootGroupRules;
	}

	public final int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}
	
	public void setGroupInstancePoints(int points)
	{
	    this.instancePoints = points;
	}
	
	public int getGroupInstancePoints()
	{
	    return instancePoints;
	}

}
