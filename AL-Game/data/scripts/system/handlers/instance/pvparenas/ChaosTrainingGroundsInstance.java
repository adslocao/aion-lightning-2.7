/*
 * This file is part of aion-lightning <aion-lightning.org>
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package instance.pvparenas;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import javolution.util.FastMap;

/**
 *
 * @author xTz
 */
@InstanceID(300420000)
public class ChaosTrainingGroundsInstance extends PvPArenaInstance {
	private static int flamme = 45;
	private static int mumu = 45;
	private static List<Point3D> mumuRakeAvalable = new ArrayList<Point3D>();
	private static FastMap<Integer, Point3D> mumuRakeLocked = new FastMap<Integer, Point3D>();
	private static List<Point3D> casusManorAvalable = new ArrayList<Point3D>();
	private static FastMap<Integer, Point3D> casusManorLocked = new FastMap<Integer, Point3D>();
	
	static {
		mumuRakeAvalable.add(new Point3D(1352.883f, 1057.614f, 337.265));
		mumuRakeAvalable.add(new Point3D(1314.494f, 1093.471f, 337.500));
		mumuRakeAvalable.add(new Point3D(1344.862f, 1094.798f, 337.625));
		mumuRakeAvalable.add(new Point3D(1313.593f, 1056.350f, 337.400));
		mumuRakeAvalable.add(new Point3D(1345.415f, 1060.410f, 337.154));
		mumuRakeAvalable.add(new Point3D(1349.373f, 1096.931f, 337.625));
		mumuRakeAvalable.add(new Point3D(1347.901f, 1054.008f, 337.369));
		mumuRakeAvalable.add(new Point3D(1352.471f, 1090.433f, 337.625));
		mumuRakeAvalable.add(new Point3D(1314.830f, 1098.899f, 337.500));

		casusManorAvalable.add(new Point3D(1916.140f, 963.972f, 230.412f));
		casusManorAvalable.add(new Point3D(1970.082f, 973.124f, 230.483f));
		casusManorAvalable.add(new Point3D(1966.370f, 946.332f, 230.350f));
		casusManorAvalable.add(new Point3D(1932.114f, 946.354f, 230.350f));
		casusManorAvalable.add(new Point3D(1950.780f, 946.527f, 223.827f));
		casusManorAvalable.add(new Point3D(1947.697f, 944.159f, 223.827f));
		casusManorAvalable.add(new Point3D(1947.761f, 941.574f, 223.827f));
		casusManorAvalable.add(new Point3D(1950.892f, 950.601f, 223.827f));
		casusManorAvalable.add(new Point3D(1997.199f, 915.206f, 230.543f));
		casusManorAvalable.add(new Point3D(1899.863f, 978.339f, 230.542f));
		casusManorAvalable.add(new Point3D(1908.593f, 1000.789f, 230.542f));
		casusManorAvalable.add(new Point3D(1956.887f, 927.213f, 222.562f));
		casusManorAvalable.add(new Point3D(1949.627f, 964.211f, 222.562f));
		casusManorAvalable.add(new Point3D(1942.479f, 964.353f, 222.562f));
		casusManorAvalable.add(new Point3D(1956.889f, 964.104f, 222.562f));
		casusManorAvalable.add(new Point3D(1942.478f, 927.320f, 222.562f));
		casusManorAvalable.add(new Point3D(1949.626f, 927.296f, 222.562f));
		casusManorAvalable.add(new Point3D(1926.180f, 927.003f, 230.344f));
		casusManorAvalable.add(new Point3D(1991.136f, 898.990f, 230.543f));
		casusManorAvalable.add(new Point3D(2001.787f, 909.296f, 230.543f));
		casusManorAvalable.add(new Point3D(1989.953f, 928.174f, 230.392f));
		casusManorAvalable.add(new Point3D(1896.031f, 985.247f, 230.569f));
		casusManorAvalable.add(new Point3D(1922.002f, 963.132f, 230.377f));
		casusManorAvalable.add(new Point3D(1976.382f, 963.869f, 230.338f));
		casusManorAvalable.add(new Point3D(1908.240f, 994.783f, 230.542f));
		casusManorAvalable.add(new Point3D(1970.003f, 968.644f, 230.338f));
		casusManorAvalable.add(new Point3D(1922.539f, 932.241f, 230.338f));
		casusManorAvalable.add(new Point3D(1903.003f, 963.648f, 230.338f));
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		killBonus = 1000;
		deathFine = -125;
		super.onInstanceCreate(instance);
	}

