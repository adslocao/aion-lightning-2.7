/*
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
package com.aionemu.gameserver.dataholders;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An instance of this class is the result of data loading.
 * 
 * @author Luno, orz Modified by Wakizashi
 */
@XmlRootElement(name = "ae_static_data")
@XmlAccessorType(XmlAccessType.NONE)
public class StaticData {

	@XmlElement(name = "world_maps")
	public WorldMapsData worldMapsData;

	@XmlElement(name = "npc_trade_list")
	public TradeListData tradeListData;

	@XmlElement(name = "npc_teleporter")
	public TeleporterData teleporterData;

	@XmlElement(name = "teleport_location")
	public TeleLocationData teleLocationData;

	@XmlElement(name = "bind_points")
	public BindPointData bindPointData;

	@XmlElement(name = "quests")
	public QuestsData questData;

	@XmlElement(name = "quest_scripts")
	public XMLQuests questsScriptData;

	@XmlElement(name = "player_experience_table")
	public PlayerExperienceTable playerExperienceTable;

	@XmlElement(name = "player_stats_templates")
	public PlayerStatsData playerStatsData;

	@XmlElement(name = "summon_stats_templates")
	public SummonStatsData summonStatsData;

	@XmlElement(name = "item_templates")
	public ItemData itemData;

	@XmlElement(name = "npc_templates")
	public NpcData npcData;
	
	@XmlElement(name = "npc_shouts")
	public NpcShoutData npcShoutData;

	@XmlElement(name = "player_initial_data")
	public PlayerInitialData playerInitialData;

	@XmlElement(name = "skill_data")
	public SkillData skillData;
	
	@XmlElement(name = "motion_times")
	public MotionData motionData;

	@XmlElement(name = "skill_tree")
	public SkillTreeData skillTreeData;

	@XmlElement(name = "cube_expander")
	public CubeExpandData cubeExpandData;

	@XmlElement(name = "warehouse_expander")
	public WarehouseExpandData warehouseExpandData;

	@XmlElement(name = "player_titles")
	public TitleData titleData;

	@XmlElement(name = "gatherable_templates")
	public GatherableData gatherableData;

	@XmlElement(name = "npc_walker")
	public WalkerData walkerData;

	@XmlElement(name = "zones")
	public ZoneData zoneData;

	@XmlElement(name = "goodslists")
	public GoodsListData goodsListData;

	@XmlElement(name = "tribe_relations")
	public TribeRelationsData tribeRelationsData;

	@XmlElement(name = "recipe_templates")
	public RecipeData recipeData;

	@XmlElement(name = "portal_templates")
	public PortalData portalData;

	@XmlElement(name = "chest_templates")
	public ChestData chestData;

	@XmlElement(name = "staticdoor_templates")
	public StaticDoorData staticDoorData;

	@XmlElement(name = "item_sets")
	public ItemSetData itemSetData;
	
	@XmlElement(name = "npc_factions")
	public NpcFactionsData npcFactionsData;

	@XmlElement(name = "npc_skill_templates")
	public NpcSkillData npcSkillData;

	@XmlElement(name = "pet_skill_templates")
	public PetSkillData petSkillData;

	@XmlElement(name = "siege_locations")
	public SiegeLocationData siegeLocationData;

	@XmlElement(name = "fly_rings")
	public FlyRingData flyRingData;

	@XmlElement(name = "shields")
	public ShieldData shieldData;

	@XmlElement(name = "pets")
	public PetData petData;

	@XmlElement(name = "guides")
	public GuideHtmlData guideData;

	@XmlElement(name = "roads")
	public RoadData roadData;

	@XmlElement(name = "instance_cooltimes")
	public InstanceCooltimeData instanceCooltimeData;

	@XmlElement(name = "decomposable_items")
	public DecomposableItemsData decomposableItemsData;

	@XmlElement(name = "ai_templates")
	public AIData aiData;

	@XmlElement(name = "flypath_template")
	public FlyPathData flyPath;
	
	@XmlElement(name = "windstreams")
	public WindstreamData windstreamsData;
	
	@XmlElement(name = "item_restriction_cleanups")
	public ItemRestrictionCleanupData itemCleanup;

	@XmlElement(name = "assembled_npcs")
	public AssembledNpcsData assembledNpcData;

	@XmlElement(name = "cosmetic_items")
	public CosmeticItemsData cosmeticItemsData;

	@XmlElement(name = "npc_drops")
	public NpcDropData npcDropData;

	@XmlElement(name = "auto_groups")
	public AutoGroupData autoGroupData;
	
	@XmlElement(name = "events_config")
	public EventData eventData;

	@XmlElement(name = "spawns")
	public SpawnsData2 spawnsData2;
	
