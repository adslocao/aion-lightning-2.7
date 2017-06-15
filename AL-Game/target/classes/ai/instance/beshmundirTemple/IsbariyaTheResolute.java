package ai.instance.beshmundirTemple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("isbariya2")
public class IsbariyaTheResolute extends AggressiveNpcAI2 {

	protected int difficulty = 0;
	private int stage = 0;
	private boolean startFigth = false;
	private Future<?> skillTaskArtos;
	private Future<?> spawnSoulsTask;
	private Future<?> spawnEnergyTask;
	private Future<?> spawnServantTask;
	private List<Point3D> soulLocations = new ArrayList<Point3D>();

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!startFigth) {
			startFigth = true;
			difficulty = getPosition().getWorldMapInstance().getInstanceHandler().getDif(getNpcId());
			NpcShoutsService.getInstance().sendMsg(getOwner(), 342051, getObjectId(), 0, 1000);
			getPosition().getWorldMapInstance().getDoors().get(535).setOpen(false);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		soulLocations.add(new Point3D(1605.5f, 1607.8f, 306.874f));
		soulLocations.add(new Point3D(1597.5f, 1605.8f, 306.874f));
		soulLocations.add(new Point3D(1590.5f, 1598.8f, 306.874f));
		soulLocations.add(new Point3D(1590.5f, 1588.8f, 306.874f));
		soulLocations.add(new Point3D(1603.5f, 1579.8f, 306.874f));
		soulLocations.add(new Point3D(1612.5f, 1582.8f, 306.874f));
		soulLocations.add(new Point3D(1616.5f, 1591.8f, 306.874f));
		soulLocations.add(new Point3D(1614.5f, 1600.8f, 306.874f));
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 342055, getObjectId(), 0, 0);
		getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
		cancelTask();
	}

	@Override
	protected void handleBackHome() {
		NpcShoutsService.getInstance().sendMsg(getOwner(), 342056, getObjectId(), 0, 0);
		super.handleBackHome();
		getOwner().getEffectController().removeAllEffects();
		getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
		cancelTask();
	}

	private void checkPercentage(int hpPercentage) {
		if(hpPercentage <= 100 && stage < 1){
			stage = 1;
			doSkillArtos(20);
		}
		if (hpPercentage <= 75 && stage < 2) {
			stage = 2;
			//NpcShoutsService.getInstance().sendMsg(getOwner(), 1400460);
			spawnSouls(25);
		}
		if (hpPercentage <= 50 && stage < 3) {
			stage = 3;
			spawnEnergy(15);
		}
		if (hpPercentage <= 25 && stage < 4) {
			stage = 4;
			spawnServant(30);
		}
	}

	// Skill ARTOS
	private void scheduleSkillArtos(final int delay){
		skillTaskArtos = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				doSkillArtos(delay);
			}
		}, (delay + Rnd.get(15) - 7) * 1000);
	}

	private void doSkillArtos(final int delay){
		if(getLifeStats().getHpPercentage() < 50){
			return;
		}
		SkillEngine.getInstance().getSkill(getOwner(), 18912 + Rnd.get(2), 55, getOwner()).useSkill();
		scheduleSkillArtos(delay  + difficulty);
	}

	// SPAWN SOULS
	private void spawnSouls(final int delay){
		if(getLifeStats().getHpPercentage() < 50){
			return;
		}
		Player target = getTargetPlayer();
		if(target != null){
			SkillEngine.getInstance().getSkill(getOwner(), 18959, 50, target).useSkill();
			List<Point3D> points = new ArrayList<Point3D>();
			points.addAll(soulLocations);
			int count = 8 - difficulty;
			for(int i = 0; i < count; i++) {
				if (!points.isEmpty()) {
					Point3D spawn = points.remove(Rnd.get(points.size()));
					spawn(281645, spawn.getX(), spawn.getY(), spawn.getZ(), (byte) 18);
				} 
			}
		}

		scheduleSpawnSouls(delay);
	}

	private void scheduleSpawnSouls(final int delay){
		spawnSoulsTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawnSouls(delay);
			}
		}, (delay + Rnd.get(-5, 5)) * 1000);
	}

	private Player getTargetPlayer() {
		List<Player> players = getClosePlayer(50);
		return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
	}
	
	// SPAWN ENERGY
	private void spawnEnergy(final int delay){
		if(getLifeStats().getHpPercentage() < 25){
			return;
		}
		rndSpawn(281660, 7 - difficulty);

		scheduleSpawnEnergy(delay);
	}

	private void scheduleSpawnEnergy(final int delay){
		spawnEnergyTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawnEnergy(delay);
			}
		}, (delay + Rnd.get(-5, 5)) * 1000);
	}

	// SPAWN SERVANT
	private void spawnServant(final int delay){
		rndSpawn(281659, 1);
		if(Rnd.get(difficulty) <= 0){
			AI2Actions.useSkill(this, 18993);
		}

		scheduleSpawnServant(delay);
	}

	private void scheduleSpawnServant(final int delay){
		spawnServantTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawnServant(delay);
			}
		}, (delay + Rnd.get(-5, 5)) * 1000);
	}
	
	// OTHER

	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}

	private SpawnTemplate rndSpawnInRange(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * 5);
		float y1 = (float) (Math.sin(Math.PI * direction) * 5);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
				+ y1, getPosition().getZ(), getPosition().getHeading());
	}

	private void cancelTask(){
		if (skillTaskArtos != null && !skillTaskArtos.isCancelled()) {
			skillTaskArtos.cancel(true);
		}
		if (spawnSoulsTask != null && !spawnSoulsTask.isCancelled()) {
			spawnSoulsTask.cancel(true);
		}
		if (spawnEnergyTask != null && !spawnEnergyTask.isCancelled()) {
			spawnEnergyTask.cancel(true);
		}
		if (spawnServantTask != null && !spawnServantTask.isCancelled()) {
			spawnServantTask.cancel(true);
		}

		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281645);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
		npcs = getPosition().getWorldMapInstance().getNpcs(281660);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
		npcs = getPosition().getWorldMapInstance().getNpcs(281659);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}

		startFigth = false;
		stage = 0;
		getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
	}
}
