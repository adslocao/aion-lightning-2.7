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
package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.items.ItemMask;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.stats.ModifiersTemplate;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author Luno modified by ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(namespace = "", name = "ItemTemplate")
public class ItemTemplate extends VisibleObjectTemplate {

	private final Logger log = LoggerFactory.getLogger(ItemTemplate.class);
	@XmlAttribute(name = "id", required = true)
	@XmlID
	private String id;

	@XmlElement(name = "modifiers", required = false)
	protected ModifiersTemplate modifiers;

	@XmlElement(name = "actions", required = false)
	protected ItemActions actions;

	@XmlAttribute(name = "mask")
	private int mask;

	@XmlAttribute(name = "category")
	private ItemCategory category = ItemCategory.NONE;

	@XmlAttribute(name = "slot")
	private int itemSlot;

	@XmlAttribute(name = "usedelayid")
	private int useDelayId;

	@XmlAttribute(name = "usedelay")
	private int useDelay;

	@XmlAttribute(name = "equipment_type")
	private EquipType equipmentType = EquipType.NONE;

	@XmlAttribute(name = "weapon_boost")
	private int weaponBoost;

	@XmlAttribute(name = "price")
	private int price;

	@XmlAttribute(name = "ap")
	private int abyssPoints;

	@XmlAttribute(name = "ai")
	private int abyssItem;

	@XmlAttribute(name = "aic")
	private int abyssItemCount;

	@XmlAttribute(name = "ri")
	private int rewardItem;

	@XmlAttribute(name = "ric")
	private int rewardItemCount;

	@XmlAttribute(name = "max_stack_count")
	private int maxStackCount = 1;

	@XmlAttribute(name = "level")
	private int level;

	@XmlAttribute(name = "quality")
	private ItemQuality itemQuality;

	@XmlAttribute(name = "item_type")
	private ItemType itemType;

	@XmlAttribute(name = "weapon_type")
	private WeaponType weaponType;

	@XmlAttribute(name = "armor_type")
	private ArmorType armorType;

	@XmlAttribute(name = "attack_type")
	private ItemAttackType attackType;

	@XmlAttribute(name = "attack_gap")
	private float attackGap;// TODO enum

	@XmlAttribute(name = "desc")
	private String description;// TODO string or int

	@XmlAttribute(name = "gender")
	private String genderPermitted;// enum

	@XmlAttribute(name = "option_slot_bonus")
	private int optionSlotBonus;

	@XmlAttribute(name = "bonus_apply")
	private String bonusApply;// enum

	@XmlAttribute(name = "no_enchant")
	private boolean noEnchant;

	@XmlAttribute(name = "dye")
	private boolean itemDyePermitted;

	@XmlAttribute(name = "race")
	private Race race = Race.PC_ALL;

	private int itemId;

	@XmlAttribute(name = "return_world")
	private int returnWorldId;

	@XmlAttribute(name = "return_alias")
	private String returnAlias;

	@XmlElement(name = "godstone")
	private GodstoneInfo godstoneInfo;

