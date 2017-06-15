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
package com.aionemu.gameserver.geoEngine.scene.mesh;

import java.util.BitSet;

import com.aionemu.gameserver.geoEngine.collision.Collidable;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.scene.Geometry;


/**
 * @author MrPoke
 *
 */
public class DoorGeometry extends Geometry {
	BitSet instances = new BitSet();
	
	
	public void setDoorState(int instanceId, boolean state){
		instances.set(instanceId, state);
	}

	@Override
	public int collideWith(Collidable other, CollisionResults results, int instanceId) {
		if (!instances.get(instanceId))
			return 0;
		return super.collideWith(other, results, instanceId);
	}
}
