/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.gameobjects.player;

/**
 *
 * @author xTz
 */
public class ActionItemNpc {

	private int endCondition = 0;
	private int startCondition = 0;
	private int talkDelay = 0;

	public ActionItemNpc() {
	}

	public void setCondition(int startCondition, int endCondition, int talkDelay) {
		this.startCondition = startCondition;
		this.endCondition = endCondition;
		this.talkDelay = talkDelay;
	}

	public int getEndCondition() {
		return endCondition;
	}

	public int getStartCondition() {
		return startCondition;
	}

	public int getTalkDelay() {
		return talkDelay;
	}
}
