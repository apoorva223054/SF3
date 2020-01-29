/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderWithStatusHistory.
 */
@XmlRootElement(name = "OrderWithUsers")
public class OrderWithStatusHistory implements Serializable
{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1432260891663717L;

	/** The order header id. */
	private String orderHeaderId;

	/** The order status id. */
	private String orderStatusId;
	
	/** The order status name. */
	private String orderStatusName;
	
	/** The updated. */
	private  Date updated;
	
	/**
	 * Sets the updated.
	 *
	 * @param updated the new updated
	 */
	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	 
	/**
	 * Gets the updated.
	 *
	 * @return the updated
	 */
	public Date getUpdated()
	{
		return updated;
	}

	/**
	 * Gets the order header id.
	 *
	 * @return the order header id
	 */
	public String getOrderHeaderId()
	{
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	/**
	 * Sets the order header id.
	 *
	 * @param orderHeaderId the new order header id
	 */
	public void setOrderHeaderId(String orderHeaderId)
	{
		this.orderHeaderId = orderHeaderId;
	}

	/**
	 * Gets the order status id.
	 *
	 * @return the order status id
	 */
	public String getOrderStatusId()
	{
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	/**
	 * Sets the order status id.
	 *
	 * @param orderStatusId the new order status id
	 */
	public void setOrderStatusId(String orderStatusId)
	{
		this.orderStatusId = orderStatusId;
	}

	/**
	 * Gets the order status name.
	 *
	 * @return the order status name
	 */
	public String getOrderStatusName()
	{
		return orderStatusName;
	}

	/**
	 * Sets the order status name.
	 *
	 * @param orderStatusName the new order status name
	 */
	public void setOrderStatusName(String orderStatusName)
	{
		this.orderStatusName = orderStatusName;
	}
	

}
