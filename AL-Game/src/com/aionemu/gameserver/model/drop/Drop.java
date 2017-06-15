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

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.RateConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;

/**
 * @author MrPoke
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "drop")
public class Drop implements DropCalculator {

	@XmlAttribute(name = "item_id", required = true)
	protected int itemId;
	@XmlAttribute(name = "min_amount", required = true)
	protected int minAmount;
	@XmlAttribute(name = "max_amount", required = true)
	protected int maxAmount;
	@XmlAttribute(required = true)
	protected float chance;
	@XmlAttribute(name = "no_reduce")
	protected Boolean noReduce = false;

	public Drop() {
	}

	public Drop(int itemId, int minAmount, int maxAmount, float chance, boolean noReduce) {
		this.itemId = itemId;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.chance = chance;
		this.noReduce = noReduce;
	}

	/**
	 * Gets the value of the itemId property.
	 * 
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Gets the value of the minAmount property.
	 * 
	 */
	public int getMinAmount() {
		return minAmount;
	}

	/**
	 * Gets the value of the maxAmount property.
	 * 
	 */
	public int getMaxAmount() {
		return maxAmount;
	}

	/**
	 * Gets the value of the chance property.
	 * 
	 */
	public float getChance() {
		return chance;
	}

	public Boolean isNoReduction() {
		return noReduce;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race, int npcId) {
		float percent = modifRatio(this.itemId, npcId);

		if (!noReduce) {
			percent *= dropModifier;
		}
		if (Rnd.get() * 100 < percent) {
			DropItem dropitem = new DropItem(this);
			dropitem.calculateCount();
			dropitem.setIndex(index++);
			result.add(dropitem);
		}
		return index;
	}

	public float modifRatio(int itemId, int npcId) {
		ItemTemplate itemT = DataManager.ITEM_DATA.getItemTemplate(itemId);
		NpcTemplate npcT = DataManager.NPC_DATA.getNpcTemplate(npcId);
		
		float dropChance = chance;

		if (npcT != null && itemT != null) {
			if(npcT.getRating() == NpcRating.NORMAL || npcT.getRating() == NpcRating.JUNK){
				dropChance /= (RateConfig.NERF_NORMAL_DROP_MOB == 0 ? 1 : RateConfig.NERF_NORMAL_DROP_MOB);
			}
			if ((itemT.isArmor() || itemT.isWeapon()) && itemT.getItemQuality() != ItemQuality.EPIC) {
				if(npcT.getRating() == NpcRating.NORMAL || npcT.getRating() == NpcRating.ELITE || npcT.getRating() == NpcRating.JUNK){
					dropChance /= (RateConfig.NERF_STUFF_DROP_MOB == 0 ? 1 : RateConfig.NERF_STUFF_DROP_MOB);
				}
			}
		}else{ // Case of invalid ITEM
			dropChance = -1;
		}
		return dropChance;
	}

	@Override
	public int dropCalculator(Set<DropItem> result, int index, float dropModifier, Race race) {
		return dropCalculator(result, index, dropModifier, race, 0);
	}
}
