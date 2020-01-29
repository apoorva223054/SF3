/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class PaymentMethodwiseReporting
{
 
	private String paymentMethodTypeName;
	private String paymentMethodName;
	private int guestCount;
	private double amountPaid;
	private double cashTip;
	private double cardTip;
	private String fromDate;
	private String toDate;
	private String locationName;
	public String getPaymentMethodTypeName() {
		return paymentMethodTypeName;
	}
	public String getPaymentMethodName() {
		return paymentMethodName;
	}
	public int getGuestCount() {
		return guestCount;
	}
	public double getAmountPaid() {
		return amountPaid;
	}
	public double getCashTip() {
		return cashTip;
	}
	public double getCardTip() {
		return cardTip;
	}
	public String getFromDate() {
		return fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setPaymentMethodTypeName(String paymentMethodTypeName) {
		this.paymentMethodTypeName = paymentMethodTypeName;
	}
	public void setPaymentMethodName(String paymentMethodName) {
		this.paymentMethodName = paymentMethodName;
	}
	public void setGuestCount(int guestCount) {
		this.guestCount = guestCount;
	}
	public void setAmountPaid(double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public void setCashTip(double cashTip) {
		this.cashTip = cashTip;
	}
	public void setCardTip(double cardTip) {
		this.cardTip = cardTip;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	 
}
