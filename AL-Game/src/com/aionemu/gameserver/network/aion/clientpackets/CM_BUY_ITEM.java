/**
 * This file is part of aion-unique <aion-unique.smfnew.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.model.trade.RepurchaseList;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.PrivateStoreService;
import com.aionemu.gameserver.services.RepurchaseService;
import com.aionemu.gameserver.services.TradeService;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;

/**
 * @author orz, ATracer, Simple, xTz
 */
public class CM_BUY_ITEM extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_BUY_ITEM.class);
	
	private int sellerObjId;
	private int unk1;
	private int amount;
	private int itemId;
	private long count;
	private boolean isAudit;
	private TradeList tradeList;
	private RepurchaseList repurchaseList;

	public CM_BUY_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		sellerObjId = readD();
		unk1 = readH();
		amount = readH(); // total no of items

		if (amount < 0 || amount > 36) {
			isAudit = true;
			AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM amount: " + amount);
			return;
		}
		if (unk1 == 2) {
			repurchaseList = new RepurchaseList(sellerObjId);
		}
		else {
			tradeList = new TradeList(sellerObjId);
		}

		for (int i = 0; i < amount; i++) {
			itemId = readD();
			count = readQ();

			// prevent exploit packets
			if (count < 1 || itemId <= 0 || itemId == 190000073 || itemId == 190000074 || count > 20000) {
				isAudit = true;
					AuditLogger.info(player, "Player might be abusing CM_BUY_ITEM item: " + itemId + " count: " + count);
				break;
			}

			if (unk1 == 13 || unk1 == 14 || unk1 == 15) {
				tradeList.addBuyItem(itemId, count);
			}
			else if (unk1 == 0 || unk1 == 1) {
				tradeList.addSellItem(itemId, count);
			}
			else if (unk1 == 2) {
				repurchaseList.addRepurchaseItem(player, itemId, count);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		VisibleObject target = player.getTarget();

		if (isAudit || player == null || target == null)
			return;

		if (target.getObjectId() != sellerObjId) {
			AuditLogger.info(player, "Trade exploit, send fake");
			return;
		}

		switch (unk1) {
			case 0:
				Player targetPlayer = (Player) World.getInstance().findVisibleObject(sellerObjId);
				PrivateStoreService.sellStoreItem(targetPlayer, player, tradeList);
				break;
			case 1:
				TradeService.performSellToShop(player, tradeList);
				break;
			case 2:
				RepurchaseService.getInstance().repurchaseFromShop(player, repurchaseList);
				break;
			case 13:
				Npc npc = (Npc) World.getInstance().findVisibleObject(sellerObjId);
				TradeListTemplate tlist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
				if (tlist.getTradeNpcType() == TradeNpcType.NORMAL)
					TradeService.performBuyFromShop(player, tradeList);
				break;
			case 14:
				Npc npc1 = (Npc) World.getInstance().findVisibleObject(sellerObjId);
				TradeListTemplate tlist1 = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc1.getNpcId());
				if (tlist1.getTradeNpcType() == TradeNpcType.ABYSS)
					TradeService.performBuyFromAbyssShop(player, tradeList);
				break;
			case 15:
				Npc npc2 = (Npc) World.getInstance().findVisibleObject(sellerObjId);
				TradeListTemplate tlist2 = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc2.getNpcId());
				if (tlist2.getTradeNpcType() == TradeNpcType.REWARD)
					TradeService.performBuyFromRewardShop(player, tradeList);
				break;
			default:
				log.info(String.format("Unhandle shop action unk1: %d", unk1));
				break;
		}
	}
}
