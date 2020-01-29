/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class OrderStatusDashboard
{
	private BigDecimal orderPlacedTime;
	private BigDecimal drinkedServedTime;
	private BigDecimal appetizerServedTime;
	private BigDecimal foodServedTime;
	private BigDecimal dessertServedTime;
	private BigDecimal checkPresentedTime;
	private BigDecimal orderPaidTime;
	private BigDecimal orderPlacedFireTime;
	private BigDecimal busReadyTime;
	private BigDecimal readyToOrderTime;

	public BigDecimal getOrderPlacedTime()
	{
		return orderPlacedTime;
	}

	public void setOrderPlacedTime(BigDecimal orderPlacedTime)
	{
		this.orderPlacedTime = orderPlacedTime;
	}

	public BigDecimal getDrinkedServedTime()
	{
		return drinkedServedTime;
	}

	public void setDrinkedServedTime(BigDecimal drinkedServedTime)
	{
		this.drinkedServedTime = drinkedServedTime;
	}

	public BigDecimal getAppetizerServedTime()
	{
		return appetizerServedTime;
	}

	public void setAppetizerServedTime(BigDecimal appetizerServedTime)
	{
		this.appetizerServedTime = appetizerServedTime;
	}

	public BigDecimal getFoodServedTime()
	{
		return foodServedTime;
	}

	public void setFoodServedTime(BigDecimal foodServedTime)
	{
		this.foodServedTime = foodServedTime;
	}

	public BigDecimal getDessertServedTime()
	{
		return dessertServedTime;
	}

	public void setDessertServedTime(BigDecimal dessertServedTime)
	{
		this.dessertServedTime = dessertServedTime;
	}

	public BigDecimal getCheckPresentedTime()
	{
		return checkPresentedTime;
	}

	public void setCheckPresentedTime(BigDecimal checkPresentedTime)
	{
		this.checkPresentedTime = checkPresentedTime;
	}

	public BigDecimal getOrderPaidTime()
	{
		return orderPaidTime;
	}

	public void setOrderPaidTime(BigDecimal orderPaidTime)
	{
		this.orderPaidTime = orderPaidTime;
	}

	public BigDecimal getOrderPlacedFireTime()
	{
		return orderPlacedFireTime;
	}

	public void setOrderPlacedFireTime(BigDecimal orderPlacedFireTime)
	{
		this.orderPlacedFireTime = orderPlacedFireTime;
	}

	public BigDecimal getBusReadyTime()
	{
		return busReadyTime;
	}

	public void setBusReadyTime(BigDecimal busReadyTime)
	{
		this.busReadyTime = busReadyTime;
	}

	public BigDecimal getReadyToOrderTime()
	{
		return readyToOrderTime;
	}

	public void setReadyToOrderTime(BigDecimal readyToOrderTime)
	{
		this.readyToOrderTime = readyToOrderTime;
	}

}
