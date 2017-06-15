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

import java.util.Calendar;
import java.util.regex.Pattern;

import com.aionemu.commons.configuration.Property;

public class GSConfig {

	/**
	 * Time Zone name (used for event config atm)
	 */	
	@Property(key = "gameserver.timezone", defaultValue = "")
	public static String TIME_ZONE_ID = Calendar.getInstance().getTimeZone().getID();

	/**
	 * Server Country Code
	 */
	@Property(key = "gameserver.country.code", defaultValue = "1")
	public static int SERVER_COUNTRY_CODE;

	/**
	 * Enable chat server connection
	 */
	@Property(key = "gameserver.chatserver.enable", defaultValue = "false")
	public static boolean ENABLE_CHAT_SERVER;

	/**
	 * Server MOTD & Display revision
	 */
	
	@Property(key = "gameserver.rev", defaultValue = "-1")
	public static int SERVER_REV;
	
	@Property(key = "gameserver.motd", defaultValue = "")
	public static String SERVER_MOTD;
	
	@Property(key = "gameserver.revisiondisplay.enable", defaultValue = "false")
	public static boolean SERVER_MOTD_DISPLAYREV;

	/**
	 * Server Mode
	 */
	@Property(key = "gameserver.character.limit.count", defaultValue = "8")
	public static int CHARACTER_LIMIT_COUNT;

	@Property(key = "gameserver.character.factions.mode", defaultValue = "0")
	public static int CHARACTER_FACTIONS_MODE;

	@Property(key = "gameserver.character.create.mode", defaultValue = "0")
	public static int CHARACTER_CREATE_MODE;

	@Property(key = "gameserver.factions.ratiolimit.enable", defaultValue = "false")
	public static boolean FACTIONS_RATIO_LIMITED;

	@Property(key = "gameserver.factions.ratio.value", defaultValue = "50")
	public static int FACTIONS_RATIO_VALUE;

	@Property(key = "gameserver.factions.ratio.level", defaultValue = "10")
	public static int FACTIONS_RATIO_LEVEL;

	@Property(key = "gameserver.factions.ratio.minimum", defaultValue = "50")
	public static int FACTIONS_RATIO_MINIMUM;

	@Property(key = "gameserver.factions.character.create.max.limit.count", defaultValue = "500")
	public static int FACTIONS_CHARACTER_CREATE_MAX_LIMIT_COUNT;

	/**
	 * Server name
	 */
	@Property(key = "gameserver.name", defaultValue = "Aion Lightning")
	public static String SERVER_NAME;

	/**
	 * Character name pattern (checked when character is being created)
	 */
	@Property(key = "gameserver.name.characterpattern", defaultValue = "[a-zA-Z]{2,16}")
	public static Pattern CHAR_NAME_PATTERN;

	/**
	 * Sucurity Enable
	 */
	@Property(key = "gameserver.security.pingcheck.enable", defaultValue = "true")
	public static boolean SECURITY_ENABLE;

	/**
	 * Sucurity Ping interval
	 */
	@Property(key = "gameserver.security.pingcheck.interval", defaultValue = "80")
	public static int PING_INTERVAL;

	/**
	 * Detect player with speed hack
	 */
	@Property(key = "gameserver.speedhack.validator", defaultValue = "false")
	public static boolean SPEEDHACK_VALIDATOR;

	/**
	 * Kicking players with speed hack
	 */
	@Property(key = "gameserver.speedhack.kick", defaultValue = "false")
	public static boolean SPEEDHACK_KICK;

	/**
	 * Enable or Disable Character Passkey
	 */
	@Property(key = "gameserver.security.passkey.enable", defaultValue = "false")
	public static boolean PASSKEY_ENABLE;

	/**
	 * Enter the maximum number of incorrect password set
	 */
	@Property(key = "gameserver.security.passkey.wrong.maxcount", defaultValue = "5")
	public static int PASSKEY_WRONG_MAXCOUNT;

	/**
	 * CAPTCHA service
	 */
	@Property(key = "gameserver.captcha.enable", defaultValue = "true")
	public static boolean CAPTCHA_ENABLE;

	@Property(key = "gameserver.captcha.appear", defaultValue = "OD")
	public static String CAPTCHA_APPEAR;

	@Property(key = "gameserver.captcha.appear.rate", defaultValue = "5")
	public static int CAPTCHA_APPEAR_RATE;

	@Property(key = "gameserver.captcha.extraction.ban.time", defaultValue = "3000")
	public static int CAPTCHA_EXTRACTION_BAN_TIME;

	@Property(key = "gameserver.captcha.extraction.ban.add.time", defaultValue = "600")
	public static int CAPTCHA_EXTRACTION_BAN_ADD_TIME;

	@Property(key = "gameserver.captcha.bonus.fp.time", defaultValue = "5")
	public static int CAPTCHA_BONUS_FP_TIME;

	/**
	 * If true when player buy broker item - will check from base
	 */
	@Property(key = "gameserver.security.broker.prebuy", defaultValue = "true")
	public static boolean BROKER_PREBUY_CHECK;
	
	@Property(key = "gameserver.abyssranking.small.cache", defaultValue = "false")
	public static boolean ABYSSRANKING_SMALL_CACHE;

	@Property(key = "gameserver.validation.flypath", defaultValue = "false")
	public static boolean ENABLE_FLYPATH_VALIDATOR;

	@Property(key = "gameserver.character.reentry.time", defaultValue = "20")
	public static int CHARACTER_REENTRY_TIME;

	@Property(key = "gameserver.x-mas.enable", defaultValue = "false")
	public static boolean XMAS_ENABLE;
}
