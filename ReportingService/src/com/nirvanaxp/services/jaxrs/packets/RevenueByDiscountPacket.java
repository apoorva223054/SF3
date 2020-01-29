/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByDiscount;

public class RevenueByDiscountPacket
{
	List<RevenueByDiscount> revenueByDiscount;
	private String discountCount;
	private String discountTotal;	
	
		
	public List<RevenueByDiscount> getRevenueByDiscount() {
		return revenueByDiscount;
	}
	public void setRevenueByDiscount(List<RevenueByDiscount> revenueByDiscount) {
		this.revenueByDiscount = revenueByDiscount;
	}
	
	public String getDiscountCount() {
		return discountCount;
	}
	public void setDiscountCount(String discountCount) {
		this.discountCount = discountCount;
	}

	public String getDiscountTotal() {
		return discountTotal;
	}
	public void setDiscountTotal(String discountTotal) {
		this.discountTotal = discountTotal;
	}
	
	@Override
	public String toString() {
		return "RevenueByDiscountPacket [revenueByDiscount=" + revenueByDiscount + ", discountCount=" + discountCount 
				+ ", discountTotal=" + discountTotal+ "]";
	}
}
