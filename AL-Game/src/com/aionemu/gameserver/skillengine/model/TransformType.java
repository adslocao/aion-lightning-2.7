package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author kecimis
 */

@XmlType(name = "TransformType")
@XmlEnum
public enum TransformType {
	FORM1,
	AVATAR,
	PC,
	NONE
}
