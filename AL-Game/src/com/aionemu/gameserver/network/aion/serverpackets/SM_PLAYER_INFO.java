/**
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerAppearance;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;


/**
 * This packet is displaying visible players.
 * 
 * @author -Nemesiss-, Avol, srx47 modified cura
 */
public class SM_PLAYER_INFO extends AionServerPacket {

	/**
	 * Visible player
	 */
	private final Player player;
	private boolean enemy;

	/**
	 * Constructs new <tt>SM_PLAYER_INFO </tt> packet
	 * 
	 * @param player
	 *          actual player.
	 * @param enemy
	 */
	public SM_PLAYER_INFO(Player player, boolean enemy) {
		this.player = player;
		this.enemy = enemy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		Player activePlayer = con.getActivePlayer();
		if (activePlayer == null || player == null) {
			return;
		}
		PlayerCommonData pcd = player.getCommonData();
		final int raceId;
		if (player.getAdminNeutral() > 1 || activePlayer.getAdminNeutral() > 1) {
			raceId = activePlayer.getRace().getRaceId();
		}
		else if (activePlayer.isEnemy(player)) {
			raceId = (activePlayer.getRace().getRaceId() == 0 ? 1 : 0);
		}
		else
			raceId = player.getRace().getRaceId();

		final int genderId = pcd.getGender().getGenderId();
		final PlayerAppearance playerAppearance = player.getPlayerAppearance();

		writeF(player.getX());// x
		writeF(player.getY());// y
		writeF(player.getZ());// z
		writeD(player.getObjectId());
		/**
		 * A3 female asmodian A2 male asmodian A1 female elyos A0 male elyos
		 */
		writeD(pcd.getTemplateId());
		/**
		 * Transformed state - send transformed model id Regular state - send player model id (from common data)
		 */
		writeD(player.getTransformedModelId() == 0 ? pcd.getTemplateId() : player.getTransformedModelId());

		writeC(0x00); // new 2.0 Packet --- probably pet info?
		writeD(player.getEffectController().checkAvatar() ? 2 : 1); // enable avatar skills
		writeC(enemy ? 0x00 : 0x26);

		writeC(raceId); // race
		writeC(pcd.getPlayerClass().getClassId());
		writeC(genderId); // sex
		writeH(player.getState());

		byte[] unk = new byte[8];
		writeB(unk);

		writeC(player.getHeading());
		writeS(player.getFullName());
		writeH(pcd.getTitleId());
		writeH(player.getCommonData().isHaveMentorFlag()? 1:0);
		
		writeH(player.getCastingSkillId());

		if (player.isLegionMember())
		{
			writeD(player.getLegion().getLegionId());
			writeC(player.getLegion().getLegionEmblem().getEmblemId());
			writeC(player.getLegion().getLegionEmblem().getEmblemType().getValue());
			writeC(player.getLegion().getLegionEmblem().getEmblemType() == LegionEmblemType.DEFAULT ? 0x00 : 0xFF);
			writeC(player.getLegion().getLegionEmblem().getColor_r());
			writeC(player.getLegion().getLegionEmblem().getColor_g());
			writeC(player.getLegion().getLegionEmblem().getColor_b());
			writeS(player.getLegion().getLegionName());
		}
		else
		{
			writeQ(0);
			writeD(0);
		}
		int maxHp = player.getLifeStats().getMaxHp();
		int currHp = player.getLifeStats().getCurrentHp();
		writeC(100 * currHp / maxHp);// %hp
		writeH(pcd.getDp());// current dp
		writeC(0x00);// unk (0x00)

		List<Item> items = player.getEquipment().getEquippedItemsWithoutStigma();
		short mask = 0;
		for (Item item : items) {
			mask |= item.getEquipmentSlot();
		}

		writeH(mask);

		for (Item item : items) {
			if (item.getEquipmentSlot() < Short.MAX_VALUE * 2) {
				writeD(item.getItemSkinTemplate().getTemplateId());
				GodStone godStone = item.getGodStone();
				writeD(godStone != null ? godStone.getItemId() : 0);
				writeD(item.getItemColor());
				writeH(0x00);// unk (0x00)
			}
		}

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
		
		writeC(5);// always 5 o0
		
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
		writeC(playerAppearance.getShoulderSize());

		writeC(playerAppearance.getTorso());
		writeC(playerAppearance.getChest()); // only woman
		writeC(playerAppearance.getWaist());

		writeC(playerAppearance.getHips());
		writeC(playerAppearance.getArmThickness());
		writeC(playerAppearance.getHandSize());
		writeC(playerAppearance.getLegThicnkess());

		writeC(playerAppearance.getFootSize());
		writeC(playerAppearance.getFacialRate());

		writeC(0x00); // always 0
		writeC(playerAppearance.getArmLength());
		writeC(playerAppearance.getLegLength());
		writeC(playerAppearance.getShoulders());
		writeC(playerAppearance.getFaceShape());
		writeC(0x00); // always 0
		
		writeC(playerAppearance.getVoice());

		writeF(playerAppearance.getHeight());
		writeF(0.25f); // scale
		writeF(2.0f); // gravity or slide surface o_O
		writeF(player.getGameStats().getMovementSpeedFloat()); // move speed

		Stat2 attackSpeed = player.getGameStats().getAttackSpeed();
		writeH(attackSpeed.getBase());
		writeH(attackSpeed.getCurrent());
        writeC(player.getTelEffect()); // This Spawn Effect!                
		

		writeS(player.hasStore() ? player.getStore().getStoreMessage() : "");// private store message

		/**
		 * Movement
		 */
		writeF(0);
		writeF(0);
		writeF(0);

		writeF(player.getX());// x
		writeF(player.getY());// y
		writeF(player.getZ());// z
		writeC(0x00); // move type

		if (player.isUsingFlyTeleport()) {
			writeD(player.getFlightTeleportId());
			writeD(player.getFlightDistance());
		}

		writeC(player.getVisualState()); // visualState
		writeS(player.getCommonData().getNote()); // note show in right down windows if your target on player

		writeH(player.getLevel()); // [level]
		writeH(player.getPlayerSettings().getDisplay()); // unk - 0x04
		writeH(player.getPlayerSettings().getDeny()); // unk - 0x00
		writeH(player.getAbyssRank().getRank().getId()); // abyss rank
		writeH(0x00); // unk - 0x01
		writeD(player.getTarget() == null ? 0 : player.getTarget().getObjectId());
		writeC(0); // suspect id
		writeD(0);
		if (player.isMentor())
			writeC(1);
		else
			writeC(0);
	}
}
