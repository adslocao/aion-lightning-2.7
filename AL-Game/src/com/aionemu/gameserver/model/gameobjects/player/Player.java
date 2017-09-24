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
package com.aionemu.gameserver.model.gameobjects.player;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.custom.CustomFun;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.controllers.FlyController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.PlayerAggroList;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.custom.CustomPlayerRank;
import com.aionemu.gameserver.dao.PlayerVarsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.NpcType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank.AbyssRankUpdateType;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.emotion.EmotionList;
import com.aionemu.gameserver.model.gameobjects.player.motion.MotionList;
import com.aionemu.gameserver.model.gameobjects.player.npcFaction.NpcFactions;
import com.aionemu.gameserver.model.gameobjects.player.title.TitleList;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.LegionStorageProxy;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceGroup;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.npc.AbyssNpcType;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.serialkillers.SerialKiller;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.RebirthEffect;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.utils.rates.RegularRates;
import com.aionemu.gameserver.world.World;

/**
 * This class is representing Player object, it contains all needed data.
 * 
 * @author -Nemesiss-
 * @author SoulKeeper
 * @author alexa026
 * @author cura
 */
public class Player extends Creature {

    /**
	* Custom
	**/
	public static Logger log = LoggerFactory.getLogger(Player.class);
	
	private boolean newPlayer = false;
		
	private InRoll inRoll;
	private PlayerAppearance playerAppearance;
	private PlayerAppearance savedPlayerAppearance;
	private PlayerCommonData playerCommonData;
	private Account playerAccount;
	private LegionMember legionMember;

	private MacroList macroList;
	private PlayerSkillList skillList;
	private FriendList friendList;
	private BlockList blockList;
	private PetList toyPetList;
	private Mailbox mailbox;
	private PrivateStore store;
	private TitleList titleList;
	private QuestStateList questStateList;
	private RecipeList recipeList;

	private ResponseRequester requester;
	private boolean lookingForGroup = false;
	private Storage inventory;
	private Storage[] petBag = new Storage[4];
	private Storage regularWarehouse;
	private Storage accountWarehouse;
	private Equipment equipment;

	private PlayerStatsTemplate playerStatsTemplate;
	private PlayerSettings playerSettings;

	private com.aionemu.gameserver.model.team2.group.PlayerGroup playerGroup2;
	private PlayerAllianceGroup playerAllianceGroup;

	private AbyssRank abyssRank;
	private NpcFactions npcFactions;
	private Rates rates;

	private int flyState = 0;
	private boolean isTrading;
	private long prisonTimer = 0;
	private long startPrison;
	private boolean invul;
	private FlyController flyController;
	private CraftingTask craftingTask;
	private int flightTeleportId;
	private int flightDistance;
	private Summon summon;
	private Pet toyPet;
	private Kisk kisk;
	private boolean isResByPlayer = false;
	private boolean isResWithHalfSoulTime = false;
	public boolean awakeReg = false;
	private boolean isFlyingBeforeDeath = false;
	private boolean isGagged = false;
	private boolean edit_mode = false;
	private Npc questFollowingNpc = null;
	private Npc postman = null;
	private boolean isInResurrectPosState = false;
	private float resPosX = 0;
	private float resPosY = 0;
	private float resPosZ = 0;
	private boolean underNoFPConsum = false;
	private boolean isAdminTeleportation = false;
	private boolean cooldownZero = false;
	private boolean isUnderInvulnerableWing = false;
	private boolean isTransformed = false;
	private boolean isFlying = false;

	private int abyssRankListUpdateMask = 0;

	private BindPointPosition bindPoint;

	private Map<Integer, ItemCooldown> itemCoolDowns;
	private PortalCooldownList portalCooldownList;
	private CraftCooldownList craftCooldownList;
	private long nextSkillUse;
	private long nextSummonSkillUse;
	private long onlineBonusTime = 0;
	private String chainCategory;
	private long lastChainSkillTime;
	private Map<AttackStatus, Long>	lastCounterSkill	= new HashMap<AttackStatus, Long>();

	private int dualEffectValue = 0;
	/**
	 * Static information for players
	 */
	private static final int CUBE_SPACE = 9;
	private static final int WAREHOUSE_SPACE = 8;
	private int nrCategoryInGameShop = 0;
	private boolean isAttackMode = false;

	private long gatherableTimer = 0;
	private long stopGatherable;
	private String captchaWord;
	private byte[] captchaImage;

	private float instanceStartPosX, instanceStartPosY, instanceStartPosZ;
	private int rebirthResurrectPercent = 1;

	/**
	 * Connection of this Player.
	 */
	private AionConnection clientConnection;
	private FlyPathEntry flyLocationId;
	private long flyStartTime;

	private EmotionList emotions;
	private MotionList motions;
	private int partnerId;
	private ActionItemNpc actionItemNpc;
	private long flyReuseTime;
	private boolean isMentor;
	private long lastMsgTime = 0;
	private int floodMsgCount = 0;
	private long onlineTime = 0;
	private boolean teleportedFlag = false;
	private int lootingNpcOid;
	private boolean rebirthRevive;
	private int teleportEid = 2;
	
	// Needed to remove supplements queue
	private int subtractedSupplementsCount;
	private int subtractedSupplementId;

	/**
	 * event engine
	 */
	private int eventTeamId = -1;
	
	private SerialKiller skList;
	private CustomPlayerRank customPlayerRank = new CustomPlayerRank(this);
	/**
	 * Used for JUnit tests
	 */
	private Player(PlayerCommonData plCommonData) {
		super(0, null, null, null, null);
		this.playerCommonData = plCommonData;
	}

	public Player(PlayerController controller, PlayerCommonData plCommonData, PlayerAppearance appereance, Account account) {
		super(plCommonData.getPlayerObjId(), controller, null, plCommonData, plCommonData.getPosition());
		this.playerCommonData = plCommonData;
		this.playerAppearance = appereance;
		this.playerAccount = account;

		this.requester = new ResponseRequester(this);
		this.questStateList = new QuestStateList();
		this.titleList = new TitleList();
		this.portalCooldownList = new PortalCooldownList(this);
		this.craftCooldownList = new CraftCooldownList(this);
		this.toyPetList = new PetList(this);
		controller.setOwner(this);
		moveController = new PlayerMoveController(this);
		plCommonData.setBoundingRadius(new BoundRadius(0.5f, 0.5f, getPlayerAppearance().getHeight()));

		setPlayerStatsTemplate(DataManager.PLAYER_STATS_DATA.getTemplate(this));
		setGameStats(new PlayerGameStats(this));
		setLifeStats(new PlayerLifeStats(this));
		actionItemNpc = new ActionItemNpc();
	}

