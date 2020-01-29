/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByCategory;

public class RevenueByCategoryPacket
{
 
	private List<RevenueByCategory> revenueByCategory;
	private String itemQty;
	private String total;
	
	
	public List<RevenueByCategory> getRevenueByCategory() {
		return revenueByCategory;
	}
	public void setRevenueByCategory(List<RevenueByCategory> revenueByCategory) {
		this.revenueByCategory = revenueByCategory;
	}
	public String getItemQty() {
		return itemQty;
	}
	public void setItemQty(String itemQty) {
		this.itemQty = itemQty;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	@Override
	public String toString() {
		return "RevenueByCategoryPacket [revenueByCategory=" + revenueByCategory + ", itemQty=" + itemQty
				+ ", total=" + total + "]";
	}
	
	
}
