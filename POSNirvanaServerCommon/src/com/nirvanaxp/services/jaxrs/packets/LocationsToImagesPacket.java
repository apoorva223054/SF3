/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.LocationsToImages;

@XmlRootElement(name = "LocationsToImagesPacket")
public class LocationsToImagesPacket extends PostPacket
{

	private LocationsToImages locationsToImages;

	
	

	public LocationsToImages getLocationsToImages() {
		return locationsToImages;
	}




	public void setLocationsToImages(LocationsToImages locationsToImages) {
		this.locationsToImages = locationsToImages;
	}




	@Override
	public String toString()
	{
		return "LocationsToImagesPacket [locationsToImages =" + locationsToImages + "]";
	}

}
