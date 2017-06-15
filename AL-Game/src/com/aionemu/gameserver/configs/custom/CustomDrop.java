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
	 * Only id of NPC, chance will be calculated in service
	 */
	@Property(key = "gameserver.custom.shoppoints.npcs", defaultValue = "")
	public static String SHOP_NPC;
	
	public static String[] getNpcShopPoint(){
		String[] npcs = SHOP_NPC.split(";");
		return npcs;
	}
}
