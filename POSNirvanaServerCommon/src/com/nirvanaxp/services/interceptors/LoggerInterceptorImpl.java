/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.interceptors;

import java.util.Arrays;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.nirvanaxp.server.util.NirvanaLogger;

@Interceptor
@LoggerInterceptor
public class LoggerInterceptorImpl
{

	private static final NirvanaLogger logger = new NirvanaLogger(LoggerInterceptorImpl.class.getName());

	@AroundInvoke
	public Object logStuff(final InvocationContext ctx) throws Exception
	{
		// skip logging, if this is load balancer call
		if (!ctx.getMethod().toString().contains("Service.isAlive()"))
		{
			logger.fine("Calling:", ctx.getMethod().toString());

			// hide user password
			if (ctx.getMethod().toString().startsWith("public javax.ws.rs.core.Response com.nirvanaxp.services.jaxrs.GlobalLoginService.login")
					|| ctx.getMethod().toString().startsWith("public javax.ws.rs.core.Response com.nirvanaxp.services.jaxrs.CustomerService.login"))
			{
				String str = "";
				Object[] objArray = ctx.getParameters();
				for (int i = 0; i < objArray.length; ++i)
				{
					Object obj = objArray[i];

					if(obj==null)
					{
						str += "null";
						continue;
					}
					
					if (i == 1)
					{
						str += "password hidden";
					}
					else
					{						
						str += obj.toString();					
					}

					if (i < objArray.length)
					{
						str += ", ";
					}
				}
				logger.fine("Parameters:", str);
			}
			else
			{
				logger.fine("Parameters:", Arrays.toString(ctx.getParameters()));
			}
			logger.fine("Context:", ctx.getContextData().toString());
		}

		Object returnValue = ctx.proceed();
		logger.fine("Called: ", ctx.getMethod().toString());
		return returnValue;

	}

}
