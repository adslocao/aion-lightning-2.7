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
package com.aionemu.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.recipe.Component;
import com.aionemu.gameserver.model.templates.recipe.RecipeTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemService.ItemAddPredicate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke, sphinx, synchro2
 */
public class CraftService {

	private static final Logger log = LoggerFactory.getLogger(CraftService.class);

	/**
	 * @param player
	 * @param recipetemplate
	 * @param critCount
	 */
	public static void finishCrafting(final Player player, RecipeTemplate recipetemplate, int critCount) {

		if (recipetemplate.getMaxProductionCount() != null) {
			player.getRecipeList().deleteRecipe(player, recipetemplate.getId());
			if (critCount == 0) {
				QuestEngine.getInstance().onFailCraft(new QuestEnv(null, player, 0, 0), recipetemplate.getComboProduct(1) == null? 0 : recipetemplate.getComboProduct(1));
			}
		}

		int xpReward = (int) ((0.008 * (recipetemplate.getSkillpoint() + 100) * (recipetemplate.getSkillpoint() + 100) + 60));

		int productItemId = critCount > 0 ? recipetemplate.getComboProduct(critCount) : recipetemplate.getProductid();

		ItemService.addItem(player, productItemId, recipetemplate.getQuantity(), new ItemAddPredicate() {

			@Override
			public boolean apply(Item item) {
				if (item.getItemTemplate().isWeapon() || item.getItemTemplate().isArmor()) {
					item.setItemCreator(player.getName());
				}
				return true;
			}
		});

		int gainedCraftExp = (int) RewardType.CRAFTING.calcReward(player, xpReward);

		if (player.getSkillList().addSkillXp(player, recipetemplate.getSkillid(), gainedCraftExp, recipetemplate.getSkillpoint())) {
			player.getCommonData().addExp(xpReward, RewardType.CRAFTING);
		}
		else {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_MSG_DONT_GET_PRODUCTION_EXP(new DescriptionId(DataManager.SKILL_DATA.getSkillTemplate(recipetemplate.getSkillid()).getNameId())));
		}

		if (recipetemplate.getCraftDelayId() != null) {
			player.getCraftCooldownList().addCraftCooldown(recipetemplate.getCraftDelayId(),
				recipetemplate.getCraftDelayTime());
		}
	}

	/**
	 * @param player
	 * @param targetTemplateId
	 * @param recipeId
	 * @param targetObjId
	 */
	public static void startCrafting(Player player, int recipeId, int targetObjId) {

		RecipeTemplate recipeTemplate = DataManager.RECIPE_DATA.getRecipeTemplateById(recipeId);
		int skillId = recipeTemplate.getSkillid();
		VisibleObject target = player.getKnownList().getObject(targetObjId);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());

		if (!checkCraft(player, recipeTemplate, skillId, target, itemTemplate)) {
			sendCancelCraft(player, skillId, targetObjId, itemTemplate);
			return;
		}

		if (recipeTemplate.getDp() != null)
			player.getCommonData().addDp(-recipeTemplate.getDp());

		int skillLvlDiff = player.getSkillList().getSkillLevel(skillId) - recipeTemplate.getSkillpoint();
		player.setCraftingTask(new CraftingTask(player, (StaticObject) target, recipeTemplate, skillLvlDiff));
		
		if(skillId == 40009)
			player.getCraftingTask().setInterval(850);
		
		player.getCraftingTask().start();
	}

	private static boolean checkCraft(Player player, RecipeTemplate recipeTemplate, int skillId, VisibleObject target,
		ItemTemplate itemTemplate) {

		if (recipeTemplate == null) {
			return false;
		}

		if (itemTemplate == null) {
			return false;
		}

		if (player.getCraftingTask() != null && player.getCraftingTask().isInProgress()) {
			return false;
		}

		// morphing dont need static object/npc to use
		if ((skillId != 40009) && (target == null || !(target instanceof StaticObject))) {
			log.info("[AUDIT] Player " + player.getName() + " tried to craft incorrect target.");
			return false;
		}

		if (recipeTemplate.getDp() != null && (player.getCommonData().getDp() < recipeTemplate.getDp())) {
			log.info("[AUDIT] Player " + player.getName() + " try craft without required DP count.");
			return false;
		}

		if (player.getInventory().isFull()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_COMBINE_INVENTORY_IS_FULL);
			return false;
		}

		if (recipeTemplate.getCraftDelayId() != null) {
			if (!player.getCraftCooldownList().isCanCraft(recipeTemplate.getCraftDelayId())) {
				log.info("[AUDIT] Player " + player.getName() + " try craft item before cooldown expire.");
				return false;
			}
		}

		for (Component component : recipeTemplate.getComponent()) {
			if (!player.getInventory().decreaseByItemId(component.getItemid(), component.getQuantity())) {
				log.info("[AUDIT] Player " + player.getName() + " tried craft without required items.");
				return false;
			}
		}

		if (!player.getSkillList().isSkillPresent(skillId)
			|| player.getSkillList().getSkillLevel(skillId) < recipeTemplate.getSkillpoint()) {
			log.info("[AUDIT] Player " + player.getName() + " tried craft without required skill.");
			return false;
		}

		return true;
	}

	private static void sendCancelCraft(Player player, int skillId, int targetObjId, ItemTemplate itemTemplate) {

		PacketSendUtility.sendPacket(player, new SM_CRAFT_UPDATE(skillId, itemTemplate, 0, 0, 4));
		PacketSendUtility.broadcastPacket(player, new SM_CRAFT_ANIMATION(player.getObjectId(), targetObjId, 0, 2), true);
	}
}
