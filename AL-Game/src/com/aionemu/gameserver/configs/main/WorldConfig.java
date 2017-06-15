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
 * @author ATracer
 */
public class WorldConfig {

	/**
	 * World region size
	 */
	@Property(key = "gameserver.world.region.size", defaultValue = "128")
	public static int WORLD_REGION_SIZE;

	/**
	 * Trace active regions and deactivate inactive
	 */
	@Property(key = "gameserver.world.region.active.trace", defaultValue = "true")
	public static boolean WORLD_ACTIVE_TRACE;
}
