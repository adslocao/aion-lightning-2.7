/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 *
 * @author Dean
 */
public class GlobalityConfig {
    /*
     * 
     *  Bonus for Online!
     */
    @Property(key = "gameserver.global.onlinebonus.enable", defaultValue = "true")
    public static boolean ONLINEBONUS_ENABLE;
    
    @Property(key = "gameserver.global.onlinebonus.item", defaultValue = "186000127")
    public static int BONUS_ITEM;
    
    @Property(key = "gameserver.global.onlinebonus.count", defaultValue = "1")
    public static int BONUS_COUNT;
    
    @Property(key = "gameserver.global.onlinebonus.time", defaultValue = "5")
    public static int BONUS_TIME;
    
    @Property(key = "gameserver.global.onlinebonus.membership", defaultValue = "0")
    public static int BONUS_MEMBERSHIP;
    
    @Property(key = "gameserver.global.onlinebonus.membership", defaultValue = "false")
    public static boolean BONUS_MEMBERSHIP_RATE;
    
    @Property(key = "gameserver.global.en", defaultValue = "en")
    public static String GLOBAL_MESSAGES_LANG;
    
    @Property(key = "gameserver.global.awakeminlvl", defaultValue = "50")
    public static int AWAKENING_MINLVL;
    
   @Property(key = "gameserver.global.levelupsurvey", defaultValue = "true")
    public static boolean LEVELUP_SURVEYS_ENABLE;
}
