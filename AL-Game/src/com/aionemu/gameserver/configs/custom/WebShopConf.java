package com.aionemu.gameserver.configs.custom;

import com.aionemu.commons.configuration.Property;

public class WebShopConf {
	
	@Property(key = "gameserver.custom.webshop.enabled", defaultValue = "false")
	public static boolean WEBSHOP_ENABLED;
	
	@Property(key = "gameserver.custom.webshop.inittime", defaultValue = "300")
	public static int WEBSHOP_INIT_TIME;
	
	@Property(key = "gameserver.custom.webshop.frequence", defaultValue = "5")
	public static int WEBSHOP_FREQUENCE;
	
	@Property(key = "gameserver.custom.webshop.disableonsiege", defaultValue = "false")
	public static boolean WEBSHOP_DISABLE_ON_SIEGE;
}