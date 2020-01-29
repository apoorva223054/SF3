/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderWithUsers.
 */
@XmlRootElement(name = "OrderWithUsers")
public class OrderWithUsers implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1432260891663717L;

	/** The order header. */
	private OrderHeader orderHeader;

	/** The user. */
	private User user;

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
	 * Gets the user.
	 *
	 * @return the user
	 */
	public User getUser()
	{
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(User user)
	{
		this.user = user;
	}

}
