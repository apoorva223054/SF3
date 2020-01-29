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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.global.types.entities.BusinessType;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.Role_;
import com.nirvanaxp.global.types.entities.Timezone;
import com.nirvanaxp.global.types.entities.TransactionalCurrency;
import com.nirvanaxp.global.types.entities.UsersToRole;
import com.nirvanaxp.global.types.entities.UsersToRole_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class GlobalLookUpService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(GlobalLookUpService.class.getName());

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

	/* ************************Business Type ***************************** */

	// get all business Type
	@GET
	@Path("/allBusinessType")
	public String getAllBusinessType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<BusinessType> criteria = builder.createQuery(BusinessType.class);
			Root<BusinessType> businessType = criteria.from(BusinessType.class);
			TypedQuery<BusinessType> query = em.createQuery(criteria.select(businessType));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// *************************End Business Type ****************************/

	/* ************************ Timezone ***************************** */

	// get all Timezone
	@GET
	@Path("/allTimezone")
	public String getAllTimezone(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Timezone> criteria = builder.createQuery(Timezone.class);
			Root<Timezone> timezone = criteria.from(Timezone.class);
			TypedQuery<Timezone> query = em.createQuery(criteria.select(timezone));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// *************************End Timezone ****************************/

	/*
	 * ************************ Transactional Currency
	 * *****************************
	 */

	// get all transaction
	@GET
	@Path("/allTransactionalCurrency")
	public String getAllTransactionalCurrency(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<TransactionalCurrency> criteria = builder.createQuery(TransactionalCurrency.class);
			Root<TransactionalCurrency> transactionalCurrency = criteria.from(TransactionalCurrency.class);
			TypedQuery<TransactionalCurrency> query = em.createQuery(criteria.select(transactionalCurrency));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	// *************************End Transactional Currency
	// ****************************/

	/* ************************ Role ***************************** */

	// get all role
	@GET
	@Path("/allRole")
	public String getAllRole(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> role = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(role));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getRolesByAccountId/{accountId}")
	public String getRolesByAccountId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> role = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(role).where(builder.and(builder.equal(role.get(Role_.accountId), accountId))));
			
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	public UsersToRole getUsersToRole(EntityManager em,Role role)
	{

		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToRole> criteria = builder.createQuery(UsersToRole.class);
			Root<UsersToRole> r = criteria.from(UsersToRole.class);
			TypedQuery<UsersToRole> query = em.createQuery(criteria.select(r).where(builder.and(builder.equal(r.get(UsersToRole_.rolesId), role.getId()),builder.equal(r.get(UsersToRole_.usersId), role.getAccountId()))));
			return query.getSingleResult();
		}catch (Exception e) {
			 logger.severe(e);
		}
		return null;
		
	}
	}
