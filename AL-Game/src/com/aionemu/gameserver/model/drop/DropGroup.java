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

package com.aionemu.gameserver.model.drop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.Race;
/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dropGroup", propOrder = { "drop" })
public class DropGroup implements DropCalculator {
	protected List<Drop> drop;
	@XmlAttribute
	protected Race race = Race.PC_ALL;
	@XmlAttribute(name = "use_category")
	protected Boolean useCategory = true;
	@XmlAttribute(name = "name")
	protected String group_name;
	@XmlAttribute(name = "must_drop")
	protected Boolean mustDrop = false;
	@XmlAttribute(name = "max_drop")
	protected int maxDrop = 1;
	@XmlAttribute(name = "drop_modifier")
	protected float dropModifier = 1f;

	public List<Drop> getDrop() {
		return this.drop;
	}

	public Race getRace() {
		return race;
	}

	public Boolean isUseCategory() {
		return useCategory;
	}

	/**
	 * @return the name
	 */
	public String getGroupName() {
		if (group_name == null)
			return "";
		return group_name;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, int npcId) {
		// Change rate in fonction of the drop groupe
		dropModifier *= getDropModifier();
		
		if (useCategory) {
			// log oldIndex
			int oldIndex = index;

			if (drop.size() == 0)
				return index;

			// clone dropList
			do {
				ArrayList<Drop> newArrayList = new ArrayList<Drop>(drop);
				do {
					int rnd = Rnd.get(0, newArrayList.size() - 1);
					Drop d = newArrayList.get(rnd);
					newArrayList.remove(rnd);
					index = d.dropCalculator(result, index, dropModifier, race, npcId);
				} while (index < oldIndex + maxDrop && !newArrayList.isEmpty());
			} while (mustDrop && index == oldIndex);
			
			return index;
		} else {
			for (int i = 0; i < drop.size(); i++) {
				Drop d = drop.get(i);
				index = d.dropCalculator(result, index, dropModifier, race, npcId);
			}
		}
		return index;
	}

	public float getDropModifier() {
		return this.dropModifier;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race) {
		return dropCalculator(result, index, dropModifier, race, 0);
	}
}
