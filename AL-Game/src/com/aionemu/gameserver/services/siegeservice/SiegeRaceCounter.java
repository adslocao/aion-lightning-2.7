package com.aionemu.gameserver.services.siegeservice;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import javolution.util.FastMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that contains all the counters for the siege. One SiegeCounter per race should be used.
 *
 * @author SoulKeeper
 */
public class SiegeRaceCounter implements Comparable<SiegeRaceCounter> {

	private final AtomicLong totalDamage = new AtomicLong();

	private final Map<Integer, AtomicLong> legionDamageCounter = new FastMap<Integer, AtomicLong>().shared();

	private final Map<Integer, AtomicLong> playerDamageCounter = new FastMap<Integer, AtomicLong>().shared();

	private final Map<Integer, AtomicLong> playerAPCounter = new FastMap<Integer, AtomicLong>().shared();
	private final Map<Integer, AtomicLong> playerKillCounter = new FastMap<Integer, AtomicLong>().shared();
	
	private final PlayerInfoContainer pIC = new PlayerInfoContainer();
	
	private final SiegeRace siegeRace;

	public SiegeRaceCounter(SiegeRace siegeRace) {
		this.siegeRace = siegeRace;
	}

	public void addPoints(Creature creature, int damage) {

		addTotalDamage(damage);

		if (creature instanceof Player) {
			addPlayerDamage((Player) creature, damage);
		}
	}

	public void addTotalDamage(int damage) {
		totalDamage.addAndGet(damage);
	}

	public void addPlayerDamage(Player player, int damage) {
		Legion legion = player.getLegion();
		if (legion != null) {
			addLegionDamage(legion, damage);
		}

		addToCounter(player.getObjectId(), damage, playerDamageCounter);
	}

	public void addLegionDamage(Legion legion, int damage) {
		addToCounter(legion.getLegionId(), damage, legionDamageCounter);
	}

	public void addAbyssPoints(Player player, int abyssPoints) {
		//custom reg
		pIC.updatePlayerInfo(player.getClientConnection().getIP(), player.getName(), player.getAcountName(), abyssPoints);
		addToCounter(player.getObjectId(), abyssPoints, playerAPCounter);
	}

	public void addKill(Player player) {
		//custom reg
		//pIC.updatePlayerInfo(player.getClientConnection().getIP(), player.getName(), player.getAcountName(), abyssPoints);
		addToCounter(player.getObjectId(), 1, playerKillCounter);
	}
	
	protected <K> void addToCounter(K key, int value, Map<K, AtomicLong> counterMap) {

		// Get the counter for specific key
		AtomicLong counter = counterMap.get(key);

		// Counter was not registered, need to create it
		if (counter == null) {

			// synchronize here, it may happen that there will be attempt to increment
			// same counter from different threads
			synchronized (this) {
				if (counterMap.containsKey(key)) {
					counter = counterMap.get(key);
				} else {
					counter = new AtomicLong();
					counterMap.put(key, counter);
				}
			}
		}
		counter.addAndGet(value);
	}

	public long getTotalDamage() {
		return totalDamage.get();
	}

	public long getNonLegionDamage() {
		return totalDamage.get() - getTotalLegionDamage();
	}

	public long getTotalLegionDamage() {
		long result = 0;
		for (AtomicLong damage : legionDamageCounter.values()) {
			result += damage.get();
		}
		return result;
	}

	/**
	 * Returns "legionId to damage" map.
	 * Map is ordered by damage in "descending" order
	 *
	 * @return map with legion damages
	 */
	public Map<Integer, Long> getLegionDamageCounter() {
		return getOrderedCounterMap(legionDamageCounter);
	}

	/**
	 * Returns "legionId to damage" map.
	 * Map is ordered by damage in "descending" order
	 *
	 * @return map with legion damages
	 */
	public Map<Integer, Long> getPlayerDamageCounter() {
		return getOrderedCounterMap(playerDamageCounter);
	}

	/**
	 * Returns "player to abyss points" map.
	 * Map is ordered by abyssPoints in descending order
	 *
	 * @return map with player abyss points
	 */
	public Map<Integer, Long> getPlayerAbyssPoints() {
		return getOrderedCounterMap(playerAPCounter);
	}
	
	
	public Map<Integer, Long> getPlayerKills() {
		return getOrderedCounterMap(playerKillCounter);
	}
	public PlayerInfoContainer getPIC() {
		return pIC;
	}
	
	protected <K> Map<K, Long> getOrderedCounterMap(Map<K, AtomicLong> unorderedMap) {
		if (GenericValidator.isBlankOrNull(unorderedMap)) {
			return Collections.emptyMap();
		}

		LinkedList<Map.Entry<K, AtomicLong>> tempList = Lists.newLinkedList(unorderedMap.entrySet());
		Collections.sort(tempList, new Comparator<Map.Entry<K, AtomicLong>>() {
			@Override
			public int compare(Map.Entry<K, AtomicLong> o1, Map.Entry<K, AtomicLong> o2) {
				return new Long(o2.getValue().get()).compareTo(o1.getValue().get());
			}
		});

		Map<K, Long> result = Maps.newLinkedHashMap();
		for (Map.Entry<K, AtomicLong> entry : tempList) {
			result.put(entry.getKey(), entry.getValue().get());
		}
		return result;
	}

	@Override
	public int compareTo(SiegeRaceCounter o) {
		return new Long(o.getTotalDamage()).compareTo(getTotalDamage());
	}

	public SiegeRace getSiegeRace() {
		return siegeRace;
	}

	/**
	 * Returns Legion id if damage done by that legion is > than 50% of total damage
	 *
	 * @return legion id or null if none
	 */
	public Integer getWinnerLegionId() {
		Map<Integer, Long> legionDamageMap = getLegionDamageCounter();
		if (legionDamageMap.isEmpty()) {
			return null;
		}

		Integer topLegion = legionDamageMap.keySet().iterator().next();
		long topLegionDamage = legionDamageMap.get(topLegion);

		// legion captures fortress if damage done is > then non-legion damage
		boolean captureByLegion = topLegionDamage > getNonLegionDamage();
		return captureByLegion ? topLegion : null;
	}
}
