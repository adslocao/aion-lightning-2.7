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
package com.aionemu.gameserver.taskmanager.tasks;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import com.aionemu.gameserver.world.knownlist.VisitorWithOwner;

/**
 * @author ATracer
 */
public class MovementNotifyTask extends AbstractFIFOPeriodicTaskManager<Creature> {

	private static final class SingletonHolder {

		private static final MovementNotifyTask INSTANCE = new MovementNotifyTask();
	}

	public static MovementNotifyTask getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private final MoveNotifier MOVE_NOTIFIER = new MoveNotifier();

	public MovementNotifyTask() {
		super(500);
	}

	@Override
	protected void callTask(Creature creature) {
		creature.getKnownList().doOnAllNpcsWithOwner(MOVE_NOTIFIER);
	}

	@Override
	protected String getCalledMethodName() {
		return "notifyOnMove()";
	}

	private class MoveNotifier implements VisitorWithOwner<Npc, VisibleObject> {

		@Override
		public void visit(Npc object, VisibleObject owner) {
			object.getAi2().onCreatureEvent(AIEventType.CREATURE_MOVED, (Creature) owner);
		}

	}
}
