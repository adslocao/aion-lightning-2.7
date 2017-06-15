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
package com.aionemu.gameserver.model.team2.common.service;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.gameobjects.player.XPCape;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.stats.StatFunctions;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerTeamDistributionService {

	/**
	 * This method will send a reward if a player is in a group
	 * 
	 * @param player
	 */
	public static final void doReward(PlayerGroup group, final Npc owner) {
		if (group == null || owner == null)
			return;

		// Find Group Members and Determine Highest Level

		PlayerGroupRewardStats filteredStats = new PlayerGroupRewardStats(owner);
		group.applyOnMembers(filteredStats);

		// All are dead or not nearby.
		if (filteredStats.players.size() == 0 || !hasOneLivingPlayer(group))
			return;

		// Rewarding...
		long expReward = 0;
		boolean apReward = owner.isRewardAP();
		if (filteredStats.players.size() + filteredStats.mentorCount == 1)
			expReward = (long) (StatFunctions.calculateSoloExperienceReward(filteredStats.players.get(0), owner));
		else
			expReward = (long) (StatFunctions.calculateGroupExperienceReward(filteredStats.highestLevel, owner));

		// Party Bonus 2 members 10%, 3 members 20% ... 6 members 50%
		int size = filteredStats.players.size();
		double bonus = 100;
		if (size > 1)
			bonus = 150 + ((size - 2) * 10);

		bonus *= 1f * filteredStats.players.size() / size;

		for (Player member : filteredStats.players) {
			if (member.isMentor())
				continue;

			// Exp reward
			long reward = (long) (expReward * bonus * member.getLevel()) / (filteredStats.partyLvlSum * 100);

			// Players 10 levels below highest member get 0 exp.
			if (filteredStats.highestLevel - member.getLevel() >= 10)
				reward = 0;
			else if (filteredStats.mentorCount > 0) {
				int cape = XPCape.values()[(int) member.getLevel()].value();
				if (cape < reward)
					reward = cape;
			}

			member.getCommonData().addExp(reward, RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());

			// DP reward
			int currentDp = member.getCommonData().getDp();
			int dpReward = StatFunctions.calculateGroupDPReward(member, owner);
			member.getCommonData().setDp(dpReward + currentDp);

			// AP reward
			if (apReward && !(filteredStats.mentorCount > 0 && CustomConfig.MENTOR_GROUP_AP)) {
				int ap = Math.round(StatFunctions.calculatePvEApGained(member, owner) / filteredStats.players.size());
				AbyssPointsService.addAp(member, ap);
			}
		}

		// Give Drop
		Player leader = owner.getAggroList().getMostPlayerDamageOfMembers(filteredStats.players);
		if (leader == null)
			return;

		if (!owner.getAi2().getName().equals("chest") || filteredStats.mentorCount == 0)
			DropRegistrationService.getInstance().registerDrop(owner, leader, filteredStats.highestLevel, filteredStats.players);
	}

	public static void doReward(PlayerAlliance alliance, Npc owner) {
		// TODO: Merge with group type do-reward. (Near identical to GroupService doReward code.)
		// Plus complete rewrite of drop system and exp system.
		// http://www.aionsource.com/topic/40542-character-stats-xp-dp-origin-gerbatorteam-july-2009/
		// Find Group Members and Determine Highest Level
		List<Player> players = new ArrayList<Player>();
		int partyLvlSum = 0;
		int highestLevel = 0;
		for (Player player : alliance.getMembers()) {
			if (!player.isOnline())
				continue;
			if (MathUtil.isIn3dRange(player, owner, GroupConfig.GROUP_MAX_DISTANCE)) {
				players.add(player);
				partyLvlSum += player.getLevel();
				if (player.getLevel() > highestLevel)
					highestLevel = player.getLevel();
			}
		}

		// All are dead or not nearby.
		if (players.isEmpty())
			return;

		boolean oneLivingPlayer = false;
		for (Player player : players) {
			if (!player.getLifeStats().isAlreadyDead()) {
				oneLivingPlayer = true;
				break;
			}
		}

		if (oneLivingPlayer == false)
			return;

		// Rewarding...
		long expReward = 0;
		boolean apReward = owner.isRewardAP();
		if (players.size() == 1)
			expReward = (long) (StatFunctions.calculateSoloExperienceReward(players.get(0), owner));
		else
			expReward = (long) (StatFunctions.calculateGroupExperienceReward(highestLevel, owner));

		// Exp Mod
		// TODO: Add logic to prevent power leveling. Players 10 levels below highest member should get 0 exp.
		double mod = 1f;
		if (players.size() > 1)
			mod = 1.5f + (((players.size() - 2) * 10) / 100);

		expReward *= mod;

		for (Player member : players) {
			// Exp reward
			long reward = (expReward * member.getLevel()) / partyLvlSum;

			// Players 10 levels below highest member get 0 exp.
			if (highestLevel - member.getLevel() >= 10)
				reward = 0;
			member.getCommonData().addExp(reward, RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());

			// DP reward
			int currentDp = member.getCommonData().getDp();
			int dpReward = StatFunctions.calculateGroupDPReward(member, owner);
			member.getCommonData().setDp(dpReward + currentDp);

			// AP reward
			if (apReward) {
				int ap = Math.round(StatFunctions.calculatePvEApGained(member, owner) / players.size());
				AbyssPointsService.addAp(member, ap);
			}

			QuestEngine.getInstance().onKill(new QuestEnv(owner, member, 0, 0));
		}

		// Give Drop
		Player leader = owner.getAggroList().getMostPlayerDamageOfMembers(players);
		if (leader == null)
			return;
		DropRegistrationService.getInstance().registerDrop(owner, leader, highestLevel, players);
	}
	
	public static boolean hasOneLivingPlayer(PlayerGroup group) {
		for(Player member : group.getMembers()) {
			if(!member.getLifeStats().isAlreadyDead())
				return true;
		}
		return false;
	}

	public static class PlayerGroupRewardStats implements Predicate<Player> {

		public final List<Player> players = new ArrayList<Player>();
		int partyLvlSum = 0;
		public int highestLevel = 0;
		int mentorCount = 0;
		Npc owner;

		public PlayerGroupRewardStats(Npc owner) {
			this.owner = owner;
		}

		@Override
		public boolean apply(Player member) {
			if (member.isOnline()) {
				if (MathUtil.isIn3dRange(member, owner, GroupConfig.GROUP_MAX_DISTANCE)) {
					QuestEngine.getInstance().onKill(new QuestEnv(owner, member, 0, 0));
					if (member.isMentor()) {
						mentorCount++;
						return true;
					}
					players.add(member);
					partyLvlSum += member.getLevel();
					if (member.getLevel() > highestLevel)
						highestLevel = member.getLevel();
				}
			}
			return true;
		}

	}

}
