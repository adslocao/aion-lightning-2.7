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
package com.aionemu.gameserver.controllers.observer;

/**
 * @author ATracer
 */
public enum ObserverType {
	MOVE(1),
	ATTACK(1 << 1),
	ATTACKED(1 << 2),
	EQUIP(1 << 3),
	UNEQUIP(1 << 4),
	SKILLUSE(1 << 5),
	DEATH(1 << 6),
	DOT_ATTACKED(1 << 7),
	ITEMUSE(1 << 8),
	NPCDIALOGREQUEST(1 << 9),
	EQUIP_UNEQUIP(EQUIP.observerMask | UNEQUIP.observerMask),
	ATTACK_DEFEND(ATTACK.observerMask | ATTACKED.observerMask),
	ALL(MOVE.observerMask | ATTACK.observerMask | ATTACKED.observerMask | SKILLUSE.observerMask | DEATH.observerMask | DOT_ATTACKED.observerMask | ITEMUSE.observerMask | NPCDIALOGREQUEST.observerMask);

	private int observerMask;

	private ObserverType(int observerMask) {
		this.observerMask = observerMask;
	}

	public boolean matchesObserver(ObserverType observerType) {
		return (observerType.observerMask & observerMask) == observerType.observerMask;
	}
}
