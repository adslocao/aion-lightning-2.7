package com.aionemu.commons.services.cron;

import com.aionlightning.commons.services.cron.RunnableRunner;

public class CurrentThreadRunnableRunner extends RunnableRunner {

	@Override
	public void executeRunnable(Runnable r) {
		r.run();
	}

	@Override
	public void executeLongRunningRunnable(Runnable r) {
		executeRunnable(r);
	}
}
