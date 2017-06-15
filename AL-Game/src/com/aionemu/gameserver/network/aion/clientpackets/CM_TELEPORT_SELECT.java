/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.teleport.TeleportService;

/**
 * @author ATracer, orz, KID
 */
public class CM_TELEPORT_SELECT extends AionClientPacket {

	/** NPC ID */
	public int targetObjectId;

	/** Destination of teleport */
	public int locId;

	public CM_TELEPORT_SELECT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		// empty
		targetObjectId = readD();
		locId = readD(); // locationId
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.getLifeStats().isAlreadyDead())
			return;
		
		AionObject obj = player.getKnownList().getObject(targetObjectId);
		if(obj != null && obj instanceof Npc) {
			int npcId = ((Npc)obj).getNpcId();
			TeleporterTemplate teleport = DataManager.TELEPORTER_DATA.getTeleporterTemplate(npcId);
			if(teleport != null)
				TeleportService.teleport(teleport, locId, player);
			else
				LoggerFactory.getLogger(CM_TELEPORT_SELECT.class).warn("teleportation id "+locId+" was not found on npc "+npcId);
		}
		else
			LoggerFactory.getLogger(CM_TELEPORT_SELECT.class).debug("player "+player.getName()+" requested npc "+targetObjectId+" for teleportation "+locId+", but he doesnt have such npc in knownlist");
	}
}
