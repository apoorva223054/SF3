/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderSourceToPaymentgatewayType;
@XmlRootElement(name = "OrderSourceToPaymentgatewayTypePacket")
public class OrderSourceToPaymentgatewayTypePacket extends PostPacket
{

	private OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType;

	public OrderSourceToPaymentgatewayType getOrderSourceToPaymentgatewayType()
	{
		return orderSourceToPaymentgatewayType;
	}

	public void setOrderSourceToPaymentgatewayType(OrderSourceToPaymentgatewayType orderSourceToPaymentgatewayType)
	{
		this.orderSourceToPaymentgatewayType = orderSourceToPaymentgatewayType;
	}

	@Override
	public String toString()
	{
		return "OrderSourceToPaymentgatewayTypePacket [orderSourceToPaymentgatewayType=" + orderSourceToPaymentgatewayType + "]";
	}

}