	@Override
	public PlayerMoveController getMoveController() {
		return (PlayerMoveController) super.getMoveController();
	}

	@Override
	protected final AggroList createAggroList() {
		return new PlayerAggroList(this);
	}

	public CustomPlayerRank getCustomPlayerRank(){
		return customPlayerRank;
	}
    public SerialKiller getSKInfo() {
        return skList;
    }
    
    public void setSKInfo(SerialKiller serialKiller) {
        skList = serialKiller;
    }
    
	public InRoll getInRoll() {
		return inRoll;
	}

	public void setInRoll(InRoll InRoll) {
		this.inRoll = InRoll;
	}

	public boolean isInRoll() {
		return inRoll != null ? true : false;
	}

	public int getNrCategoryInGameShop() {
		return nrCategoryInGameShop;
	}

	public void setNrCategoryInGameShop(int nrCategoryInGameShop) {
		this.nrCategoryInGameShop = nrCategoryInGameShop;
	}

	public PlayerCommonData getCommonData() {
		return playerCommonData;
	}

	@Override
	public String getName() {
		return playerCommonData.getName();
	}
	
	public String getFullName() {
		String nameFormat = "%s";
		if (AdminConfig.CUSTOMTAG_ENABLE) {
			switch (getAccessLevel()) {
				case 1:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS1;
					break;
				case 2:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS2;
					break;
				case 3:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS3;
					break;
				case 4:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS4;
					break;
				case 5:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS5;
					break;
				case 7:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS7;
					break;
				case 8:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS8;
					break;
				case 9:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS9;
					break;
				case 10:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS10;
					break;
				case 11:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS11;
					break;
				case 12:
					nameFormat = AdminConfig.CUSTOMTAG_ACCESS12;
					break;
			}
		}
		if(CustomFun.CUSTOM_RANK_ENABLED && !isGM())
			nameFormat = "%s ["+getCustomPlayerRank().getRank() +"]";
		return String.format(nameFormat, getName());
	}

	public PlayerAppearance getPlayerAppearance() {
		return playerAppearance;
	}

	public void setPlayerAppearance(PlayerAppearance playerAppearance) {
		this.playerAppearance = playerAppearance;
	}

	/**
	 * Only use for the Size admin command
	 * 
	 * @return PlayerAppearance : The saved player's appearance, to rollback his appearance
	 */
	public PlayerAppearance getSavedPlayerAppearance() {
		return savedPlayerAppearance;
	}

	/**
	 * Only use for the Size admin command
	 * 
	 * @param playerAppearance
	 *          PlayerAppearance : The saved player's appearance, to rollback his appearance
	 */
	public void setSavedPlayerAppearance(PlayerAppearance savedPlayerAppearance) {
		this.savedPlayerAppearance = savedPlayerAppearance;
	}

	/**
	 * Set connection of this player.
	 * 
	 * @param clientConnection
	 */
	public void setClientConnection(AionConnection clientConnection) {
		this.clientConnection = clientConnection;
	}

	/**
	 * Get connection of this player.
	 * 
	 * @return AionConnection of this player.
	 */
	public AionConnection getClientConnection() {
		return this.clientConnection;
	}

	public MacroList getMacroList() {
		return macroList;
	}

	public void setMacroList(MacroList macroList) {
		this.macroList = macroList;
	}

	public PlayerSkillList getSkillList() {
		return skillList;
	}

	public void setSkillList(PlayerSkillList skillList) {
		this.skillList = skillList;
	}

	/**
	 * @return the toyPet
	 */
	public Pet getPet() {
		return toyPet;
	}

	/**
	 * @param toyPet
	 *          the toyPet to set
	 */
	public void setToyPet(Pet toyPet) {
		this.toyPet = toyPet;
	}

	/**
	 * Gets this players Friend List
	 * 
	 * @return FriendList
	 */
	public FriendList getFriendList() {
		return friendList;
	}

	/**
	 * Is this player looking for a group
	 * 
	 * @return true or false
	 */
	public boolean isLookingForGroup() {
		return lookingForGroup;
	}

	/**
	 * Sets whether or not this player is looking for a group
	 * 
	 * @param lookingForGroup
	 */
	public void setLookingForGroup(boolean lookingForGroup) {
		this.lookingForGroup = lookingForGroup;
	}

	public boolean isAttackMode() {
		return isAttackMode;
	}

	public void setAttackMode(boolean isAttackMode) {
		this.isAttackMode = isAttackMode;
	}

	public boolean isNotGatherable() {
		return gatherableTimer != 0;
	}

	public void setGatherableTimer(long gatherableTimer) {
		if (gatherableTimer < 0)
			gatherableTimer = 0;

		this.gatherableTimer = gatherableTimer;
	}

	public long getGatherableTimer() {
		return gatherableTimer;
	}

	public long getStopGatherable() {
		return stopGatherable;
	}

	public void setStopGatherable(long stopGatherable) {
		this.stopGatherable = stopGatherable;
	}

	public String getCaptchaWord() {
		return captchaWord;
	}

	public void setCaptchaWord(String captchaWord) {
		this.captchaWord = captchaWord;
	}

	public byte[] getCaptchaImage() {
		return captchaImage;
	}

	public void setCaptchaImage(byte[] captchaImage) {
		this.captchaImage = captchaImage;
	}
	public long getOnlineBonusTime()
	{
		return this.onlineBonusTime;
	}

	public void setOnlineBonusTime(long time)
	{
		this.onlineBonusTime = time;
	}

	/**
	 * Sets this players friend list. <br />
	 * Remember to send the player the <tt>SM_FRIEND_LIST</tt> packet.
	 * 
	 * @param list
	 */
	public void setFriendList(FriendList list) {
		this.friendList = list;
	}

	public BlockList getBlockList() {
		return blockList;
	}

	public void setBlockList(BlockList list) {
		this.blockList = list;
	}

	public final PetList getPetList() {
		return toyPetList;
	}

	@Override
	public PlayerLifeStats getLifeStats() {
		return (PlayerLifeStats) super.getLifeStats();
	}

	@Override
	public PlayerGameStats getGameStats() {
		return (PlayerGameStats) super.getGameStats();
	}

	/**
	 * Gets the ResponseRequester for this player
	 * 
	 * @return ResponseRequester
	 */
	public ResponseRequester getResponseRequester() {
		return requester;
	}

	public boolean isOnline() {
		return getClientConnection() != null;
	}

	public void setQuestExpands(int questExpands) {
		this.playerCommonData.setQuestExpands(questExpands);
		getInventory().setLimit(getInventory().getLimit() + (questExpands + getNpcExpands()) * CUBE_SPACE);
	}

