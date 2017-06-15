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
package com.aionemu.gameserver.services.instance;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.DredgionConfig;
import com.aionemu.gameserver.model.autogroup.AutoGroupsType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.AutoGroupService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import java.util.Iterator;
import javolution.util.FastList;

/**
 *
 * @author xTz
 */
public class DredgionService2 {

	private static final Logger log = LoggerFactory.getLogger(DredgionService2.class);
	private boolean registerAvailable;
	private FastList<Integer> playersWithCooldown = new FastList<Integer>();

	public DredgionService2() {
	}

	public void start() {
		String times = DredgionConfig.DREDGION_TIMES;
		CronService.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				startDredgionRegistration();
			}

		}, DredgionConfig.DREDGION_TIMES);
		log.info("Scheduled Dredgion: based on cron expression: " + times + " Duration: " + DredgionConfig.DREDGION_TIMER + " in minutes");
	}

	private void startUregisterDredgionTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				registerAvailable = false;
				playersWithCooldown.clear();
				AutoGroupService2.getInstance().unregisterInstance(AutoGroupsType.BARANATH_DREDGION);
				AutoGroupService2.getInstance().unregisterInstance(AutoGroupsType.CHANTRA_DREDGION);
				Iterator<Player> iter = World.getInstance().getPlayersIterator();
				Player player = null;
				while (iter.hasNext()) {
					player = iter.next();
					if (player.getLevel() > 45) {
						byte instanceMaskId = getInstanceMaskId(player);
						if (instanceMaskId == 0) {
							continue;
						}
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
					}
				}
			}
		}, DredgionConfig.DREDGION_TIMER * 60 * 1000);
	}

	private void startDredgionRegistration() {
		registerAvailable = true;
		startUregisterDredgionTask();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		Player player = null;
		while (iter.hasNext()) {
			player = iter.next();
			if (player.getCommonData().getLevel() > 45) {
				byte instanceMaskId = getInstanceMaskId(player);
				if (instanceMaskId == 0) {
					continue;
				}
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
				if (player.getCommonData().getLevel() > 50)
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDDREADGION_02);
				else
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDAB1_DREADGION);
			}
		}
	}

	public boolean isDredgionAvialable() {
		return registerAvailable;
	}

	public byte getInstanceMaskId(Player player) {
		int level = player.getLevel();
		if (level > 45 && level < 51) {
			return 1 ;
		}
		else if (level > 50 && level< 56) {
			return 2;
		}
		return 0;
	}

	public void addCoolDown(Player player) {
		playersWithCooldown.add(player.getObjectId());
	}

	public boolean hasCoolDown(Player player) {
		return playersWithCooldown.contains(player.getObjectId());
	}

	public void showWindow(Player player, byte instanceMaskId) {
		if (getInstanceMaskId(player) != instanceMaskId) {
			return;
		}

		if (!playersWithCooldown.contains(player.getObjectId())) {
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId));
		}
	}

	private static class SingletonHolder {
		protected static final DredgionService2 instance = new DredgionService2();
	}

	public static DredgionService2 getInstance() {
		return SingletonHolder.instance;
	}
}
