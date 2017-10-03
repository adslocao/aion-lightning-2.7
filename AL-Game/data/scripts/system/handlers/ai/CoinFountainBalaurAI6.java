package ai;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.TranslationService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
*
* @author Medzo, Seita
* @modified Ferosia
*/


@AIName("balaur_fountain")
public class CoinFountainBalaurAI6 extends NpcAI2 {

	@Override
	protected void handleDialogStart(final Player player) {
		
		if(!CustomConfig.ENABLE_BALAUREA_FOUNTAIN_AI) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(player.getTarget().getObjectId(), 10));
			return;
		}
		
		if(!hasItem(player, 186000030)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_QUEST_ACQUIRE_ERROR_INVENTORY_ITEM(new DescriptionId(1478723)));
			return;
		}
		else if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			return;
		}
		else {
			Item item = player.getInventory().getFirstItemByItemId(186000030);
			player.getInventory().decreaseByObjectId(item.getObjectId(), 1);
			PacketSendUtility.sendMessage(player, TranslationService.COIN_FOUNTAIN_START.toString(player));
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					giveItem(player);
				}
			}, 1000);				
		}
	}	

	private boolean hasItem(Player player, int itemId) {
		return player.getInventory().getItemCountByItemId(itemId) > 0;
	}

	private void giveItem(Player player) {
		int rnd = Rnd.get(1, 100);
		
		if (rnd > CustomConfig.FOUNTAIN_PLATINUM) {
			ItemService.addItem(player, 186000096, 1);
			PacketSendUtility.sendMessage(player, TranslationService.COIN_FOUNTAIN_PLATINUM.toString(player));
		}
		else if (rnd > CustomConfig.FOUNTAIN_GOLD) {
			ItemService.addItem(player, 186000030, 2);
			PacketSendUtility.sendMessage(player, TranslationService.COIN_FOUNTAIN_GOLD.toString(player));
		}
		else if (rnd > CustomConfig.FOUNTAIN_RUSTED) {
			ItemService.addItem(player, 182005206, 1);
			PacketSendUtility.sendMessage(player, TranslationService.COIN_FOUNTAIN_RUSTED.toString(player));
		}
	}
}
