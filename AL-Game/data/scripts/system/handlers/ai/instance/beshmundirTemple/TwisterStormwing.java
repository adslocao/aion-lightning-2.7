package ai.instance.beshmundirTemple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("twisterstormwing")
public class TwisterStormwing extends AggressiveNpcAI2 {
	private Future<?> skillTask;


	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		this.setStateIfNot(AIState.FOLLOWING);
		Npc AO = getPosition().getWorldMapInstance().getNpc(216264);
		if(AO.getLifeStats().isAlreadyDead() || AO.getTarget() == null){
			AI2Actions.deleteOwner(TwisterStormwing.this);
			return;
		}
		selectNextMove();
		doAttack();
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}

	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 281795 ? 18620 : 18619), 55, getTarget()).useSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawnInRange(getNpcId());
			}
		}, 8000);
		AI2Actions.deleteOwner(TwisterStormwing.this);
	}

	private void selectNextMove(){
		List<Player> players = getClosePlayer(200);
		Player player = (!players.isEmpty() ? players.get(Rnd.get(players.size())) : null);
		if(player == null){
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					rndSpawnInRange(getNpcId());
				}
			}, 2000);
			AI2Actions.deleteOwner(TwisterStormwing.this);
			return;
		}

		AI2Actions.targetCreature(this, player);
		getMoveController().moveToTargetObject();
	}

	private void doAttack(){
		List<Player> players = new ArrayList<Player>();
		for (Player player : getPosition().getWorldMapInstance().getPlayersInside()) {
			if (!player.getLifeStats().isAlreadyDead() && MathUtil.isIn3dRange(player, getOwner(), 2) && player != getTarget()) {
				players.add(player);
			}
		}
		if(players.size() != 0){
			for (Player p : players) {
				SkillEngine.getInstance().getSkill(getOwner(), (getNpcId() == 281795 ? 18620 : 18619), 55, p).useSkill();
			}
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					rndSpawnInRange(getNpcId());
				}
			}, 2000);
			AI2Actions.deleteOwner(TwisterStormwing.this);
			return;
		}
		scheduleAttack();
	}

	private void scheduleAttack(){
		skillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				doAttack();
			}
		}, 500);
	}

	private Npc rndSpawnInRange(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * Rnd.get(28));
		float y1 = (float) (Math.sin(Math.PI * direction) * Rnd.get(28));
		return (Npc) spawn(npcId, 548 + x1, 1360 + y1, 225, (byte) 0);
	}

	@Override
	public boolean canThink() {
		return false;
	}
	
	@Override
	public int modifyDamage(int damage)	{
		return 2;
	}
}
