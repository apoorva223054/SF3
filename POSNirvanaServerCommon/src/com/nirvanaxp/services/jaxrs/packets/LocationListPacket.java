/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.Location;

@XmlRootElement(name = "LocationListPacket")
public class LocationListPacket extends PostPacket
{

	private List<Location> location;

	public List<Location> getLocation()
	{
		return location;
	}

	public void setLocation(List<Location> location)
	{
		this.location = location;
	}

	@Override
	public String toString()
	{
		return "LocationPacket [location=" + location + "]";
	}

}
