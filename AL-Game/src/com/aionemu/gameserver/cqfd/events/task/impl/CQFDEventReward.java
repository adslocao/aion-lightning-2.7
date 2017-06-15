package com.aionemu.gameserver.cqfd.events.task.impl;

import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventTeam;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTask;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class CQFDEventReward extends CQFDEventTask{
	CQFDEventTeam _team;
	public CQFDEventReward(CQFDEvent event, CQFDEventTeam team, Object[] params) {
		super(event, params);
		_team = team;
	}

	@Override
	public void run(){
		_team.rewardPlayer((Player)getParams()[0], 0);
		_team.rewardPlayer((Player)getParams()[1], 1);
	}
}
