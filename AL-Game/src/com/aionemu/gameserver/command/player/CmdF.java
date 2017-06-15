package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.player.PlayerChatService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import org.apache.commons.lang.StringUtils;

/*"syntax .f <message> */

/**
 * @author Shepper
 */
public class CmdF extends BaseCommand {


	public void execute(Player player, String... params) {
		Storage sender = player.getInventory();

		if (!CustomConfig.FACTION_CMD_CHANNEL) {
			PacketSendUtility.sendMessage(player, "The command is disabled.");
			return;
		}

		if (params.length == 0) {
			showHelp(player);
			return;
		}

		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't talk in Prison.");
			return;
		}
		else if (player.isGagged()) {
			PacketSendUtility.sendMessage(player, "You are gaged, you can't talk.");
			return;
		}

		if (!CustomConfig.FACTION_FREE_USE) {
			if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE)
				sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
			else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
				return;
			}
		}

		String message = StringUtils.join(params, " ");

		if (!PlayerChatService.isFlooding(player)) {
			message = player.getName() + ": " + message;
			for (Player a : World.getInstance().getAllPlayers()) {
				if (a.getAccessLevel() >= 2)
					PacketSendUtility.sendMessage(a, (player.getRace() == Race.ASMODIANS ? "[A] " : "[E] ") + message);
				else if (a.getRace() == player.getRace())
					PacketSendUtility.sendMessage(a, message);
			}
		}

	}

}