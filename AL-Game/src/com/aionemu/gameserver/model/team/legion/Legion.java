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
package com.aionemu.gameserver.model.team.legion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.World;

/**
 * @author Simple
 */
public class Legion {

	/** Legion Information **/
	private int legionId = 0;
	private String legionName = "";
	private int legionLevel = 1;
	private int legionRank = 0;
	private int contributionPoints = 0;
	private List<Integer> legionMembers = new ArrayList<Integer>();
	private short deputyPermission = 0x1E0C;
	private short centurionPermission = 0x1C08;
	private short legionaryPermission = 0x1800;
	private short volunteerPermission = 0x800;
	private int disbandTime;
	private TreeMap<Timestamp, String> announcementList = new TreeMap<Timestamp, String>();
	private LegionEmblem legionEmblem = new LegionEmblem();
	private LegionWarehouse legionWarehouse;
	private SortedSet<LegionHistory> legionHistory;

	/**
	 * Only called when a legion is created!
	 *
	 * @param legionId
	 * @param legionName
	 */
	public Legion(int legionId, String legionName) {
		this();
		this.legionId = legionId;
		this.legionName = legionName;
	}

	/**
	 * Only called when a legion is loaded!
	 */
	public Legion() {
		this.legionWarehouse = new LegionWarehouse(this);
		this.legionHistory = new TreeSet<LegionHistory>(new Comparator<LegionHistory>() {

			@Override
			public int compare(LegionHistory o1, LegionHistory o2) {
				return o1.getTime().getTime() < o2.getTime().getTime() ? 1 : -1;
			}

		});
	}

	/**
	 * @param legionId
	 *          the legionId to set
	 */
	public void setLegionId(int legionId) {
		this.legionId = legionId;
	}

	/**
	 * @return the legionId
	 */
	public int getLegionId() {
		return legionId;
	}

	/**
	 * @param legionName
	 *          the legionName to set
	 */
	public void setLegionName(String legionName) {
		this.legionName = legionName;
	}

	/**
	 * @return the legionName
	 */
	public String getLegionName() {
		return legionName;
	}

	/**
	 * @param legionMembers
	 *          the legionMembers to set
	 */
	public void setLegionMembers(ArrayList<Integer> legionMembers) {
		this.legionMembers = legionMembers;
	}

	/**
	 * @return the legionMembers
	 */
	public List<Integer> getLegionMembers() {
		return legionMembers;
	}

	/**
	 * @return the online legionMembers
	 */
	public ArrayList<Player> getOnlineLegionMembers() {
		ArrayList<Player> onlineLegionMembers = new ArrayList<Player>();
		for (int legionMemberObjId : legionMembers) {
			Player onlineLegionMember = World.getInstance().findPlayer(legionMemberObjId);
			if (onlineLegionMember != null)
				onlineLegionMembers.add(onlineLegionMember);
		}
		return onlineLegionMembers;
	}

	/**
	 * Add a legionMember to the legionMembers list
	 *
	 * @param legionMember
	 */
	public boolean addLegionMember(int playerObjId) {
		if (canAddMember()) {
			legionMembers.add(playerObjId);
			return true;
		}
		return false;
	}

	/**
	 * Delete a legionMember from the legionMembers list
	 *
	 * @param playerObjId
	 */
	public void deleteLegionMember(int playerObjId) {
		legionMembers.remove(new Integer(playerObjId));
	}

	/**
	 * This method will set the permissions
	 *
	 * @param legionarPermission2
	 * @param centurionPermission1
	 * @param centurionPermission2
	 * @return true or false
	 */
	public boolean setLegionPermissions(short deputyPermission,	short centurionPermission,	short legionaryPermission,	short volunteerPermission) {
			this.deputyPermission = deputyPermission;
			this.centurionPermission = centurionPermission;
			this.legionaryPermission = legionaryPermission;
			this.volunteerPermission = volunteerPermission;
			return true;
	}


	/**
	 * @return the deputyPermission
	 */
	public short getDeputyPermission() {
		return deputyPermission;
	}


	/**
	 * @return the centurionPermission
	 */
	public short getCenturionPermission() {
		return centurionPermission;
	}


	/**
	 * @return the legionarPermission
	 */
	public short getLegionaryPermission() {
		return legionaryPermission;
	}


	/**
	 * @return the volunteerPermission
	 */
	public short getVolunteerPermission() {
		return volunteerPermission;
	}

	/**
	 * @return the legionLevel
	 */
	public int getLegionLevel() {
		return legionLevel;
	}

	/**
	 * @param legionLevel
	 */
	public void setLegionLevel(int legionLevel) {
		this.legionLevel = legionLevel;
	}

	/**
	 * @param legionRank
	 *          the legionRank to set
	 */
	public void setLegionRank(int legionRank) {
		this.legionRank = legionRank;
	}

