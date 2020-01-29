/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.exceptions;

import javax.security.auth.login.LoginException;

public class POSNirvanaLoginException extends LoginException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5112889305487389954L;

	public POSNirvanaLoginException(String msg)
	{
		super(msg);
	}

	public String toString()
	{
		return getMessage();
	}

}
