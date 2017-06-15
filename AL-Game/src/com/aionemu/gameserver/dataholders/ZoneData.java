/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.geometry.Area;
import com.aionemu.gameserver.model.geometry.CylinderArea;
import com.aionemu.gameserver.model.geometry.PolyArea;
import com.aionemu.gameserver.model.geometry.SphereArea;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.model.templates.zone.ZoneTemplate;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "zones")
public class ZoneData{
	
	@XmlElement(name = "zone")
	protected List<ZoneTemplate> zoneList;

	@XmlTransient
	private TIntObjectHashMap<List<ZoneInfo>> zoneNameMap = new TIntObjectHashMap<List<ZoneInfo>>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (ZoneTemplate zone : zoneList) {
			Area area = null;
			switch (zone.getAreaType()){
				case POLYGON:
					area = new PolyArea(zone.getName(), zone.getMapid(), zone.getPoints().getPoint(), zone.getPoints().getBottom(),zone.getPoints().getTop());
					break;
				case CYLINDER:
					area = new CylinderArea(zone.getName(), zone.getMapid(), zone.getCylinder().getX(), zone.getCylinder().getY(), zone.getCylinder().getR(), zone.getCylinder().getBottom(), zone.getCylinder().getTop());
					break;
				case SPHERE:
					area = new SphereArea(zone.getName(), zone.getMapid(), zone.getSphare().getX(), zone.getSphare().getY(), zone.getSphare().getZ(), zone.getSphare().getR());
			}
			if(area != null){
				List<ZoneInfo> zones = zoneNameMap.get(zone.getMapid());
				if (zones == null){
					zones = new ArrayList<ZoneInfo>();
					zoneNameMap.put(zone.getMapid(), zones);
				}
				zones.add(new ZoneInfo(area, zone));
			}
		}
	}


	public TIntObjectHashMap<List<ZoneInfo>> getZones() {
		return zoneNameMap;
	}

	public int size() {
		return zoneList.size();
	}
}
