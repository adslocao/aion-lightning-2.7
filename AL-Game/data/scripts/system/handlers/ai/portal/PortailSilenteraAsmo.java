package ai.portal;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.teleport.TeleportService;

import ai.PortalAI2;

/**
 * @author joelc
 *
 */
@AIName("portalsilenteraasmo")
public class PortailSilenteraAsmo extends PortalAI2 {
	
	@Override
	protected void handleDialogStart(Player player) {
		if(player.getRace() != Race.ASMODIANS){
			return;
		}
		
		if(SiegeService.getInstance().getFortress(3011).getRace() == SiegeRace.ASMODIANS || SiegeService.getInstance().getFortress(3021).getRace() == SiegeRace.ASMODIANS){
			super.handleDialogStart(player);
		}else{
			TeleportService.teleportTo(player, 210050000, 1313, 1996, 381, 0);
		}
	}
}
