/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.annotation.WebListener;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.ws.WebServiceException;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServerConfigService extends AbstractNirvanaService
{

	/**  */
	private static final NirvanaLogger logger = new NirvanaLogger(ServerConfigService.class.getName());

	/**
	 * 
	 *
	 * @return 
	 */
	@GET
	@Path("/downloadConfigFile")
	public Response downloadFile()
	{

		try
		{
			String filePath = ConfigFileReader.getPathForOneAppForAllConfigFile();
			File file = new File(filePath);
			ResponseBuilder response = Response.ok((Object) file);
			response.header("Content-Disposition", "attachment;filename=DisplayName=ServerConfig.txt");
			return response.build();

		}
		catch (Exception ex)
		{
			System.err.println(ex);
			throw new WebServiceException(ex);
		}
	}

	/**
	 * 
	 *
	 * @return 
	 */
	@GET
	@Path("/downloadConfigFileAsByteStream")
	public byte[] downloadFileAsByteArray()
	{

		try
		{
			String filePath = ConfigFileReader.getPathForOneAppForAllConfigFile();
			File file = new File(filePath);

			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream inputStream = new BufferedInputStream(fis);
			byte[] fileBytes = new byte[(int) file.length()];
			inputStream.read(fileBytes);
			inputStream.close();

			return fileBytes;
		}
		catch (Exception ex)
		{
			System.err.println(ex);
			throw new WebServiceException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@GET
	@Path("/isAlive")
	@Override
	public boolean isAlive()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}
}
