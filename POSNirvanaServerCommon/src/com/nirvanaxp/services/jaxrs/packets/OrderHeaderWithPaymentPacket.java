/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;
@XmlRootElement(name = "OrderHeaderWithPaymentPacket")
public class OrderHeaderWithPaymentPacket
{
	private OrderHeader header;
	private OrderPaymentDetail orderPaymentDetail;

	public OrderHeader getHeader()
	{
		return header;
	}

	public void setHeader(OrderHeader header)
	{
		this.header = header;
	}

	public OrderPaymentDetail getOrderPaymentDetail()
	{
		return orderPaymentDetail;
	}

	public void setOrderPaymentDetail(OrderPaymentDetail orderPaymentDetail)
	{
		this.orderPaymentDetail = orderPaymentDetail;
	}

	@Override
	public String toString()
	{
		return "OrderHeaderWithPaymentPacket [header=" + header + ", orderPaymentDetail=" + orderPaymentDetail + "]";
	}

}
