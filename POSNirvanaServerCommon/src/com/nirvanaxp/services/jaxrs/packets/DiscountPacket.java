/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.discounts.Discount;
@XmlRootElement(name = "DiscountPacket")
public class DiscountPacket extends PostPacket
{

	private Discount discount;
	private int isBaseLocationUpdate;
	private String locationsListId;

	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	
	
	public Discount getDiscount()
	{
		return discount;
	}

	public void setDiscount(Discount discount)
	{
		this.discount = discount;
	}

	public int getIsBaseLocationUpdate()
	{
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate)
	{
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	@Override
	public String toString()
	{
		return "DiscountPacket [discount=" + discount + ", isBaseLocationUpdate=" + isBaseLocationUpdate + "]";
	}
	
	

	





	 
}
