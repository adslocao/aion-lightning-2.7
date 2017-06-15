package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import javolution.util.FastMap;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.ReportToMany;

/**
 * @author Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportToManyData")
public class ReportToManyData extends XMLQuest {

	@XmlAttribute(name = "start_item_id")
	protected int startItemId;
	@XmlAttribute(name = "start_npc_id")
	protected int startNpcId;
	@XmlAttribute(name = "start_npc_id2")
	protected int startNpcId2;
	@XmlAttribute(name = "end_npc_id")
	protected int endNpc;
	@XmlAttribute(name = "end_npc_id2")
	protected int endNpc2;
	@XmlAttribute(name = "start_dialog_id")
	protected int startDialog;
	@XmlAttribute(name = "end_dialog_id")
	protected int endDialog;
	@XmlElement(name = "npc_infos", required = true)
	protected List<NpcInfos> npcInfos;

	@Override
	public void register(QuestEngine questEngine) {
		int maxVar = 0;
		FastMap<Integer, NpcInfos> NpcInfo = new FastMap<Integer, NpcInfos>();
		for (NpcInfos mi : npcInfos) {
			NpcInfo.put(mi.getNpcId(), mi);
			if (mi.getVar() > maxVar)
				maxVar = mi.getVar();
		}
		ReportToMany template = new ReportToMany(id, startItemId, startNpcId2, startNpcId, endNpc, endNpc2, NpcInfo, startDialog, endDialog,
			maxVar);
		questEngine.addQuestHandler(template);
	}
}
