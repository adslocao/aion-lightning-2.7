package ai.instance.dredgion;

import java.util.List;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.ExitPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService;

import ai.ActionItemNpcAI2;

@AIName("dredgion_teleportation_device")
public class DredgionTeleporter extends ActionItemNpcAI2 {
	
	@SuppressWarnings("static-access")
	@Override
	protected void handleUseItemFinish(Player player)
	{
		int npcId = getNpcId();
		int instanceId = getPosition().getWorldMapInstance().getInstanceId();

		PortalTemplate portalTemplate = DataManager.PORTAL_DATA.getPortalTemplate(npcId);
		
		if(portalTemplate == null){
			return;
		}
		List<ExitPoint> exits = portalTemplate.getExitPoints();
		if(exits == null || exits.size() != 1){
			return;
		}
		ExitPoint exit = exits.get(0);
		
		if(exit.getRace() != getRace().PC_ALL && exit.getRace() != player.getRace()){
			return;
		}
		
		TeleportService.teleportTo(player, exit.getMapId(), instanceId, exit.getX(), exit.getY(), exit.getZ(), 0, false);
	}
}
