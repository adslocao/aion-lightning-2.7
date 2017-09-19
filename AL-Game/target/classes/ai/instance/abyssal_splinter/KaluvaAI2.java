package ai.instance.abyssal_splinter;

import ai.SummonerAI2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.actions.NpcActions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Luzien
 */
@AIName("kaluva")
public class KaluvaAI2 extends SummonerAI2 {
	private boolean canThink = true;
	private boolean isStart = false;
	private Future<?> task;
	private Future<?> transfo;
	private static final int TASK_TIME = 70; // 70 sec
	private static final int TRANSFO_TIME = 50; // 50 sec

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if(!isStart){
			isStart = true;
			scheduleTask();
			scheduleTransfo();
		}
	}

	@Override
	protected void handleMoveArrived() {
		if (canThink) {
			super.handleMoveArrived();
			return;
		}

		SkillEngine.getInstance().getSkill(getOwner(), 19152, 55, getOwner()).useSkill();
		Npc egg = getPosition().getWorldMapInstance().getNpc(281902);
		if (egg != null) {
			SkillEngine.getInstance().getSkill(getOwner(), 19223, 55, egg).useSkill();
		}

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run(){
				canThink = true;
				Creature creature = getAggroList().getMostHated();
				if (creature != null && getOwner().canSee(creature) && !NpcActions.isAlreadyDead(creature)) {
					getOwner().setTarget(creature);
					getOwner().getGameStats().renewLastAttackTime();
					getOwner().getGameStats().renewLastAttackedTime();
					getOwner().getGameStats().renewLastChangeTargetTime();
					getOwner().getGameStats().renewLastSkillTime();
				}
				setStateIfNot(AIState.FIGHT);
				think();
			}
		}, 2000);
		super.handleMoveArrived();
	}

	@Override
	protected void handleBackHome() {
		cancelTask();
		super.handleBackHome();
	}

	@Override
	protected void handleDied() {
		cancelTask();
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}

	@Override
	protected void handleDespawned() {
		cancelTask();
		super.handleDespawned();
	}

	private void scheduleTask() {
		int time = Rnd.get(0, 20) - 10 + TASK_TIME;
		task = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run(){
				doTask();
			}
		}, time * 1000);
	}

	private void scheduleTransfo() {
		int time = Rnd.get(0, 20) - 10 + TRANSFO_TIME;
		transfo = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run(){
				doTransfo();
			}
		}, time * 1000);
	}
	
	private void doTransfo(){
		Player p = getTargetPlayer();
		if(p == null){
			return;
		}

		SkillEngine.getInstance().getSkill(getOwner(), 19158, 55, p).useSkill();
		Npc npc = (Npc) spawn(281910, p.getX(), p.getY(), p.getZ(), (byte) 0);
		((KaluvaWeb) npc.getAi2()).setP(p);
	}

	private void doTask(){
		spawn();
		canThink = false;
		EmoteManager.emoteStopAttacking(getOwner());
		setStateIfNot(AIState.FOLLOWING);
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
		AI2Actions.targetCreature(this, getPosition().getWorldMapInstance().getNpc(281902));
		getMoveController().moveToTargetObject();

		scheduleTask();
	}
	
	private Player getTargetPlayer() {
		List<Player> players = getClosePlayer(50);
		return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
	}

	private void spawn() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(281902, 663.322021f, 556.731995f, 424.295013f, (byte) 64);
				break;
			case 2:
				spawn(281902, 644.0224f, 523.9641f, 423.09103f, (byte) 32);
				break;
			case 3:
				spawn(281902, 611.008f, 539.73395f, 423.25034f, (byte) 119);
				break;
			case 4:
				spawn(281902, 628.4426f, 585.4443f, 424.31854f, (byte) 93);
				break;
		}
	}

	private void cancelTask() {
		canThink = true;
		isStart = false;
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
		if (transfo != null && !transfo.isCancelled()) {
			transfo.cancel(true);
		}
		
		getEffectController().removeEffect(19152);
		
		List<Npc> npcs = new ArrayList<Npc>();
		npcs.addAll(getPosition().getWorldMapInstance().getNpcs(281902));
		npcs.addAll(getPosition().getWorldMapInstance().getNpcs(281911));
		npcs.addAll(getPosition().getWorldMapInstance().getNpcs(281912));
		npcs.addAll(getPosition().getWorldMapInstance().getNpcs(282057));

		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	@Override
	public boolean canThink() {
		return canThink;
	}

}
