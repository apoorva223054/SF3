/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

public class DayRevenue
{
	private List<RevenuePacket> packet;

	public List<RevenuePacket> getPacket()
	{
		return packet;
	}

	public void setPacket(List<RevenuePacket> packet)
	{
		this.packet = packet;
	}

}
