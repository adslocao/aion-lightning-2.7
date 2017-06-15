/*
 * This file is part of Aion X EMU <aionxemu.com>.
 *
 *  This is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionengine.chatserver.model.channel;

import java.nio.charset.Charset;
import java.util.Arrays;

import com.aionengine.chatserver.model.PlayerClass;
import com.aionengine.chatserver.model.Race;
import com.aionengine.chatserver.model.WorldMapType;
import com.aionengine.chatserver.service.GameServerService;

/**
 * @author ATracer
 */
public enum Channels
{
	/**
	 * LFG channels
	 */
	LFG_E(new LfgChannel(Race.ELYOS), "@\u0001partyFind_PF\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	LFG_A(new LfgChannel(Race.ASMODIANS), "@\u0001partyFind_PF\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	/**
	 * Trade channels
	 */
	TRADE_POETA_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_VERTERON_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF1A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_SANCTUM_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LC1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ELTNEN_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_THEOMOBOS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF2A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_HEIRON_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ISHALGEN_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_MORHEIM_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_PANDAEMONIUM_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DC1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_BELUSLAN_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ALTGARD_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF1A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_BRUSTHONIN_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF2A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ABYSS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_Ab1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_KAISINEL_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LC2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_INGGISON_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF4\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_MARCHUTAN_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DC2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),	
	TRADE_GELKMAROS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF4\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),	
	TRADE_UNDERPASS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_Underpass\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),	
	
	TRADE_POETA_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_VERTERON_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF1A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_SANCTUM_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LC1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ELTNEN_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_THEOMOBOS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF2A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_HEIRON_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ISHALGEN_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_MORHEIM_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_PANDAEMONIUM_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DC1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_BELUSLAN_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ALTGARD_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF1A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_BRUSTHONIN_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF2A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ABYSS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_Ab1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_KAISINEL_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LC2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_INGGISON_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF4\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_MARCHUTAN_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DC2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),	
	TRADE_GELKMAROS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF4\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_UNDERPASS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_Underpass\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	/**
	 * Region channels
	 */
	REGION_POETA_E(new RegionChannel(WorldMapType.POETA.getId(), Race.ELYOS), "@\u0001public_LF1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_VERTERON_E(new RegionChannel(WorldMapType.VERTERON.getId(), Race.ELYOS), "@\u0001public_LF1A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_SANCTUM_E(new RegionChannel(WorldMapType.SANCTUM.getId(), Race.ELYOS), "@\u0001public_LC1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ELTNEN_E(new RegionChannel(WorldMapType.ELTNEN.getId(), Race.ELYOS), "@\u0001public_LF2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_THEOMOBOS_E(new RegionChannel(WorldMapType.THEOMOBOS.getId(), Race.ELYOS), "@\u0001public_LF2A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_HEIRON_E(new RegionChannel(WorldMapType.HEIRON.getId(), Race.ELYOS), "@\u0001public_LF3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ISHALGEN_E(new RegionChannel(WorldMapType.ISHALGEN.getId(), Race.ELYOS), "@\u0001public_DF1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_MORHEIM_E(new RegionChannel(WorldMapType.MORHEIM.getId(), Race.ELYOS), "@\u0001public_DF2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_PANDAEMONIUM_E(new RegionChannel(WorldMapType.PANDAEMONIUM.getId(), Race.ELYOS), "@\u0001public_DC1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_BELUSLAN_E(new RegionChannel(WorldMapType.BELUSLAN.getId(), Race.ELYOS), "@\u0001public_DF3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ALTGARD_E(new RegionChannel(WorldMapType.ALTGARD.getId(), Race.ELYOS), "@\u0001public_DF1A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_BRUSTHONIN_E(new RegionChannel(WorldMapType.BRUSTHONIN.getId(), Race.ELYOS), "@\u0001public_DF2A\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ABYSS_E(new RegionChannel(WorldMapType.RESHANTA.getId(), Race.ELYOS), "@\u0001public_Ab1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_KAISINEL_E(new RegionChannel(WorldMapType.KAISINEL.getId(), Race.ELYOS), "@\u0001public_LC2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_INGGISON_E(new RegionChannel(WorldMapType.INGGISON.getId(), Race.ELYOS), "@\u0001public_LF4\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_MARCHUTAN_E(new RegionChannel(WorldMapType.MARCHUTAN.getId(), Race.ELYOS), "@\u0001public_DC2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),	
	REGION_GELKMAROS_E(new RegionChannel(WorldMapType.GELKMAROS.getId(), Race.ELYOS), "@\u0001public_DF4\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_UNDERPASS_E(new RegionChannel(WorldMapType.UNDERPASS.getId(), Race.ELYOS), "@\u0001public_Underpass\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
		
	REGION_POETA_A(new RegionChannel(WorldMapType.POETA.getId(), Race.ASMODIANS), "@\u0001public_LF1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_VERTERON_A(new RegionChannel(WorldMapType.VERTERON.getId(), Race.ASMODIANS), "@\u0001public_LF1A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_SANCTUM_A(new RegionChannel(WorldMapType.SANCTUM.getId(), Race.ASMODIANS), "@\u0001public_LC1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ELTNEN_A(new RegionChannel(WorldMapType.ELTNEN.getId(), Race.ASMODIANS), "@\u0001public_LF2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_THEOMOBOS_A(new RegionChannel(WorldMapType.THEOMOBOS.getId(), Race.ASMODIANS), "@\u0001public_LF2A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_HEIRON_A(new RegionChannel(WorldMapType.HEIRON.getId(), Race.ASMODIANS), "@\u0001public_LF3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ISHALGEN_A(new RegionChannel(WorldMapType.ISHALGEN.getId(), Race.ASMODIANS), "@\u0001public_DF1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_MORHEIM_A(new RegionChannel(WorldMapType.MORHEIM.getId(), Race.ASMODIANS), "@\u0001public_DF2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_PANDAEMONIUM_A(new RegionChannel(WorldMapType.PANDAEMONIUM.getId(), Race.ASMODIANS), "@\u0001public_DC1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_BELUSLAN_A(new RegionChannel(WorldMapType.BELUSLAN.getId(), Race.ASMODIANS), "@\u0001public_DF3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ALTGARD_A(new RegionChannel(WorldMapType.ALTGARD.getId(), Race.ASMODIANS), "@\u0001public_DF1A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_BRUSTHONIN_A(new RegionChannel(WorldMapType.BRUSTHONIN.getId(), Race.ASMODIANS), "@\u0001public_DF2A\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ABYSS_A(new RegionChannel(WorldMapType.RESHANTA.getId(), Race.ASMODIANS), "@\u0001public_Ab1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_KAISINEL_A(new RegionChannel(WorldMapType.KAISINEL.getId(), Race.ASMODIANS), "@\u0001public_LC2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_INGGISON_A(new RegionChannel(WorldMapType.INGGISON.getId(), Race.ASMODIANS), "@\u0001public_LF4\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_MARCHUTAN_A(new RegionChannel(WorldMapType.MARCHUTAN.getId(), Race.ASMODIANS), "@\u0001public_DC2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),	
	REGION_GELKMAROS_A(new RegionChannel(WorldMapType.GELKMAROS.getId(), Race.ASMODIANS), "@\u0001public_DF4\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_UNDERPASS_A(new RegionChannel(WorldMapType.UNDERPASS.getId(), Race.ASMODIANS), "@\u0001public_Underpass\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),

	/**
	*Others and instances
	*/
	REGION_KARAMATIS_E(new RegionChannel(300010000, Race.ELYOS), "@\u0001public_IDAbPro\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_KARAMATIS_A(new RegionChannel(300010000, Race.ASMODIANS), "@\u0001public_IDAbPro\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_KARAMATIS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbPro\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_KARAMATIS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbPro\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_BIOLAB_E(new RegionChannel(310050000, Race.ELYOS), "@\u0001public_IDLF3Lp\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_BIOLAB_A(new RegionChannel(310050000, Race.ASMODIANS), "@\u0001public_IDLF3Lp\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_BIOLAB_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLF3Lp\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_BIOLAB_A(new TradeChannel( Race.ASMODIANS), "@\u0001trade_IDLF3Lp\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_SLIVER_E(new RegionChannel(310070000, Race.ELYOS), "@\u0001public_IDLF1B_Stigma\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_SLIVER_A(new RegionChannel(310070000, Race.ASMODIANS), "@\u0001public_IDLF1B_Stigma\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_SLIVER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLF1B_Stigma\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_SLIVER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDLF1B_Stigma\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_SANCTUMARENA_E(new RegionChannel(310080000, Race.ELYOS), "@\u0001public_IDLC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_SANCTUMARENA_A(new RegionChannel(310080000, Race.ASMODIANS), "@\u0001public_IDLC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_SANCTUMARENA_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_SANCTUMARENA_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDLC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_TRINIELARENA_E(new RegionChannel(320090000, Race.ELYOS), "@\u0001public_IDDC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_TRINIELARENA_A(new RegionChannel(320090000, Race.ASMODIANS), "@\u0001public_IDDC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_TRINIELARENA_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_TRINIELARENA_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDC1_arena\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ATAXIAR1_E(new RegionChannel(320010000, Race.ELYOS), "@\u0001public_IDAbProD1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ATAXIAR1_A(new RegionChannel(320010000, Race.ASMODIANS), "@\u0001public_IDAbProD1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ATAXIAR1_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbProD1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ATAXIAR1_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbProD1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ATAXIAR2_E(new RegionChannel(320020000, Race.ELYOS), "@\u0001public_IDAbProD2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ATAXIAR2_A(new RegionChannel(320020000, Race.ASMODIANS), "@\u0001public_IDAbProD2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ATAXIAR2_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbProD2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ATAXIAR2_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbProD2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_BREGIRUN_E(new RegionChannel(320030000, Race.ELYOS), "@\u0001public_IDAbGateD1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_BREGIRUN_A(new RegionChannel(320030000, Race.ASMODIANS), "@\u0001public_IDAbGateD1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_BREGIRUN_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbGateD1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_BREGIRUN_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbGateD1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_NIDALBER_E(new RegionChannel(320040000, Race.ELYOS), "@\u0001public_IDAbGateD2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_NIDALBER_A(new RegionChannel(320040000, Race.ASMODIANS), "@\u0001public_IDAbGateD2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_NIDALBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbGateD2\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_NIDALBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbGateD2\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_SKYTEMPLE_E(new RegionChannel(320050000, Race.ELYOS), "@\u0001public_IDDF2Flying\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_SKYTEMPLE_A(new RegionChannel(320050000, Race.ASMODIANS), "@\u0001public_IDDF2Flying\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_SKYTEMPLE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDF2Flying\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_SKYTEMPLE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDF2Flying\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_FIRETEMPLE_E(new RegionChannel(320100000, Race.ELYOS), "@\u0001public_IDDF2_Dflame\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_FIRETEMPLE_A(new RegionChannel(320100000, Race.ASMODIANS), "@\u0001public_IDDF2_Dflame\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_FIRETEMPLE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDF2_Dflame\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_FIRETEMPLE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDF2_Dflame\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_SPACE_E(new RegionChannel(320070000, Race.ELYOS), "@\u0001public_IDSpace\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_SPACE_A(new RegionChannel(320070000, Race.ASMODIANS), "@\u0001public_IDSpace\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_SPACE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDSpace\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_SPACE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDSpace\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_PRISON1_E(new RegionChannel(510010000, Race.ELYOS), "@\u0001public_LF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_PRISON1_A(new RegionChannel(510010000, Race.ASMODIANS), "@\u0001public_LF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_PRISON1_E(new TradeChannel(Race.ELYOS), "@\u0001trade_LF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_PRISON1_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_LF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_PRISON2_E(new RegionChannel(520010000, Race.ELYOS), "@\u0001public_DF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_PRISON2_A(new RegionChannel(520010000, Race.ASMODIANS), "@\u0001public_DF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_PRISON2_E(new TradeChannel(Race.ELYOS), "@\u0001trade_DF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_PRISON2_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_DF_Prison\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_TEST1_E(new RegionChannel(900100000, Race.ELYOS), "@\u0001public_Test_GiantMonster\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_TEST1_A(new RegionChannel(900100000, Race.ASMODIANS), "@\u0001public_Test_GiantMonster\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_TEST1_E(new TradeChannel(Race.ELYOS), "@\u0001trade_Test_GiantMonster\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_TEST1_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_Test_GiantMonster\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_TEST2_E(new RegionChannel(900020000, Race.ELYOS), "@\u0001public_Test_Basic\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_TEST2_A(new RegionChannel(900020000, Race.ASMODIANS), "@\u0001public_Test_Basic\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_TEST2_E(new TradeChannel(Race.ELYOS), "@\u0001trade_Test_Basic\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_TEST2_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_Test_Basic\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_TEST3_E(new RegionChannel(900030000, Race.ELYOS), "@\u0001public_Test_Server\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_TEST3_A(new RegionChannel(900030000, Race.ASMODIANS), "@\u0001public_Test_Server\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_TEST3_E(new TradeChannel(Race.ELYOS), "@\u0001trade_Test_Server\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_TEST3_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_Test_Server\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_STEELRAKE_E(new RegionChannel(300100000, Race.ELYOS), "@\u0001public_IDshulackShip\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_STEELRAKE_A(new RegionChannel(300100000, Race.ASMODIANS), "@\u0001public_IDshulackShip\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_STEELRAKE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDshulackShip\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_STEELRAKE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDshulackShip\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_UDAS_E(new RegionChannel(300150000, Race.ELYOS), "@\u0001public_IDTemple_Up\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_UDAS_A(new RegionChannel(300150000, Race.ASMODIANS), "@\u0001public_IDTemple_Up\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_UDAS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDTemple_Up\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_UDAS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDTemple_Up\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_NOCHSANA_TRAINING_CAMP_E(new RegionChannel(300030000, Race.ELYOS), "@\u0001public_IDAb1_MiniCastle\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_NOCHSANA_TRAINING_CAMP_A(new RegionChannel(300030000, Race.ASMODIANS), "@\u0001public_IDAb1_MiniCastle\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_NOCHSANA_TRAINING_CAMP_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAb1_MiniCastle\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_NOCHSANA_TRAINING_CAMP_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAb1_MiniCastle\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_DARK_POETA_E(new RegionChannel(300040000, Race.ELYOS), "@\u0001public_IDLF1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_DARK_POETA_A(new RegionChannel(300040000, Race.ASMODIANS), "@\u0001public_IDLF1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_DARK_POETA_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLF1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_DARK_POETA_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDLF1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ASTERIA_CHAMBER_E(new RegionChannel(300050000, Race.ELYOS), "@\u0001public_IDAbRe_Up_Asteria\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ASTERIA_CHAMBER_A(new RegionChannel(300050000, Race.ASMODIANS), "@\u0001public_IDAbRe_Up_Asteria\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ASTERIA_CHAMBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Up_Asteria\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ASTERIA_CHAMBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Up_Asteria\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_SULFUR_TREE_NEST_E(new RegionChannel(300060000, Race.ELYOS), "@\u0001public_IDAbRe_Low_Divine\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_SULFUR_TREE_NEST_A(new RegionChannel(300060000, Race.ASMODIANS), "@\u0001public_IDAbRe_Low_Divine\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_SULFUR_TREE_NEST_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Low_Divine\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_SULFUR_TREE_NEST_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Low_Divine\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_CHAMBER_OF_ROAH_E(new RegionChannel(300070000, Race.ELYOS), "@\u0001public_IDAbRe_Up_Rhoo\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_CHAMBER_OF_ROAH_A(new RegionChannel(300070000, Race.ASMODIANS), "@\u0001public_IDAbRe_Up_Rhoo\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_CHAMBER_OF_ROAH_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Up_Rhoo\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_CHAMBER_OF_ROAH_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Up_Rhoo\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_LEFT_WING_CHAMBER_E(new RegionChannel(300080000, Race.ELYOS), "@\u0001public_IDAbRe_Low_Wciel\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_LEFT_WING_CHAMBER_A(new RegionChannel(300080000, Race.ASMODIANS), "@\u0001public_IDAbRe_Low_Wciel\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_LEFT_WING_CHAMBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Low_Wciel\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_LEFT_WING_CHAMBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Low_Wciel\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_RIGHT_WING_CHAMBER_E(new RegionChannel(300090000, Race.ELYOS), "@\u0001public_IDAbRe_Low_Eciel\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_RIGHT_WING_CHAMBER_A(new RegionChannel(300090000, Race.ASMODIANS), "@\u0001public_IDAbRe_Low_Eciel\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_RIGHT_WING_CHAMBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Low_Eciel\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_RIGHT_WING_CHAMBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Low_Eciel\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_DREDGION_E(new RegionChannel(300110000, Race.ELYOS), "@\u0001public_IDAb1_Dreadgion\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_DREDGION_A(new RegionChannel(300110000, Race.ASMODIANS), "@\u0001public_IDAb1_Dreadgion\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_DREDGION_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAb1_Dreadgion\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_DREDGION_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAb1_Dreadgion\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_KYSIS_CHAMBER_E(new RegionChannel(300120000, Race.ELYOS), "@\u0001public_IDAbRe_Up3_Dkisas\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_KYSIS_CHAMBER_A(new RegionChannel(300120000, Race.ASMODIANS), "@\u0001public_IDAbRe_Up3_Dkisas\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_KYSIS_CHAMBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Up3_Dkisas\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_KYSIS_CHAMBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Up3_Dkisas\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_MIREN_CHAMBER_E(new RegionChannel(300130000, Race.ELYOS), "@\u0001public_IDAbRe_Up3_Lamiren\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_MIREN_CHAMBER_A(new RegionChannel(300130000, Race.ASMODIANS), "@\u0001public_IDAbRe_Up3_Lamiren\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_MIREN_CHAMBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Up3_Lamiren\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_MIREN_CHAMBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Up3_Lamiren\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_KROTAN_CHAMBER_E(new RegionChannel(300140000, Race.ELYOS), "@\u0001public_IDAbRe_Up3_Crotan\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_KROTAN_CHAMBER_A(new RegionChannel(300140000, Race.ASMODIANS), "@\u0001public_IDAbRe_Up3_Crotan\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_KROTAN_CHAMBER_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Up3_Crotan\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_KROTAN_CHAMBER_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Up3_Crotan\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_TEMPLELOW_E(new RegionChannel(300160000, Race.ELYOS), "@\u0001public_IDTemple_Low\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_TEMPLELOW_A(new RegionChannel(300160000, Race.ASMODIANS), "@\u0001public_IDTemple_Low\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_TEMPLELOW_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDTemple_Low\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_TEMPLELOW_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDTemple_Low\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_CATACOMBS_E(new RegionChannel(300170000, Race.ELYOS), "@\u0001public_IDCatacombs\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_CATACOMBS_A(new RegionChannel(300170000, Race.ASMODIANS), "@\u0001public_IDCatacombs\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_CATACOMBS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDCatacombs\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_CATACOMBS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDCatacombs\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_TALOCSHOLLOW_E(new RegionChannel(300190000, Race.ELYOS), "@\u0001public_IDElim\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_TALOCSHOLLOW_A(new RegionChannel(300190000, Race.ASMODIANS), "@\u0001public_IDElim\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_TALOCSHOLLOW_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDElim\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_TALOCSHOLLOW_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDElim\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_NOVICE_E(new RegionChannel(300200000, Race.ELYOS), "@\u0001public_IDNovice\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_NOVICE_A(new RegionChannel(300200000, Race.ASMODIANS), "@\u0001public_IDNovice\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_NOVICE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDNovice\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_NOVICE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDNovice\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_DREDGION2_E(new RegionChannel(300210000, Race.ELYOS), "@\u0001public_IDDreadgion_02\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_DREDGION2_A(new RegionChannel(300210000, Race.ASMODIANS), "@\u0001public_IDDreadgion_02\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_DREDGION2_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDreadgion_02\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_DREDGION2_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDreadgion_02\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_DEBRIS_OF_ABYSS_E(new RegionChannel(300220000, Race.ELYOS), "@\u0001public_IDAbRe_Core\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_DEBRIS_OF_ABYSS_A(new RegionChannel(300220000, Race.ASMODIANS), "@\u0001public_IDAbRe_Core\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_DEBRIS_OF_ABYSS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbRe_Core\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_DEBRIS_OF_ABYSS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbRe_Core\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_CROMEDE_E(new RegionChannel(300230000, Race.ELYOS), "@\u0001public_IDCromede\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_CROMEDE_A(new RegionChannel(300230000, Race.ASMODIANS), "@\u0001public_IDCromede\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_CROMEDE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDCromede\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_CROMEDE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDCromede\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_AZOTURAN_FORTRESS_E(new RegionChannel(310100000, Race.ELYOS), "@\u0001public_IDLF3_Castle_Lehpar\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_AZOTURAN_FORTRESS_A(new RegionChannel(310100000, Race.ASMODIANS), "@\u0001public_IDLF3_Castle_Lehpar\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_AZOTURAN_FORTRESS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLF3_Castle_Lehpar\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_AZOTURAN_FORTRESS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDLF3_Castle_Lehpar\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_INDRATU_FORTRESS_E(new RegionChannel(310090000, Race.ELYOS), "@\u0001public_IDLF3_Castle_indratoo\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_INDRATU_FORTRESS_A(new RegionChannel(310090000, Race.ASMODIANS), "@\u0001public_IDLF3_Castle_indratoo\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_INDRATU_FORTRESS_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLF3_Castle_indratoo\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_INDRATU_FORTRESS_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDLF3_Castle_indratoo\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_THEOBOMOS_LAB_E(new RegionChannel(310110000, Race.ELYOS), "@\u0001public_IDLF2a_Lab\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_THEOBOMOS_LAB_A(new RegionChannel(310110000, Race.ASMODIANS), "@\u0001public_IDLF2a_Lab\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_THEOBOMOS_LAB_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDLF2a_Lab\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_THEOBOMOS_LAB_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDLF2a_Lab\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_DRAUPNIR_CAVE_E(new RegionChannel(320080000, Race.ELYOS), "@\u0001public_IDDF3_Dragon\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_DRAUPNIR_CAVE_A(new RegionChannel(320080000, Race.ASMODIANS), "@\u0001public_IDDF3_Dragon\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_DRAUPNIR_CAVE_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDF3_Dragon\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_DRAUPNIR_CAVE_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDF3_Dragon\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ALQUIMIA_E(new RegionChannel(320110000, Race.ELYOS), "@\u0001public_IDDF3_LP\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ALQUIMIA_A(new RegionChannel(320110000, Race.ASMODIANS), "@\u0001public_IDDF3_LP\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ALQUIMIA_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDF3_LP\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ALQUIMIA_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDF3_LP\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ADMA_STRONGHOLD_E(new RegionChannel(320130000, Race.ELYOS), "@\u0001public_IDDf2a_Adma\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ADMA_STRONGHOLD_A(new RegionChannel(320130000, Race.ASMODIANS), "@\u0001public_IDDf2a_Adma\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ADMA_STRONGHOLD_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDDf2a_Adma\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ADMA_STRONGHOLD_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDDf2a_Adma\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_AERDINA_E(new RegionChannel(310030000, Race.ELYOS), "@\u0001public_IDAbGateL1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_AERDINA_A(new RegionChannel(310030000, Race.ASMODIANS), "@\u0001public_IDAbGateL1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_AERDINA_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbGateL1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_AERDINA_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbGateL1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_KARAMATIS3_E(new RegionChannel(310120000, Race.ELYOS), "@\u0001public_IDAbProL3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_KARAMATIS3_A(new RegionChannel(310120000, Race.ASMODIANS), "@\u0001public_IDAbProL3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_KARAMATIS3_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbProL3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_KARAMATIS3_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbProL3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_ATAXIAR3_E(new RegionChannel(320140000, Race.ELYOS), "@\u0001public_IDAbProD3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_ATAXIAR3_A(new RegionChannel(320140000, Race.ASMODIANS), "@\u0001public_IDAbProD3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_ATAXIAR3_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbProD3\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_ATAXIAR3_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbProD3\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	REGION_KARAMATIS1_E(new RegionChannel(310010000, Race.ELYOS), "@\u0001public_IDAbProL1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	REGION_KARAMATIS1_A(new RegionChannel(310010000, Race.ASMODIANS), "@\u0001public_IDAbProL1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	TRADE_KARAMATIS1_E(new TradeChannel(Race.ELYOS), "@\u0001trade_IDAbProL1\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	TRADE_KARAMATIS1_A(new TradeChannel(Race.ASMODIANS), "@\u0001trade_IDAbProL1\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	/**
	 * Job channels
	 */
	JOB_GLADIATOR_E(new JobChannel(PlayerClass.GLADIATOR, Race.ELYOS), "@\u0001job_Gladiateur[f:\"Gladiatrice\"]\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_GLADIATOR_A(new JobChannel(PlayerClass.GLADIATOR, Race.ASMODIANS), "@\u0001job_Gladiateur[f:\"Gladiatrice\"]\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_TEMPLAR_E(new JobChannel(PlayerClass.TEMPLAR, Race.ELYOS), "@\u0001job_Templier[f:\"Templi\u00E8re\"]\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_TEMPLAR_A(new JobChannel(PlayerClass.TEMPLAR, Race.ASMODIANS), "@\u0001job_Templier[f:\"Templi\u00E8re\"]\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_SORCERER_E(new JobChannel(PlayerClass.SORCERER, Race.ELYOS), "@\u0001job_Sorcier[f:\"Sorci\u00E8re\"]\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_SORCERER_A(new JobChannel(PlayerClass.SORCERER, Race.ASMODIANS), "@\u0001job_Sorcier[f:\"Sorci\u00E8re\"]\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_SPIRITMASTER_E(new JobChannel(PlayerClass.SPIRIT_MASTER, Race.ELYOS), "@\u0001job_Spiritualiste\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_SPIRITMASTER_A(new JobChannel(PlayerClass.SPIRIT_MASTER, Race.ASMODIANS), "@\u0001job_Spiritualiste\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_CHANTER_E(new JobChannel(PlayerClass.CHANTER, Race.ELYOS), "@\u0001job_A\u00E8de\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_CHANTER_A(new JobChannel(PlayerClass.CHANTER, Race.ASMODIANS), "@\u0001job_A\u00E8de\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_RANGER_E(new JobChannel(PlayerClass.RANGER, Race.ELYOS), "@\u0001job_R\u00F4deur[f:\"R\u00F4deuse\"]\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_RANGER_A(new JobChannel(PlayerClass.RANGER, Race.ASMODIANS), "@\u0001job_R\u00F4deur[f:\"R\u00F4deuse\"]\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_ASSASSIN_E(new JobChannel(PlayerClass.ASSASSIN, Race.ELYOS), "@\u0001job_Assassin[f:\"Assassine\"]\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_ASSASSIN_A(new JobChannel(PlayerClass.ASSASSIN, Race.ASMODIANS), "@\u0001job_Assassin[f:\"Assassine\"]\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	JOB_CLERIC_E(new JobChannel(PlayerClass.CLERIC, Race.ELYOS), "@\u0001job_Clerc\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	JOB_CLERIC_A(new JobChannel(PlayerClass.CLERIC, Race.ASMODIANS), "@\u0001job_Clerc\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	/**
	 * Language channels
	 */
	USER_ENGLISH_E(new LanguageChannel(Race.ELYOS), "@\u0001User_English\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_ENGLISH_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_English\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_FRENCH_E(new LanguageChannel(Race.ELYOS), "@\u0001User_French\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_FRENCH_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_French\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_GERMAN_E(new LanguageChannel(Race.ELYOS), "@\u0001User_German\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_GERMAN_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_German\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_ITALIAN_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Italian\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_ITALIAN_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Italian\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_SPANISH_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Spanish\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_SPANISH_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Spanish\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_DANISH_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Danish\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_DANISH_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Danish\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_SWEDISH_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Swedish\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_SWEDISH_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Swedish\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_FINNISH_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Finnish\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_FINNISH_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Finnish\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_NORWEGIAN_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Norwegian\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_NORWEGIAN_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Norwegian\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR"),
	
	USER_GREEK_E(new LanguageChannel(Race.ELYOS), "@\u0001User_Greek\u0001" + GameServerService.GAMESERVER_ID + ".0.AION.KOR"),
	USER_GREEK_A(new LanguageChannel(Race.ASMODIANS), "@\u0001User_Greek\u0001" + GameServerService.GAMESERVER_ID + ".1.AION.KOR");
	
	private Channel channel;
	private byte[] identifier;
	
	/**
	 * 
	 * @param channel
	 * @param identifier
	 */
	private Channels(Channel channel, String identifier)
	{
		this.channel = channel;
		this.identifier = identifier.getBytes(Charset.forName("UTF-16le"));
	}

	/**
	 * @return the channel
	 */
	public Channel getChannel()
	{
		return channel;
	}

	/**
	 * @return the identifier
	 */
	public byte[] getIdentifier()
	{
		return identifier;
	}

	/**
	 * 
	 * @param channelId
	 * @return
	 */
	public static Channel getChannelById(int channelId)
	{
		for(Channels channel : values())
		{
			if(channel.getChannel().getChannelId() == channelId)
				return channel.getChannel();
		}
		throw new IllegalArgumentException("Wrong channel id provided");
	}
	
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public static Channel getChannelByIdentifier(byte[] identifier)
	{
		for(Channels channel : values())
		{
			if(Arrays.equals(channel.getIdentifier(), identifier))
				return channel.getChannel();
		}
		return null;
	}

}
