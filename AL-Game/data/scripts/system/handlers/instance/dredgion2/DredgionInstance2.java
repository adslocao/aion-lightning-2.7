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
package instance.dredgion2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang.mutable.MutableInt;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.controllers.SummonController.UnsummonType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.DredgionReward;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.DredgionPlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.templates.portal.EntryPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INSTANCE_SCORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author xTz
 */
public class DredgionInstance2 extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;
	protected DredgionReward dredgionReward;
	private float loosingGroupMultiplier = 1;
	private boolean isInstanceDestroyed = false;
	protected boolean isInstanceStarted = false;
	private long instanceTime;
	private Future<?> instanceTask;

	protected DredgionPlayerReward getPlayerReward(Player player) {
		if (dredgionReward.getPlayerReward(player) == null) {
			addPlayerToReward(player);
		}
		return (DredgionPlayerReward) dredgionReward.getPlayerReward(player);
	}

	protected void captureRoom(Race race, int roomId) {
		dredgionReward.getDredgionRoomById(roomId).captureRoom(race);
	}

	private void addPlayerToReward(Player player) {
		dredgionReward.addPlayerReward(new DredgionPlayerReward(player));
	}

	private boolean containPlayer(Player player) {
		return dredgionReward.containPlayer(player);
	}

	@Override
	public void onEnterInstance(final Player player) {
		if (!isInstanceStarted) {
			isInstanceStarted = true;
			instanceTime = System.currentTimeMillis();
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					openFirstDoors();
					dredgionReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
					sendPacket();
				}

			}, 120000);
			instanceTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					stopInstance(dredgionReward.getWinningRaceByScore());
				}

			}, 2520000);
		}
		if (!containPlayer(player)) {
			addPlayerToReward(player);
		}
		else {
			getPlayerReward(player).setPlayer(player);
		}
		sendPacket();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		dredgionReward = new DredgionReward(mapId, instanceId);
		dredgionReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
	}

	protected void stopInstance(Race race) {
		stopInstanceTask();
		dredgionReward.setWinningRace(race);
		dredgionReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward();
		sendPacket();
	}

	public void doReward() {
		for (InstancePlayerReward playerReward : dredgionReward.getPlayersInsideByRace(Race.PC_ALL)) {
			float abyssPoint = playerReward.getPoints() * 1.6f; // to do finde on what depend this modifier
			Player player = playerReward.getPlayer();
			if (player.getRace().equals(dredgionReward.getWinningRace())) {
				abyssPoint = dredgionReward.getWinnerPoints();
				ItemService.addItem(player, 166000130, 3);
			}
			else {
				abyssPoint = dredgionReward.getLooserPoints();
				ItemService.addItem(player, 166000120, 3);
			}
			AbyssPointsService.addAp(player, (int) abyssPoint);
			QuestEnv env = new QuestEnv(null, player, 0, 0);
			QuestEngine.getInstance().onDredgionReward(env);
		}
		for (Npc npc : instance.getNpcs()) {
			npc.getController().onDelete();
		}
	}

	@Override
	public boolean onReviveEvent(Player player) {
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PlayerReviveService.revive(player, 100, 100, false);
		player.getGameStats().updateStatsAndSpeedVisually();
		dredgionReward.portToPosition(player);
		return true;
	}

	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		Summon summon = player.getSummon();
		if (summon != null) {
			summon.getController().release(UnsummonType.UNSPECIFIED);
		}

		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0
			: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		if (lastAttacker != null) {
			if (lastAttacker instanceof Player) {
				if (lastAttacker.getRace() != player.getRace()) {
					int points = 60;
					InstancePlayerReward playerReward = getPlayerReward(player);

					if (playerReward.getPoints() == 0) {
						points = 0;
					}
					if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
						points *= loosingGroupMultiplier;
					}
					updateScore((Player) lastAttacker, player, points, true);
				}
			}
			else {
				updateScore(player, null, -60, false);
			}
		}
		return true;
	}

	private MutableInt getPointsByRace(Race race) {
		return dredgionReward.getPointsByRace(race);
	}

	private void addPointsByRace(Race race, int points) {
		dredgionReward.addPointsByRace(race, points);
	}

	private void addPointToPlayer(Player player, int points) {
		getPlayerReward(player).addPoints(points);
	}

	private void addPvPKillToPlayer(Player player) {
		getPlayerReward(player).addPvPKillToPlayer();
	}

	private void addBalaurKillToPlayer(Player player) {
		getPlayerReward(player).addMonsterKillToPlayer();
	}

	protected void updateScore(Player player, Creature target, int points, boolean pvpKill) {
		// group score
		addPointsByRace(player.getRace(), points);

		// player score
		List<Player> playersToGainScore = new ArrayList<Player>();

		if (!pvpKill && target != null && player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (member.getLifeStats().isAlreadyDead()) {
					continue;
				}
				if (MathUtil.isIn3dRange(member, target, GroupConfig.GROUP_MAX_DISTANCE)) {
					playersToGainScore.add(member);
				}
			}
		}
		else {
			playersToGainScore.add(player);
		}

		for (Player playerToGainScore : playersToGainScore) {
			addPointToPlayer(playerToGainScore, Math.round(points / playersToGainScore.size()));
			if (target instanceof Npc) {
				PacketSendUtility.sendPacket(playerToGainScore, new SM_SYSTEM_MESSAGE(1400237, new DescriptionId(((Npc) target).getObjectTemplate().getNameId() * 2 + 1), points));
			}
			else if (target instanceof Player) {
				PacketSendUtility.sendPacket(playerToGainScore, new SM_SYSTEM_MESSAGE(1400237, target.getName(), points));
			}
		}

		// recalculate point multiplier
		int pointDifference = getPointsByRace(Race.ASMODIANS).intValue() - (getPointsByRace(Race.ELYOS)).intValue();
		if (pointDifference < 0) {
			pointDifference *= -1;
		}
		if (pointDifference >= 3000) {
			loosingGroupMultiplier = 10;
		}
		else if (pointDifference >= 1000) {
			loosingGroupMultiplier = 1.5f;
		}
		else {
			loosingGroupMultiplier = 1;
		}

		// pvpKills for pvp and balaurKills for pve
		if (pvpKill && points > 0) {
			addPvPKillToPlayer(player);
		}
		else if (target instanceof Npc && ((Npc) target).getRace().equals(Race.DRAKAN)){
			addBalaurKillToPlayer(player);
		}
		sendPacket();
	}

	@Override
	public void onDie(Npc npc) {
		int hpGauge = npc.getObjectTemplate().getHpGauge();
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
		if (hpGauge <= 5) {
			updateScore(mostPlayerDamage, npc, 12, false);
		}
		else if (hpGauge <= 9) {
			updateScore(mostPlayerDamage, npc, 32, false);
		}
		else {
			updateScore(mostPlayerDamage, npc, 42, false);
		}
	}

	@Override
	public void onInstanceDestroy() {
		stopInstanceTask();
		isInstanceDestroyed = true;
		dredgionReward.clear();
		doors.clear();
	}

	protected void openFirstDoors() {
	}

	protected void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null) {
			door.setOpen(true);
		}
	}

	@Override
	public void sendPacket() {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_INSTANCE_SCORE(getTime(), dredgionReward));
			}
		});
	}

	private int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (result < 120000) {
			return (int) (120000 - result);
		}
		else if (result < 2520000) {
			return (int) (2400000 - (result - 120000));
		}
		return 0;
	}

	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
		sp(npcId, x, y, z, h, 0, time);
	}

	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int staticId, final int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					spawn(npcId, x, y, z, h, staticId);
				}
			}

		}, time);
	}

	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {

					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(msg));
						}
					}

				});
			}

		}, time);

	}

	private void stopInstanceTask() {
		if (instanceTask != null) {
			instanceTask.cancel(true);
		}
	}

	@Override
	public InstanceReward<?> getInstanceReward() {
		return dredgionReward;
	}

	@Override
	public void onExitInstance(Player player) {
		PortalTemplate portal = DataManager.PORTAL_DATA.getInstancePortalTemplate(mapId, player.getRace());
		EntryPoint entryPoint = TeleportService.getEntryPointByRace(portal, player.getRace());
		TeleportService.teleportTo(player, entryPoint.getMapId(), 1, entryPoint.getX(), entryPoint.getY(),
				entryPoint.getZ(), 3000, true);
	}

}