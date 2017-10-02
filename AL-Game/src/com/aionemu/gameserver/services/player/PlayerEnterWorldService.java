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
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.dao.*;
import com.aionemu.gameserver.model.ChatType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterBanInfo;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.PunishmentService.PunishmentType;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssSkillService;
import com.aionemu.gameserver.services.reward.RewardService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.services.toypet.PetService;
import com.aionemu.gameserver.services.transfers.PlayerTransferService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.audit.GMService;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.world.World;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public final class PlayerEnterWorldService {

	private static final Logger log = LoggerFactory.getLogger(PlayerEnterWorldService.class);
    //private static final String serverName = " " + GSConfig.SERVER_NAME + " ";
	//private static final String serverIntro = "Please remember:";
	//private static final String serverInfo;
	//private static String alInfo;
	private static final Set<Integer> pendingEnterWorld = new HashSet<Integer>();

	/**
	 * @param objectId
	 * @param client
	 */
	public static void startEnterWorld(final int objectId, final AionConnection client) {
		// check if char is banned
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		Timestamp lastOnline = playerAccData.getPlayerCommonData().getLastOnline();
		if (lastOnline != null) {
			if (System.currentTimeMillis() - lastOnline.getTime() < (GSConfig.CHARACTER_REENTRY_TIME * 1000)) {
				client.sendPacket(new SM_ENTER_WORLD_CHECK((byte) 6)); // 20 sec time
				return;
			}
		}
		CharacterBanInfo cbi = client.getAccount().getPlayerAccountData(objectId).getCharBanInfo();
		if (cbi != null) {
			if (cbi.getEnd() > System.currentTimeMillis() / 1000) {
				client.close(new SM_QUIT_RESPONSE(), false);
				return;
			}
			else {
				DAOManager.getDAO(PlayerPunishmentsDAO.class).unpunishPlayer(objectId, PunishmentType.CHARBAN);
			}
		}
		// passkey check
		if (GSConfig.PASSKEY_ENABLE && !client.getAccount().getCharacterPasskey().isPass()) {
			showPasskey(objectId, client);
		}
		else {
			validateAndEnterWorld(objectId, client);
		}
	}

	/**
	 * @param objectId
	 * @param client
	 */
	private static void showPasskey(final int objectId, final AionConnection client) {
		client.getAccount().getCharacterPasskey().setConnectType(ConnectType.ENTER);
		client.getAccount().getCharacterPasskey().setObjectId(objectId);
		boolean isExistPasskey = DAOManager.getDAO(PlayerPasskeyDAO.class).existCheckPlayerPasskey(
				client.getAccount().getId());

		if (!isExistPasskey)
			client.sendPacket(new SM_CHARACTER_SELECT(0));
		else
			client.sendPacket(new SM_CHARACTER_SELECT(1));
	}

	/**
	 * @param objectId
	 * @param client
	 */
	private static void validateAndEnterWorld(final int objectId, final AionConnection client) {
		synchronized (pendingEnterWorld) {
			if (pendingEnterWorld.contains(objectId)) {
				log.warn("Skipping enter world " + objectId);
				return;
			}
			pendingEnterWorld.add(objectId);
		}
		int delay = 0;
		// double checked enter world
		if (World.getInstance().findPlayer(objectId) != null) {
			delay = 15000;
			log.warn("Postponed enter world " + objectId);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				try {
					Player player = World.getInstance().findPlayer(objectId);
					if (player != null) {
						AuditLogger.info(player, "Duplicate player in world");
						client.close(new SM_QUIT_RESPONSE(), false);
						return;
					}
					enterWorld(client, objectId);
				}
				catch (Throwable ex) {
					log.error("Error during enter world " + objectId, ex);
				}
				finally {
					synchronized (pendingEnterWorld) {
						pendingEnterWorld.remove(objectId);
					}
				}
			}

		}, delay);
	}
	
	private static void getBoostAPnewPlayer(Player player){
		long createDate =  DAOManager.getDAO(PlayerDAO.class).getCreationTime(player.getObjectId());
		long newbieTime = CustomConfig.BOOST_AP_NEW_PLAYER_TIME * 24 * 60 * 60 * 1000; // millisecond
		long creatSince = System.currentTimeMillis() - createDate;
		float bonus = CustomConfig.BOOST_AP_NEW_PLAYER_RATIO;
		long timeLeft = newbieTime - creatSince;

		if(creatSince > newbieTime){
			return;
		}

		String message = TranslationService.NEW_PLAYER_BONUS_AP.toString(player, String.valueOf(bonus), String.valueOf(timeLeft / 86400000));
		PacketSendUtility.sendBrightYellowMessage(player, message);
		player.setNewPlayer(true);
	}
	
	/**
	 * @param client
	 * @param objectId
	 */
	public static void enterWorld(AionConnection client, int objectId) {
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);

		if (playerAccData == null) {
			// Somebody wanted to login on character that is not at his account
			return;
		}
		Player player = PlayerService.getPlayer(objectId, account);

		if (player != null && client.setActivePlayer(player)) {
			player.setClientConnection(client);
			

			log.info("[MAC_AUDIT] Player " + player.getName() + " (account " + account.getName()
					+ ") has entered world with " + client.getMacAddress() + " MAC.");
			World.getInstance().storeObject(player);
			SerialKillerService.getInstance().onLogin(player);
			StigmaService.onPlayerLogin(player);

			/**
			 * Energy of Repose must be calculated before sending SM_STATS_INFO
			 */
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				PlayerCommonData pcd = player.getCommonData();
				long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				if (pcd.isReadyForSalvationPoints()) {
					// 10 mins offline = 0 salvation points.
					if (secondsOffline > 10 * 60) {
						player.getCommonData().resetSalvationPoints();
					}
				}
				if (pcd.isReadyForReposteEnergy()) {
					pcd.updateMaxReposte();
					// more than 4 hours offline = start counting Reposte Energy addition.
					if (secondsOffline > 4 * 3600) {
						double hours = secondsOffline / 3600d;
						long maxRespose = player.getCommonData().getMaxReposteEnergy();
						if (hours > 24)
							hours = 24;
						// 24 hours offline = 100% Reposte Energy
						long addResposeEnergy = (long) ((hours / 24) * maxRespose);
						pcd.addReposteEnergy(addResposeEnergy);
					}
				}
				// if (((System.currentTimeMillis() / 1000) - lastOnline) > 300)
				//	player.getCommonData().setDp(0);
				/*
				log.info("[DEBUG] CommonData getLastOnline ");
				if (secondsOffline > 300)
					player.getCommonData().setDp(0);
				else{
					int dp = player.getVarInt("dp");
					log.info("[DEBUG] dp value : "+ dp);
					player.getCommonData().setDp(dp);
				}
				*/
			}
			
			client.sendPacket(new SM_SKILL_LIST(player));
			AbyssSkillService.onEnterWorld(player);

			if (player.getSkillCoolDowns() != null)
				client.sendPacket(new SM_SKILL_COOLDOWN(player.getSkillCoolDowns()));

			if (player.getItemCoolDowns() != null)
				client.sendPacket(new SM_ITEM_COOLDOWN(player.getItemCoolDowns()));

			FastList<QuestState> questList = FastList.newInstance();
			FastList<QuestState> completeQuestList = FastList.newInstance();
			for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
				if (qs.getStatus() == QuestStatus.NONE)
					continue;
				if (qs.getStatus() == QuestStatus.COMPLETE)
					completeQuestList.add(qs);
				else
					questList.add(qs);
			}
			client.sendPacket(new SM_QUEST_COMPLETED_LIST(completeQuestList));
			client.sendPacket(new SM_QUEST_LIST(questList));
			client.sendPacket(new SM_RECIPE_LIST(player.getRecipeList().getRecipeList()));
			client.sendPacket(new SM_ENTER_WORLD_CHECK());

			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();

			if (uiSettings != null)
				client.sendPacket(new SM_UI_SETTINGS(uiSettings, 0));

			if (shortcuts != null)
				client.sendPacket(new SM_UI_SETTINGS(shortcuts, 1));

			sendItemInfos(client, player);
			playerLoggedIn(player);

			client.sendPacket(new SM_INSTANCE_INFO(player, false, false));

			sendMacroList(client, player);

			client.sendPacket(new SM_GAME_TIME());

			client.sendPacket(new SM_MOTION(player.getMotions().getMotions().values()));

			client.sendPacket(new SM_TITLE_INFO(player));
			client.sendPacket(new SM_CHANNEL_INFO(player.getPosition()));
			client.sendPacket(new SM_PLAYER_SPAWN(player));

			client.sendPacket(new SM_EMOTION_LIST((byte) 0, player.getEmotions().getEmotions()));

			client.sendPacket(new SM_INFLUENCE_RATIO());
			client.sendPacket(new SM_SIEGE_LOCATION_INFO());
			// TODO: Send Rift Announce Here
			client.sendPacket(new SM_PRICES());
			client.sendPacket(new SM_ABYSS_RANK(player.getAbyssRank()));

			// Intro message
			PacketSendUtility.sendWhiteMessage(player, " " + GSConfig.SERVER_NAME + " ");
			//PacketSendUtility.sendYellowMessage(player, serverIntro);
			//PacketSendUtility.sendBrightYellowMessage(player, serverInfo);
			if (GSConfig.SERVER_MOTD_DISPLAYREV) {
				PacketSendUtility.sendYellowMessage(player, "-----------------------------");
				PacketSendUtility.sendYellowMessage(player, "REV: " + GSConfig.SERVER_REV);
			}
			if (GSConfig.SERVER_MOTD.length() > 0) {
				PacketSendUtility.sendYellowMessage(player, "-----------------------------");
				PacketSendUtility.sendYellowMessage(player, GSConfig.SERVER_MOTD);
			}
			
			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));
			if (CustomConfig.PREMIUM_NOTIFY) {
				showPremiumAccountInfo(client, account);
			}
			
			if(player.getRace().toString().contains(CustomConfig.FACTION_BONUS_TO)) {
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_ANNOUNCE.toString(player));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_HUNTING.toString(player, formatBonus(CustomConfig.FACTION_BONUS_HUNT)));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_QUEST.toString(player, formatBonus(CustomConfig.FACTION_BONUS_QUEST)));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_CRAFT.toString(player, formatBonus(CustomConfig.FACTION_BONUS_CRAFT)));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_GATHER.toString(player, formatBonus(CustomConfig.FACTION_BONUS_GATHER)));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_AP.toString(player, formatBonus(CustomConfig.FACTION_BONUS_AP)));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_ATTACK.toString(player, formatBonus(CustomConfig.FACTION_BONUS_ATTACK)));
				PacketSendUtility.sendWhiteMessage(player, TranslationService.DFB_LOGIN_DEFENSE.toString(player, formatBonus(CustomConfig.FACTION_BONUS_DEFENSE)));
			}
			
			if(CustomConfig.BOOST_AP_NEW_PLAYER)
				getBoostAPnewPlayer(player);

			if (player.isGM()) {
				if (AdminConfig.INVULNERABLE_GM_CONNECTION || AdminConfig.INVISIBLE_GM_CONNECTION
						|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")
						|| AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")
						|| AdminConfig.VISION_GM_CONNECTION) {
					PacketSendUtility.sendMessage(player, "=============================");
					if (AdminConfig.INVULNERABLE_GM_CONNECTION) {
						player.setInvul(true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invulnerable mode <<");
					}
					if (AdminConfig.INVISIBLE_GM_CONNECTION) {
						player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
						player.setVisualState(CreatureVisualState.HIDE3);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Invisible mode <<");
					}
					if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Neutral")) {
						player.setAdminNeutral(3);
						player.setAdminEnmity(0);
						PacketSendUtility.sendMessage(player, ">> Connection in Neutral mode <<");
					}
					if (AdminConfig.ENEMITY_MODE_GM_CONNECTION.equalsIgnoreCase("Enemy")) {
						player.setAdminNeutral(0);
						player.setAdminEnmity(3);
						PacketSendUtility.sendMessage(player, ">> Connection in Enemy mode <<");
					}
					if (AdminConfig.VISION_GM_CONNECTION) {
						player.setSeeState(CreatureSeeState.SEARCH2);
						PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
						PacketSendUtility.sendMessage(player, ">> Connection in Vision mode <<");
					}
					if(AdminConfig.ADMIN_NO_CD_ON_CONNECTION) {
						player.setCoolDownZero(true);
						PacketSendUtility.sendMessage(player, ">> Connection in No CD mode <<");
					}
					
					PacketSendUtility.sendMessage(player, "=============================");
				}
			}

			KiskService.onLogin(player);
			TeleportService.sendSetBindPoint(player);

			// Alliance Packet after SetBindPoint
			PlayerAllianceService.onPlayerLogin(player);

			if (player.isInPrison())
			{
				//@author GoodT
				//fix prisonbreak - if player log on different map as prison will be teleported back to prison
				if(player.getWorldId() != 510010000 || player.getWorldId() == 520010000)
					TeleportService.teleportToPrison(player);
				
				PunishmentService.updatePrisonStatus(player);
			}

			if (player.isNotGatherable())
				PunishmentService.updateGatherableStatus(player);

			if (player.isLegionMember())
				LegionService.getInstance().onLogin(player);

			PlayerGroupService.onPlayerLogin(player);
			PetService.getInstance().onPlayerLogin(player);
			MailService.getInstance().onPlayerLogin(player);
			BrokerService.getInstance().onPlayerLogin(player);
			PetitionService.getInstance().onPlayerLogin(player);
			SiegeService.getInstance().onPlayerLogin(player);
			AutoGroupService2.getInstance().onPlayerLogin(player);
			ClassChangeService.showClassChangeDialog(player);

			GMService.getInstance().onPlayerLogin(player);
			/**
			 * Trigger restore services on login.
			 */
			player.getLifeStats().updateCurrentStats();

			if (HTMLConfig.ENABLE_HTML_WELCOME)
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("welcome.xhtml"));

			player.getNpcFactions().sendDailyQuest();

			if (HTMLConfig.ENABLE_GUIDES)
				HTMLService.onPlayerLogin(player);

			for (StorageType st : StorageType.values()) {
				if (st == StorageType.LEGION_WAREHOUSE)
					continue;
				IStorage storage = player.getStorage(st.getId());
				if (storage != null) {
					for (Item item : storage.getItemsWithKinah())
						if (item.getExpireTime() > 0)
							ExpireTimerTask.getInstance().addTask(item, player);
				}
			}

			for (Item item : player.getEquipment().getEquippedItems())
				if (item.getExpireTime() > 0)
					ExpireTimerTask.getInstance().addTask(item, player);

			for (Motion motion : player.getMotions().getMotions().values()) {
				if (motion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(motion, player);
				}
			}

			for (Emotion emotion : player.getEmotions().getEmotions()) {
				if (emotion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(emotion, player);
				}
			}

			for (Title title : player.getTitleList().getTitles()) {
				if (title.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(title, player);
				}
			}
			// scheduler periodic update
			player.getController().addTask(
					TaskId.PLAYER_UPDATE,
					ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(player.getObjectId()),
					PeriodicSaveConfig.PLAYER_GENERAL * 1000, PeriodicSaveConfig.PLAYER_GENERAL * 1000));
			player.getController().addTask(
					TaskId.INVENTORY_UPDATE,
					ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(player.getObjectId()),
					PeriodicSaveConfig.PLAYER_ITEMS * 1000, PeriodicSaveConfig.PLAYER_ITEMS * 1000));

			SurveyService.getInstance().showAvailable(player);

			if (CustomConfig.ENABLE_REWARD_SERVICE)
				RewardService.getInstance().verify(player);

			if (EventsConfig.ENABLE_EVENT_SERVICE)
				EventService.getInstance().onPlayerLogin(player);

			PlayerTransferService.getInstance().onEnterWorld(player);
			
			player.setPartnerId(DAOManager.getDAO(WeddingDAO.class).loadPartnerId(player));
			DAOManager.getDAO(PlayerRankDAO.class).loadCustomRank(player);;
			//DP restore
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				final long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				final long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				
				if (secondsOffline > 300)
					player.getCommonData().setDp(0);
				else{
					final int dp = player.getVarInt("dp");
					player.getCommonData().setDp(dp);
				}
			}
		}
		else {
			log.info("[DEBUG] enter world" + objectId + ", Player: " + player);
		}
	}

	private static String formatBonus(float bonus) {
		bonus = (bonus - 1) * 100;
		return Math.round(bonus) + "%";
	}

	/**
	 * @param client
	 * @param player
	 */
	// TODO! this method code is really odd [Nemesiss]
	private static void sendItemInfos(AionConnection client, Player player) {
		// Cubesize limit set in inventory.
		int questExpands = player.getQuestExpands();
		int npcExpands = player.getNpcExpands();
		player.getInventory().setLimit(StorageType.CUBE.getLimit() + (questExpands + npcExpands) * 9);
		player.getWarehouse().setLimit(StorageType.REGULAR_WAREHOUSE.getLimit() + player.getWarehouseSize() * 8);

		// items
		Storage inventory = player.getInventory();
		List<Item> equipedItems = player.getEquipment().getEquippedItems();
		if (equipedItems.size() != 0) {
			client.sendPacket(new SM_INVENTORY_INFO(player.getEquipment().getEquippedItems(), npcExpands, questExpands,
					player));
		}

		List<Item> unequipedItems = inventory.getItemsWithKinah();
		int itemsSize = unequipedItems.size();
		if (itemsSize != 0) {
			int index = 0;
			while (index + 10 < itemsSize) {
				client.sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, index + 10), npcExpands, questExpands,
						player));
				index += 10;
			}
			client.sendPacket(new SM_INVENTORY_INFO(unequipedItems.subList(index, itemsSize), npcExpands, questExpands,
					player));
		}
		client.sendPacket(new SM_INVENTORY_INFO());
		client.sendPacket(new SM_STATS_INFO(player));
		client.sendPacket(SM_CUBE_UPDATE.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
	}

	private static void sendMacroList(AionConnection client, Player player) {
		client.sendPacket(new SM_MACRO_LIST(player));
	}

	/**
	 * @param player
	 */
	private static void playerLoggedIn(Player player) {
		log.info("Player logged in: " + player.getName() + " Account: "
				+ player.getClientConnection().getAccount().getName());
		player.getCommonData().setOnline(true);
		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, true);
		player.onLoggedIn();
		player.setOnlineTime();
	}

	private static void showPremiumAccountInfo(AionConnection client, Account account) {
		byte membership = account.getMembership();
		if (membership > 0) {
			String accountType = "";
			switch (account.getMembership()) {
				case 1:
					accountType = "premium";
					break;
				case 2:
					accountType = "VIP";
					break;
			}
			client.sendPacket(new SM_MESSAGE(0, null, "Your account is " + accountType, ChatType.GOLDEN_YELLOW));
		}
	}

}

class GeneralUpdateTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(GeneralUpdateTask.class);
	private final int playerId;

	GeneralUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			try {
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
				DAOManager.getDAO(PlayerSkillListDAO.class).storeSkills(player);
				DAOManager.getDAO(PlayerQuestListDAO.class).store(player);
				DAOManager.getDAO(PlayerDAO.class).storePlayer(player);
				DAOManager.getDAO(PlayerRankDAO.class).loadCustomRank(player);
			}
			catch (Exception ex) {
				log.error("Exception during periodic saving of player " + player.getName(), ex);
			}
		}

	}

}

class ItemUpdateTask implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(ItemUpdateTask.class);
	private final int playerId;

	ItemUpdateTask(int playerId) {
		this.playerId = playerId;
	}

	@Override
	public void run() {
		Player player = World.getInstance().findPlayer(playerId);
		if (player != null) {
			try {
				DAOManager.getDAO(InventoryDAO.class).store(player);
				DAOManager.getDAO(ItemStoneListDAO.class).save(player);
			}
			catch (Exception ex) {
				log.error("Exception during periodic saving of player items " + player.getName(), ex);
			}
		}
	}

}
