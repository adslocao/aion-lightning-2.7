package com.aionemu.gameserver.configs.custom;

import com.aionemu.commons.configuration.Property;

public class CustomFun {
	@Property(key = "gameserver.custom.customfun.customrank.enable", defaultValue = "false")
	public static boolean CUSTOM_RANK_ENABLED;

	@Property(key = "gameserver.custom.customfun.popuppvparena.disable", defaultValue = "fasle")
	public static boolean POPUP_PVP_ARENA;
	
	/**
	 * Only for higth monster lvl than player
	 */
	@Property(key = "gameserver.custom.customdrop.onlyhighlvl", defaultValue = "true")
	public static boolean ONLY_HIGH_LVL;
	
	/**
	 * Only for higth monster lvl than player
	 */
	@Property(key = "gameserver.custom.customfun.besh.isbarya", defaultValue = "false")
	public static boolean ISBARYA_CUSTOM;

}
