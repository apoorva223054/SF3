/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.List;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.business.BusinessHour;

public class BusinessHourPacket extends PostPacket
{

	private List<BusinessHour> businessHourList;

	public List<BusinessHour> getBusinessHourList()
	{
		return businessHourList;
	}

	public void setBusinessHourList(List<BusinessHour> businessHourList)
	{
		this.businessHourList = businessHourList;
	}

	@Override
	public String toString()
	{
		return "BusinessHourPacket [businessHourList=" + businessHourList + "]";
	}

}
