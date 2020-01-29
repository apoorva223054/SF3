/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.services.jaxrs.Tip;

public class TipPacket
{
	private String businessName;
	private List<Tip> employeeTipList;
	
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public List<Tip> getEmployeeTipList() {
		return employeeTipList;
	}
	public void setEmployeeTipList(List<Tip> employeeTipList) {
		this.employeeTipList = employeeTipList;
	}
	@Override
	public String toString() {
		return "TipPacket [businessName=" + businessName + ", employeeTipList=" + employeeTipList + "]";
	}
 
	 
}
