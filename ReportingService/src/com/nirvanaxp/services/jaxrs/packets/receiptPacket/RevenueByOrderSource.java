/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class RevenueByOrderSource
{
 
	private String guestCount;
	private String orderSourceGroupName;
	private String orderSourceName;
	private String amount;
	
	public String getGuestCount() {
		return guestCount;
	}
	public void setGuestCount(String guestCount) {
		this.guestCount = guestCount;
	}
	public String getOrderSourceGroupName() {
		return orderSourceGroupName;
	}
	public void setOrderSourceGroupName(String orderSourceGroupName) {
		this.orderSourceGroupName = orderSourceGroupName;
	}
	public String getOrderSourceName() {
		return orderSourceName;
	}
	public void setOrderSourceName(String orderSourceName) {
		this.orderSourceName = orderSourceName;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "RevenueByOrderSource [guestCount=" + guestCount + ", orderSourceGroupName=" + orderSourceGroupName
				+ ", orderSourceName=" + orderSourceName + ", amount=" + amount + "]";
	}
	
}
