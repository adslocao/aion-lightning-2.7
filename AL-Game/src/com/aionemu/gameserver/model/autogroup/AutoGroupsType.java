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

import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import java.util.List;

/**
 *
 * @author xTz
 */
public enum  AutoGroupsType {

	CHANTRA_DREDGION((byte) 2, 0, 12),
	BARANATH_DREDGION((byte) 1, 0, 12),
	ELYOS_FIRE_TEMPLE((byte) 4, 300000, 6),
	NOCHSANA_TRAINING_CAMP((byte) 5, 600000, 6),
	DARK_POETA((byte) 6, 1200000, 6),
	STEEL_RAKE((byte) 7, 1200000, 6),
	UDAS_TEMPLE((byte) 8, 600000, 6),
	LOWER_UDAS_TEMPLE((byte) 9, 600000, 6),
	EMPYREAN_CRUCIBLE((byte) 11, 600000, 6),
	ASMODIANS_FIRE_TEMPLE((byte) 14, 300000, 6),
	ARENA_OF_CHAOS_1((byte) 21, 0, PvPConfig.CHOAS_NB_PLAYER),
	ARENA_OF_CHAOS_2((byte) 22, 0, PvPConfig.CHOAS_NB_PLAYER),
	ARENA_OF_CHAOS_3((byte) 23, 0, PvPConfig.CHOAS_NB_PLAYER),
	ARENA_OF_DISCIPLINE_1((byte) 24, 0, 2),
	ARENA_OF_DISCIPLINE_2((byte) 25, 0, 2),
	ARENA_OF_DISCIPLINE_3((byte) 26, 0, 2),
	CHAOS_TRAINING_GROUNDS_1((byte) 27, 0, PvPConfig.CHOAS_NB_PLAYER),
	CHAOS_TRAINING_GROUNDS_2((byte) 28, 0, PvPConfig.CHOAS_NB_PLAYER),
	CHAOS_TRAINING_GROUNDS_3((byte) 29, 0, PvPConfig.CHOAS_NB_PLAYER),
	DISCIPLINE_TRAINING_GROUNDS_1((byte) 30, 0, 2),
	DISCIPLINE_TRAINING_GROUNDS_2((byte) 31, 0, 2),
	DISCIPLINE_TRAINING_GROUNDS_3((byte) 32, 0, 2);

	private byte instanceMaskId;
	private int time;
	private int playerSize;
	private AutoGroup template;

	private AutoGroupsType(byte instanceMaskId, int time, int playerSize) {
		this.instanceMaskId = instanceMaskId;
		this.time = time;
		this.playerSize = playerSize;
		template = DataManager.AUTO_GROUP.getTemplateByInstaceMaskId(instanceMaskId);
	}

	public int getInstanceMapId() {
		return template.getInstanceId();
	}

	public int getPlayerSize() {
		return playerSize;
	}

	public byte getInstanceMaskId() {
		return instanceMaskId;
	}

	public int getNameId() {
		return template.getNameId();
	}

	public int getTittleId() {
		return template.getTitleId();
	}

	public int getTime() {
		return time;
	}

	public int getMinLevel() {
		return template.getMinLvl();
	}

	public int getMaxLevel() {
		return template.getMaxLvl();
	}

	public boolean hasRegisterGroup() {
		return template.hasRegisterGroup();
	}

	public boolean hasRegisterFast() {
		return template.hasRegisterFast();
	}

	public boolean containNpcId(int npcId) {
		return template.getNpcIds().contains(npcId);
	}

	public List<Integer> getNpcIds() {
		return template.getNpcIds();
	}

	public boolean isDredgion() {
		return instanceMaskId == 1 || instanceMaskId == 2;
	}

	public static AutoGroupsType getAutoGroupByInstanceMaskId(byte instanceMaskId) {
		for (AutoGroupsType autoGroupsType : values()) {
			if (autoGroupsType.getInstanceMaskId() == instanceMaskId) {
				return autoGroupsType;
			}
		}
		return null;
	}

	public static AutoGroupsType getAutoGroup(int level, int npcId) {
		for (AutoGroupsType agt : values()) {
			if (agt.hasLevelPermit(level) && agt.containNpcId(npcId)) {
				return agt;
			}
		}
		return null;
	}

	public static AutoGroupsType getAutoGroup(int npcId) {
		for (AutoGroupsType agt : values()) {
			if (agt.containNpcId(npcId)) {
				return agt;
			}
		}
		return null;
	}

	public boolean isPvPSoloArena() {
		switch (this) {
			case ARENA_OF_DISCIPLINE_1:
			case ARENA_OF_DISCIPLINE_2:
			case ARENA_OF_DISCIPLINE_3:
				return true;
		}
		return false;
	}

	public boolean isTrainigPvPSoloArena() {
		switch (this) {
			case DISCIPLINE_TRAINING_GROUNDS_1:
			case DISCIPLINE_TRAINING_GROUNDS_2:
			case DISCIPLINE_TRAINING_GROUNDS_3:
				return true;
		}
		return false;
	}

	public boolean isPvPFFAArena() {
		switch (this) {
			case ARENA_OF_CHAOS_1:
			case ARENA_OF_CHAOS_2:
			case ARENA_OF_CHAOS_3:
				return true;
		}
		return false;
	}

	public boolean isTrainigPvPFFAArena() {
		switch (this) {
			case CHAOS_TRAINING_GROUNDS_1:
			case CHAOS_TRAINING_GROUNDS_2:
			case CHAOS_TRAINING_GROUNDS_3:
				return true;
		}
		return false;
	}

	public boolean isPvpArena() {
		return isTrainigPvPFFAArena() || isPvPFFAArena() || isTrainigPvPSoloArena() || isPvPSoloArena();
	}

	public boolean hasLevelPermit(int level) {
		return level >= getMinLevel() && level <= getMaxLevel();
	}
}
