/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class RevenuePacketForPieChart
{
	private int totalOrders;
	private BigDecimal totalRevenue;
	private int totalGuest;
	private BigDecimal averageRevenue;
	private String cardType;
	private int typeId;

	public int getTypeId()
	{
		return typeId;
	}

	public void setTypeId(int typeId)
	{
		this.typeId = typeId;
	}

	public int getTotalOrders()
	{
		return totalOrders;
	}

	public void setTotalOrders(int totalOrders)
	{
		this.totalOrders = totalOrders;
	}

	public BigDecimal getTotalRevenue()
	{
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue)
	{
		this.totalRevenue = totalRevenue;
	}

	public int getTotalGuest()
	{
		return totalGuest;
	}

	public void setTotalGuest(int totalGuest)
	{
		this.totalGuest = totalGuest;
	}

	public BigDecimal getAverageRevenue()
	{
		return averageRevenue;
	}

	public void setAverageRevenue(BigDecimal averageRevenue)
	{
		this.averageRevenue = averageRevenue;
	}

	public String getCardType()
	{
		return cardType;
	}

	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	@Override
	public String toString()
	{
		return "RevenuePacketForPieChart [totalOrders=" + totalOrders + ", totalRevenue=" + totalRevenue + ", totalGuest=" + totalGuest + ", averageRevenue=" + averageRevenue + ", cardType="
				+ cardType + ", typeId=" + typeId + "]";
	}
}
