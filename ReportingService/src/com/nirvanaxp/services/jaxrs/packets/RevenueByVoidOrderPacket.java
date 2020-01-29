/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByVoidOrder;

public class RevenueByVoidOrderPacket
{
 
	private List<RevenueByVoidOrder> revenueByVoidOrder;
	private String orderCount;
	private String total;
	private String balanceDue;
	
	 
	public List<RevenueByVoidOrder> getRevenueByVoidOrder() {
		return revenueByVoidOrder;
	}
	public void setRevenueByVoidOrder(List<RevenueByVoidOrder> revenueByVoidOrder) {
		this.revenueByVoidOrder = revenueByVoidOrder;
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
		return "RevenueByVoidOrderPacket [revenueByVoidOrder=" + revenueByVoidOrder + ", orderCount=" + orderCount + ", total="
				+ total + ", balanceDue=" + balanceDue + "]";
	}
	 
}
