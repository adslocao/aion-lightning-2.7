package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.toypet.PetAdoptionService;

public class CmdPet extends BaseCommand {
	
	/*syntax //pet <add [petid name]>*/
	
	
	public void execute(Player player, String... params) {
		if (params.length < 2) {
			showHelp(player);
			return;
		}
		
		if (params[0].equalsIgnoreCase("add")) {
			if (params.length != 3)
				showHelp(player);
			else {
				int petId = Integer.parseInt(params[1]);
				String name = params[2];
				PetAdoptionService.addPet(player, petId, name, 0);
			}
		}
		else if (params[0].equalsIgnoreCase("del")) {
			int petId = Integer.parseInt(params[1]);
			PetAdoptionService.surrenderPet(player, petId);
		}
	}

}
