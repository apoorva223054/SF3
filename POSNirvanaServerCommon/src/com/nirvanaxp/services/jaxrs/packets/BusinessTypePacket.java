/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.Business;
@XmlRootElement(name = "BusinessTypePacket")
public class BusinessTypePacket extends PostPacket
{

	private Business business;

	private Address address;

	private List<String> globalUserIdList;

	private String userId;

	public Business getBusiness()
	{
		return business;
	}

	public void setBusiness(Business business)
	{
		this.business = business;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}


	public List<String> getGlobalUserIdList() {
		return globalUserIdList;
	}

	public void setGlobalUserIdList(List<String> globalUserIdList) {
		this.globalUserIdList = globalUserIdList;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString()
	{
		return "BusinessTypePacket [business=" + business + ", address=" + address + ", globalUserIdList=" + globalUserIdList + ", userId=" + userId + "]";
	}

}
