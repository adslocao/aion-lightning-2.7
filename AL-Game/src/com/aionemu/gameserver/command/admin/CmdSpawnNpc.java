package com.aionemu.gameserver.command.admin;

import java.io.IOException;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;


public class CmdSpawnNpc extends BaseCommand {
	
	
	public void execute(Player admin, String... params) {
		if (params.length < 1 || params.length > 2) {
			showHelp(admin);
			return;
		}

		
		int respawnTime = 0;
		if (params.length == 2)
			respawnTime = ParseInteger(params[1]);
		
		
		
		int templateId = ParseInteger(params[0]);
		float x = admin.getX();
		float y = admin.getY();
		float z = admin.getZ();
		byte heading = admin.getHeading();
		int worldId = admin.getWorldId();

		SpawnTemplate spawn = SpawnEngine.addNewSpawn(worldId, templateId, x, y, z, heading, respawnTime);
		
		if (spawn == null) {
			PacketSendUtility.sendMessage(admin, "Il n'y a pas de npc avec cette id : " + templateId);
			return;
		}
		
		VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, admin.getInstanceId());

		if (visibleObject == null) {
			PacketSendUtility.sendMessage(admin, "Spawn id " + templateId + " was not found!");
			return ;
		}
		
		else if (respawnTime > 0) {
			try {
				DataManager.SPAWNS_DATA2.saveSpawn(admin, visibleObject, false);
			}
			catch (IOException e) {
				e.printStackTrace();
				PacketSendUtility.sendMessage(admin, "Could not save spawn");
			}
		}

		String objectName = visibleObject.getObjectTemplate().getName();
		PacketSendUtility.sendMessage(admin, objectName + " spawned");
	}
}

