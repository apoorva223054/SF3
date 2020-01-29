/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class DiscountItemReporting
{
	 
		private String discountName;
		private String paymentMethodName;
		private String discountsId;
		private double priceDiscount;
		private double total;
		private double subTotal;
		private int  itemsQty;
		private String fromDate;
		private String toDate;
		private String locationName;
		public String getDiscountName() {
			return discountName;
		}
		public String getPaymentMethodName() {
			return paymentMethodName;
		}
		public String getDiscountsId() {
			 if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
		}
		public double getPriceDiscount() {
			return priceDiscount;
		}
		public double getTotal() {
			return total;
		}
		public double getSubTotal() {
			return subTotal;
		}
		public int getItemsQty() {
			return itemsQty;
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
		public void setDiscountName(String discountName) {
			this.discountName = discountName;
		}
		public void setPaymentMethodName(String paymentMethodName) {
			this.paymentMethodName = paymentMethodName;
		}
		public void setDiscountsId(String discountsId) {
			this.discountsId = discountsId;
		}
		public void setPriceDiscount(double priceDiscount) {
			this.priceDiscount = priceDiscount;
		}
		public void setTotal(double total) {
			this.total = total;
		}
		public void setSubTotal(double subTotal) {
			this.subTotal = subTotal;
		}
		public void setItemsQty(int itemsQty) {
			this.itemsQty = itemsQty;
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
			return "PaymentMethodwiseReporting [discountName=" + discountName + ", paymentMethodName=" + paymentMethodName
					+ ", discountsId=" + discountsId + ", priceDiscount=" + priceDiscount + ", total=" + total
					+ ", subTotal=" + subTotal + ", itemsQty=" + itemsQty + ", fromDate=" + fromDate + ", toDate=" + toDate
					+ ", locationName=" + locationName + "]";
		}
		
		
}
