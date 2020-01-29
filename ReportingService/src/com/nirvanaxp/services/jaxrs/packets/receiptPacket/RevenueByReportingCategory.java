package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class RevenueByReportingCategory {
	private String categoryName;
	private String itemName;
	private String itemQty;
	private String total;
	private String priceSelling;
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
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
	public String getPriceSelling() {
		return priceSelling;
	}
	public void setPriceSelling(String priceSelling) {
		this.priceSelling = priceSelling;
	}
	@Override
	public String toString() {
		return "RevenueByReportingCategory [categoryName=" + categoryName
				+ ", itemName=" + itemName + ", itemQty=" + itemQty
				+ ", total=" + total + ", priceSelling=" + priceSelling + "]";
	}
	
	

}
