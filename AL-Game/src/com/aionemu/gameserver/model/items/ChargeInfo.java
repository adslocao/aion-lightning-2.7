/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.ObserverType;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer modified by Ferosia
 * TODO On attack event, apply attackBurn to fusionned item charge if item does not have charge level
 */
public class ChargeInfo extends ActionObserver {

	public static final int LEVEL2 = 1000000;
	public static final int LEVEL1 = 500000;

	private int chargePoints;
	private final int attackBurn;
	private final int defendBurn;
	private final Item item;
	private Player player;

	/**
	 * @param chargePoints
	 */
	public ChargeInfo(int chargePoints, Item item) {
		super(ObserverType.ATTACK_DEFEND);
		this.chargePoints = chargePoints;
		this.item = item;
		this.attackBurn = item.getItemTemplate().getBurnAttack();
		this.defendBurn = item.getItemTemplate().getBurnDefend();
	}

	public int getChargePoints() {
		return this.chargePoints;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int updateChargePoints(int addPoints) {
		int newChargePoints = chargePoints + addPoints;
		if (newChargePoints > LEVEL2) {
			newChargePoints = LEVEL2;
		}
		else if (newChargePoints < 0) {
			newChargePoints = 0;
		}
		if (item.isEquipped() && player != null)
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
		this.chargePoints = newChargePoints;
		return newChargePoints;
	}

	@Override
	public void attacked(Creature creature) {
		updateChargePoints(-defendBurn);
		Player player = this.player;
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}

	@Override
	public void attack(Creature creature) {
		// No attack burn of item charge if target is a Dummy
		if (creature.getTribe() == TribeClass.DUMMY || creature.getTribe() == TribeClass.DUMMY2)
			return;
		updateChargePoints(-attackBurn);
		Player player = this.player;
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}

}
