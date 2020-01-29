/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

public class OrderSourceGroupwiseReporting
{
 
	private String orderSourceGroupName;
	private int pointOfServiceCount;
	private String amountPaid;
	private String fromDate;
	private String toDate;
	private String locationName;
	public String getOrderSourceGroupName()
	{
		return orderSourceGroupName;
	}
	public void setOrderSourceGroupName(String orderSourceGroupName)
	{
		this.orderSourceGroupName = orderSourceGroupName;
	}
	public int getPointOfServiceCount()
	{
		return pointOfServiceCount;
	}
	public void setPointOfServiceCount(int pointOfServiceCount)
	{
		this.pointOfServiceCount = pointOfServiceCount;
	}
	public String getAmountPaid()
	{
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid)
	{
		this.amountPaid = amountPaid;
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
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	@Override
	public String toString() {
		return "OrderSourceGroupwiseReporting [orderSourceGroupName=" + orderSourceGroupName + ", pointOfServiceCount="
				+ pointOfServiceCount + ", amountPaid=" + amountPaid + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", locationName=" + locationName + "]";
	}
	
}
