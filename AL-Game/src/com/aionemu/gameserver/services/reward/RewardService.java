package com.aionemu.gameserver.services.reward;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.dao.RewardServiceDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * 
 * @author KID
 * 
 */
public class RewardService {

	private static RewardService controller = new RewardService();
	private static final Logger log = LoggerFactory.getLogger(RewardService.class);
	private RewardServiceDAO dao;

	public static RewardService getInstance() {
		return controller;
	}

	public RewardService() {
		dao = DAOManager.getDAO(RewardServiceDAO.class);
	}

	public void verify(Player player) {
		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
			log.warn("[RewardController] player " + player.getName() + " tried to receive item with full inventory.");
			return;
		}

		FastList<RewardEntryItem> list = dao.getAvailable(player.getObjectId());
		if (list.size() == 0)
			return;

		FastList<Integer> rewarded = FastList.newInstance();
		for (RewardEntryItem item : list) {
			if (DataManager.ITEM_DATA.getItemTemplate(item.id) == null) {
				log.warn("[RewardController]["+item.unique+"] null template for item " + item.id + " on player " + player.getObjectId() + ".");
				continue;
			}

			try {
				long count = ItemService.addItem(player, item.id, item.count);
				if (count == 0) {
					log.info("[RewardController]["+item.unique+"] player " + player.getName() + " has received (" + item.count + ")" + item.id + ".");
					rewarded.add(item.unique);
				} else {
					log.warn("[RewardController]["+item.unique+"] player " + player.getName() + " was NOT received (" + item.count + ")" + item.id + " on " + player.getObjectId() + ".");
				}
			} catch (Exception e) {
				log.error("[RewardController]["+item.unique+"] failed to add item (" + item.count + ")" + item.id + " to " + player.getObjectId(), e);
				continue;
			}
		}

		if (rewarded.size() > 0) {
			dao.uncheckAvailable(rewarded);

			FastList.recycle(rewarded);
			FastList.recycle(list);
		}
	}
}
