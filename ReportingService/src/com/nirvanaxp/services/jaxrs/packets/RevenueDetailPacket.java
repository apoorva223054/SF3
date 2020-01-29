/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

public class RevenueDetailPacket
{

	List<RevenuePacket> dayHour;
	private int totalGuestCount;
	private float totalRevenue;
	private float averageRevenue;
	private int totalNumberOfOrders;

	public List<RevenuePacket> getDayHour()
	{
		return dayHour;
	}

	public void setDayHour(List<RevenuePacket> dayHour)
	{
		this.dayHour = dayHour;
	}

	public int getTotalGuestCount()
	{
		return totalGuestCount;
	}

	public void setTotalGuestCount(int totalGuestCount)
	{
		this.totalGuestCount = totalGuestCount;
	}

	public float getTotalRevenue()
	{
		return totalRevenue;
	}

	public void setTotalRevenue(float totalRevenue)
	{
		this.totalRevenue = totalRevenue;
	}

	public float getAverageRevenue()
	{
		return averageRevenue;
	}

	public void setAverageRevenue(float averageRevenue)
	{
		this.averageRevenue = averageRevenue;
	}

	public int getTotalNumberOfOrders()
	{
		return totalNumberOfOrders;
	}

	public void setTotalNumberOfOrders(int totalNumberOfOrders)
	{
		this.totalNumberOfOrders = totalNumberOfOrders;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "RevenueDetailPacket [dayHour=" + (dayHour != null ? dayHour.subList(0, Math.min(dayHour.size(), maxLen)) : null) + ", totalGuestCount=" + totalGuestCount + ", totalRevenue="
				+ totalRevenue + ", averageRevenue=" + averageRevenue + ", totalNumberOfOrders=" + totalNumberOfOrders + "]";
	}
}
