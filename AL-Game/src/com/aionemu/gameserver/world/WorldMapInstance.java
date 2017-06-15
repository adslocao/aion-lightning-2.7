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
package com.aionemu.gameserver.world;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.aionemu.gameserver.configs.custom.CustomFun;
import com.aionemu.gameserver.configs.main.WorldConfig;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.quest.QuestNpc;
import com.aionemu.gameserver.model.templates.world.WorldMapTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.world.exceptions.DuplicateAionObjectException;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.RegionZone;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;
import com.aionemu.gameserver.world.zone.ZoneService;

import java.util.ArrayList;
import java.util.List;

/**
 * World map instance object.
 * 
 * @author -Nemesiss-
 */
public abstract class WorldMapInstance {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(WorldMapInstance.class);
	/**
	 * Size of region
	 */
	public static final int regionSize = WorldConfig.WORLD_REGION_SIZE;
	/**
	 * WorldMap witch is parent of this instance.
	 */
	private final WorldMap parent;
	/**
	 * Map of active regions.
	 */
	protected final TIntObjectHashMap<MapRegion> regions = new TIntObjectHashMap<MapRegion>();

	/**
	 * All objects spawned in this world map instance
	 */
	private final Map<Integer, VisibleObject> worldMapObjects = new FastMap<Integer, VisibleObject>().shared();

	/**
	 * All players spawned in this world map instance
	 */
	private final FastMap<Integer, Player> worldMapPlayers = new FastMap<Integer, Player>().shared();
	private final ArrayList<Integer> worldMapHistoryPlayers = new ArrayList<Integer>();
	private final Set<Integer> registeredObjects = Collections.newSetFromMap(new FastMap<Integer, Boolean>().shared());

	private PlayerGroup registeredGroup = null;

	private Future<?> emptyInstanceTask = null;

	/**
	 * Id of this instance (channel)
	 */
	private int instanceId;

	private final FastList<Integer> questIds = new FastList<Integer>();

	private InstanceHandler instanceHandler;
	
	private Map<ZoneName, ZoneInstance> zones = new HashMap<ZoneName, ZoneInstance>();

	private Integer soloPlayer;
	private Player soloPlayerp;
	
	private PlayerAlliance registredAlliance;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 */
	public WorldMapInstance(WorldMap parent, int instanceId) {
		this.parent = parent;
		this.instanceId = instanceId;
		this.zones = ZoneService.getInstance().getZoneInstancesByWorldId(parent.getMapId());
		initMapRegions();
	}

	/**
	 * Return World map id.
	 * 
	 * @return world map id
	 */
	public Integer getMapId() {
		return getParent().getMapId();
	}

	/**
	 * Returns WorldMap witch is parent of this instance
	 * 
	 * @return parent
	 */
	public WorldMap getParent() {
		return parent;
	}
	
	public WorldMapTemplate getTemplate() {
		return parent.getTemplate();
	}

	/**
	 * Returns MapRegion that contains coordinates of VisibleObject. If the region doesn't exist, it's created.
	 * 
	 * @param object
	 * @return a MapRegion
	 */
	MapRegion getRegion(VisibleObject object) {
		return getRegion(object.getX(), object.getY(), object.getZ());
	}

	/**
	 * Returns MapRegion that contains given x,y coordinates. If the region doesn't exist, it's created.
	 * 
	 * @param x
	 * @param y
	 * @return a MapRegion
	 */
	public abstract MapRegion getRegion(float x, float y, float z);

	/**
	 * Create new MapRegion and add link to neighbours.
	 * 
	 * @param regionId
	 * @return newly created map region
	 */
	protected abstract MapRegion createMapRegion(int regionId);
	
	protected abstract void initMapRegions();

	/**
	 * Returs {@link World} instance to which belongs this WorldMapInstance
	 * 
	 * @return World
	 */
	public World getWorld() {
		return getParent().getWorld();
	}

