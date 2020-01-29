package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.services.jaxrs.packets.receiptPacket.RevenueByReportingCategory;

public class RevenueByReportingCategoryPacket {

	private List<RevenueByReportingCategory> revenueByReportingCategories;
	private String itemQty;
	private String total;

	public List<RevenueByReportingCategory> getRevenueByReportingCategories() {
		return revenueByReportingCategories;
	}

	public void setRevenueByReportingCategories(
			List<RevenueByReportingCategory> revenueByReportingCategories) {
		this.revenueByReportingCategories = revenueByReportingCategories;
	}

	@Override
	public String toString() {
		return "RevenueByReportingCategoryPacket [revenueByReportingCategories="
				+ revenueByReportingCategories + "]";
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
	
	
}