	@XmlElement(name = "stigma")
	private Stigma stigma;

	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "restrict")
	private String restrict;

	@XmlAttribute(name = "restrict_max")
	private String restrictMax;

	@XmlTransient
	private int[] restricts;

	@XmlTransient
	private int[] restrictsMax;

	@XmlAttribute(name = "m_slots")
	private int manastoneSlots;

	@XmlAttribute(name = "m_slots_r")
	private int manastoneSlotsRandom;

	@XmlAttribute(name = "temp_exchange_time")
	protected int temExchangeTime;

	@XmlAttribute(name = "expire_time")
	protected int expireTime;

	@XmlElement(name = "weapon_stats")
	protected WeaponStats weaponStats;

	@XmlAttribute(name = "activate_count")
	private int activationCount;

	@XmlAttribute(name = "func_pet_id")
	private int funcPetId;

	@XmlAttribute
	private String usearea;

	@XmlAttribute(name = "charge_level")
	private int chargeLevel;

	@XmlAttribute(name = "charge_price1")
	private int chargePrice1;

	@XmlAttribute(name = "charge_price2")
	private int chargePrice2;

	@XmlAttribute(name = "burn_attack")
	private int burnAttack;

	@XmlAttribute(name = "burn_defend")
	private int burnDefend;

  @XmlElement(name = "tradein_list")
  protected TradeinList tradeinList;

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		setItemId(Integer.parseInt(id));
		String[] parts = restrict.split(",");
		restricts = new int[12];
		for (int i = 0; i < parts.length; i++) {
			restricts[i] = Integer.parseInt(parts[i]);
		}
		if (restrictMax != null) {
			String[] partsMax = restrictMax.split(",");
			restrictsMax = new int[12];
			for (int i = 0; i < partsMax.length; i++) {
				restrictsMax[i] = Integer.parseInt(partsMax[i]);
			}
		}
	}

	public int getMask() {
		return mask;
	}

	public ItemCategory getCategory() {
		return category;
	}

	public int getItemSlot() {
		return itemSlot;
	}

	/**
	 * @param playerClass
	 * @return
	 */
	public boolean isClassSpecific(PlayerClass playerClass) {
		boolean related = restricts[playerClass.ordinal()] > 0;
		if (!related && !playerClass.isStartingClass()) {
			related = restricts[PlayerClass.getStartingClassFor(playerClass).ordinal()] > 0;
		}
		return related;
	}

	/**
	 * @param playerClass
	 * @param level
	 * @return
	 */
	public int getRequiredLevel(PlayerClass playerClass) {
		int requiredLevel = restricts[playerClass.ordinal()];
		if (requiredLevel == 0)
			return -1;
		else
			return requiredLevel;
	}

	public List<StatFunction> getModifiers() {
		if (modifiers != null) {
			return modifiers.getModifiers();
		}
		return null;
	}

	public ItemActions getActions() {
		return actions;
	}

	public EquipType getEquipmentType() {
		return equipmentType;
	}

	public int getPrice() {
		return price;
	}

	public int getAbyssPoints() {
		return abyssPoints;
	}

	public int getAbyssItem() {
		return abyssItem;
	}

	public int getAbyssItemCount() {
		return abyssItemCount;
	}

	public int getRewardItem() {
		return rewardItem;
	}

	public int getRewardItemCount() {
		return rewardItemCount;
	}

	public int getLevel() {
		return level;
	}

	public ItemQuality getItemQuality() {
		return itemQuality;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public WeaponType getWeaponType() {
		return weaponType;
	}

	public ArmorType getArmorType() {
		return armorType;
	}

	@Override
	public int getNameId() {
		try {
			int val = Integer.parseInt(description);
			return val;
		}
		catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public long getMaxStackCount() {
		if (isKinah()) {
			if (CustomConfig.ENABLE_KINAH_CAP) {
				return CustomConfig.KINAH_CAP_VALUE;
			}
			else {
				return Long.MAX_VALUE;
			}
		}
		return maxStackCount;
	}

	public ItemAttackType getAttackType() {
		return attackType;
	}

	public float getAttackGap() {
		return attackGap;
	}

	public String getGenderPermitted() {
		return genderPermitted;
	}

	public int getOptionSlotBonus() {
		return optionSlotBonus;
	}

	public String getBonusApply() {
		return bonusApply;
	}

	public boolean isNoEnchant() {
		return noEnchant;
	}

	public boolean isItemDyePermitted() {
		return itemDyePermitted;
	}

	public Race getRace() {
		return race;
	}

	public int getWeaponBoost() {
		return weaponBoost;
	}

	public boolean isWeapon() {
		return equipmentType == EquipType.WEAPON;
	}

	public boolean isArmor() {
		return equipmentType == EquipType.ARMOR;
	}

	public boolean isKinah() {
		return itemId == ItemId.KINAH.value();
	}

	public boolean isStigma() {
		return itemId > 140000000 && itemId < 140001000;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return id of the associated ItemSetTemplate or null if none
	 */
	public ItemSetTemplate getItemSet() {
		return DataManager.ITEM_SET_DATA.getItemSetTemplateByItemId(itemId);
	}

	/**
	 * Checks if the ItemTemplate belongs to an item set
	 */
	public boolean isItemSet() {
		return getItemSet() != null;
	}

	public GodstoneInfo getGodstoneInfo() {
		return godstoneInfo;
	}

	@Override
	public String getName() {
		return name != null ? name : StringUtils.EMPTY;
	}

	@Override
	public int getTemplateId() {
		return itemId;
	}

	public int getReturnWorldId() {
		return returnWorldId;
	}

	public String getReturnAlias() {
		return returnAlias;
	}

	/**
	 * @return the delay for item.
	 */
	public int getDelayTime() {
		return useDelay;
	}

	public int getDelayId() {
		return useDelayId;
	}

	public Stigma getStigma() {
		return stigma;
	}

	public int getManastoneSlots() {
		return manastoneSlots;
	}

	public int getManastoneSlotsRandom() {
		return manastoneSlotsRandom;
	}

	public boolean isTradeable() {
		return (getMask() & ItemMask.TRADEABLE) == ItemMask.TRADEABLE;
	}

	public boolean isCanFuse() {
		return (getMask() & ItemMask.CAN_COMPOSITE_WEAPON) == ItemMask.CAN_COMPOSITE_WEAPON;
	}

	public boolean canExtract() {
		return (getMask() & ItemMask.CAN_SPLIT) == ItemMask.CAN_SPLIT;
	}

	public boolean isSoulBound() {
		return (getMask() & ItemMask.SOUL_BOUND) == ItemMask.SOUL_BOUND;
	}
	
	public boolean isBreakable() {
		return (getMask() & ItemMask.BREAKABLE) == ItemMask.BREAKABLE;
	}

	public boolean isDeletable() {
		return (getMask() & ItemMask.DELETABLE) == ItemMask.DELETABLE;
	}

	public boolean isTwoHandWeapon() {
		if (!isWeapon())
			return false;

		switch (weaponType) {
			case BOOK_2H:
			case ORB_2H:
			case POLEARM_2H:
			case STAFF_2H:
			case SWORD_2H:
			case TOOLPICK_2H:
			case TOOLROD_2H:
			case BOW:
				return true;
			default:
				return false;
		}
	}

	public int getTempExchangeTime() {
		return temExchangeTime;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public final WeaponStats getWeaponStats() {
		return weaponStats;
	}

	public int getActivationCount() {
		return activationCount;
	}

	public final int getFuncPetId() {
		return funcPetId;
	}

	public void modifyMask(boolean apply, int filter) {
		if (apply)
			mask |= filter;
		else
			mask &= ~filter;
	}

	public boolean isStackable() {
		return this.maxStackCount > 1;
	}

	public boolean hasAreaRestriction() {
		return this.usearea != null && this.usearea.length() > 0;
	}

	public ZoneName getUseArea() {
		if (this.usearea == null)
			return null;

		try {
			return ZoneName.valueOf(this.usearea);
		}
		catch (Exception e) {
			log.warn("Item " + id + " has invalid zone dependacy " + this.usearea);
			return null;
		}
	}

	public int getChargeLevel() {
		return chargeLevel;
	}

	public int getChargePrice1() {
		return chargePrice1;
	}

	public int getChargePrice2() {
		return chargePrice2;
	}

	public int getBurnAttack() {
		return burnAttack;
	}

	public int getBurnDefend() {
		return burnDefend;
	}

	
	/**
	 * @return the tradeinList
	 */
	public TradeinList getTradeinList() {
		return tradeinList;
	}

}
