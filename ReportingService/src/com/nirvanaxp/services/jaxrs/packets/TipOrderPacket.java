/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.services.jaxrs.TipByOrder;

public class TipOrderPacket
{
	private String businessName;
	private List<TipByOrder> employeeTipOrderList;
	
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	 
	
	public List<TipByOrder> getEmployeeTipOrderList() {
		return employeeTipOrderList;
	}
	public void setEmployeeTipOrderList(List<TipByOrder> employeeTipOrderList) {
		this.employeeTipOrderList = employeeTipOrderList;
	}
	@Override
	public String toString() {
		return "TipOrderPacket [businessName=" + businessName + ", employeeTipOrderList=" + employeeTipOrderList + "]";
	}
	 
	
}
