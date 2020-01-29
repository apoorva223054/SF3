/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LocationIdPacket")
public class LocationIdPacket extends PostPacket
{
	private List<String> locationsId;

	public List<String> getLocationsId() {
		return locationsId;
	}

	public void setLocationsId(List<String> locationsId) {
		this.locationsId = locationsId;
	}

	@Override
	public String toString() {
		return "LocationIdPacket [locationsId=" + locationsId + "]";
	}
 

}
