/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

public class ServiceOperationsUtility
{

	public static String getOperationName(String operationNameWithServiceName)
	{
		if (operationNameWithServiceName != null && !operationNameWithServiceName.isEmpty() && operationNameWithServiceName.length() > 3)
		{
			int indexOfDash = operationNameWithServiceName.indexOf("_");
			if (indexOfDash > -1)
			{
				return operationNameWithServiceName.substring(indexOfDash + 1);
			}
		}
		throw new IllegalArgumentException("operationNameWithServiceName is invalid: " + operationNameWithServiceName);
	}

	public static String getServiceName(String operationNameWithServiceName)
	{
		if (operationNameWithServiceName != null && !operationNameWithServiceName.isEmpty() && operationNameWithServiceName.length() > 3)
		{
			int indexOfDash = operationNameWithServiceName.indexOf("_");
			if (indexOfDash > -1)
			{

				return operationNameWithServiceName.substring(0, indexOfDash);
			}
		}
		throw new IllegalArgumentException("operationNameWithServiceName is invalid: " + operationNameWithServiceName);
	}

}
