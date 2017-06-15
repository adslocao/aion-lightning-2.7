/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.abyss;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 */
public class AbyssService {

	private static final int[] abyssMapList = { 210050000, 220070000, 400010000, 600010000 };

	/**
	 * @param player
	 */
	public static final boolean isOnPvpMap(Player player) {
		for (int i : abyssMapList) {
			if (i == player.getWorldId())
				return true;
			else
				continue;
		}
		return false;
	}

	/**
	 * @param victim
	 */
	public static final void rankedKillAnnounce(final Player victim) {
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (p != victim && victim.getWorldId() == p.getWorldId()) {
					PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_ABYSS_ORDER_RANKER_DIE(victim, AbyssRankEnum.getRankDescriptionId(victim)));
				}
			}
		});
	}
	
	public static final void rankerSkillAnnounce(final Player player, final int nameId) {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player p) {
				if (p != player && player.getWorldType() == p.getWorldType() && !p.isInInstance()) {
					PacketSendUtility.sendPacket(p, SM_SYSTEM_MESSAGE.STR_SKILL_ABYSS_SKILL_IS_FIRED(player, new DescriptionId(nameId)));
				}
			}
		});
	}
}
