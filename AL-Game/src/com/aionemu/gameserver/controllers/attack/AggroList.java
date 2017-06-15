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
package com.aionemu.gameserver.controllers.attack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.metadata.ObjectCallback;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer, KKnD
 */
@SuppressWarnings("rawtypes")
public class AggroList {

	protected final Creature owner;

	private FastMap<Integer, AggroInfo> aggroList = new FastMap<Integer, AggroInfo>().shared();

	public AggroList(Creature owner) {
		this.owner = owner;
	}

	/**
	 * Only add damage from enemies. (Verify this includes summons, traps, pets, and excludes fall damage.)
	 * 
	 * @param creature
	 * @param damage
	 */
	@ObjectCallback(AddDamageValueCallback.class)
	public void addDamage(Creature creature, int damage) {
		if (!isAware(creature))
			return;

		AggroInfo ai = getAggroInfo(creature);
		ai.addDamage(damage);
		/**
		 * For now we add hate equal to each damage received Additionally there will be broadcast of extra hate
		 */
		ai.addHate(damage);

		// TODO move out to controller
		owner.getAi2().onCreatureEvent(AIEventType.ATTACK, creature);
	}

	public void addHate(final Creature creature, int hate) {
		addHate(creature, hate, true);
	}

	/**
	 * Extra hate that is received from using non-damange skill effects
	 */
	public void addHate(final Creature creature, int hate, boolean socialCall) {
		if (!isAware(creature))
			return;

		addHateValue(creature, hate);
		if (socialCall)
			owner.getKnownList().doOnAllNpcs(new Visitor<Npc>() {

				@Override
				public void visit(Npc object) {
					object.getAi2().onCreatureEvent(AIEventType.CREATURE_ATTACKED, owner);
				}
			});
	}

	/**
	 * start hating creature by adding 1 hate value
	 */
	public void startHate(final Creature creature) {
		addHateValue(creature, 1);
	}

	protected void addHateValue(final Creature creature, int hate) {
		AggroInfo ai = getAggroInfo(creature);
		ai.addHate(hate);

		// TODO move out to controller
		owner.getAi2().onCreatureEvent(AIEventType.ATTACK, creature);
	}

