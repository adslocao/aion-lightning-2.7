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
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;


/**
 * @author MrPoke
 *
 */
public abstract class MotionDAO implements DAO{

	public abstract void loadMotionList(Player player);

	public abstract boolean storeMotion(int objectId, Motion motion);
	
	public abstract boolean updateMotion(int objectId, Motion motion);

	public abstract boolean deleteMotion(int objectId, int motionId);

	@Override
	public String getClassName() {
		return MotionDAO.class.getName();
	}
}
