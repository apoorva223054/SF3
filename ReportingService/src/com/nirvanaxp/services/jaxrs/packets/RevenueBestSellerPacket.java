/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class RevenueBestSellerPacket implements Comparable<RevenueBestSellerPacket>
{
	private int id;
	private BigDecimal priceSelling;
	private int quantityOrdered;
	private String displayName;
	private BigDecimal totalRevenue;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public BigDecimal getPriceSelling()
	{
		return priceSelling;
	}

	public void setPriceSelling(BigDecimal priceSelling)
	{
		this.priceSelling = priceSelling;
	}

	public int getQuantityOrdered()
	{
		return quantityOrdered;
	}

	public void setQuantityOrdered(int quantityOrdered)
	{
		this.quantityOrdered = quantityOrdered;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public BigDecimal getTotalRevenue()
	{
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue)
	{
		this.totalRevenue = totalRevenue;
	}

	public boolean equals(RevenueBestSellerPacket revenueBestSellerPacket)
	{
		if (revenueBestSellerPacket instanceof RevenueBestSellerPacket && ((RevenueBestSellerPacket) revenueBestSellerPacket).getId() == this.id)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int compareTo(RevenueBestSellerPacket o)
	{

		if (this.priceSelling != null && o.priceSelling != null)
		{
			return -(this.priceSelling.compareTo(o.priceSelling));
		}
		else
		{
			if (this.priceSelling != null)
			{
				return 1;
			}
			else if (o.priceSelling != null)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}

	}

	@Override
	public String toString()
	{
		return "RevenueBestSellerPacket [id=" + id + ", priceSelling=" + priceSelling + ", quantityOrdered=" + quantityOrdered + ", displayName=" + displayName + ", totalRevenue=" + totalRevenue
				+ "]";
	}

}
