package com.aionemu.gameserver.command.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.ingameshop.InGameShopEn;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.item.ArmorType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Ferosia
 */

public class CmdRemodel extends BaseCommand {
	
	public static Logger log = LoggerFactory.getLogger(CmdRemodel.class);
	
	public void execute(final Player player, String... params) {
		
		// 2 parameters are needed, only one if player ask for help
		if (params.length != 2) {
			showHelp(player);
			return;
		}
		
		String [] keepItemLink = params[0].split(";");
		String [] keepItemIdArray = keepItemLink[0].split(":");
		int keepItemId = ParseInteger(keepItemIdArray[1]);
				
		int extractItemId = ParseInteger(params[1]);
		
		if(extractItemId == 0) {
			PacketSendUtility.sendBrightYellowMessage(player, "[Remodel] " + params[1] + " n'est pas un ID d'objet valide.");
			return;
		}
		
		Storage inventory = player.getInventory();
		
		if(inventory.countItemsById(keepItemId) > 1) {
			PacketSendUtility.sendBrightYellowMessage(player, "[Remodel] L'objet [item:" + keepItemId + "] est pr\u00E9sent plusieurs fois dans votre inventaire. Il ne doit y en avoir qu'un.");
			return;
		}
		
		Item keepItem = inventory.getFirstItemByItemId(keepItemId);
		
		
		ItemTemplate templateExtractItem = DataManager.ITEM_DATA.getItemTemplate(extractItemId);
        if (templateExtractItem == null) {
        	PacketSendUtility.sendBrightYellowMessage(player, "[Remodel] L'objet " + extractItemId + " n'existe pas dans le client 2.7");
            return;
        }
        
        if(keepItem == null) {
        	PacketSendUtility.sendBrightYellowMessage(player, "[Remodel] L'objet [item:" + keepItemId + "] doit \u00EAtre dans votre inventaire et non \u00E9quipp\u00E9.");
            return;
        }
        
		// Check that types match.
		if (keepItem.getItemTemplate().getWeaponType() != templateExtractItem.getWeaponType()
			|| (templateExtractItem.getArmorType() != ArmorType.CLOTHES && keepItem.getItemTemplate()
				.getArmorType() != templateExtractItem.getArmorType())
			|| keepItem.getItemTemplate().getArmorType() == ArmorType.CLOTHES
			|| keepItem.getItemTemplate().getItemSlot() != templateExtractItem.getItemSlot()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CHANGE_ITEM_SKIN_NOT_COMPATIBLE(new DescriptionId(keepItem.getItemTemplate().getNameId()), new DescriptionId(templateExtractItem.getNameId())));
			return;
		}
		
		final int tollPrice = 800;
        final int tolls = player.getClientConnection().getAccount().getToll();
        
        if (tolls < tollPrice) {
            PacketSendUtility.sendBrightYellowMessage(player, "[Remodel] Vous n'avez pas assez de points (" + tolls + "/" + tollPrice + ")");
            return;
        }
        player.getClientConnection().getAccount().setToll(tolls - tollPrice);
        PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(tolls - tollPrice));
        InGameShopEn.getInstance().removeToll(player, tollPrice);
        
        keepItem.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(extractItemId));
        ItemPacketService.updateItemAfterInfoChange(player, keepItem);
        
        PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1300483, new DescriptionId(keepItem.getItemTemplate().getNameId())));
        PacketSendUtility.sendBrightYellowMessage(player, "[Remodel] Votre objet " + params[0] + " vient d'\u00EAtre remodel\u00E9 en [item:" + params[1] + "].");
        PacketSendUtility.sendWhiteMessage(player, "Vous avez d\u00E9pens\u00E9 " + tollPrice + " points.");
        
	}
	
}