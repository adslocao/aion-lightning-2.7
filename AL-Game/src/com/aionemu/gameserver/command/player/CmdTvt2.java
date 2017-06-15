package com.aionemu.gameserver.command.player;


import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.shedule.TvtSchedule;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.tvt.TvtService;


public class CmdTvt2 extends BaseCommand {

  
    public void execute(Player player, String... params) {
        for (TvtSchedule.TvtLevel l : TvtService.getInstance().getTvtSchedule().getTvtLevelList()) {
            TvtService.getInstance().getTvt(l.getId()).getHolders().info(player, false);
        }
    }
}

 