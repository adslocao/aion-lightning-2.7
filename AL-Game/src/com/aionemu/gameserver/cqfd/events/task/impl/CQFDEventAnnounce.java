package com.aionemu.gameserver.cqfd.events.task.impl;

import java.util.Collection;

import com.aionemu.gameserver.cqfd.events.CQFDEvent;
import com.aionemu.gameserver.cqfd.events.CQFDEventTeam;
import com.aionemu.gameserver.cqfd.events.task.CQFDEventTask;
import com.aionemu.gameserver.cqfd.events.tools.eventTools;

public class CQFDEventAnnounce extends CQFDEventTask{
		private Collection<CQFDEventTeam> _teams;
		private String _message;
		public CQFDEventAnnounce(Collection<CQFDEventTeam> teams, CQFDEvent event, String message){
			super(event);
			_teams = teams;
			_message = message;
		}
		
		public CQFDEventAnnounce(Collection<CQFDEventTeam> teams, CQFDEvent event, String message, long init){
			super(event, init);
			_teams = teams;
			_message = message;
		}
		
		public CQFDEventAnnounce(Collection<CQFDEventTeam> teams, CQFDEvent event, String message, long init, long repeatDelay){
			super(event, init);
			_teams = teams;
			_message = message;
			setDelay(repeatDelay);
		}
		@Override
		public void run(){
			eventTools.announce(_teams, _message);
		}
}
