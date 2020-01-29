/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CoverRevenue")
public class CoverRevenue
{

	private String created;
	private BigDecimal revenue;

	public CoverRevenue()
	{

	}

	public String getCreated()
	{
		return created;
	}

	public void setCreated(String created)
	{
		this.created = created;
	}

	public BigDecimal getRevenue()
	{
		return revenue;
	}

	public void setRevenue(BigDecimal revenue)
	{
		this.revenue = revenue;
	}

}
