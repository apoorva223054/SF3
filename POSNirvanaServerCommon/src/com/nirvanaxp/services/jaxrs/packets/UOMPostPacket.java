/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.UnitOfMeasurement;

@XmlRootElement(name = "UOMPostPacket")
public class UOMPostPacket extends PostPacket
{

	private UnitOfMeasurement unitOfMeasurement;
	private int isBaseLocationUpdate;
    private String locationsListId;
	
	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	public UnitOfMeasurement getUnitOfMeasurement()
	{
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement)
	{
		this.unitOfMeasurement = unitOfMeasurement;
	}
	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	@Override
	public String toString()
	{
		return "UOMPostPacket [unitOfMeasurement=" + unitOfMeasurement + ", isBaseLocationUpdate="
				+ isBaseLocationUpdate + "]";
	}

}
