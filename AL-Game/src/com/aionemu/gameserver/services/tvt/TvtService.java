package com.aionemu.gameserver.services.tvt;

import com.aionemu.commons.services.CronService;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javolution.util.FastMap;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.shedule.TvtSchedule;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.templates.academy.PvpZoneTemplate;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.WorldMapInstanceFactory;
import com.aionemu.gameserver.world.knownlist.Visitor;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TvtService extends InstanceService {

    /**
     * Just a logger
     */
    private static final Logger log = LoggerFactory.getLogger(TvtService.class);
    /**
     * Singleton that is loaded on the class initialization. Guys, we really do
     * not SingletonHolder classes
     */
    private static final TvtService instance = new TvtService();
    private TvtSchedule tvtSchedule;
    private static final String TVT_LOOP_STATUS_BROADCAST_SCHEDULE = "0 0 * ? * *";
    private final Map<Integer, TvtRegistrator> activeTvt = new FastMap<Integer, TvtRegistrator>().shared();
    private final Map<Byte, TvtRegistrator> activeTvtForLevel = new FastMap<Byte, TvtRegistrator>().shared();
    private WorldMapInstance pvpArenaMap;
    /**
     * Returns the single instance of siege service
     *
     * @return siege service instance
     */
    public static TvtService getInstance() {
        return instance;
    }

    public void initTvt() {
        if (!EventsConfig.TVT_ENABLE) {
            return;
        }

        tvtSchedule = TvtSchedule.load();

        for (TvtSchedule.TvtLevel l : tvtSchedule.getTvtLevelList()) {
            for (String tvtTime : l.getTvtTimes()) {
                CronService.getInstance().schedule(new TvtStartRunnable(l.getId(), l.getStartTime(), l.getDuration(), l.getLevel(), l.getMapId()), tvtTime);
                log.info("[Ascension] Scheduled tvt of id " + l.getId() + " based on cron expression: " + tvtTime);
            }
        }

        updateNextStage();

        // Schedule tvt status broadcast (every hour)
        CronService.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                updateNextStage();
            }
        }, TVT_LOOP_STATUS_BROADCAST_SCHEDULE);
        log.info("[Ascension] Broadcasting Tvt Begining status based on expression: " + TVT_LOOP_STATUS_BROADCAST_SCHEDULE);

    }

    public void startTvt(final int tvtId, final int startTime, final int duration, final int level, final int mapId) {
        log.info("[Ascension] Starting tvt registration of tvt id: " + tvtId);
        TvtRegistrator tvt;
        synchronized (this) {
            if (activeTvt.containsKey(tvtId)) {
                log.error("[Ascension] Attempt to start tvt twice for tvt id: " + tvtId);
                return;
            }
            tvt = newTvt(tvtId, startTime, duration, level, mapId);
            activeTvt.put(tvtId, tvt);
            activeTvtForLevel.put((byte) tvt.getLevel(), tvt);
        }

        // schedule tvt start
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                startEvent(tvtId);
            }
        }, tvt.getStartTime() * 1000 /*
                 * 30 * 1000
                 */);
        log.info("[Ascension] Spawning registration npc for tvt id: " + tvtId);
        tvt.spawnRegNpc();
        sendWelcomeMeaasge();
        timeForBegin(tvt);
    }

    @Deprecated
    private void timeForBegin(final TvtRegistrator tvt) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                sendMessage(tvt);
                if (tvt.getRemainingTime() > 0) {
                    timeForBegin(tvt);
                }
            }
        }, 60000);
    }

    private void startEvent(final int tvtId) {
        log.info("[Ascension] Starting tvt event of tvt id: " + tvtId);
        TvtRegistrator tvt;
        synchronized (this) {
            tvt = activeTvt.get(tvtId);
        }
        if (tvt == null) {
            return;
        }

        pvpArenaMap = (WorldMapInstance) getNextJSUnionInstance(tvt.getMapId());
        tvt.getHolders().makeGroup();
        tvt.despawnRegNpc();
    }

    public void forceStopTvt(int tvtId) {
        log.info("[Ascension] Forced stopping tvt event of tvt id: " + tvtId);

        TvtRegistrator tvt;
        synchronized (this) {
            tvt = activeTvt.remove(tvtId);
        }

        if (tvt == null) {
            return;
        }

        tvt.getHolders().clearAll();
        tvt.setIsStarted(false);
    }

    public Map<Integer, TvtRegistrator> getActiveTvt() {
        return activeTvt;
    }

    public boolean regPlayer(Player player) {
        TvtRegistrator tvt;
        synchronized (this) {
            tvt = activeTvtForLevel.get(player.getLevel());
        }
        if (tvt == null) {
            TvtService.sendPublicMsg(player, "[Ascension]Tvt: Sorry, Not found Tvt for Your Level!.");
            return false;
        }
        if (tvt.getHolders().addPlayer(player)) {
        	TvtService.sendPublicMsg(player, "[Ascension]Tvt: You have been registered!");
            return true;
        }
        return false;
    }

    public void unRegPlayer(Player player) {
        TvtRegistrator tvt;
        synchronized (this) {
            tvt = activeTvtForLevel.get(player.getLevel());
        }
        if (tvt == null) {
            return;
        }
        tvt.getHolders().removePlayer(player);
    }

    public void info(Player player, boolean isAdmin) {
        TvtRegistrator tvt;
        synchronized (this) {
            tvt = activeTvtForLevel.get(player.getLevel());
        }
        if (tvt == null) {
            return;
        }
        tvt.getHolders().info(player, isAdmin);
    }

    public void portPlayer(TvtRegistrator tvt) {
        PvpZoneTemplate.PvpWorld world = DataManager.PVP_ZONE_DATA.getMapId(tvt.getMapId());
        if (world == null) {
            return;
        }
        PvpZoneTemplate.PvpStage list = world.getPositionForStage(0);
        if (list == null) {
            return;
        }
        List<PvpZoneTemplate.PvpPosition> position = list.getPosition();
        int e = 0;
        int a = 6;

        if (tvt.getHolders().getGroups() != null) {
            for (PlayerGroup group : tvt.getHolders().getGroups()) {
                registerGroupWithInstance(pvpArenaMap, group);
            }
        } else //only for GM and for Test 
        {
            for (Player player : tvt.getHolders().getPlayers()) {
                registerPlayerWithInstance(pvpArenaMap, player);
            }
        }

        for (Player player : tvt.getHolders().getPlayers()) {
            if ((!player.isInGroup2() && !player.isGM()) || player == null) {
                continue;
            }

            if (player.getKisk() != null) {
                player.getKisk().removePlayer(player);
                player.setKisk(null);
            }
            switch (player.getRace()) {
                case ELYOS:
                    TeleportService.teleportTo(player, tvt.getMapId(), pvpArenaMap.getInstanceId(),
                            position.get(e).getX(), position.get(e).getY(), position.get(e).getZ(), 3000, true);
                    e++;
                    if (e >= 5) {
                        e = 0;
                    }
                    break;
                case ASMODIANS:
                    TeleportService.teleportTo(player, tvt.getMapId(), pvpArenaMap.getInstanceId(),
                            position.get(a).getX(), position.get(a).getY(), position.get(a).getZ(), 3000, true);
                    a++;
                    if (a >= 11) {
                        a = 0;
                    }
                    break;

            }
        }
        tvt.getHolders().clearPlayer();
    }

    private TvtRegistrator newTvt(int id, int st, int d, int l, int m) {
        return new TvtRegistrator(id, st, d, l, m);
    }

    public static void sendPublicMsg(Player p, String message) {
        PacketSendUtility.sendWhiteMessageOnCenter(p, message);
    }

    public void sendWelcomeMeaasge() {
        World.getInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                switch (player.getRace()) {
                    case ASMODIANS:
                    	PacketSendUtility.sendWhiteMessage(player, "[Ascension]Tvt: Manager Rukbar Waiting in Pandaemonium.");
                        break;
                    case ELYOS:
                    	PacketSendUtility.sendWhiteMessage(player, "[Ascension]Tvt: Manager Hippotades Waiting in Sanctum");
                        break;
                }
            }
        });
    }

    protected void updateNextStage() {
        // filter tvt start runnables
        Map<Runnable, JobDetail> tvtStartRunables = CronService.getInstance().getRunnables();
        tvtStartRunables = Maps.filterKeys(tvtStartRunables, new Predicate<Runnable>() {

            @Override
            public boolean apply(@Nullable Runnable runnable) {
                return runnable instanceof TvtStartRunnable;
            }
        });

        // Create map TvtLevel-To-AllTriggers
        Map<Integer, List<Trigger>> tvtIdTotvtStartTriggers = Maps.newHashMap();
        for (Map.Entry<Runnable, JobDetail> entry : tvtStartRunables.entrySet()) {
            TvtStartRunnable fssr = (TvtStartRunnable) entry.getKey();

            List<Trigger> storage = tvtIdTotvtStartTriggers.get(fssr.getTvtId());
            if (storage == null) {
                storage = Lists.newArrayList();
                tvtIdTotvtStartTriggers.put(fssr.getTvtId(), storage);
            }
            storage.addAll(CronService.getInstance().getJobTriggers(entry.getValue()));
        }
    }

    public TvtSchedule getTvtSchedule() {
        return tvtSchedule;
    }

    public TvtRegistrator getTvt(Integer tvtId) {
        return activeTvt.get(tvtId);
    }

    public TvtRegistrator getTvtByLevel(Byte level) {
        return activeTvtForLevel.get(level);
    }

    private WorldMapInstance getNextJSUnionInstance(int worldId) {
        WorldMap map = World.getInstance().getWorldMap(worldId);

        if (!map.isInstanceType()) {
            throw new UnsupportedOperationException("Invalid call for next available instance  of " + worldId);
        }

        int nextInstanceId = map.getNextInstanceId();

        log.info("[TVT] Creating new Tvt instance: " + nextInstanceId);

        WorldMapInstance pvpArenaInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
        InstanceService.startInstanceChecker(pvpArenaInstance);
        map.addInstance(nextInstanceId, pvpArenaInstance);
        SpawnEngine.spawnInstance(worldId, pvpArenaInstance.getInstanceId());
        InstanceEngine.getInstance().onInstanceCreate(pvpArenaInstance);
        return pvpArenaInstance;
    }

    private void sendMessage(final TvtRegistrator tvt) {
        switch (tvt.getRemainingTime() / 60) {
            case 20:
            case 15:
            case 10:
            case 5:
            case 2:
            case 1:
                World.getInstance().doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player player) {
                        if (!tvt.getHolders().getPlayer(player) && player.getCommonData().getLevel() >= tvt.getLevel()) {
                            switch (player.getRace()) {
                                case ASMODIANS:
                                	PacketSendUtility.sendWhiteMessageOnCenter(player, "[Ascension]Tvt: Registration will be ending for: " + tvt.getRemainingTime() / 60 + " minutes.\nAsmodians:"+ tvt.getHolders().getAsmoReg() +" | Elyos:"+tvt.getHolders().getElysReg());
                                    break;
                                case ELYOS:
                                	PacketSendUtility.sendWhiteMessageOnCenter(player, "[Ascension]Tvt: Registration will be ending for: " + tvt.getRemainingTime() / 60 + " minutes.\nAsmodians:"+ tvt.getHolders().getAsmoReg() +" | Elyos:"+tvt.getHolders().getElysReg());
                                    break;
                            }
                        }
                    }
                });
                break;
        }
    }
}