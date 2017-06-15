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
package com.aionemu.gameserver.model.gameobjects.player;

/**
 * @author M@xx
 */
public class PlayerPet {

	private int databaseIndex;
	private Player master;
	private int decoration;
	private String name;
	private int petId;

	public PlayerPet() {

	}

	public Player getMaster() {
		return master;
	}

	/**
	 * @return the databaseIndex
	 */
	public int getDatabaseIndex() {
		return databaseIndex;
	}

	/**
	 * @param databaseIndex
	 *          the databaseIndex to set
	 */
	public void setDatabaseIndex(int databaseIndex) {
		this.databaseIndex = databaseIndex;
	}

	/**
	 * @return the petId
	 */
	public int getPetId() {
		return petId;
	}

	/**
	 * @param petId
	 *          the petId to set
	 */
	public void setPetId(int petId) {
		this.petId = petId;
	}

	/**
	 * @return the decoration
	 */
	public int getDecoration() {
		return decoration;
	}

	/**
	 * @param decoration
	 *          the decoration to set
	 */
	public void setDecoration(int decoration) {
		this.decoration = decoration;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *          the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
