package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.DriverOrder;

@XmlRootElement(name = "DriverOrderPacket")
public class DriverOrderPacket extends PostPacket {
	
	DriverOrder driverOrder;

	public DriverOrder getDriverOrder()
	{
		return driverOrder;
	}

	public void setDriverOrder(DriverOrder driverOrder)
	{
		this.driverOrder = driverOrder;
	}

}
