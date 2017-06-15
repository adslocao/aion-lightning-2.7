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
package com.aionemu.gameserver.dataholders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.loadingutils.XmlDataLoader;
import com.aionemu.gameserver.utils.Util;

/**
 * This class is holding whole static data, that is loaded from /data/static_data directory.<br>
 * The data is loaded by XMLDataLoader using JAXB.<br>
 * <br>
 * This class temporarily also contains data loaded from txt files by DataLoaders. It'll be changed later.
 * 
 * @author Luno , orz modified by Wakizashi
 */

public final class DataManager {

	static Logger log = LoggerFactory.getLogger(DataManager.class);

	public static NpcData NPC_DATA;
	
	public static NpcDropData NPC_DROP_DATA;
	
	public static NpcShoutData NPC_SHOUT_DATA;

	public static GatherableData GATHERABLE_DATA;

	public static WorldMapsData WORLD_MAPS_DATA;

	public static TradeListData TRADE_LIST_DATA;

	public static PlayerExperienceTable PLAYER_EXPERIENCE_TABLE;

	public static TeleporterData TELEPORTER_DATA;

	public static TeleLocationData TELELOCATION_DATA;

	public static CubeExpandData CUBEEXPANDER_DATA;

	public static WarehouseExpandData WAREHOUSEEXPANDER_DATA;

	public static BindPointData BIND_POINT_DATA;

	public static QuestsData QUEST_DATA;

	public static XMLQuests XML_QUESTS;

	public static PlayerStatsData PLAYER_STATS_DATA;

	public static SummonStatsData SUMMON_STATS_DATA;

	public static ItemData ITEM_DATA;

	public static TitleData TITLE_DATA;

	public static PlayerInitialData PLAYER_INITIAL_DATA;

	public static SkillData SKILL_DATA;
	
	public static MotionData MOTION_DATA;

	public static SkillTreeData SKILL_TREE_DATA;

	public static GuideHtmlData GUIDE_HTML_DATA;

	public static WalkerData WALKER_DATA;

	public static ZoneData ZONE_DATA;

	public static GoodsListData GOODSLIST_DATA;

	public static TribeRelationsData TRIBE_RELATIONS_DATA;

	public static RecipeData RECIPE_DATA;

	public static PortalData PORTAL_DATA;

	public static ChestData CHEST_DATA;

	public static StaticDoorData STATICDOOR_DATA;

	public static ItemSetData ITEM_SET_DATA;

	public static NpcFactionsData NPC_FACTIONS_DATA;

	public static NpcSkillData NPC_SKILL_DATA;

	public static PetSkillData PET_SKILL_DATA;

	public static SiegeLocationData SIEGE_LOCATION_DATA;

	public static FlyRingData FLY_RING_DATA;

	public static ShieldData SHIELD_DATA;

	public static PetData PET_DATA;

	public static RoadData ROAD_DATA;

	public static InstanceCooltimeData INSTANCE_COOLTIME_DATA;

	public static DecomposableItemsData DECOMPOSABLE_ITEMS_DATA;

	public static AIData AI_DATA;

	public static FlyPathData FLY_PATH;

	public static WindstreamData WINDSTREAM_DATA;

	public static ItemRestrictionCleanupData ITEM_CLEAN_UP;

	public static AssembledNpcsData ASSEMBLED_NPC_DATA;

	public static CosmeticItemsData COSMETIC_ITEMS_DATA;
	
	public static ItemGroupsData ITEM_GROUPS_DATA;

	public static SpawnsData2 SPAWNS_DATA2;
	
	public static AutoGroupData AUTO_GROUP;

	public static EventData	 EVENT_DATA;
	
	public static PvpZoneData PVP_ZONE_DATA;

	private XmlDataLoader loader;

	/**
	 * Constructor creating <tt>DataManager</tt> instance.<br>
	 * NOTICE: calling constructor implies loading whole data from /data/static_data immediately
	 */
	public static final DataManager getInstance() {
		return SingletonHolder.instance;
	}

	private DataManager() {
		Util.printSection("Static Data");

		this.loader = XmlDataLoader.getInstance();

		long start = System.currentTimeMillis();
		StaticData data = loader.loadStaticData();
		long time = System.currentTimeMillis() - start;

		WORLD_MAPS_DATA = data.worldMapsData;
		PLAYER_EXPERIENCE_TABLE = data.playerExperienceTable;
		PLAYER_STATS_DATA = data.playerStatsData;
		SUMMON_STATS_DATA = data.summonStatsData;
		ITEM_CLEAN_UP = data.itemCleanup;
		ITEM_DATA = data.itemData;
		NPC_DATA = data.npcData;
		NPC_DROP_DATA = data.npcDropData;
		NPC_SHOUT_DATA = data.npcShoutData;
		GATHERABLE_DATA = data.gatherableData;
		PLAYER_INITIAL_DATA = data.playerInitialData;
		SKILL_DATA = data.skillData;
		MOTION_DATA = data.motionData;
		SKILL_TREE_DATA = data.skillTreeData;
		TITLE_DATA = data.titleData;
		TRADE_LIST_DATA = data.tradeListData;
		TELEPORTER_DATA = data.teleporterData;
		TELELOCATION_DATA = data.teleLocationData;
		CUBEEXPANDER_DATA = data.cubeExpandData;
		WAREHOUSEEXPANDER_DATA = data.warehouseExpandData;
		BIND_POINT_DATA = data.bindPointData;
		QUEST_DATA = data.questData;
		XML_QUESTS = data.questsScriptData;
		ZONE_DATA = data.zoneData;
		WALKER_DATA = data.walkerData;
		GOODSLIST_DATA = data.goodsListData;
		TRIBE_RELATIONS_DATA = data.tribeRelationsData;
		RECIPE_DATA = data.recipeData;
		PORTAL_DATA = data.portalData;
		CHEST_DATA = data.chestData;
		STATICDOOR_DATA = data.staticDoorData;
		ITEM_SET_DATA = data.itemSetData;
		NPC_FACTIONS_DATA = data.npcFactionsData;
		NPC_SKILL_DATA = data.npcSkillData;
		PET_SKILL_DATA = data.petSkillData;
		SIEGE_LOCATION_DATA = data.siegeLocationData;
		FLY_RING_DATA = data.flyRingData;
		SHIELD_DATA = data.shieldData;
		PET_DATA = data.petData;
		GUIDE_HTML_DATA = data.guideData;
		ROAD_DATA = data.roadData;
		INSTANCE_COOLTIME_DATA = data.instanceCooltimeData;
		DECOMPOSABLE_ITEMS_DATA = data.decomposableItemsData;
		AI_DATA = data.aiData;
		FLY_PATH = data.flyPath;
		WINDSTREAM_DATA = data.windstreamsData;
		ASSEMBLED_NPC_DATA = data.assembledNpcData;
		COSMETIC_ITEMS_DATA = data.cosmeticItemsData;
		SPAWNS_DATA2 = data.spawnsData2;
		ITEM_GROUPS_DATA = data.itemGroupsData;
		AUTO_GROUP = data.autoGroupData;
		EVENT_DATA = data.eventData;
		PVP_ZONE_DATA = data.pvpZoneData;
		ITEM_DATA.cleanup();
		
		// some sexy time message
		long seconds = time / 1000;

		String timeMsg = seconds > 0 ? seconds + " seconds" : time + " miliseconds";

		log.info("##### [Static Data loaded in: " + timeMsg + "] #####");
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DataManager instance = new DataManager();
	}
}
