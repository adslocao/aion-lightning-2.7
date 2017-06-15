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
package com.aionemu.gameserver.model.templates.portal;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Portal")
public class PortalTemplate {

	@XmlAttribute(name = "npcid")
	protected int npcId;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "instance")
	protected boolean instance;
	@XmlAttribute(name = "minlevel")
	protected int minLevel;
	@XmlAttribute(name = "maxlevel")
	protected int maxLevel;
	@XmlAttribute(name = "playersize")
	protected int playerSize;
	@XmlElement(name = "entrypoint")
	protected List<EntryPoint> entryPoints;
	@XmlElement(name = "exitpoint")
	protected List<ExitPoint> exitPoints;
	@XmlElement(name = "portalitem")
	protected List<PortalItem> portalItem;
	@XmlAttribute(name = "titleid")
	protected int IdTitle;
	@XmlAttribute(name = "requirequest")
	private String requirequest;
	@XmlTransient
	private int[][] requirequests;
	@XmlAttribute(name = "instancesiegeid")
	protected int InstanceSiegeId;

	/**
	 * @return the npcId
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the instance
	 */
	public boolean isInstance() {
		return instance;
	}

	/**
	 * @return the minLevel
	 */
	public int getMinLevel() {
		return minLevel;
	}

	/**
	 * @return the InstanceSiegeId
	 */
	public int getInstanceSiegeId() {
		return InstanceSiegeId;
	}

	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	public int getPlayerSize() {
		return playerSize;
	}

	/**
	 * @return the entryPoint
	 */
	public List<EntryPoint> getEntryPoints() {
		return entryPoints;
	}

	/**
	 * @return the exitPoint
	 */
	public List<ExitPoint> getExitPoints() {
		return exitPoints;
	}

	public boolean existsExitForRace(Race race) {
		for (ExitPoint exit : exitPoints) {
			if (exit.getRace() == race || exit.getRace() == Race.PC_ALL) {
				return true;
			}
		}
		return false;
	}
	
	public boolean existsEntryForRace(Race race) {
		for (EntryPoint entry : entryPoints) {
			if (entry.getRace() == race || entry.getRace() == Race.PC_ALL) {
				return true;
			}
		}
		return false;
	}

	public boolean leadsToMap(int mapId) {
		for (ExitPoint exit : exitPoints) {
			if (exit.getMapId() == mapId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the portalItem
	 */
	public List<PortalItem> getPortalItem() {
		return portalItem;
	}

	/**
	 * @return the Title Id
	 */
	public int getIdTitle() {
		return IdTitle;
	}

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (requirequest != null) {
			String[] parts = requirequest.split(",");
			requirequests = new int[parts.length][2];
			for (int i = 0; i < parts.length; i++) {
				String[] parts2 = parts[i].split("/");
				requirequests[i][0] = Integer.parseInt(parts2[0]);
				if (parts2.length == 1)
					requirequests[i][1] = 0;
				else
					requirequests[i][1] = Integer.parseInt(parts2[1]);
			}
		}
	}

	public boolean needQuest() {
		return requirequests != null;
	}

	public int[][] getQuests() {
		return requirequests;
	}
}
