/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.employee.EmployeeOperation;

@XmlRootElement(name = "EmployeeOperationPacket")
public class EmployeeOperationPacket extends PostPacket
{
	private EmployeeOperation employeeOperation;

	public EmployeeOperation getEmployeeOperation()
	{
		return employeeOperation;
	}

	public void setEmployeeOperation(EmployeeOperation employeeOperation)
	{
		this.employeeOperation = employeeOperation;
	}

	@Override
	public String toString()
	{
		return "EmployeeOperationPacket [employeeOperation=" + employeeOperation + "]";
	}

}
