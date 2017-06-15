package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class PvPConfig {

	/**
	 * Set the number of player to chaos arena
	 */
	@Property(key = "gameserver.chaos.nb.player", defaultValue = "10")
	public static int CHOAS_NB_PLAYER;
	
	/**
	 * Enable rewarding kill points to the winner group members (that are nearby)
	 */
	@Property(key = "gameserver.pvp.kill.rewarding_to_group.enable", defaultValue = "true")
	public static boolean ENABLE_REWARD_KILL_TO_GROUP;
	
	/**
	 * Enable bus nerf
	 */
	@Property(key = "gameserver.pvp.bus.nerf", defaultValue = "false")
	public static boolean ENABLE_NERF_BUS;
	
	/**
	 * Cap when considering in bus
	 */
	@Property(key = "gameserver.pvp.bus.cap", defaultValue = "8")
	public static int CAP_NERF_BUS;
	
	/**
	 * Nerf bus base in %
	 */
	@Property(key = "gameserver.pvp.bus.base", defaultValue = "50")
	public static int RATIO_BASE_BUS;
	
	/**
	 * Nerf bus per player in %
	 */
	@Property(key = "gameserver.pvp.bus.player", defaultValue = "1")
	public static int RATIO_PERSON_BUS;
	
}