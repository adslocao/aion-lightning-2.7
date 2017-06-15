/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.model.drop;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer
 */
public class DropItem {

	private int index = 0;
	private long count = 0;
	private Drop dropTemplate;
	private int playerObjId = 0;
	private boolean isFreeForAll = false;
	private long highestValue = 0;
	private Player winningPlayer = null;
	private boolean isItemWonNotCollected = false;
	private boolean isDistributeItem = false;
	private int npcObj;

	public DropItem(Drop dropTemplate) {
		this.dropTemplate = dropTemplate;
	}

	/**
	 * Regenerates item count upon each call // TODO input parameters - based on attacker stats // TODO more precise
	 * calculations (non-linear)
	 */
	public void calculateCount() {
		count = Rnd.get(dropTemplate.getMinAmount(), dropTemplate.getMaxAmount());
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *          the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @param count
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * @return the dropTemplate
	 */
	public Drop getDropTemplate() {
		return dropTemplate;
	}

	/**
	 * @return the playerObjId
	 */
	public int getPlayerObjId() {
		return playerObjId;
	}

	/**
	 * @param playerObjId
	 *          the playerObjId to set
	 */
	public void setPlayerObjId(int playerObjId) {
		this.playerObjId = playerObjId;
	}

	/**
	 * @param isFreeForAll
	 *          to set
	 */
	public void isFreeForAll(boolean isFreeForAll) {
		this.isFreeForAll = isFreeForAll;
	}

	/**
	 * @return isFreeForAll
	 */
	public boolean isFreeForAll() {
		return isFreeForAll;
	}

	/**
	 * @return highestValue
	 */
	public long getHighestValue() {
		return highestValue;
	}

	/**
	 * @param highestValue
	 *          to set
	 */
	public void setHighestValue(long highestValue) {
		this.highestValue = highestValue;
	}

	/**
	 * @param WinningPlayer
	 *          to set
	 */
	public void setWinningPlayer(Player winningPlayer) {
		this.winningPlayer = winningPlayer;

	}

	/**
	 * @return winningPlayer
	 */
	public Player getWinningPlayer() {
		return winningPlayer;
	}

	/**
	 * @param isItemWonNotCollected
	 *          to set
	 */
	public void isItemWonNotCollected(boolean isItemWonNotCollected) {
		this.isItemWonNotCollected = isItemWonNotCollected;
	}

	/**
	 * @return isItemWonNotCollected
	 */
	public boolean isItemWonNotCollected() {
		return isItemWonNotCollected;
	}

	/**
	 * @param isDistributeItem
	 *          to set
	 */
	public void isDistributeItem(boolean isDistributeItem) {
		this.isDistributeItem = isDistributeItem;
	}

	/**
	 * @return isDistributeItem
	 */
	public boolean isDistributeItem() {
		return isDistributeItem;
	}

	public int getNpcObj() {
		return npcObj;
	}

	public void setNpcObj(int npcObj) {
		this.npcObj = npcObj;
	}

}