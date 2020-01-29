/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class RevenueByVoidOrder
{
 
	private String orderStatusName;
	private String orderCount;
	private String total;
	private String balanceDue;
	public String getOrderStatusName() {
		return orderStatusName;
	}
	public void setOrderStatusName(String orderStatusName) {
		this.orderStatusName = orderStatusName;
	}
	 
	public String getOrderCount()
	{
		return orderCount;
	}
	public void setOrderCount(String orderCount)
	{
		this.orderCount = orderCount;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getBalanceDue() {
		return balanceDue;
	}
	public void setBalanceDue(String balanceDue) {
		this.balanceDue = balanceDue;
	}
	@Override
	public String toString() {
		return "RevenueByVoidOrder [orderStatusName=" + orderStatusName + ", orderCount=" + orderCount + ", total="
				+ total + ", balanceDue=" + balanceDue + "]";
	}
	 
}
