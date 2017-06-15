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
package com.aionemu.gameserver.model.templates.pet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.templates.stats.PetStatsTemplate;

/**
 * @author IlBuono
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "pet")
public class PetTemplate {

	@XmlAttribute(name = "id", required = true)
	private int id;
	@XmlAttribute(name = "name", required = true)
	private String name;
	@XmlAttribute(name = "nameid", required = true)
	private int nameId;
	@XmlAttribute(name = "condition_reward")
	private int conditionReward;
	@XmlElement(name = "petfunction")
	private List<PetFunction> petFunctions;
	@XmlElement(name = "petstats")
	private PetStatsTemplate petStats;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getNameId() {
		return nameId;
	}

	public List<PetFunction> getPetFunction() {
		if (petFunctions == null) {
			List<PetFunction> result = new ArrayList<PetFunction>();
			result.add(PetFunction.CreateEmpty());
			petFunctions = result;
		}
		return petFunctions;
	}

	public PetFunction getWarehouseFunction() {
		if (petFunctions == null)
			return null;
		for (PetFunction pf : petFunctions) {
			if (pf.getPetFunctionType() == PetFunctionType.WAREHOUSE)
				return pf;
		}
		return null;
	}

	/**
	 * Used to write to SM_PET packet, so checks only needed ones
	 */
	public boolean ContainsFunction(PetFunctionType type) {
		if (type.getId() < 0)
			return false;

		for (PetFunction t : getPetFunction()) {
			if (t.getPetFunctionType() == type)
				return true;
		}
		return false;
	}

	public PetStatsTemplate getPetStats() {
		return petStats;
	}

	public final int getConditionReward() {
		return conditionReward;
	}

}
