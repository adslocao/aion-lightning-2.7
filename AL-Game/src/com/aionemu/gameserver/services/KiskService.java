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
package com.aionemu.gameserver.services;

import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SET_BIND_POINT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.container.KiskContainer;

/**
 * @author Sarynth
 */
public class KiskService {

	private static final KiskContainer kiskContainer = new KiskContainer();

	/**
	 * @param player
	 * @return kisk
	 */
	public Kisk getKisk(Player player) {
		return kiskContainer.get(player);
	}

	/**
	 * Remove kisk references and containers.
	 * 
	 * @param kisk
	 */
	public static void removeKisk(Kisk kisk) {
		for (Player member : kisk.getCurrentMemberList()) {
			kiskContainer.remove(member);
			PacketSendUtility.sendPacket(member, new SM_SET_BIND_POINT(0, 0f, 0f, 0f, member));
			member.setKisk(null);
			if (member.getLifeStats().isAlreadyDead())
				member.getController().sendDie();
		}
	}

	/**
	 * @param kisk
	 * @param player
	 */
	public static void onBind(Kisk kisk, Player player) {
		if(kisk.isInsideZoneType(ZoneType.SIEGE)){
			return;
		}
		
		if (player.getKisk() != null) {
			kiskContainer.remove(player);
			player.getKisk().removePlayer(player);
		}

		kiskContainer.add(kisk, player);
		kisk.addPlayer(player);

		// Send Bind Point Data
		TeleportService.sendSetBindPoint(player);

		// Send System Message
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_BINDSTONE_REGISTER);

		// Send Animated Bind Flash
		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 2, player.getCommonData()
			.getLevel()), true);
	}

	/**
	 * @param player
	 */
	public static void onLogin(Player player) {
		Kisk kisk = kiskContainer.get(player);
		if (kisk != null)
			kisk.reAddPlayer(player);
	}

}
