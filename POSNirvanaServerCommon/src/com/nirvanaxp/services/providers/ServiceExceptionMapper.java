/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.providers;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.exceptions.POSNirvanaLoginException;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<Throwable>
{

	@Context
	HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(ServiceExceptionMapper.class.getName());

	@Override
	public Response toResponse(Throwable throwable)
	{

		NirvanaServiceErrorResponse message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNEXPECTED_EXCEPTION, MessageConstants.ERROR_MESSAGE_UNEXPECTED_DISPLAY_MESSAGE + ": "
				+ throwable.getMessage(), throwable.getMessage());

		if (throwable instanceof InvalidSessionException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_INVALID_SESSION_EXCEPTION, MessageConstants.ERROR_MESSAGE_INVALID_SESSION_DISPLAY_MESSAGE, throwable.getMessage());
		}

		else if (throwable instanceof POSNirvanaLoginException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LOGIN_EXCEPTION, MessageConstants.ERROR_MESSAGE_LOGIN_EXCEPTION + ": " + throwable.getMessage(),
					throwable.getMessage());
		}

		else if (throwable instanceof NoResultException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NO_RESULT_EXCEPTION, MessageConstants.ERROR_MESSAGE_NO_RESULT_DISPLAY_MESSAGE, throwable.getMessage());
		}

		else if (throwable instanceof NonUniqueResultException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_NON_UNIQUE_RESULT_EXCEPTION, MessageConstants.ERROR_MESSAGE_NON_UNIQUE_RESULT_DISPLAY_MESSAGE, throwable.getMessage());
		}

		else if (throwable instanceof FileNotFoundException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_FILE_NOT_FOUND, MessageConstants.ERROR_MESSAGE_FILE_NOT_FOUND, throwable.getMessage());

		}

		else if (throwable instanceof IOException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_IO_EXCEPTION, MessageConstants.ERROR_MESSAGE_IO_EXCEPTION, throwable.getMessage());
		}

//		else if (throwable instanceof DatabaseException)
//		{
//			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_DATABASE_EXCEPTION, MessageConstants.ERROR_MESSAGE_DATABASE_EXCEPTION, throwable.getMessage());
//		}

		else if (throwable instanceof IllegalArgumentException)
		{
			message = new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_BAD_INPUT_EXCEPTION, MessageConstants.ERROR_MESSAGE_BAD_INPUT_EXCEPTION + ": " + throwable.getMessage(),
					throwable.getMessage());
		}

		else if (throwable instanceof NirvanaXPException)
		{
			return Response.ok(((NirvanaXPException) throwable).toString()).build();
		}

		
		logger.severe(httpRequest, throwable, message.toString());
		
		return Response.ok(message.toString()).build();

	}

}
