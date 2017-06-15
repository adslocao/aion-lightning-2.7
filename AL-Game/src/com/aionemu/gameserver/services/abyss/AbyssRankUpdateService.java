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
package com.aionemu.gameserver.services.abyss;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author ATracer
 */
public class AbyssRankUpdateService {

	private static final Logger log = LoggerFactory.getLogger(AbyssRankUpdateService.class);

	private AbyssRankUpdateService() {
	}

	public static AbyssRankUpdateService getInstance() {
		return SingletonHolder.instance;
	}

	public void scheduleUpdate() {
		ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		int nextTime = dao.load("abyssRankUpdate");
		if (nextTime < System.currentTimeMillis()/1000){
			performUpdate();
		}

		log.info("Starting ranking update task based on cron expression: " + RankingConfig.TOP_RANKING_UPDATE_RULE);
		CronService.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				performUpdate();
			}
		}, RankingConfig.TOP_RANKING_UPDATE_RULE, true);
	}

	/**
	 * Perform update of all ranks
	 */
	public void performUpdate() {
		log.info("AbyssRankUpdateService: executing rank update");
		long startTime = System.currentTimeMillis();		
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				player.getAbyssRank().doUpdate();
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
			}
		});

		updateLimitedRanks();
		AbyssRankingCache.getInstance().reloadRankings();
		log.info("AbyssRankUpdateService: execution time: " + (System.currentTimeMillis() - startTime) / 1000);
	}

	/**
	 * Update player ranks based on quota for all players (online/offline)
	 */
	private void updateLimitedRanks() {
		updateAllRanksForRace(Race.ASMODIANS, AbyssRankEnum.STAR1_OFFICER.getRequired());
		updateAllRanksForRace(Race.ELYOS, AbyssRankEnum.STAR1_OFFICER.getRequired());
	}

	private void updateAllRanksForRace(Race race, int apLimit) {
		Map<Integer, Integer> playerApMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersAp(race, apLimit);
		List<Entry<Integer, Integer>> playerApEntries = new ArrayList<Entry<Integer, Integer>>(playerApMap.entrySet());
		Collections.sort(playerApEntries, new PlayerApComparator<Integer, Integer>());

		selectRank(AbyssRankEnum.SUPREME_COMMANDER, playerApEntries);
		selectRank(AbyssRankEnum.COMMANDER, playerApEntries);
		selectRank(AbyssRankEnum.GREAT_GENERAL, playerApEntries);
		selectRank(AbyssRankEnum.GENERAL, playerApEntries);
		selectRank(AbyssRankEnum.STAR5_OFFICER, playerApEntries);
		selectRank(AbyssRankEnum.STAR4_OFFICER, playerApEntries);
		selectRank(AbyssRankEnum.STAR3_OFFICER, playerApEntries);
		selectRank(AbyssRankEnum.STAR2_OFFICER, playerApEntries);
		selectRank(AbyssRankEnum.STAR1_OFFICER, playerApEntries);
		updateToNoQuotaRank(playerApEntries);
	}

	private void selectRank(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerApEntries) {
		int quota = rank.getQuota();
		for (int i = 0; i < quota; i++) {
			if (playerApEntries.isEmpty()) {
				return;
			}
			// check next player in list
			Entry<Integer, Integer> playerAp = playerApEntries.get(0);
			// check if there are some players left in map
			if (playerAp == null) {
				return;
			}
			int playerId = playerAp.getKey();
			int ap = playerAp.getValue();
			// check if this (and the rest) player has required ap count
			if (ap < rank.getRequired()) {
				return;
			}
			// remove player and update its rank
			playerApEntries.remove(0);
			updateRankTo(rank, playerId);
		}
	}

	private void updateToNoQuotaRank(List<Entry<Integer, Integer>> playerApEntries) {
		for (Entry<Integer, Integer> playerApEntry : playerApEntries) {
			updateRankTo(AbyssRankEnum.GRADE1_SOLDIER, playerApEntry.getKey());
		}
	}

	protected void updateRankTo(AbyssRankEnum newRank, int playerId) {
		// check if rank is changed for online players
		Player onlinePlayer = World.getInstance().findPlayer(playerId);
		if (onlinePlayer != null) {
			AbyssRank abyssRank = onlinePlayer.getAbyssRank();
			AbyssRankEnum currentRank = abyssRank.getRank();
			if (currentRank != newRank) {
				abyssRank.setRank(newRank);
				AbyssPointsService.checkRankChanged(onlinePlayer, currentRank, newRank);
			}
		}
		else {
			DAOManager.getDAO(AbyssRankDAO.class).updateAbyssRank(playerId, newRank);
		}
	}

	private static class SingletonHolder {

		protected static final AbyssRankUpdateService instance = new AbyssRankUpdateService();
	}

	private static class PlayerApComparator<K, V extends Comparable<V>> implements Comparator<Entry<K, V>> {

		@Override
		public int compare(Entry<K, V> o1, Entry<K, V> o2) {
			return -o1.getValue().compareTo(o2.getValue()); // descending order
		}
	}

}
