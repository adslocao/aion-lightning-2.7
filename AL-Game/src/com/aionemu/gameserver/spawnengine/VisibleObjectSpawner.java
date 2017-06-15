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
package com.aionemu.gameserver.spawnengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.PetController;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.GroupGate;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.siege.SiegeType;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.chest.ChestTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.CreatureAwareKnownList;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * @author ATracer
 */
public class VisibleObjectSpawner {

	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);
	private static List<Integer> modifiedTemplate = new ArrayList<Integer>();
	private static Map<Integer, List<Trap>> trapLimit = new HashMap<Integer, List<Trap>>();
	private static int TRAP_LIMIT = 2;

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @return
	 */
	protected static VisibleObject spawnNpc(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		NpcType npcType = npcTemplate.getNpcType();

		PortalTemplate pt = DataManager.PORTAL_DATA.getPortalTemplate(npcTemplate.getTemplateId());
		if (pt != null && npcType != NpcType.PORTAL)
			npcTemplate.setNpcType(NpcType.PORTAL);

		ChestTemplate ct = DataManager.CHEST_DATA.getChestTemplate(npcTemplate.getTemplateId());
		if (ct != null && npcType != NpcType.CHEST)
			npcTemplate.setNpcType(NpcType.CHEST);

		Npc npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);

		switch (npcType) {
		case POSTBOX:
		case RESURRECT:
		case PORTAL:
		case CHEST:
		case USEITEM:
			npc.setKnownlist(new PlayerAwareKnownList(npc));
			break;
		default:
			npc.setKnownlist(new NpcKnownList(npc));
		}

		npc.setEffectController(new EffectController(npc));

		if (WalkerFormator.getInstance().processClusteredNpc(npc, instanceIndex))
			return npc;

		try {
			SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		} catch (Exception ex) {
			log.error("Error during spawn of npc {}, world {}, x-y {}-{}",
					new Object[] { npcTemplate.getTemplateId(), spawn.getWorldId(), spawn.getX(), spawn.getY() });
			log.error("Npc {} will be despawned", npcTemplate.getTemplateId(), ex);
			World.getInstance().despawn(npc);
		}
		return npc;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @return
	 */
	protected static VisibleObject spawnSiegeNpc(SiegeSpawnTemplate spawn, int instanceIndex) {
		if (!SiegeConfig.SIEGE_ENABLED)
			return null;

		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null) {
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = null;

		int spawnSiegeId = spawn.getSiegeId();
		SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(spawnSiegeId);

		// Custom change templet compare to online player
		if (loc.isVulnerable() && loc.getType() == SiegeType.FORTRESS
				&& !modifiedTemplate.contains(npcTemplate.getTemplateId())) {
			modifiedTemplate.add(npcTemplate.getTemplateId());
			npcTemplate = calcCustomStat(npcTemplate, spawn.getSiegeRace());
		}

		if ((spawn.isPeace() || loc.isVulnerable()) && spawnSiegeId == loc.getLocationId()
				&& spawn.getSiegeRace() == loc.getRace()) {
			// default: GUARD
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else if (spawn.isAssault() && loc.isVulnerable() && spawn.getSiegeRace().equals(SiegeRace.BALAUR)) {
			// attakers
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		} else {
			return null;
		}
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}

	private static NpcTemplate calcCustomStat(NpcTemplate template, SiegeRace siegeRace) {
		if (!isDuxOrDor(template)) {
			return template;
		}

		Race racePlayer = null;
		float div = 1;
		if (siegeRace.equals(SiegeRace.ELYOS)) {
			racePlayer = Race.ASMODIANS;
		} else if (siegeRace.equals(SiegeRace.ASMODIANS)) {
			racePlayer = Race.ELYOS;
		} else {
			racePlayer = Race.NONE;
			div = 2f;
		}
		int numberOnline = DAOManager.getDAO(PlayerDAO.class).getOnlinePlayerCountBeforeSiege(racePlayer);
		numberOnline = (int) (numberOnline / div);

		float percentLife = (float) Math.min(1, 0.05 + numberOnline * 0.3f / 100f);

		template.getStatsTemplate().setMaxHp((int) (template.getStatsTemplate().getMaxHp() * percentLife));
		// template.getStatsTemplate().setPower((int)(template.getStatsTemplate().getPower()
		// * percentStat));
		// template.getStatsTemplate().setMainHandAttack((int)(template.getStatsTemplate().getMainHandAttack()
		// * percentStat));

		return template;
	}

	private static boolean isDuxOrDor(NpcTemplate template) {
		return template.getRating() == NpcRating.HERO || template.getRating() == NpcRating.LEGENDARY;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @return
	 */
	protected static VisibleObject spawnGatherable(SpawnTemplate spawn, int instanceIndex) {
		int objectId = spawn.getNpcId();
		VisibleObjectTemplate template = DataManager.GATHERABLE_DATA.getGatherableTemplate(objectId);
		Gatherable gatherable = new Gatherable(spawn, template, IDFactory.getInstance().nextId(),
				new GatherableController());
		gatherable.setKnownlist(new PlayerAwareKnownList(gatherable));
		SpawnEngine.bringIntoWorld(gatherable, spawn, instanceIndex);
		return gatherable;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @return
	 */
	public static Trap spawnTrap(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		Trap trap = new Trap(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		trap.setKnownlist(new NpcKnownList(trap));
		trap.setEffectController(new EffectController(trap));
		trap.setCreator(creator);
		trap.getSkillList().addSkill(trap, skillId, 1);
		trap.setVisualState(CreatureVisualState.HIDE1);
		// set proper trap range
		trap.getAi2().onCustomEvent(1,
				DataManager.SKILL_DATA.getSkillTemplate(skillId).getProperties().getTargetDistance());
		SpawnEngine.bringIntoWorld(trap, spawn, instanceIndex);

		PacketSendUtility.broadcastPacket(trap, new SM_PLAYER_STATE(trap));
		// Check nb piege
		List<Trap> traps = trapLimit.get(creator.getObjectId());
		if (traps == null) {
			traps = new ArrayList<Trap>();
		}
		traps.add(trap);
		int i = 0;
		while (i < traps.size()) {
			if (traps.get(i) == null || !traps.get(i).isSpawned()) {
				traps.remove(i);
			} else {
				i++;
			}
		}
		while (traps.size() > TRAP_LIMIT) {
			traps.get(0).getController().onDelete();
			traps.remove(0);
		}
		trapLimit.put(creator.getObjectId(), traps);

		return trap;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @return
	 */
	public static GroupGate spawnGroupGate(SpawnTemplate spawn, int instanceIndex, Creature creator) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		GroupGate groupgate = new GroupGate(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		groupgate.setKnownlist(new PlayerAwareKnownList(groupgate));
		groupgate.setEffectController(new EffectController(groupgate));
		groupgate.setCreator(creator);
		SpawnEngine.bringIntoWorld(groupgate, spawn, instanceIndex);
		return groupgate;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @return
	 */
	public static Kisk spawnKisk(SpawnTemplate spawn, int instanceIndex, Player creator) {
		int npcId = spawn.getNpcId();
		NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		Kisk kisk = new Kisk(IDFactory.getInstance().nextId(), new NpcController(), spawn, template, creator);
		kisk.setKnownlist(new PlayerAwareKnownList(kisk));
		kisk.setCreator(creator);
		kisk.setEffectController(new EffectController(kisk));
		SpawnEngine.bringIntoWorld(kisk, spawn, instanceIndex);
		return kisk;
	}

	/**
	 * @param recipient
	 * @author ViAl Spawns postman for express mail
	 */
	public static Npc spawnPostman(final Player recipient) {
		int npcId = recipient.getRace() == Race.ELYOS ? 798100 : 798101;
		NpcData npcData = DataManager.NPC_DATA;
		NpcTemplate template = npcData.getNpcTemplate(npcId);
		IDFactory iDFactory = IDFactory.getInstance();
		int worldId = recipient.getWorldId();
		int instanceId = recipient.getInstanceId();
		float x = recipient.getX();
		float y = recipient.getY();
		float z = recipient.getZ();
		byte heading = recipient.getHeading();
		SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		final Npc postman = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		postman.setKnownlist(new PlayerAwareKnownList(postman));
		postman.setEffectController(new EffectController(postman));
		// set creator id in ai
		postman.getAi2().onCustomEvent(1, recipient.getObjectId());
		SpawnEngine.bringIntoWorld(postman, spawn, instanceId);
		recipient.setPostman(postman);
		return postman;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @param skillId
	 * @param level
	 * @return
	 */
	public static Servant spawnServant(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId, int level,
			NpcObjectType objectType) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);

		int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		byte servantLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);

		Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate,
				servantLevel);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(objectType);
		servant.getSkillList().addSkill(servant, skillId, 1);
		servant.setTarget(creator.getTarget());
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		return servant;
	}

	/**
	 * @param spawn
	 * @param instanceIndex
	 * @param creator
	 * @param attackCount
	 * @return
	 */
	public static Homing spawnHoming(SpawnTemplate spawn, int instanceIndex, Creature creator, int attackCount,
			int skillId, int level) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);

		int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		byte homingLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);

		Homing homing = new Homing(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate,
				homingLevel);
		homing.setState(CreatureState.WEAPON_EQUIPPED);
		homing.setKnownlist(new NpcKnownList(homing));
		homing.setEffectController(new EffectController(homing));
		homing.setCreator(creator);
		homing.setAttackCount(attackCount);
		SpawnEngine.bringIntoWorld(homing, spawn, instanceIndex);
		return homing;
	}

	/**
	 * @param creator
	 * @param npcId
	 * @param skillLevel
	 * @return
	 */
	public static Summon spawnSummon(Player creator, int npcId, int skillId, int skillLevel) {
		float x = creator.getX();
		float y = creator.getY();
		float z = creator.getZ();
		byte heading = creator.getHeading();
		int worldId = creator.getWorldId();
		int instanceId = creator.getInstanceId();

		SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, npcId, x, y, z, heading);
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);

		skillLevel = SkillLearnService.getSkillLearnLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		byte level = (byte) SkillLearnService.getSkillMinLevel(skillId, creator.getCommonData().getLevel(), skillLevel);

		Summon summon = new Summon(IDFactory.getInstance().nextId(), new SummonController(), spawn, npcTemplate, level);
		summon.setKnownlist(new CreatureAwareKnownList(summon));
		summon.setEffectController(new EffectController(summon));
		summon.setMaster(creator);
		summon.getLifeStats().synchronizeWithMaxStats();

		SpawnEngine.bringIntoWorld(summon, spawn, instanceId);
		return summon;
	}

	/**
	 * @param player
	 * @param petId
	 * @return
	 */
	public static Pet spawnPet(Player player, int petId) {

		PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (petCommonData == null) {
			return null;
		}
		PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		if (petTemplate == null)
			return null;

		PetController controller = new PetController();
		Pet pet = new Pet(petTemplate, controller, petCommonData, player);
		pet.setKnownlist(new PlayerAwareKnownList(pet));
		player.setToyPet(pet);

		float x = player.getX();
		float y = player.getY();
		float z = player.getZ();
		byte heading = player.getHeading();
		int worldId = player.getWorldId();
		int instanceId = player.getInstanceId();
		SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, petId, x, y, z, heading);

		SpawnEngine.bringIntoWorld(pet, spawn, instanceId);
		return pet;
	}
}
