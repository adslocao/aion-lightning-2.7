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

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEGION_EDIT;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author ATracer
 */
public class AbyssPointsService {

	@GlobalCallback(AddAPGlobalCallback.class)
	public static void addAp(Player player, int value) {
		if (player == null)
			return;
		
		// Notify player of AP gained (This should happen before setAp happens.)
		if(value > 0)
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_ABYSS_POINT_GAIN(value));
		else //You used %num0 Abyss Points.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300965, value *-1));

		// Set the new AP value
		setAp(player, value);

		// Add Abyss Points to Legion
		if (player.isLegionMember() && value > 0) {
			player.getLegion().addContributionPoints(value);
			PacketSendUtility.broadcastPacketToLegion(player.getLegion(), new SM_LEGION_EDIT(0x03, player.getLegion()));
		}
	}

	/**
	 * @param player
	 * @param value
	 */
	public static void setAp(Player player, int value) {
		if (player == null)
			return;

		AbyssRank rank = player.getAbyssRank();

		AbyssRankEnum oldAbyssRank = rank.getRank();
		rank.addAp(value);
		AbyssRankEnum newAbyssRank = rank.getRank();

		checkRankChanged(player, oldAbyssRank, newAbyssRank);

		PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK(player.getAbyssRank()));
	}

	/**
	 * @param player
	 * @param oldAbyssRank
	 * @param newAbyssRank
	 */
	public static void checkRankChanged(Player player, AbyssRankEnum oldAbyssRank, AbyssRankEnum newAbyssRank) {
		if (oldAbyssRank == newAbyssRank) {
			return;
		}

		PacketSendUtility.broadcastPacket(player, new SM_ABYSS_RANK_UPDATE(0, player));
		// Apparently we are not in our own known list... so we must tell ourselves as well
		PacketSendUtility.sendPacket(player, new SM_ABYSS_RANK_UPDATE(0, player));

		AbyssSkillService.updateSkills(player);
	}

	@SuppressWarnings("rawtypes")
	public abstract static class AddAPGlobalCallback implements Callback{

		@Override
		public CallbackResult beforeCall(Object obj, Object[] args) {
			return CallbackResult.newContinue();
		}

		@Override
		public CallbackResult afterCall(Object obj, Object[] args, Object methodResult) {
			Player player = (Player) args[0];
			int abyssPoints = (Integer)args[1];
			onAbyssPointsAdded(player, abyssPoints);
			return CallbackResult.newContinue();
		}

		@Override
		public Class<? extends Callback> getBaseClass() {
			return AddAPGlobalCallback.class;
		}

		public abstract void onAbyssPointsAdded(Player player, int abyssPoints);
	}
}
