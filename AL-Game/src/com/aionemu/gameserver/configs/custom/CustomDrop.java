package com.aionemu.gameserver.configs.custom;

import com.aionemu.commons.configuration.Property;
import com.aionemu.gameserver.model.drop.Drop;

public class CustomDrop {

	@Property(key = "gameserver.custom.customdrop.enable", defaultValue = "false")
	public static boolean ENABLED;
	
	/**
	 * Only for higth monster lvl than player
	 */
	@Property(key = "gameserver.custom.customdrop.onlyhighlvl", defaultValue = "true")
	public static boolean ONLY_HIGH_LVL;

	/**
	 * Custom drops
	 * id,chance;id,chance
	 */
	@Property(key = "gameserver.custom.customdrop.drops", defaultValue = "")
	public static String DROPS;
	
	public static Drop[] getCustomDrops(){
		String[] drops = DROPS.split(";");
		Drop[] result = new Drop[drops.length];
		for(int i = 0 ; i < drops.length; i++){
			String[] drop = drops[i].split(",");
			if(drop.length != 2)
				continue;
			result[i] = new Drop(Integer.parseInt(drop[0]), 1, 1, (Integer.parseInt(drop[1])/1000000)*100, false);
		}
		return result;
	}
	

	/**
	 * Shop point drops
	 */
	@Property(key = "gameserver.custom.shoppoints.enable", defaultValue = "false")
	public static boolean SHOPPOINTS_DROPABLE;
	
	@Property(key = "gameserver.custom.shoppoints.fixed_reward", defaultValue = "true")
	public static boolean SHOPPOINTS_FIXED_REWARD;
	
	@Property(key = "gameserver.custom.shoppoints.fixed_reward.luck", defaultValue = "50")
	public static int SHOPPOINTS_FIXED_REWARD_LUCK;
	
	@Property(key = "gameserver.custom.shoppoints.fixed_reward.max", defaultValue = "1")
	public static int SHOPPOINTS_FIXED_REWARD_MAX;
	
	@Property(key = "gameserver.custom.shoppoints.fixed_reward.min", defaultValue = "1")
	public static int SHOPPOINTS_FIXED_REWARD_MIN;
	
	@Property(key = "gameserver.custom.shoppoints.legendary.luck", defaultValue = "50")
	public static int SHOPPOINTS_LEGENDARY_LUCK;
	
	@Property(key = "gameserver.custom.shoppoints.legendary.max", defaultValue = "1")
	public static int SHOPPOINTS_LEGENDARY_MAX;
	
	@Property(key = "gameserver.custom.shoppoints.legendary.min", defaultValue = "1")
	public static int SHOPPOINTS_LEGENDARY_MIN;
	
	@Property(key = "gameserver.custom.shoppoints.hero.luck", defaultValue = "50")
	public static int SHOPPOINTS_HERO_LUCK;
	
	@Property(key = "gameserver.custom.shoppoints.hero.max", defaultValue = "1")
	public static int SHOPPOINTS_HERO_MAX;
	
	@Property(key = "gameserver.custom.shoppoints.hero.min", defaultValue = "1")
	public static int SHOPPOINTS_HERO_MIN;
	
	@Property(key = "gameserver.custom.shoppoints.elite.luck", defaultValue = "50")
	public static int SHOPPOINTS_ELITE_LUCK;
	
	@Property(key = "gameserver.custom.shoppoints.elite.max", defaultValue = "1")
	public static int SHOPPOINTS_ELITE_MAX;
	
	@Property(key = "gameserver.custom.shoppoints.elite.min", defaultValue = "1")
	public static int SHOPPOINTS_ELITE_MIN;
	
	@Property(key = "gameserver.custom.shoppoints.normal.luck", defaultValue = "50")
	public static int SHOPPOINTS_NORMAL_LUCK;
	
	@Property(key = "gameserver.custom.shoppoints.normal.max", defaultValue = "1")
	public static int SHOPPOINTS_NORMAL_MAX;
	
	@Property(key = "gameserver.custom.shoppoints.normal.min", defaultValue = "1")
	public static int SHOPPOINTS_NORMAL_MIN;
	
	@Property(key = "gameserver.custom.shoppoints.npcs", defaultValue = "")
	public static String SHOP_NPC;
	
	public static String[] getNpcShopPoint(){
		String[] npcs = SHOP_NPC.split(";");
		return npcs;
	}
}
