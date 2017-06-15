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
package com.aionlightning.loginserver.taskmanager.trigger.implementations;

import com.aionlightning.commons.network.util.ThreadPoolManager;
import com.aionlightning.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfterRestartTrigger extends TaskFromDBTrigger
{
	private static Logger log = LoggerFactory.getLogger(AfterRestartTrigger.class);
	
	private boolean isBlocking = false;
	
	@Override
	public boolean isValidTrigger()
	{
		if (params.length == 1)
		{
			try
			{
				isBlocking = Boolean.parseBoolean(this.params[0]);
				return true;
			}
			catch (Exception e)
			{
				log.warn("A time for FixedInTimeTrigger is missing or invalid", e);
			}
		}
		log.warn("Not exact 1 parameter for AfterRestartTrigger received, task is not registered");
		return false;
	}

	@Override
	public void initTrigger()
	{
		if (!isBlocking)
		{
			ThreadPoolManager.getInstance().schedule(this, 5000);
		}
		else
		{
			this.run();
		}
	}
}
