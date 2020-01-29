/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;

@XmlRootElement(name = "NirvanaServiceErrorResponse")
public final class NirvanaServiceErrorResponse implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4289946342230513700L;

	public static final NirvanaServiceErrorResponse INVALID_SESSION_EXCEPTION = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION,
			MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE, null);

	private String errorCode;

	private String displayMessage;

	private String technicalErrorMessage;

	public NirvanaServiceErrorResponse(String code, String displayMessage, String technicalErrorMessage)
	{
		this.errorCode = code;
		this.displayMessage = displayMessage;
		this.technicalErrorMessage = technicalErrorMessage;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public String getDisplayMessage()
	{
		return displayMessage;
	}

	public String getTechnicalErrorMessage()
	{
		return technicalErrorMessage;
	}

	@Override
	public String toString()
	{
		return String.join("", "{\"errorCode\":\"", errorCode, "\",\"displayMessage\":\"", displayMessage, "\",\"technicalErrorMessage\":\"", technicalErrorMessage, "\"}");
	}

}
