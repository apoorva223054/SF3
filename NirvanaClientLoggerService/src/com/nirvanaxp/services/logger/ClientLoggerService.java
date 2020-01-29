/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.logger;

import javax.persistence.EntityManager;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.global.types.entities.ClientLog;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientLoggerService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientLoggerService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(ClientLoggerService.class.getName());

	/**
	 * Checks if is alive.
	 *
	 * @return true, if is alive
	 */
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/**
	 * Save log.
	 *
	 * @param log
	 *            the log
	 */
	@POST
	@Path("/saveLog")
	public void saveLog(ClientLog log)
	{
		if (log != null)
		{

			log.update(httpRequest);

			if (log.getLogLevel() == null || log.getLogLevel().trim().isEmpty())
			{
				log.setLogLevel("severe");
			}

			// log to file
			switch (log.getLogLevel())
			{
			case "info":
			{
				logger.info(httpRequest, log.toString());
				break;
			}
			case "fine":
			{
				logger.fine(httpRequest, log.toString());
				break;
			}
			case "warn":
			{
				logger.warn(httpRequest, log.toString());
				break;
			}
			case "severe":
			default:
			{
				logger.severe(httpRequest, log.toString());
				break;
			}
			}

			new Thread()
			{

				@Override
				public void run()
				{
					// log to database
					EntityManager em = null;

					try
					{
						em = GlobalSchemaEntityManager.getInstance().getEntityManager();
						GlobalSchemaEntityManager.persist(em, log);
					}
					catch (Exception e)
					{
						// todo shlok need
						// handle proper Exception remove e
						logger.severe(httpRequest, e, "Could not Save log to Database");
					}
					finally
					{
						GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
					}
				}

			}.start();

		}
		else
		{
			logger.severe(String.join(" ", "\nRemote IP Address: ", httpRequest != null ? httpRequest.getRemoteAddr() : "Null HTTP", "Client sent null log"));
		}
	}

}
