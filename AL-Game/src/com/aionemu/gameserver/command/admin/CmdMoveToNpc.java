package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;


public class CmdMoveToNpc extends BaseCommand {
	
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			showHelp(admin);
			return;
		}
		
		int npcId = 0;
		String message = "";
		try {
			npcId = Integer.valueOf(params[0]);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			showHelp(admin);
		}
		catch (NumberFormatException e) {
			String npcName = "";

			for (int i = 0; i < params.length; i++)
				npcName += params[i] + " ";
			npcName = npcName.substring(0, npcName.length() - 1);

			for (NpcTemplate template : DataManager.NPC_DATA.getNpcData().valueCollection()) {
				if (template.getName().equalsIgnoreCase(npcName)) {
					if (npcId == 0)
						npcId = template.getTemplateId();
					else {
						if (message.equals(""))
							message += "Found others (" + npcName + "): \n";
						message += "Id: " + template.getTemplateId() + "\n";
					}
				}
			}
			if (npcId == 0) {
				PacketSendUtility.sendMessage(admin, "NPC " + npcName + " cannot be found");
			}
		}

		if(npcId > 0) {
			message = "Teleporting to Npc: "+npcId+"\n"+message;
			PacketSendUtility.sendMessage(admin, message);
			TeleportService.teleportToNpc(admin, npcId);
		}
	}
}