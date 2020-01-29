/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.LocationsToFunction;
@XmlRootElement(name = "LocationToFunctionsListPacket")
public class LocationToFunctionsListPacket extends PostPacket
{

	List<LocationsToFunction> function;

	public List<LocationsToFunction> getFunction()
	{
		return function;
	}

	public void setFunction(List<LocationsToFunction> function)
	{
		this.function = function;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "LocationToFunctionsListPacket [function=" + (function != null ? function.subList(0, Math.min(function.size(), maxLen)) : null) + "]";
	}

}
