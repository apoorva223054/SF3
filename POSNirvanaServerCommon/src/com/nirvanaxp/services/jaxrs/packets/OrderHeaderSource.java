/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

import com.nirvanaxp.types.entities.orders.OrderHeader;

public class OrderHeaderSource
{

	private String orderId;
	private String orderSourceId;
	private String reservationId;
	private String updatedBy;
	private String userId;
	private String previousReservationStatusId;
	private String newReservationStatusId;
	private int partySize;
	private String cancelOrderId;
	private String orderStatusId;
	private OrderHeader orderHeader;



	/**
	 * @return the orderSourceId
	 */
	public String getOrderSourceId()
	{
		return orderSourceId;
	}

	/**
	 * @param orderSourceId
	 *            the orderSourceId to set
	 */
	public void setOrderSourceId(String orderSourceId)
	{
		this.orderSourceId = orderSourceId;
	}


	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the previousReservationStatusId
	 */
	public String getPreviousReservationStatusId()
	{
		return previousReservationStatusId;
	}

	/**
	 * @param previousReservationStatusId
	 *            the previousReservationStatusId to set
	 */
	public void setPreviousReservationStatusId(String previousReservationStatusId)
	{
		this.previousReservationStatusId = previousReservationStatusId;
	}

	/**
	 * @return the newReservationStatusId
	 */
	public String getNewReservationStatusId()
	{
		return newReservationStatusId;
	}

	/**
	 * @param newReservationStatusId
	 *            the newReservationStatusId to set
	 */
	public void setNewReservationStatusId(String newReservationStatusId)
	{
		this.newReservationStatusId = newReservationStatusId;
	}

	/**
	 * @return the partySize
	 */
	public int getPartySize()
	{
		return partySize;
	}

	/**
	 * @param partySize
	 *            the partySize to set
	 */
	public void setPartySize(int partySize)
	{
		this.partySize = partySize;
	}



	/**
	 * @return the orderStatusId
	 */
	public String getOrderStatusId()
	{
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	/**
	 * @param orderStatusId
	 *            the orderStatusId to set
	 */
	public void setOrderStatusId(String orderStatusId)
	{
		this.orderStatusId = orderStatusId;
	}

	/**
	 * @return the orderHeader
	 */
	public OrderHeader getOrderHeader()
	{
		return orderHeader;
	}

	/**
	 * @param orderHeader
	 *            the orderHeader to set
	 */
	public void setOrderHeader(OrderHeader orderHeader)
	{
		this.orderHeader = orderHeader;
	}
	

	public String getCancelOrderId() {
		return cancelOrderId;
	}

	public void setCancelOrderId(String cancelOrderId) {
		this.cancelOrderId = cancelOrderId;
	}

	@Override
	public String toString() {
		return "OrderHeaderSource [orderId=" + orderId + ", orderSourceId="
				+ orderSourceId + ", reservationId=" + reservationId
				+ ", updatedBy=" + updatedBy + ", userId=" + userId
				+ ", previousReservationStatusId="
				+ previousReservationStatusId + ", newReservationStatusId="
				+ newReservationStatusId + ", partySize=" + partySize
				+ ", cancelOrderId=" + cancelOrderId + ", orderStatusId="
				+ orderStatusId + ", orderHeader=" + orderHeader + "]";
	}
	
	
}
