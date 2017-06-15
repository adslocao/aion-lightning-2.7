package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.utils.PacketSendUtility;


/*syntax: //addemotion <emotion id [expire time] || html>\nhtml to show html with names*/

/**
 * @author ginho1, Damon
 * 
 */
public class CmdAddEmotion extends BaseCommand {


	public void execute(Player admin, String... params) {
		
		long expireMinutes = 0;
		int emotionId = 0;
		Player target = null;
			
		if (params.length < 1 || params.length > 2) {
			showHelp(admin);
			return;
		}
		
		try {
			emotionId = Integer.parseInt(params[0]);
			if (params.length == 2)
				expireMinutes = Long.parseLong(params[1]);
		}
		catch (NumberFormatException ex) {
			if(params[0].equalsIgnoreCase("html"))
				HTMLService.showHTML(admin, HTMLCache.getInstance().getHTML("emote.xhtml"));
				return;
		}

		if (emotionId < 1 || (emotionId > 35 && emotionId < 64) || emotionId > 129) {
			PacketSendUtility.sendMessage(admin, "Invalid <emotion id>, must be in intervals : [1-35]U[64-129]");
			return;
		}
		
		target = AutoTarget(admin, false);
		if (target == null) {
			PacketSendUtility.sendMessage(admin, "Bad Target, please select a Player");
			return;
		}
		
		if (target.getEmotions().contains(emotionId)) {
			PacketSendUtility.sendMessage(admin, "Target has aldready this emotion !");
			return;
		}
			
		if (params.length == 2)
			target.getEmotions().add(emotionId, (int)((System.currentTimeMillis()/1000)+expireMinutes*60), true);
		else
			target.getEmotions().add(emotionId, 0, true);
	}
}