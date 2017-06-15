/*
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

import com.aionemu.gameserver.network.aion.serverpackets.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is holding opcodes for all server packets. It's used only to have all opcodes in one place
 * 
 * @author Luno, alexa026, ATracer, avol, orz, cura
 */
public class ServerPacketsOpcodes {

	private static Map<Class<? extends AionServerPacket>, Integer> opcodes = new HashMap<Class<? extends AionServerPacket>, Integer>();

	static {
		Set<Integer> idSet = new HashSet<Integer>();

		addPacketOpcode(SM_VERSION_CHECK.class, 0x00, idSet);
		addPacketOpcode(SM_STATS_INFO.class, 0x01, idSet);
		addPacketOpcode(SM_STATUPDATE_HP.class, 0x03, idSet);
		addPacketOpcode(SM_STATUPDATE_MP.class, 0x04, idSet);
		addPacketOpcode(SM_ATTACK_STATUS.class, 0x05, idSet);
		addPacketOpcode(SM_STATUPDATE_DP.class, 0x06, idSet);
		addPacketOpcode(SM_DP_INFO.class, 0x07, idSet);
		addPacketOpcode(SM_STATUPDATE_EXP.class, 0x08, idSet);
		addPacketOpcode(SM_NPC_ASSEMBLER.class, 0x0A, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_NICKNAME.class, 0x0B, idSet);
		addPacketOpcode(SM_LEGION_TABS.class, 0x0C, idSet);
		addPacketOpcode(SM_ENTER_WORLD_CHECK.class, 0x0D, idSet);
		addPacketOpcode(SM_NPC_INFO.class, 0x0E, idSet);
		addPacketOpcode(SM_PLAYER_SPAWN.class, 0x0F, idSet);
		addPacketOpcode(SM_FORTRESS_INFO.class, 0xF3, idSet);
		addPacketOpcode(SM_GATHERABLE_INFO.class, 0x11, idSet);
		addPacketOpcode(SM_TELEPORT_LOC.class, 0x14, idSet);
		addPacketOpcode(SM_PLAYER_MOVE.class, 0x15, idSet);
		addPacketOpcode(SM_DELETE.class, 0x16, idSet);
		addPacketOpcode(SM_LOGIN_QUEUE.class, 0x17, idSet);
		addPacketOpcode(SM_MESSAGE.class, 0x18, idSet);
		addPacketOpcode(SM_SYSTEM_MESSAGE.class, 0x19, idSet);
		addPacketOpcode(SM_INVENTORY_INFO.class, 0x1A, idSet);
		addPacketOpcode(SM_INVENTORY_ADD_ITEM.class, 0x1B, idSet);
		addPacketOpcode(SM_DELETE_ITEM.class, 0x1C, idSet);
		addPacketOpcode(SM_INVENTORY_UPDATE_ITEM.class, 0x1D, idSet);
		addPacketOpcode(SM_UI_SETTINGS.class, 0x1E, idSet); 
		addPacketOpcode(SM_PLAYER_STANCE.class, 0x1F, idSet);
		addPacketOpcode(SM_PLAYER_INFO.class, 0x20, idSet);
		addPacketOpcode(SM_CASTSPELL.class, 0x21, idSet);
		addPacketOpcode(SM_GATHER_STATUS.class, 0x22, idSet);
		addPacketOpcode(SM_GATHER_UPDATE.class, 0x23, idSet);
		addPacketOpcode(SM_UPDATE_PLAYER_APPEARANCE.class, 0x24, idSet);
		addPacketOpcode(SM_EMOTION.class, 0x25, idSet);
		addPacketOpcode(SM_GAME_TIME.class, 0x26, idSet);
		addPacketOpcode(SM_TIME_CHECK.class, 0x27, idSet);
		addPacketOpcode(SM_LOOKATOBJECT.class, 0x28, idSet);
		addPacketOpcode(SM_TARGET_SELECTED.class, 0x29, idSet);
		addPacketOpcode(SM_SKILL_CANCEL.class, 0x2A, idSet);
		addPacketOpcode(SM_CASTSPELL_RESULT.class, 0x2B, idSet);
		addPacketOpcode(SM_SKILL_LIST.class, 0x2C, idSet);
		addPacketOpcode(SM_SKILL_REMOVE.class, 0x2D, idSet);
		addPacketOpcode(SM_SKILL_ACTIVATION.class, 0x2E, idSet);
		addPacketOpcode(SM_ABNORMAL_STATE.class, 0x31, idSet);
		addPacketOpcode(SM_ABNORMAL_EFFECT.class, 0x32, idSet);
		addPacketOpcode(SM_SKILL_COOLDOWN.class, 0x33, idSet);
		addPacketOpcode(SM_QUESTION_WINDOW.class, 0x34, idSet);
		addPacketOpcode(SM_ATTACK.class, 0x36, idSet);
		addPacketOpcode(SM_MOVE.class, 0x37, idSet);
		addPacketOpcode(SM_HEADING_UPDATE.class, 0x39, idSet);//TODO! not used
		addPacketOpcode(SM_TRANSFORM.class, 0x3A, idSet);
		addPacketOpcode(SM_DIALOG_WINDOW.class, 0x3C, idSet);
		addPacketOpcode(SM_SELL_ITEM.class, 0x3E, idSet);
		addPacketOpcode(SM_VIEW_PLAYER_DETAILS.class, 0x41, idSet);
		addPacketOpcode(SM_WEATHER.class, 0x43, idSet);
		addPacketOpcode(SM_PLAYER_STATE.class, 0x44, idSet);
		addPacketOpcode(SM_LEVEL_UPDATE.class, 0x46, idSet);
		addPacketOpcode(SM_QUEST_LIST.class, 0x47, idSet);
		addPacketOpcode(SM_KEY.class, 0x48, idSet);
		addPacketOpcode(SM_SUMMON_PANEL_REMOVE.class, 0x49, idSet);
		addPacketOpcode(SM_EXCHANGE_REQUEST.class, 0x4A, idSet);
		addPacketOpcode(SM_EXCHANGE_ADD_ITEM.class, 0x4B, idSet);
		addPacketOpcode(SM_EXCHANGE_ADD_KINAH.class, 0x4D, idSet);
		addPacketOpcode(SM_EXCHANGE_CONFIRMATION.class, 0x4E, idSet);
		addPacketOpcode(SM_EMOTION_LIST.class, 0x4F, idSet);
		addPacketOpcode(SM_TARGET_UPDATE.class, 0x51, idSet);
		addPacketOpcode(SM_INFLUENCE_RATIO.class, 0x55, idSet);
		addPacketOpcode(SM_PLASTIC_SURGERY.class, 0x53, idSet);
		addPacketOpcode(SM_FORTRESS_STATUS.class, 0x56, idSet);
		addPacketOpcode(SM_CAPTCHA.class, 0x57, idSet);
		addPacketOpcode(SM_RENAME.class, 0x58, idSet);
		addPacketOpcode(SM_SHOW_NPC_ON_MAP.class, 0x59, idSet);
		addPacketOpcode(SM_GROUP_INFO.class, 0x5A, idSet);
		addPacketOpcode(SM_GROUP_MEMBER_INFO.class, 0x5B, idSet);
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO.class, 0x60, idSet);
		addPacketOpcode(SM_QUIT_RESPONSE.class, 0x62, idSet);
		addPacketOpcode(SM_CHAT_WINDOW.class, 0x63, idSet);// 2.1
		addPacketOpcode(SM_PET.class, 0x65, idSet); //2.7
		addPacketOpcode(SM_ITEM_COOLDOWN.class, 0x67, idSet); //2.7
		addPacketOpcode(SM_UPDATE_NOTE.class, 0x68, idSet);
		addPacketOpcode(SM_PLAY_MOVIE.class, 0x69, idSet);
		addPacketOpcode(SM_LEGION_INFO.class, 0x6E, idSet); //2.7
		addPacketOpcode(SM_LEGION_ADD_MEMBER.class, 0x6F, idSet);
		addPacketOpcode(SM_LEGION_LEAVE_MEMBER.class, 0x70, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_MEMBER.class, 0x71, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_TITLE.class, 0x72, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_SELF_INTRO.class, 0x77, idSet);
		addPacketOpcode(SM_INSTANCE_SCORE.class, 0x79, idSet);
		//addPacketOpcode(SM_RIFT_STATUS.class, 0x8C, idSet);
		addPacketOpcode(SM_AUTO_GROUP.class, 0x7A, idSet);
		addPacketOpcode(SM_QUEST_COMPLETED_LIST.class, 0x7B, idSet);
		addPacketOpcode(SM_QUEST_ACTION.class, 0x7C, idSet);
		addPacketOpcode(SM_NEARBY_QUESTS.class, 0x7F, idSet);
		addPacketOpcode(SM_PING_RESPONSE.class, 0x80, idSet);
		addPacketOpcode(SM_CUBE_UPDATE.class, 0x82, idSet);
		addPacketOpcode(SM_FRIEND_LIST.class, 0x84, idSet);
		addPacketOpcode(SM_PRIVATE_STORE.class, 0x86, idSet);
		addPacketOpcode(SM_GROUP_LOOT.class, 0x87, idSet);
		addPacketOpcode(SM_ABYSS_RANK_UPDATE.class, 0x88, idSet);
		addPacketOpcode(SM_MAY_LOGIN_INTO_GAME.class, 0x89, idSet);
		addPacketOpcode(SM_ABYSS_RANKING_PLAYERS.class, 0x8A, idSet);
		addPacketOpcode(SM_ABYSS_RANKING_LEGIONS.class, 0x8B, idSet);
		addPacketOpcode(SM_INSTANCE_INFO.class, 0x8D, idSet);
		addPacketOpcode(SM_PONG.class, 0x8E, idSet);
		addPacketOpcode(SM_KISK_UPDATE.class, 0x90, idSet);
		addPacketOpcode(SM_PRIVATE_STORE_NAME.class, 0x91, idSet);
		addPacketOpcode(SM_BROKER_SERVICE.class, 0x92, idSet);
		addPacketOpcode(SM_MOTION.class, 0x94, idSet);
		addPacketOpcode(SM_TRADE_IN_LIST.class, 0x097, idSet);
		// addPacketOpcode(SM_BROKER_REGISTRATION_SERVICE.class, 0x93, idSet);
		// addPacketOpcode(SM_BROKER_SETTLED_LIST.class, 0x95, idSet);
		addPacketOpcode(SM_SUMMON_OWNER_REMOVE.class, 0x9A, idSet);
		addPacketOpcode(SM_SUMMON_PANEL.class, 0x99, idSet);
		addPacketOpcode(SM_SUMMON_UPDATE.class, 0x9B, idSet);
		addPacketOpcode(SM_TRANSFORM_IN_SUMMON.class, 0x9C, idSet);
		addPacketOpcode(SM_LEGION_MEMBERLIST.class, 0x9D, idSet);
		addPacketOpcode(SM_LEGION_EDIT.class, 0x9E, idSet);
		addPacketOpcode(SM_TOLL_INFO.class, 0x9F, idSet); // ingameshop
		addPacketOpcode(SM_MAIL_SERVICE.class, 0xA1, idSet);
		addPacketOpcode(SM_SUMMON_USESKILL.class, 0xA2, idSet);
		addPacketOpcode(SM_WINDSTREAM.class, 0xA3, idSet);
		addPacketOpcode(SM_WINDSTREAM_ANNOUNCE.class, 0xA4, idSet);
		addPacketOpcode(SM_FIND_GROUP.class, 0xA6, idSet);
		addPacketOpcode(SM_REPURCHASE.class, 0xA7, idSet);
		addPacketOpcode(SM_WAREHOUSE_INFO.class, 0xA8, idSet);
		addPacketOpcode(SM_WAREHOUSE_ADD_ITEM.class, 0xA9, idSet);
		addPacketOpcode(SM_DELETE_WAREHOUSE_ITEM.class, 0xAA, idSet);
		addPacketOpcode(SM_WAREHOUSE_UPDATE_ITEM.class, 0xAB, idSet);
		addPacketOpcode(SM_IN_GAME_SHOP_CATEGORY_LIST.class, 0xAC, idSet); // ingameshop
		addPacketOpcode(SM_IN_GAME_SHOP_LIST.class, 0xAD, idSet); // ingameshop
		addPacketOpcode(SM_IN_GAME_SHOP_ITEM.class, 0xAE, idSet); // ingameshop
		addPacketOpcode(SM_TITLE_INFO.class, 0xB0, idSet);
		addPacketOpcode(SM_CHARACTER_SELECT.class, 0xB1, idSet);
		// addPacketOpcode(SM_BROKER_REGISTERED_LIST.class, 0xB1, idSet);
		addPacketOpcode(SM_CRAFT_ANIMATION.class, 0xB4, idSet);
		addPacketOpcode(SM_CRAFT_UPDATE.class, 0xB5, idSet);
		addPacketOpcode(SM_ASCENSION_MORPH.class, 0xB6, idSet);
		addPacketOpcode(SM_ITEM_USAGE_ANIMATION.class, 0xB7, idSet);
		addPacketOpcode(SM_CUSTOM_SETTINGS.class, 0xB8, idSet);
		addPacketOpcode(SM_DUEL.class, 0xB9, idSet);
		addPacketOpcode(SM_PET_EMOTE.class, 0xBB, idSet);
		addPacketOpcode(SM_QUESTIONNAIRE.class, 0xBF, idSet);
		addPacketOpcode(SM_DIE.class, 0xC1, idSet);
		addPacketOpcode(SM_RESURRECT.class, 0xC2, idSet);
		addPacketOpcode(SM_FORCED_MOVE.class, 0xC3, idSet);
		addPacketOpcode(SM_TELEPORT_MAP.class, 0xC4, idSet);
		addPacketOpcode(SM_USE_OBJECT.class, 0xC5, idSet);
		//TODO! 0xC6 format: d - oid, cdd - smth related to SM_NPC_INFO
		addPacketOpcode(SM_L2AUTH_LOGIN_CHECK.class, 0xC7, idSet);
		addPacketOpcode(SM_CHARACTER_LIST.class, 0xC8, idSet);
		addPacketOpcode(SM_CREATE_CHARACTER.class, 0xC9, idSet);
		addPacketOpcode(SM_DELETE_CHARACTER.class, 0xCA, idSet);
		addPacketOpcode(SM_RESTORE_CHARACTER.class, 0xCB, idSet);
		addPacketOpcode(SM_TARGET_IMMOBILIZE.class, 0xCC, idSet);
		addPacketOpcode(SM_LOOT_STATUS.class, 0xCD, idSet);
		addPacketOpcode(SM_LOOT_ITEMLIST.class, 0xCE, idSet);
		addPacketOpcode(SM_RECIPE_LIST.class, 0x0CF, idSet);
		addPacketOpcode(SM_MANTRA_EFFECT.class, 0xD0, idSet);
		addPacketOpcode(SM_SIEGE_LOCATION_INFO.class, 0xD1, idSet);
		addPacketOpcode(SM_PLAYER_SEARCH.class, 0xD3, idSet);
		addPacketOpcode(SM_LEGION_SEND_EMBLEM.class, 0xD5, idSet);
		addPacketOpcode(SM_LEGION_SEND_EMBLEM_DATA.class, 0xD6, idSet);
		addPacketOpcode(SM_LEGION_UPDATE_EMBLEM.class, 0xD7, idSet);
		//TODO! 0xD8 format: ccc time milis: d time milis: d
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO2.class, 0xD9, idSet);
		addPacketOpcode(SM_SHIELD_EFFECT.class, 0xDA, idSet);
		addPacketOpcode(SM_ABYSS_ARTIFACT_INFO3.class, 0xDC, idSet);
		addPacketOpcode(SM_FRIEND_RESPONSE.class, 0xDE, idSet);
		addPacketOpcode(SM_BLOCK_RESPONSE.class, 0xDF, idSet);
		addPacketOpcode(SM_BLOCK_LIST.class, 0xE0, idSet);
		addPacketOpcode(SM_FRIEND_NOTIFY.class, 0xE1, idSet);
		addPacketOpcode(SM_CHANNEL_INFO.class, 0xE5, idSet);
		addPacketOpcode(SM_CHAT_INIT.class, 0xE6, idSet);
		addPacketOpcode(SM_MACRO_LIST.class, 0xE7, idSet);
		addPacketOpcode(SM_MACRO_RESULT.class, 0xE8, idSet);
		addPacketOpcode(SM_NICKNAME_CHECK_RESPONSE.class, 0xE9, idSet);
		addPacketOpcode(SM_SET_BIND_POINT.class, 0xEB, idSet);
		addPacketOpcode(SM_RIFT_ANNOUNCE.class, 0xEC, idSet);
		addPacketOpcode(SM_ABYSS_RANK.class, 0xED, idSet);
		addPacketOpcode(SM_PETITION.class, 0xEF, idSet);
		addPacketOpcode(SM_FRIEND_UPDATE.class, 0xF0, idSet);
		addPacketOpcode(SM_LEARN_RECIPE.class, 0xF1, idSet);
		addPacketOpcode(SM_RECIPE_DELETE.class, 0xF2, idSet);
		addPacketOpcode(SM_FLY_TIME.class, 0xF4, idSet);
		addPacketOpcode(SM_ALLIANCE_INFO.class, 0xF5, idSet);
		addPacketOpcode(SM_ALLIANCE_MEMBER_INFO.class, 0xF6, idSet);
		addPacketOpcode(SM_LEAVE_GROUP_MEMBER.class, 0xF7, idSet);
		addPacketOpcode(SM_SHOW_BRAND.class, 0xF9, idSet);
		addPacketOpcode(SM_ALLIANCE_READY_CHECK.class, 0xFA, idSet);
		addPacketOpcode(SM_PRICES.class, 0xFC, idSet);
		addPacketOpcode(SM_TRADELIST.class, 0xFD, idSet);
		addPacketOpcode(SM_RECONNECT_KEY.class, 0xFF, idSet);
		addPacketOpcode(SM_INSTANCE_STAGE_INFO .class, 0x8C, idSet);
		addPacketOpcode(SM_CUSTOM_PACKET.class, 99999, idSet); // fake packet
		// Unrecognized Opcodes from 1.5.4:
		// addPacketOpcode(SM_BUY_LIST.class, 0x7E, idSet);

		// Unrecognized Opcodes from 1.5.0:
		// addPacketOpcode(SM_VIRTUAL_AUTH.class, 0xE4, idSet);
		// addPacketOpcode(SM_WAITING_LIST.class, 0x18, idSet);
	}

	static int getOpcode(Class<? extends AionServerPacket> packetClass) {
		Integer opcode = opcodes.get(packetClass);
		if (opcode == null)
			throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");

		return opcode;
	}

	private static void addPacketOpcode(Class<? extends AionServerPacket> packetClass, int opcode, Set<Integer> idSet) {
		if (opcode < 0)
			return;

		if (idSet.contains(opcode))
			throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));

		idSet.add(opcode);
		opcodes.put(packetClass, opcode);
	}
}
