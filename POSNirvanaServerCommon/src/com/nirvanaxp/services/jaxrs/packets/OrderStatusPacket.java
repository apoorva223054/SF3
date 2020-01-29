/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderStatus;
@XmlRootElement(name = "OrderStatusPacket")
public class OrderStatusPacket extends PostPacket
{

	private OrderStatus orderStatus;

	public OrderStatus getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString()
	{
		return "OrderStatusPacket [orderStatus=" + orderStatus + "]";
	}

}
