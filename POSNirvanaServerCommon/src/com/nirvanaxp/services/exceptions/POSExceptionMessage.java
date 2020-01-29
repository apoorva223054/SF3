/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.exceptions;

public class POSExceptionMessage
{

	private String message;

	private int code;

	public POSExceptionMessage(String message, int code)
	{
		super();
		this.message = message;
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public int getCode()
	{
		return code;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

}
