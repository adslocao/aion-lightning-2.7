package ai.instance.abyssal_splinter;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import ai.GeneralNpcAI2;

@AIName("kaluvaweb")
public class KaluvaWeb extends GeneralNpcAI2 {
	Player p = null;

	@Override
	protected void handleDied() {
		if(p != null){
			p.getEffectController().removeEffect(19158);
		}
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}
	
	
	public void setP(Player p) {
		this.p = p;
	}
}
