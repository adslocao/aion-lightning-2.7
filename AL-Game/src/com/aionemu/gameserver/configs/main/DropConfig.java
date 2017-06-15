/*
 * This file is part of aion-unique <aion-unique.org>.
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
 * @author Tiger0319
 */
public class DropConfig {

	/**
	 * Disable drop rate reduction based on level diference between players and mobs
	 */
	@Property(key = "gameserver.drop.reduction.disable", defaultValue = "false")
	public static boolean DISABLE_DROP_REDUCTION;

	/**
	 * Enable announce when a player drops Unique / Epic item
	 */
	@Property(key = "gameserver.unique.drop.announce.enable", defaultValue = "true")
	public static boolean ENABLE_UNIQUE_DROP_ANNOUNCE;
}
