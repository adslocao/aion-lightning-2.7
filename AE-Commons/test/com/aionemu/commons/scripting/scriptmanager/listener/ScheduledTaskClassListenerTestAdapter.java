package com.aionemu.commons.scripting.scriptmanager.listener;

import com.aionlightning.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionlightning.commons.services.CronService;

public class ScheduledTaskClassListenerTestAdapter extends ScheduledTaskClassListener {

	private final CronService cronService;

	public ScheduledTaskClassListenerTestAdapter(CronService cronService) {
		this.cronService = cronService;
	}

	@Override
	protected CronService getCronService() {
		return cronService;
	}
}
