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
package com.aionemu.gameserver.skillengine.effect;


/**
 * @author ATracer
 */
public enum AbnormalState {
	BUFF(0),
	POISON(1),
	BLEED(2),
	PARALYZE(4, 2),
	SLEEP(8),
	ROOT(16), // ?? cannot move ?
	BLIND(32),
	UNKNOWN(64),
	DISEASE(128),
	SILENCE(256),
	FEAR(512), // Fear I
	CURSE(1024),
	CHAOS(2056),
	STUN(4096, 2),
	PETRIFICATION(8192),
	STUMBLE(16384, 1),
	STAGGER(32768, 1),
	OPENAERIAL(65536, 1),
	SNARE(131072),
	SLOW(262144),
	SPIN(524288, 1),
	BIND(1048576),
	DEFORM(2097152), // (Curse of Roots I, Fear I)
	CANNOT_MOVE(4194304, 1), // (Inescapable Judgment I)
	NOFLY(8388608), // cannot fly
	KNOCKBACK(16777216, 1),//simple_root
	HIDE(536870912), // hide 33554432

	/**
	 * Compound abnormal states
	 */
	CANT_ATTACK_STATE(SPIN.id | SLEEP.id | STUN.id | STUMBLE.id | STAGGER.id
		| OPENAERIAL.id | PARALYZE.id | FEAR.id | CANNOT_MOVE.id),
	CANT_MOVE_STATE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id
		| OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id);

	private int id;

	// 0 unknow - 1 physical - 2 magical
	private final int abnormalType;
	
	private AbnormalState(int id) {
		this.id = id;
		abnormalType = 0;
	}

	private AbnormalState(int id, int abnormalType) {
		this.id = id;
		this.abnormalType = abnormalType;
	}
	
	public int getId() {
		return id;
	}

	public static AbnormalState getIdByName(String name) {
		for (AbnormalState id : values()) {
			if (id.name().equals(name))
				return id;
		}
		return null;
	}

	public static AbnormalState getAbnormalStateById(int id) {
		for (AbnormalState abns : values()) {
			if (abns.getId() == id)
				return abns;
		}
		return null;
	}
	
	public boolean isMagical() {
		return abnormalType == 2;
	}
	
	public boolean isPhysical() {
		return abnormalType == 1;
	}
}
