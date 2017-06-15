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
package com.aionemu.gameserver.model.templates.flyring;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.utils3d.Point3D;

/**
 * @author M@xx
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlyRing")
public class FlyRingTemplate {

	@XmlAttribute(name = "name")
	protected String name;

	@XmlAttribute(name = "map")
	protected int map;

	@XmlAttribute(name = "radius")
	protected float radius;

	@XmlElement(name = "center")
	protected FlyRingPoint center;

	@XmlElement(name = "p1")
	protected FlyRingPoint p1;

	@XmlElement(name = "p2")
	protected FlyRingPoint p2;

	public String getName() {
		return name;
	}

	public int getMap() {
		return map;
	}

	public float getRadius() {
		return radius;
	}

	public FlyRingPoint getCenter() {
		return center;
	}

	public FlyRingPoint getP1() {
		return p1;
	}

	public FlyRingPoint getP2() {
		return p2;
	}

	public FlyRingTemplate() {
	};

	public FlyRingTemplate(String name, int mapId, Point3D center, Point3D p1, Point3D p2, int radius) {
		this.name = name;
		this.map = mapId;
		this.radius = radius;
		this.center = new FlyRingPoint(center);
		this.p1 = new FlyRingPoint(p1);
		this.p2 = new FlyRingPoint(p2);
	}

}