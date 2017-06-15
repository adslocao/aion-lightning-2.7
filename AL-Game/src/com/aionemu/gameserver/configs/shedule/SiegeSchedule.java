package com.aionemu.gameserver.configs.shedule;

import com.aionemu.commons.utils.xml.JAXBUtil;
import java.io.File;
import java.util.List;
import javax.xml.bind.annotation.*;
import org.apache.commons.io.FileUtils;


@XmlRootElement(name = "siege_schedule")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiegeSchedule {

	@XmlElement(name = "fortress", required = true)
	private List<Fortress> fortressList;

	public List<Fortress> getFortressList() {
		return fortressList;
	}

	public void setFortressList(List<Fortress> fortressList) {
		this.fortressList = fortressList;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "fortress")
	public static class Fortress {

		@XmlAttribute(required = true)
		private int id;

		@XmlElement(name = "siegeTime", required = true)
		private List<String> siegeTimes;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public List<String> getSiegeTimes() {
			return siegeTimes;
		}

		public void setSiegeTimes(List<String> siegeTimes) {
			this.siegeTimes = siegeTimes;
		}
	}

	public static SiegeSchedule load() {
		SiegeSchedule ss;
		try {
			String xml = FileUtils.readFileToString(new File("./config/shedule/siege_schedule.xml"));
			ss = JAXBUtil.deserialize(xml, SiegeSchedule.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize sieges", e);
		}
		return ss;
	}
}
