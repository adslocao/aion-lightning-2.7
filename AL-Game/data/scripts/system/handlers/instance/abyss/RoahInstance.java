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
package instance.abyss;

import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author bobobear
 */
@InstanceID(300070000)
public class RoahInstance extends GeneralInstanceHandler {

	private boolean isStartTimer = false;
	private long startTime;
	private boolean isInstanceDestroyed = false;
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		spawnRings();
	}
	
	private void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("ROAH_WING_1", mapId,
				new Point3D(501.77, 409.53, 94.12),
				new Point3D(503.93, 409.65, 98.9),
				new Point3D(506.26, 409.7, 94.15), 10), instanceId);
		f1.spawn();
	}
	
	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		if (flyingRing.equals("ROAH_WING_1")) {
			if (!isStartTimer) {
				isStartTimer = true;
				startTime = System.currentTimeMillis();
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900));
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						despawnNpcs(getNpcs(700472));
						despawnNpcs(getNpcs(700473));
						despawnNpcs(getNpcs(700474));
					}

				}, 900000);
			}
		}
		return false;
	}
	
	@Override
	public void onEnterInstance(Player player) {
		if (isStartTimer) {
			long time = System.currentTimeMillis() - startTime;
			if (time < 900000) {
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900 - (int) time / 1000));
			}
		}
	}
	
	private List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}

	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			npc.getController().onDelete();
		}
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		Storage bag = player.getInventory();
		bag.decreaseByItemId(185000036, bag.getItemCountByItemId(185000036));
		bag.decreaseByItemId(185000037, bag.getItemCountByItemId(185000037));
		bag.decreaseByItemId(185000038, bag.getItemCountByItemId(185000038));
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
	}
}
