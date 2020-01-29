/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderSource;
@XmlRootElement(name = "OrderSourcePacket")
public class OrderSourcePacket extends PostPacket
{

	private OrderSource orderSource;

	public OrderSource getOrderSource()
	{
		return orderSource;
	}

	public void setOrderSource(OrderSource orderSource)
	{
		this.orderSource = orderSource;
	}

	@Override
	public String toString()
	{
		return "OrderSourcePacket [orderSource=" + orderSource + "]";
	}

}
