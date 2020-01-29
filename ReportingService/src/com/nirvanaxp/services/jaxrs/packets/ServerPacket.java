/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class ServerPacket
{
	private int id;
	private BigDecimal priceSelling;
	private int quantityOrdered;
	private String displayName;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ServerPacket [id=" + id + ", priceSelling=" + priceSelling + ", quantityOrdered=" + quantityOrdered + ", displayName=" + displayName + "]";
	}

}
