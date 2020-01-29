/**
			 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.wrapper;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.OrderPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;

@XmlRootElement(name = "OrderPacketWapper")
public class OrderPacketWapper  
{

	private OrderPacket OrderPacket;

	public OrderPacket getOrderPacket()
	{
		return OrderPacket;
	}

	public void setOrderPacket(OrderPacket orderPacket)
	{
		OrderPacket = orderPacket;
	}

	@Override
	public String toString()
	{
		return "OrderPacketWapper [OrderPacket=" + OrderPacket + "]";
	}

 
	

}
