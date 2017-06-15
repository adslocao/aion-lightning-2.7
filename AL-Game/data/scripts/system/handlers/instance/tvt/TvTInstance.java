package instance.tvt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.shedule.TvtSchedule;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.global.additions.MessagerAddition;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.instance.instancereward.TvtReward;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.academy.PvpZoneTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.services.SystemMailService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.services.tvt.TvtBossTemplate;
import com.aionemu.gameserver.services.tvt.TvtRegistrator;
import com.aionemu.gameserver.services.tvt.TvtService;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

import javolution.util.FastMap;




@InstanceID(300420000)
public class TvTInstance extends GeneralInstanceHandler {

    /**
     * Just a logger
     */
    private static final Logger log = LoggerFactory.getLogger(TvtService.class);
    protected TvtReward instanceReward;
    private Future<?> instanceTimer;
    private List<Npc> helpers = new ArrayList<Npc>();
    private FastMap<Integer, TvtBossTemplate> template = new FastMap<Integer, TvtBossTemplate>().shared();
    private long remainingTime;

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        instanceReward = new TvtReward(mapId, instanceId);
        super.onInstanceCreate(instance);
    }

    @Override
    public boolean isEnemy(Player effector, Creature effected) {
        if (effector == effected) {
            return false;
        }
        if (effected instanceof Player) {
            return isEnemyPlayer(effector, (Player) effected);
        } else if (effected instanceof Npc) {
            return isEnemyNpc(effector, (Npc) effected);
        }

        return true;
    }

    protected boolean isEnemyPlayer(Player effector, Player effected) {
        return effector.getRace() != effected.getRace();
    }
    
    @Override
    public boolean isEnemyPlayer(Creature effector, Creature effected){
        return ((Npc) effector).getObjectTemplate().getRace() != ((Player)effected).getRace() && !((Player)effected).isAggroIconTo(null); // Dont Know!Maybe effected, but method dont compatible
    }

    protected boolean isEnemyNpc(Player effector, Npc effected) {
        return effector.getRace() != effected.getObjectTemplate().getRace() && !effector.isAggroIconTo(effected);
    }

    @Override
    public void onEnterInstance(Player player) {
        player.getController().cancelCurrentSkill();
        cancelAvatar(player);
        player.getCommonData().setDp(4000);
        openFirstDoors(player);       
        if (instanceTimer == null) {
            final TvtRegistrator tvt = TvtService.getInstance().getTvtByLevel(player.getLevel());
            setRemainingTime(System.currentTimeMillis() + tvt.getDuration() * 1000);
            // schedule tvt stop
            instanceTimer = ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    stopTvt(tvt.getTvtId());
                }
            }, tvt.getDuration() * 1000 /*
                     * 30 * 1000
                     */);
        }
        
       // PacketSendUtility.sendSysMessage(player, "" + ((getRemainingTime() / 60) + 1));
        MessagerAddition.announce(player, "[Ascension]Tvt: Welcome to Tvt!Event over in " + ((getRemainingTime() / 60) + 1)+ "min.");
        PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(4, 0, getRemainingTime()));
    }

    private int getRemainingTime() {
        int result;
        result = (int) (remainingTime - System.currentTimeMillis()) / 1000;
        if (result < 0) {
            result = 0;
        }
        return result;
    }
    
    public void cancelAvatar(Player player)
    {
        for (com.aionemu.gameserver.skillengine.model.Effect ef : player.getEffectController().getAbnormalEffects()) {
				if (ef.isAvatar()) {
					ef.endEffect();
					player.getEffectController().clearEffect(ef);
				}
			}
    }
   

    public final void setRemainingTime(long paramLong) {
        this.remainingTime = paramLong;
    }

    @Override
    public void onLeaveInstance(Player player) {
        if (TvtService.getInstance().getTvtByLevel(player.getLevel()).getIsStarted()) {
            TvtService.getInstance().unRegPlayer(player);
            //PacketSendUtility.sendSysMessage(player, LanguageHandler.translateRU(CustomMessageIdRU.TVT_LEAVE));
            MessagerAddition.announce(player, "[Ascension]Tvt: You're leave the Tvt, you dont give the reward!");
        }
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        Summon summon = player.getSummon();
        if (summon != null) {
            summon.getController().release(SummonController.UnsummonType.UNSPECIFIED);
        }
        if (player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING)) {
            player.unsetState(CreatureState.FLYING);
            player.unsetState(CreatureState.GLIDING);
            player.setFlyState(0);
        }
        player.getEffectController().removeAllEffects();
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, lastAttacker == null ? 0 : lastAttacker.getObjectId()), true);

        PacketSendUtility.sendPacket(player, new SM_DIE(false, false, 0, 8));
        player.getObserveController().notifyDeathObservers(player);
        if (lastAttacker instanceof Summon) {
            addScore(((Player) lastAttacker).getSummon().getMaster());
        } else if (lastAttacker instanceof Player) {
            addScore((Player) lastAttacker);
        } else if (lastAttacker instanceof Npc) {
            addScore((Npc) lastAttacker);
        }
        if (getPoint(Race.ASMODIANS) - getPoint(Race.ELYOS) > 20 && helpers.isEmpty()) {
            spawnHelpers(player, Race.ELYOS);
        } else if (getPoint(Race.ELYOS) - getPoint(Race.ASMODIANS) > 20 && helpers.isEmpty()) {
            spawnHelpers(player, Race.ASMODIANS);
        } else if (getPoint(Race.ELYOS) == getPoint(Race.ASMODIANS) && !helpers.isEmpty()) {
            despawnHelpersNpc();
        }
        spawnBoss((Player) lastAttacker);
        return true;
    }

    private void spawnBoss(Player player) {
        TvtRegistrator tvt = TvtService.getInstance().getTvtByLevel(player.getLevel());
        TvtSchedule tvtSchedule = TvtService.getInstance().getTvtSchedule();
        for (TvtSchedule.TvtLevel l : tvtSchedule.getTvtLevelList()) {
            if (!l.getBoss()) {
                continue;
            }
            if (l.getId() != tvt.getTvtId()) {
                continue;
            }
            for (TvtSchedule.TvtBoss boss : l.getTvtBoss()) {
                if (getPoint(player.getRace()) == boss.getNeedScore()) {
                    for (String p : boss.getLoc()) {
                        StringTokenizer st = new StringTokenizer(p, ";");
                        if (st.countTokens() != 4) {
                            continue;
                        }
                        float x = Float.parseFloat(st.nextToken());
                        float y = Float.parseFloat(st.nextToken());
                        float z = Float.parseFloat(st.nextToken());
                        byte head = Byte.parseByte(st.nextToken());
                       // SpawnTemplate bossT = SpawnEngine.addNewSpawn(mapId, instanceId, boss.getBossId(), x, y, z, head, 0, 0, true);
                        SpawnTemplate bossT = SpawnEngine.addNewSpawn(mapId, boss.getBossId(), x, y, z, head, 0);
                        Npc npc = (Npc) SpawnEngine.spawnObject(bossT, instanceId);
                        TvtBossTemplate bossTmp = new TvtBossTemplate(boss.getReward(), boss.getVote(), boss.isCd(), boss.getAp(), boss.isBoost(), npc);
                        template.put(npc.getObjectId(), bossTmp);
                    }
                }
            }
        }
    }
