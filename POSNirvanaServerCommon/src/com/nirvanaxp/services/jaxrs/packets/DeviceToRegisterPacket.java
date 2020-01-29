/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.device.DeviceToRegister;

@XmlRootElement(name = "DeviceToRegisterPacket")
public class DeviceToRegisterPacket extends PostPacket
{

	private List<DeviceToRegister> deviceToRegisters;

	public List<DeviceToRegister> getDeviceToRegisters() {
		return deviceToRegisters;
	}
	public void setDeviceToRegisters(List<DeviceToRegister> deviceToRegisters) {
		this.deviceToRegisters = deviceToRegisters;
	}
	@Override
	public String toString() {
		return "DeviceToRegisterPacket [deviceToRegisters=" + deviceToRegisters
				+ "]";
	}
	
	

	
}
