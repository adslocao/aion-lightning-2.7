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

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 */

@AIName("yamenessportal")
public class YamenessPortalSummonedAI2 extends AggressiveNpcAI2 {

	private Future<?> task1 = null;
	private Future<?> task2 = null;

	@Override
	protected void handleSpawned() {
		task1 = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if(isAlreadyDead()){
					AI2Actions.deleteOwner(YamenessPortalSummonedAI2.this);
					return;
				}
				spawnSummons();
			}
		}, 12000);
	}

	private void spawnSummons() {
		spawn(281903, getOwner().getX() + 3, getOwner().getY() - 3, getOwner().getZ(), (byte) 0);
		spawn(281904, getOwner().getX() - 3, getOwner().getY() + 3, getOwner().getZ(), (byte) 0);
		task2 = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if(!isAlreadyDead()){
					spawn(281903, getOwner().getX() + 3, getOwner().getY() - 3, getOwner().getZ(), (byte) 0);
					spawn(281904, getOwner().getX() - 3, getOwner().getY() + 3, getOwner().getZ(), (byte) 0);
				}
				AI2Actions.deleteOwner(YamenessPortalSummonedAI2.this);
			}
		}, 60000);
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
		super.handleDied();
		cancel();
		AI2Actions.deleteOwner(this);
	}

	private void cancel() {
		if (task1 != null && !task1.isDone()) {
			task1.cancel(true);
		}
		if (task2 != null && !task2.isDone()) {
			task2.cancel(true);
		}
	}
}
