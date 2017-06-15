/*
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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.PetAction;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.templates.pet.PetFunctionType;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import java.util.Collection;

/**
 * @author M@xx, xTz, Rolandas
 */
public class SM_PET extends AionServerPacket {

	private int actionId;
	private Pet pet;
	private PetCommonData commonData;
	private int itemObjectId;
	private Collection<PetCommonData> pets;
	private int count;
	private int subType;
	private int shuggleEmotion;
	private int happinessAdded;

	private boolean isLooting;
	private int lootNpcId;

	public SM_PET(int subType, int actionId, int objectId, int count, Pet pet) {
		this.subType = subType;
		this.actionId = actionId;
		this.count = count;
		this.itemObjectId = objectId;
		this.pet = pet;
	}

	public SM_PET(int actionId) {
		this.actionId = actionId;
	}

	public SM_PET(int actionId, Pet pet) {
		this(0, actionId, 0, 0, pet);
	}

	public SM_PET(int actionId, boolean isLooting) {
		this.actionId = actionId;
		this.isLooting = isLooting;
	}

	public SM_PET(int actionId, boolean isLooting, int npcId) {
		this(actionId, isLooting);
		this.lootNpcId = npcId;
	}

	/**
	 * For mood only
	 * 
	 * @param actionId
	 * @param pet
	 * @param shuggleEmotion
	 */
	public SM_PET(Pet pet, int subType, int shuggleEmotion, int happinessAdded) {
		this(0, PetAction.MOOD.getActionId(), 0, 0, pet);
		this.shuggleEmotion = shuggleEmotion;
		this.subType = subType;
		this.happinessAdded = happinessAdded;
	}

	/**
	 * For adopt only
	 * 
	 * @param actionId
	 * @param commonData
	 */
	public SM_PET(int actionId, PetCommonData commonData) {
		this.actionId = actionId;
		this.commonData = commonData;
	}

