/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.LocationSetting;

@XmlRootElement(name = "LocationSettingPacket")
public class LocationSettingPacket extends PostPacket
{

	private LocationSetting locationSetting;

	
	public LocationSetting getLocationSetting() {
		return locationSetting;
	}


	public void setLocationSetting(LocationSetting locationSetting) {
		this.locationSetting = locationSetting;
	}



	@Override
	public String toString()
	{
		return "LocationSettingPacket [locationSetting=" + locationSetting + "]";
	}

}
