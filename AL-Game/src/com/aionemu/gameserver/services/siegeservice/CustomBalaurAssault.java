package com.aionemu.gameserver.services.siegeservice;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpcPart;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_ASSEMBLER;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import java.util.Iterator;
import javolution.util.FastList;

/**
 * @author synchro2
 */
public class CustomBalaurAssault {

	private static final CustomBalaurAssault instance = new CustomBalaurAssault();

	public static CustomBalaurAssault getInstance() {
		return instance;
	}

	public static void startCheck(final SiegeNpc target) {
		SiegeRace targetRace = selectTarget();
		if (targetRace.equals(SiegeRace.BALAUR)) {
			return;
		}
		if (!((targetRace.equals(SiegeRace.ELYOS) && (target.getRace().equals(Race.GCHIEF_LIGHT)))
				|| (targetRace.equals(SiegeRace.ASMODIANS) && (target.getRace().equals(Race.GCHIEF_DARK))))) {
			return;
		}
		startAssault(target);
	}

	private static SiegeRace selectTarget() {
		float elyosInfl = Influence.getInstance().getElyos();
		float asmosInfl = Influence.getInstance().getAsmos();
		//float balaurInfl = Influence.getInstance().getBalaur();
		SiegeRace targetRace = SiegeRace.BALAUR;

		if ((Rnd.get() < asmosInfl)) {
			targetRace = SiegeRace.ASMODIANS;
		}
		else if (Rnd.get() < elyosInfl) {
			targetRace = SiegeRace.ELYOS;
		}

		return targetRace;
	}

	private static void startAssault(SiegeNpc target) {
		int despawnTime = 3600;
		int amount = Rnd.get(50, 100);
		int radius1 = Rnd.get(5, 15);
		int radius2 = Rnd.get(20, 35);
		float x = target.getX();
		float y = target.getY();
		float z = target.getZ();
		byte heading = target.getHeading();
		int worldId = target.getWorldId();
		int templateId;
		SiegeSpawnTemplate spawn = null;

		float interval = (float) (Math.PI * 2.0f / (amount/2));
		float x1;
		float y1;

		VisibleObject visibleObject;
		List<VisibleObject> despawnList = new ArrayList<VisibleObject>();

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(210799);
		idList.add(211961);
		idList.add(213831);
		idList.add(253739);
		idList.add(210566);
		idList.add(210745);
		idList.add(210997);
		idList.add(213831);
		idList.add(213547);
		idList.add(253739);
		idList.add(210942);
		idList.add(212631);
		idList.add(210997);
		idList.add(255704);
		idList.add(211962);
		idList.add(213240);
		idList.add(214387);
		idList.add(213547);
		idList.add(250187);
		idList.add(250187);
		idList.add(250187);
		idList.add(250182);
		idList.add(250182);
		idList.add(250182);
		idList.add(250187);

		for( int i = 0; amount > i; i++) {
			int hateValue;
			if(i < (amount/2)) {
				x1 = (float)(Math.cos( interval * i ) * radius1);
				y1 = (float)(Math.sin( interval * i ) * radius1);
				hateValue = 5000;
			}
			else {
				x1 = (float)(Math.cos( interval * i ) * radius2);
				y1 = (float)(Math.sin( interval * i ) * radius2);
				hateValue = 500;
			}
			templateId = idList.get((int)(Math.random() * idList.size()));
			spawn = SpawnEngine.addNewSiegeSpawn(worldId, templateId, x + x1 , y + y1, z, heading);

			visibleObject = SpawnEngine.spawnObject(spawn, 1);
			despawnList.add(visibleObject);

			Creature attaker = (Creature)visibleObject;
			attaker.getAggroList().addHate((Creature)target, hateValue, false);
		}

		despawnAttakers(despawnList, despawnTime);
		spawnRegularBalaurs(target.getSiegeId());
		spawnDredgion();
	}

	private static void spawnRegularBalaurs(int siegeLocationId) {
//		List<SiegeSpawnTemplate> siegeSpawns = DataManager.SPAWNS_DATA2.getSiegeSpawnsByLocId(siegeLocationId);
//		for (SiegeSpawnTemplate st : siegeSpawns) {
//			if (st.getSiegeRace() != SiegeRace.BALAUR) {
//				continue;
//			}
//			SpawnEngine.spawnObject(st, 1);
//		}
	}

	private static void spawnDredgion() {
		int spawnId = 1;
		AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
		FastList<AssembledNpcPart> assembledPatrs = new FastList<AssembledNpcPart>();
		for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart : template.getAssembledNpcPartTemplates()) {
			assembledPatrs.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
		}
		AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledPatrs);
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		Player findedPlayer = null;
		while (iter.hasNext()) {
			findedPlayer = iter.next();
			PacketSendUtility.sendPacket(findedPlayer, new SM_NPC_ASSEMBLER(npc));
		}
	}

	private static void despawnAttakers(final List<VisibleObject> despawnList, final int despawnTime) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				//int despawnCount = 0;
				for(VisibleObject visObj : despawnList)	{
					if(visObj != null && visObj.isSpawned()) {
						visObj.getController().delete();
						//despawnCount++;
					}
				}
			}
		}, despawnTime * 1000);
	}
}
