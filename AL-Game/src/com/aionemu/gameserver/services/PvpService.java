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
package com.aionemu.gameserver.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.configs.main.PvPConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.attack.KillList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.siegeservice.FortressSiegeKillListener;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.utils.stats.StatFunctions;

/**
 * @author Sarynth
 */
public class PvpService {

	private static Logger log = LoggerFactory.getLogger(PvpService.class);
	public static final PvpService getInstance() {
		return SingletonHolder.instance;
	}

	private FastMap<Integer, KillList> pvpKillLists;

	private PvpService() {
		pvpKillLists = new FastMap<Integer, KillList>();
	}

	/**
	 * @param winnerId
	 * @param victimId
	 * @return
	 */
	private int getKillsFor(int winnerId, int victimId) {
		KillList winnerKillList = pvpKillLists.get(winnerId);

		if (winnerKillList == null)
			return 0;
		return winnerKillList.getKillsFor(victimId);
	}

	/**
	 * @param winnerId
	 * @param victimId
	 */
	private void addKillFor(int winnerId, int victimId) {
		KillList winnerKillList = pvpKillLists.get(winnerId);
		if (winnerKillList == null) {
			winnerKillList = new KillList();
			pvpKillLists.put(winnerId, winnerKillList);
		}
		winnerKillList.addKillFor(victimId);
	}

