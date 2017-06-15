package com.aionemu.gameserver.command.player;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.ItemRemodelService;

/**
 * @author Kashim
 */
public class CmdPreview extends BaseCommand {

    public void execute(Player player, String... params) {
        if (params.length != 1) {
            showHelp(player);
            return;
        }

        ItemRemodelService.commandPreviewRemodelItem(player, ParseInteger(params[0]), 15);
    }
}