/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.orders.OrderHeader;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderWithAddressInfo.
 */
public class OrderWithAddressInfo
{

	/** The order header. */
	private OrderHeader orderHeader;

	/** The billing address. */
	private Address billingAddress;

	/** The shi address. */
	private Address shiAddress;

	/**
	 * Gets the order header.
	 *
	 * @return the order header
	 */
	public OrderHeader getOrderHeader()
	{
		return orderHeader;
	}

	/**
	 * Sets the order header.
	 *
	 * @param orderHeader the new order header
	 */
	public void setOrderHeader(OrderHeader orderHeader)
	{
		this.orderHeader = orderHeader;
	}

	/**
	 * Gets the billing address.
	 *
	 * @return the billing address
	 */
	public Address getBillingAddress()
	{
		return billingAddress;
	}

	/**
	 * Sets the billing address.
	 *
	 * @param billingAddress the new billing address
	 */
	public void setBillingAddress(Address billingAddress)
	{
		this.billingAddress = billingAddress;
	}

	/**
	 * Gets the shi address.
	 *
	 * @return the shi address
	 */
	public Address getShiAddress()
	{
		return shiAddress;
	}

	/**
	 * Sets the shi address.
	 *
	 * @param shiAddress the new shi address
	 */
	public void setShiAddress(Address shiAddress)
	{
		this.shiAddress = shiAddress;
	}

}
