package ai.instance.abyssal_splinter;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.geometry.Point3D;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Luzien
 */
@AIName("pazuzu")
public class PazuzuAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> task;
	private List<Point3D> wormLocations = new ArrayList<Point3D>();
	private List<Byte> wormLocationsByte = new ArrayList<Byte>();
	private static final int ADD_SPAWN_TIME = 50; // 50 sec

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), 342219, getObjectId(), 0, 0);
			schedulTask();
		}
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		wormLocations.add(new Point3D(665.351990f, 351.425995f, 466.073987f));
		wormLocations.add(new Point3D(666.604980f, 314.497009f, 465.394012f));
		wormLocations.add(new Point3D(685.588989f, 342.955994f, 465.908997f));
		wormLocations.add(new Point3D(651.322021f, 346.554993f, 465.563995f));
		wormLocations.add(new Point3D(650.7373f, 325.2235f, 465.47953f));
		wormLocations.add(new Point3D(686.7373f, 323.2235f, 465.47953f));
		wormLocationsByte.add((byte) 8);
		wormLocationsByte.add((byte) 27);
		wormLocationsByte.add((byte) 68);
		wormLocationsByte.add((byte) 111);
		wormLocationsByte.add((byte) 30);
		wormLocationsByte.add((byte) 42);
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500003, getObjectId(), 0, 0);
		AI2Actions.deleteOwner(this);
	}

	@Override
	protected void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}
	
	private void schedulTask(){
		int time = Rnd.get(0,10) - 5 + ADD_SPAWN_TIME;
		task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				startTask();
			}
		}, time * 1000);
	}

	private void startTask() {
		SkillEngine.getInstance().getSkill(getOwner(), 19145, 55, getOwner()).useSkill();
		SkillEngine.getInstance().getSkill(getOwner(), 19291, 55, getOwner()).useSkill();
		
		schedulTask();

		// Get all posiible spawn
		List<Point3D> points = new ArrayList<Point3D>();
		List<Byte> pointsByte = new ArrayList<Byte>();
		points.addAll(wormLocations);
		pointsByte.addAll(wormLocationsByte);
		
		// Get number of worm
		int count = 2;
		if(getLifeStats().getHpPercentage() < 70){
			count = 3;
		}
		if(getLifeStats().getHpPercentage() < 40){
			count = 4;
		}
		for(int i = 0; i < count; i++) {
			if (points.isEmpty()) {
				return;
			}
			int pos = Rnd.get(points.size());
			Point3D spawn = points.remove(pos);
			byte b = pointsByte.remove(pos);
			spawn(281909, spawn.getX(), spawn.getY(), spawn.getZ(), b);
		}
	}

	private void cancelTask() {
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}

		getOwner().getEffectController().removeAllEffects();
		for (Npc npc : getPosition().getWorldMapInstance().getNpcs(281909)) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}
	
}
