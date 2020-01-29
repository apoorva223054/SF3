/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class ItemGroupReporting
{
	 
		private String categoryName;
		private String categoryId;
		private double itemsQty;
		private double total;
		private double subTotal;
		private double tax1;
		private double tax2;
		private double tax3;
		private double tax4;
		private double discount;
		private double gratuity;
		private String fromDate;
		private String toDate;
		private String locationName;
		public String getCategoryName() {
			return categoryName;
		}
		public String getCategoryId() {
			 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
		}
		public double getItemsQty() {
			return itemsQty;
		}
		public double getTotal() {
			return total;
		}
		public double getSubTotal() {
			return subTotal;
		}
		public double getTax1() {
			return tax1;
		}
		public double getTax2() {
			return tax2;
		}
		public double getTax3() {
			return tax3;
		}
		public double getTax4() {
			return tax4;
		}
		public double getDiscount() {
			return discount;
		}
		public double getGratuity() {
			return gratuity;
		}
		public String getFromDate() {
			return fromDate;
		}
		public String getToDate() {
			return toDate;
		}
		public String getLocationName() {
			return locationName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}
		public void setItemsQty(double itemsQty) {
			this.itemsQty = itemsQty;
		}
		public void setTotal(double total) {
			this.total = total;
		}
		public void setSubTotal(double subTotal) {
			this.subTotal = subTotal;
		}
		public void setTax1(double tax1) {
			this.tax1 = tax1;
		}
		public void setTax2(double tax2) {
			this.tax2 = tax2;
		}
		public void setTax3(double tax3) {
			this.tax3 = tax3;
		}
		public void setTax4(double tax4) {
			this.tax4 = tax4;
		}
		public void setDiscount(double discount) {
			this.discount = discount;
		}
		public void setGratuity(double gratuity) {
			this.gratuity = gratuity;
		}
		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}
		public void setToDate(String toDate) {
			this.toDate = toDate;
		}
		public void setLocationName(String locationName) {
			this.locationName = locationName;
		}
		@Override
		public String toString() {
			return "OrderSourceGroupwiseReporting [categoryName=" + categoryName + ", categoryId=" + categoryId
					+ ", itemsQty=" + itemsQty + ", total=" + total + ", subTotal=" + subTotal + ", tax1=" + tax1
					+ ", tax2=" + tax2 + ", tax3=" + tax3 + ", tax4=" + tax4 + ", discount=" + discount + ", gratuity="
					+ gratuity + ", fromDate=" + fromDate + ", toDate=" + toDate + ", locationName=" + locationName + "]";
		}
		
}
