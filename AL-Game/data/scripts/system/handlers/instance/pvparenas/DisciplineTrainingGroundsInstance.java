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
package instance.pvparenas;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author xTz
 */
@InstanceID(300430000)
public class DisciplineTrainingGroundsInstance extends PvPArenaInstance {

	protected int togTime = 35;
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 200;
		deathFine = -100;
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onDie(Npc npc) {
		super.onDie(npc);
		int timeRnd = Rnd.get(0, 10) - 5;
		
		// Tog and brax respawn
		if(npc.getNpcId() == 218706 || npc.getNpcId() == 218705){
			spawnTog(togTime + timeRnd);
		}
		npc.getController().delete();
	}
	
	protected void spawnOnStart(){
		spawnTog(0);
		spawnRelicsLava(0);
		spawnRelicsColisea(0);
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		super.handleUseItemFinish(player, npc);

		int timeRnd = Rnd.get(0, 10) - 5;
		if (npc.getNpcId() == 701221) { // In lava zone
			spawnRelicsLava(relicTime + timeRnd);
			return;
		}
		if (npc.getNpcId() == 701220) { // In colisea zone
			spawnRelicsColisea(relicTime + timeRnd);
			return;
		}
	}
	
	private void spawnRelicsColisea(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				spawn(701220, 1848.173f, 1737.195f, 305.182f, (byte) 0);
			}

		}, time * 1000);
	}

	private void spawnRelicsLava(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				if(Rnd.get(1, 2) == 1){
					spawn(701221, 707.409f, 1779.437f, 165.419f, (byte) 0); // Position 1
				}else{
					spawn(701221, 692.663f, 1770.740f, 219.014f, (byte) 0); // Position 2
				}
			}

		}, time * 1000);
	}
	
	private void spawnTog(int time){
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				if(Rnd.get(1, 2) == 1){
					spawn(218706, 1856.197f, 1071.305f, 337.287f, (byte) 90); // Tog
				}else{
					spawn(218705, 1856.197f, 1071.305f, 337.287f, (byte) 90); // Brax
				}
			}

		}, time * 1000);
	}

}