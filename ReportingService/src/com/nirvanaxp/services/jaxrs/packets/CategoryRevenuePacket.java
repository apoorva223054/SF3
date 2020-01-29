/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class CategoryRevenuePacket
{
	private int id;
	private BigDecimal revenue;
	private String displayName;
	private int quantityOrdered;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public BigDecimal getRevenue()
	{
		return revenue;
	}

	public void setRevenue(BigDecimal revenue)
	{
		this.revenue = revenue;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getQuantityOrdered()
	{
		return quantityOrdered;
	}

	public void setQuantityOrdered(int quantityOrdered)
	{
		this.quantityOrdered = quantityOrdered;
	}

}
