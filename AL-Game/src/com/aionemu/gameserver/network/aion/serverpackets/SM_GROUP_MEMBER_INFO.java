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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author Lyahim, ATracer
 */
public class SM_GROUP_MEMBER_INFO extends AionServerPacket {

	private int groupId;
	private Player player;
	private GroupEvent event;

	public SM_GROUP_MEMBER_INFO(PlayerGroup group, Player player, GroupEvent event) {
		this.groupId = group.getTeamId();
		this.player = player;
		this.event = event;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		PlayerLifeStats pls = player.getLifeStats();
		PlayerCommonData pcd = player.getCommonData();
		WorldPosition wp = pcd.getPosition();

		if (event == GroupEvent.ENTER && !player.isOnline()) {
			event = GroupEvent.ENTER_OFFLINE;
		}

		writeD(groupId);
		writeD(player.getObjectId());
		if (player.isOnline()) {
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp()); // maxflighttime
			writeD(pls.getCurrentFp()); // currentflighttime
			writeD(wp.getMapId());
			writeD(wp.getMapId());
			writeF(wp.getX());
			writeF(wp.getY());
			writeF(wp.getZ());
		}
		else {
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeF(0);
			writeF(0);
			writeF(0);
		}

		writeC(pcd.getPlayerClass().getClassId()); // class id
		writeC(pcd.getGender().getGenderId()); // gender id
		writeC(pcd.getLevel()); // level

		writeC(event.getId()); // something events
		writeH(player.isOnline() ? 1 : 0); // TODO channel?
		writeC(player.isMentor() ? 0x01 : 0x00);

		switch (event) {
			case MOVEMENT:
				break;
			case LEAVE:
				writeH(0x00); // unk
				writeC(0x00); // unk
				break;
			case ENTER_OFFLINE:
			case JOIN:
				writeS(pcd.getName()); // name
				break;
			default:
				writeS(pcd.getName()); // name
				writeD(0x00); // unk
				writeD(0x00); // unk
				List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
				writeH(abnormalEffects.size()); // Abnormal effects
				for (Effect effect : abnormalEffects) {
					writeD(effect.getEffectorId()); // casterid
					writeH(effect.getSkillId()); // spellid
					writeC(effect.getSkillLevel()); // spell level
					writeC(effect.getTargetSlot()); // unk ?
					writeD(effect.getRemainingTime()); // estimatedtime
				}
				writeD(0x25F7); // unk 9719
				break;
		}
	}

}
