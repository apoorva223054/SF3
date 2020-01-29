/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.List;

import javax.persistence.EntityManager;
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

import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.Business_;
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
	@Path("/getAllBusiness")
	public String getAllBusiness() throws Exception
	{
		EntityManager em =  null;
		try
		{
			 em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Business> criteria = builder.createQuery(Business.class);
			Root<Business> business = criteria.from(Business.class);
			TypedQuery<Business> query = em.createQuery(criteria.select(business).
					where(builder.equal(business.get(Business_.status), "A"), 
							builder.equal(business.get(Business_.isOnlineApp), "1")));
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getBusinessByCityId/{cityId}")
	public String getBusinessByCityId(@PathParam("cityId") int cityId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			String sql = "select b from Business b "
					+ " where ( b.shippingAddressId in ( select a.id from Address a where a.cityId =?) or "
					+ " b.billiAddressId in ( select a.id from Address a where a.cityId =?) ) and b.isOnlineApp=1";
			TypedQuery<Business> q = em.createQuery(sql,Business.class).setParameter(1, cityId).setParameter(2, cityId);
			List<Business> businessList = q.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(businessList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}


}