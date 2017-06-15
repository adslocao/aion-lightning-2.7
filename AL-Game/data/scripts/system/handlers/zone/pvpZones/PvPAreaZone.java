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
package zone.pvpZones;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * @author MrPoke
 */
@ZoneNameAnnotation(value = "LC1_PVP_SUB_C DC1_PVP_ZONE")
public class PvPAreaZone extends PvPZone {

	@Override
	protected void doTeleport(Player player, ZoneName zoneName) {
		switch (zoneName){
			case LC1_PVP_SUB_C:
				TeleportService.teleportTo(player, 110010000, 1470.3f, 1343.5f, 563.7f, 0, true);
				break;
			case DC1_PVP_ZONE:
				TeleportService.teleportTo(player, 120010000, 1005.1f, 1528.9f, 222.1f, 0, true);
				break;
		}
	}
}
