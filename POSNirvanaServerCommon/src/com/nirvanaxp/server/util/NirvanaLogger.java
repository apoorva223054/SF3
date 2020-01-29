/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

@SuppressWarnings("restriction")
public final class NirvanaLogger
{

	private static final String DELIMITER = " ";

	private Logger logger = null;

	private String callingClass = null;

	public NirvanaLogger(String className)
	{
		callingClass = className;
		logger = Logger.getLogger(className);
	}

	/*********** FINE *********************/

	public void fine(HttpServletRequest httpRequest, String... args)
	{
		log(Level.FINE, httpRequest, null, args);
	}

	public void fine(String... args)
	{
		log(Level.FINE, null, null, args);
	}

	/*********** INFO *********************/

	public void info(HttpServletRequest httpRequest, String... args)
	{
		log(Level.INFO, httpRequest, null, args);
	}

	public void info(Throwable t, String... args)
	{
		log(Level.INFO, null, t, args);
	}

	public void info(String... args)
	{
		log(Level.INFO, null, null, args);
	}

	/*********** WARN *********************/

	public void warn(HttpServletRequest httpRequest, String... args)
	{
		log(Level.WARNING, httpRequest, null, args);
	}

	public void warn(Throwable t, String... args)
	{
		log(Level.WARNING, null, t, args);
	}

	/*********** SEVERE *********************/
	public void severe(HttpServletRequest httpRequest, String... args)
	{
		log(Level.SEVERE, httpRequest, null, args);
	}

	public void severe(Throwable t, String... args)
	{
		log(Level.SEVERE, null, t, args);
	}

	public void severe(HttpServletRequest httpRequest, Throwable t)
	{
		log(Level.SEVERE, httpRequest, t);
	}

	public void severe(HttpServletRequest httpRequest, Throwable t, String... args)
	{
		log(Level.SEVERE, httpRequest, t, args);
	}

	public void severe(String... args)
	{
		log(Level.SEVERE, null, null, args);
	}
	
	public boolean isLoggable(Level level)
	{
		return logger.isLoggable(level);
	}

	private void log(Level level, HttpServletRequest httpRequest, Throwable t, String... args)
	{
		try
		{
			String details = getHttpRequestDetails(httpRequest);

			if (logger.isLoggable(level))
			{
				String message = join(t, details, args);

				LogRecord lr = new LogRecord(level, message);
				lr.setSourceClassName(logger.getName());
				lr.setSourceMethodName(getSourceMethodName());

				if (t != null)
				{
					lr.setThrown(t);
				}

				logger.log(lr);
			}
		}
		catch (Throwable t1)
		{
			logger.log(Level.SEVERE, "EXCEPTION IN LOGGER!!", t1);
		}
	}

	private String getSourceMethodName()
	{

		JavaLangAccess access = SharedSecrets.getJavaLangAccess();
		Throwable throwable = new Throwable();
		int depth = access.getStackTraceDepth(throwable);

		StackTraceElement previous = null;
		for (int i = 0; i < depth; ++i)
		{
			// Calling getStackTraceElement directly prevents the VM
			// from paying the cost of building the entire stack frame.
			previous = access.getStackTraceElement(throwable, i);

			if (previous.getClassName().equals(callingClass))
			{
				break;
			}

		}

		if (previous != null)
		{
			return previous.getMethodName();
		}

		StackTraceElement frame = access.getStackTraceElement(throwable, depth);
		return frame.getMethodName();
	}

	private String join(Throwable t, String details, String... args)
	{
		if (t != null)
		{
			return String.join("\nException Message: ", t.getMessage(), "\n", details, "\n", String.join(DELIMITER, args));
		}
		else
		{
			return String.join(DELIMITER, details, String.join(DELIMITER, args));
		}
	}

	private String getHttpRequestDetails(HttpServletRequest httpRequest)
	{

		if (httpRequest != null)
		{
			String requestData = String.join(DELIMITER, "\nRemote IP Address: ", getRemoteAddress(httpRequest) + "\n");
			// is there a UserSession object
			Object obj = httpRequest.getAttribute(AbstractNirvanaService.NIRVANA_USER_SESSION);
			
			if (obj != null)
			{
				UserSession session = (UserSession) obj;
				StringBuilder data = new StringBuilder().append("Local IP: " + session.getIpAddress() + "\n").append("Login Time: " + session.getLoginTime() + "\n")
						.append("Merchant Id: " + session.getMerchant_id() + "\n").append("Schema: " + session.getSchema_name() + "\n").append("Session Id: " + session.getSession_id() + "\n")
						.append("Version Id: " + session.getVersionInfo() + "\n").append("User Id: " + session.getUser_id() + "\n");
				if (session.getDeviceInfo() != null)
				{
					data.append("Device Id: " + session.getDeviceInfo().getDeviceId() + "\n").append("Device Name: " + session.getDeviceInfo().getDeviceName() + "\n")
							.append("Device Type: " + session.getDeviceInfo().getDeviceType() != null ? session.getDeviceInfo().getDeviceType().getDisplayName() : null + "\n");
				}

				requestData += data.toString();
			}

			return requestData;
		}

		return "No HttpRequest Data\n";
	}

	private String getRemoteAddress(HttpServletRequest httpRequest)
	{
		try
		{
			String IP = httpRequest.getHeader("x-forwarded-for");
			if (IP==null)
			{
				logger.log(Level.SEVERE, "Could not get remote IP from Forwarded header");
			
				return httpRequest.getRemoteAddr();
			}
			else
			{
				return IP;
			}
		}
		catch(Throwable t)
		{
			logger.log(Level.SEVERE, "Could not extract remote address", t);
		}
		return null;
	}

	public void finest(String... args)
	{
		log(Level.FINEST, null, null, args);
	}

	public void finer(String... args)
	{
		log(Level.FINER, null, null, args);
	}

	public String extractBuffer(HttpServletRequest httpRequest)
	{
		return getHttpRequestDetails(httpRequest);
	}
}
