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
package com.aionemu.gameserver.model.stats.calc.functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ArmorType;

/**
 * @author ATracer (based on Mr.Poke EnchantModifier)
 */
public class StatEnchantFunction extends StatAddFunction {

	private static final Logger log = LoggerFactory.getLogger(StatEnchantFunction.class);

	private Item item;

	public StatEnchantFunction(Item owner, StatEnum stat) {
		this.stat = stat;
		this.item = owner;
	}

	@Override
	public final int getPriority() {
		return 30;
	}

	@Override
	public void apply(Stat2 stat) {
		if (!item.isEquipped())
			return;
		int enchantLvl = item.getEnchantLevel();
		if (enchantLvl == 0)
			return;
		if (item.getEquipmentSlot() == ItemSlot.MAIN_OFF_HAND.getSlotIdMask()
			|| item.getEquipmentSlot() == ItemSlot.SUB_OFF_HAND.getSlotIdMask())
			return;
		stat.addToBase(getEnchantAdditionModifier(item.getEnchantLevel(), stat));
	}

	private int getEnchantAdditionModifier(int enchantLvl, Stat2 stat) {
		if (item.getItemTemplate().isWeapon()) {
			return getWeaponModifiers(enchantLvl);
		}
		else if (item.getItemTemplate().isArmor()) {
			return getArmorModifiers(enchantLvl, stat);
		}
		return 0;
	}

	private int getWeaponModifiers(int enchantLvl) {
		switch (stat) {
			case MAIN_HAND_POWER:
			case OFF_HAND_POWER:
			case PHYSICAL_ATTACK:
				switch (item.getItemTemplate().getWeaponType()) {
					case DAGGER_1H:
					case SWORD_1H:
						return 2 * enchantLvl;
					case POLEARM_2H:
					case SWORD_2H:
					case BOW:
						return 4 * enchantLvl;
					case MACE_1H:
					case STAFF_2H:
						return 3 * enchantLvl;
				}
				return 0;
			case BOOST_MAGICAL_SKILL:
				switch (item.getItemTemplate().getWeaponType()) {
					case BOOK_2H:
					case MACE_1H:
					case STAFF_2H:
					case ORB_2H:
						return 20 * enchantLvl;
				}
				return 0;
			case MAGICAL_ATTACK:
				switch (item.getItemTemplate().getWeaponType()) {
					case BOOK_2H:
					case ORB_2H:
						return 3 * enchantLvl;
				}
				return 0;
			default:
				return 0;
		}
	}

	private int getArmorModifiers(int enchantLvl, Stat2 applyStat) {
		ArmorType armorType = item.getItemTemplate().getArmorType();
		if (armorType == null){
			log.warn("Missing item ArmorType itemId: " +item.getItemId() +" EquipmentSlot: "+item.getEquipmentSlot()+" playerObjectId: "+applyStat.getOwner().getObjectId());
			return 0;
		}
		switch (item.getItemTemplate().getArmorType()) {
			case ROBE:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 2 * enchantLvl;
							case MAXHP:
								return 12 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 14 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case LEATHER:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 8 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 12 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case CHAIN:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 3 * enchantLvl;
							case MAXHP:
								return 6 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 10 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case PLATE:
				switch (item.getEquipmentSlot()) {
					case 1 << 5:
					case 1 << 11:
					case 1 << 4:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 4 * enchantLvl;
							case MAXHP:
								return 4 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 2 * enchantLvl;
						}
						return 0;
					case 1 << 12:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 5 * enchantLvl;
							case MAXHP:
								return 6 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 3 * enchantLvl;
						}
						return 0;
					case 1 << 3:
						switch (stat) {
							case PHYSICAL_DEFENSE:
								return 6 * enchantLvl;
							case MAXHP:
								return 8 * enchantLvl;
							case PHYSICAL_CRITICAL_RESIST:
								return 4 * enchantLvl;
						}
						return 0;
				}
				return 0;
			case SHIELD:
				switch (stat) {
					case DAMAGE_REDUCE:
						float reduceRate = enchantLvl > 10 ? 0.2f : enchantLvl * 0.02f;
						return Math.round(reduceRate * applyStat.getBase());
					case BLOCK:
						if (enchantLvl > 10)
							return 30 * (enchantLvl - 10);
						return 0;
				}
			default:
				return 0;
		}
	}

}
