/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OrderSourceGroupPerformanceDashboard
{

	private String name;
	private BigInteger minTime;
	private BigDecimal avgTime;
	private BigInteger maxTime;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public BigInteger getMinTime()
	{
		return minTime;
	}

	public void setMinTime(BigInteger minTime)
	{
		this.minTime = minTime;
	}

	public BigDecimal getAvgTime()
	{
		return avgTime;
	}

	public void setAvgTime(BigDecimal avgTime)
	{
		this.avgTime = avgTime;
	}

	public BigInteger getMaxTime()
	{
		return maxTime;
	}

	public void setMaxTime(BigInteger maxTime)
	{
		this.maxTime = maxTime;
	}

}
