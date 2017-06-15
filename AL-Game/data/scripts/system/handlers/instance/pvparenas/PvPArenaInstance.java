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

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.controllers.observer.PvpArenaObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.PvPArenaReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.model.templates.portal.EntryPoint;
import com.aionemu.gameserver.model.templates.portal.PortalTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.AutoGroupService2;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastMap;

/**
 *
 * @author xTz
 */
public class PvPArenaInstance extends GeneralInstanceHandler {

	protected boolean isInstanceDestroyed;
	protected PvPArenaReward instanceReward;
	protected int killBonus;
	protected int relicTime = 40;
	protected int deathFine;
	protected FastMap<Integer, Integer> ticketRemoved = new FastMap<Integer, Integer>(); 
	protected FastMap<Integer, PvpArenaObserver> observed = new FastMap<Integer, PvpArenaObserver>().shared();
	//protected FastMap<Integer, PvparenaSkillObserver> observedSkills = new FastMap<Integer, PvparenaSkillObserver>().shared();
	protected static final Logger log = LoggerFactory.getLogger(PvPArenaInstance.class);

	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		// PvPArenaPlayerReward ownerReward = getPlayerReward(player);
		/*
		 * ownerReward.endBoostMoraleEffect(player);
		 * ownerReward.applyBoostMoraleEffect(player);
		 */
		sendPacket();
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
				lastAttacker == null || player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));

		final Player winner = player.getAggroList().getMostPlayerDamage();

		if (winner != null) {
			PvPArenaPlayerReward reward = getPlayerReward(winner);
			reward.addPvPKillToPlayer();
			/*
			 * reward.endBoostMoraleEffect(winner);
			 * reward.applyBoostMoraleEffect(winner);
			 */

			// notify Kill-Quests
			int worldId = winner.getWorldId();
			QuestEngine.getInstance().onKillInWorld(new QuestEnv(player, winner, 0, 0), worldId);
		}

		updatePoints(player);

		return true;
	}

	private void updatePoints(Creature victim) {

		if (!instanceReward.isStartProgress()) {
			return;
		}

		int bonus = 0;
		int rank = 0;

		// Decrease victim points
		if (victim instanceof Player) {
			PvPArenaPlayerReward victimFine = getPlayerReward((Player) victim);
			victimFine.addPoints(deathFine);
			bonus = killBonus;
			rank = instanceReward.getRank(victimFine.getPoints());
		} else
			bonus = getNpcBonus(((Npc) victim).getNpcId());

		if (bonus == 0) {
			return;
		}

		// Reward all damagers
		for (AggroInfo damager : victim.getAggroList().getList()) {
			if (!(damager.getAttacker() instanceof Creature)) {
				continue;
			}
			Creature master = ((Creature) damager.getAttacker()).getMaster();
			if (master == null) {
				continue;
			}
			if (master instanceof Player) {
				Player attaker = (Player) master;
				int rewardPoints = 0;
				if(victim.getAggroList().getTotalDamage() != 0){
					rewardPoints = (victim instanceof Player && instanceReward.getRound() == 3 && rank == 0 ? bonus * 3
							: bonus) * damager.getDamage() / victim.getAggroList().getTotalDamage();
				}
				getPlayerReward(attaker).addPoints(rewardPoints);
				sendSystemMsg(attaker, victim, rewardPoints);
			}
		}
		if (instanceReward.hasCapPoints()) {
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
		}
		sendPacket();
	}

	@Override
	public void sendSystemMsg(Player player, Creature creature, int rewardPoints) {
		int nameId = creature.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player,
				new SM_SYSTEM_MESSAGE(1400237, nameId == 0 ? creature.getName() : name, rewardPoints));
	}

	@Override
	public void onDie(Npc npc) {
		if (npc.getAggroList().getMostPlayerDamage() == null) {
			return;
		}
		updatePoints(npc);
	}

	@Override
	public void onEnterInstance(Player player) {
		// Integer object = player.getObjectId();
		if (!containPlayer(player)) {
			instanceReward.regPlayerReward(player);
			// getPlayerReward(object).applyBoostMoraleEffect(player);
			instanceReward.setRndPosition(player);
		} else {
			instanceReward.portToPosition(player);
		}
		// Observer to kill player falling of the bridge
		PvpArenaObserver observer = new PvpArenaObserver(player, instanceReward, false);
		player.getObserveController().addObserver(observer);
		observed.put(player.getObjectId(), observer);

		sendPacket();
	}

	protected void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}

		});
	}

	private int getNpcBonus(int npcId) {
		switch (npcId) {
		case 701187: // Blessed Relics
		case 701188: // Cursed Relics
		case 701173: // Blessed Relics
		case 701174: // Cursed Relics
			return 1750;
		case 218690: // Pale Carmina
		case 218703: // Pale Carmina
		case 218716: // Pale Carmina
		case 218691: // Corrupt Casus
		case 218704: // Corrupt Casus
		case 218717: // Corrupt Casus
			return 1500;
		case 218682: // MuMu Rake Gatherer
		case 218695: // MuMu Rake Gatherer
		case 218708: // MuMu Rake Gatherer
			return 1250;
		case 218685: // Casus Manor Guard
		case 218698: // Casus Manor Guard
		case 218711: // Casus Manor Guard
		case 218687: // Casus Manor Maid
		case 218700: // Casus Manor Maid
		case 218713: // Casus Manor Maid
		case 218686: // Casus Manor Maidservant
		case 218699: // Casus Manor Maidservant
		case 218712: // Casus Manor Maidservant
			return 250;
		case 701172: // Plaza Flame Thrower
		case 701171: // Plaza Flame Thrower
		case 701170: // Plaza Flame Thrower
		case 701169: // Plaza Flame Thrower
		case 218806: // Casus Manor Chief Maid
		case 218807: // Casus Manor Chief Maid
		case 218808: // Casus Manor Chief Maid
		case 701317: // Illusion of hope
		case 701318: // Illusion of hope
		case 701319: // Illusion of hope
			return 500;
		case 218701: // Casus Manor Butler
		case 218688: // Casus Manor Butler
		case 218714: // Casus Manor Butler
			return 650;
		case 701181: // Cursed Relics
		case 701195: // Cursed Relics
		case 701209: // Cursed Relics
		case 218689: // Casus Manor Noble
		case 218702: // Casus Manor Noble
		case 218715: // Casus Manor Noble
			return 750;
		case 218683: // Black Claw Scratcher
		case 218696: // Black Claw Scratcher
		case 218709: // Black Claw Scratcher
		case 218684: // Mutated Drakan Fighter
		case 218697: // Mutated Drakan Fighter
		case 218710: // Mutated Drakan Fighter
		case 218693: // Red Sand Tog
		case 218706: // Red Sand Tog
		case 218705: // Red Sand brax
		case 218719: // Red Sand Tog
		case 701215: // Blesed Relic
		case 701220: // Blesed Relic
		case 701225: // Blesed Relic
		case 701216: // Blesed Relic
		case 701221: // Blesed Relic
		case 701226: // Blesed Relic
			return 100;
		default:
			return 0;
		}
	}

	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}

	@Override
	public void onPlayerLogOut(Player player) {
		PacketSendUtility.sendMessage(player, "onPlayerLogOut "+player.getName());
		checkStarted(player);
		getPlayerReward(player).updateLogOutTime();
	}

	@Override
	public void onPlayerLogin(Player player) {
		getPlayerReward(player).updateBonusTime();
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new PvPArenaReward(mapId, instanceId, instance);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		instanceReward.setInstanceStartTime();
		
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				// start round 1
				if (!isInstanceDestroyed && !instanceReward.isRewarded() && canStart()) {
					spawnOnStart();
					openDoors();
					sendPacket(new SM_SYSTEM_MESSAGE(1401058));
					instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
					sendPacket();
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							// start round 2
							canContinue();
							if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
								instanceReward.setRound(2);
								instanceReward.setRndZone();
								sendPacket();
								changeZone();
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										// start round 3
										canContinue();
										if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
											instanceReward.setRound(3);
											instanceReward.setRndZone();
											sendPacket(new SM_SYSTEM_MESSAGE(1401203));
											sendPacket();
											changeZone();
											ThreadPoolManager.getInstance().schedule(new Runnable() {
												@Override
												public void run() {
													// end
													if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
														instanceReward
																.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
														reward();
														sendPacket();
													}
												}

											}, 180000);
										}
									}

								}, 180000);
							}
						}

					}, 180000);
				}
			}

		}, 120000);
	}

	protected boolean canStart() {
		for (Player p : instance.getPlayersInside()) {
			PvpArenaObserver observer = observed.get(p.getObjectId());
			observer.setStarted(true);
		}

		if (instance.getPlayersInside().size() < 2) {
			sendPacket(new SM_SYSTEM_MESSAGE(1401303));
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			// reward();
			sendPacket();
			return false;
		}

		return true;
	}

	protected void canContinue() {
		if (instance.getPlayersInside().size() < 2) {
			sendPacket(new SM_SYSTEM_MESSAGE(1401303));
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
			sendPacket();
		}
	}

	@Override
	public void onExitInstance(Player player) {
		PortalTemplate portal = DataManager.PORTAL_DATA.getInstancePortalTemplate(mapId, player.getRace());
		EntryPoint entryPoint = TeleportService.getEntryPointByRace(portal, player.getRace());
		TeleportService.teleportTo(player, entryPoint.getMapId(), 1, entryPoint.getX(), entryPoint.getY(),
				entryPoint.getZ(), 3000, true);
	}

	private void openDoors() {
		for (StaticDoor door : instance.getDoors().values()) {
			if (door != null) {
				door.setOpen(true);
			}
		}
	}

	private boolean containPlayer(Player player) {
		return instanceReward.containPlayer(player);
	}

	protected PvPArenaPlayerReward getPlayerReward(Player player) {
		instanceReward.regPlayerReward(player);
		return (PvPArenaPlayerReward) instanceReward.getPlayerReward(player);
	}

	@Override
	public boolean onReviveEvent(Player player) {
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PlayerReviveService.revive(player, 100, 100, false);
		player.getGameStats().updateStatsAndSpeedVisually();
		if (!isInstanceDestroyed) {
			// Observer to kill player falling of the bridge
			PvpArenaObserver observer = new PvpArenaObserver(player, instanceReward, true);
			player.getObserveController().addObserver(observer);
			observed.put(player.getObjectId(), observer);

			player.unsetState(CreatureState.FLYING);
			player.unsetState(CreatureState.GLIDING);
			player.setFlyState(0);
			instanceReward.portToPosition(player);
		}
		return true;
	}

	@Override
	public void onLeaveInstance(Player player) {
		PvpArenaObserver observer = observed.remove(player.getObjectId());
		player.getObserveController().removeObserver(observer);

		clearDebuffs(player);
		PvPArenaPlayerReward playerReward = getPlayerReward(player);
		checkStarted(player);
		if (playerReward != null) {
			// playerReward.endBoostMoraleEffect(player);
			instanceReward.clearPosition(playerReward.getPosition(), Boolean.FALSE);
			instanceReward.removePlayerReward(playerReward);
		}

		// TODO STOP ARENA IF 1 player left
		/*if (isArenaStarted()) {
			log.info("continue !!!");
			canContinue();
		} else {
			log.info("start !!!");
			canStart();
		}*/
	}

	protected void checkStarted(Player player) {
		if (ticketRemoved.get(player.getObjectId()) != null || !isPvpArena() || instanceReward.isRewarded()) {
			return;
		}
		if (isArenaStarted()) {
			return;
		}

		long size = 1;
		int itemId = getIdEnteringItem();

		List<Item> items = player.getInventory().getItemsByItemId(itemId);
		items = sort(items, on(Item.class).getExpireTime());
		for (Item item : items) {
			ticketRemoved.put(player.getObjectId(), 1);
			player.getInventory().decreaseItemCount(item, size);
			break;
		}
	}

	private boolean isArenaStarted() {
		for (StaticDoor door : instance.getDoors().values()) {
			if (door != null && door.isOpen()) {
				return true;
			}
		}
		return false;
	}

	protected int getIdEnteringItem() {
		return 0;
	}

	private void clearDebuffs(Player player) {
		for (Effect ef : player.getEffectController().getAbnormalEffects()) {
			DispelCategoryType category = ef.getSkillTemplate().getDispelCategory();
			if (category == DispelCategoryType.DEBUFF_MENTAL || category == DispelCategoryType.DEBUFF_PHYSICAL
					|| category == DispelCategoryType.ALL) {
				ef.endEffect();
				player.getEffectController().clearEffect(ef);
			}
		}
	}

	@Override
	public void sendPacket() {
		instanceReward.sendPacket();
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		instanceReward.clear();
	}

	private void changeZone() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player : instance.getPlayersInside()) {
					instanceReward.portToPosition(player);
				}
				sendPacket();
			}

		}, 2000);
	}

	protected void reward() {
		for (Player player : instance.getPlayersInside()) {
			/*if (player.getLifeStats().isAlreadyDead())
				PlayerReviveService.duelRevive(player);*/
			PvPArenaPlayerReward reward = getPlayerReward(player);
			if (!reward.isRewarded()) {
				reward.setRewarded();

				int totalAp = reward.getBasicAP() + reward.getRankingAP() + reward.getScoreAP();
				AbyssPointsService.addAp(player, totalAp);

				int courage = reward.getBasicCourage() + reward.getRankingCourage() + reward.getScoreCourage();
				if (courage != 0) {
					ItemService.addItem(player, 186000137, courage);
				}
				int crucible = reward.getBasicCrucible() + reward.getRankingCrucible() + reward.getScoreCrucible();
				if (crucible != 0) {
					ItemService.addItem(player, 186000130, crucible);
				}
				int platinumMedal = reward.getPlatinumMedal();
				if (platinumMedal != 0) {
					ItemService.addItem(player, 186000096, platinumMedal);
				}
				int goldMedal = reward.getGoldmMedal();
				if (goldMedal != 0) {
					ItemService.addItem(player, 186000030, goldMedal);
				}
				int silverMedal = reward.getSilverMedal();
				if (silverMedal != 0) {
					ItemService.addItem(player, 186000031, silverMedal);
				}
				int gloriousInsignia = reward.getGloriousInsignia();
				if (gloriousInsignia != 0) {
					ItemService.addItem(player, 182213259, gloriousInsignia);
				}
				int lifeSerum = reward.getLifeSerum();
				if (lifeSerum != 0) {
					ItemService.addItem(player, 162000077, lifeSerum);
				}
			}
		}
		for (Npc npc : instance.getNpcs()) {
			npc.getController().onDelete();
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						onExitInstance(player);

					}
					AutoGroupService2.getInstance().unRegisterInstance(instanceId);
				}
			}

		}, 10000);
	}

	protected Npc getNpc(float x, float y, float z) {
		if (!isInstanceDestroyed) {
			for (Npc npc : instance.getNpcs()) {
				SpawnTemplate st = npc.getSpawn();
				if (st.getX() == x && st.getY() == y && st.getZ() == z) {
					return npc;
				}
			}
		}
		return null;
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		if (!instanceReward.isStartProgress()) {
			return;
		}

		int rewardetPoints = getNpcBonus(npc.getNpcId());
		int skill = instanceReward.getNpcBonusSkill(npc.getNpcId());
		if (skill != 0) {
			useSkill(npc, player, skill >> 8, skill & 0xFF);
		}
		
		getPlayerReward(player).addPoints(rewardetPoints);
		sendSystemMsg(player, npc, rewardetPoints);
		sendPacket();
	}

	protected void useSkill(Npc npc, Player player, int skillId, int level) {
		SkillEngine.getInstance().getSkill(npc, skillId, level, player).useSkill();
	}

	protected boolean isPvpArena() {
		return false;
	}

	protected void spawnOnStart(){
	}
}