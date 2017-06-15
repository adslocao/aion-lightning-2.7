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
package com.aionemu.gameserver.model.gameobjects.player.npcFaction;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.factions.NpcFactionTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestMentorType;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.PacketSendUtility;


/**
 * @author MrPoke
 *
 */
public class NpcFactions {
	private Player owner;

	private Map<Integer, NpcFaction> factions = new HashMap<Integer, NpcFaction>();
	private NpcFaction[] activeNpcFaction = new NpcFaction[2];
	private int[] timeLimit = new int[]{0,0};

	/**
	 * @param owner
	 */
	public NpcFactions(Player owner) {
		this.owner = owner;
	}

	public void addNpcFaction(NpcFaction faction){
		factions.put(faction.getId(), faction);
		int type = 0;
		if (faction.isMentor())
			type = 1;
		
		if (faction.isActive())
			activeNpcFaction[type] = faction;
		if (timeLimit[type] < faction.getTime() && faction.getState() == ENpcFactionQuestState.COMPLETE)
			timeLimit[type] = faction.getTime();
		}
	
	public NpcFaction getNpcFactinById(int id){
		return factions.get(id);
	}

	public Collection<NpcFaction> getNpcFactions(){
		return factions.values();
	}

	public NpcFaction getActiveNpcFaction(boolean mentor){
		if (mentor){
			return activeNpcFaction[1];
		}
		else
			return activeNpcFaction[0];
	}

	public NpcFaction setActive(int npcFactionId){
		NpcFaction npcFaction = factions.get(npcFactionId);
		if (npcFaction == null){
			npcFaction = new NpcFaction(npcFactionId, 0, false, ENpcFactionQuestState.NOTING, 0);
			factions.put(npcFactionId, npcFaction);
		}
		npcFaction.setActive(true);
		if (npcFaction.isMentor())
			this.activeNpcFaction[1] = npcFaction;
		else
			this.activeNpcFaction[0] = npcFaction;
		return npcFaction;
	}

