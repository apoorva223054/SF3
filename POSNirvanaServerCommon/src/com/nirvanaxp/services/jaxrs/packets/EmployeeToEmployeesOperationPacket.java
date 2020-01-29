/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.employee.EmployeesToEmployeesOperation;

@XmlRootElement(name = "EmployeeToEmployeesOperationPacket")
public class EmployeeToEmployeesOperationPacket extends PostPacket
{
	private EmployeesToEmployeesOperation employeesToEmployeesOperation;

	public EmployeesToEmployeesOperation getEmployeesToEmployeesOperation() {
		return employeesToEmployeesOperation;
	}

	public void setEmployeesToEmployeesOperation(
			EmployeesToEmployeesOperation employeesToEmployeesOperation) {
		this.employeesToEmployeesOperation = employeesToEmployeesOperation;
	}
}
