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
package com.aionemu.gameserver.services.toypet;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public class PetAdoptionService {

	private static final Logger log = LoggerFactory.getLogger(PetAdoptionService.class);

	/**
	 * Create a pet for player (with validation)
	 * 
	 * @param player
	 * @param eggObjId
	 * @param petId
	 * @param name
	 * @param decorationId
	 */
	public static void adoptPet(Player player, int eggObjId, int petId, String name, int decorationId) {

		if (!validateEgg(player, eggObjId, petId)) {
			return;
		}
		addPet(player, petId, name, decorationId);
	}

	/**
	 * Add pet to player
	 * 
	 * @param player
	 * @param petId
	 * @param name
	 * @param decorationId
	 */
	public static void addPet(Player player, int petId, String name, int decorationId) {
		if (player.getPetList().hasPet(petId)) {
			log.warn("Duplicate pet adoption");
			return;
		}
		PetCommonData petCommonData = player.getPetList().addPet(player, petId, decorationId, name);
		if (petCommonData != null) {
			PacketSendUtility.sendPacket(player, new SM_PET(1, petCommonData));
		}
	}

	private static boolean validateEgg(Player player, int eggObjId, int petId) {
		int eggId = player.getInventory().getItemByObjId(eggObjId).getItemId();
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(eggId);
		if (template == null || template.getFuncPetId() != petId) {
			return false;
		}
		return player.getInventory().decreaseByObjectId(eggObjId, 1);
	}

	/**
	 * Delete pet
	 * 
	 * @param player
	 * @param petId
	 */
	public static void surrenderPet(Player player, int petId) {
		PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (player.getPet() != null && player.getPet().getPetId() == petCommonData.getPetId()) {
			petCommonData.setCancelFood(true);
			PetSpawnService.dismissPet(player, false);
		}
		player.getPetList().deletePet(petCommonData.getPetId());
		PacketSendUtility.sendPacket(player, new SM_PET(2, petCommonData));
	}

}
