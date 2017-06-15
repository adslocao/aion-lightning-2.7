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
@AIName("portalsilenteraely")
public class PortailSilenteraEly extends PortalAI2 {
	@Override
	protected void handleDialogStart(Player player) {
		if(player.getRace() != Race.ELYOS){
			return;
		}
		
		if(SiegeService.getInstance().getFortress(2011).getRace() == SiegeRace.ELYOS || SiegeService.getInstance().getFortress(2021).getRace() == SiegeRace.ELYOS){
			super.handleDialogStart(player);
		}else{
			TeleportService.teleportTo(player, 220070000, 1603, 1499, 415, 0);
		}
	}
}