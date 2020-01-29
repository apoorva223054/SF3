/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OrderHeaderSourcePacket")
public class OrderHeaderSourcePacket extends PostPacket
{

	private OrderHeaderSource orderHeaderSource;

	/**
	 * @return the orderHeaderSource
	 */
	public OrderHeaderSource getOrderHeaderSource()
	{
		return orderHeaderSource;

	}

	/**
	 * @param orderHeaderSource
	 *            the orderHeaderSource to set
	 */
	public void setOrderHeaderSource(OrderHeaderSource orderHeaderSource)
	{
		this.orderHeaderSource = orderHeaderSource;
	}

	@Override
	public String toString()
	{
		return "OrderHeaderSourcePacket [orderHeaderSource=" + orderHeaderSource + "]";
	}

}
