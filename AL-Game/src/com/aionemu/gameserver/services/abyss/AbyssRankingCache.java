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



import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_LEGIONS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_RANKING_PLAYERS;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;


/**
 * @author VladimirZ
 */
public class AbyssRankingCache {

	private static final Logger log = LoggerFactory.getLogger(AbyssRankingCache.class);
	private int lastUpdate;
	private final FastMap<Race, List<SM_ABYSS_RANKING_PLAYERS>> players = new FastMap<Race, List<SM_ABYSS_RANKING_PLAYERS>>();
	private final FastMap<Race, SM_ABYSS_RANKING_LEGIONS> legions = new FastMap<Race,  SM_ABYSS_RANKING_LEGIONS>();

	public void reloadRankings() {
		log.info("Updating abyss ranking cache");
		this.lastUpdate = (int)(System.currentTimeMillis()/1000);
		getDAO().updateRankList();
		
		//delete not before new list is created
		List<SM_ABYSS_RANKING_PLAYERS> newlyCalculated;
		newlyCalculated = generatePacketsForRace(Race.ASMODIANS);
		players.remove(Race.ASMODIANS);
		players.put(Race.ASMODIANS, newlyCalculated);
		newlyCalculated = generatePacketsForRace(Race.ELYOS);
		players.remove(Race.ELYOS);
		players.put(Race.ELYOS, newlyCalculated);

		legions.clear();
		legions.put(Race.ELYOS, new SM_ABYSS_RANKING_LEGIONS(lastUpdate, getDAO().getAbyssRankingLegions(Race.ELYOS), Race.ELYOS));
		legions.put(Race.ASMODIANS, new SM_ABYSS_RANKING_LEGIONS(lastUpdate, getDAO().getAbyssRankingLegions(Race.ASMODIANS), Race.ASMODIANS));
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
					player.resetAbyssRankListUpdated();
			}
		});
	}
	
	private List<SM_ABYSS_RANKING_PLAYERS> generatePacketsForRace(Race race) {
		//players orderd by ap
		ArrayList<AbyssRankingResult> list = getDAO().getAbyssRankingPlayers(race);
		int page = 1;
		List<SM_ABYSS_RANKING_PLAYERS> playerPackets = new ArrayList<SM_ABYSS_RANKING_PLAYERS>();
		for (int i = 0; i< list.size(); i+= 46){
			if (list.size() > i+46){
				playerPackets.add(new SM_ABYSS_RANKING_PLAYERS(lastUpdate, list.subList(i, i+46), race, page, false));
			}
			else{
				playerPackets.add(new SM_ABYSS_RANKING_PLAYERS(lastUpdate, list.subList(i, list.size()), race, page, true));
			}
			page++;
		}
		return playerPackets;
	}

	/**
	 * @return all players
	 */
	public List<SM_ABYSS_RANKING_PLAYERS> getPlayers(Race race) {
		return players.get(race);
	}

	/**
	 * @return all legions
	 */
	public SM_ABYSS_RANKING_LEGIONS getLegions(Race race) {
		return legions.get(race);
	}

	
	/**
	 * @return the lastUpdate
	 */
	public int getLastUpdate() {
		return lastUpdate;
	}

	private AbyssRankDAO getDAO() {
		return DAOManager.getDAO(AbyssRankDAO.class);
	}

	public static final AbyssRankingCache getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		protected static final AbyssRankingCache INSTANCE = new AbyssRankingCache();
	}
}
