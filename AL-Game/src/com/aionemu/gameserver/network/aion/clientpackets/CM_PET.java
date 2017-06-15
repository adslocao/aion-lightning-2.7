/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * aion-emu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * aion-emu. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.services.toypet.PetAdoptionService;
import com.aionemu.gameserver.services.toypet.PetMoodService;
import com.aionemu.gameserver.services.toypet.PetService;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author M@xx, xTz
 */
public class CM_PET extends AionClientPacket {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_PET.class);

	private int actionId;
	private PetAction action;
	private int petId;
	private String petName;
	private int decorationId;
	private int eggObjId;
	private int objectId;
	private int count;
	private int subType;
	private int emotionId;
	private int actionType;
	private int dopingItemId;

	@SuppressWarnings("unused")
	private int unk2;
	@SuppressWarnings("unused")
	private int unk3;
	@SuppressWarnings("unused")
	private int unk5;
	@SuppressWarnings("unused")
	private int unk6;

	public CM_PET(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readH();
		action = PetAction.getActionById(actionId);
		switch (action) {
			case ADOPT:
				eggObjId = readD();
				petId = readD();
				unk2 = readC();
				unk3 = readD();
				decorationId = readD();
				unk5 = readD();
				unk6 = readD();
				petName = readS();
				break;
			case SURRENDER:
			case SPAWN:
			case DISMISS:
				petId = readD();
				break;
			case FOOD:
				actionType = readD();
				objectId = readD();
				if (actionType == 2)
					dopingItemId = readD();
				else
					count = readD();
				break;
			case RENAME:
				petId = readD();
				petName = readS();
				break;
			case MOOD:
				subType = readD();
				emotionId = readD();
				break;
			default:
				break;
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		Pet pet = player.getPet();
		switch (action) {
			case ADOPT:
				PetAdoptionService.adoptPet(player, eggObjId, petId, petName, decorationId);
				break;
			case SURRENDER:
				PetAdoptionService.surrenderPet(player, petId);
				break;
			case SPAWN:
				PetSpawnService.summonPet(player, petId, true);
				break;
			case DISMISS:
				PetSpawnService.dismissPet(player, true);
				break;
			case FOOD:
				if (actionType == 2) {
					// Pet doping
					PetService.getInstance().feedDoping(player, dopingItemId);
				}
				else if (actionType == 3) {
					// Pet looting
				}
				else {
					if (pet != null && !pet.getCommonData().isFeedingTime()) {
						pet.getCommonData().setNrFood(0);
						PacketSendUtility.sendPacket(player, new SM_PET(8, actionId, objectId, count, player.getPet()));
					}
					else if (pet != null && objectId == 0 && pet.getCommonData().isFeedingTime()) {
						pet.getCommonData().setCancelFood(true);
						PacketSendUtility.sendPacket(player, new SM_PET(4, actionId, 0, 0, player.getPet()));
						PacketSendUtility.sendPacket(player,
								new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
						pet.getCommonData().setNrFood(0);
					}
					else
						PetService.getInstance().removeObject(objectId, count, actionId, player);
				}
				break;
			case RENAME:
				PetService.getInstance().renamePet(player, petName);
				break;
			case MOOD:
				if (pet != null && pet.getCommonData().getMoodRemainingTime() == 0) {
					PetMoodService.checkMood(pet, subType, emotionId);
				}
			default:
				break;
		}
	}

}