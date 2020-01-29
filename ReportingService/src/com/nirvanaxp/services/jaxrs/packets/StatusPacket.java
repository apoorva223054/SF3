/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class StatusPacket
{
	private String statusName;
	private String statusDisplayName;
	private String time;

	public String getStatusName()
	{
		return statusName;
	}

	public void setStatusName(String statusName)
	{
		this.statusName = statusName;
	}

	public String getStatusDisplayName()
	{
		return statusDisplayName;
	}

	public void setStatusDisplayName(String statusDisplayName)
	{
		this.statusDisplayName = statusDisplayName;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "StatusPacket [statusName=" + statusName + ", statusDisplayName=" + statusDisplayName + ", time=" + time + "]";
	}

}
