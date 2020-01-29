/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class RevenueByPaymentMethodType
{
 
	private String paymentMethodTypeName;
	private String guestCount;
	private String opdCount;
	 
	private String cashTip;
	private String amountPaid;
	private String cardTip;
	private String creditTermTip;
	private String subTotal;
	private String priceGratuity;
	private String totalTax;
	
	public String getPaymentMethodTypeName() {
		return paymentMethodTypeName;
	}
	public void setPaymentMethodTypeName(String paymentMethodTypeName) {
		this.paymentMethodTypeName = paymentMethodTypeName;
	}

	public String getGuestCount() {
		return guestCount;
	}
	public void setGuestCount(String guestCount) {
		this.guestCount = guestCount;
	}
	
	
	public String getOpdCount() {
		return opdCount;
	}
	public void setOpdCount(String opdCount) {
		this.opdCount = opdCount;
	}
	public String getCashTip() {
		return cashTip;
	}
	public void setCashTip(String cashTip) {
		this.cashTip = cashTip;
	}
	public String getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getCardTip() {
		return cardTip;
	}
	public void setCardTip(String cardTip) {
		this.cardTip = cardTip;
	}
	public String getCreditTermTip() {
		return creditTermTip;
	}
	public void setCreditTermTip(String creditTermTip) {
		this.creditTermTip = creditTermTip;
	}
	
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public String getPriceGratuity() {
		return priceGratuity;
	}
	public void setPriceGratuity(String priceGratuity) {
		this.priceGratuity = priceGratuity;
	}
	public String getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(String totalTax) {
		this.totalTax = totalTax;
	}
	@Override
	public String toString() {
		return "RevenueByPaymentMethodType [paymentMethodTypeName=" + paymentMethodTypeName + ", guestCount="
				+ guestCount + ", cashTip=" + cashTip + ", amountPaid=" + amountPaid + ", opdCount=" + opdCount + ", cardTip=" + cardTip
				+ ", subTotal=" + subTotal + ", priceGratuity=" + priceGratuity + ", totalTax=" + totalTax
				+ ", creditTermTip=" + creditTermTip + "]";
	}
	
	 
}
