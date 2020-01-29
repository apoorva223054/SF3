/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.discounts.DiscountsType;
import com.nirvanaxp.types.entities.locations.LocationsToShiftPreAssignServer;
@XmlRootElement(name = "LocationsToShiftPreAssignServerPacket")
public class LocationsToShiftPreAssignServerPacket extends PostPacket
{
	private List<LocationsToShiftPreAssignServer> locationsToShiftPreAssignServerList;

	public List<LocationsToShiftPreAssignServer> getLocationsToShiftPreAssignServerList()
	{
		return locationsToShiftPreAssignServerList;
	}

	public void setLocationsToShiftPreAssignServerList(List<LocationsToShiftPreAssignServer> locationsToShiftPreAssignServerList)
	{
		this.locationsToShiftPreAssignServerList = locationsToShiftPreAssignServerList;
	}

	@Override
	public String toString()
	{
		return "LocationsToShiftPreAssignServerPacket [locationsToShiftPreAssignServerList=" + locationsToShiftPreAssignServerList + "]";
	}

	
}
