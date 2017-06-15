/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.aionemu.gameserver.taskmanager.tasks;

import java.util.Map;

import javolution.util.FastMap;

import com.aionemu.gameserver.model.IExpirable;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.taskmanager.AbstractPeriodicTaskManager;

/**
 * @author Mr. Poke
 */
public class ExpireTimerTask extends AbstractPeriodicTaskManager {

	private FastMap<IExpirable, Player> expirables = new FastMap<IExpirable, Player>();

	/**
	 * @param period
	 */
	public ExpireTimerTask() {
		super(1000);
	}

	public static ExpireTimerTask getInstance() {
		return SingletonHolder._instance;
	}

	public void addTask(IExpirable expirable, Player player) {
		writeLock();
		try {
			expirables.put(expirable, player);
		}
		finally {
			writeUnlock();
		}
	}

	public void removeExpirable(IExpirable expirable) {
		writeLock();
		try {
			expirables.remove(expirable);
		}
		finally {
			writeUnlock();
		}
	}

	public void removePlayer(Player player) {
		writeLock();
		try {
			for (Map.Entry<IExpirable, Player> entry : expirables.entrySet()) {
				if (entry.getValue() == player)
					expirables.remove(entry.getKey());
			}
		}
		finally {
			writeUnlock();
		}
	}

	@Override
	public void run() {
		writeLock();
		try {
			int timeNow = (int) (System.currentTimeMillis() / 1000);
			for (Map.Entry<IExpirable, Player> entry : expirables.entrySet()) {
				IExpirable expirable = entry.getKey();
				Player player = entry.getValue();
				int min = (expirable.getExpireTime() - timeNow);
				if (min < 0) {
					expirable.expireEnd(player);
					expirables.remove(expirable);
					continue;
				}
				switch (min) {
					case 1800:
					case 900:
					case 600:
					case 300:
					case 60:
						expirable.expireMessage(player, min / 60);
						break;
				}
			}
		}
		finally {
			writeUnlock();
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ExpireTimerTask _instance = new ExpireTimerTask();
	}
}
