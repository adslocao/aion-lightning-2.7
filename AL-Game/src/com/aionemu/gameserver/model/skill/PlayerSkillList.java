/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public final class PlayerSkillList implements SkillList<Player> {

	private final Map<Integer, PlayerSkillEntry> skills;

	private final List<PlayerSkillEntry> deletedSkills;

	public PlayerSkillList() {
		this.skills = new HashMap<Integer, PlayerSkillEntry>();
		this.deletedSkills = new ArrayList<PlayerSkillEntry>();
	}

	public PlayerSkillList(Map<Integer, PlayerSkillEntry> skills) {
		this.skills = skills;
		this.deletedSkills = new ArrayList<PlayerSkillEntry>();
	}

	/**
	 * Returns array with all skills
	 */
	public PlayerSkillEntry[] getAllSkills() {
		return skills.values().toArray(new PlayerSkillEntry[skills.size()]);
	}

	public PlayerSkillEntry[] getDeletedSkills() {
		return deletedSkills.toArray(new PlayerSkillEntry[deletedSkills.size()]);
	}

	public PlayerSkillEntry getSkillEntry(int skillId) {
		return skills.get(skillId);
	}

	@Override
	public boolean addSkill(Player player, int skillId, int skillLevel) {
		return add(player, skillId, skillLevel, PersistentState.NEW);
	}

	/**
	 * Add temporary skill which will not be saved in db
	 * 
	 * @param player
	 * @param skillId
	 * @param skillLevel
	 * @param msg
	 * @return
	 */
	public boolean addTemporarySkill(Player player, int skillId, int skillLevel) {
		return add(player, skillId, skillLevel, PersistentState.NOACTION);
	}

	public void addStigmaSkill(Player player, int skillId, int skillLevel, boolean equipedByNpc) {
		PlayerSkillEntry skill = new PlayerSkillEntry(skillId, true, skillLevel, PersistentState.NOACTION);
		skills.put(skillId, skill);
		if (equipedByNpc) {
			PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(skill, 1300401, false));
		}
	}

	private synchronized boolean add(Player player, int skillId, int skillLevel, PersistentState state) {
		PlayerSkillEntry existingSkill = skills.get(skillId);
		boolean isNew = false;
		if (existingSkill != null) {
			if (existingSkill.getSkillLevel() >= skillLevel) {
				return false;
			}
			existingSkill.setSkillLvl(skillLevel);
		}
		else {
			skills.put(skillId, new PlayerSkillEntry(skillId, false, skillLevel, state));
			isNew = true;
		}
		if (player.isSpawned())
			sendMessage(player, skillId, isNew);
		return true;
	}

	/**
	 * @param player
	 * @param skillId
	 * @param xpReward
	 * @return
	 */
	public boolean addSkillXp(Player player, int skillId, int xpReward, int objSkillPoints) {
		PlayerSkillEntry skillEntry = getSkillEntry(skillId);
		int maxDiff = 40;
		int SkillLvlDiff = skillEntry.getSkillLevel() - objSkillPoints;
		if (maxDiff < SkillLvlDiff) {
			return false;
		}
		switch (skillEntry.getSkillId()) {
			case 30001:
				if (skillEntry.getSkillLevel() == 49)
					return false;
			case 30002:
			case 30003:
				if (skillEntry.getSkillLevel() == 449)
					break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40007:
			case 40008:
				switch (skillEntry.getSkillLevel()) {
					case 99:
					case 199:
					case 299:
					case 399:
					case 449:
					case 499:
					case 549:
						return false;
				}
				player.getRecipeList().autoLearnRecipe(player, skillId, skillEntry.getSkillLevel());
		}
		boolean updateSkill = skillEntry.addSkillXp(xpReward);
		if (updateSkill)
			sendMessage(player, skillId, false);
		return true;
	}

	@Override
	public boolean isSkillPresent(int skillId) {
		return skills.containsKey(skillId);
	}

	@Override
	public int getSkillLevel(int skillId) {
		return skills.get(skillId).getSkillLevel();
	}

	@Override
	public synchronized boolean removeSkill(int skillId) {
		PlayerSkillEntry entry = skills.get(skillId);
		if (entry != null) {
			entry.setPersistentState(PersistentState.DELETED);
			deletedSkills.add(entry);
			skills.remove(skillId);
		}
		return entry != null;
	}

	@Override
	public int size() {
		return skills.size();
	}

	/**
	 * @param player
	 * @param skillId
	 */
	private void sendMessage(Player player, int skillId, boolean isNew) {
		switch (skillId) {
			case 30001:
			case 30002:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330005,
					false));
				break;
			case 30003:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330005,
					false));
				break;
			case 40001:
			case 40002:
			case 40003:
			case 40004:
			case 40005:
			case 40006:
			case 40007:
			case 40008:
			case 40009:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1330061,
					false));
				break;
			default:
				PacketSendUtility.sendPacket(player, new SM_SKILL_LIST(player.getSkillList().getSkillEntry(skillId), 1300050,
					isNew));
		}
	}
}
