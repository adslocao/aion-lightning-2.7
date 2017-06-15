package ai;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * TO DO REMOVE BAD IDEA
 * @author Luzien trigger onNpcGroupKillEvent on use
 */
@AIName("killonuse")
public class KillOnUseAI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		player.getActionItemNpc().setCondition(1, 0, getTalkDelay());
		super.handleUseItemStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {

		AI2Actions.fireNpcKillInstanceEvent(this, player);

		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				AI2Actions.deleteOwner(KillOnUseAI2.this);
			}

		}, 2000);
	}

}
