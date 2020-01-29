/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

public class OrderHeaderWithCreatedAndUpdatedCountPacket
{
	private int totalRecordCount;
	private List<OrderHeaderWithCreatedAndUpdated> orderHeaderWithCreatedAndUpdated;

	public int getTotalRecordCount()
	{
		return totalRecordCount;
	}

	public void setTotalRecordCount(int totalRecordCount)
	{
		this.totalRecordCount = totalRecordCount;
	}

	public List<OrderHeaderWithCreatedAndUpdated> getOrderHeaderWithCreatedAndUpdated()
	{
		return orderHeaderWithCreatedAndUpdated;
	}

	public void setOrderHeaderWithCreatedAndUpdated(List<OrderHeaderWithCreatedAndUpdated> orderHeaderWithCreatedAndUpdated)
	{
		this.orderHeaderWithCreatedAndUpdated = orderHeaderWithCreatedAndUpdated;
	}

	@Override
	public String toString()
	{
		return "OrderHeaderWithCreatedAndUpdatedCountPacket [totalRecordCount=" + totalRecordCount + ", orderHeaderWithCreatedAndUpdated=" + orderHeaderWithCreatedAndUpdated + "]";
	}

}
