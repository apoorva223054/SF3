/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class RevenuePacket
{

	private float total;
	private float totalRevenue;
	private int totalVisits;
	private String timeDuration;
	private float balanceDue;
	private float revenuePerCover;

	public int getTotalVisits()
	{
		return totalVisits;
	}

	public void setTotalVisits(int totalVisits)
	{
		this.totalVisits = totalVisits;
	}

	public String getTimeDuration()
	{
		return timeDuration;
	}

	public void setTimeDuration(String timeDuration)
	{
		this.timeDuration = timeDuration;
	}

	public float getRevenuePerCover()
	{
		return revenuePerCover;
	}

	public void setRevenuePerCover(float revenuePerCover)
	{
		this.revenuePerCover = revenuePerCover;
	}

	public float getTotalRevenue()
	{
		return totalRevenue;
	}

	public void setTotalRevenue(float totalRevenue)
	{
		this.totalRevenue = totalRevenue;
	}

	public float getBalanceDue()
	{
		return balanceDue;
	}

	public void setBalanceDue(float balanceDue)
	{
		this.balanceDue = balanceDue;
	}

	public float getTotal()
	{
		return total;
	}

	public void setTotal(float total)
	{
		this.total = total;
	}

	@Override
	public String toString()
	{
		return "RevenuePacket [total=" + total + ", totalRevenue=" + totalRevenue + ", totalVisits=" + totalVisits + ", timeDuration=" + timeDuration + ", balanceDue=" + balanceDue
				+ ", revenuePerCover=" + revenuePerCover + "]";
	}
}