	/**
	 * @return player/group/alliance with most damage.
	 */
	public AionObject getMostDamage() {
		AionObject mostDamage = null;
		int maxDamage = 0;

		for (AggroInfo ai : getFinalDamageList(true)) {
			if (ai.getAttacker() == null)
				continue;

			if (ai.getDamage() > maxDamage) {
				mostDamage = ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}

		return mostDamage;
	}

	public Race getPlayerWinnerRace() {
		AionObject winner = getMostDamage();
		if (winner instanceof PlayerGroup) {
			return ((PlayerGroup) winner).getRace();
		}
		else if (winner instanceof Player)
			return ((Player) winner).getRace();
		return null;
	}

	/**
	 * @return player with most damage
	 */
	public Player getMostPlayerDamage() {
		if (aggroList.isEmpty())
			return null;

		Player mostDamage = null;
		int maxDamage = 0;

		// Use final damage list to get pet damage as well.
		for (AggroInfo ai : this.getFinalDamageList(false)) {
			if (ai.getDamage() > maxDamage) {
				mostDamage = (Player) ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}

		return mostDamage;
	}

	/**
	 * @return player with most damage
	 */
	public Player getMostPlayerDamageOfMembers(Collection<Player> players) {
		if (aggroList.isEmpty())
			return null;

		Player mostDamage = null;
		int maxDamage = 0;

		// Use final damage list to get pet damage as well.
		for (AggroInfo ai : this.getFinalDamageList(false)) {
			if (!players.contains((Player) ai.getAttacker())) {
				continue;
			}
			if (ai.getDamage() > maxDamage) {

				mostDamage = (Player) ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}

		return mostDamage;
	}

	/**
	 * @return most hated creature
	 */
	public Creature getMostHated() {
		if (aggroList.isEmpty())
			return null;

		Creature mostHated = null;
		int maxHate = 0;

		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null)
				continue;

			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();

			if (attacker.getLifeStats().isAlreadyDead() || !owner.getKnownList().knowns(attacker))
				ai.setHate(0);

			if (ai.getHate() > maxHate) {
				mostHated = attacker;
				maxHate = ai.getHate();
			}
		}

		return mostHated;
	}

	/**
	 * @param creature
	 * @return
	 */
	public boolean isMostHated(Creature creature) {
		if (creature == null || creature.getLifeStats().isAlreadyDead())
			return false;

		Creature mostHated = getMostHated();
		return mostHated != null && mostHated.equals(creature);

	}

	/**
	 * @param creature
	 * @param value
	 */
	public void notifyHate(Creature creature, int value) {
		if (isHating(creature))
			addHate(creature, value);
	}

	/**
	 * @param creature
	 */
	public void stopHating(VisibleObject creature) {
		AggroInfo aggroInfo = aggroList.get(creature.getObjectId());
		if (aggroInfo != null)
			aggroInfo.setHate(0);
	}

	/**
	 * Remove completely creature from aggro list
	 * 
	 * @param creature
	 */
	public void remove(Creature creature) {
		aggroList.remove(creature.getObjectId());
	}

	/**
	 * Clear aggroList
	 */
	public void clear() {
		aggroList.clear();
	}

	/**
	 * @param creature
	 * @return aggroInfo
	 */
	public AggroInfo getAggroInfo(Creature creature) {
		AggroInfo ai = aggroList.get(creature.getObjectId());
		if (ai == null) {
			ai = new AggroInfo(creature);
			aggroList.put(creature.getObjectId(), ai);
		}
		return ai;
	}

	/**
	 * @param creature
	 * @return boolean
	 */
	public boolean isHating(Creature creature) {
		return aggroList.containsKey(creature.getObjectId());
	}

	/**
	 * @return aggro list
	 */
	public Collection<AggroInfo> getList() {
		return aggroList.values();
	}

	/**
	 * @return total damage
	 */
	public int getTotalDamage() {
		int totalDamage = 0;
		for (AggroInfo ai : aggroList.values()) {
			totalDamage += ai.getDamage();
		}
		return totalDamage;
	}
	
	/*public int getCountPlayer(){
		int res = 0;
		for (AggroInfo ai : aggroList.values()) {
			if (ai.getAttacker() instanceof Player){
				res++;
			}
		}
		return res;
	}*/

	/**
	 * Used to get a list of AggroInfo with player/group/alliance damages combined. - Includes only AggroInfo with
	 * PlayerAlliance, PlayerGroup, and Player objects.
	 * 
	 * @return finalDamageList including players/groups/alliances
	 */
	public Collection<AggroInfo> getFinalDamageList(boolean mergeGroupDamage) {
		Map<Integer, AggroInfo> list = new HashMap<Integer, AggroInfo>();

		for (AggroInfo ai : aggroList.values()) {
			if (!(ai.getAttacker() instanceof Creature))
				continue;

			// Check to see if this is a summon, if so add the damage to the group.

			Creature master = ((Creature) ai.getAttacker()).getMaster();

			if (!(master instanceof Player))
				return Collections.emptyList();

			Player player = (Player) master;

			// Don't include damage from players outside the known list.
			if (!owner.getKnownList().knowns(player))
				continue;

			if (mergeGroupDamage) {
				AionObject source;

				if (player.isInTeam()) {
					source = player.getCurrentTeam();
				}
				else {
					source = player;
				}

				if (list.containsKey(source.getObjectId())) {
					list.get(source.getObjectId()).addDamage(ai.getDamage());
				}
				else {
					AggroInfo aggro = new AggroInfo(source);
					aggro.setDamage(ai.getDamage());
					list.put(source.getObjectId(), aggro);
				}
			}
			else if (list.containsKey(player.getObjectId())) {
				// Summon or other assistance
				list.get(player.getObjectId()).addDamage(ai.getDamage());
			}
			else {
				// Create a separate object so we don't taint current list.
				AggroInfo aggro = new AggroInfo(player);
				aggro.addDamage(ai.getDamage());
				list.put(player.getObjectId(), aggro);
			}
		}

		return list.values();
	}

	protected boolean isAware(Creature creature) {
		return creature != null && !creature.getObjectId().equals(owner.getObjectId()) && 
			(creature.isEnemy(owner) || DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(owner.getTribe(), creature.getTribe()));
	}

	public static abstract class AddDamageValueCallback implements Callback<AggroList> {

		@Override
		public final CallbackResult beforeCall(AggroList obj, Object[] args) {
			return CallbackResult.newContinue();
		}

		@Override
		public final CallbackResult afterCall(AggroList obj, Object[] args, Object methodResult) {

			Creature creature = (Creature) args[0];
			Integer damage = (Integer) args[1];

			if (obj.isAware(creature)) {
				onDamageAdded(creature, damage);
			}

			return CallbackResult.newContinue();
		}

		@Override
		public final Class<? extends Callback> getBaseClass() {
			return AddDamageValueCallback.class;
		}

		public abstract void onDamageAdded(Creature creature, int hate);
	}
}
