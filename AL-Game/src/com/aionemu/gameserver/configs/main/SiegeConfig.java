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

/**
 * @author Sarynth, xTz, Source
 */
public class SiegeConfig {

	/**
	 * Siege Enabled
	 */
	@Property(key = "gameserver.siege.enable", defaultValue = "true")
	public static boolean SIEGE_ENABLED;

	/**
	 * Siege Location Values
	 */
	@Property(key = "gameserver.siege.influence.fortress", defaultValue = "8")
	public static int SIEGE_POINTS_FORTRESS;

	@Property(key = "gameserver.siege.influence.artifact", defaultValue = "1")
	public static int SIEGE_POINTS_ARTIFACT;

	/**
	 * Siege Reward Rate
	 */
	@Property(key = "gameserver.siege.medal.rate", defaultValue = "1")
	public static int SIEGE_MEDAL_RATE;

	/**
	 * Siege sield Enabled
	 */
	@Property(key = "gameserver.siege.shield.enable", defaultValue = "true")
	public static boolean SIEGE_SHIELD_ENABLED;

	/**
	 * Balaur Assaults Enabled
	 */
	@Property(key = "gameserver.siege.assault.enable", defaultValue = "false")
	public static boolean BALAUR_AUTO_ASSAULT;
	@Property(key = "gameserver.siege.assault.balaurea.enable", defaultValue = "true")
	public static boolean BALAUR_AUTO_ASSAULT_BALAUREA;
	@Property(key = "gameserver.siege.assault.rate", defaultValue = "1")
	public static float BALAUR_ASSAULT_RATE;
	@Property(key = "gameserver.siege.assault.speed.rate", defaultValue = "1")
	public static float BALAUR_ASSAULT_SPEED_RATE;
}
