/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.LocationsToFunction;

@XmlRootElement(name = "LocationToFunctionPacket")
public class LocationToFunctionPacket extends PostPacket
{

	private LocationsToFunction locationsToFunction;

	/**
	 * @return the locationsToFunction
	 */
	public LocationsToFunction getLocationsToFunction()
	{
		return locationsToFunction;
	}

	/**
	 * @param locationsToFunction
	 *            the locationsToFunction to set
	 */
	public void setLocationsToFunction(LocationsToFunction locationsToFunction)
	{
		this.locationsToFunction = locationsToFunction;
	}

	@Override
	public String toString()
	{
		return "LocationToFunctionPacket [locationsToFunction=" + locationsToFunction + "]";
	}

}
