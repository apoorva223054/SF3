/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;

public abstract class AbstractNirvanaService implements INirvanaService, ServletContextListener
{

	protected abstract NirvanaLogger getNirvanaLogger();

	@Override
	public void contextInitialized(ServletContextEvent arg0)
	{
		getNirvanaLogger().severe("NOT AN ERROR. Initialized ", this.getClass().getName());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		shutdownEntityManagerFactory();
	}

	protected void shutdownEntityManagerFactory()
	{
		try
		{
			LocalSchemaEntityManager.getInstance().closeEntityManagerFactory();
			getNirvanaLogger().severe("Local Entity Manager Factory Shutdown Complete.");

			GlobalSchemaEntityManager.getInstance().closeEntityManagerFactory();
			getNirvanaLogger().severe("Global Entity Manager Factory Shutdown Complete.");
		}
		catch (Throwable t)
		{
			getNirvanaLogger().severe(t, "Error while shutting down entity manager factory: ", t.getMessage());
		}
	}

	public void setUserSession(HttpServletRequest httpRequest, UserSession session)
	{
		httpRequest.setAttribute(NIRVANA_USER_SESSION, session);
	}

}
