/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.abyssal_splinter;

import java.util.List;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldMapInstance;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritsu, Luzien
 */
@AIName("yamennes")
public class YamenesAI2 extends AggressiveNpcAI2 {

	private boolean top;
	private Future<?> portalTask = null;
	private Future<?> bufflTask = null;
	private Future<?> skillTask = null;
	private AtomicBoolean isStart = new AtomicBoolean(false);

	@Override
	protected void handleSpawned() {
		top = true;
		super.handleSpawned();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isStart.compareAndSet(false, true)) {
			startTasks();
		}
	}

	private void startTasks() {

		if(getOwner().getNpcId() != 216952){
			bufflTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						EmoteManager.emoteStopAttacking(getOwner());
						SkillEngine.getInstance().getSkill(getOwner(), 19098, 55, getOwner()).useSkill();
					}
				}
			}, 600000);
		}

		portalTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
					spawnPortal();
					if(getOwner().getNpcId() == 216952){
						return;
					}
					Npc boss = getOwner();
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					deleteNpcs(instance.getNpcs(282107));
					spawn(282107, boss.getX() + 10, boss.getY() - 10, boss.getZ(), (byte) 0);
					spawn(282107, boss.getX() - 10, boss.getY() + 10, boss.getZ(), (byte) 0);
					spawn(282107, boss.getX() + 10, boss.getY() + 10, boss.getZ(), (byte) 0);				
			}
		}, 120000, 120000);
		
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
					if(getOwner().getNpcId() == 216952){
						return;
					}
					Npc boss = getOwner();
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(boss, 19282, 55, getTarget()).useSkill();
					boss.clearAttackedCount();
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1400729);
				
			}
		}, 70000, 70000);
	}

	private void spawnPortal() {
		Npc portalA = getPosition().getWorldMapInstance().getNpc(282014);
		Npc portalB = getPosition().getWorldMapInstance().getNpc(282015);
		Npc portalC = getPosition().getWorldMapInstance().getNpc(282131);

		NpcShoutsService.getInstance().sendMsg(getOwner(), 1400637);
		
		if (portalA == null) {
			if (!top) {
				spawn(282014, 288.10f, 741.95f, 216.81f, (byte) 3);
			}
			else {
				spawn(282014, 303.69f, 736.35f, 198.7f, (byte) 0);
			}
		}
		if (portalB == null) {
			if (!top) {
				spawn(282015, 375.05f, 750.67f, 216.82f, (byte) 59);
			}
			else {
				spawn(282015, 335.19f, 708.92f, 198.9f, (byte) 35);
			}
		}
		if (portalC == null) {
			if (!top) {
				spawn(282131, 341.33f, 699.38f, 216.86f, (byte) 59);
			}
			else {
				spawn(282131, 360.23f, 741.07f, 198.7f, (byte) 0);
			}
		}
		top = !top;
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null)
				npc.getController().onDelete();
		}
	}

	@Override
	protected void handleBackHome() {
		cancel();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned() {
		cancel();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		cancel();
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

	private void cancel(){
		top = true;
		isStart.set(false);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282107));
		deleteNpcs(instance.getNpcs(282014));
		deleteNpcs(instance.getNpcs(282015));
		deleteNpcs(instance.getNpcs(282131));
		deleteNpcs(instance.getNpcs(281904));
		deleteNpcs(instance.getNpcs(281903));
		getEffectController().removeEffect(19098);

		if (portalTask != null && !portalTask.isDone()) {
			portalTask.cancel(true);
		}
		if (skillTask != null && !skillTask.isDone()) {
			skillTask.cancel(true);
		}
		if (bufflTask != null && !bufflTask.isDone()) {
			bufflTask.cancel(true);
		}
	}
}