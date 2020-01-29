/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderStatus;
@XmlRootElement(name = "OrderStatusListPacket")
public class OrderStatusListPacket extends PostPacket
{

	List<OrderStatus> orderStatus;

	/**
	 * @return the orderStatus
	 */
	public List<OrderStatus> getOrderStatus()
	{
		return orderStatus;
	}

	/**
	 * @param orderStatus
	 *            the orderStatus to set
	 */
	public void setOrderStatus(List<OrderStatus> orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "OrderStatusListPacket [orderStatus=" + (orderStatus != null ? orderStatus.subList(0, Math.min(orderStatus.size(), maxLen)) : null) + "]";
	}

}
