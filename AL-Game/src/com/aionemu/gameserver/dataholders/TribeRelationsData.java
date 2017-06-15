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

import gnu.trove.map.hash.THashMap;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.templates.tribe.Tribe;

/**
 * @author ATracer
 */
@XmlRootElement(name = "tribe_relations")
@XmlAccessorType(XmlAccessType.FIELD)
public class TribeRelationsData {

	@XmlElement(name = "tribe", required = true)
	protected List<Tribe> tribeList;

	protected THashMap<TribeClass, Tribe> tribeNameMap = new THashMap<TribeClass, Tribe>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (Tribe tribe : tribeList) {
			tribeNameMap.put(tribe.getName(), tribe);
		}
		tribeList = null;
	}

	/**
	 * @return tribeNameMap.size()
	 */
	public int size() {
		return tribeNameMap.size();
	}

	/**
	 * @param tribeName
	 * @return
	 */
	public boolean hasAggressiveRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		return !tribe.getAggro().isEmpty();
	}

	/**
	 * @param tribeName
	 * @return
	 */
	public boolean hasHostileRelations(TribeClass tribeName) {
		Tribe tribe = tribeNameMap.get(tribeName);
		if (tribe == null)
			return false;
		return !tribe.getHostile().isEmpty();
	}

	/**
	 * @param tribeName1
	 * @param tribeName2
	 * @return
	 */
	public boolean isAggressiveRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		if (isFriendlyRelation(tribeName1, tribeName2))
			return false;
		return tribe1.getAggro().contains(tribeName2) || tribe2.isBasic() && tribe1.getAggro().contains(tribe2.getBase());
	}

	/**
	 * @param tribeName1
	 * @param tribeName2
	 * @return
	 */
	public boolean isSupportRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getSupport().contains(tribeName2) || tribe2.isBasic() && tribe1.getAggro().contains(tribe2.getBase());
	}

	/**
	 * @param tribeName1
	 * @param tribeName2
	 * @return
	 */
	public boolean isFriendlyRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getFriend().contains(tribeName2) || tribe2.isBasic() && tribe1.getFriend().contains(tribe2.getBase());
	}

	/**
	 * @param tribeName1
	 * @param tribeName2
	 * @return
	 */
	public boolean isNeutralRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getNeutral().contains(tribeName2) || tribe2.isBasic() && tribe1.getNeutral().contains(tribe2.getBase());
	}
	
	/**
	 * @param tribeName1
	 * @param tribeName2
	 * @return
	 */
	public boolean isNoneRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getNone().contains(tribeName2) || tribe2.isBasic() && tribe1.getNone().contains(tribe2.getBase());
	}

	/**
	 * @param tribeName1
	 * @param tribeName2
	 * @return
	 */
	public boolean isHostileRelation(TribeClass tribeName1, TribeClass tribeName2) {
		Tribe tribe1 = tribeNameMap.get(tribeName1);
		Tribe tribe2 = tribeNameMap.get(tribeName2);
		if (tribe1 == null || tribe2 == null)
			return false;
		return tribe1.getHostile().contains(tribeName2) || tribe2.isBasic() && tribe1.getHostile().contains(tribe2.getBase());
	}
}
