/**
 * This file is part of aion-lightning <aion-lightning.org>.
 * 
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.loginserver.taskmanager.handler.implementations;

import com.aionlightning.loginserver.Shutdown;
import com.aionlightning.loginserver.taskmanager.handler.TaskFromDBHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Divinity, nrg
 */
public class RestartHandler extends TaskFromDBHandler {

	private static final Logger log = LoggerFactory.getLogger(RestartHandler.class);

	@Override
	public void trigger() {
		log.info("Task[" + taskId + "] launched : restarting the server !");

		Shutdown shutdown = Shutdown.getInstance();
		shutdown.setRestartOnly(true);
		shutdown.start();
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
