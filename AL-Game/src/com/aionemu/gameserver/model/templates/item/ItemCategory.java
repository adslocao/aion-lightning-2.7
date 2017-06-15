package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author LokiReborn
 */
@XmlType(name = "item_category")
@XmlEnum
public enum ItemCategory {
	MANASTONE,
	GODSTONE,
	ENCHANTMENT,
	FLUX,
	BALIC_EMOTION,
	BALIC_MATERIAL,
	RAWHIDE,
	SOULSTONE,
	RECIPE,
	GATHERABLE,
	GATHERABLE_BONUS,
	SWORD,
	DAGGER,
	MACE,
	ORB,
	SPELLBOOK,
	GREATSWORD,
	POLEARM,
	STAFF,
	BOW,
	SHIELD,
	JACKET,
	PANTS,
	SHOES,
	GLOVES,
	SHOULDERS,
	NECKLACE,
	EARRINGS,
	RINGS,
	HELMET,
	BELT,
	SKILLBOOK,
	STIGMA,
	COINS,
	MEDALS,
	QUEST,
	KEY,
	NONE
}