	/**
	 * For listing all pets on this character
	 * 
	 * @param actionId
	 * @param pets
	 */
	public SM_PET(int actionId, Collection<PetCommonData> pets) {
		this.actionId = actionId;
		this.pets = pets;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		PetTemplate petTemplate = null;
		writeH(actionId);
		switch (actionId) {
			case 0:
				// load list on login
				writeC(0); // unk
				writeH(pets.size());
				for (PetCommonData petCommonData : pets) {
					petTemplate = DataManager.PET_DATA.getPetTemplate(petCommonData.getPetId());
					writeS(petCommonData.getName());
					writeD(petCommonData.getPetId());
					writeD(petCommonData.getObjectId());
					writeD(petCommonData.getMasterObjectId());
					writeD(0);
					writeD(0);
					writeD((int) petCommonData.getBirthday());
					writeD(0); // accompanying time

					if (petTemplate.ContainsFunction(PetFunctionType.WAREHOUSE)) {
						writeH(PetFunctionType.WAREHOUSE.getId());
					}
					else if (petTemplate.ContainsFunction(PetFunctionType.NONE)) {
						writeH(PetFunctionType.NONE.getId());
					}

					if (petTemplate.ContainsFunction(PetFunctionType.FOOD)) {
						writeH(PetFunctionType.FOOD.getId());
						writeC(0x10); // seen values 4, 8, 10, 13
						writeC(petCommonData.getHungryLevel());
						writeC(0);
						writeC(0);
						writeD((int) petCommonData.getTime() / 1000);
					}
					else if (petTemplate.ContainsFunction(PetFunctionType.NONE)
						|| petTemplate.ContainsFunction(PetFunctionType.WAREHOUSE)) {
						writeH(PetFunctionType.NONE.getId());
					}

					writeH(PetFunctionType.APPEARANCE.getId());
					writeC(0); // not implemented color R ?
					writeC(0); // not implemented color G ?
					writeC(0); // not implemented color B ?
					writeD(petCommonData.getDecoration());

					// epilog
					writeD(0); // unk
					writeD(0); // unk
				}
				break;
			case 1:
				// adopt
				writeS(commonData.getName());
				writeD(commonData.getPetId());
				writeD(commonData.getObjectId());
				writeD(commonData.getMasterObjectId());
				writeD(0);
				writeD(0);
				writeD(commonData.getBirthday());
				writeD(0); // accompanying time
				petTemplate = DataManager.PET_DATA.getPetTemplate(commonData.getPetId());
				if (petTemplate.ContainsFunction(PetFunctionType.WAREHOUSE)) {
					writeH(PetFunctionType.WAREHOUSE.getId());
				}
				else if (petTemplate.ContainsFunction(PetFunctionType.NONE)) {
					writeH(PetFunctionType.NONE.getId());
				}

				if (petTemplate.ContainsFunction(PetFunctionType.FOOD)) {
					writeH(PetFunctionType.FOOD.getId());
					writeC(0);
					writeC(0);
					writeC(0);
					writeC(0);
					writeD(0);
				}
				else if (petTemplate.ContainsFunction(PetFunctionType.NONE)
					|| petTemplate.ContainsFunction(PetFunctionType.WAREHOUSE)) {
					writeH(PetFunctionType.NONE.getId());
				}

				writeH(PetFunctionType.APPEARANCE.getId());
				writeC(0); // not implemented color R ?
				writeC(0); // not implemented color G ?
				writeC(0); // not implemented color B ?
				writeD(commonData.getDecoration());

				// epilog
				writeD(0); // unk
				writeD(0); // unk
				break;
			case 2:
				// surrender
				writeD(commonData.getPetId());
				writeD(commonData.getObjectId());
				writeD(0); // unk
				writeD(0); // unk
				break;
			case 3:
				// spawn
				writeS(pet.getName());
				writeD(pet.getPetId());
				writeD(pet.getObjectId());

				if (pet.getPosition().getX() == 0 && pet.getPosition().getY() == 0 && pet.getPosition().getZ() == 0) {
					writeF(pet.getMaster().getX());
					writeF(pet.getMaster().getY());
					writeF(pet.getMaster().getZ());

					writeF(pet.getMaster().getX());
					writeF(pet.getMaster().getY());
					writeF(pet.getMaster().getZ());

					writeC(pet.getMaster().getHeading());
				}
				else {
					writeF(pet.getPosition().getX());
					writeF(pet.getPosition().getY());
					writeF(pet.getPosition().getZ());
					writeF(pet.getMoveController().getTargetX2());
					writeF(pet.getMoveController().getTargetY2());
					writeF(pet.getMoveController().getTargetZ2());
					writeC(pet.getHeading());
				}

				writeD(pet.getMaster().getObjectId()); // unk

				writeC(1); // unk
				writeD(0); // accompanying time ??
				writeD(pet.getCommonData().getDecoration());
				writeD(0); // wings ID if customize_attach = 1
				writeD(0); // unk
				break;
			case 4:
				// dismiss
				writeD(pet.getObjectId());
				writeC(0x01);
				break;
			case 9:
				writeH(1);
				writeC(1);
				writeC(subType);
				switch (subType) {
					case 1:
						writeD(pet.getCommonData().getHungryLevel());
						writeD(0);
						writeD(itemObjectId);
						writeD(count);
						break;
					case 2:
						writeD(pet.getCommonData().getHungryLevel());
						writeD(0);
						writeD(itemObjectId);
						writeD(count);
						writeC(0);
						break;
					case 4:
					case 5:
						writeD(pet.getCommonData().getHungryLevel());
						writeD(0);
						break;
					case 6:
						writeD(pet.getCommonData().getHungryLevel());
						writeD(0);
						writeD(itemObjectId);
						writeC(0);
						break;
					case 7:
						writeD(pet.getCommonData().getHungryLevel());
						writeD(600);// time
						writeH(45081);
						writeH(32830);
						writeD(0);
						break;
					case 8:
						writeD(pet.getCommonData().getHungryLevel());
						writeD((int) pet.getCommonData().getTime() / 1000);
						writeD(itemObjectId);
						writeD(count);
						break;
				}
				break;
			case 10:
				// rename
				writeD(pet.getObjectId());
				writeS(pet.getName());
				break;
			case 12:
				switch (subType) {
					case 0: // check pet status
						writeC(subType);
						// desynced feedback data, need to send delta in percents
						if (pet.getCommonData().getLastSentPoints() < pet.getCommonData().getMoodPoints(true))
							writeD(pet.getCommonData().getMoodPoints(true) - pet.getCommonData().getLastSentPoints());
						else {
							writeD(0);
							pet.getCommonData().setLastSentPoints(pet.getCommonData().getMoodPoints(true));
						}
						break;
					case 2: // emotion sent
						writeC(subType);
						writeD(happinessAdded);
						writeD(pet.getCommonData().getMoodPoints(true));
						writeD(shuggleEmotion);
						pet.getCommonData().setLastSentPoints(pet.getCommonData().getMoodPoints(true));
						pet.getCommonData().setMoodCdStarted(System.currentTimeMillis());
						break;
					case 3: // give gift
						writeC(subType);
						writeD(pet.getPetTemplate().getConditionReward());
						pet.getCommonData().setGiftCdStarted(System.currentTimeMillis());
						break;
					case 4: // periodic update
						writeC(subType);
						writeD(pet.getCommonData().getMoodPoints(true));
						writeD(pet.getCommonData().getMoodRemainingTime());
						writeD(pet.getCommonData().getGiftRemainingTime());
						pet.getCommonData().setLastSentPoints(pet.getCommonData().getMoodPoints(true));
						break;
				}
				break;
			case 13:
				// looting
				writeC(3);
				if (lootNpcId > 0) {
					writeC(2); // 0x02 display looted msg.
					writeD(lootNpcId);
				}
				else {
					writeC(0);
					writeC(isLooting ? 1 : 0);
				}
				break;
			default:
				break;
		}
	}
}
