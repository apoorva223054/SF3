/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.exceptions;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;

public class NirvanaXPException extends Exception
{

	private static final long serialVersionUID = -1423056768684128333L;

	private NirvanaServiceErrorResponse response = null;

	public NirvanaXPException(Throwable t)
	{
		super(t);
	}

	public NirvanaXPException(NirvanaServiceErrorResponse response)
	{
		this.response = response;
	}

	@Override
	public String toString()
	{
		if (response != null)
		{
			return response.toString();
		}
		return String.join("", "{\"errorCode\":\"", MessageConstants.ERROR_CODE_UNEXPECTED_EXCEPTION, "\",\"displayMessage\":\"", MessageConstants.ERROR_MESSAGE_UNEXPECTED_DISPLAY_MESSAGE,
				"\",\"technicalErrorMessage\":\"", getCause().getMessage(), "\"}");
	}

}