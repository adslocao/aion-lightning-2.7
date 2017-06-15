package ai.instance.beshmundirTemple;

import java.util.List;
import java.util.concurrent.Future;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.SkillEngine;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("calamity")
public class Calamity extends AggressiveNpcAI2 {

	private Future<?> skillTask;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackPlayer();
			}
		}, 12000);
	}

	@Override
	public void handleDied() {
		super.handleDied();
		cancelTask();
		getOwner().getController().onDespawn();
	}

	@Override
	public void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}

	private void attackPlayer() {
		List<Player> players = getClosePlayer(50);
		Player player = (!players.isEmpty() ? players.get(Rnd.get(players.size())) : null);
		if(player == null){
			return;
		}

		SkillEngine.getInstance().getSkill(getOwner(), 18968, 50, player).useSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				AI2Actions.deleteOwner(Calamity.this);
			}
		}, 6000);
	}

	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()){
			skillTask.cancel(true);
		}
	}
}
