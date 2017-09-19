package com.aionemu.gameserver.configs.custom;

import com.aionemu.commons.configuration.Property;

public class RecursiveAddConf {
	
	@Property(key = "gameserver.custom.recursiveAdd.enabled", defaultValue = "false")
	public static boolean enabled;


	@Property(key = "gameserver.custom.recursiveAdd.itemId", defaultValue = "-1")
	public static int itemId;
	
	
	@Property(key = "gameserver.custom.recursiveAdd.itemCount", defaultValue = "-1")
	public static int itemCount;
	
	@Property(key = "gameserver.custom.recursiveAdd.frequence", defaultValue = "1")
	public static int frequence;
}
