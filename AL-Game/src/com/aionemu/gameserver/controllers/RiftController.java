/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.controllers;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_RIFT_ANNOUNCE;
import com.aionemu.gameserver.services.RespawnService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.spawnengine.RiftSpawnManager.RiftEnum;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
public class RiftController extends NpcController {

	private boolean isMaster = false;
	private SpawnTemplate slaveSpawnTemplate;
	private Npc slave;

	private Integer maxEntries;
	private Integer minLevel;
	private Integer maxLevel;

	private int usedEntries = 0;
	private boolean isAccepting;

	private RiftEnum riftTemplate;

	private int deSpawnedTime;
	/**
	 * Used to create master rifts or slave rifts (slave == null)
	 * 
	 * @param slaveSpawnTemplate
	 */

	public RiftController(Npc slave, RiftEnum riftTemplate) {
		this.deSpawnedTime = ((int)(System.currentTimeMillis()/1000))+60*60;
		this.riftTemplate = riftTemplate;
		this.maxEntries = riftTemplate.getEntries();
		if (slave != null)// master rift should be created
		{
			this.slave = slave;
			this.slaveSpawnTemplate = slave.getSpawn();
			this.minLevel = riftTemplate.getMinLevel();
			this.maxLevel = riftTemplate.getMaxLevel();
			isMaster = true;
			isAccepting = true;
		}
	}

	@Override
	public void onDialogRequest(Player player) {
		if (!isMaster && !isAccepting)
			return;

		RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {

			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (!isAccepting)
					return;

				if(!getOwner().isSpawned())
					return;

				int worldId = slaveSpawnTemplate.getWorldId();
				float x = slaveSpawnTemplate.getX();
				float y = slaveSpawnTemplate.getY();
				float z = slaveSpawnTemplate.getZ();

				TeleportService.teleportTo(responder, worldId, x, y, z, 0);
				usedEntries++;

				if (usedEntries >= maxEntries) {
					isAccepting = false;

					RespawnService.scheduleDecayTask(getOwner());
					RespawnService.scheduleDecayTask(slave);
				}

				WorldMapInstance worldInstance = getOwner().getPosition().getMapRegion().getParent();
				final SM_RIFT_ANNOUNCE masterPacket = new SM_RIFT_ANNOUNCE(getThis(), true);
				worldInstance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						PacketSendUtility.sendPacket(player, masterPacket);
					}
				});
			}

			@Override
			public void denyRequest(Creature requester, Player responder) {
				// do nothing
			}
		};

		boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_USE_RIFT, responseHandler);
		if (requested) {
			PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_USE_RIFT, 0));
		}
	}

	@Override
	public void onDelete() {

		WorldMapInstance worldInstance = getOwner().getPosition().getMapRegion().getParent();
		final SM_RIFT_ANNOUNCE packet = new SM_RIFT_ANNOUNCE(getOwner().getObjectId());
		worldInstance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (player.isSpawned()) {
					PacketSendUtility.sendPacket(player, packet);
				}
			}
		});
	
		super.onDelete();
	}
	/**
	 * @param activePlayer
	 */
	public void sendMessage(Player activePlayer) {
		if (!getOwner().isSpawned())
			return;
		if (isMaster){
			PacketSendUtility.sendPacket(activePlayer, new SM_RIFT_ANNOUNCE(this, isMaster));
			PacketSendUtility.sendPacket(activePlayer, new SM_RIFT_ANNOUNCE(riftTemplate.getDestination()));
		}
		else{
			PacketSendUtility.sendPacket(activePlayer, new SM_RIFT_ANNOUNCE(this, isMaster));
		}
	}

	/**
	 * 
	 */
	public void sendAnnounce() {
		if (getOwner().isSpawned()) {
			WorldMapInstance worldInstance = getOwner().getPosition().getMapRegion().getParent();
			final SM_RIFT_ANNOUNCE masterPacket = new SM_RIFT_ANNOUNCE(this, true);
			final SM_RIFT_ANNOUNCE slavePacket = new SM_RIFT_ANNOUNCE(this, false);
			final SM_RIFT_ANNOUNCE announcePacket = new SM_RIFT_ANNOUNCE(riftTemplate.getDestination());
			worldInstance.doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isSpawned()) {
						if (isMaster){
							PacketSendUtility.sendPacket(player, masterPacket);
							PacketSendUtility.sendPacket(player, announcePacket);
						}
						else{
							PacketSendUtility.sendPacket(player, slavePacket);
						}
					}
				}
			});
		}
	}

	
	/**
	 * @return the maxEntries
	 */
	public Integer getMaxEntries() {
		return maxEntries;
	}

	
	/**
	 * @return the minLevel
	 */
	public Integer getMinLevel() {
		return minLevel;
	}

	
	/**
	 * @return the maxLevel
	 */
	public Integer getMaxLevel() {
		return maxLevel;
	}

	
	/**
	 * @return the riftTemplate
	 */
	public RiftEnum getRiftTemplate() {
		return riftTemplate;
	}

	
	/**
	 * @return the usedEntries
	 */
	public int getUsedEntries() {
		return usedEntries;
	}
	
	private RiftController getThis(){
		return this;
	}
	public int getRemainTime(){
		return deSpawnedTime -(int)(System.currentTimeMillis()/1000);
	}
}