	/**
	 * @param victim
	 */
	public void doReward(Player victim) {
		// winner is the player that receives the kill count
		final Player winner = victim.getAggroList().getMostPlayerDamage();


		
		int totalDamage = victim.getAggroList().getTotalDamage();

		if (totalDamage == 0 || winner == null || winner.getRace() == victim.getRace() || victim.getEventTeamId() != -1) {
			return;
		}

		String ip1 = winner.getClientConnection().getIP();
		String mac1 = winner.getClientConnection().getMacAddress();
		String ip2 = victim.getClientConnection().getIP();
		String mac2 = victim.getClientConnection().getMacAddress();
		if (ip1.equalsIgnoreCase(ip2) && NetworkConfig.GAMESERVER_ID != 100) {
			return;
		}
		if (mac1 != null && mac2 != null && mac1.equalsIgnoreCase(mac2) && NetworkConfig.GAMESERVER_ID != 100) {
			return;
		}

		// Announce that player has died.
		PacketSendUtility.broadcastPacketAndReceive(victim,
			SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH_TO_B(victim.getName(), winner.getName()));
			
		//Kill-log
		if (LoggingConfig.LOG_KILL)
			log.info("[KILL] Player [" + winner.getName()+ "] killed [" + victim.getName() + "] at WorldId [" + winner.getWorldId() + "]");

		// Keep track of how much damage was dealt by players
		// so we can remove AP based on player damage...
		int playerDamage = 0;
		boolean success = false;
		
		Collection<AggroInfo> agros = victim.getAggroList().getFinalDamageList(true);
		int nbPlayerOnTarget = calcNbOnBus(agros, victim);

		// Distribute AP and Custom Reward to groups and players that had damage.
		for (AggroInfo aggro : agros) {
			if (aggro.getAttacker() instanceof Player) {
				success = rewardPlayer(victim, totalDamage, aggro, winner, nbPlayerOnTarget);
			}
			else if (aggro.getAttacker() instanceof PlayerGroup) {
				success = rewardPlayerGroup(victim, totalDamage, aggro , winner, nbPlayerOnTarget);
			}
			else if (aggro.getAttacker() instanceof PlayerAlliance) {
				success = rewardPlayerAlliance(victim, totalDamage, aggro, winner, nbPlayerOnTarget);
			}

			// Add damage last, so we don't include damage from same race. (Duels, Arena)
			if (success)
				playerDamage += aggro.getDamage();
		}

		// Apply lost AP to defeated player
		final int apLost = StatFunctions.calculatePvPApLost(victim, winner);
		final int apActuallyLost = (int) (apLost * playerDamage / totalDamage);

		if (apActuallyLost > 0)
			AbyssPointsService.addAp(victim, -apActuallyLost);
	}

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerGroup(Player victim, int totalDamage, AggroInfo aggro, Player winner, int nbPlayerOnTarget) {
		// Reward Group
		PlayerGroup group = ((PlayerGroup) aggro.getAttacker());
	
		// Don't Reward Player of Same Faction.
		if (group.getRace() == victim.getRace())
			return false;

		// Find group members in range
		List<Player> players = new ArrayList<Player>();

		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;

		for (Player member : group.getMembers()) {
			if (!member.isOnline())
				continue;
			if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead()) {
					players.add(member);
					if (member.getLevel() > maxLevel)
						maxLevel = member.getLevel();
					if (member.getAbyssRank().getRank().getId() > maxRank)
						maxRank = member.getAbyssRank().getRank().getId();
				}
			}
		}

		// They are all dead or out of range or 10 level.
		if (players.size() == 0/* && (maxLevel - victim.getLevel()) >= 10*/)
			return false;
		
		int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		if(winner.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
			baseApReward *= CustomConfig.FACTION_BONUS_AP;
		}
		int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
		int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
		float groupPercentage = (float) aggro.getDamage() / totalDamage;
		int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
		int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
		int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());
		
		for (Player member : players) {
			int memberApGain = 1;
			int memberXpGain = 1;
			int memberDpGain = 1;
			if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
				if (apRewardPerMember > 0)
					memberApGain = calcAPonBus(victim, Math.round(apRewardPerMember * member.getRates().getApPlayerGainRate()), nbPlayerOnTarget);
				if (xpRewardPerMember > 0)
					memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
				if (dpRewardPerMember > 0)
					memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
			     // Update nearby group members kill count (Daily/Weekly ...)
				if (PvPConfig.ENABLE_REWARD_KILL_TO_GROUP && (maxLevel - victim.getLevel()) < 10){
					addCustomKillReward(member, victim, winner);
					if (winner.getObjectId() == member.getObjectId()){
						this.addKillFor(member.getObjectId(), victim.getObjectId());
					}
				}	
			}
			if(member.isNewPlayer()){
				memberApGain *= CustomConfig.BOOST_AP_NEW_PLAYER_RATIO;
			}
			AbyssPointsService.addAp(member, memberApGain);
			member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
			member.getCommonData().addDp(memberDpGain);
		
			//notify Kill-Quests
			int worldId = member.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, member, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, member, 0, 0), victim.getAbyssRank().getRank());
			
		}

		return true;
	}

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if group is not same race
	 */
	private boolean rewardPlayerAlliance(Player victim, int totalDamage, AggroInfo aggro, Player winner, int nbPlayerOnTarget) {
		// Reward Alliance
		PlayerAlliance alliance = ((PlayerAlliance) aggro.getAttacker());
		
		// Don't Reward Player of Same Faction.
		if (alliance.getLeaderObject().getRace() == victim.getRace())
			return false;

		// Find group members in range
		List<Player> players = new ArrayList<Player>();

		// Find highest rank and level in local group
		int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
		int maxLevel = 0;

		for (Player member : alliance.getMembers()) {
			if (!member.isOnline())
				continue;
			if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
				// Don't distribute AP to a dead player!
				if (!member.getLifeStats().isAlreadyDead()) {
					players.add(member);
					if (member.getLevel() > maxLevel)
						maxLevel = member.getLevel();
					if (member.getAbyssRank().getRank().getId() > maxRank)
						maxRank = member.getAbyssRank().getRank().getId();
				}
			}
		}
		
		// Kill gestion
		for(PlayerAllianceGroup group : alliance.getGroups()) {
            if(!group.hasMember(winner.getObjectId())){//if killer is not member of this group
                continue;
            }
            for(Player pl : group.getMembers()){ //give reward for all group
                if (players.contains(pl) && PvPConfig.ENABLE_REWARD_KILL_TO_GROUP && (maxLevel - victim.getLevel()) < 10 && this.getKillsFor(pl.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS){
                    addCustomKillReward(pl, victim, winner);
        			if (winner.getObjectId() == pl.getObjectId()){
        				this.addKillFor(pl.getObjectId(), victim.getObjectId());
        			}
                }
            }
        }
		
		// They are all dead or out of range.
		if (players.size() == 0/* && (maxLevel - victim.getLevel()) >= 10*/)
			return false;

		int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
		if(winner.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
			baseApReward *= CustomConfig.FACTION_BONUS_AP;
		}
		int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
		int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
		float groupPercentage = (float) aggro.getDamage() / totalDamage;
		int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
		int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
		int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());
		
		// AP Gestion
		for (Player member : players) {
			int memberApGain = 1;
			int memberXpGain = 1;
			int memberDpGain = 1;
			if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
				if (apRewardPerMember > 0)
					memberApGain = calcAPonBus(victim, Math.round(apRewardPerMember * member.getRates().getApPlayerGainRate()), nbPlayerOnTarget);
				if (xpRewardPerMember > 0)
					memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
				if (dpRewardPerMember > 0)
					memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
			    // Update nearby group members kill count (Daily/Weekly ...)

			}
			if(member.isNewPlayer()){
				memberApGain *= CustomConfig.BOOST_AP_NEW_PLAYER_RATIO;
			}
			AbyssPointsService.addAp(member, memberApGain);
			member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
			member.getCommonData().addDp(memberDpGain);

			//notify Kill-Quests
			int worldId = member.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, member, 0, 0), worldId);
			QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, member, 0, 0), victim.getAbyssRank().getRank());
			
		}

		return true;
	}
	
	private int calcNbOnBus(Collection<AggroInfo> agros, Player victim){
		int count = 0;
		for (AggroInfo aggro : agros) {
			if (aggro.getAttacker() instanceof Player) {
				count++;
			}
			else if (aggro.getAttacker() instanceof PlayerGroup) {
				// Reward Group
				PlayerGroup group = ((PlayerGroup) aggro.getAttacker());
				for (Player member : group.getMembers()) {
					if (member.isOnline() && MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
						count++;
					}
				}
			}
			else if (aggro.getAttacker() instanceof PlayerAlliance) {
				// Reward Group
				PlayerAlliance group = ((PlayerAlliance) aggro.getAttacker());
				for (Player member : group.getMembers()) {
					if (member.isOnline() && MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
						count++;
					}
				}
			}
		}
		return count;
	}
	
	private int calcAPonBus(Player victim, int memberApGain, int nbPlayerOnTarget){
		boolean isSiege = SiegeService.getInstance().isAtLeastOneSiegeInProgress();
		
		if(isSiege){
			return memberApGain;
		}
		if(!PvPConfig.ENABLE_NERF_BUS || nbPlayerOnTarget < PvPConfig.CAP_NERF_BUS){
			return memberApGain;
		}
		float ratioBus = (PvPConfig.RATIO_BASE_BUS + PvPConfig.RATIO_PERSON_BUS * nbPlayerOnTarget);
		ratioBus = 100 - Math.min(ratioBus, 90);
		
		return Math.round(memberApGain * ratioBus/100);
	}

	/**
	 * @param victim
	 * @param totalDamage
	 * @param aggro
	 * @return true if player is not same race
	 */
	private boolean rewardPlayer(Player victim, int totalDamage, AggroInfo aggro, Player winner, int nbPlayerOnTarget) {
		// Reward Player
		Player activeChar = ((Player) aggro.getAttacker());
		
		// Don't Reward Player of Same Faction et 10 level.
		if (activeChar.getRace() == victim.getRace()/* && (winner.getLevel() - victim.getLevel()) >= 10*/)
			return false;

		int baseApReward = 1;
		int baseXpReward = 1;
		int baseDpReward = 1;

		if (this.getKillsFor(activeChar.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS)
		{
			baseApReward = StatFunctions.calculatePvpApGained(victim, activeChar.getAbyssRank().getRank().getId(),
					activeChar.getLevel());
			baseXpReward = StatFunctions.calculatePvpXpGained(victim, activeChar.getAbyssRank().getRank().getId(),
					activeChar.getLevel());
			baseDpReward = StatFunctions.calculatePvpDpGained(victim, activeChar.getAbyssRank().getRank().getId(), 
					activeChar.getLevel());
			// Update nearby group members kill count (Daily/Weekly ...)
			if((winner.getLevel() - victim.getLevel()) < 10){
				addCustomKillReward(activeChar, victim, winner);
				this.addKillFor(activeChar.getObjectId(), victim.getObjectId());
			}
			
		}
		if(winner.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
			baseApReward *= CustomConfig.FACTION_BONUS_AP;
		}
		int apPlayerReward = calcAPonBus(victim, Math.round(baseApReward * activeChar.getRates().getApPlayerGainRate() * aggro.getDamage()
			/ totalDamage), nbPlayerOnTarget);
		int xpPlayerReward = Math.round(baseXpReward * activeChar.getRates().getXpPlayerGainRate() * aggro.getDamage()
			/ totalDamage);
		int dpPlayerReward = Math.round(baseDpReward * activeChar.getRates().getDpPlayerRate() * aggro.getDamage() 
			/ totalDamage);
		
		if(activeChar.isNewPlayer()){
			apPlayerReward *= CustomConfig.BOOST_AP_NEW_PLAYER_RATIO;
		}

		AbyssPointsService.addAp(activeChar, apPlayerReward);
		activeChar.getCommonData().addExp(xpPlayerReward, RewardType.PVP_KILL, victim.getName());
		activeChar.getCommonData().addDp(dpPlayerReward);

		//notify Kill-Quests
		int worldId = activeChar.getWorldId();
		QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, activeChar, 0, 0), worldId);
		QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, activeChar, 0, 0), victim.getAbyssRank().getRank());

		
		return true;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PvpService instance = new PvpService();
	}
	
	/**
	 * give kill for member 
	 * if </br>
	 * 	winner has max daily pvp kills dont give anymore </br>
	 * else </br>
	 * 	give CustomReward </br>
	 * 
	 * @param member
	 * @param victim
	 * @param winner
	 */
	private void addCustomKillReward(Player member, Player victim, Player winner){
		
		if(member != winner	&& (member.getCurrentGroup() == null || member.getCurrentGroup() != winner.getCurrentGroup()))
			return;
		
		//notify FortressKill
		FortressSiegeKillListener.onKillEvent(member);
		
		member.getAbyssRank().updateKillCounts();
		// Add Player Kill to record.
		if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
			int kills = member.getAbyssRank().getAllKill();
			// Pvp Kill Reward.
			if (CustomConfig.ENABLE_KILL_REWARD && kills > 5) {
				if (kills % CustomConfig.KILLS_NEEDED1 == 0) {
					ItemService.addItem(member, CustomConfig.REWARD1, 1);
					PacketSendUtility.sendMessage(member, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD1
						+ "] for having killed " + CustomConfig.KILLS_NEEDED1 + " players !");
					    log.info("[REWARD] Player [" + member.getName()+ "] win 2 [" + CustomConfig.REWARD1 + "]");
				}
				if (kills % CustomConfig.KILLS_NEEDED2 == 0) {
					ItemService.addItem(member, CustomConfig.REWARD2, 1);
					PacketSendUtility.sendMessage(member, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD2
						+ "] for having killed " + CustomConfig.KILLS_NEEDED2 + " players !");
					log.info("[REWARD] Player [" + member.getName()+ "] win 4 [" + CustomConfig.REWARD2 + "]");
				}
				if (kills % CustomConfig.KILLS_NEEDED3 == 0) {
					ItemService.addItem(member, CustomConfig.REWARD3, 1);
					PacketSendUtility.sendMessage(member, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD3
						+ "] for having killed " + CustomConfig.KILLS_NEEDED3 + " players !");
					log.info("[REWARD] Player [" + member.getName()+ "] win 6 [" + CustomConfig.REWARD3 + "]");
				}
			}
		}
	}
}
