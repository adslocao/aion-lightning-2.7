/*
 * This file is part of Aion Lightning <www.aion-lightning.com>.
 *
 *  Aion Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion Lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.item.actions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;

/**
 * @author Tiger
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceTimeClear")
public class InstanceTimeClear extends AbstractItemAction {

	@XmlAttribute
	protected int mapid;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		return true;
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {	
		if (parentItem.getActivationCount() > 1)
			parentItem.setActivationCount(parentItem.getActivationCount() - 1);
		else
			player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
		
		player.getPortalCooldownList().removePortalCoolDown(mapid);
		   
	}
}