	/**
	 * @param object
	 */
	public void addObject(VisibleObject object) {
		if (worldMapObjects.put(object.getObjectId(), object) != null) {
			throw new DuplicateAionObjectException("Object with templateId "
				+ String.valueOf(object.getObjectTemplate().getTemplateId()) + " already spawned in the instance "
				+ String.valueOf(this.getMapId()) + " " + String.valueOf(this.getInstanceId()));
		}
		if (object instanceof Npc) {
			QuestNpc data = QuestEngine.getInstance().getQuestNpc(((Npc) object).getNpcId());
			if (data != null) {
				for (int id : data.getOnQuestStart())
					if (!questIds.contains(id))
						questIds.add(id);
			}
		}
		if (object instanceof Player){
			Player pl = ((Player)object);
			if (this.getParent().isPossibleFly())
				pl.setInsideZoneType(ZoneType.FLY);
			worldMapPlayers.put(object.getObjectId(), pl);
			
			//CustomRank
			if(CustomFun.CUSTOM_RANK_ENABLED){
				if(!worldMapHistoryPlayers.contains(pl.getObjectId())){
					pl.getCustomPlayerRank().checkLvUp();
					worldMapHistoryPlayers.add(pl.getObjectId());
				}
			}
		}
	}

	/**
	 * @param object
	 */
	public void removeObject(AionObject object) {
		worldMapObjects.remove(object.getObjectId());
		if (object instanceof Player){
			if (this.getParent().isPossibleFly())
				((Player)object).unsetInsideZoneType(ZoneType.FLY);
			worldMapPlayers.remove(object.getObjectId());
		}
	}

