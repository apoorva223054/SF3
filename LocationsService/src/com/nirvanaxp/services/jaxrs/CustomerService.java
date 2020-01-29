/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.types.entities.TransactionalCurrency;
import com.nirvanaxp.types.entities.salestax.OrderSourceGroupToSalesTax;
import com.nirvanaxp.types.entities.salestax.SalesTax;

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
	@Path("/getRootLocations")
	public String getRootLocations() throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new LocationsServiceBean().getRootLocations(httpRequest, em));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getLocationsById/{id}")
	public String getLocationsById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			return new JSONUtility(httpRequest).convertToJsonString(new LocationsServiceBean().getLocationsById(httpRequest, em, id));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getRootLocationsByBusiness/{businessId}")
	public String getRootLocationsByAccount(@PathParam("businessId") int businessId) throws Exception
	{

		EntityManager em = null;
		
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, null);

			return new JSONUtility(httpRequest).convertToJsonString(new LocationsServiceBean().getRootLocationsByAccount(httpRequest, em, businessId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	@GET
	@Path("/getNirvanaXpRootLocationsByCountryId/{countryId}")
	public String getNirvanaXpRootLocationsByCountryId(@PathParam("countryId") int countryId) throws Exception
	{
		// sessionId = "bf1f878ff4386e53c0a4c79545494eea";
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumberForNirvanaXP(httpRequest, auth_token);
			countryId = isValidCountry(em, countryId);
			return new JSONUtility(httpRequest).convertToJsonString(new LocationsServiceBean().getNirvanaXpRootLocationsByCountryId(httpRequest, em, countryId));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
	private int isValidCountry(EntityManager em, int countryId)
	{

		String queryString = "SELECT a.country_id FROM address a JOIN locations l ON l.address_id = a.id WHERE a.country_id = ? AND (l.locations_id ='0' or l.locations_id is null) AND l.status =  'A'";

		@SuppressWarnings("rawtypes")
		List resultList = em.createNativeQuery(queryString).setParameter(1, countryId).getResultList();
		int validCountryId = 0;
		if (resultList.size() > 0)
		{
			for (Object objRow : resultList)
			{
				validCountryId = (int) objRow;
			}
		}
		else
		{
			queryString = "select c.id from countries c where c.name='UNITED STATES'";
			@SuppressWarnings("rawtypes")
			List resultListDefault = em.createNativeQuery(queryString).getResultList();
			if (resultListDefault.size() > 0)
			{
				for (Object objRow : resultListDefault)
				{
					validCountryId = (int) objRow;
				}
			}
		}
		return validCountryId;
	}
	
	@GET
	@Path("/getTransactionalCurrencyById/{id}")
	public String getTransactionalCurrencyById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);	
			String queryString = "select tc from TransactionalCurrency tc where tc.id = ? ";
			TypedQuery<TransactionalCurrency> query = em.createQuery(queryString, TransactionalCurrency.class).setParameter(1, id);
			List<TransactionalCurrency> resultSet = query.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(resultSet);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}


	@GET
	@Path("/getSalesTaxByOrderSourceGroupById/{id}")
	public String getSalesTaxByOrderSourceGroupById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;
		String auth_token	=	httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);	
			String queryString = "select osc from OrderSourceGroupToSalesTax osc where osc.sourceGroupId = ? and osc.status != 'D' ";
			TypedQuery<OrderSourceGroupToSalesTax> query = em.createQuery(queryString, OrderSourceGroupToSalesTax.class).setParameter(1, id);
			List<OrderSourceGroupToSalesTax> resultSet = query.getResultList();
			List<SalesTax> salesTaxs = null;
			if(resultSet != null && resultSet.size()>0){
				salesTaxs = new ArrayList<SalesTax>();
				for(OrderSourceGroupToSalesTax toSalesTax : resultSet){
					SalesTax salesTax = (SalesTax) new CommonMethods().getObjectById("SalesTax", em,SalesTax.class,toSalesTax.getTaxId());
					if(!salesTax.getStatus().equals("D") && salesTax.getIsItemSpecific()!=1 && !salesTax.getStatus().equals("I")){
						salesTaxs.add(salesTax);
					}
					
				}
			}
			
			return new JSONUtility(httpRequest).convertToJsonString(salesTaxs);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	 @Path("/getAllLocationToFunctionForCustomerByLocationId/{locationId}")
	 public String getAllLocationToFunctionForCustomerByLocationId(@PathParam("locationId") String locationId) throws Exception
	 {
	  EntityManager em = null;
	  String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
	  try
	  {
	   
	   em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token); 
	   
	   return new JSONUtility(httpRequest).convertToJsonString(new LocationsServiceBean().getAllLocationToFunctionForCustomerByLocationId(httpRequest, em, locationId));
	  }
	  finally
	  {
	   LocalSchemaEntityManager.getInstance().closeEntityManager(em);
	  }

	 }

}