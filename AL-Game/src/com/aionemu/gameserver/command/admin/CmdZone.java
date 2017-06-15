package com.aionemu.gameserver.command.admin;

import java.util.List;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * @author ATracer
 */
public class CmdZone extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		Creature target;
		if (admin.getTarget() == null || !(admin.getTarget() instanceof Creature))
			target = admin;
		else
			target = (Creature) admin.getTarget();
		if (params.length == 0) {
			List<ZoneInstance> zones = target.getPosition().getMapRegion().getZones(target);
			if (zones.isEmpty()) {
				PacketSendUtility.sendMessage(admin, target.getName()+" are out of any zone");
			}
			else {
				PacketSendUtility.sendMessage(admin, target.getName()+" are in zone: ");
				for (ZoneInstance zone : zones){
					PacketSendUtility.sendMessage(admin, zone.getAreaTemplate().getZoneName().name());
				}
			}
		}
		else if ("refresh".equalsIgnoreCase(params[0])) {
			admin.revalidateZones();
		}
		else if ("inside".equalsIgnoreCase(params[0]) && params.length == 2){
			try {
				ZoneName name = ZoneName.valueOf(params[1]);
				PacketSendUtility.sendMessage(admin, "isInsideZone: "+ admin.isInsideZone(name));
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Zone name missing!");
			}
		}
	}

}