	@XmlElement(name="pvp_zone_data")
	public PvpZoneData pvpZoneData;
	
	@XmlElement(name = "item_groups")
	public ItemGroupsData itemGroupsData;

	// JAXB callback
	@SuppressWarnings("unused")
	private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		DataManager.log.info("Loaded world maps data: " + worldMapsData.size() + " maps");
		DataManager.log.info("Loaded player exp table: " + playerExperienceTable.getMaxLevel() + " levels");
		DataManager.log.info("Loaded " + playerStatsData.size() + " player stat templates");
		DataManager.log.info("Loaded " + summonStatsData.size() + " summon stat templates");
		DataManager.log.info("Loaded " + itemCleanup.size() + " item cleanup entries");
		DataManager.log.info("Loaded " + itemData.size() + " item templates");
		DataManager.log.info("Loaded " + itemGroupsData.size() + " item group templates");
		DataManager.log.info("Loaded " + npcData.size() + " npc templates");
		DataManager.log.info("Loaded " + npcShoutData.size() + " npc shout templates");
		DataManager.log.info("Loaded " + petData.size() + " pet templates");
		DataManager.log.info("Loaded " + playerInitialData.size() + " initial player templates");
		DataManager.log.info("Loaded " + tradeListData.size() + " trade lists");
		DataManager.log.info("Loaded " + teleporterData.size() + " npc teleporter templates");
		DataManager.log.info("Loaded " + teleLocationData.size() + " teleport locations");
		DataManager.log.info("Loaded " + skillData.size() + " skill templates");
		DataManager.log.info("Loaded " + motionData.size() + " motion times");
		DataManager.log.info("Loaded " + skillTreeData.size() + " skill learn entries");
		DataManager.log.info("Loaded " + cubeExpandData.size() + " cube expand entries");
		DataManager.log.info("Loaded " + warehouseExpandData.size() + " warehouse expand entries");
		DataManager.log.info("Loaded " + bindPointData.size() + " bind point entries");
		DataManager.log.info("Loaded " + questData.size() + " quest data entries");
		DataManager.log.info("Loaded " + gatherableData.size() + " gatherable entries");
		DataManager.log.info("Loaded " + titleData.size() + " title entries");
		DataManager.log.info("Loaded " + walkerData.size() + " walker routes");
		DataManager.log.info("Loaded " + zoneData.size() + " zone entries");
		DataManager.log.info("Loaded " + goodsListData.size() + " goodslist entries");
		DataManager.log.info("Loaded " + tribeRelationsData.size() + " tribe relation entries");
		DataManager.log.info("Loaded " + recipeData.size() + " recipe entries");
		DataManager.log.info("Loaded " + portalData.size() + " portal entries");
		DataManager.log.info("Loaded " + chestData.size() + " chest locations");
		DataManager.log.info("Loaded " + staticDoorData.size() + " static door locations");
		DataManager.log.info("Loaded " + itemSetData.size() + " item set entries");
		DataManager.log.info("Loaded " + npcFactionsData.size() + " npc factions");
		DataManager.log.info("Loaded " + npcSkillData.size() + " npc skill list entries");
		DataManager.log.info("Loaded " + petSkillData.size() + " pet skill list entries");
		DataManager.log.info("Loaded " + siegeLocationData.size() + " siege location entries");
		DataManager.log.info("Loaded " + flyRingData.size() + " fly ring entries");
		DataManager.log.info("Loaded " + shieldData.size() + " shield entries");
		DataManager.log.info("Loaded " + petData.size() + " pet entries");
		DataManager.log.info("Loaded " + guideData.size() + " guide entries");
		DataManager.log.info("Loaded " + roadData.size() + " road entries");
		DataManager.log.info("Loaded " + instanceCooltimeData.size() + " instance cooltime entries");
		DataManager.log.info("Loaded " + decomposableItemsData.size() + " decomposable items entries");
		DataManager.log.info("Loaded " + aiData.size() + " ai templates");
		DataManager.log.info("Loaded " + flyPath.size() + " flypath templates");
		DataManager.log.info("Loaded " + windstreamsData.size() + " windstream entries");
		DataManager.log.info("Loaded " + assembledNpcData.size() + " assembled npcs entries");
		DataManager.log.info("Loaded " + cosmeticItemsData.size() + " cosmetic items entries");
		DataManager.log.info("Loaded " + npcDropData.size() + " npc drop entries");
		DataManager.log.info("Loaded " + autoGroupData.size() + " auto group entries");
		DataManager.log.info("Loaded " + spawnsData2.size() + " spawn maps entries");
		DataManager.log.info("Loaded " + eventData.size() + " active events");
		DataManager.log.info("Loaded " + pvpZoneData.size() + " Pvp Zone Positions");
	}
}