	public int getQuestExpands() {
		return this.playerCommonData.getQuestExpands();
	}

	public void setNpcExpands(int npcExpands) {
		this.playerCommonData.setNpcExpands(npcExpands);
		getInventory().setLimit(getInventory().getLimit() + (npcExpands + getQuestExpands()) * CUBE_SPACE);
	}

	public int getNpcExpands() {
		return this.playerCommonData.getNpcExpands();
	}

	public PlayerClass getPlayerClass() {
		return playerCommonData.getPlayerClass();
	}

	public Gender getGender() {
		return playerCommonData.getGender();
	}

	/**
	 * Return PlayerController of this Player Object.
	 * 
	 * @return PlayerController.
	 */
	@Override
	public PlayerController getController() {
		return (PlayerController) super.getController();
	}

	@Override
	public byte getLevel() {
		return (byte) playerCommonData.getLevel();
	}

	/**
	 * @return the inventory
	 */

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	/**
	 * @return the player private store
	 */
	public PrivateStore getStore() {
		return store;
	}

	/**
	 * @param store
	 *          the store that needs to be set
	 */
	public void setStore(PrivateStore store) {
		this.store = store;
	}

	/**
	 * @return the questStatesList
	 */
	public QuestStateList getQuestStateList() {
		return questStateList;
	}

	/**
	 * @param questStateList
	 *          the QuestStateList to set
	 */
	public void setQuestStateList(QuestStateList questStateList) {
		this.questStateList = questStateList;
	}

	/**
	 * @return the playerStatsTemplate
	 */
	public PlayerStatsTemplate getPlayerStatsTemplate() {
		return playerStatsTemplate;
	}

	/**
	 * @param playerStatsTemplate
	 *          the playerStatsTemplate to set
	 */
	public void setPlayerStatsTemplate(PlayerStatsTemplate playerStatsTemplate) {
		this.playerStatsTemplate = playerStatsTemplate;
	}

	public RecipeList getRecipeList() {
		return recipeList;
	}

	public void setRecipeList(RecipeList recipeList) {
		this.recipeList = recipeList;
	}

	/**
	 * @param inventory
	 *          the inventory to set Inventory should be set right after player object is created
	 */
	public void setStorage(Storage storage, StorageType storageType) {
		if (storageType == StorageType.CUBE) {
			this.inventory = storage;
		}
		if (storageType.getId() > 31 && storageType.getId() < 36) {
			this.petBag[storageType.getId() - 32] = storage;
		}
		if (storageType == StorageType.REGULAR_WAREHOUSE) {
			this.regularWarehouse = storage;
		}
		if (storageType == StorageType.ACCOUNT_WAREHOUSE) {
			this.accountWarehouse = storage;
		}
		storage.setOwner(this);
	}