	/**
	 * @param npcId
	 * 
	 * @return npc
	 */
	public Npc getNpc(int npcId) {
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();) {
			VisibleObject obj = iter.next();
			if (obj instanceof Npc) {
				Npc npc = (Npc) obj;
				if (npc.getNpcId() == npcId) {
					return npc;
				}
			}
		}
		return null;
	}

	public List<Player> getPlayersInside() {
		List<Player> playersInside = new ArrayList<Player>();
		Iterator<Player> players = playerIterator();
		while (players.hasNext()) {
			playersInside.add(players.next());
		}
		return playersInside;
	}

	/**
	 * @param npcId
	 * 
	 * @return List<npc>
	 */
	public List<Npc> getNpcs(int npcId) {
		List<Npc> npcs = new ArrayList<Npc>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();) {
			VisibleObject obj = iter.next();
			if (obj instanceof Npc) {
				Npc npc = (Npc) obj;
				if (npc.getNpcId() == npcId) {
					npcs.add(npc);
				}
			}
		}
		return npcs;
	}

	/**
	 * 
	 * @return List<npc>
	 */
	public List<Npc> getNpcs() {
		List<Npc> npcs = new ArrayList<Npc>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();) {
			VisibleObject obj = iter.next();
			if (obj instanceof Npc) {
				npcs.add((Npc) obj);
			}
		}
		return npcs;
	}

	/**
	 * @param npcId
	 * 
	 * @return List<npc>
	 */
	public Map<Integer,StaticDoor> getDoors() {
		Map<Integer,StaticDoor> doors = new HashMap<Integer,StaticDoor>();
		for (Iterator<VisibleObject> iter = objectIterator(); iter.hasNext();) {
			VisibleObject obj = iter.next();
			if (obj instanceof StaticDoor) {
				StaticDoor door = (StaticDoor) obj;
				doors.put(door.getSpawn().getStaticId(), door);
			}
		}
		return doors;
	}

	/**
	 * @return the instanceIndex
	 */
	public int getInstanceId() {
		return instanceId;
	}

	/**
	 * Check player is in instance
	 * 
	 * @param objId
	 * @return
	 */
	public boolean isInInstance(int objId) {
		return worldMapPlayers.containsKey(objId);
	}

	/**
	 * @return
	 */
	public Iterator<VisibleObject> objectIterator() {
		return worldMapObjects.values().iterator();
	}

	/**
	 * @return
	 */
	public Iterator<Player> playerIterator() {
		return worldMapPlayers.values().iterator();
	}

	public void registerGroup(PlayerGroup group) {
		registeredGroup = group;
		register(group.getTeamId());
	}

	public void registerGroup(PlayerAlliance group) {
		registredAlliance = group;
		register(group.getObjectId());
	}

	public PlayerAlliance getRegistredAlliance() {
		return registredAlliance;
	}

	/**
	 * @param objectId
	 */
	public void register(int objectId) {
		registeredObjects.add(objectId);
	}

	/**
	 * @param objectId
	 * @return
	 */
	public boolean isRegistered(int objectId) {
		return registeredObjects.contains(objectId);
	}

	/**
	 * @return the emptyInstanceTask
	 */
	public Future<?> getEmptyInstanceTask() {
		return emptyInstanceTask;
	}

	/**
	 * @param emptyInstanceTask
	 *          the emptyInstanceTask to set
	 */
	public void setEmptyInstanceTask(Future<?> emptyInstanceTask) {
		this.emptyInstanceTask = emptyInstanceTask;
	}

	/**
	 * @return the registeredGroup
	 */
	public PlayerGroup getRegisteredGroup() {
		return registeredGroup;
	}

	/**
	 * @return
	 */
	public int playersCount() {
		return worldMapPlayers.size();
	}

	public FastList<Integer> getQuestIds() {
		return questIds;
	}

	public final InstanceHandler getInstanceHandler() {
		return instanceHandler;
	}

	public final void setInstanceHandler(InstanceHandler instanceHandler) {
		this.instanceHandler = instanceHandler;
	}
	
	public Player getPlayer(Integer object) {
		for (Player player : worldMapPlayers.values()) {
			if (object == player.getObjectId()) {
				return player;
			}
		}
		return null;
	}

	/**
	 * @param visitor
	 */
	public void doOnAllPlayers(Visitor<Player> visitor) {
		try {
			for (Player player : worldMapPlayers.values())
				if (player != null) {
					visitor.visit(player);
				}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all players" + ex);
		}
	}
	
	protected ZoneInstance[] filterZones(int mapId, int regionId, float startX, float startY, float minZ, float maxZ) {
		if (zones.isEmpty()) {
			log.debug("No zones for map " + mapId);
			return new ZoneInstance[0];
		}

		List<ZoneInstance> regionZones = new ArrayList<ZoneInstance>();
		RegionZone regionZone = new RegionZone(startX, startY, minZ, maxZ);

		for (ZoneInstance zoneInstance : zones.values()) {
			if (zoneInstance.getAreaTemplate().intersectsRectangle(regionZone))
				regionZones.add(zoneInstance);
		}
		return regionZones.toArray(new ZoneInstance[regionZones.size()]);
	}
	
	/**
	 * @param player
	 * @param zoneName
	 * @return
	 */
	public boolean isInsideZone(VisibleObject object, ZoneName zoneName) {
		ZoneInstance zoneTemplate = zones.get(zoneName);
		if (zoneTemplate == null)
			return false;
		return isInsideZone(object.getPosition(), zoneName);
	}

	/**
	 * @param pos
	 * @param zone
	 * @return
	 */
	public boolean isInsideZone(WorldPosition pos, ZoneName zoneName) {
		MapRegion mapRegion = this.getRegion(pos.getX(), pos.getY(), pos.getZ());
		return mapRegion.isInsideZone(zoneName, pos.getX(), pos.getY(), pos.getZ());
	}

	public void  setSoloPlayerObj(Player player) {
		soloPlayer = player.getObjectId();
		soloPlayerp = player;
	}

	public Integer getSoloPlayerObj() {
		return soloPlayer;
	}
	public Player getSoloPlayer() {
		return soloPlayerp;
	}
}
