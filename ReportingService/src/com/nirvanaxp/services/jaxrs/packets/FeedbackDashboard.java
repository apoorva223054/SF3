/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

public class FeedbackDashboard
{
	private String smileyName;
	private BigInteger totalCount;

	public String getSmileyName()
	{
		return smileyName;
	}

	public void setSmileyName(String smileyName)
	{
		this.smileyName = smileyName;
	}

	public BigInteger getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(BigInteger totalCount)
	{
		this.totalCount = totalCount;
	}

}
