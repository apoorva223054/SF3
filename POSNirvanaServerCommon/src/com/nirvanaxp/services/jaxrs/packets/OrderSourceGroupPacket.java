/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderSourceGroup;
@XmlRootElement(name = "OrderSourceGroupPacket")
public class OrderSourceGroupPacket extends PostPacket
{
	private OrderSourceGroup orderSourceGroup;

	public OrderSourceGroup getOrderSourceGroup()
	{
		return orderSourceGroup;
	}

	public void setOrderSourceGroup(OrderSourceGroup orderSourceGroup)
	{
		this.orderSourceGroup = orderSourceGroup;
	}

	@Override
	public String toString()
	{
		return "OrderSourceGroupPacket [orderSourceGroup=" + orderSourceGroup + "]";
	}

}
