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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.StaticObjectController;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.staticdoor.StaticDoorTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author MrPoke
 *
 */
public class StaticDoor extends StaticObject {

	private boolean open = false;
	/**
	 * @param objectId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public StaticDoor(int objectId, StaticObjectController controller, SpawnTemplate spawnTemplate,
		StaticDoorTemplate objectTemplate) {
		super(objectId, controller, spawnTemplate, objectTemplate);
	}

	
	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	
	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
		PacketSendUtility.broadcastPacket(this, new SM_EMOTION(this.getSpawn().getStaticId(), open ?  EmotionType.OPEN_DOOR : EmotionType.CLOSE_DOOR));
	}
	
	@Override
	public StaticDoorTemplate getObjectTemplate() {
		return (StaticDoorTemplate) super.getObjectTemplate();
	}
}
