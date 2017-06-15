/*
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
package com.aionemu.gameserver.model.templates.world;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.world.WorldType;

/**
 * @author Luno
 */
@XmlRootElement(name = "map")
@XmlAccessorType(XmlAccessType.NONE)
public class WorldMapTemplate {

	@XmlAttribute(name = "name")
	private String name = "";

	@XmlAttribute(name = "id", required = true)
	private Integer mapId;

	@XmlAttribute(name = "twin_count")
	private int twinCount;

	@XmlAttribute(name = "max_user")
	private int maxUser;

	@XmlAttribute(name = "prison")
	private boolean prison = false;

	@XmlAttribute(name = "instance")
	private boolean instance = false;

	@XmlAttribute(name = "death_level", required = true)
	private int deathlevel = 0;

	@XmlAttribute(name = "water_level", required = true)
	// TODO: Move to Zone
	private int waterlevel = 16;

	@XmlAttribute(name = "fly")
	private boolean fly = false;

	@XmlAttribute(name = "world_type")
	private WorldType worldType = WorldType.NONE;

	@XmlAttribute(name = "world_size")
	private int worldSize;

	@XmlElement(name = "ai_info")
	private AiInfo aiInfo = AiInfo.DEFAULT;

	@XmlAttribute(name = "except_buff")
	private boolean exceptBuff = false;
	
	public String getName() {
		return name;
	}

	public Integer getMapId() {
		return mapId;
	}

	public int getTwinCount() {
		return twinCount;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public boolean isPrison() {
		return prison;
	}

	public boolean isInstance() {
		return instance;
	}

	public int getWaterLevel() {
		return waterlevel;
	}

	public int getDeathLevel() {
		return deathlevel;
	}

	public WorldType getWorldType() {
		return worldType;
	}

	public int getWorldSize() {
		return worldSize;
	}

	public boolean isFly() {
		return fly;
	}

	
	/**
	 * @return the exceptBuff
	 */
	public boolean isExceptBuff() {
		return exceptBuff;
	}

	public AiInfo getAiInfo() {
		return aiInfo;
	}

}
