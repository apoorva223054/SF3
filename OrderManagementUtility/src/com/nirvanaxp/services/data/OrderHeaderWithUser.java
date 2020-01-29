/*
 * 
 */
package com.nirvanaxp.services.data;

import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.user.User;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderHeaderWithUser.
 */
public class OrderHeaderWithUser {
	
	/** The order header. */
	private OrderHeader orderHeader;
	
	/** The user. */
	private User user;
	
	/** The tab order push. */
	private boolean tabOrderPush;
	
	/**
	 * Instantiates a new order header with user.
	 *
	 * @param oh the oh
	 * @param u the u
	 * @param tabOrderPush the tab order push
	 */
	public OrderHeaderWithUser(OrderHeader oh, User u, boolean tabOrderPush)
	{
		this.orderHeader = oh;
		this.user = u;
		this.tabOrderPush = tabOrderPush;
	}
	
	/**
	 * Gets the order header.
	 *
	 * @return the order header
	 */
	public OrderHeader getOrderHeader() {
		return orderHeader;
	}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Checks if is tab order push.
	 *
	 * @return true, if is tab order push
	 */
	public boolean isTabOrderPush()
	{
		return tabOrderPush;
	}

	public void setOrderHeader(OrderHeader orderHeader)
	{
		this.orderHeader = orderHeader;
	}
	
	

}
