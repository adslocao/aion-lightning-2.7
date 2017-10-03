/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CustomConfig {

	@Property(key = "gameserver.phx.speedhack", defaultValue = "false")
	public static boolean PHX_SPEEDHACK;

	@Property(key = "gameserver.phx.speedhack.power", defaultValue = "41")
	public static int PHX_SPEEDHACK_POWER;

	@Property(key = "gameserver.phx.speedhack.punish", defaultValue = "1")
	public static int PHX_SPEEDHACK_PUNISH = 0;

	/**
	 * Show premium account details on login
	 */
	@Property(key = "gameserver.premium.notify", defaultValue = "false")
	public static boolean PREMIUM_NOTIFY;

	/**
	 * Enable announce when a player succes enchant item 15
	 */
	@Property(key = "gameserver.enchant.announce.enable", defaultValue = "true")
	public static boolean ENABLE_ENCHANT_ANNOUNCE;

	/**
	 * Enable speaking between factions
	 */
	@Property(key = "gameserver.chat.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_BETWEEN_FACTIONS;

	/**
	 * Minimum level to use whisper
	 */
	@Property(key = "gameserver.chat.whisper.level", defaultValue = "10")
	public static int LEVEL_TO_WHISPER;

	/**
	 * Factions search mode
	 */
	@Property(key = "gameserver.search.factions.mode", defaultValue = "false")
	public static boolean FACTIONS_SEARCH_MODE;

	/**
	 * list gm when search players
	 */
	@Property(key = "gameserver.search.gm.list", defaultValue = "false")
	public static boolean SEARCH_GM_LIST;

	/**
	 * Minimum level to use search
	 */
	@Property(key = "gameserver.search.player.level", defaultValue = "10")
	public static int LEVEL_TO_SEARCH;

	/**
	 * Allow opposite factions to bind in enemy territories
	 */
	@Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
	public static boolean ENABLE_CROSS_FACTION_BINDING;

	/**
	 * Enable second class change without quest
	 */
	@Property(key = "gameserver.simple.secondclass.enable", defaultValue = "false")
	public static boolean ENABLE_SIMPLE_2NDCLASS;

	/**
	 * Disable chain trigger rate (chain skill with 100% success)
	 */
	@Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
	public static boolean SKILL_CHAIN_TRIGGERRATE;

	/**
	 * Unstuck delay
	 */
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int UNSTUCK_DELAY;

	/**
	 * The price for using dye command
	 */
	@Property(key = "gameserver.admin.dye.price", defaultValue = "1000000")
	public static int DYE_PRICE;

	/**
	 * Base Fly Time
	 */
	@Property(key = "gameserver.base.flytime", defaultValue = "60")
	public static int BASE_FLYTIME;

	/**
	 * Disable prevention using old names with coupon & command
	 */
	@Property(key = "gameserver.oldnames.coupon.disable", defaultValue = "false")
	public static boolean OLD_NAMES_COUPON_DISABLED;
	@Property(key = "gameserver.oldnames.command.disable", defaultValue = "true")
	public static boolean OLD_NAMES_COMMAND_DISABLED;

	/**
	 * Friendlist size
	 */
	@Property(key = "gameserver.friendlist.size", defaultValue = "90")
	public static int FRIENDLIST_SIZE;

	/**
	 * Basic Quest limit size
	 */
	@Property(key = "gameserver.basic.questsize.limit", defaultValue = "40")
	public static int BASIC_QUEST_SIZE_LIMIT;

	/**
	 * Basic Quest limit size
	 */
	@Property(key = "gameserver.basic.cubesize.limit", defaultValue = "9")
	public static int BASIC_CUBE_SIZE_LIMIT;
	
	 /**
	 * Npc Cube Expands limit size
	 */
	@Property(key = "gameserver.npcexpands.limit", defaultValue = "5")
	public static int NPC_CUBE_EXPANDS_SIZE_LIMIT;

	/**
	 * Enable instances
	 */
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean ENABLE_INSTANCES;

	/**
	 * Enable instances mob always aggro player ignore level
	 */
	@Property(key = "gameserver.instances.mob.aggro", defaultValue = "300080000,300090000,300060000")
	public static String INSTANCES_MOB_AGGRO;

	/**
	 * Enable instances cooldown filtring
	 */
	@Property(key = "gameserver.instances.cooldown.filter", defaultValue = "0")
	public static String INSTANCES_COOL_DOWN_FILTER;

	/**
	 * Instances formula
	 */
	@Property(key = "gameserver.instances.cooldown.rate", defaultValue = "1")
	public static int INSTANCES_RATE;

	/**
	 * Enable Kinah cap
	 */
	@Property(key = "gameserver.enable.kinah.cap", defaultValue = "false")
	public static boolean ENABLE_KINAH_CAP;

	/**
	 * Kinah cap value
	 */
	@Property(key = "gameserver.kinah.cap.value", defaultValue = "999999999")
	public static long KINAH_CAP_VALUE;

	/**
	 * Enable AP cap
	 */
	@Property(key = "gameserver.enable.ap.cap", defaultValue = "false")
	public static boolean ENABLE_AP_CAP;

	/**
	 * AP cap value
	 */
	@Property(key = "gameserver.ap.cap.value", defaultValue = "1000000")
	public static long AP_CAP_VALUE;

	/**
	 * Enable no AP in mentored group.
	 */
	@Property(key = "gameserver.noap.mentor.group", defaultValue = "false")
	public static boolean MENTOR_GROUP_AP;

	/**
	 * .faction cfg
	 */
	@Property(key = "gameserver.faction.free", defaultValue = "true")
	public static boolean FACTION_FREE_USE;

	@Property(key = "gameserver.faction.prices", defaultValue = "10000")
	public static int FACTION_USE_PRICE;
	
	@Property(key = "gameserver.faction.cmdchannel", defaultValue = "true")
	public static boolean FACTION_CMD_CHANNEL;

	/**
	 * Time in milliseconds in which players are limited for killing one player
	 */
	@Property(key = "gameserver.pvp.dayduration", defaultValue = "86400000")
	public static long PVP_DAY_DURATION;
	
	/**
	 * Allowed Kills in configuered time for full AP. Move to separate config when more pvp options.
	 */
	@Property(key = "gameserver.pvp.maxkills", defaultValue = "5")
	public static int MAX_DAILY_PVP_KILLS;

	/**
	 * Add a reward to player for pvp kills
	 */
	@Property(key = "gameserver.kill.reward.enable", defaultValue = "false")
	public static boolean ENABLE_KILL_REWARD;

	/**
	 * Kills needed for item reward
	 */
	@Property(key = "gameserver.kills.needed1", defaultValue = "5")
	public static int KILLS_NEEDED1;
	@Property(key = "gameserver.kills.needed2", defaultValue = "10")
	public static int KILLS_NEEDED2;
	@Property(key = "gameserver.kills.needed3", defaultValue = "15")
	public static int KILLS_NEEDED3;

	/**
	 * Item Rewards
	 */
	@Property(key = "gameserver.item.reward1", defaultValue = "186000031")
	public static int REWARD1;
	@Property(key = "gameserver.item.reward2", defaultValue = "186000030")
	public static int REWARD2;
	@Property(key = "gameserver.item.reward3", defaultValue = "186000096")
	public static int REWARD3;

	/**
	 * Show dialog id and quest id
	 */
	@Property(key = "gameserver.dialog.showid", defaultValue = "true")
	public static boolean ENABLE_SHOW_DIALOGID;

	/**
	 * Custom RiftLevels for Heiron and Beluslan
	 */
	@Property(key = "gameserver.rift.heiron_fm", defaultValue = "50")
	public static int HEIRON_FM;
	@Property(key = "gameserver.rift.heiron_gm", defaultValue = "50")
	public static int HEIRON_GM;
	@Property(key = "gameserver.rift.beluslan_fm", defaultValue = "50")
	public static int BELUSLAN_FM;
	@Property(key = "gameserver.rift.beluslan_gm", defaultValue = "50")
	public static int BELUSLAN_GM;

	@Property(key = "gameserver.survey.delay.minute", defaultValue = "20")
	public static int SURVEY_DELAY;

	@Property(key = "gameserver.reward.service.enable", defaultValue = "false")
	public static boolean ENABLE_REWARD_SERVICE;
	
	/**
	 * Flood Protection
	 */
	@Property(key = "gameserver.flood.delay", defaultValue = "1")
	public static int FLOOD_DELAY;

	@Property(key = "gameserver.flood.msg", defaultValue = "6")
	public static int FLOOD_MSG;

	/**
	 * Limits Config
	 */
	@Property(key = "gameserver.limits.enable", defaultValue = "true")
	public static boolean LIMITS_ENABLED;

	@Property(key = "gameserver.limits.update", defaultValue = "0 0 0 * * ?")
	public static String LIMITS_UPDATE;

	@Property(key = "gameserver.limits.sell", defaultValue = "12900000")
	public static long LIMITS_SELL;

	@Property(key = "gameserver.gmaudit.message.broadcast", defaultValue = "false")
	public static boolean GM_AUDIT_MESSAGE_BROADCAST;

	@Property(key = "gameserver.chat.text.length", defaultValue = "150")
	public static int MAX_CHAT_TEXT_LENGHT;
	
	@Property(key = "gameserver.instance.keycheck", defaultValue = "false")
	public static boolean INSTANCE_KEYCHECK;
	
	@Property(key = "gameserver.pff.enable", defaultValue = "false")
	public static boolean PFF_ENABLE;
	
	@Property(key = "gameserver.pff.level", defaultValue = "1")
	public static int PFF_LEVEL;

	@Property(key = "gameserver.autoassault.enable", defaultValue = "false")
	public static boolean AUTO_ASSAULT;
	
	@Property(key = "gameserver.abyssxform.afterlogout", defaultValue = "false")
	public static boolean ABYSSXFORM_LOGOUT;
	
	@Property(key = "gameserver.boost.ap.newplayer", defaultValue = "false")
	public static boolean BOOST_AP_NEW_PLAYER;
	
	@Property(key = "gameserver.boost.ap.newplayertime", defaultValue = "30")
	public static long BOOST_AP_NEW_PLAYER_TIME;
	
	@Property(key = "gameserver.boost.ap.newplayerRatio", defaultValue = "2")
	public static long BOOST_AP_NEW_PLAYER_RATIO;
	
	@Property(key = "gameserver.enchant.incant.time", defaultValue = "5000")
	public static int ENCHANT_INCANT_TIME;
	
	/**
	 * Bonus faction
	 */
	@Property(key = "gameserver.faction.bonus.applyto", defaultValue = "BALAUR")
	public static String FACTION_BONUS_TO;
	
	@Property(key = "gameserver.faction.bonus.exp.hunt", defaultValue = "1.5")
	public static float FACTION_BONUS_HUNT;
	
	@Property(key = "gameserver.faction.bonus.exp.quest", defaultValue = "1.1")
	public static float FACTION_BONUS_QUEST;
	
	@Property(key = "gameserver.faction.bonus.exp.craft", defaultValue = "1.5")
	public static float FACTION_BONUS_CRAFT;
	
	@Property(key = "gameserver.faction.bonus.exp.gather", defaultValue = "1.5")
	public static float FACTION_BONUS_GATHER;
	
	@Property(key = "gameserver.faction.bonus.pvp.ap", defaultValue = "2.0")
	public static float FACTION_BONUS_AP;
	
	@Property(key = "gameserver.faction.bonus.pvp.defense", defaultValue = "1.1")
	public static float FACTION_BONUS_DEFENSE;
	
	@Property(key = "gameserver.faction.bonus.pvp.attack", defaultValue = "1.1")
	public static float FACTION_BONUS_ATTACK;
	
	/**
	 * Other customs
	 */
	@Property(key = "gameserver.toll.name.en", defaultValue = "Shop point")
	public static String TOLL_NAME_EN;
	
	@Property(key = "gameserver.toll.name.fr", defaultValue = "Point boutique")
	public static String TOLL_NAME_FR;
	
	@Property(key = "gameserver.toll.exchange.price", defaultValue = "500000")
	public static int TOLL_EXCHANGE_PRICE;
	
	@Property(key = "gameserver.gs.disableeffect", defaultValue = "false")
	public static boolean DISABLE_GS_EFFECT;
	
	@Property(key = "gameserver.gs.securisedeffect", defaultValue = "false")
	public static boolean GS_SECURISED_EFFECT;

	@Property(key = "gameserver.ai.balaurea.fountains.enable", defaultValue = "false")
	public static boolean ENABLE_BALAUREA_FOUNTAIN_AI;
	
	@Property(key = "gameserver.rnd.fountain.getplatinum", defaultValue = "90")
	public static int FOUNTAIN_PLATINUM;
	
	@Property(key = "gameserver.rnd.fountain.getgold", defaultValue = "55")
	public static int FOUNTAIN_GOLD;
	
	@Property(key = "gameserver.rnd.fountain.getrusted", defaultValue = "0")
	public static int FOUNTAIN_RUSTED;
	
	@Property(key = "gameserver.stats.physical.disableuseofarrow", defaultValue = "false")
	public static boolean DISABLE_USE_OF_ARROW;
}
