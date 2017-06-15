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
package com.aionemu.gameserver.services.instance;

import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.autogroup.AutoGroupsType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.joda.time.DateTime;

/**
 *
 * @author xTz
 */
public class PvPArenaService {

	public static boolean isPvPArenaAvailable(Player player, AutoGroupsType agt) {
		if (player.getLevel() < 46) {
			return false;
		}
		if (!checkTime(agt)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401306, agt.getInstanceMapId()));
			return false;
		}
		if (!checkItem(player, agt)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219, agt.getInstanceMapId()));
			return false;
		}
		return true;
	}

	public static boolean checkItem(Player player, AutoGroupsType agt) {
		if(NetworkConfig.GAMESERVER_ID == 100){
			return true;
		}
		Storage inventory = player.getInventory();
		if (agt.isPvPFFAArena()) {
			return inventory.getItemCountByItemId(186000135) > 0;
		}
		if ( agt.isPvPSoloArena()) {
			return inventory.getItemCountByItemId(186000136) > 0;
		}
		return true;
	}

	private static boolean checkTime(AutoGroupsType agt) {
		if(NetworkConfig.GAMESERVER_ID == 100){
			return true;
		}
		if (agt.isPvPSoloArena()) {
			return isPvPArenaAvailableSolo();
		}
		if (agt.isPvPFFAArena()) {
			return isPvPArenaAvailableGroupe();
		}
		return true;
	}

	private static boolean isPvPArenaAvailableSolo() {
		DateTime now = DateTime.now();
		int hour = now.getHourOfDay();
		//int day = now.getDayOfWeek();
		/*if (day == 6 || day == 7 || day == 3) {
			return hour == 18 || hour == 13 || hour == 14 || hour == 22;
		}*/
		return hour == 12 || hour == 13 || hour == 18 || hour == 19 || hour == 22 || hour == 23;
	}

	private static boolean isPvPArenaAvailableGroupe() {
		DateTime now = DateTime.now();
		int hour = now.getHourOfDay();
		//int day = now.getDayOfWeek();
		/*if (day == 6 || day == 7 || day == 3) {
			return hour == 18 || hour == 13 || hour == 14 || hour == 22;
		}*/
		return hour == 18 || hour == 19 || hour == 22 || hour == 23;
	}
}