	/**
	 * @return the legionRank
	 */
	public int getLegionRank() {
		return legionRank;
	}

	/**
	 * @param contributionPoints
	 *          the contributionPoints to set
	 */
	public void addContributionPoints(int contributionPoints) {
		this.contributionPoints = this.contributionPoints + contributionPoints;
		if (this.contributionPoints < 0)
			this.contributionPoints = Integer.MAX_VALUE;
	}

	/**
	 * @param newPoints
	 */
	public void setContributionPoints(int contributionPoints) {
		this.contributionPoints = contributionPoints;
		if (this.contributionPoints < 0)
			this.contributionPoints = Integer.MAX_VALUE;
	}

	/**
	 * @return the contributionPoints
	 */
	public int getContributionPoints() {
		return contributionPoints;
	}

	/**
	 * This method will check whether a legion has enough members to level up
	 *
	 * @return true or false
	 */
	public boolean hasRequiredMembers() {
		switch (getLegionLevel()) {
			case 1:
				if (getLegionMembers().size() >= LegionConfig.LEGION_LEVEL2_REQUIRED_MEMBERS)
					return true;
				break;
			case 2:
				if (getLegionMembers().size() >= LegionConfig.LEGION_LEVEL3_REQUIRED_MEMBERS)
					return true;
				break;
			case 3:
				if (getLegionMembers().size() >= LegionConfig.LEGION_LEVEL4_REQUIRED_MEMBERS)
					return true;
				break;
			case 4:
				if (getLegionMembers().size() >= LegionConfig.LEGION_LEVEL5_REQUIRED_MEMBERS)
					return true;
				break;
		}
		return false;
	}

	/**
	 * This method will return the kinah price required to level up
	 *
	 * @return int
	 */
	public int getKinahPrice() {
		switch (getLegionLevel()) {
			case 1:
				return LegionConfig.LEGION_LEVEL2_REQUIRED_KINAH;
			case 2:
				return LegionConfig.LEGION_LEVEL3_REQUIRED_KINAH;
			case 3:
				return LegionConfig.LEGION_LEVEL4_REQUIRED_KINAH;
			case 4:
				return LegionConfig.LEGION_LEVEL5_REQUIRED_KINAH;
		}
		return 0;
	}

	/**
	 * This method will return the contribution points required to level up
	 *
	 * @return int
	 */
	public int getContributionPrice() {
		switch (getLegionLevel()) {
			case 1:
				return LegionConfig.LEGION_LEVEL2_REQUIRED_CONTRIBUTION;
			case 2:
				return LegionConfig.LEGION_LEVEL3_REQUIRED_CONTRIBUTION;
			case 3:
				return LegionConfig.LEGION_LEVEL4_REQUIRED_CONTRIBUTION;
			case 4:
				return LegionConfig.LEGION_LEVEL5_REQUIRED_CONTRIBUTION;
		}
		return 0;
	}

	/**
	 * This method will return true if a legion is able to add a member
	 *
	 * @return
	 */
	private boolean canAddMember() {
		switch (getLegionLevel()) {
			case 1:
				if (getLegionMembers().size() < LegionConfig.LEGION_LEVEL1_MAX_MEMBERS)
					return true;
				break;
			case 2:
				if (getLegionMembers().size() < LegionConfig.LEGION_LEVEL2_MAX_MEMBERS)
					return true;
				break;
			case 3:
				if (getLegionMembers().size() < LegionConfig.LEGION_LEVEL3_MAX_MEMBERS)
					return true;
				break;
			case 4:
				if (getLegionMembers().size() < LegionConfig.LEGION_LEVEL4_MAX_MEMBERS)
					return true;
				break;
			case 5:
				if (getLegionMembers().size() < LegionConfig.LEGION_LEVEL5_MAX_MEMBERS)
					return true;
				break;
		}
		return false;
	}

	/**
	 * @param announcementList
	 *          the announcementList to set
	 */
	public void setAnnouncementList(TreeMap<Timestamp, String> announcementList) {
		this.announcementList = announcementList;
	}

	/**
	 * This method will add a new announcement to the list
	 */
	public void addAnnouncementToList(Timestamp unixTime, String announcement) {
		this.announcementList.put(unixTime, announcement);
	}

	/**
	 * This method removes the first entry
	 */
	public void removeFirstEntry() {
		this.announcementList.remove(this.announcementList.firstEntry().getKey());
	}

	/**
	 * @return the announcementList
	 */
	public TreeMap<Timestamp, String> getAnnouncementList() {
		return this.announcementList;
	}

	/**
	 * @return the currentAnnouncement
	 */
	public Entry<Timestamp, String> getCurrentAnnouncement() {
		if (this.announcementList.size() > 0)
			return this.announcementList.lastEntry();
		return null;
	}

