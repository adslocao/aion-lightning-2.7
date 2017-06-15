package ai.event;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.services.tvt.TvtRegistrator;
import com.aionemu.gameserver.services.tvt.TvtService;
import com.aionemu.gameserver.utils.PacketSendUtility;



@AIName("tvtregistrator")
public class TvtEventAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(final Player player) {
                String message = "[Ascension]Do You want register in Tvt?";
	        RequestResponseHandler responseHandler = new RequestResponseHandler(player){

	            public void acceptRequest(Creature requester, Player responder)
	            {
	                TvtRegistrator tvt = TvtService.getInstance().getTvtByLevel(player.getLevel());
                      if (!tvt.getHolders().getPlayer(player)) {
                       if (!TvtService.getInstance().regPlayer(player)) {
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
                        return;
                    }
                    PacketSendUtility.sendMessage(player, "[Ascension]Congratulations!You Registered, for More Info Click to NPC - Aernia");
                    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(4, 0, tvt.getRemainingTime()));
                   // inRestRoom(player); Maybe Need :)
                    tvt.getHolders().info(player, player.isGM());
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
                    } 
                     else 
                    {
                    TvtService.getInstance().unRegPlayer(player);
                    PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(4, 0, 0));
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
                    }
	            }

	            public void denyRequest(Creature requester, Player responder){ return; }
	        };
	    boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
	    if(requested){PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, message));return;}

      }
    
    /**
     * 
     * @param player 
     *
    void inRestRoom(final Player player)
    {
                String message = "[Ascension]Tvt: You Registered in Tvt Event!\nDo You want Teleport in [Ascension]Rest Room?";
	        RequestResponseHandler responseHandler = new RequestResponseHandler(player){

	            public void acceptRequest(Creature requester, Player responder)
                    {
                        TeleportService.goInCircle(player, 300200000, 1, 176, 21, 144, (byte)0);
	            }

	            public void denyRequest(Creature requester, Player responder){ return; }
	        };
	    boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
	    if(requested){PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, message));return;}

    }
    */
}
