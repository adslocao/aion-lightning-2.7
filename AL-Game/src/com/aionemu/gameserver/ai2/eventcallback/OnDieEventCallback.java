package com.aionemu.gameserver.ai2.eventcallback;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.event.AIEventType;

/**
 * Callback for {@link AIEventType#DIED} event
 *
 * @author SoulKeeper
 */
public abstract class OnDieEventCallback extends OnHandleAIGeneralEvent {

	@Override
	protected void onAIHandleGeneralEvent(AbstractAI obj, AIEventType eventType) {
		if (AIEventType.DIED == eventType) {
			onDie(obj);
		}
	}

	public abstract void onDie(AbstractAI obj);
}
