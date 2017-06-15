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
package com.aionemu.gameserver.world.zone;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.templates.zone.ZoneInfo;
import com.aionemu.gameserver.world.zone.handler.GeneralZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneHandler;
import com.aionemu.gameserver.world.zone.handler.ZoneHandlerClassListener;
import com.aionemu.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * @author ATracer modified by antness
 */
public final class ZoneService implements GameEngine {

	private static final Logger log = LoggerFactory.getLogger(ZoneService.class);
	private TIntObjectHashMap<List<ZoneInfo>> zoneByMapIdMap;
	private final Map<ZoneName, Class<? extends ZoneHandler>> handlers = new HashMap<ZoneName, Class<? extends ZoneHandler>>();
	public static final ZoneHandler DUMMY_ZONE_HANDLER = new GeneralZoneHandler();

	private static ScriptManager scriptManager = new ScriptManager();
	public static final File ZONE_DESCRIPTOR_FILE = new File("./data/scripts/system/zonehandlers.xml");

	private ZoneService() {
		this.zoneByMapIdMap = DataManager.ZONE_DATA.getZones();
	}

	public static ZoneService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final ZoneService instance = new ZoneService();
	}

	public ZoneHandler getNewZoneHandler(ZoneName zoneName) {
		Class<? extends ZoneHandler> zoneClass = handlers.get(zoneName);
		ZoneHandler zoneHandler = null;
		if (zoneClass != null) {
			try {
				zoneHandler = zoneClass.newInstance();
			}
			catch (IllegalAccessException ex){
				log.warn("Can't instantiate zone handler " + zoneName, ex);
			}
			catch (Exception ex) {
				log.warn("Can't instantiate zone handler " + zoneName, ex);
			}
		}
		if (zoneHandler == null) {
			zoneHandler = DUMMY_ZONE_HANDLER;
		}
		return zoneHandler;
	}

	/**
	 * @param handler
	 */
	public final void addZoneHandlerClass(Class<? extends ZoneHandler> handler) {
		ZoneNameAnnotation idAnnotation = handler.getAnnotation(ZoneNameAnnotation.class);
		if (idAnnotation != null) {
				String[] zoneNames = idAnnotation.value().split(" ");
				for (String zoneNameString : zoneNames){
					try {
						ZoneName zoneName = ZoneName.valueOf(zoneNameString.trim());
						handlers.put(zoneName, handler);
					}
					catch (Exception e) {
						log.warn("Missing ZoneName: " + idAnnotation.value());
					}
				}
		}
	}

	public final void addZoneHandlerClass(ZoneName zoneName, Class<? extends ZoneHandler> handler) {
			handlers.put(zoneName, handler);
	}

	@Override
	public void load() {
		log.info("Zone engine load started");
		scriptManager = new ScriptManager();

		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ZoneHandlerClassListener());
		scriptManager.setGlobalClassListener(acl);

		try {
			scriptManager.load(ZONE_DESCRIPTOR_FILE);
		}
		catch (IllegalStateException e){
			log.warn("Can't initialize instance handlers.", e.getMessage());
		}
		catch (Exception e) {
			throw new GameServerError("Can't initialize instance handlers.", e);
		}
		log.info("Loaded " + handlers.size() + " zone handlers.");
	}

	@Override
	public void shutdown() {
		log.info("Zone engine shutdown started");
		scriptManager.shutdown();
		scriptManager = null;
		handlers.clear();
		log.info("Zone engine shutdown complete");
	}

	/**
	 * @param mapId
	 * @return
	 */
	public Map<ZoneName, ZoneInstance> getZoneInstancesByWorldId(int mapId) {
		Map<ZoneName, ZoneInstance> zones = new HashMap<ZoneName, ZoneInstance>();
		Collection<ZoneInfo> areas = this.zoneByMapIdMap.get(mapId);
		if (areas == null)
			return Collections.emptyMap();
		for (ZoneInfo area : this.zoneByMapIdMap.get(mapId)){
			ZoneInstance instance = null;
			switch (area.getZoneTemplate().getZoneType()){
				case FLY:
					instance = new FlyZoneInstance(mapId, area);
					break;
				case FORT:
					instance = new SiegeZoneInstance(mapId, area);
					SiegeLocation siege = DataManager.SIEGE_LOCATION_DATA.getSiegeLocations().get(area.getZoneTemplate().getSiegeId().get(0));
					if (siege != null)
						siege.addZone((SiegeZoneInstance) instance);
					break;
				case ARTIFACT:
					instance = new SiegeZoneInstance(mapId, area);
					for(int artifactId: area.getZoneTemplate().getSiegeId()){
						SiegeLocation artifact = DataManager.SIEGE_LOCATION_DATA.getArtifacts().get(artifactId);
						artifact.addZone((SiegeZoneInstance) instance);
					}
					break;
				case PVP:
					instance = new PvPZoneInstance(mapId, area);
					break;
				default:
					instance = new ZoneInstance(mapId, area);
			}
			instance.addHandler(getNewZoneHandler(area.getZoneTemplate().getName()));
			zones.put(area.getZoneTemplate().getName(), instance);
		}
		return zones;
	}
}
