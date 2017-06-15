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
package instance;

import static com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TRANSFORM;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * @author xTz, Gigi
 */
@InstanceID(300230000)
public class KromedesTrialInstance extends GeneralInstanceHandler {

	private int transformId;
	private List<Integer> movies = new ArrayList<Integer>();
	private boolean isSpawned = false;
	private boolean inManor = false;

	@Override
	public void onEnterInstance(Player player) {
		transform(player);
		if (movies.contains(453)) {
			return;
		}
		transformId = player.getRace() == Race.ASMODIANS ? 202546 : 202545;
		sendMovie(player, 453);
		transform(player);
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.setTransformedModelId(0);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	private void transform(Player player) {
		player.setTransformedModelId(transformId);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, true));
	}

	@Override
	public void onPlayerLogOut(Player player) {
		player.setTransformedModelId(0);
		PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
		Storage storage = player.getInventory();
		switch (movieId) {
			case 454:
				Npc npc1 = player.getPosition().getWorldMapInstance().getNpc(730308);
				if (npc1 != null && MathUtil.isIn3dRange(player, npc1, 20)) {
					storage.decreaseByItemId(185000109, storage.getItemCountByItemId(185000109));
					TeleportService.teleportTo(player, mapId, 687.56116f, 681.68225f, 200.28648f, 30);
				}
				break;
		}
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		switch (zone.getAreaTemplate().getZoneName()) {
			case MANOR_ENTRANCE_300230000:
				sendMovie(player, 462);
				break;
			case KALIGA_TREASURY_300230000:
				if (!isSpawned) {
					isSpawned = true;
					Npc npc1 = instance.getNpc(217002);
					Npc npc2 = instance.getNpc(217000);
					Npc npc3 = instance.getNpc(216982);
					if (isDead(npc1) && isDead(npc2) && isDead(npc3)) {
						spawn(217005, 669.214f, 774.387f, 216.88f, (byte) 60);
						spawn(217001, 663.8805f, 779.1967f, 216.26213f, (byte) 60);
						spawn(217003, 663.0468f, 774.6116f, 216.26215f, (byte) 60);
						spawn(217004, 663.0468f, 770.03815f, 216.26212f, (byte) 60);
					}
					else {
						spawn(217006, 669.214f, 774.387f, 216.88f, (byte) 60);
					}
				}
				break;
			case KALIGA_DUNGEONS_300230000:
				inManor = true;
				break;
		}
	}

	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead()); 
	}

	private void sendMovie(Player player, int movie) {
		if (!movies.contains(movie)) {
			movies.add(movie);
			PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, movie));
		}
	}

	@Override
	public void onInstanceDestroy() {
		movies.clear();
	}
	
	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

	@Override
	public boolean onReviveEvent(Player player) {
		WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
		if (map == null) {
			PlayerReviveService.bindRevive(player);
			return true;
		}
		PlayerReviveService.revive(player, 25, 25, true);
		PacketSendUtility.sendPacket(player, STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (inManor)
			TeleportService.teleportTo(player, player.getWorldId(), 687, 681, 201, 0, true);
		else
			TeleportService.teleportTo(player, player.getWorldId(), 248, 244, 189, 0, true);
		transform(player);
		player.unsetResPosState();
		return true;
	}
	
}
