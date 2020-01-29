/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.employee.EmployeesToEmployeesOperation;
import com.nirvanaxp.types.entities.user.User;

@XmlRootElement(name = "EmployeeToEmployeeOperationPacket")
public class EmployeeToEmployeeOperationPacket extends PostPacket
{
	private List<EmployeesToEmployeesOperation> employeesToEmployeesOperation;

	public List<EmployeesToEmployeesOperation> getEmployeesToEmployeesOperation() {
		return employeesToEmployeesOperation;
	}

	public void setEmployeesToEmployeesOperation(
			List<EmployeesToEmployeesOperation> employeesToEmployeesOperation) {
		this.employeesToEmployeesOperation = employeesToEmployeesOperation;
	}
	
	private User user;

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
	

	

}
