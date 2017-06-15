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

package com.aionemu.gameserver.model.templates.quest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rewards", propOrder = { "selectableRewardItem", "rewardItem" })
public class Rewards {

	@XmlElement(name = "selectable_reward_item")
	protected List<QuestItems> selectableRewardItem;
	@XmlElement(name = "reward_item")
	protected List<QuestItems> rewardItem;
	@XmlAttribute
	protected Integer gold;
	@XmlAttribute
	protected Integer exp;
	@XmlAttribute(name = "reward_abyss_point")
	protected Integer rewardAbyssPoint;
	@XmlAttribute
	protected Integer title;
	@XmlAttribute(name = "extend_inventory")
	protected Integer extendInventory;
	@XmlAttribute(name = "extend_stigma")
	protected Integer extendStigma;

	/**
	 * Gets the value of the selectableRewardItem property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the selectableRewardItem property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getSelectableRewardItem().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 */
	public List<QuestItems> getSelectableRewardItem() {
		if (selectableRewardItem == null) {
			selectableRewardItem = new ArrayList<QuestItems>();
		}
		return this.selectableRewardItem;
	}

	/**
	 * Gets the value of the rewardItem property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the rewardItem property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRewardItem().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 */
	public List<QuestItems> getRewardItem() {
		if (rewardItem == null) {
			rewardItem = new ArrayList<QuestItems>();
		}
		return this.rewardItem;
	}

	/**
	 * Gets the value of the gold property.
	 * 
	 * @return possible object is {@link Integer }
	 */
	public Integer getGold() {
		return gold;
	}

	/**
	 * Gets the value of the exp property.
	 * 
	 * @return possible object is {@link Integer }
	 */
	public Integer getExp() {
		return exp;
	}

	/**
	 * Gets the value of the rewardAbyssPoint property.
	 * 
	 * @return possible object is {@link Integer }
	 */
	public Integer getRewardAbyssPoint() {
		return rewardAbyssPoint;
	}

	/**
	 * Gets the value of the title property.
	 * 
	 * @return possible object is {@link Integer }
	 */
	public Integer getTitle() {
		return title;
	}

	/**
	 * @return the extendInventory
	 */
	public Integer getExtendInventory() {
		return extendInventory;
	}

	/**
	 * @return the extendStigma
	 */
	public Integer getExtendStigma() {
		return extendStigma;
	}
}
