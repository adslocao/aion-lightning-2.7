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
package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestSkillData")
public class QuestSkillData {

	@XmlAttribute(name = "id", required = true)
	protected int skillId;
	@XmlAttribute(name = "start_var")
	protected int startVar = 0;
	@XmlAttribute(name = "end_var", required = true)
	protected int endVar;
	@XmlAttribute(name = "var_num")
	protected int varNum = 0;

	public int getSkillId() {
		return skillId;
	}

	public int getVarNum() {
		return varNum;
	}

	public int getStartVar() {
		return startVar;
	}

	public int getEndVar() {
		return endVar;
	}
}
