package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.command.CmdTeleService;
import com.aionemu.gameserver.command.CmdTeleTemplate;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;

/*	//tele <teleportname>
*	//tele add <newteleportname>
*/

public class CmdTele extends BaseCommand {
	
	public CmdTele () {
		subCmds.put("add", new SubCmdTeleAdd());
	}

	public void execute(Player admin, String... params) {
		if (params.length != 1) {
			showHelp(admin);
			return ;
		}
		
		CmdTeleTemplate myTeleport = CmdTeleService.getInstance().getTeleport(params[0]);
		if (myTeleport == null) {
			PacketSendUtility.sendMessage(admin, "Teleport introuvable.");
			return ;
		}
		
		goTo(admin, myTeleport.getWorldId(), myTeleport.getX(), myTeleport.getY(), myTeleport.getZ());
		PacketSendUtility.sendMessage(admin, "Vous venez d'etre teleporte a " + params[0]);
	}
	
	private static void goTo(final Player admin, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType())
			TeleportService.teleportTo(admin, worldId, getInstanceId(worldId, admin), x, y, z, 0 ,true);
		else
			TeleportService.teleportTo(admin, worldId, x, y, z, 0, true);
	}
	
	private static int getInstanceId(int worldId, Player admin) {
		if (admin.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, admin.getObjectId());
			if (registeredInstance != null)
				return registeredInstance.getInstanceId();
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, admin);
		return newInstance.getInstanceId();
	}
	
	public class SubCmdTeleAdd extends BaseCommand {
		public void execute(Player admin, String... params) {
			if (params.length != 1) {
				showHelp(admin);
				return ;
			}
				
			if (CmdTeleService.getInstance().addTeleTemplate(params[0], admin.getPosition()))
				PacketSendUtility.sendMessage(admin, "Teleport cree.");
			else
				PacketSendUtility.sendMessage(admin, "Erreur de creation de teleport.");
			
		}
	}

}
