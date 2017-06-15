package com.aionemu.gameserver.command.player;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.EquipType;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ferosia
 */

public class CmdReskin extends BaseCommand {
	
	public static Logger log = LoggerFactory.getLogger(CmdReskin.class);
	public static int cost = 50;
	
	public void execute(Player player, String... params) {
		
		// The id of the extracted item
		if (params.length != 1) {
			showHelp(player);
			return;
		}
		
		log.info(params[0]);
		
		int extractItemId = ParseInteger(params[0]);
		if(extractItemId == 0){
			try{
				String [] extractItemLink = params[0].split(";");
				String [] extractItemIdArray = extractItemLink[0].split(":");
				extractItemId = ParseInteger(extractItemIdArray[1]);
			}catch (Exception e) {
				extractItemId = 0;
	    	}
		}
		
		if(extractItemId == 0){
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Vous avez sp\u00E9cifi\u00E9 un mauvais ID.");
			return;
		}
				
		Storage inventory = player.getInventory();
		if(inventory.countItemsById(extractItemId) > 1) {
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] L'objet [item:" + extractItemId + "] est pr\u00E9sent plusieurs fois dans votre inventaire. Il ne doit y en avoir qu'un.");
			return;
		}
		
		Item extractItem = inventory.getFirstItemByItemId(extractItemId);

		if(extractItem == null) {
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] L'objet [item:" + extractItemId + "] n'a pas \u00E9t\u00E9 trouv\u00E9 dans votre inventaire.");
			return;
		}

		if(extractItem.getItemTemplate().getEquipmentType() != EquipType.ARMOR){
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Seul les armures sont reskinable via cette commande.");
			return;
		}
		
		// Get the corresponding item
		List<Item> items = player.getAllItems();
		ItemCategory extractCat = extractItem.getItemTemplate().getCategory();
		Item keepItem = null;
		
		if(extractCat != ItemCategory.HELMET && extractCat != ItemCategory.GLOVES && extractCat != ItemCategory.JACKET && extractCat != ItemCategory.PANTS && extractCat != ItemCategory.SHOES && extractCat != ItemCategory.SHOULDERS){
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Veuillez reskin votre vêtement sur une armure avant de faire la commande.");
			return;
		}
		
		for (Item item : items) {
			if(item.isEquipped() && item.getItemTemplate().getCategory() == extractCat){
				keepItem = item;
				break;
			}
		}
		
		if(keepItem == null) {
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Aucune armure correspondante disponible.");
			return;
		}

		if(keepItem.getItemTemplate().getEquipmentType() != EquipType.ARMOR){
			PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Seul les armures sont reskinable via cette command");
			return;
		}
		
		// Check enouth tool
        final int tolls = player.getClientConnection().getAccount().getToll();
        if (tolls < cost && !player.isGM()) {
            PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Vous n'avez pas assez de points (" + tolls + "/" + cost + ")");
            return;
        }
		
        // Remove tool
        if(!player.isGM()){
        	player.getClientConnection().getAccount().setToll(tolls - cost);
            PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(tolls - cost));
            InGameShopEn.getInstance().removeToll(player, cost);
        }
		
		// Remove Item
		player.getInventory().decreaseItemCount(extractItem, 1);

		// REMODEL ITEM
		keepItem.setItemSkinTemplate(extractItem.getItemSkinTemplate());

		// Transfer Dye
		keepItem.setItemColor(extractItem.getItemColor());

		// Notify Player
		ItemPacketService.updateItemAfterInfoChange(player, keepItem);

		PacketSendUtility.sendBrightYellowMessage(player, "[Reskin] Votre item est reskin. R\u00E9\u00E9quiper le pour le voir.");

	}	
}