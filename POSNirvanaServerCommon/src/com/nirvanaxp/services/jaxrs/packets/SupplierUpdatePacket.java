/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.Location;
@XmlRootElement(name = "SupplierUpdatePacket")
public class SupplierUpdatePacket
{

	private Location supplier;

	private List<Location> locationsList;

	public Location getSupplier()
	{
		return supplier;
	}

	public void setSupplier(Location supplier)
	{
		this.supplier = supplier;
	}

	public List<Location> getLocationsList()
	{
		return locationsList;
	}

	public void setLocationsList(List<Location> locationsList)
	{
		this.locationsList = locationsList;
	}

	@Override
	public String toString()
	{
		return "SupplierUpdatePacket [supplier=" + supplier + ", locationsList=" + locationsList + "]";
	}

}
