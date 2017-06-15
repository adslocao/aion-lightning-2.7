/*
 *  This file is part of Zetta-Core Engine <http://www.zetta-core.org>.
 *
 *  Zetta-Core is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License,
 *  or (at your option) any later version.
 *
 *  Zetta-Core is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a  copy  of the GNU General Public License
 *  along with Zetta-Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.shield.Shield;
import com.aionemu.gameserver.model.templates.shield.ShieldTemplate;

/**
 * @author xavier
 */
public class ShieldService {

	Logger log = LoggerFactory.getLogger(ShieldService.class);

	private static class SingletonHolder {

		protected static final ShieldService instance = new ShieldService();
	}

	public static final ShieldService getInstance() {
		return SingletonHolder.instance;
	}

	private ShieldService() {
		for (ShieldTemplate t : DataManager.SHIELD_DATA.getShieldTemplates()) {
			Shield f = new Shield(t);
			f.spawn();
			log.debug("Added " + f.getName() + " at m=" + f.getWorldId() + ",x=" + f.getX() + ",y=" + f.getY() + ",z="
				+ f.getZ());
		}
	}
}
