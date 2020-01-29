/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;

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

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}


	@GET
	@Path("/getReferenceNumberByAccountIdAndBusinessId/{accountId}/{businessId}")
	public Response getReferenceNumberByAccountIdAndBusinessId(@PathParam("accountId") int accountId, 
			@PathParam("businessId") int businessId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);

			TypedQuery<POSNPartners> query = em.createQuery(criteria.select(root).where(
					builder.equal(root.get(POSNPartners_.accountId), accountId), 
					builder.equal(root.get(POSNPartners_.businessId), businessId), 
					builder.equal(root.get(POSNPartners_.partnerName), "CustomerApp")));

			try
			{
				POSNPartners posnPartners = query.getSingleResult();
				
				POSNPartners newPosnPartners = new POSNPartners();
				newPosnPartners.setAccountId(posnPartners.getAccountId());
				newPosnPartners.setBusinessId(posnPartners.getBusinessId());
				newPosnPartners.setPartnerName(posnPartners.getPartnerName());
				newPosnPartners.setDisplayName(posnPartners.getDisplayName());
				
				Response response = Response.ok(new JSONUtility(httpRequest).convertToJsonIncludeNotNullAndNotEmpty(newPosnPartners)).header(INirvanaService.NIRVANA_AUTH_TOKEN, posnPartners.getReferenceNumber()).build();
				return response;
				
			}
			catch (NoResultException e)
			{
				//return "Unauthorized Access.";

				logger.severe(httpRequest, e, "Unauthorized Access.");
				return Response.ok(MessageConstants.ERROR_MESSAGE_UNAUTHORIZED_ACCESS).build();
			
			}

		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, e.getMessage());
			throw e;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getPartnerDetailByReferenceNumber")
	public String getPartnerDetailByReferenceNumber() throws Exception
	{
		EntityManager em = null;
		// dont chnge this line else all apps stop working
		// ********************************************************
		String auth_token	=	httpRequest.getHeader("auth-token");
		// ********************************************************	 

		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);

			TypedQuery<POSNPartners> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), auth_token)));

			try
			{
				POSNPartners posnPartners = query.getSingleResult();
				return new JSONUtility(httpRequest).convertToJsonString(posnPartners);
			}
			catch (NoResultException e)
			{
				return "Unauthorized Access.";
			}

		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, e.getMessage());
			throw e;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	


}