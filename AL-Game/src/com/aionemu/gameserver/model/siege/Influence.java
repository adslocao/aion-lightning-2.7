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
package com.aionemu.gameserver.model.siege;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INFLUENCE_RATIO;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import java.util.Iterator;

/**
 * Calculates fortresses as 10 points and artifacts as 1 point each. Need to find retail calculation. (Upper forts worth
 * more...)
 * 
 * @author Sarynth
 */
public class Influence {

	private static final Influence instance = new Influence();

	private float elyos = 0;
	private float asmos = 0;
	private float balaur = 0;

	private Influence() {
		calculateInfluence();
	}

	public static Influence getInstance() {
		return instance;
	}

	/**
	 * Recalculates Influence and Broadcasts new values
	 */
	public void recalculateInfluence() {
		calculateInfluence();

		broadcastInfluencePacket();
	}

	/**
	 * calculate influence
	 */
	private void calculateInfluence() {
		int total = 0;
		int asmos = 0;
		int elyos = 0;
		int balaur = 0;

		for (SiegeLocation sLoc : SiegeService.getInstance().getSiegeLocations().values()) {
			// TODO: Better formula...
			total += sLoc.getInfluenceValue();
			switch (sLoc.getRace()) {
				case BALAUR:
					balaur += sLoc.getInfluenceValue();
					break;
				case ASMODIANS:
					asmos += sLoc.getInfluenceValue();
					break;
				case ELYOS:
					elyos += sLoc.getInfluenceValue();
					break;
			}
		}

		this.balaur = (float) balaur / total;
		this.elyos = (float) elyos / total;
		this.asmos = (float) asmos / total;
	}

	/**
	 * Broadcast packet with influence update to all players. - Responsible for the message
	 * "The Divine Fortress is now vulnerable."
	 */
	private void broadcastInfluencePacket() {
		SM_INFLUENCE_RATIO pkt = new SM_INFLUENCE_RATIO();

		Player player;
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		while (iter.hasNext()) {
			player = iter.next();
			PacketSendUtility.sendPacket(player, pkt);
		}
	}

	/**
	 * @return elyos control
	 */
	public float getElyos() {
		return this.elyos;
	}

	/**
	 * @return asmos control
	 */
	public float getAsmos() {
		return this.asmos;
	}

	/**
	 * @return balaur control
	 */
	public float getBalaur() {
		return this.balaur;
	}
	
	/**
	 * @return float containing dmg modifier for disadvantaged race
	 */
	public float getPvpRaceBonus(Race attRace) {
		float bonus = 1;
		float elyos = getElyos();
		float asmo = getAsmos();
		switch (attRace) {
			case ASMODIANS:
					if (elyos >= 0.81f && asmo <= 0.10f)
						bonus = 1.2f;
					else if (elyos >= 0.81f || (elyos >= 0.71f && asmo <= 0.10f))
						bonus = 1.15f;
					else if(elyos >= 0.71f)
						bonus = 1.1f;
					break;
			case ELYOS:
					if (asmo >= 0.81f && elyos <= 0.10f)
						bonus = 1.2f;
					else if (asmo >= 0.81f || (asmo >= 0.71f && elyos <= 0.10f))
						bonus = 1.15f;
					else if(asmo >= 0.71f)
						bonus = 1.1f;
					break;
		}
		return bonus;
	}
}
