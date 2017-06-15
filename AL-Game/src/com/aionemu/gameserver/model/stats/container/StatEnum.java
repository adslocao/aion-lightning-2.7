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
package com.aionemu.gameserver.model.stats.container;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.items.ItemSlot;

/**
 * @author xavier
 * @author ATracer
 */
@XmlType(name = "StatEnum")
@XmlEnum
public enum StatEnum {

	MAXDP,
	MAXHP(18),
	MAXMP(20),

	AGILITY(107, true),
	BLOCK(33),
	EVASION(31),
	CONCENTRATION(41),
	WILL(0, true),
	HEALTH(0, true),
	ACCURACY(0, true),
	KNOWLEDGE(106, true),
	PARRY(32),
	POWER(0, true),
	SPEED(36, true),
	WALK(35, true),
	HIT_COUNT(0, true),

	ATTACK_RANGE(0, true),
	ATTACK_SPEED(29, -1, true),
	PHYSICAL_ATTACK(25),
	PHYSICAL_ACCURACY(30),
	PHYSICAL_CRITICAL(34),
	PHYSICAL_DEFENSE(26),
	MAIN_HAND_HITS,
	MAIN_HAND_ACCURACY,
	MAIN_HAND_CRITICAL,
	MAIN_HAND_POWER,
	MAIN_HAND_ATTACK_SPEED,
	OFF_HAND_HITS,
	OFF_HAND_ACCURACY,
	OFF_HAND_CRITICAL,
	OFF_HAND_POWER,
	OFF_HAND_ATTACK_SPEED,

	MAGICAL_ATTACK(27),
	MAGICAL_ACCURACY(105),
	MAGICAL_CRITICAL(40),
	MAGICAL_RESIST(28),
	MAX_DAMAGES,
	MIN_DAMAGES,
	IS_MAGICAL_ATTACK(0, true),

	EARTH_RESISTANCE,
	FIRE_RESISTANCE(15),
	WIND_RESISTANCE,
	WATER_RESISTANCE,
	DARK_RESISTANCE,
	LIGHT_RESISTANCE,

	BOOST_MAGICAL_SKILL(104),
	BOOST_SPELL_ATTACK,
	BOOST_CASTING_TIME,
	BOOST_CASTING_TIME_HEAL,
	BOOST_CASTING_TIME_TRAP,
	BOOST_CASTING_TIME_ATTACK,
	BOOST_CASTING_TIME_SUMMONHOMING,
	BOOST_CASTING_TIME_SUMMON,
	BOOST_HATE(109),

	FLY_TIME(23),
	FLY_SPEED(37),

	PVP_ATTACK_RATIO,
	PVP_DEFEND_RATIO,
	PVP_DEFEND_RATIO_PHYSICAL,//TODO unhandled

	DAMAGE_REDUCE,

	BLEED_RESISTANCE,
	BLIND_RESISTANCE,
	BIND_RESISTANCE,
	CHARM_RESISTANCE,
	CONFUSE_RESISTANCE,
	CURSE_RESISTANCE,
	DISEASE_RESISTANCE,
	DEFORM_RESISTANCE,
	FEAR_RESISTANCE,
	OPENAREIAL_RESISTANCE,
	PARALYZE_RESISTANCE,
	PERIFICATION_RESISTANCE,
	POISON_RESISTANCE,
	PULLED_RESISTANCE,//custom
	ROOT_RESISTANCE,
	SILENCE_RESISTANCE,
	SLEEP_RESISTANCE,
	SLOW_RESISTANCE,
	SNARE_RESISTANCE,
	SPIN_RESISTANCE,
	STAGGER_RESISTANCE,
	STUMBLE_RESISTANCE,
	STUN_RESISTANCE,

	SILENCE_RESISTANCE_PENETRATION,
	ARALYZE_RESISTANCE_PENETRATION,
	PARALYZE_RESISTANCE_PENETRATION,
	
	REGEN_MP,
	REGEN_HP,

	REGEN_FP(0),
	STAGGER_RESISTANCE_PENETRATION,
	STUMBLE_RESISTANCE_PENETRATION,
	STUN_RESISTANCE_PENETRATION,
	HEAL_BOOST(110),
	HEAL_SKILL_BOOST,
	ALLRESIST,
	STUNLIKE_RESISTANCE,
	ELEMENTAL_RESISTANCE_DARK,
	ELEMENTAL_RESISTANCE_LIGHT,
	MAGICAL_CRITICAL_RESIST,
	MAGICAL_CRITICAL_DAMAGE_REDUCE,
	PHYSICAL_CRITICAL_RESIST,
	PHYSICAL_CRITICAL_DAMAGE_REDUCE,
	ERFIRE,
	ERAIR,
	EREARTH,
	ERWATER,
	ABNORMAL_RESISTANCE_ALL,
	MAGICAL_DEFEND,
	ALLPARA,
	KNOWIL,
	AGIDEX,
	STRVIT,

	BOOST_HUNTING_XP_RATE,
	BOOST_GROUP_HUNTING_XP_RATE,
	BOOST_QUEST_XP_RATE,
	BOOST_CRAFTING_XP_RATE,
	BOOST_GATHERING_XP_RATE,
	BOOST_DROP_RATE,
	BOOST_MANTRA_RANGE;

	private boolean replace;
	private int sign;

	private int itemStoneMask;

	private StatEnum() {
		this(0);
	}

	private StatEnum(int stoneMask) {
		this(stoneMask, 1, false);
	}

	private StatEnum(int stoneMask, boolean replace) {
		this(stoneMask, 1, replace);
	}

	private StatEnum(int stoneMask, int sign) {
		this(stoneMask, sign, false);
	}

	private StatEnum(int stoneMask, int sign, boolean replace) {
		this.itemStoneMask = stoneMask;
		this.replace = replace;
		this.sign = sign;
	}

	public int getSign() {
		return sign;
	}

	/**
	 * @return the itemStoneMask
	 */
	public int getItemStoneMask() {
		return itemStoneMask;
	}

	/**
	 * Used to find specific StatEnum by its item stone mask
	 * 
	 * @param mask
	 * @return StatEnum
	 */
	public static StatEnum findByItemStoneMask(int mask) {
		for (StatEnum sEnum : values()) {
			if (sEnum.getItemStoneMask() == mask) {
				return sEnum;
			}
		}
		throw new IllegalArgumentException("Cannot find StatEnum for stone mask: " + mask);
	}

	public StatEnum getHandStat(int itemSlot) {
		switch (this) {
			case PHYSICAL_ATTACK:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_POWER : OFF_HAND_POWER;
			case PHYSICAL_ACCURACY:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_ACCURACY : OFF_HAND_ACCURACY;
			case PHYSICAL_CRITICAL:
				return itemSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? MAIN_HAND_CRITICAL : OFF_HAND_CRITICAL;
			default:
				return this;
		}
	}

	public boolean isMainOrSubHandStat() {
		switch (this) {
			case PHYSICAL_ATTACK:
			case POWER:
			case PHYSICAL_ACCURACY:
			case PHYSICAL_CRITICAL:
				return true;

			default:
				return false;
		}
	}

	public boolean isReplace() {
		return replace;
	}
}