	/**
	 * @param storageType
	 * @return
	 */
	public IStorage getStorage(int storageType) {
		if (storageType == StorageType.REGULAR_WAREHOUSE.getId())
			return regularWarehouse;

		if (storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
			return accountWarehouse;

		if (storageType == StorageType.LEGION_WAREHOUSE.getId() && getLegion() != null) {
			return new LegionStorageProxy(getLegion().getLegionWarehouse(), this);
		}

		if (storageType > 31 && storageType < 36)
			return petBag[storageType - 32];

		if (storageType == StorageType.CUBE.getId())
			return inventory;
		return null;
	}

	/**
	 * Items from UPDATE_REQUIRED storages and equipment
	 * 
	 * @return
	 */
	public List<Item> getDirtyItemsToUpdate() {
		List<Item> dirtyItems = new ArrayList<Item>();

		IStorage cubeStorage = getStorage(StorageType.CUBE.getId());
		if (cubeStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(cubeStorage.getItemsWithKinah());
			dirtyItems.addAll(cubeStorage.getDeletedItems());
			cubeStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage regularWhStorage = getStorage(StorageType.REGULAR_WAREHOUSE.getId());
		if (regularWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(regularWhStorage.getItemsWithKinah());
			dirtyItems.addAll(regularWhStorage.getDeletedItems());
			regularWhStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage accountWhStorage = getStorage(StorageType.ACCOUNT_WAREHOUSE.getId());
		if (accountWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(accountWhStorage.getItemsWithKinah());
			dirtyItems.addAll(accountWhStorage.getDeletedItems());
			accountWhStorage.setPersistentState(PersistentState.UPDATED);
		}

		IStorage legionWhStorage = getStorage(StorageType.LEGION_WAREHOUSE.getId());
		if (legionWhStorage != null) {
			if (legionWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(legionWhStorage.getItemsWithKinah());
				dirtyItems.addAll(legionWhStorage.getDeletedItems());
				legionWhStorage.setPersistentState(PersistentState.UPDATED);
			}
		}

		for (int petBagId = 32; petBagId < 36; petBagId++) {
			IStorage petBag = getStorage(petBagId);
			if (petBag != null && petBag.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
				dirtyItems.addAll(petBag.getItemsWithKinah());
				dirtyItems.addAll(petBag.getDeletedItems());
				petBag.setPersistentState(PersistentState.UPDATED);
			}
		}

		Equipment equipment = getEquipment();
		if (equipment.getPersistentState() == PersistentState.UPDATE_REQUIRED) {
			dirtyItems.addAll(equipment.getEquippedItems());
			equipment.setPersistentState(PersistentState.UPDATED);
		}

		return dirtyItems;
	}

	/**
	 * //TODO probably need to optimize here
	 * 
	 * @return
	 */
	public FastList<Item> getAllItems() {
		FastList<Item> items = FastList.newInstance();
		items.addAll(this.inventory.getItemsWithKinah());
		if (this.regularWarehouse != null)
			items.addAll(this.regularWarehouse.getItemsWithKinah());
		if (this.accountWarehouse != null)
			items.addAll(this.accountWarehouse.getItemsWithKinah());

		for (int petBagId = 32; petBagId < 36; petBagId++) {
			IStorage petBag = getStorage(petBagId);
			if (petBag != null)
				items.addAll(petBag.getItemsWithKinah());
		}

		items.addAll(getEquipment().getEquippedItems());
		return items;
	}

	public Storage getInventory() {
		return inventory;
	}

	/**
	 * @return the playerSettings
	 */
	public PlayerSettings getPlayerSettings() {
		return playerSettings;
	}

	/**
	 * @param playerSettings
	 *          the playerSettings to set
	 */
	public void setPlayerSettings(PlayerSettings playerSettings) {
		this.playerSettings = playerSettings;
	}

	public TitleList getTitleList() {
		return titleList;
	}

	public void setTitleList(TitleList titleList) {
		if (havePermission(MembershipConfig.TITLES_ADDITIONAL_ENABLE)) {
			titleList.addEntry(102, 0);
			titleList.addEntry(103, 0);
			titleList.addEntry(104, 0);
			titleList.addEntry(105, 0);
			titleList.addEntry(106, 0);
			titleList.addEntry(146, 0);
			titleList.addEntry(151, 0);
			titleList.addEntry(152, 0);
			titleList.addEntry(160, 0);
			titleList.addEntry(161, 0);
		}
		this.titleList = titleList;
		titleList.setOwner(this);
	}

	public com.aionemu.gameserver.model.team2.group.PlayerGroup getPlayerGroup2() {
		return playerGroup2;
	}

	public void setPlayerGroup2(com.aionemu.gameserver.model.team2.group.PlayerGroup playerGroup) {
		this.playerGroup2 = playerGroup;
	}

	/**
	 * @return the abyssRank
	 */
	public AbyssRank getAbyssRank() {
		return abyssRank;
	}

	/**
	 * @param abyssRank
	 *          the abyssRank to set
	 */
	public void setAbyssRank(AbyssRank abyssRank) {
		this.abyssRank = abyssRank;
	}

	@Override
	public PlayerEffectController getEffectController() {
		return (PlayerEffectController) super.getEffectController();
	}

	public void onLoggedIn() {
		friendList.setStatus(Status.ONLINE);
	}

	public void onLoggedOut() {
		requester.denyAll();
		friendList.setStatus(FriendList.Status.OFFLINE);
	}

	/**
	 * Returns true if has valid LegionMember
	 */
	public boolean isLegionMember() {
		return legionMember != null;
	}

	/**
	 * @param legionMember
	 *          the legionMember to set
	 */
	public void setLegionMember(LegionMember legionMember) {
		this.legionMember = legionMember;
	}

	/**
	 * @return the legionMember
	 */
	public LegionMember getLegionMember() {
		return legionMember;
	}

	/**
	 * @return the legion
	 */
	public Legion getLegion() {
		return legionMember != null ? legionMember.getLegion() : null;
	}

	/**
	 * Checks if object id's are the same
	 * 
	 * @return true if the object id is the same
	 */
	public boolean sameObjectId(int objectId) {
		return this.getObjectId() == objectId;
	}

	/**
	 * @return true if a player has a store opened
	 */
	public boolean hasStore() {
		if (getStore() != null)
			return true;
		return false;
	}

	/**
	 * Removes legion from player
	 */
	public void resetLegionMember() {
		setLegionMember(null);
	}

	public boolean isInGroup2() {
		return playerGroup2 != null;
	}

	/**
	 * Access level of this player
	 * 
	 * @return byte
	 */
	public byte getAccessLevel() {
		return playerAccount.getAccessLevel();
	}

	/**
	 * accountName of this player
	 * 
	 * @return int
	 */
	public String getAcountName() {
		return playerAccount.getName();
	}

	/**
	 * @return the rates
	 */
	public Rates getRates() {
		if (rates == null)
			rates = new RegularRates();
		return rates;
	}

	/**
	 * @param rates
	 *          the rates to set
	 */
	public void setRates(Rates rates) {
		this.rates = rates;
	}

	/**
	 * @return warehouse size
	 */
	public int getWarehouseSize() {
		return this.playerCommonData.getWarehouseSize();
	}

	/**
	 * @param warehouseSize
	 */
	public void setWarehouseSize(int warehouseSize) {
		this.playerCommonData.setWarehouseSize(warehouseSize);
		getWarehouse().setLimit(getWarehouse().getLimit() + (warehouseSize * WAREHOUSE_SPACE));
	}

	/**
	 * @return regularWarehouse
	 */
	public Storage getWarehouse() {
		return regularWarehouse;
	}

	/**
	 * 0: regular, 1: fly, 2: glide
	 */
	public int getFlyState() {
		return this.flyState;
	}

	public void setFlyState(int flyState) {
		this.flyState = flyState;
		if (flyState == 1)
			setFlyingMode(true);
		else if (flyState == 0)
			setFlyingMode(false);
	}

	/**
	 * @return the isTrading
	 */
	public boolean isTrading() {
		return isTrading;
	}

	/**
	 * @param isTrading
	 *          the isTrading to set
	 */
	public void setTrading(boolean isTrading) {
		this.isTrading = isTrading;
	}

	/**
	 * @return the isInPrison
	 */
	public boolean isInPrison() {
		return prisonTimer != 0;
	}

	/**
	 * @param prisonTimer
	 *          the prisonTimer to set
	 */
	public void setPrisonTimer(long prisonTimer) {
		if (prisonTimer < 0)
			prisonTimer = 0;

		this.prisonTimer = prisonTimer;
	}

	/**
	 * @return the prisonTimer
	 */
	public long getPrisonTimer() {
		return prisonTimer;
	}

	/**
	 * @return the time in ms of start prison
	 */
	public long getStartPrison() {
		return startPrison;
	}

	/**
	 * @param start
	 *          : The time in ms of start prison
	 */
	public void setStartPrison(long start) {
		this.startPrison = start;
	}

	/**
	 * @return
	 */
	public boolean isProtectionActive() {
		return isInVisualState(CreatureVisualState.BLINKING);
	}

	/**
	 * Check is player is invul
	 * 
	 * @return boolean
	 **/
	public boolean isInvul() {
		return invul;
	}

	/**
	 * Sets invul on player
	 * 
	 * @param invul
	 *          - boolean
	 **/
	public void setInvul(boolean invul) {
		this.invul = invul;
	}

	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public Mailbox getMailbox() {
		return mailbox;
	}

	/**
	 * @return the flyController
	 */
	public FlyController getFlyController() {
		return flyController;
	}

	/**
	 * @param flyController
	 *          the flyController to set
	 */
	public void setFlyController(FlyController flyController) {
		this.flyController = flyController;
	}

	public int getLastOnline() {
		Timestamp lastOnline = playerCommonData.getLastOnline();
		if (lastOnline == null || isOnline())
			return 0;

		return (int) (lastOnline.getTime() / 1000);
	}

	/**
	 * @param craftingTask
	 */
	public void setCraftingTask(CraftingTask craftingTask) {
		this.craftingTask = craftingTask;
	}

	/**
	 * @return
	 */
	public CraftingTask getCraftingTask() {
		return craftingTask;
	}

	/**
	 * @param flightTeleportId
	 */
	public void setFlightTeleportId(int flightTeleportId) {
		this.flightTeleportId = flightTeleportId;
	}

	/**
	 * @return flightTeleportId
	 */
	public int getFlightTeleportId() {
		return flightTeleportId;
	}

	/**
	 * @param flightDistance
	 */
	public void setFlightDistance(int flightDistance) {
		this.flightDistance = flightDistance;

	}

	/**
	 * @param path
	 */
	public void setCurrentFlypath(FlyPathEntry path) {
		this.flyLocationId = path;
		if (path != null)
			this.flyStartTime = System.currentTimeMillis();
		else
			this.flyStartTime = 0;
	}

	/**
	 * @return flightDistance
	 */
	public int getFlightDistance() {
		return flightDistance;
	}

	/**
	 * @return
	 */
	public boolean isUsingFlyTeleport() {
		return isInState(CreatureState.FLIGHT_TELEPORT) && flightTeleportId != 0;
	}

	public boolean isGM() {
		return getAccessLevel() >= AdminConfig.GM_LEVEL;
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return creature.isEnemyFrom(this);
	}

	/**
	 * Npc enemies:<br>
	 * - monsters<br>
	 * - aggressive npcs<br>
	 * 
	 * @param npc
	 * @return
	 */
	@Override
	public boolean isEnemyFrom(Npc npc) {
		return npc.isAttackableNpc() || isAggroIconTo(npc);
	}

	/**
	 * Player enemies:<br>
	 * - different race<br>
	 * - duel partner<br>
	 * 
	 * @param player
	 * @return
	 */
	@Override
	public boolean isEnemyFrom(Player player) {
		//self can't be enemy
		if(player.getObjectId() == getObjectId())
			return false;
		//is in duel
		else if(getController().isDueling(player))
			return true;
		// if is in group can't be enemy
		else if(isInSameGroup(player))
			return false;
		// admin enemy
		else if(getAdminEnmity() > 1 || player.getAdminEnmity() > 1)
			return true;
		// is in event
		else if(getEventTeamId() != -1 && player.getEventTeamId() !=-1)
			return getEventTeamId() != player.getEventTeamId();
		//in pvp zone
		else if(this.isInsideZoneType(ZoneType.PVP) && player.isInsideZoneType(ZoneType.PVP))
			return true;
		// same race
		else
			return !player.getRace().equals(getRace());
	}

	private boolean isInSameGroup(Player player) {
		if (isInGroup2() && player.isInGroup2()) {
			return getPlayerGroup2().getTeamId().equals(player.getPlayerGroup2().getTeamId());
		}
		else if (isInAlliance2() && player.isInAlliance2()) {
			return getPlayerAlliance2().getObjectId().equals(player.getPlayerAlliance2().getObjectId());
		}
		return false;
	}

	@Override
	public TribeClass getTribe() {
		switch (getRace()) {
		case ELYOS:
			return TribeClass.PC;
		default:
			return TribeClass.PC_DARK;
		}
	}

	@Override
	public boolean isAggroFrom(Npc npc) {
		// npc's that are 10 or more levels lower don't get aggro on players
		return (isAggroIconTo(npc) && (npc.getTribe().isGuard()
				|| npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE || npc.getLevel() + 10 > getLevel() || (npc
						.isInInstance() && InstanceService.isAggro(npc.getWorldId()))));
	}

	/**
	 * Used in SM_NPC_INFO to check aggro irrespective to level
	 * 
	 * @param npcTribe
	 * @return
	 */
	public boolean isAggroIconTo(Npc npc) {
		Race race = npc.getRace();
		TribeClass tribe = npc.getTribe();
		if (getAdminEnmity() == 1 || getAdminEnmity() == 3)
			return true;
		// Exception by Tribe
		if (tribe == TribeClass.USEALL)
			return false;
		// AbyssType != NONE -> SiegeNpc
		if (npc.getObjectTemplate().getAbyssNpcType() != AbyssNpcType.NONE)
			return checkSiegeRelations(npc);

		switch (getRace()) {
		case ELYOS:
			if (race == Race.ASMODIANS || tribe.isDarkGuard())
				return true;
			return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(tribe, TribeClass.PC);
		case ASMODIANS:
			if (race == Race.ELYOS || tribe.isLightGuard())
				return true;
			return DataManager.TRIBE_RELATIONS_DATA.isAggressiveRelation(tribe, TribeClass.PC_DARK);
		}
		return false;
	}

	/*
	 * Siege npc relations to player
	 */
	public boolean checkSiegeRelations(Npc npc) {
		Race race = npc.getRace();
		NpcType npcType = npc.getNpcType();
		TribeClass tribe = npc.getTribe();
		// Artifact can't be Enemy
		if (npcType == NpcType.ARTIFACT)
			return false;
		// Portal can't be Enemy
		if (npcType == NpcType.PORTAL)
			return false;
		// Exception friendly Balaur's
		if (race == Race.DRAKAN && npcType == NpcType.NON_ATTACKABLE)
			return false;
		switch (getRace()) {
		case ELYOS:
			// Elyos Gate
			if (race == Race.PC_LIGHT_CASTLE_DOOR)
				return false;
			// Elyos General
			if (race == Race.GCHIEF_LIGHT)
				return false;
			// Elyos Teleporter
			if (race == Race.TELEPORTER && tribe == TribeClass.GENERAL)
				return false;
			// Elyos Shield generators
			if ((race == Race.CONSTRUCT || race == Race.BARRIER) &&
					(tribe == TribeClass.GENERAL || tribe == TribeClass.F4GUARD_LIGHT))
				return false;
			break;
		case ASMODIANS:
			// Asmo Gate
			if (race == Race.PC_DARK_CASTLE_DOOR)
				return false;
			// Asmo General
			if (race == Race.GCHIEF_DARK)
				return false;
			// Asmo Teleporter
			if (race == Race.TELEPORTER && tribe == TribeClass.GENERAL_DARK)
				return false;
			// Elyos Shield generators
			if ((race == Race.CONSTRUCT || race == Race.BARRIER) &&
					(tribe == TribeClass.GENERAL_DARK || tribe == TribeClass.F4GUARD_DARK))
				return false;
			break;
		}
		return getRace() != race;
	}

	/**
	 * @return the summon
	 */
	public Summon getSummon() {
		return summon;
	}

	/**
	 * @param summon
	 *          the summon to set
	 */
	public void setSummon(Summon summon) {
		this.summon = summon;
	}

	/**
	 * @param new kisk to bind to (null if unbinding)
	 */
	public void setKisk(Kisk newKisk) {
		this.kisk = newKisk;
	}

	/**
	 * @return
	 */
	public Kisk getKisk() {
		return this.kisk;
	}

	/**
	 * @param delayId
	 * @return
	 */
	public boolean isItemUseDisabled(int delayId) {
		if (itemCoolDowns == null || !itemCoolDowns.containsKey(delayId))
			return false;

		Long coolDown = itemCoolDowns.get(delayId).getReuseTime();
		
		// Remove following code : coolDown can't be null because of previous check
		/*
		if (coolDown == null)
			return false;
		*/
		
		if (coolDown < System.currentTimeMillis()) {
			itemCoolDowns.remove(delayId);
			return false;
		}

		return true;
	}

	/**
	 * @param delayId
	 * @return
	 */
	public long getItemCoolDown(int delayId) {
		if (itemCoolDowns == null || !itemCoolDowns.containsKey(delayId))
			return 0;

		return itemCoolDowns.get(delayId).getReuseTime();
	}

	/**
	 * @return the itemCoolDowns
	 */
	public Map<Integer, ItemCooldown> getItemCoolDowns() {
		return itemCoolDowns;
	}

	/**
	 * @param delayId
	 * @param time
	 * @param useDelay
	 */
	public void addItemCoolDown(int delayId, long time, int useDelay) {
		if (itemCoolDowns == null)
			itemCoolDowns = new FastMap<Integer, ItemCooldown>().shared();

		itemCoolDowns.put(delayId, new ItemCooldown(time, useDelay));
	}

	/**
	 * @param itemMask
	 */
	public void removeItemCoolDown(int itemMask) {
		if (itemCoolDowns == null)
			return;
		itemCoolDowns.remove(itemMask);
	}

	/**
	 * @param isGagged
	 *          the isGagged to set
	 */
	public void setGagged(boolean isGagged) {
		this.isGagged = isGagged;
	}

	/**
	 * @return the isGagged
	 */
	public boolean isGagged() {
		return isGagged;
	}

	public void setQuestFollowingNpc(Npc npc) {
		questFollowingNpc = npc;
	}

	public Npc getQuestFollowingNpc() {
		return questFollowingNpc;
	}

	/**
	 * @return isAdminTeleportation
	 */
	public boolean getAdminTeleportation() {
		return isAdminTeleportation;
	}

	/**
	 * @param isAdminTeleportation
	 */
	public void setAdminTeleportation(boolean isAdminTeleportation) {
		this.isAdminTeleportation = isAdminTeleportation;
	}

	public final boolean isCoolDownZero() {
		return cooldownZero;
	}

	public final void setCoolDownZero(boolean cooldownZero) {
		this.cooldownZero = cooldownZero;
	}

	public void setPlayerResActivate(boolean isActivated) {
		this.isResByPlayer = isActivated;
	}

	public boolean getResStatus() {
		return isResByPlayer;
	}
	
	public void setResWithHalfSoulTime(boolean isActivated) {
		this.isResWithHalfSoulTime = isActivated;
	}

	public boolean getResWithHalfSoulTime() {
		return isResWithHalfSoulTime;
	}
	
	public void setIsFlyingBeforeDeath(boolean isActivated) {
		this.isFlyingBeforeDeath = isActivated;
	}

	public boolean getIsFlyingBeforeDeath() {
		return isFlyingBeforeDeath;
	}

	public com.aionemu.gameserver.model.team2.alliance.PlayerAlliance getPlayerAlliance2() {
		return playerAllianceGroup != null ? playerAllianceGroup.getAlliance() : null;
	}

	public PlayerAllianceGroup getPlayerAllianceGroup2() {
		return playerAllianceGroup;
	}

	public boolean isInAlliance2() {
		return playerAllianceGroup != null;
	}

	public void setPlayerAllianceGroup2(PlayerAllianceGroup playerAllianceGroup) {
		this.playerAllianceGroup = playerAllianceGroup;
	}

	public final boolean isInLeague(){
		return isInAlliance2() && getPlayerAlliance2().isInLeague();
	}

	public final boolean isInTeam() {
		return isInGroup2() || isInAlliance2();
	}

	/**
	 * @return current {@link PlayerGroup}, {@link PlayerAlliance} or null
	 */
	public final TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentTeam() {
		return isInGroup2() ? getPlayerGroup2() : getPlayerAlliance2();
	}

	/**
	 * @return current {@link PlayerGroup}, {@link PlayerAllianceGroup} or null
	 */
	public final TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentGroup() {
		return isInGroup2() ? getPlayerGroup2() : getPlayerAllianceGroup2();
	}

	/**
	 * @return current team id
	 */
	public final int getCurrentTeamId() {
		return isInTeam() ? getCurrentTeam().getTeamId() : 0;
	}

	/**
	 * @param worldId
	 * @return
	 */
	public PortalCooldownList getPortalCooldownList() {
		return portalCooldownList;
	}

	public CraftCooldownList getCraftCooldownList() {
		return craftCooldownList;
	}

	/**
	 * @author IlBuono
	 */
	public void setEditMode(boolean edit_mode) {
		this.edit_mode = edit_mode;
	}

	/**
	 * @author IlBuono
	 */
	public boolean isInEditMode() {
		return edit_mode;
	}

	public Npc getPostman() {
		return postman;
	}

	public void setPostman(Npc postman) {
		this.postman = postman;
	}

	public Account getPlayerAccount() {
		return playerAccount;
	}

	/**
	 * Quest completion
	 * 
	 * @param questId
	 * @return
	 */
	public boolean isCompleteQuest(int questId) {
		QuestState qs = getQuestStateList().getQuestState(questId);

		if (qs == null)
			return false;

		return qs.getStatus() == QuestStatus.COMPLETE;
	}

	public long getNextSkillUse() {
		return nextSkillUse;
	}

	public void setNextSkillUse(long nextSkillUse) {
		this.nextSkillUse = nextSkillUse;
	}

	public long getNextSummonSkillUse() {
		return nextSummonSkillUse;
	}

	public void setNextSummonSkillUse(long nextSummonSkillUse) {
		this.nextSummonSkillUse = nextSummonSkillUse;
	}

	/**
	 * chain skills
	 */
	public void setChainCategory(String chainCategory) {
		this.chainCategory = chainCategory;
	}
	public String getChainCategory() {
		return this.chainCategory;
	}
	public void setLastChainSkillTime() {
		this.lastChainSkillTime = System.currentTimeMillis();
	}
	public long getLastChainSkillTime() {
		return this.lastChainSkillTime;
	}
	public void setLastCounterSkill(AttackStatus status)	{
		long time = System.currentTimeMillis();		
		if(AttackStatus.getBaseStatus(status) == AttackStatus.DODGE &&
				PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.SCOUT) {
			this.lastCounterSkill.put(AttackStatus.DODGE, time);
		}
		else if(AttackStatus.getBaseStatus(status) == AttackStatus.PARRY && 
				(getPlayerClass() == PlayerClass.GLADIATOR || getPlayerClass() == PlayerClass.CHANTER)) {
			this.lastCounterSkill.put(AttackStatus.PARRY, time);
		}
		else if(AttackStatus.getBaseStatus(status) == AttackStatus.BLOCK &&
				PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.WARRIOR) {
			this.lastCounterSkill.put(AttackStatus.BLOCK, time);
		}
	}
	public long getLastCounterSkill(AttackStatus status)	{
		if(this.lastCounterSkill.get(status) == null)
			return 0;

		return this.lastCounterSkill.get(status);
	}

	/**
	 * @return the dualEffectValue
	 */
	public int getDualEffectValue() {
		return dualEffectValue;
	}


	/**
	 * @param dualEffectValue the dualEffectValue to set
	 */
	public void setDualEffectValue(int dualEffectValue) {
		this.dualEffectValue = dualEffectValue;
	}

	/**
	 * @return the Resurrection Positional State
	 */
	public boolean isInResPostState() {
		return this.isInResurrectPosState;
	}

	/**
	 * @param the
	 *          Resurrection Positional State to set
	 */
	public void setResPosState(boolean value) {
		this.isInResurrectPosState = value;
	}

	/**
	 * @param the
	 *          Resurrection Positional X value to set
	 */
	public void setResPosX(float value) {
		this.resPosX = value;
	}

	/**
	 * @return the Resurrection Positional X value
	 */
	public float getResPosX() {
		return this.resPosX;
	}

	/**
	 * @param the
	 *          Resurrection Positional Y value to set
	 */
	public void setResPosY(float value) {
		this.resPosY = value;
	}

	/**
	 * @return the Resurrection Positional Y value
	 */
	public float getResPosY() {
		return this.resPosY;
	}

	/**
	 * @param the
	 *          Resurrection Positional Z value to set
	 */
	public void setResPosZ(float value) {
		this.resPosZ = value;
	}

	/**
	 * @return the Resurrection Positional Z value
	 */
	public float getResPosZ() {
		return this.resPosZ;
	}

	/**
	 * @return true if player is under NoFly Effect
	 */
	public boolean isUnderNoFly() {
		return this.getEffectController().isAbnormalSet(AbnormalState.NOFLY);
	}

	/**
	 * @param the
	 *          status of NoFpConsum Effect
	 */
	public void setUnderNoFPConsum(boolean value) {
		this.underNoFPConsum = value;
	}

	/**
	 * @return true if player is under NoFpConsumEffect
	 */
	public boolean isUnderNoFPConsum() {
		return this.underNoFPConsum;
	}

	public void setInstanceStartPos(float instanceStartPosX, float instanceStartPosY, float instanceStartPosZ) {
		this.instanceStartPosX = instanceStartPosX;
		this.instanceStartPosY = instanceStartPosY;
		this.instanceStartPosZ = instanceStartPosZ;
	}

	public float getInstanceStartPosX() {
		return instanceStartPosX;
	}

	public float getInstanceStartPosY() {
		return instanceStartPosY;
	}

	public float getInstanceStartPosZ() {
		return instanceStartPosZ;
	}

	public boolean havePermission(byte perm) {
		return playerAccount.getMembership() >= perm;
	}

	/**
	 * @return Returns the emotions.
	 */
	public EmotionList getEmotions() {
		return emotions;
	}

	/**
	 * @param emotions
	 *          The emotions to set.
	 */
	public void setEmotions(EmotionList emotions) {
		this.emotions = emotions;
	}

	public int getRebirthResurrectPercent() {
		return rebirthResurrectPercent;
	}

	public void setRebirthResurrectPercent(int rebirthResurrectPercent) {
		this.rebirthResurrectPercent = rebirthResurrectPercent;
	}

	public BindPointPosition getBindPoint() {
		return bindPoint;
	}

	public void setBindPoint(BindPointPosition bindPoint) {
		this.bindPoint = bindPoint;
	}

	public byte speedHackValue;
	private int enterWindstream;
	private boolean inWindstream = false;

	@Override
	public ItemAttackType getAttackType() {
		Item weapon = getEquipment().getMainHandWeapon();
		if (weapon != null)
			return weapon.getItemTemplate().getAttackType();
		return ItemAttackType.PHYSICAL;
	}

	public long getFlyStartTime() {
		return this.flyStartTime;
	}

	public FlyPathEntry getCurrentFlyPath() {
		return flyLocationId;
	}
	
	public boolean isInvulnerableWing() {
		return this.isUnderInvulnerableWing;
	}

	public void setInvulnerableWing(boolean value) {
		this.isUnderInvulnerableWing = value;
	}

	public void resetAbyssRankListUpdated() {
		this.abyssRankListUpdateMask = 0;
	}

	public void setAbyssRankListUpdated(AbyssRankUpdateType type) {
		this.abyssRankListUpdateMask |= type.value();
	}

	public boolean isAbyssRankListUpdated(AbyssRankUpdateType type) {
		return (this.abyssRankListUpdateMask & type.value()) == type.value();
	}

	/**
	 * Methods pertaining to Windstreams
	 */
	public int getEnterWindstream() {
		return enterWindstream;
	}

	public void setEnterWindstream(int enterWindstream) {
		this.enterWindstream = enterWindstream;
	}

	public void addSalvationPoints(long points) {
		this.playerCommonData.addSalvationPoints(points);
		PacketSendUtility.sendPacket(this, new SM_STATS_INFO(this));
	}

	public boolean isInWindstream() {
		return inWindstream;
	}

	public void setInWindstream(boolean InWindstream) {
		this.inWindstream = InWindstream;
	}

	@Override
	public byte isPlayer() {
		if (this.isGM())
			return 2;
		else
			return 1;
	}

	/**
	 * @return the motions
	 */
	public MotionList getMotions() {
		return motions;
	}

	/**
	 * @param motions
	 *          the motions to set
	 */
	public void setMotions(MotionList motions) {
		this.motions = motions;
	}

	public void setTransformed(boolean value) {
		this.isTransformed = value;
	}

	public boolean isTransformed() {
		return this.isTransformed;
	}

	public ActionItemNpc getActionItemNpc() {
		return actionItemNpc;
	}

	/**
	 * @return the npcFactions
	 */
	public NpcFactions getNpcFactions() {
		return npcFactions;
	}

	/**
	 * @param npcFactions
	 *          the npcFactions to set
	 */
	public void setNpcFactions(NpcFactions npcFactions) {
		this.npcFactions = npcFactions;
	}

	public void setIsTeleporting(boolean b) {
		teleportedFlag = b;
	}

	public boolean isTeleporting() {
		return this.teleportedFlag;
	}

	/**
	 * @return the flyReuseTime
	 */
	public long getFlyReuseTime() {
		return flyReuseTime;
	}

	/**
	 * @param flyReuseTime
	 *          the flyReuseTime to set
	 */
	public void setFlyReuseTime(long flyReuseTime) {
		this.flyReuseTime = flyReuseTime;
	}

	/**
	 * @param the
	 *          flying mode flag to set
	 */
	public void setFlyingMode(boolean value) {
		this.isFlying = value;
	}

	/**
	 * @return true if player is in Flying mode
	 */
	public boolean isInFlyingMode() {
		return this.isFlying;
	}

	/**
	 * Stone Use Order determined by highest inventory slot. :( If player has two types, wrong one might be used.
	 * 
	 * @param player
	 * @return selfRezItem
	 */
	public Item getSelfRezStone() {
		Item item = null;
		item = getReviveStone(161001001);
		if (item == null)
			item = getReviveStone(161000003);
		if (item == null)
			item = getReviveStone(161000004);
		if (item == null)
			item = getReviveStone(161000001);
		return item;
	}

	/**
	 * @param stoneItemId
	 * @return stoneItem or null
	 */
	private Item getReviveStone(int stoneId) {
		Item item = getInventory().getFirstItemByItemId(stoneId);
		if (item != null && isItemUseDisabled(item.getItemTemplate().getDelayId()))
			item = null;
		return item;
	}

	/**
	 * Need to find how an item is determined as able to self-rez.
	 * 
	 * @return boolean can self rez with item
	 */
	public boolean haveSelfRezItem() {
		return (getSelfRezStone() != null);
	}

	/**
	 * Rebirth Effect is id 160.
	 * 
	 * @return
	 */
	public boolean haveSelfRezEffect() {
		if (getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES)
			return true;

		// Store the effect info.
		List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects) {
			for (EffectTemplate template : effect.getEffectTemplates()) {
				if (template.getEffectid() == 160) {
					if (template instanceof RebirthEffect) {
						RebirthEffect rebirthEffect = (RebirthEffect) template;
						setRebirthResurrectPercent(rebirthEffect.getResurrectPercent());
					}
					return true;
				}
			}
		}
		return false;
	}

	public void unsetResPosState() {
		if (isInResPostState()) {
			setResPosState(false);
			setResPosX(0);
			setResPosY(0);
			setResPosZ(0);
		}
	}

	public LootGroupRules getLootGroupRules() {
		if (isInGroup2()) {
			return getPlayerGroup2().getLootGroupRules();
		}
		if (isInAlliance2()) {
			return getPlayerAlliance2().getLootGroupRules();
		}
		return null;
	}

	public boolean isLooting() {
		return lootingNpcOid != 0;
	}

	public void setLootingNpcOid(int lootingNpcOid) {
		this.lootingNpcOid = lootingNpcOid;
	}

	public int getLootingNpcOid()	{
		return lootingNpcOid;
	}

	public final boolean isMentor() {
		return isMentor;
	}

	public final void setMentor(boolean isMentor) {
		this.isMentor = isMentor;
	}

	@Override
	public Race getRace() {
		return playerCommonData.getRace();
	}

	public Player findPartner() {
		return World.getInstance().findPlayer(partnerId);
	}

	private PlayerVarsDAO daoVars = DAOManager.getDAO(PlayerVarsDAO.class);
	private Map<String, Object> vars = FastMap.newInstance();

	public boolean hasVar(String key) {
		return vars.containsKey(key);
	}

	public void setVar(String key, Object value, boolean sql) {
		vars.put(key, value);
		if(sql) 
			daoVars.set(this.getObjectId(), key, value);
	}

	public void updateVar(String key, Object value, boolean sql) {
		vars.put(key, value);
		if(sql) 
			daoVars.update(this.getObjectId(), key, value);
	}
	
	public String getTimedVar(String key, long TimeafterExpir) {
		return daoVars.loadTimed(this.getObjectId(), key, TimeafterExpir);
	}

	public Object getVar(String key) {
		return this.vars.get(key);
	}
	
	public int getVarInt(String key) {
		Object o = this.vars.get(key);
		if(o != null)
			return Integer.parseInt(o.toString());
		return 0;
	}
	
	public String getVarStr(String key) {
		Object o = this.vars.get(key);
		if(o != null)
			return o.toString();
		return null;
	}

	public void setVars(Map<String, Object> map) {
		this.vars = map;
	}

	public boolean isMarried() {
		return partnerId != 0;
	}

	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}

	@Override
	public int getSkillCooldown(SkillTemplate template) {
		return isCoolDownZero() ? 0 : template.getCooldown();
	}

	@Override
	public int getItemCooldown(ItemTemplate template) {
		return isCoolDownZero() ? 0 : template.getDelayTime();
	}

	public void setLastMessageTime() {
		if ((System.currentTimeMillis() - lastMsgTime) / 1000 < CustomConfig.FLOOD_DELAY)
			floodMsgCount++;
		else
			floodMsgCount = 0;
		lastMsgTime = System.currentTimeMillis();
	}

	public int floodMsgCount() {
		return floodMsgCount;
	}

	public void setOnlineTime() {
		onlineTime = System.currentTimeMillis();
	}

	/*
	 * return online time in sec
	 */
	public long getOnlineTime() {
		return (System.currentTimeMillis() - onlineTime) / 1000;
	}

	public void setRebirthRevive(boolean rebirthRevive) {
		this.rebirthRevive = rebirthRevive;
	}

	public boolean canUseRebirthRevive() {
		return rebirthRevive;
	}

	public void setTelEffect(int teleportEid) {
		this.teleportEid = teleportEid;
	}

	public int getTelEffect() {
		return teleportEid;
	}

	/**
	 * Put up supplements to subtraction queue, so that when moving they would not decrease, need update as confirmation
	 * To update use updateSupplements()
	 */
	public void subtractSupplements(int count, int supplementId) {
		subtractedSupplementsCount = count;
		subtractedSupplementId = supplementId;
	}

	/**
	 * Update supplements in queue and clear the queue
	 */
	public void updateSupplements(boolean result) {
		if (subtractedSupplementId == 0 || subtractedSupplementsCount == 0)
			return;
		this.getInventory().decreaseByItemId(subtractedSupplementId, (int) (subtractedSupplementsCount * (result ? 1 : 0.4)));
		subtractedSupplementsCount = 0;
		subtractedSupplementId = 0;
	}

	public Object getTransformModel() {
		return null;
	}
	/**
	 * Custom
	 **/
	public int getEventTeamId() {
		return eventTeamId;
	}

	public void setEventTeamId(int eventTeamId) {
		this.eventTeamId = eventTeamId;
	}

	public boolean isNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(boolean newPlayer) {
		this.newPlayer = newPlayer;
	}
	
}