/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class RevenueByDiscount
{
 
	private String discountName;
	private String discountCount;
	private String discountTotal;	
	
	public String getDiscountName() {
		return discountName;
	}
	public void setDiscountName(String discountName) {
		this.discountName = discountName;
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
		return "RevenueByDiscount [discountName=" + discountName + ", discountCount=" + discountCount 
				+ ", discountTotal=" + discountTotal+ "]";
	}
}
