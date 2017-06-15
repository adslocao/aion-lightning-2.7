/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javolution.util.FastMap;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PortalCooldownList;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.world.World;

/**
 * @author xavier
 */
public class SM_INSTANCE_INFO extends AionServerPacket {

	private Player player;
	private boolean update;
	private boolean enter;
	private int instanceId;

	public SM_INSTANCE_INFO(Player player, boolean update, boolean enter) {
		this.player = player;
		this.update = update;
		this.enter = enter;
		this.instanceId = 0;
	}

	public SM_INSTANCE_INFO(Player player, int instanceId) {
		this.player = player;
		this.update = true;
		this.enter = true;
		this.instanceId = instanceId;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (!enter && !update) {
			writeH(0x2);
			writeD(0x0);
			writeH(0x1);
			writeD(player.getObjectId());
			writeH(0x0);
			writeS(player.getName());
			return;
		}
		if (instanceId != 0) {
			writeC(update ? 2 : 1);
			writeC(enter ? 3 : 0);
			writeD(0x0);
			writeH(1);
			writeD(player.getObjectId());
			writeH(1);
			writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(instanceId).getId());
			int objId = player.getCurrentTeamId();
			writeD(objId);
			writeD((int) (player.getPortalCooldownList().getPortalCooldown(instanceId) - System.currentTimeMillis()) / 1000);
			writeS(player.getName());
			return;
		}
		Collection<Player> players = null;
		PlayerAlliance playerAlliance = player.getPlayerAlliance2();

		PlayerGroup playerGroup2 = player.getPlayerGroup2();
		if (playerGroup2 != null) {
			players = playerGroup2.getMembers();
		}
		else if (playerAlliance != null) {
			players = new ArrayList<Player>();
			for (Player member : playerAlliance.getMembers()) {
				if (member.isOnline() && member.getPortalCooldownList().hasCooldowns()) {
					Player p = World.getInstance().findPlayer(member.getObjectId());
					if (p != null && p.isOnline()) {
						players.add(p);
					}
				}
			}
		}
		else {
			if (player.getPortalCooldownList().hasCooldowns()) {
				players = Collections.singletonList(player);
			}
		}

		if (players == null || players.isEmpty()) {
			writeD(0x0);
			writeD(0x0);
		}
		else {
			writeC(update ? 2 : 1);
			writeC(enter ? 3 : 0);
			writeD(0x0);
			writeH(players.size());
			{
				for (Player member : players) {
					PortalCooldownList portalCooldownList = member.getPortalCooldownList();
					int instanceCooldownRate = InstanceService.getInstanceRate(player, instanceId);
					writeD(member.getObjectId());
					writeH(portalCooldownList.size());
					for (FastMap.Entry<Integer, Long> e = portalCooldownList.getPortalCoolDowns().head(), end = portalCooldownList
						.getPortalCoolDowns().tail(); (e = e.getNext()) != end;) {
						if (instanceCooldownRate > 0) {
							writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(e.getKey()).getId()
								/ instanceCooldownRate);
						}
						else {
							writeD(DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(e.getKey()).getId());
						}
						writeD(0x0);
						writeD((int) (e.getValue() - System.currentTimeMillis()) / 1000);
						writeS(member.getName());
					}
				}
			}
		}
	}
}
