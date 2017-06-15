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
package com.aionemu.gameserver.model.templates.factions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;


/**
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcFaction")
public class NpcFactionTemplate {

	@XmlAttribute(name = "id", required = true)
	protected int id;
	@XmlAttribute(name = "name")
	protected String name;
	@XmlAttribute(name = "nameId")
	protected int nameId;
	@XmlAttribute(name = "category")
	protected FactionCategory category;
	@XmlAttribute(name = "minlevel")
	protected Integer minlevel;
	@XmlAttribute(name = "maxlevel")
	protected int maxlevel = 99;
	@XmlAttribute(name = "race")
	protected Race race;
	@XmlAttribute(name = "npcid")
	protected int npcId;

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public int getNameId() {
		return nameId;
	}
	
	public FactionCategory getCategory() {
		return category;
	}
	
	public int getMinLevel() {
		return minlevel;
	}
	
	public int getMaxLevel() {
		return maxlevel;
	}
	
	public Race getRace() {
		return race;
	}
	
	public boolean isMentor(){
		return category == FactionCategory.MENTOR;
	}

	
	/**
	 * @return the npcId
	 */
	public int getNpcId() {
		return npcId;
	}
}