	protected void spawnOnStart(){
		spawnRings();
		spawnRelicsPlazza(0);
		spawnRelicsFlamme(0);
		spawnRelicsIllusion(0);
		spawnPlazzaFlamme(0, null);
		spawnMumu(0, 3);
		spawnMau(0);
		spawnCasus(0, 7);
		spawnGather(0, null);
	}
	
	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		int timeRnd = Rnd.get(0, 10) - 5;

		// No super on plazza flamme thrower, point handle int IA mob
		if (npc.getNpcId() == 701169 || npc.getNpcId() == 701170 || npc.getNpcId() == 701171 || npc.getNpcId() == 701172) {
			spawnPlazzaFlamme((flamme + timeRnd) * 1000, npc);
			return;
		}

		super.handleUseItemFinish(player, npc);
		if (npc.getNpcId() == 701174 || npc.getNpcId() == 701173) {
			spawnRelicsFlamme((relicTime + timeRnd) * 1000);
		}
		
		if (npc.getNpcId() == 701187 || npc.getNpcId() == 701188) {
			spawnRelicsPlazza((relicTime + timeRnd) * 1000);
		}
		
		if (npc.getNpcId() == 701318) {
			spawnRelicsIllusion((relicTime + timeRnd) * 1000);
		}
	}
	
	@Override
	public void onDie(Npc npc) {
		super.onDie(npc);
		int timeRnd = Rnd.get(0, 10) - 5;

		// Mumu rake
		if(npc.getNpcId() == 218695){
			spawnMumu((mumu + timeRnd)*1000, 1);
			mumuRakeAvalable.add(mumuRakeLocked.remove(npc.getObjectId()));
		}
		// Mau
		if(npc.getNpcId() == 218696){
			spawnMau((mumu + timeRnd)*1000);
		}
		// Casus Manor
		if(npc.getNpcId() == 218699 || npc.getNpcId() == 218698 || npc.getNpcId() == 218702 || npc.getNpcId() == 218700 || npc.getNpcId() == 218688){
			spawnCasus((mumu + timeRnd)*1000, 1);
			casusManorAvalable.add(casusManorLocked.remove(npc.getObjectId()));
		}
		npc.getController().delete();
	}

	private void spawnMumu(int time, final int j) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				for(int i=0; i<j; i++){
					Point3D point = mumuRakeAvalable.remove(Rnd.get(0, mumuRakeAvalable.size()-1));
					VisibleObject npc = spawn(218695, (float)point.x, (float)point.y, (float)point.z, (byte) 0);
					mumuRakeLocked.put(npc.getObjectId(), point);
				}
			}

		}, time);
	}

	private void spawnCasus(int time, final int j) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				for(int i=0; i<j; i++){
					int npcId = 218699;
					int rnd = Rnd.get(0, 4);
					if(rnd == 1){
						npcId = 218698;
					}
					if(rnd == 2){
						npcId = 218702;
					}
					if(rnd == 3){
						npcId = 218700;
					}
					if(rnd == 4){
						npcId = 218688;
					}
					Point3D point = casusManorAvalable.remove(Rnd.get(0, casusManorAvalable.size()-1));
					VisibleObject npc = spawn(npcId, (float)point.x, (float)point.y, (float)point.z, (byte) 0);
					casusManorLocked.put(npc.getObjectId(), point);
				}
			}

		}, time);
	}

	@Override
	public void onGather(Player player, Gatherable gatherable) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		getPlayerReward(player).addPoints(1250);
		sendPacket();
		int nameId = gatherable.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400237, name, 1250));
		int timeRnd = Rnd.get(0, 10) - 5;
		spawnGather((40 + timeRnd)*1000, gatherable);
	}
	
	private void spawnPlazzaFlamme(int time, final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				if (npc == null || npc.getNpcId() == 701169) {
					spawn(701169, 1803.476f, 1716.913f, 311.239f, (byte) 68);
				}
				if (npc == null || npc.getNpcId() == 701170) {
					spawn(701170, 1826.590f, 1773.080f, 311.185f, (byte) 38);
				}
				if (npc == null || npc.getNpcId() == 701171) {
					spawn(701171, 1859.583f, 1693.732f, 11.185f, (byte) 96);
				}
				if (npc == null || npc.getNpcId() == 701172) {
					spawn(701172, 1882.873f, 1749.328f, 311.195f, (byte) 6);
				}
			}

		}, time);
	}
	
	private void spawnRelicsPlazza(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				spawn(Rnd.get(1, 2) == 1 ? 701187 : 701188, 1841.951f, 1733.968f, 300.242f, (byte) 0);
			}

		}, time);
	}
	
	private void spawnRelicsFlamme(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				if(Rnd.get(1, 2) == 1){
					spawn(Rnd.get(1, 2) == 1 ? 701174 : 701173, 674.517f, 1778.428f, 204.693f, (byte) 0); // Position 1
				}else{
					spawn(Rnd.get(1, 2) == 1 ? 701174 : 701173, 663.49f, 1756.89f, 145.24f, (byte) 0); // Position 2
				}
			}

		}, time);
	}
	
	private void spawnGather(int time, final Gatherable npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				if(npc == null){
					spawn(Rnd.get(1, 2) == 1 ? 405000 : 405001, 1349.522f, 1057.374f, 337.375f, (byte) 0);
					spawn(Rnd.get(1, 2) == 1 ? 405000 : 405001, 1316.816f, 1097.861f, 337.500f, (byte) 0);
					spawn(Rnd.get(1, 2) == 1 ? 405000 : 405001, 1349.552f, 1093.443f, 337.625f, (byte) 0);
					spawn(Rnd.get(1, 2) == 1 ? 405000 : 405001, 1317.240f, 1058.067f, 337.375f, (byte) 0);
					return;
				}
				
				spawn(Rnd.get(1, 2) == 1 ? 405000 : 405001, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
			}

		}, time);
	}
	
	private void spawnMau(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				if(Rnd.get(1, 2) == 1){
					spawn(218696, 1366.265f, 1077.497f, 339.479f, (byte) 0); // Position 1
				}else{
					spawn(218696, 1328.881f, 1039.994f, 339.766f, (byte) 0); // Position 2
				}
			}

		}, time);
	}
	
	private void spawnRelicsIllusion(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (isInstanceDestroyed || instanceReward.isRewarded()) {
					return;
				}
				spawn(701318, 663.554f, 266.000f, 512.847f, (byte) 0);
			}

		}, time);
	}

	protected void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("PVP_ARENA_1", mapId,
				new Point3D(674.66974, 1792.8499, 149.77501),
				new Point3D(674.66974, 1792.8499, 155.77501),
				new Point3D(678.83636, 1788.5325, 149.77501), 6), instanceId);
		f1.spawn();
		FlyRing f2 = new FlyRing(new FlyRingTemplate("PVP_ARENA_2", mapId,
				new Point3D(688.30615, 1769.7937, 149.88556),
				new Point3D(688.30615, 1769.7937, 155.88556),
				new Point3D(689.42096, 1763.8982, 149.88556), 6), instanceId);
		f2.spawn();
		FlyRing f3 = new FlyRing(new FlyRingTemplate("PVP_ARENA_3", mapId,
				new Point3D(664.2252, 1761.671, 170.95732),
				new Point3D(664.2252, 1761.671, 176.95732),
				new Point3D(669.2843, 1764.8967, 170.95732), 6), instanceId);
		f3.spawn();
		FlyRing fv1 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_1", mapId,
				new Point3D(690.28625, 1753.8561, 192.07726),
				new Point3D(690.28625, 1753.8561, 198.07726),
				new Point3D(689.4365, 1747.9165, 192.07726), 6), instanceId);
		fv1.spawn();
		FlyRing fv2 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_2", mapId,
				new Point3D(690.1935, 1797.0029, 203.79236),
				new Point3D(690.1935, 1797.0029, 209.79236),
				new Point3D(692.8295, 1802.3928, 203.79236), 6), instanceId);
		fv2.spawn();
		FlyRing fv3 = new FlyRing(new FlyRingTemplate("PVP_ARENA_VOID_3", mapId,
				new Point3D(659.2784, 1766.0273, 207.25465),
				new Point3D(659.2784, 1766.0273, 213.25465),
				new Point3D(665.2619, 1766.4718, 207.25465), 6), instanceId);
		fv3.spawn();
	}

	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		PvPArenaPlayerReward playerReward = getPlayerReward(player);
		if (playerReward == null || !instanceReward.isStartProgress()) {
			return false;
		}
		Npc npc;
		if (flyingRing.equals("PVP_ARENA_1")) {
			npc = getNpc(674.841f, 1793.065f, 150.964f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_2")) {
			npc = getNpc(688.410f, 1769.611f, 150.964f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				playerReward.addPoints(250);
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_3")) {
			npc = getNpc(664.160f, 1761.933f, 171.504f);
			if (npc != null && npc.isSpawned()) {
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				playerReward.addPoints(250);
				sendSystemMsg(player, npc, 250);
				sendPacket();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_VOID_1")) {
			npc = getNpc(693.061f, 1752.479f, 186.750f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_VOID_2")) {
			npc = getNpc(688.061f, 1798.229f, 198.500f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		else if (flyingRing.equals("PVP_ARENA_VOID_3")) {
			npc = getNpc(659.311f, 1768.979f, 201.500f);
			if (npc != null && npc.isSpawned()) {
				useSkill(npc, player, 20059, 1);
				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
			}
		}
		return false;
	}

}