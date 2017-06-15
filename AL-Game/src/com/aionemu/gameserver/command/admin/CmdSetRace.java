package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapType;

/*syntax: //setrace <elyos | asmodians> */
public class CmdSetRace extends BaseCommand {

	

	
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			showHelp(admin);
			return;
		}

		VisibleObject visibleobject = admin.getTarget();

		if (visibleobject == null || !(visibleobject instanceof Player)) {
			PacketSendUtility.sendMessage(admin, "Wrong select target.");
			return;
		}

		Player target = (Player) visibleobject;
		if (params[0].equalsIgnoreCase("elyos")) {
			target.getCommonData().setRace(Race.ELYOS);
			TeleportService.teleportTo(target, WorldMapType.SANCTUM.getId(), 1322, 1511, 568, 0);
			PacketSendUtility.sendMessage(target, "Has been moved to Sanctum.");
		}
		else if (params[0].equalsIgnoreCase("asmodians")) {
			target.getCommonData().setRace(Race.ASMODIANS);
			TeleportService.teleportTo(target, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195, 0);
			PacketSendUtility.sendMessage(target, "Has been moved to Pandaemonium");
		}
		PacketSendUtility.sendMessage(admin,
			target.getName() + " race has been changed to " + params[0] + ".\n" + target.getName()
				+ " has been moved to town.");
	}

}
