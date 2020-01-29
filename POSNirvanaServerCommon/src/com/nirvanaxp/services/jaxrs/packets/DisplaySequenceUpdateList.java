/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "DisplaySequenceUpdateList")
public class DisplaySequenceUpdateList extends PostPacket
{

	private List<DisplaySequenceData> displaySequenceData;
	private String tableName;

	public List<DisplaySequenceData> getDisplaySequenceData()
	{
		return displaySequenceData;
	}

	public void setDisplaySequenceData(List<DisplaySequenceData> displaySequenceData)
	{
		this.displaySequenceData = displaySequenceData;
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	@Override
	public String toString() {
		return "DisplaySequenceUpdateList [displaySequenceData="
				+ displaySequenceData + ", tableName=" + tableName + "]";
	}

}
