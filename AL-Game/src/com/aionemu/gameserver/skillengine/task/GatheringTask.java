/*
 * This file is part of aion-unique <aion-unique.org>.
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
package com.aionemu.gameserver.skillengine.task;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.gather.GatherableTemplate;
import com.aionemu.gameserver.model.templates.gather.Material;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHER_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemAddPredicate;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class GatheringTask extends AbstractCraftTask {

	private GatherableTemplate template;
	private Material material;
	
	private static final GatherItemAddPredicate ITEM_ADD_PREDICATE = new GatherItemAddPredicate();
	
	private static class GatherItemAddPredicate extends ItemAddPredicate {

		@Override
		public ItemUpdateType getUpdateType(Item input) {
			return ItemUpdateType.INC_GATHER;
		}
	}

	public GatheringTask(Player requestor, Gatherable gatherable, Material material, int skillLvlDiff) {
		super(requestor, gatherable, skillLvlDiff);
		this.template = gatherable.getObjectTemplate();
		this.material = material;
	}

	@Override
	protected void onInteractionAbort() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, 0, 0, 5));
		// TODO this packet is incorrect cause i need to find emotion of aborted gathering
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			2));
	}

	@Override
	protected void onInteractionFinish() {
		((Gatherable) responder).getController().completeInteraction();
	}

	@Override
	protected void onInteractionStart() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, 0, 0, 0));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			0), true);
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			1), true);
	}

	@Override
	protected void sendInteractionUpdate() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue,
			currentFailureValue, 1));
	}

	@Override
	protected void onFailureFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue,
			currentFailureValue, 1));
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue,
			currentFailureValue, 7));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(),
			3), true);
	}

	@Override
	protected boolean onSuccessFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 2));
		PacketSendUtility.sendPacket(requestor, new SM_GATHER_UPDATE(template, material, currentSuccessValue, currentFailureValue, 6));
		PacketSendUtility.broadcastPacket(requestor, new SM_GATHER_STATUS(requestor.getObjectId(), responder.getObjectId(), 2), true);
		PacketSendUtility.sendPacket(requestor, SM_SYSTEM_MESSAGE.STR_EXTRACT_GATHER_SUCCESS_1_BASIC(new DescriptionId(material.getNameid())));
		requestor.getInventory().decreaseByItemId(template.getRequiredItemId(), 1);
		ItemService.addItem(requestor, material.getItemid(), 1, ITEM_ADD_PREDICATE);
		if (requestor.isInInstance()) {
			requestor.getPosition().getWorldMapInstance().getInstanceHandler().onGather(requestor, (Gatherable) responder);
		}
		((Gatherable) responder).getController().rewardPlayer(requestor);
		return true;
	}
}
