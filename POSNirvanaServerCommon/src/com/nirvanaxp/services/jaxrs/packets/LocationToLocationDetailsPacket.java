/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.LocationToLocationDetails;

@XmlRootElement(name = "LocationToLocationDetailsPacket")
public class LocationToLocationDetailsPacket extends PostPacket
{

	private LocationToLocationDetails locationToLocationDetails;

	
	public LocationToLocationDetails getLocationToLocationDetails() {
		return locationToLocationDetails;
	}

	public void setLocationToLocationDetails(
			LocationToLocationDetails locationToLocationDetails) {
		this.locationToLocationDetails = locationToLocationDetails;
	}

	@Override
	public String toString()
	{
		return "LocationToLocationDetailsPacket [locationToLocationDetails =" + locationToLocationDetails + "]";
	}

}
