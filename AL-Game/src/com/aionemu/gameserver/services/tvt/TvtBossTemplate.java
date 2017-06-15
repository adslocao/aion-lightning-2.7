package com.aionemu.gameserver.services.tvt;

import com.aionemu.gameserver.model.gameobjects.Npc;


public class TvtBossTemplate {

    private int reward;
    private int vote;
    private boolean removeCd;
    private int ap;
    private boolean boost;
    private Npc boss;

    public TvtBossTemplate(int reward, int vote, boolean removeCd, int ap, boolean boost, Npc boss) {
        this.removeCd = removeCd;
        this.reward = reward;
        this.vote = vote;
        this.ap = ap;
        this.boost = boost;
        this.boss = boss;
    }

    public int getReward() {
        return reward;
    }

    public boolean getRemoveCd() {
        return removeCd;
    }

    public int getAp() {
        return ap;
    }

    public boolean getBoost() {
        return boost;
    }
    
    public int getVote(){
        return vote;
    }

    public Npc getBoss() {
        return boss;
    }
}
