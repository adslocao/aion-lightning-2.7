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

import com.aionlightning.commons.database.dao.DAO;
import com.aionlightning.loginserver.model.AccountTime;

/**
 * DAO to manage account time
 */
public abstract class AccountTimeDAO implements DAO {

	/**
	 * Updates @link com.aionlightning.loginserver.model.AccountTime data of account
	 * 
	 * @param accountId
	 *          account id
	 * @param accountTime
	 *          account time set
	 * @return was update successfull or not
	 */
	public abstract boolean updateAccountTime(int accountId, AccountTime accountTime);

	/**
	 * Updates @link com.aionlightning.loginserver.model.AccountTime data of account
	 * 
	 * @param accountId
	 * @return AccountTime
	 */
	public abstract AccountTime getAccountTime(int accountId);

	/**
	 * Returns uniquire class name for all implementations
	 * 
	 * @return uniquire class name for all implementations
	 */
	@Override
	public final String getClassName() {
		return AccountTimeDAO.class.getName();
	}

}
