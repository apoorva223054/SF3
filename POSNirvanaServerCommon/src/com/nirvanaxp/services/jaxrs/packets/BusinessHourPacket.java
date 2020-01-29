/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.business.BusinessHour;

@XmlRootElement(name = "BusinessHourPacket")
public class BusinessHourPacket extends PostPacket
{

	private List<BusinessHour> businessHourList;
	private BusinessHour businessHour;

	public List<BusinessHour> getBusinessHourList()
	{
		return businessHourList;
	}

	public void setBusinessHourList(List<BusinessHour> businessHourList)
	{
		this.businessHourList = businessHourList;
	}

	public BusinessHour getBusinessHour()
	{
		return businessHour;
	}

	public void setBusinessHour(BusinessHour businessHour)
	{
		this.businessHour = businessHour;
	}

	@Override
	public String toString()
	{
		return "BusinessHourPacket [businessHourList=" + businessHourList + ", businessHour=" + businessHour + "]";
	}

}
