/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class CategorywiseReporting
{
 
	private String categoryName;
	private String categoryId;
	private String itemQty;
	private String total;
	private String subTotal;
	
	private String tax1;
	private String tax2;
	private String tax3;
	private String tax4;
	private String discount;
	private String gratuity;
	private String fromDate;
	private String toDate;
	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryId() {
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public String getTax1() {
		return tax1;
	}
	public void setTax1(String tax1) {
		this.tax1 = tax1;
	}
	public String getTax2() {
		return tax2;
	}
	public void setTax2(String tax2) {
		this.tax2 = tax2;
	}
	public String getTax3() {
		return tax3;
	}
	public void setTax3(String tax3) {
		this.tax3 = tax3;
	}
	public String getTax4() {
		return tax4;
	}
	public void setTax4(String tax4) {
		this.tax4 = tax4;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getGratuity() {
		return gratuity;
	}
	public void setGratuity(String gratuity) {
		this.gratuity = gratuity;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	@Override
	public String toString() {
		return "CategorywiseReporting [categoryName=" + categoryName + ", categoryId=" + categoryId + ", itemQty="
				+ itemQty + ", total=" + total + ", subTotal=" + subTotal + ", tax1=" + tax1 + ", tax2=" + tax2
				+ ", tax3=" + tax3 + ", tax4=" + tax4 + ", discount=" + discount + ", gratuity=" + gratuity
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
	 
	 
}
