package com.aionemu.gameserver.command.admin;

import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapType;

/*Syntax : //goto <location> */


public class CmdGoTo extends BaseCommand {

	
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			showHelp(admin);
			return;
		}
		
		StringBuilder sbDestination = new StringBuilder();
		for(String p : params)
			sbDestination.append(p + " ");
		
		String destination = sbDestination.toString().trim();
		
		/**
		 * Elysea
		 */
		// Sanctum
		if (destination.equalsIgnoreCase("Sanctum"))
			goTo(admin, WorldMapType.SANCTUM.getId(), 1322, 1511, 568);
		// Kaisinel
		else if (destination.equalsIgnoreCase("Kaisinel"))
			goTo(admin, WorldMapType.KAISINEL.getId(), 2155, 1567, 1205);
		// Poeta
		else if (destination.equalsIgnoreCase("Poeta"))
			goTo(admin, WorldMapType.POETA.getId(), 806, 1242, 119);
		else if (destination.equalsIgnoreCase("Melponeh"))
			goTo(admin, WorldMapType.POETA.getId(), 426, 1740, 119);
		// Verteron
		else if (destination.equalsIgnoreCase("Verteron"))
			goTo(admin, WorldMapType.VERTERON.getId(), 1643, 1500, 119);
		else if (destination.equalsIgnoreCase("Cantas") || destination.equalsIgnoreCase("Cantas Coast"))
			goTo(admin, WorldMapType.VERTERON.getId(), 2384, 788, 102);
		else if (destination.equalsIgnoreCase("Ardus") || destination.equalsIgnoreCase("Ardus Shrine"))
			goTo(admin, WorldMapType.VERTERON.getId(), 2333, 1817, 193);
		else if (destination.equalsIgnoreCase("Pilgrims") || destination.equalsIgnoreCase("Pilgrims Respite"))
			goTo(admin, WorldMapType.VERTERON.getId(), 2063, 2412, 274);
		else if (destination.equalsIgnoreCase("Tolbas") || destination.equalsIgnoreCase("Tolbas Village"))
			goTo(admin, WorldMapType.VERTERON.getId(), 1291, 2206, 142);
		// Eltnen
		else if (destination.equalsIgnoreCase("Eltnen"))
			goTo(admin, WorldMapType.ELTNEN.getId(), 343, 2724, 264);
		else if (destination.equalsIgnoreCase("Golden") || destination.equalsIgnoreCase("Golden Bough Garrison"))
			goTo(admin, WorldMapType.ELTNEN.getId(), 688, 431, 332);
		else if (destination.equalsIgnoreCase("Eltnen Observatory"))
			goTo(admin, WorldMapType.ELTNEN.getId(), 1779, 883, 422);
		else if (destination.equalsIgnoreCase("Novan"))
			goTo(admin, WorldMapType.ELTNEN.getId(), 947, 2215, 252);
		else if (destination.equalsIgnoreCase("Agairon"))
			goTo(admin, WorldMapType.ELTNEN.getId(), 1921, 2045, 361);
		else if (destination.equalsIgnoreCase("Kuriullu"))
			goTo(admin, WorldMapType.ELTNEN.getId(), 2411, 2724, 361);
		// Theobomos
		else if (destination.equalsIgnoreCase("Theobomos"))
			goTo(admin, WorldMapType.THEOMOBOS.getId(), 1398, 1557, 31);
		else if (destination.equalsIgnoreCase("Jamanok") || destination.equalsIgnoreCase("Jamanok Inn"))
			goTo(admin, WorldMapType.THEOMOBOS.getId(), 458, 1257, 127);
		else if (destination.equalsIgnoreCase("Meniherk"))
			goTo(admin, WorldMapType.THEOMOBOS.getId(), 1396, 1560, 31);
		else if (destination.equalsIgnoreCase("obsvillage"))
			goTo(admin, WorldMapType.THEOMOBOS.getId(), 2234, 2284, 50);
		else if (destination.equalsIgnoreCase("Josnack"))
			goTo(admin, WorldMapType.THEOMOBOS.getId(), 901, 2774, 62);
		else if (destination.equalsIgnoreCase("Anangke"))
			goTo(admin, WorldMapType.THEOMOBOS.getId(), 2681, 847, 138);
		// Heiron
		else if (destination.equalsIgnoreCase("Heiron"))
			goTo(admin, WorldMapType.HEIRON.getId(), 2540, 343, 411);
		else if (destination.equalsIgnoreCase("Heiron Observatory"))
			goTo(admin, WorldMapType.HEIRON.getId(), 1423, 1334, 175);
		else if (destination.equalsIgnoreCase("Senemonea"))
			goTo(admin, WorldMapType.HEIRON.getId(), 971, 686, 135);
		else if (destination.equalsIgnoreCase("Jeiaparan"))
			goTo(admin, WorldMapType.HEIRON.getId(), 1635, 2693, 115);
		else if (destination.equalsIgnoreCase("Changarnerk"))
			goTo(admin, WorldMapType.HEIRON.getId(), 916, 2256, 157);
		else if (destination.equalsIgnoreCase("Kishar"))
			goTo(admin, WorldMapType.HEIRON.getId(), 1999, 1391, 118);
		else if (destination.equalsIgnoreCase("Arbolu"))
			goTo(admin, WorldMapType.HEIRON.getId(), 170, 1662, 120);
		
		/**
		 * Asmodae
		 */
		// Pandaemonium
		else if  (destination.equalsIgnoreCase("Pandaemonium") || destination.equalsIgnoreCase("Pandae"))
			goTo(admin, WorldMapType.PANDAEMONIUM.getId(), 1679, 1400, 195);
		// Marchutran
		else if (destination.equalsIgnoreCase("Marchutan"))
			goTo(admin, WorldMapType.MARCHUTAN.getId(), 1557, 1429, 266);
		// Ishalgen
		else if (destination.equalsIgnoreCase("Ishalgen"))
			goTo(admin, WorldMapType.ISHALGEN.getId(), 529, 2449, 281);
		else if (destination.equalsIgnoreCase("Anturoon"))
			goTo(admin, WorldMapType.ISHALGEN.getId(), 940, 1707, 259);
		// Altgard
		else if (destination.equalsIgnoreCase("Altgard"))
			goTo(admin, WorldMapType.ALTGARD.getId(), 1748, 1807, 254);
		else if (destination.equalsIgnoreCase("Basfelt"))
			goTo(admin, WorldMapType.ALTGARD.getId(), 1903, 696, 260);
		else if (destination.equalsIgnoreCase("Trader"))
			goTo(admin, WorldMapType.ALTGARD.getId(), 2680, 1024, 311);
		else if (destination.equalsIgnoreCase("Impetusiom"))
			goTo(admin, WorldMapType.ALTGARD.getId(), 2643, 1658, 324);
		else if (destination.equalsIgnoreCase("Altgard Observatory"))
			goTo(admin, WorldMapType.ALTGARD.getId(), 1468, 2560, 299);
		// Morheim
		else if (destination.equalsIgnoreCase("Morheim"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 308, 2274, 449);
		else if (destination.equalsIgnoreCase("Desert"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 634, 900, 360);
		else if (destination.equalsIgnoreCase("Slag"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 1772, 1662, 197);
		else if (destination.equalsIgnoreCase("Kellan"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 1070, 2486, 239);
		else if (destination.equalsIgnoreCase("Alsig"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 2387, 1742, 102);
		else if (destination.equalsIgnoreCase("Morheim Observatory"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 2794, 1122, 171);
		else if (destination.equalsIgnoreCase("Halabana"))
			goTo(admin, WorldMapType.MORHEIM.getId(), 2346, 2219, 127);
		// Brusthonin
		else if (destination.equalsIgnoreCase("Brusthonin"))
			goTo(admin, WorldMapType.BRUSTHONIN.getId(), 2917, 2421, 15);
		else if (destination.equalsIgnoreCase("Baltasar"))
			goTo(admin, WorldMapType.BRUSTHONIN.getId(), 1413, 2013, 51);
		else if (destination.equalsIgnoreCase("Bollu"))
			goTo(admin, WorldMapType.BRUSTHONIN.getId(), 840, 2016, 307);
		else if (destination.equalsIgnoreCase("Edge"))
			goTo(admin, WorldMapType.BRUSTHONIN.getId(), 1523, 374, 231);
		else if (destination.equalsIgnoreCase("Bubu"))
			goTo(admin, WorldMapType.BRUSTHONIN.getId(), 526, 848, 76);
		else if (destination.equalsIgnoreCase("Settlers"))
			goTo(admin, WorldMapType.BRUSTHONIN.getId(), 2917, 2417, 15);
		// Beluslan
		else if (destination.equalsIgnoreCase("Beluslan"))
			goTo(admin, WorldMapType.BELUSLAN.getId(), 398, 400, 222);
		else if (destination.equalsIgnoreCase("Besfer"))
			goTo(admin, WorldMapType.BELUSLAN.getId(), 533, 1866, 262);
		else if (destination.equalsIgnoreCase("Kidorun"))
			goTo(admin, WorldMapType.BELUSLAN.getId(), 1243, 819, 260);
		else if (destination.equalsIgnoreCase("Red Mane"))
			goTo(admin, WorldMapType.BELUSLAN.getId(), 2358, 1241, 470);
		else if (destination.equalsIgnoreCase("Kistenian"))
			goTo(admin, WorldMapType.BELUSLAN.getId(), 1942, 513, 412);
		else if (destination.equalsIgnoreCase("Hoarfrost"))
			goTo(admin, WorldMapType.BELUSLAN.getId(), 2431, 2063, 579);
		
		/**
		 * Balaurea
		 */
		// Inggison
		else if (destination.equalsIgnoreCase("Inggison"))
			goTo(admin, WorldMapType.INGGISON.getId(), 1335, 276, 590);
		else if (destination.equalsIgnoreCase("Ufob"))
			goTo(admin, WorldMapType.INGGISON.getId(), 382, 951, 460);
		else if (destination.equalsIgnoreCase("Soteria"))
			goTo(admin, WorldMapType.INGGISON.getId(), 2713, 1477, 382);
		else if (destination.equalsIgnoreCase("Hanarkand"))
			goTo(admin, WorldMapType.INGGISON.getId(), 1892, 1748, 327);
		else if (destination.equalsIgnoreCase("Dragon"))
			goTo(admin, WorldMapType.INGGISON.getId(), 1779, 2061, 319);
		else if (destination.equalsIgnoreCase("Autel"))
			goTo(admin, WorldMapType.INGGISON.getId(), 814, 1819, 331);
		// Gelkmaros
		else if (destination.equalsIgnoreCase("Gelkmaros"))
			goTo(admin, WorldMapType.GELKMAROS.getId(), 1763, 2911, 554);
		else if (destination.equalsIgnoreCase("Subterranea"))
			goTo(admin, WorldMapType.GELKMAROS.getId(), 2503, 2147, 464);
		else if (destination.equalsIgnoreCase("Rhonnam"))
			goTo(admin, WorldMapType.GELKMAROS.getId(), 845, 1737, 354);
		else if (destination.equalsIgnoreCase("Vorgaltem"))
			goTo(admin, WorldMapType.GELKMAROS.getId(), 1200, 985, 303);
		else if (destination.equalsIgnoreCase("Pourpre"))
			goTo(admin, WorldMapType.GELKMAROS.getId(), 1880, 1223, 321);
		// Silentera
		else if (destination.equalsIgnoreCase("Silentera"))
			goTo(admin, 600010000, 583, 767, 300);
		else if (destination.equalsIgnoreCase("Besh Entry") || destination.equalsIgnoreCase("Beshentry"))
			goTo(admin, 600010000, 1411, 767, 314);
		
		/**
		 * Abyss
		 */
		else if (destination.equalsIgnoreCase("Reshanta"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 951, 936, 1667);
		else if (destination.equalsIgnoreCase("Abyss 1") || destination.equalsIgnoreCase("Teminon"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 2867, 1034, 1528);
		else if (destination.equalsIgnoreCase("Abyss 2") || destination.equalsIgnoreCase("Primum"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 1078, 2839, 1636);
		else if (destination.equalsIgnoreCase("Abyss 3"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 1596, 2952, 2943);
		else if (destination.equalsIgnoreCase("Abyss 4"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 2054, 660, 2843);
		else if (destination.equalsIgnoreCase("Eye of Reshanta") || destination.equalsIgnoreCase("Eye"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 1979, 2114, 2291);
		else if (destination.equalsIgnoreCase("Divine Fortress") || destination.equalsIgnoreCase("Divine"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 2130, 1925, 2322);
		else if (destination.equalsIgnoreCase("Tigraki"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 587, 1159, 2837);
		else if (destination.equalsIgnoreCase("Nuage Noir"))
			goTo(admin, WorldMapType.RESHANTA.getId(), 3404, 2446, 2767);

		/**
		 * Instances
		 */
		else if (destination.equalsIgnoreCase("Haramel"))
			goTo(admin, 300200000, 176, 21, 144);
		else if (destination.equalsIgnoreCase("Nochsana") || destination.equalsIgnoreCase("NTC"))
			goTo(admin, 300030000, 513, 668, 331);
		else if (destination.equalsIgnoreCase("Arcanis") || destination.equalsIgnoreCase("Sky Temple of Arcanis"))
			goTo(admin, 320050000, 177, 229, 536);
		else if (destination.equalsIgnoreCase("Fire Temple") ||destination.equalsIgnoreCase("FT"))
			goTo(admin, 320100000, 144, 312, 123);
		else if (destination.equalsIgnoreCase("Kromede") || destination.equalsIgnoreCase("Kromede Trial"))
			goTo(admin, 300230000, 248, 244, 189);
		// Steel Rake
		else if (destination.equalsIgnoreCase("Steel Rake") || destination.equalsIgnoreCase("SR"))
			goTo(admin, 300100000, 237, 506, 948);
		else if (destination.equalsIgnoreCase("Steel Rake Lower") || destination.equalsIgnoreCase("SR Low"))
			goTo(admin, 300100000, 283, 453, 903);
		else if (destination.equalsIgnoreCase("Steel Rake Middle") || destination.equalsIgnoreCase("SR Mid"))
			goTo(admin, 300100000, 283, 453, 953);
		else if (destination.equalsIgnoreCase("Indratu") || destination.equalsIgnoreCase("Indratu Fortress"))
			goTo(admin, 310090000, 562, 335, 1015);
		else if (destination.equalsIgnoreCase("Azoturan") || destination.equalsIgnoreCase("Azoturan Fortress"))
			goTo(admin, 310100000, 458, 428, 1039);
		else if (destination.equalsIgnoreCase("Bio Lab") || destination.equalsIgnoreCase("Aetherogenetics Lab"))
			goTo(admin, 310050000, 225, 244, 133);
		else if (destination.equalsIgnoreCase("Adma") || destination.equalsIgnoreCase("Adma Stronghold"))
			goTo(admin, 320130000, 450, 200, 168);
		else if (destination.equalsIgnoreCase("Alquimia") || destination.equalsIgnoreCase("Alquimia Research Center"))
			goTo(admin, 320110000, 603, 527, 200);
		else if (destination.equalsIgnoreCase("Draupnir") || destination.equalsIgnoreCase("Draupnir Cave"))
			goTo(admin, 320080000, 491, 373, 622);
		else if (destination.equalsIgnoreCase("Theobomos Lab") || destination.equalsIgnoreCase("Theobomos Research Lab"))
			goTo(admin, 310110000, 477, 201, 170);
		else if (destination.equalsIgnoreCase("Dark Poeta") || destination.equalsIgnoreCase("DP"))
			goTo(admin, 300040000, 1214, 412, 140);
		// Lower Abyss
		else if (destination.equalsIgnoreCase("Sulfur") || destination.equalsIgnoreCase("Sulfur Tree Nest"))
			goTo(admin, 300060000, 462, 345, 163);
		else if (destination.equalsIgnoreCase("Right Wing") || destination.equalsIgnoreCase("Right Wing Chamber"))
			goTo(admin, 300090000, 263, 386, 103);
		else if (destination.equalsIgnoreCase("Left Wing") || destination.equalsIgnoreCase("Left Wing Chamber"))
			goTo(admin, 300080000, 672, 606, 321);
		// Upper Abyss
		else if (destination.equalsIgnoreCase("Asteria Chamber"))
			goTo(admin, 300050000, 469, 568, 202);
		else if (destination.equalsIgnoreCase("Miren Chamber"))
			goTo(admin, 300130000, 527, 120, 176);
		else if (destination.equalsIgnoreCase("Kysis Chamber"))
			goTo(admin, 300120000, 528, 121, 176);
		else if (destination.equalsIgnoreCase("Krotan Chamber"))
			goTo(admin, 300140000, 528, 109, 176);
		else if (destination.equalsIgnoreCase("Roah Chamber"))
			goTo(admin, 300070000, 504, 396, 94);
		// Divine
		else if (destination.equalsIgnoreCase("Abyssal Splinter") || destination.equalsIgnoreCase("Core"))
			goTo(admin, 300220000, 704, 153, 453);
		else if (destination.equalsIgnoreCase("Dredgion"))
			goTo(admin, 300110000, 414, 193, 431);
		else if (destination.equalsIgnoreCase("Chantra") || destination.equalsIgnoreCase("Chantra Dredgion"))
			goTo(admin, 300210000, 414, 193, 431);
		else if (destination.equalsIgnoreCase("Taloc") || destination.equalsIgnoreCase("Taloc's Hollow"))
			goTo(admin, 300190000, 200, 214, 1099);
		// Udas
		else if (destination.equalsIgnoreCase("Udas") || destination.equalsIgnoreCase("Udas Temple"))
			goTo(admin, 300150000, 637, 657, 134);
		else if (destination.equalsIgnoreCase("Udas Lower") || destination.equalsIgnoreCase("Udas Lower Temple"))
			goTo(admin, 300160000, 1146, 277, 116);
		else if (destination.equalsIgnoreCase("Beshmundir") || destination.equalsIgnoreCase("BT") || destination.equalsIgnoreCase("Beshmundir Temple") || destination.equalsIgnoreCase("Besh"))
			goTo(admin, 300170000, 1477, 237, 243);
		// Padmarashka Cave
		else if (destination.equalsIgnoreCase("Padmarashka Cave") || destination.equalsIgnoreCase("Padmarashka") || destination.equalsIgnoreCase("Padma"))
			goTo(admin, 320150000, 385, 506, 66);
		
		/**
		 * Quest Instance Maps
		 */
		// TODO : Changer id maps
		else if (destination.equalsIgnoreCase("Karamatis 0"))
			goTo(admin, 310010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Karamatis 1"))
			goTo(admin, 310020000, 312, 274, 206);
		else if (destination.equalsIgnoreCase("Karamatis 2"))
			goTo(admin, 310120000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Aerdina"))
			goTo(admin, 310030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("Gerania"))
			goTo(admin, 310040000, 275, 168, 205);
		// Stigma quest
		else if (destination.equalsIgnoreCase("Sliver") || destination.equalsIgnoreCase("Sliver of Darkness"))
			goTo(admin, 310070000, 247, 249, 1392);
		else if (destination.equalsIgnoreCase("Space") || destination.equalsIgnoreCase("Space of Destiny"))
			goTo(admin, 320070000, 246, 246, 125);
		else if (destination.equalsIgnoreCase("Ataxiar 1"))
			goTo(admin, 320010000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Ataxiar 2"))
			goTo(admin, 320020000, 221, 250, 206);
		else if (destination.equalsIgnoreCase("Bregirun"))
			goTo(admin, 320030000, 275, 168, 205);
		else if (destination.equalsIgnoreCase("Nidalber"))
			goTo(admin, 320040000, 275, 168, 205);
		
		/**
		 * Arenas
		 */
		else if (destination.equalsIgnoreCase("Sanctum Arena"))
			goTo(admin, 310080000, 275, 242, 159);
		else if (destination.equalsIgnoreCase("Triniel Arena"))
			goTo(admin, 320090000, 275, 239, 159);
		// Empyrean Crucible
		else if (destination.equalsIgnoreCase("Crucible 1-0"))
			goTo(admin, 300300000, 380, 350, 95);
		else if (destination.equalsIgnoreCase("Crucible 1-1"))
			goTo(admin, 300300000, 346, 350, 96);
		else if (destination.equalsIgnoreCase("Crucible 5-0"))
			goTo(admin, 300300000, 1265, 821, 359);
		else if (destination.equalsIgnoreCase("Crucible 5-1"))
			goTo(admin, 300300000, 1256, 797, 359);
		else if (destination.equalsIgnoreCase("Crucible 6-0"))
			goTo(admin, 300300000, 1596, 150, 129);
		else if (destination.equalsIgnoreCase("Crucible 6-1"))
			goTo(admin, 300300000, 1628, 155, 126);
		else if (destination.equalsIgnoreCase("Crucible 7-0"))
			goTo(admin, 300300000, 1813, 797, 470);
		else if (destination.equalsIgnoreCase("Crucible 7-1"))
			goTo(admin, 300300000, 1785, 797, 470);
		else if (destination.equalsIgnoreCase("Crucible 8-0"))
			goTo(admin, 300300000, 1776, 1728, 304);
		else if (destination.equalsIgnoreCase("Crucible 8-1"))
			goTo(admin, 300300000, 1776, 1760, 304);
		else if (destination.equalsIgnoreCase("Crucible 9-0"))
			goTo(admin, 300300000, 1357, 1748, 320);
		else if (destination.equalsIgnoreCase("Crucible 9-1"))
			goTo(admin, 300300000, 1334, 1741, 316);
		else if (destination.equalsIgnoreCase("Crucible 10-0"))
			goTo(admin, 300300000, 1750, 1255, 395);
		else if (destination.equalsIgnoreCase("Crucible 10-1"))
			goTo(admin, 300300000, 1761, 1280, 395);
		// Arena Of Chaos
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 1"))
			goTo(admin, 300350000, 1332, 1078, 340);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 2"))
			goTo(admin, 300350000, 599, 1854, 227);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 3"))
			goTo(admin, 300350000, 663, 265, 512);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 4"))
			goTo(admin, 300350000, 1840, 1730, 302);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 5"))
			goTo(admin, 300350000, 1932, 1228, 270);
		else if (destination.equalsIgnoreCase("Arena Of Chaos - 6"))
			goTo(admin, 300350000, 1949, 946, 224);

		/**
		 * Miscellaneous
		 */
		// Prison
		else if (destination.equalsIgnoreCase("Prison LF") || destination.equalsIgnoreCase("Prison Elyos"))
			goTo(admin, 510010000, 256, 256, 49);
		else if (destination.equalsIgnoreCase("Prison DF") || destination.equalsIgnoreCase("Prison Asmos"))
			goTo(admin, 520010000, 256, 256, 49);
		// Test
		/*
		else if (destination.equalsIgnoreCase("Test Dungeon"))
			goTo(admin, 300020000, 104, 66, 25);
		*/
		else if (destination.equalsIgnoreCase("Test Basic"))
			goTo(admin, 900020000, 144, 136, 20);
		else if (destination.equalsIgnoreCase("Test Server"))
			goTo(admin, 900030000, 228, 171, 49);
		else if (destination.equalsIgnoreCase("Test GiantMonster"))
			goTo(admin, 900100000, 196, 187, 20);
		// Unknown
		else if (destination.equalsIgnoreCase("IDAbPro"))
			goTo(admin, 300010000, 270, 200, 206);
		// GamezNetwork GM zone
		else if (destination.equalsIgnoreCase("gm"))
			goTo(admin, 120020000, 1442, 1133, 302);
		
		/**
		 * 2.5 Maps
		 */
		else if (destination.equalsIgnoreCase("Kaisinel Academy"))
			goTo(admin, 110070000, 459, 251, 128);
		else if (destination.equalsIgnoreCase("Marchutan Priory"))
			goTo(admin, 120080000, 577, 250, 94);
		else if (destination.equalsIgnoreCase("Esoterrace") || destination.equalsIgnoreCase("Esoterrasse") || destination.equalsIgnoreCase("Eso"))
			goTo(admin, 300250000, 333, 437, 326);
              		
		else
			PacketSendUtility.sendMessage(admin, "Could not find the specified destination !");
	}
	
	private static void goTo(final Player admin, int worldId, float x, float y, float z) {
		WorldMap destinationMap = World.getInstance().getWorldMap(worldId);
		if (destinationMap.isInstanceType())
			TeleportService.teleportTo(admin, worldId, getInstanceId(worldId, admin), x, y, z, 0 ,true);
		else
			TeleportService.teleportTo(admin, worldId, x, y, z, 0, true);
	}
	
	private static int getInstanceId(int worldId, Player admin) {
		if (admin.getWorldId() == worldId)	{
			WorldMapInstance registeredInstance = InstanceService.getRegisteredInstance(worldId, admin.getObjectId());
			if (registeredInstance != null)
				return registeredInstance.getInstanceId();
		}
		WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(worldId);
		InstanceService.registerPlayerWithInstance(newInstance, admin);
		return newInstance.getInstanceId();
	}
	
}