package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.configs.shedule.TvtSchedule;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.services.tvt.TvtService;
import com.aionemu.gameserver.services.tvt.TvtStartRunnable;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CmdTvt2 extends BaseCommand {

    /*syntax \\tvt2 <info> <start> <reg> <unreg>
      syntax \\tvt2 <start> <id> <time (18 00 hours minutes)  */
    private static final Logger log = LoggerFactory.getLogger(TvtService.class);

    
    public void execute(Player player, String... params) {
    	if (params.length == 0) {
    		showHelp(player);
    		return ;
    	}
        if (params[0].equals("info")) {
            for (TvtSchedule.TvtLevel l : TvtService.getInstance().getTvtSchedule().getTvtLevelList()) {
                TvtService.getInstance().getTvt(l.getId()).getHolders().info(player, true);
            }
        } else if (params[0].equals("start")) {
        	if (params.length < 4) {
        		showHelp(player);
        		return ;
        	}
            String time = params[3] + " " + params[2];
            int tvtId;
            try {
                tvtId = Integer.parseInt(params[1]);
            } catch (NumberFormatException ex) {
                PacketSendUtility.sendMessage(player, "You must give number to tvtId.");
                return;
            }
            TvtSchedule.TvtLevel l = TvtService.getInstance().getTvtSchedule().getTvtLevel(tvtId);
            String tvtTime = "0 " + time + " ? * *";
            CronService.getInstance().schedule(new TvtStartRunnable(l.getId(), l.getStartTime(), l.getDuration(), l.getLevel(), l.getMapId()), tvtTime);
            PacketSendUtility.sendMessage(player, "CronService add tvt2 event shedule at: " + params[2] + ":" + params[3]);
            log.info("[TVT] Scheduled tvt of id " + l.getId() + " based on cron expression: " + tvtTime);
        } else if (params[0].equals("reg")) {
            PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(4, 0, TvtService.getInstance().getTvtByLevel(player.getLevel()).getRemainingTime()));
            TvtService.getInstance().regPlayer(player);
        } else if (params[0].equals("unreg")) {
            TvtService.getInstance().unRegPlayer(player);
        }
    }

    
}
