package ai;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
*
 * @author Medzo , Seita
*/


@AIName("balaur_fountain")
public class CoinFountainBalaurAI6 extends NpcAI2 {

  @Override
    protected void handleDialogStart(final Player player) {
		if(!hasItem(player, 186000030)){
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_INVENTORY_ITEM(new DescriptionId(1478723)));
			return;
		}
		else if(hasItem(player, 186000030)) //recheck to be sure
		{
			Item item = player.getInventory().getFirstItemByItemId(186000030);
            player.getInventory().decreaseByObjectId(item.getObjectId(), 1);
			PacketSendUtility.sendMessage(player, "La fontaine se concentre (*roulement de tambour*)");
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					giveItem(player);
				}
			}, 2 * 1000);				
		}
	}	

    private boolean hasItem(Player player, int itemId) {
        return player.getInventory().getItemCountByItemId(itemId) > 0;
    }

    private void giveItem(Player player) {
        int rnd = Rnd.get(0, 100);
		// PacketSendUtility.sendMessage(player, "Votre score est de : " + rnd);
        if (rnd > 90 || rnd < 15) {
            ItemService.addItem(player, 186000030, 1); // or
			PacketSendUtility.sendMessage(player, "Bravo, vous recevez une médaille d'or ! ");
        }
		else if (rnd > 80 || rnd < 25) {
            ItemService.addItem(player, 186000096, 1); // platine
			PacketSendUtility.sendMessage(player, "Vous recevez une médaille de platine, vous pouvez retenter votre chance ! ");
        }
		else {
            ItemService.addItem(player, 182005205, 1); // rouille
			PacketSendUtility.sendMessage(player, "Ouuucchhh une médaille toute rouillée ! ");
        }
    }
}
