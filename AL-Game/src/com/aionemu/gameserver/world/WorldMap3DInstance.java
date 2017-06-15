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
package com.aionemu.gameserver.world;

import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * @author ATracer
 */
public class WorldMap3DInstance extends WorldMapInstance {

	/**
	 * @param parent
	 * @param instanceId
	 */
	public WorldMap3DInstance(WorldMap parent, int instanceId) {
		super(parent, instanceId);
	}

	@Override
	public MapRegion getRegion(float x, float y, float z) {
		int regionId = RegionUtil.get3dRegionId(x, y, z);
		return regions.get(regionId);
	}

	protected void initMapRegions() {
		int size = this.getParent().getWorldSize();
		// Create all mapRegion
		for (int x = 0; x <= size; x = x + regionSize) {
			for (int y = 0; y <= size; y = y + regionSize) {
				for (int z = 0; z <= size; z = z + regionSize) {
				int regionId = RegionUtil.get3dRegionId(x, y, z);
				regions.put(regionId, createMapRegion(regionId));
			}
		}}

		// Add Neighbour
		for (int x = 0; x <= size; x = x + regionSize) {
			for (int y = 0; y <= size; y = y + regionSize) {
				for (int z = 0; z <= size; z = z + regionSize) {
					int regionId = RegionUtil.get3dRegionId(x, y, z);
					MapRegion mapRegion = regions.get(regionId);
					for (int x2 = x - regionSize; x2 <= x + regionSize; x2+=regionSize) {
						for (int y2 = y - regionSize; y2 <= y + regionSize; y2+=regionSize) {
							for (int z2 = z - regionSize; z2 <= z + regionSize; z2+=regionSize) {
								if (x2 == x && y2 == y && z2 == z)
									continue;
								int neighbourId = RegionUtil.get3dRegionId(x2, y2, z2);
								MapRegion neighbour = regions.get(neighbourId);
								if (neighbour != null)
									mapRegion.addNeighbourRegion(neighbour);
							}
						}
					}
				}
			}
		}
	}

	@Override
	protected MapRegion createMapRegion(int regionId) {
		float startX = RegionUtil.getXFrom3dRegionId(regionId);
		float startY = RegionUtil.getYFrom3dRegionId(regionId);
		float startZ = RegionUtil.getZFrom3dRegionId(regionId);
		ZoneInstance[] zones = filterZones(this.getMapId(), regionId, startX, startY, startZ, startZ
			+ WorldConfig.WORLD_REGION_SIZE);
		return new MapRegion(regionId, this, zones);
	}
}
