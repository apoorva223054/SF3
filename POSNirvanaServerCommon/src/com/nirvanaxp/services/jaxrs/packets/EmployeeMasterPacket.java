/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.tip.EmployeeMaster;


@XmlRootElement(name = "EmployeeMasterPacket")
public class EmployeeMasterPacket extends PostPacket
{
	private EmployeeMaster employeeMaster;

	public EmployeeMaster getEmployeeMaster()
	{
		return employeeMaster;
	}

	public void setEmployeeMaster(EmployeeMaster employeeMaster)
	{
		this.employeeMaster = employeeMaster;
	}

	@Override
	public String toString()
	{
		return "EmployeeMasterPacket [employeeMaster=" + employeeMaster + "]";
	}

}
