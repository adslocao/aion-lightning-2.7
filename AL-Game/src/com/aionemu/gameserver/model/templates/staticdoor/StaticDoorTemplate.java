/*
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

package com.aionemu.gameserver.model.templates.staticdoor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;

/**
 * @author Wakizashi
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaticDoor")
public class StaticDoorTemplate extends VisibleObjectTemplate {

	@XmlAttribute
	protected Float x;
	@XmlAttribute
	protected Float y;
	@XmlAttribute
	protected Float z;
	@XmlAttribute(name = "doorid")
	protected int doorId;
	@XmlAttribute(name = "keyid")
	protected int keyId;

	public Float getX() {
		return x;
	}

	public Float getY() {
		return y;
	}

	public Float getZ() {
		return z;
	}

	/**
	 * @return the doorId
	 */
	public int getDoorId() {
		return doorId;
	}

	/**
	 * @return the keyItem
	 */
	public int getKeyId() {
		return keyId;
	}

	@Override
	public int getTemplateId() {
		return 300001;
	}

	@Override
	public String getName() {
		return "door";
	}

	@Override
	public int getNameId() {
		return 0;
	}
}