	/**
	 * @param disbandTime
	 *          the disbandTime to set
	 */
	public void setDisbandTime(int disbandTime) {
		this.disbandTime = disbandTime;
	}

	/**
	 * @return the disbandTime
	 */
	public int getDisbandTime() {
		return disbandTime;
	}

	/**
	 * @return true if currently disbanding
	 */
	public boolean isDisbanding() {
		return disbandTime > 0;
	}

	/**
	 * This function checks if object id is in list
	 *
	 * @param playerObjId
	 * @return true if ID is found in the list
	 */
	public boolean isMember(int playerObjId) {
		return legionMembers.contains(playerObjId);
	}

	/**
	 * @param legionEmblem
	 *          the legionEmblem to set
	 */
	public void setLegionEmblem(LegionEmblem legionEmblem) {
		this.legionEmblem = legionEmblem;
	}

	/**
	 * @return the legionEmblem
	 */
	public LegionEmblem getLegionEmblem() {
		return legionEmblem;
	}

	/**
	 * @param legionWarehouse
	 *          the legionWarehouse to set
	 */
	public void setLegionWarehouse(LegionWarehouse legionWarehouse) {
		this.legionWarehouse = legionWarehouse;
	}

	/**
	 * @return the legionWarehouse
	 */
	public LegionWarehouse getLegionWarehouse() {
		return legionWarehouse;
	}

	/**
	 * Get warehouse slots
	 *
	 * @return warehouse slots
	 */
	public int getWarehouseSlots() {
		switch (getLegionLevel()) {
			case 1:
				return LegionConfig.LWH_LEVEL1_SLOTS;
			case 2:
				return LegionConfig.LWH_LEVEL2_SLOTS;
			case 3:
				return LegionConfig.LWH_LEVEL3_SLOTS;
			case 4:
				return LegionConfig.LWH_LEVEL4_SLOTS;
			case 5:
				return LegionConfig.LWH_LEVEL5_SLOTS;
		}
		return LegionConfig.LWH_LEVEL1_SLOTS;
	}

	public int getWarehouseLevel()
	{
		return getLegionLevel() - 1;
	}

	/**
	 * @return the legionHistory
	 */
	public Collection<LegionHistory> getLegionHistory() {
		return this.legionHistory;
	}
	
	public Collection<LegionHistory> getLegionHistoryByTabId(int tabId) {
		if (legionHistory.isEmpty())
			return legionHistory;
		
		SortedSet<LegionHistory> result = new TreeSet<LegionHistory>(new Comparator<LegionHistory>() {

			@Override
			public int compare(LegionHistory o1, LegionHistory o2) {
				return o1.getTime().getTime() < o2.getTime().getTime() ? 1 : -1;
			}

		});
		for (LegionHistory lh : legionHistory) {
			if (isLegionHistoryTypeForTabId(tabId, lh))
				result.add(lh);
		}
		return result;
	}
	
	private boolean isLegionHistoryTypeForTabId(int tabId, LegionHistory legionHistory) {
		switch (legionHistory.getLegionHistoryType().getHistoryId()) {
			case 0: //CREATE(0)
			case 1: //JOIN(1)
			case 2: //KICK(2)
			case 3: //LEVEL_UP(3)
			case 4: //APPOINTED(4)
			case 5: //EMBLEM_REGISTER(5)
			case 6: //EMBLEM_MODIFIED(6)
				return tabId == 0;
			case 15: //ITEM_DEPOSIT(15)
			case 16: //ITEM_WITHDRAW(16)
			case 17: //KINAH_DEPOSIT(17)
			case 18: //KINAH_WITHDRAW(18)
				return tabId == 2;
		}
		return false;
	}
	
	public static int getTabIdFromLegionHistoryType(LegionHistory legionHistory) {
		switch (legionHistory.getLegionHistoryType().getHistoryId()) {
			case 0: //CREATE(0)
			case 1: //JOIN(1)
			case 2: //KICK(2)
			case 3: //LEVEL_UP(3)
			case 4: //APPOINTED(4)
			case 5: //EMBLEM_REGISTER(5)
			case 6: //EMBLEM_MODIFIED(6)
				return 0;
			case 15: //ITEM_DEPOSIT(15)
			case 16: //ITEM_WITHDRAW(16)
			case 17: //KINAH_DEPOSIT(17)
			case 18: //KINAH_WITHDRAW(18)
				return 2;
		}
		return 0;
	}
	
	/**
	 * @param history
	 */
	public void addHistory(LegionHistory history) {
		this.legionHistory.add(history);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Legion legion = (Legion) o;
		return legionId == legion.legionId;
	}

	@Override
	public int hashCode() {
		return legionId;
	}
}