/*
    private void spawnClock(Player player) {
        TvtRegistrator tvt = TvtService.getInstance().getTvtByLevel(player.getLevel());
        TvtSchedule tvtSchedule = TvtService.getInstance().getTvtSchedule();
        for (TvtSchedule.TvtLevel l : tvtSchedule.getTvtLevelList()) {
            if (!l.getBoss()) {
                continue;
            }
            if (l.getId() != tvt.getTvtId()) {
                continue;
            }

            for (TvtSchedule.TvtClock c : l.getTvtClock()) {
                if (player.getRace() != c.getRace()) {
                    for (String p : c.getLoc()) {
                        StringTokenizer st = new StringTokenizer(p, ";");
                        if (st.countTokens() != 4) {
                            continue;
                        }
                        float x = Float.parseFloat(st.nextToken());
                        float y = Float.parseFloat(st.nextToken());
                        float z = Float.parseFloat(st.nextToken());
                        byte head = Byte.parseByte(st.nextToken());
                       // SpawnTemplate clock = SpawnEngine.addNewSpawn(mapId, instanceId, c.getClockId(),  x, y, z, head, 0, 0, true);
                        SpawnTemplate clock = SpawnEngine.addNewSpawn(mapId, c.getClockId(), x, y, z, head, 0);
                        SpawnEngine.spawnObject(clock, instanceId);
                    }
                }
            }
        }
    }
*/
    private void despawnHelpersNpc() {
        for (VisibleObject obj : helpers) {
            obj.getController().delete();
        }
        helpers.clear();
    }

    @Override
    public void onDie(Npc npc) {
        VisibleObject obj = (VisibleObject) npc.getAggroList().getMostDamage();
        if (obj instanceof Player) {
            addScore((Player) obj);
        }
        TvtBossTemplate bossTmp = template.get(npc.getObjectId());
        if (bossTmp != null) {
            for (Player p : instance.getPlayersInside()) {
                Race race = null;
                if (obj instanceof Player) {
                    race = ((Player) obj).getRace();
                } else if (obj instanceof Npc) {
                    race = ((Npc) obj).getObjectTemplate().getRace();
                }
                if (race == null) {
                    return;
                }
                if (p.getRace() == race) {
                    if (bossTmp.getBoost()) {
                        onBoost(p);
                    }
                    if (bossTmp.getAp() > 0) {
                        AbyssPointsService.addAp(p, bossTmp.getAp());
                    }
                    if (bossTmp.getRemoveCd()) {
                        List<Integer> delayIds = new ArrayList<Integer>();
                        if (p.getSkillCoolDowns() != null) {
                            for (Map.Entry<Integer, Long> en : p.getSkillCoolDowns().entrySet()) {
                                delayIds.add(en.getKey());
                            }
                            for (Integer delayId : delayIds) {
                                p.setSkillCoolDown(delayId, 0);
                            }
                            delayIds.clear();
                            PacketSendUtility.sendPacket(p, new SM_SKILL_COOLDOWN(p.getSkillCoolDowns()));
                        }
                        if (p.getItemCoolDowns() != null) {
                            for (Map.Entry<Integer, ItemCooldown> en : p.getItemCoolDowns().entrySet()) {
                                delayIds.add(en.getKey());
                            }
                            for (Integer delayId : delayIds) {
                                p.addItemCoolDown(delayId, 0, 0);
                            }
                            delayIds.clear();
                            PacketSendUtility.sendPacket(p, new SM_ITEM_COOLDOWN(p.getItemCoolDowns()));
                        }
                    }
                }
            }
        }
        npc.getController().onDelete();
    }

    private void spawnHelpers(Player player, Race race) {
        TvtRegistrator tvt = TvtService.getInstance().getTvtByLevel(player.getLevel());
        TvtSchedule tvtSchedule = TvtService.getInstance().getTvtSchedule();
        for (TvtSchedule.TvtLevel l : tvtSchedule.getTvtLevelList()) {
            if (l.getId() != tvt.getTvtId()) {
                continue;
            }
            for (TvtSchedule.TvtHelpers h : l.getTvtHelpers()) {
                if (h.getRace().equals(race)) {
                    for (String p : h.getLoc()) {
                        StringTokenizer st = new StringTokenizer(p, ";");
                        if (st.countTokens() != 4) {
                            continue;
                        }
                        float x = Float.parseFloat(st.nextToken());
                        float y = Float.parseFloat(st.nextToken());
                        float z = Float.parseFloat(st.nextToken());
                        byte head = Byte.parseByte(st.nextToken());
                       // SpawnTemplate helper = SpawnEngine.addNewSpawn(mapId, instanceId, h.getHelperId(),  x, y, z, head, 0, 0, true);
                        SpawnTemplate helper = SpawnEngine.addNewSpawn(mapId, h.getHelperId(), x, y, z, head, 0);
                        helpers.add((Npc) SpawnEngine.spawnObject(helper, instanceId));
                    }
                }
            }
        }
    }

    @Override
    public boolean onReviveEvent(Player player) {
        int size = getPosition().size();
        int randomSize = Rnd.get(0, (size - 1));
        float x = getPosition().get(randomSize).getX();
        float y = getPosition().get(randomSize).getY();
        float z = getPosition().get(randomSize).getZ();
        byte heading = getPosition().get(randomSize).getH();
        teleport(player, x, y, z, heading);
        player.getController().stopProtectionActiveTask();
        /*
         * if you need use special skill for revive
         *
         * SkillTemplate template =
         * DataManager.SKILL_DATA.getSkillTemplate(AscensionConfig.TVT_SKILL_USE);
         * Effect e = new Effect(player, player, template, 1,
         * template.getEffectsDuration()); e.initialize(); e.applyEffect();
         *
         */
        return true;
    }

    private void onBoost(Player player) {
        SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(EventsConfig.TVT_SKILL_USE);
        Effect e = new Effect(player, player, skillTemplate, 1, skillTemplate.getEffectsDuration());
        e.initialize();
        e.applyEffect();
    }

    private void stopTvt(int tvtId) {
        log.info("[Ascension] Stopping tvt event of tvt id: " + tvtId);

        TvtRegistrator tvt;
        synchronized (this) {
            tvt = TvtService.getInstance().getActiveTvt().remove(tvtId);
        }

        if (tvt == null) {
            return;
        }

        tvt.despawnRegNpc();
        tvt.getHolders().clearAll();
        tvt.setIsStarted(false);
        onStopInstance();
    }

    @Override
    public void onInstanceDestroy() {
        if (instanceTimer != null) {
            instanceTimer.cancel(false);
        }
    }

    @Override
    public void onStopInstance() {
        sendEndMessage();
        doReward();
        for (Player p : instance.getPlayersInside()) {
            PacketSendUtility.sendPacket(p, new SM_QUEST_ACTION(4, 0, 0));
            int rndXportal = Rnd.get(1, 3);
            int rndYportal = Rnd.get(1, 4);
            switch (p.getRace()) {
                case ELYOS:
                    TeleportService.teleportTo(p, 110010000, (rndXportal + 1500), (rndYportal + 1511), 566, 3000, true);
                    break;
                case ASMODIANS:
                    TeleportService.teleportTo(p, 120010000, (rndXportal + 1356), (rndYportal + 1401), 208, 3000, true);
                    break;
            }
        }
    }

    private void doReward() {
        Influence inf = Influence.getInstance();
        for (Player p : instance.getPlayersInside()) {
            if (p.getRace() == getWinnerRace()) {
                float percent;
                if (getWinnerRace() == Race.ELYOS) {
                    percent = inf.getElyos();
                } else {
                    percent = inf.getAsmos();
                }
                if (p.getClientConnection().getAccount().getMembership() >= 1) {
                ItemService.addItem(p, EventsConfig.TVT_WINNER_REWARD, EventsConfig.TVT_WINNER_NUMBER + 2);
                    ItemService.addItem(p, EventsConfig.TVT_WINNER_DOUBLEREWARD, EventsConfig.TVT_WINNER_NUMBER + 2);
                    if (Rnd.get(100) > ((Math.round(100 - (100 * percent))) - 5)) {
                        mail(p);
                    }
                } else {
                    ItemService.addItem(p, EventsConfig.TVT_WINNER_REWARD, EventsConfig.TVT_WINNER_NUMBER);
                    ItemService.addItem(p, EventsConfig.TVT_WINNER_DOUBLEREWARD, EventsConfig.TVT_WINNER_NUMBER);
                    if (Rnd.get(100) > Math.round(100 - (100 * percent))) {
                        mail(p);
                    }
                }
            }
            if (p.isInGroup2()) {
                PlayerGroupService.removePlayer(p);
            }
        }
    }

    private void mail(Player player) {
        String senderName = "[Ascension]Tvt";
        String message = "Congratulations!You're Win on the [Ascension]Tvt Event!We wish you victory in the future! Good luck and pleasant hikes on Events!";
        String service = "[Ascension]";
        //SystemMailService.getInstance().sendMail(service, player.getName(), senderName, message, 0, 1, 0, false, "");
        SystemMailService.getInstance().sendMail(service, player.getName(), senderName, message, 0, 1, 0, false);
       // player.getCommonData().setMoney(DAOManager.getDAO(PlayerDAO.class).getMoney(player.getCommonData().getPlayerObjId()));
        //DAOManager.getDAO(PlayerDAO.class).setMoney(player.getCommonData().getMoney() + 1, player.getObjectId());
      //  player.getCommonData().setMoney(player.getCommonData().getMoney() + 1);
        //PacketSendUtility.sendPacket(player, new SM_INGAMESHOP_BALANCE(player.getCommonData().getMoney()));

    }

    private void openFirstDoors(Player player) {
        for (int i = 213; i < 225; i++) {
            PacketSendUtility.sendPacket(player, new SM_EMOTION(i));
        }
    }

    protected void teleport(Player player, float x, float y, float z, byte h) {
        if (player != null) {
            TeleportService.teleportTo(player, mapId, instanceId, x, y, z, h, 3000, true);
        } else {
            for (Player playerInside : instance.getPlayersInside()) {
                if (playerInside.isOnline()) {
                    TeleportService.teleportTo(playerInside, mapId, instanceId, x, y, z, h, 3000, true);
                }
            }
        }
    }

    protected void teleport(float x, float y, float z, byte h) {
        teleport(null, x, y, z, h);
    }

    private List<PvpZoneTemplate.PvpPosition> getPosition() {
        PvpZoneTemplate.PvpWorld world = DataManager.PVP_ZONE_DATA.getMapId(instance.getMapId());
        PvpZoneTemplate.PvpStage stageList = world.getPositionForStage(0);
        List<PvpZoneTemplate.PvpPosition> position = stageList.getPosition();
        return position;
    }

    private synchronized void addScore(Player p) {
        instanceReward.addScore(p);
        sendScoreMessage();
    }

    private synchronized void addScore(Npc n) {
        instanceReward.addScore(n);
        sendScoreMessage();
    }

    private int getPoint(Race race) {
        return instanceReward.getPoints(race);
    }

    private synchronized void sendScoreMessage() {
        for (Player pl : instance.getPlayersInside()) {
            if (pl.getRace() == Race.ASMODIANS) {
                PacketSendUtility.sendMessage(pl, "Elysea:" + getPoint(Race.ELYOS) + "| Asmodea:" + getPoint(Race.ASMODIANS));
            } else {
                PacketSendUtility.sendMessage(pl, "Asmodea:" + getPoint(Race.ASMODIANS) + "| Elysea:" + getPoint(Race.ELYOS));
            }
        }
    }

    private void sendEndMessage() {
        String message = "[Ascension]Tvt Winners: ";

        if (getWinnerRace() == Race.NONE) {
            message += "\nSorry, but these now was Winners!";
        } else {
            message += getWinnerRace().toString() + " !\n";
            message += "Congratulations!:";
            for (Player p : instance.getPlayersInside()) {
                if (p.getRace() == getWinnerRace()) {
                    message += "Character:" + p.getName();
                }
            }
            message += "[Ascension]Tvt: You have been teleporter in your Metropolis at 5 seconds";
        }
        for (Player p : instance.getPlayersInside()) {
            MessagerAddition.announce(p, message);
        }
    }

    public Race getWinnerRace() {
        if (getPoint(Race.ASMODIANS) == getPoint(Race.ELYOS)) {
            return Race.NONE;
        } else if (getPoint(Race.ASMODIANS) > getPoint(Race.ELYOS)) {
            return Race.ASMODIANS;
        } else {
            return Race.ELYOS;
        }
    }
}
