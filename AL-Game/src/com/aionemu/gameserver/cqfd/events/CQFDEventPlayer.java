package com.aionemu.gameserver.cqfd.events;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class CQFDEventPlayer {
	public int _world;
	public int _instanceId;
	public float _x;
	public float _y;
	public float _z;
	public byte _head;
	public Player _player;
	public final ArrayList<EquipementC> equipment = new ArrayList<EquipementC>();

	public int _teamId;
	public CQFDEvent _event;

	public static Logger _log = LoggerFactory.getLogger(CQFDEventPlayer.class);
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
	
	public CQFDEventPlayer(int worldId, int instanceId, int x, int y, int z, byte head){
		_world = worldId;
		_instanceId = instanceId;
		_x = x;
		_y = y;
		_z = z;
		_head = head;
		_player = null;
	}
	
	public CQFDEventPlayer(Player player, CQFDEvent event, int teamId){
		_world = player.getWorldId();
		_instanceId = player.getInstanceId();
		_x = player.getX();
		_y = player.getY();
		_z = player.getZ();
		_head = player.getHeading();
		_teamId = teamId;
		_player = player;
		_event = event;
	}
	
	public CQFDEventPlayer setLoc(Player admin){
		_world = admin.getWorldId();
		_instanceId = admin.getInstanceId();
		_x = admin.getX();
		_y = admin.getY();
		_z = admin.getZ();
		_head = admin.getHeading();
		return this;
	}
	
	/*
	public CQFDEventPlayer(CQFDEvent event, int teamId, int instanceId , Location loc){
		_instanceId = instanceId;
		_x = loc.x;
		_y = loc.y;
		_z = loc.z;
		_head = loc.h;
		_teamId = teamId;
		_player = null;
		_event = event;
		
	}
	*/
	
	public CQFDEventPlayer setTeamId(int teamId){
		_teamId = teamId;
		return this;
	}
	
	public CQFDEventPlayer setEvent(CQFDEvent event){
		_event = event;
		return this;
	}
	public void unequip(){
		//_equipment.add(new EquipementC(item));
		//_player.getInventory().unEquipItem(item);
	}
	
	public void equip(){
		_log.info("Equipement equip stop");
		for(EquipementC e : equipment)
			_player.getEquipment().equipItem(e.objectId, e.slot);
		_log.info("Equipement equip ok");
	}
}
