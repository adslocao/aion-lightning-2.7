/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * @author xTz
 */
public class InGameShopConfig {

	/**
	 * Enable in game shop
	 */
	@Property(key = "gameserver.ingameshop.enable", defaultValue = "false")
	public static boolean ENABLE_IN_GAME_SHOP;

	/**
	 * Enable gift system between factions
	 */
	@Property(key = "gameserver.ingameshop.gift", defaultValue = "false")
	public static boolean ENABLE_GIFT_OTHER_RACE;

	/**
	 * Categories properties
	 */
	@Property(key = "gameserver.ingameshop.category3", defaultValue = "Clothing")
	public static String CATEGORY_3;

	@Property(key = "gameserver.ingameshop.category4", defaultValue = "Hats")
	public static String CATEGORY_4;

	@Property(key = "gameserver.ingameshop.category5", defaultValue = "Food")
	public static String CATEGORY_5;

	@Property(key = "gameserver.ingameshop.category6", defaultValue = "Drinks")
	public static String CATEGORY_6;

	@Property(key = "gameserver.ingameshop.category7", defaultValue = "Paint")
	public static String CATEGORY_7;

	@Property(key = "gameserver.ingameshop.category8", defaultValue = "Titles")
	public static String CATEGORY_8;

	@Property(key = "gameserver.ingameshop.category9", defaultValue = "Modifications")
	public static String CATEGORY_9;

	@Property(key = "gameserver.ingameshop.category10", defaultValue = "Modifications of wea")
	public static String CATEGORY_10;

	@Property(key = "gameserver.ingameshop.category11", defaultValue = "Pets")
	public static String CATEGORY_11;

	@Property(key = "gameserver.ingameshop.category12", defaultValue = "Wings")
	public static String CATEGORY_12;

	@Property(key = "gameserver.ingameshop.category13", defaultValue = "Recipes")
	public static String CATEGORY_13;

	@Property(key = "gameserver.ingameshop.category14", defaultValue = "Items collection")
	public static String CATEGORY_14;

	@Property(key = "gameserver.ingameshop.category15", defaultValue = "Miscellaneous")
	public static String CATEGORY_15;

	@Property(key = "gameserver.ingameshop.category16", defaultValue = "Asmodian hairstyles")
	public static String CATEGORY_16;

	@Property(key = "gameserver.ingameshop.category17", defaultValue = "Elyos hairstyles")
	public static String CATEGORY_17;

	@Property(key = "gameserver.ingameshop.category18", defaultValue = "Potion")
	public static String CATEGORY_18;

	@Property(key = "gameserver.ingameshop.category19", defaultValue = "Emotion")
	public static String CATEGORY_19;
	
	@Property(key = "gameserver.ingameshop.allow.gift", defaultValue = "true")
	public static boolean ALLOW_GIFTS;
}
