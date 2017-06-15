package ai.instance.pvpArenas;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;

import ai.ActionItemNpcAI2;

@AIName("plazzathrower")
public class PlazzaThrower extends ActionItemNpcAI2 {
	private boolean isRewarded;

	@Override
	protected void handleDialogStart(Player player) {
		InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
		if (instance != null && !instance.isStartProgress()) {
			return;
		}
		super.handleDialogStart(player);
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
		if (instance != null && !instance.isStartProgress()) {
			return;
		}
		if (isRewarded) {
			return;
		}
		
		List<Player> playerInside = new ArrayList<Player>();
		InstancePlayerReward playerReward = null;
		for (InstancePlayerReward instanceReward : instance.getPlayersInside()) {
			Player p = instanceReward.getPlayer();
			if(p.getObjectId() == player.getObjectId()){
				playerReward = instanceReward;
				continue;
			}
			if(MathUtil.isInSphere(p, 1842, 1732, 300, 20)){
				playerInside.add(p);
			}
		}
		//use skill from player
		if(playerInside.size() > 0){
			SkillEngine.getInstance().getSkill(player, 20055, 50, playerInside.get(Rnd.get(0, playerInside.size()-1))).useSkill();
		}
		
		if(playerReward != null){
			playerReward.addPoints(500);
			InstanceHandler instanceArena = getPosition().getWorldMapInstance().getInstanceHandler();
			instanceArena.sendSystemMsg(player, getOwner(), 500);
			instanceArena.sendPacket();
		}
		
		isRewarded = true;
		AI2Actions.handleUseItemFinishNpc(this, player);
		AI2Actions.deleteOwner(this);
	}
}
