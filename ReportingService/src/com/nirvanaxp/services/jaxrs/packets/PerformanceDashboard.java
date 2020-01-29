/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class PerformanceDashboard
{

	private String name;
	private BigDecimal amountPaidPercentage;
	private BigDecimal amountPaid;
	private BigDecimal subtotal;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public BigDecimal getAmountPaidPercentage()
	{
		return amountPaidPercentage;
	}

	public void setAmountPaidPercentage(BigDecimal amountPaidPercentage)
	{
		this.amountPaidPercentage = amountPaidPercentage;
	}

	public BigDecimal getAmountPaid()
	{
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid)
	{
		this.amountPaid = amountPaid;
	}

	public BigDecimal getSubtotal()
	{
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal)
	{
		this.subtotal = subtotal;
	}

}
