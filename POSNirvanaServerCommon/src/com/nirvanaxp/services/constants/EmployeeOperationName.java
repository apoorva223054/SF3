/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.constants;

public enum EmployeeOperationName {

	ClockIn("Clock In"), ClockOut("Clock Out"), BreakIn("Break In"), BreakOut("Break Out");

	private String operationName;

	private EmployeeOperationName(String opName)
	{
		this.operationName = opName;
	}

	public String getOperationName()
	{
		return operationName;
	}

	public static EmployeeOperationName getByOperationName(String opName)
	{
		if (EmployeeOperationName.ClockIn.getOperationName().equals(opName))
		{
			return EmployeeOperationName.ClockIn;
		}

		if (EmployeeOperationName.ClockOut.getOperationName().equals(opName))
		{
			return EmployeeOperationName.ClockOut;
		}

		if (EmployeeOperationName.BreakIn.getOperationName().equals(opName))
		{
			return EmployeeOperationName.BreakIn;
		}

		if (EmployeeOperationName.BreakOut.getOperationName().equals(opName))
		{
			return EmployeeOperationName.BreakOut;
		}
		return null;
	}

}
