package com.aionemu.gameserver.eventengine;

import com.aionemu.gameserver.model.gameobjects.player.Player;

public class EventLocation {
	public final int world;
	public final int instanceId;
	public final float x;
	public final float y;
	public final float z;
	public final byte head;
	public final Player player;
	
	/*
		TeleportService.teleportTo(playerToMove, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(),
				admin.getZ(), admin.getHeading(), 0, true);
	*/
	
	public EventLocation(Player player){
		world = player.getWorldId();
		instanceId = player.getInstanceId();
		x = player.getX();
		y = player.getY();
		z = player.getZ();
		head = player.getHeading();
		this.player = player;
	}
}
