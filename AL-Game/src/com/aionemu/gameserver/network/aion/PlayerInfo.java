/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author AEJTester
 * @author Nemesiss
 * @author Niato
 */
public abstract class PlayerInfo extends AionServerPacket {

	private static Logger log = LoggerFactory.getLogger(PlayerInfo.class);

	protected PlayerInfo() {
	}

	protected void writePlayerInfo(PlayerAccountData accPlData) {
		PlayerCommonData pbd = accPlData.getPlayerCommonData();
		final int raceId = pbd.getRace().getRaceId();
		final int genderId = pbd.getGender().getGenderId();
		final PlayerAppearance playerAppearance = accPlData.getAppereance();
		writeD(pbd.getPlayerObjId());
		writeS(pbd.getName(), 52);
		writeD(genderId);
		writeD(raceId);
		writeD(pbd.getPlayerClass().getClassId());
		writeD(playerAppearance.getVoice());
		writeD(playerAppearance.getSkinRGB());
		writeD(playerAppearance.getHairRGB());
		writeD(playerAppearance.getEyeRGB());
		writeD(playerAppearance.getLipRGB());
		writeC(playerAppearance.getFace());
		writeC(playerAppearance.getHair());
		writeC(playerAppearance.getDeco());
		writeC(playerAppearance.getTattoo());
		writeC(playerAppearance.getFaceContour());
		writeC(playerAppearance.getExpression());
		writeC(4);// always 4 o0
		writeC(playerAppearance.getJawLine());
		writeC(playerAppearance.getForehead());
		writeC(playerAppearance.getEyeHeight());
		writeC(playerAppearance.getEyeSpace());
		writeC(playerAppearance.getEyeWidth());
		writeC(playerAppearance.getEyeSize());
		writeC(playerAppearance.getEyeShape());
		writeC(playerAppearance.getEyeAngle());
		writeC(playerAppearance.getBrowHeight());
		writeC(playerAppearance.getBrowAngle());
		writeC(playerAppearance.getBrowShape());
		writeC(playerAppearance.getNose());
		writeC(playerAppearance.getNoseBridge());
		writeC(playerAppearance.getNoseWidth());
		writeC(playerAppearance.getNoseTip());
		writeC(playerAppearance.getCheek());
		writeC(playerAppearance.getLipHeight());
		writeC(playerAppearance.getMouthSize());
		writeC(playerAppearance.getLipSize());
		writeC(playerAppearance.getSmile());
		writeC(playerAppearance.getLipShape());
		writeC(playerAppearance.getJawHeigh());
		writeC(playerAppearance.getChinJut());
		writeC(playerAppearance.getEarShape());
		writeC(playerAppearance.getHeadSize());
		// 1.5.x 0x00, shoulderSize, armLength, legLength (BYTE) after HeadSize

		writeC(playerAppearance.getNeck());
		writeC(playerAppearance.getNeckLength());
		writeC(playerAppearance.getShoulderSize()); // shoulderSize

		writeC(playerAppearance.getTorso());
		writeC(playerAppearance.getChest());
		writeC(playerAppearance.getWaist());
		writeC(playerAppearance.getHips());
		writeC(playerAppearance.getArmThickness());
		writeC(playerAppearance.getHandSize());
		writeC(playerAppearance.getLegThicnkess());
		writeC(playerAppearance.getFootSize());
		writeC(playerAppearance.getFacialRate());
		writeC(0x00); // 0x00
		writeC(playerAppearance.getArmLength()); // armLength
		writeC(playerAppearance.getLegLength()); // legLength
		writeC(playerAppearance.getShoulders());
		writeC(playerAppearance.getFaceShape());
		writeC(0x00); // always 0 may be acessLevel
		writeC(0x00); // always 0 - unk
		writeC(0x00);
		writeF(playerAppearance.getHeight());
		int raceSex = 100000 + raceId * 2 + genderId;
		writeD(raceSex);
		writeD(pbd.getPosition().getMapId());// mapid for preloading map
		writeF(pbd.getPosition().getX());
		writeF(pbd.getPosition().getY());
		writeF(pbd.getPosition().getZ());
		writeD(pbd.getPosition().getHeading());
		writeD(pbd.getLevel());// lvl confirmed
		writeH(pbd.getTitleId());
		writeH(0); //unk 2.5
		if (accPlData.isLegionMember()) {
			writeD(accPlData.getLegion().getLegionId());
			writeS(accPlData.getLegion().getLegionName(), 88);
		}
		else {
			writeB(new byte[92]);
		}

		int itemsDataSize = 0;
		// TODO figure out this part when fully equipped
		List<Item> items = accPlData.getEquipment();

		for (Item item : items) {
			if(itemsDataSize >= 208)
				break;

			ItemTemplate itemTemplate = item.getItemTemplate();
			if (itemTemplate == null) {
				log.warn("Missing item. PlayerId: " + pbd.getPlayerObjId() + " ItemId: " + item.getObjectId());
				continue;
			}

			if(itemTemplate.isArmor() || itemTemplate.isWeapon()) {
				if (itemTemplate.getItemSlot() <= ItemSlot.PANTS.getSlotIdMask()) {
					writeC(1); // this flas is needed to show equipment on selection screen
					writeD(item.getItemSkinTemplate().getTemplateId());
					GodStone godStone = item.getGodStone();
					writeD(godStone != null ? godStone.getItemId() : 0);
					writeD(item.getItemColor());

					itemsDataSize += 13;
				}
			}
		}

		byte[] stupidNc = new byte[208 - itemsDataSize];
		writeB(stupidNc);
		writeD(accPlData.getDeletionTimeInSeconds());
	}
}
