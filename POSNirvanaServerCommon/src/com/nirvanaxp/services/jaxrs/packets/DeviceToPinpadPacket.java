/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.device.DeviceToPinPad;

@XmlRootElement(name = "DeviceToPinpadPacket")
public class DeviceToPinpadPacket extends PostPacket
{
	private List<DeviceToPinPad> deviceToPinPad;

	public List<DeviceToPinPad> getDeviceToPinPad()
	{
		return deviceToPinPad;
	}

	public void setDeviceToPinPad(List<DeviceToPinPad> deviceToPinPad)
	{
		this.deviceToPinPad = deviceToPinPad;
	}

	@Override
	public String toString()
	{
		return "DeviceToPinpadPacket [deviceToPinPad=" + deviceToPinPad + "]";
	}

 
 
	
  
}
