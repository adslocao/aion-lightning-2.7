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
package com.aionlightning.loginserver.dao;

import java.util.ArrayList;

import com.aionlightning.commons.database.dao.DAO;
import com.aionlightning.loginserver.taskmanager.trigger.TaskFromDBTrigger;

/**
 * @author Divinity, nrg
 */
public abstract class TaskFromDBDAO implements DAO {

	/**
	 * Return all tasks from DB
	 * 
	 * @return all tasks
	 */
	public abstract ArrayList<TaskFromDBTrigger> getAllTasks();

	/**
	 * Returns class name that will be uses as unique identifier for all DAO classes
	 * 
	 * @return class name
	 */
	@Override
	public final String getClassName() {
		return TaskFromDBDAO.class.getName();
	}
}
