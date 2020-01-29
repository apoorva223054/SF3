/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.persistence.EntityManager;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.util.ItemsServiceBean;

@WebListener
@Path("/CustomerService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	@GET
	@Path("/getItemsByCategoryId/{categoryId}/{locationId}")
	public String getItemsByCategoryId(@PathParam("categoryId") String categoryId, @PathParam("locationId") String locationId)
			throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemsByCategoryId(em, categoryId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getItemById/{id}")
	public String getItemById(@PathParam("id") String itemId) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemById(em, itemId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getItemByIdForCustomer/{itemId}")
	public String getItemByIdForCustomer(@PathParam("itemId") String itemId) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			return new JSONUtility(httpRequest).convertToJsonString(new ItemsServiceBean().getItemByIdForCustomer(em, itemId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

}
