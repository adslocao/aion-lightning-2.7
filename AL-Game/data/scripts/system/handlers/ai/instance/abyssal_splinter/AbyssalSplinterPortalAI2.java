package ai.instance.abyssal_splinter;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService;

import ai.ActionItemNpcAI2;

@AIName("teleportation_device")
public class AbyssalSplinterPortalAI2 extends ActionItemNpcAI2
{

	@Override
	protected void handleUseItemFinish(Player player)
	{
		Npc npc =getOwner();
		int instanceId = getPosition().getWorldMapInstance().getInstanceId();
		if(npc.getX() == 302.201f)
			TeleportService.teleportTo(player, 300220000, instanceId, 294.632f, 732.189f, 215.854f, 0, false);
		else if(npc.getX() == 334.001f)
			TeleportService.teleportTo(player, 300220000, instanceId, 338.475f, 701.417f, 215.916f, 0, false);
		else if(npc.getX() == 362.192f)
			TeleportService.teleportTo(player, 300220000, instanceId, 373.611f, 739.125f, 215.903f, 0, false);
	}
}
