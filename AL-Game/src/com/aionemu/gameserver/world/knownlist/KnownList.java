/**
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
package com.aionemu.gameserver.world.knownlist;

import java.util.Collections;
import java.util.Map;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.MapRegion;

/**
 * KnownList.
 * 
 * @author -Nemesiss-
 * @modified kosyachok
 */

public class KnownList {

	private static final Logger log = LoggerFactory.getLogger(KnownList.class);

	// how far player will see visible object
	private static final float VisibilityDistance = 95;

	// maxZvisibleDistance
	private static final float maxZvisibleDistance = 95;

	/**
	 * Owner of this KnownList.
	 */
	protected final VisibleObject owner;
	/**
	 * List of objects that this KnownList owner known
	 */
	protected final FastMap<Integer, VisibleObject> knownObjects = new FastMap<Integer, VisibleObject>().shared();

	/**
	 * List of player that this KnownList owner known
	 */
	protected volatile FastMap<Integer, Player> knownPlayers;

	private long lastUpdate;

	/**
	 * @param owner
	 */
	public KnownList(VisibleObject owner) {
		this.owner = owner;
	}

	/**
	 * Do KnownList update.
	 */
	public void doUpdate() {
		if ((System.currentTimeMillis() - lastUpdate) < 1000)
			return;

		forgetObjects();
		findVisibleObjects();

		lastUpdate = System.currentTimeMillis();
	}

	/**
	 * Clear known list. Used when object is despawned.
	 */
	public void clear() {
		for (VisibleObject object : knownObjects.values()) {
			object.getKnownList().del(owner, false);
		}
		knownObjects.clear();
		if (knownPlayers != null) {
			knownPlayers.clear();
		}
	}

	/**
	 * Check if object is known
	 * 
	 * @param object
	 * @return true if object is known
	 */
	public boolean knowns(AionObject object) {
		return knownObjects.containsKey(object.getObjectId());
	}

	/**
	 * Add VisibleObject to this KnownList.
	 * 
	 * @param object
	 */
	protected boolean add(VisibleObject object) {
		if (!isAwareOf(object)) {
			return false;
		}
		if (knownObjects.put(object.getObjectId(), object) == null) {
			owner.getController().see(object);
			if (object instanceof Player) {
				checkKnownPlayersInitialized();
				knownPlayers.put(object.getObjectId(), (Player) object);
			}
			return true;
		}
		return false;
	}

	/**
	 * Delete VisibleObject from this KnownList.
	 * 
	 * @param object
	 */
	private void del(VisibleObject object, boolean isOutOfRange) {
		/**
		 * object was known.
		 */
		if (knownObjects.remove(object.getObjectId()) != null) {
			owner.getController().notSee(object, isOutOfRange);
			if (knownPlayers != null) {
				knownPlayers.remove(object.getObjectId());
			}
		};
	}

	/**
	 * forget out of distance objects.
	 */
	private void forgetObjects() {
		for (VisibleObject object : knownObjects.values()) {
			if (!checkObjectInRange(object) && !object.getKnownList().checkReversedObjectInRange(owner)) {
				del(object, true);
				object.getKnownList().del(owner, true);
			}
		}
	}

	/**
	 * Find objects that are in visibility range.
	 */
	protected void findVisibleObjects() {
		if (owner == null || !owner.isSpawned())
			return;

		MapRegion[] regions = owner.getActiveRegion().getNeighbours();
		for (int i = 0; i < regions.length; i++) {
			MapRegion r = regions[i];
			FastMap<Integer, VisibleObject> objects = r.getObjects();
			for (FastMap.Entry<Integer, VisibleObject> e = objects.head(), mapEnd = objects.tail(); (e = e.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject == owner || newObject == null)
					continue;

				if (!isAwareOf(newObject)) {
					continue;
				}
				if (knownObjects.containsKey(newObject.getObjectId()))
					continue;

				if (!checkObjectInRange(newObject) && !newObject.getKnownList().checkReversedObjectInRange(owner))
					continue;

				/**
				 * New object is not known.
				 */
				if (add(newObject)) {
					newObject.getKnownList().add(owner);
				}
			}
		}
	}

	/**
	 * Whether knownlist owner aware of found object (should be kept in knownlist)
	 * 
	 * @param newObject
	 * @return
	 */
	protected boolean isAwareOf(VisibleObject newObject) {
		return true;
	}

	protected boolean checkObjectInRange(VisibleObject newObject) {
		// check if Z distance is greater than maxZvisibleDistance
		if (Math.abs(owner.getZ() - newObject.getZ()) > maxZvisibleDistance)
			return false;
		
		return MathUtil.isInRange(owner, newObject, VisibilityDistance);
	}
	
	/**
	 * Check can be overriden if new object has different known range and that value should be used
	 * @param newObject
	 * @return
	 */
	protected boolean checkReversedObjectInRange(VisibleObject newObject) {
		return false;
	}

	public void doOnAllNpcs(Visitor<Npc> visitor) {
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = knownObjects.head(), mapEnd = knownObjects.tail(); (e = e
				.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject instanceof Npc) {
					visitor.visit((Npc) newObject);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all npcs" + ex);
		}
	}

	public void doOnAllNpcsWithOwner(VisitorWithOwner<Npc, VisibleObject> visitor) {
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = knownObjects.head(), mapEnd = knownObjects.tail(); (e = e
				.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject instanceof Npc) {
					visitor.visit((Npc) newObject, owner);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all npcs" + ex);
		}
	}

	public void doOnAllPlayers(Visitor<Player> visitor) {
		if (knownPlayers == null) {
			return;
		}
		try {
			for (FastMap.Entry<Integer, Player> e = knownPlayers.head(), mapEnd = knownPlayers.tail(); (e = e.getNext()) != mapEnd;) {
				Player player = e.getValue();
				if (player != null) {
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all players" + ex);
		}
	}

	public void doOnAllObjects(Visitor<VisibleObject> visitor) {
		try {
			for (FastMap.Entry<Integer, VisibleObject> e = knownObjects.head(), mapEnd = knownObjects.tail(); (e = e
				.getNext()) != mapEnd;) {
				VisibleObject newObject = e.getValue();
				if (newObject != null) {
					visitor.visit(newObject);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all objects" + ex);
		}
	}

	public Map<Integer, VisibleObject> getKnownObjects() {
		return knownObjects;
	}

	public Map<Integer, Player> getKnownPlayers() {
		return knownPlayers != null ? knownPlayers : Collections.<Integer, Player> emptyMap();
	}

	final void checkKnownPlayersInitialized() {
		if (knownPlayers == null) {
			synchronized (this) {
				if (knownPlayers == null) {
					knownPlayers = new FastMap<Integer, Player>().shared();
				}
			}
		}
	}

	public VisibleObject getObject(int targetObjectId) {
		return this.knownObjects.get(targetObjectId);
	}
}
