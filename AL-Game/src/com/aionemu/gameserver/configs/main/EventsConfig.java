/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * @author Rolandas
 */
public class EventsConfig {

	/**
	 * Event Enabled
	 */
	@Property(key = "gameserver.event.enable", defaultValue = "false")
	public static boolean EVENT_ENABLED;

	/**
	 * Event Rewarding Membership
	 */
	@Property(key = "gameserver.event.membership", defaultValue = "0")
	public static int EVENT_REWARD_MEMBERSHIP;

	@Property(key = "gameserver.event.membership.rate", defaultValue = "false")
	public static boolean EVENT_REWARD_MEMBERSHIP_RATE;

	/**
	 * Event Rewarding Period
	 */
	@Property(key = "gameserver.event.period", defaultValue = "60")
	public static int EVENT_PERIOD;

	/**
	 * Event Reward Values
	 */
	@Property(key = "gameserver.event.item", defaultValue = "141000001")
	public static int EVENT_ITEM;
	
	@Property(key = "gameserver.events.givejuice", defaultValue = "160009017")
	public static int EVENT_GIVEJUICE;
	
	@Property(key = "gameserver.events.givecake", defaultValue = "160010073")
	public static int EVENT_GIVECAKE;

	@Property(key = "gameserver.event.count", defaultValue = "1")
	public static int EVENT_ITEM_COUNT;

	@Property(key = "gameserver.event.service.enable", defaultValue = "false")
	public static boolean ENABLE_EVENT_SERVICE;
	
	/**
	 *TvT Event configuration
	 */
	@Property(key = "gameserver.tvtevent.enable", defaultValue = "true")
	public static boolean TVT_ENABLE;
	@Property(key = "gameserver.tvtevent.min.players", defaultValue = "6")
	public static int TVT_MIN_PLAYERS;
	@Property(key = "gameserver.tvtevent.skill.use", defaultValue = "9833")
	public static int TVT_SKILL_USE;
        
        @Property(key = "gameserver.tvtevent.winner.reward", defaultValue = "188051136")
        public static int TVT_WINNER_REWARD;
        @Property(key = "gameserver.tvtevent.winner.dublereward", defaultValue = "188051135")
        public static int TVT_WINNER_DOUBLEREWARD;
        @Property(key = "gameserver.tvtevent.winner.number", defaultValue = "1")
        public static int TVT_WINNER_NUMBER;

}
