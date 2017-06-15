package ai.instance.abyssal_splinter;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.utils.MathUtil;

import ai.HomingNpcAI2;

@AIName("rukrilebonsoulfragment")
public class RukrilEbonsoulFragment extends HomingNpcAI2{

	
	@Override
	protected void handleDied() {
		super.handleDied();
		
		Npc npc = getPosition().getWorldMapInstance().getNpc(getOwner().getNpcId() == 281907 ? 216949 : 216948);
		if (npc != null && !npc.getLifeStats().isAlreadyDead() && MathUtil.isIn3dRange(getOwner(), npc, 10)) {
			npc.getEffectController().removeEffect(npc.getNpcId() == 216948 ? 19266 : 19159);
		}
		AI2Actions.deleteOwner(this);
	}
}
