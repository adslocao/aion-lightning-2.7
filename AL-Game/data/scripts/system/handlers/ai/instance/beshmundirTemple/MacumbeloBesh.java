package ai.instance.beshmundirTemple;

import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MESSAGE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("macumbelobesh")
public class MacumbeloBesh extends AggressiveNpcAI2 {
	private boolean figthStart = false;
	private boolean addSpowned = false;

	private Future<?> skillTaskSpawn;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		startFight();
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
	}

	@Override
	public void handleDied() {
		super.handleDied();
		cancelTask();
	}

	private void cancelTask() {
		figthStart = false;
		addSpowned = false;
		if (skillTaskSpawn != null && !skillTaskSpawn.isDone()) {
			skillTaskSpawn.cancel(true);
		}

		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281698);
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void startFight() {
		if (figthStart) {
			return;
		}
		figthStart = true;
		SkillEngine.getInstance().getSkill(getOwner(), 19049, 50, getTarget()).useSkill();
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 95 && !addSpowned) {
			addSpowned = true;
			spawnAdd(40);
		}
	}

	private void checkOneShoot() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281698);
				int addRemaining = 0;
				for (Npc npc : npcs) {
					if (npc != null) {
						if (!npc.getLifeStats().isAlreadyDead()) {
							addRemaining++;
						}
						npc.getController().onDelete();
					}
				}
				if (addRemaining <= 0) {
					PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_MESSAGE(getOwner().getObjectId(),
							getOwner().getName(), "I am fealing weak again", ChatType.NORMAL));
					return;
				}

				List<Player> players = getClosePlayer(50);
				Player player = (!players.isEmpty() ? players.get(Rnd.get(players.size())) : null);
				if (player == null) {
					return;
				}
				PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_MESSAGE(getOwner().getObjectId(),
						getOwner().getName(), "My power is over 9000", ChatType.NORMAL));
				SkillEngine.getInstance().getSkill(getOwner(), 19049, 55, getOwner()).useSkill();
				SkillEngine.getInstance().getSkill(getOwner(), 19051, 55, player).useSkill();
			}

		}, 20000);
	}

	private void spawnAdd(int delay) {
		PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_MESSAGE(getOwner().getObjectId(),
				getOwner().getName(), "Give me your power my slave", ChatType.NORMAL));
		spawn(281698, 1003.531f, 111.217f, 243.026f, (byte) 0);
		spawn(281698, 955.531f, 111.217f, 243.026f, (byte) 0);
		spawn(281698, 955.531f, 156.217f, 242.026f, (byte) 0);
		spawn(281698, 1003.531f, 156.217f, 242.026f, (byte) 0);
		checkOneShoot();
		scheduleSpawn(delay);
	}

	private void scheduleSpawn(final int delay) {
		skillTaskSpawn = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawnAdd(delay);
			}

		}, (delay + Rnd.get(20) - 10) * 1000);
	}
}
