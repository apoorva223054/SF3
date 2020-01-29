/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderSourceGroupToPaymentgatewayType;
@XmlRootElement(name = "OrderSourceGroupToPaymentgatewayTypePacket")
public class OrderSourceGroupToPaymentgatewayTypePacket extends PostPacket
{

	private OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType;

	public OrderSourceGroupToPaymentgatewayType getOrderSourceGroupToPaymentgatewayType()
	{
		return orderSourceGroupToPaymentgatewayType;
	}

	public void setOrderSourceGroupToPaymentgatewayType(OrderSourceGroupToPaymentgatewayType orderSourceGroupToPaymentgatewayType)
	{
		this.orderSourceGroupToPaymentgatewayType = orderSourceGroupToPaymentgatewayType;
	}

	@Override
	public String toString()
	{
		return "OrderSourceGroupToPaymentgatewayTypePacket [orderSourceGroupToPaymentgatewayType=" + orderSourceGroupToPaymentgatewayType + "]";
	}

}
