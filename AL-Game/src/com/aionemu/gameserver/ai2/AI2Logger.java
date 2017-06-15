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
package com.aionemu.gameserver.ai2;

import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.model.gameobjects.Creature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public class AI2Logger {

	private static final Logger log = LoggerFactory.getLogger(AI2Logger.class);

	public static void info(AbstractAI ai, String message) {
		if (ai.isLogging()) {
			log.info("[AI2] " + ai.getOwner().getObjectId() + " - " + message);
		}
	}

	public static void info(AI2 ai, String message) {
		info((AbstractAI) ai, message);
	}

	/**
	 * @param owner
	 * @param message
	 */
	public static void moveinfo(Creature owner, String message) {
		if (AIConfig.MOVE_DEBUG && owner.getAi2().isLogging()) {
			log.info("[AI2] " + owner.getObjectId() + " - " + message);
		}
	}
}
