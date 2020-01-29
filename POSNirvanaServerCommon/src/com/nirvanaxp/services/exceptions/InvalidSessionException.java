/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.exceptions;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;

public class InvalidSessionException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4022798857853052250L;

	public InvalidSessionException()
	{
		super();
	}

	@Override
	public String getMessage()
	{
		return MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE;
	}

	@Override
	public String toString()
	{
		return String.join("", "{\"errorCode\":\"", MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION, "\",\"displayMessage\":\"", MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE,
				"\",\"technicalErrorMessage\":\"Session Invalid\"}");
	}

}
