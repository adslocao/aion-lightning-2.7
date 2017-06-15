/**
 * This file is part of aion-lightning <aion-lightning.org>.
 * 
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.aionlightning.commons.database.DatabaseFactory;
import com.aionlightning.commons.database.dao.DAOManager;
import com.aionlightning.commons.services.CronService;
import com.aionlightning.commons.utils.AEInfos;
import com.aionlightning.commons.utils.ExitCode;
import com.aionlightning.loginserver.configs.Config;
import com.aionlightning.loginserver.controller.BannedIpController;
import com.aionlightning.loginserver.controller.PremiumController;
import com.aionlightning.loginserver.dao.BannedMacDAO;
import com.aionlightning.loginserver.network.NetConnector;
import com.aionlightning.loginserver.network.ncrypt.KeyGen;
import com.aionlightning.loginserver.service.PlayerTransferService;
import com.aionlightning.loginserver.taskmanager.TaskFromDBManager;
import com.aionlightning.loginserver.utils.DeadLockDetector;
import com.aionlightning.loginserver.utils.ThreadPoolManager;
import com.aionlightning.loginserver.utils.cron.ThreadPoolManagerRunnableRunner;

/**
 * @author -Nemesiss-
 */
public class LoginServer {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginServer.class);

	private static void initalizeLoggger() {
		new File("./log/backup/").mkdirs();
		File[] files = new File("log").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});

		if (files != null && files.length > 0) {
			byte[] buf = new byte[1024];
			try {
				String outFilename = "./log/backup/" + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".zip";
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
				out.setMethod(ZipOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_COMPRESSION);

				for (File logFile : files) {
					FileInputStream in = new FileInputStream(logFile);
					out.putNextEntry(new ZipEntry(logFile.getName()));
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.closeEntry();
					in.close();
					logFile.delete();
				}
				out.close();
			} catch (IOException e) {
			}
		}
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("config/slf4j-logback.xml");
		} catch (JoranException je) {
			throw new RuntimeException("Failed to configure loggers, shutting down...", je);
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		long start = System.currentTimeMillis();

		initalizeLoggger();
		CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);
		(new ServerCommandProcessor()).start();  // *PJ Launch the server command processor thread   
		//write a timestamp that can be used by TruncateToZipFileAppender
		log.info("\f" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis())) + "\f");
		Config.load();
		DatabaseFactory.init();
		DAOManager.init();

		/**
		 * Start deadlock detector that will restart server if deadlock happened
		 */
		new DeadLockDetector(60, DeadLockDetector.RESTART).start();
		ThreadPoolManager.getInstance();

		/**
		 * Initialize Key Generator
		 */
		try {
			KeyGen.init();
		} catch (Exception e) {
			log.error("Failed initializing Key Generator. Reason: " + e.getMessage(), e);
			System.exit(ExitCode.CODE_ERROR);
		}

		GameServerTable.load();
		BannedIpController.start();
		DAOManager.getDAO(BannedMacDAO.class).cleanExpiredBans();

		NetConnector.getInstance().connect();
		PlayerTransferService.getInstance();
		TaskFromDBManager.getInstance();

		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

		AEInfos.printAllInfos();

		PremiumController.getController();
		
		log.info("AL Login Server started in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
	}
}
