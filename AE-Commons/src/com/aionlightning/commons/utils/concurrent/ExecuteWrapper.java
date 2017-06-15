/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.commons.utils.concurrent;

import com.aionlightning.commons.configs.CommonsConfig;

import javolution.text.TextBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author NB4L1
 */
public class ExecuteWrapper implements Executor {

	private static final Logger log = LoggerFactory.getLogger(ExecuteWrapper.class);

	@Override
	public void execute(Runnable runnable) {
		execute(runnable, Long.MAX_VALUE);
	}

	public static void execute(Runnable runnable, long maximumRuntimeInMillisecWithoutWarning) {
		long begin = System.nanoTime();

		try {
			runnable.run();
		} catch (Throwable t) {
			log.warn("Exception in a Runnable execution:", t);
		} finally {

			long runtimeInNanosec = System.nanoTime() - begin;
			Class<? extends Runnable> clazz = runnable.getClass();

			if (CommonsConfig.RUNNABLESTATS_ENABLE) {
				RunnableStatsManager.handleStats(clazz, runtimeInNanosec);
			}

			long runtimeInMillisec = TimeUnit.NANOSECONDS.toMillis(runtimeInNanosec);
			if (runtimeInMillisec > maximumRuntimeInMillisecWithoutWarning) {
				TextBuilder tb = TextBuilder.newInstance();
				tb.append(clazz);
				tb.append(" - execution time: ");
				tb.append(runtimeInMillisec);
				tb.append("msec");
				log.warn(tb.toString());
			}
		}
	}
}
