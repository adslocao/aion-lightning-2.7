/*
 * This file is part of the requirements for the Illusion Gate Skill.
 * Code References from ATracer's Trap.java of Aion-Unique
 */
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author LokiReborn
 */
public class GroupGate extends SummonedObject {

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public GroupGate(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		super(objId, controller, spawnTemplate, objectTemplate, (byte) 1);
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return getCreator().isEnemy(creature);
	}

	/**
	 * @return NpcObjectType.GROUPGATE
	 */
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.GROUPGATE;
	}
}
