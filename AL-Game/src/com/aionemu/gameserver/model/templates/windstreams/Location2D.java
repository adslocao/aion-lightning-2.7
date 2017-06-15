package com.aionemu.gameserver.model.templates.windstreams;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author LokiReborn
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Location2D")
public class Location2D
{
	@XmlAttribute(name = "id")
	protected int	id;
	@XmlAttribute(name = "boost")
	protected int	boost;
	@XmlAttribute(name = "bidirectional")
	protected int	bidirectional;

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @return the boost
	 */
	public int getBoost()
	{
		return boost;
	}
	
	/**
	 * @return the bidirectional
	 */
	public int getBidirectional()
	{
		return bidirectional;
	}	
}

