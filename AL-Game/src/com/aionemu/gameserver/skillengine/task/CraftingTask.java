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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.CraftConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import com.aionemu.gameserver.services.CraftService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke, synchro2
 */
public class CraftingTask extends AbstractCraftTask {

	protected RecipeTemplate recipeTemplate;
	protected ItemTemplate itemTemplate;
	protected int critCount;
	protected boolean crit = false;
	protected int maxCritCount;

	/**
	 * @param requestor
	 * @param responder
	 * @param successValue
	 * @param failureValue
	 */

	public CraftingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplate, int skillLvlDiff) {
		super(requestor, responder, skillLvlDiff);
		this.recipeTemplate = recipeTemplate;
		this.maxCritCount = recipeTemplate.getComboProductSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.task.AbstractCraftTask#onFailureFinish()
	 */
	@Override
	protected void onFailureFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate,
			currentSuccessValue, currentFailureValue, 6));
		PacketSendUtility.broadcastPacket(requestor,
			new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 3), true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.task.AbstractCraftTask#onSuccessFinish()
	 */
	@Override
	protected boolean onSuccessFinish() {
		if (crit && recipeTemplate.getComboProduct(critCount) != null) {
			PacketSendUtility.sendPacket(requestor,
				new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 3));
			onInteractionStart();
			return false;
		}
		else {
			PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate,
				currentSuccessValue, currentFailureValue, 5));
			PacketSendUtility.broadcastPacket(requestor,
				new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
			CraftService.finishCrafting(requestor, recipeTemplate, critCount);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.task.AbstractCraftTask#sendInteractionUpdate()
	 */
	@Override
	protected void sendInteractionUpdate() {
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate,
			currentSuccessValue, currentFailureValue, 1));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.task.AbstractInteractionTask#onInteractionAbort()
	 */
	@Override
	protected void onInteractionAbort() {
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 4));
		PacketSendUtility.broadcastPacket(requestor,
			new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
		requestor.setCraftingTask(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.task.AbstractInteractionTask#onInteractionFinish()
	 */
	@Override
	protected void onInteractionFinish() {
		requestor.setCraftingTask(null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aionemu.gameserver.skillengine.task.AbstractInteractionTask#onInteractionStart()
	 */
	@Override
	protected void onInteractionStart() {
		currentSuccessValue = 0;
		currentFailureValue = 0;
		// TODO: check if there could be a better way for morphing skill
		if(recipeTemplate.getSkillid() == 40009) {
			currentSuccessValue = 99;
		}
		checkCrit();
		if (critCount < maxCritCount && Rnd.get(100) < CraftConfig.CRAFT_CRIT_RATE) {
			critCount++;
			crit = true;
		}

		PacketSendUtility.sendPacket(requestor,
			new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, completeValue, completeValue, 0));
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 1));
		PacketSendUtility.broadcastPacket(requestor,
			new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 0), true);
		PacketSendUtility.broadcastPacket(requestor,
			new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 1), true);
	}
	
	protected void checkCrit() {
		if (crit) {
			crit = false;
			this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getComboProduct(critCount));
		}
		else {
			this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
		}
	}
}