	public void leaveNpcFaction(Npc npc){
		int targetObjectId = npc.getObjectId();
		NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		if (npcFactionTemplate == null) {
			return;
		}
		NpcFaction npcFaction = getNpcFactinById(npcFactionTemplate.getId());
		if (npcFaction == null || !npcFaction.isActive()) {
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1438));
			return;
		}
		
		PacketSendUtility.sendPacket(owner, new SM_SYSTEM_MESSAGE(1300526, new DescriptionId(
			npcFactionTemplate.getNameId())));
		PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1353));

		npcFaction.setActive(false);
		activeNpcFaction[npcFactionTemplate.isMentor()? 1: 0] = null;
		switch(npcFaction.getState()){
			case START:
				QuestService.abandonQuest(owner, npcFaction.getQuestId());
				npcFaction.setState(ENpcFactionQuestState.NOTING);
		}
	}
	
	public void enterGuild(Npc npc) {
		int targetObjectId = npc.getObjectId();
		NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionByNpcId(npc.getNpcId());
		if (npcFactionTemplate == null) {
			return;
		}
		NpcFaction npcFaction = getNpcFactinById(npcFactionTemplate.getId());
		int npcFactionId = npcFactionTemplate.getId();

		if (owner.getLevel() < npcFactionTemplate.getMinLevel() || owner.getLevel() > npcFactionTemplate.getMaxLevel()) {
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1182));
			return;
		}
		if (owner.getRace() != npcFactionTemplate.getRace()) {
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1097));
			return;
		}
		if (npcFaction != null && npcFaction.isActive()) {
			PacketSendUtility.sendPacket(owner, new SM_SYSTEM_MESSAGE(1300525));
			return;
		}
		NpcFaction activeNpcFaction = getActiveNpcFaction(npcFactionTemplate.isMentor());
		if (activeNpcFaction != null && activeNpcFaction.getId() != npcFactionId) {
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1267));
			return;
		}
		if (npcFaction == null || !npcFaction.isActive()) {
			PacketSendUtility.sendPacket(owner, new SM_SYSTEM_MESSAGE(1300524, new DescriptionId(
				npcFactionTemplate.getNameId())));
			PacketSendUtility.sendPacket(owner, new SM_DIALOG_WINDOW(targetObjectId, 1012));
			setActive(npcFactionId);
			
			sendDailyQuest();
		}
	}

	public void startQuest(QuestTemplate questTemplate){
		NpcFaction npcFaction = activeNpcFaction[questTemplate.isMentor()? 1 :0];
		if (npcFaction == null)
			return;
		if (npcFaction.getState() != ENpcFactionQuestState.NOTING && npcFaction.getQuestId() == 0)
			return;
		npcFaction.setState(ENpcFactionQuestState.START);
	}
	
	public void abortQuest(QuestTemplate questTemplate){
		NpcFaction npcFaction = this.factions.get(questTemplate.getNpcFactionId());
		if (npcFaction == null || !npcFaction.isActive())
			return;
		npcFaction.setState(ENpcFactionQuestState.NOTING);
		sendDailyQuest();
	}
	
	public void completeQuest(QuestTemplate questTemplate){
		NpcFaction npcFaction = activeNpcFaction[questTemplate.isMentor()? 1 :0];
		if (npcFaction == null)
			return;
		npcFaction.setTime(getNextTime());
		npcFaction.setState(ENpcFactionQuestState.COMPLETE);
		this.timeLimit[npcFaction.isMentor()? 1: 0] = npcFaction.getTime();
		if (questTemplate.getMentorType() == QuestMentorType.MENTOR){
			owner.getCommonData().setMentorFlagTime((int)(System.currentTimeMillis()/1000)+60*60*24); //TODO 1 day
			PacketSendUtility.broadcastPacket(owner, new SM_TITLE_INFO(owner, true), false);
			PacketSendUtility.sendPacket(owner, new SM_TITLE_INFO(true));
		}
	}

	/**
	 * 
	 */
	public void sendDailyQuest() {
		for (int i = 0; i<2 ; i++){
			NpcFaction faction = activeNpcFaction[i];
			if (faction == null || !faction.isActive())
				continue;
			if (this.timeLimit[i] > System.currentTimeMillis()/1000)
				continue;
			int questId = 0;
			switch(faction.getState()){
				case COMPLETE:
					if (faction.getTime() > System.currentTimeMillis()/1000)
						continue;
					break;
				case START:
					continue;
				case NOTING:
					if (faction.getTime() > System.currentTimeMillis()/1000)
						questId = faction.getQuestId();
					break;
			}
			
			if (questId == 0){
				List<QuestTemplate> quests = DataManager.QUEST_DATA.getQuestsByNpcFaction(faction.getId(), owner);
				if (quests.isEmpty())
					continue;
				questId = quests.get(Rnd.get(quests.size())).getId();
				faction.setQuestId(questId);
				faction.setTime(getNextTime());
			}
			PacketSendUtility.sendPacket(owner, new SM_QUEST_ACTION(questId, true));
		}
	}

	public void onLevelUp(){
		for (int i = 0; i<2 ; i++){
			NpcFaction faction = activeNpcFaction[i];
			if (faction == null || !faction.isActive())
				continue;
			NpcFactionTemplate npcFactionTemplate = DataManager.NPC_FACTIONS_DATA.getNpcFactionById(faction.getId());
			if (npcFactionTemplate.getMaxLevel() < owner.getLevel()){
				faction.setActive(false);
				activeNpcFaction[i] = null;
				switch(faction.getState()){
					case START:
						QuestService.abandonQuest(owner, faction.getQuestId());
				}
				PacketSendUtility.sendPacket(owner, SM_SYSTEM_MESSAGE.STR_FACTION_LEAVE_BY_LEVEL_LIMIT(npcFactionTemplate.getNameId()));
				faction.setState(ENpcFactionQuestState.NOTING);
			}
		}
	}
	private int getNextTime(){
		Calendar repeatDate = Calendar.getInstance(); // current date
		repeatDate.set(Calendar.AM_PM, Calendar.AM);
		repeatDate.set(Calendar.HOUR, 9);
		repeatDate.set(Calendar.MINUTE, 0);
		repeatDate.set(Calendar.SECOND, 0); // current date 09:00
		if (repeatDate.getTime().getTime() < System.currentTimeMillis()) {
			repeatDate.add(Calendar.HOUR, 24); // can repeat next day
		}
		return (int) (repeatDate.getTimeInMillis()/1000);
	}
}
