/*
 * This file is part of aion-engine <aion-engine.com>
 *
 * aion-engine is private software: you can redistribute it and or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Private Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with aion-engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/*"Syntax : //movetoobj <object id> */


public class CmdMoveToObject extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params == null || params.length != 1) {
			showHelp(admin);
			return;	
		}


		int objectId = 0;

		try {
			objectId = Integer.valueOf(params[1]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Only numbers please!!!");
		}

		VisibleObject object = World.getInstance().findVisibleObject(objectId);
		if (object == null) {
			PacketSendUtility.sendMessage(admin, "Cannot find object for spawn #" + objectId);
			return;
		}

		VisibleObject spawn = (VisibleObject) object;

		TeleportService.teleportTo(admin, spawn.getWorldId(), spawn.getSpawn().getX(), spawn.getSpawn().getY(), spawn
			.getSpawn().getZ(), 0);
		admin.getController().stopProtectionActiveTask();
	}
}
