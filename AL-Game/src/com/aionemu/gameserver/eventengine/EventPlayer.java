package com.aionemu.gameserver.eventengine;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.player.Player;

public class EventPlayer {
	public final int world;
	public final int instanceId;
	public final float x;
	public final float y;
	public final float z;
	public final byte head;
	public final Player player;
	public final ArrayList<EquipementC> equipment = new ArrayList<EquipementC>();

	public static Logger log = LoggerFactory.getLogger(EventPlayer.class);
	/*
		TeleportService.teleportTo(playerToMove, admin.getWorldId(), admin.getInstanceId(), admin.getX(), admin.getY(),
				admin.getZ(), admin.getHeading(), 0, true);
	*/
	class EquipementC{
		public final int objectId;
		public final int slot;
		public EquipementC(int objectId, int slot){
			this.objectId = objectId;
			this.slot = slot;
		}
	}
	
	public EventPlayer(Player player){
		world = player.getWorldId();
		instanceId = player.getInstanceId();
		x = player.getX();
		y = player.getY();
		z = player.getZ();
		head = player.getHeading();
		this.player = player;
	}
	
	public void seauvItem(int objectId, int slot){
		equipment.add(new EquipementC(objectId, slot));
		log.info("Equipement size : "+ equipment.size());
	}
	
	public void equip(){
		log.info("Equipement equip stop");
		for(EquipementC e : equipment)
			player.getEquipment().equipItem(e.objectId, e.slot);
		log.info("Equipement equip ok");
	}
}
