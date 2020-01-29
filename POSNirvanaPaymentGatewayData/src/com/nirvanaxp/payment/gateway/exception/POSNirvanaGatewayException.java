/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.exception;

public class POSNirvanaGatewayException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6266350920480915238L;
	String msg;

	public POSNirvanaGatewayException(String msg)
	{
		this.msg = msg;
	}

	public String toString()
	{
		return msg;
	}

	public String getMessage()
	{
		return msg;
	}
}