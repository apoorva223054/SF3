/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PartnerManagemtService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(PartnerManagemtService.class.getName());

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	@GET
	@Path("/getPartnerDetailByReferenceNumber")
	public String allReservationsStatus(@CookieParam(value = "referenceNumber") String referenceNumber) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);

			TypedQuery<POSNPartners> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

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
				
				String logInCookieName = ConfigFileReader.getHostNameFromFile();

				// create cookie
				int thirtyDays = 30 * 24 * 60 * 60 * 1000;
				
				NewCookie cookie = new NewCookie("", posnPartners.getReferenceNumber(), "", logInCookieName, "login cookie", thirtyDays, true);
				Response response = Response.ok(new JSONUtility(httpRequest).convertToJsonIncludeNotNullAndNotEmpty(newPosnPartners)).cookie(cookie).build();
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

	@POST
	@Path("/addPOSNPartner")
	public String addPOSNPartner(POSNPartners posnPartner, @CookieParam(value = "referenceNumber") String referenceNumber) throws Exception
	{

		EntityManager em = null;
		try
		{

			// check validation before adding a partner with us
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			if (posnPartner == null)
			{
				return "No information received by the web service";
			}
			else
			{
				if (posnPartner.getPartnerName() == null || posnPartner.getPartnerName().length() == 0)
				{
					return "Partner name cannot be null or blank";
				}
				if (posnPartner.getDisplayName() == null || posnPartner.getDisplayName().length() == 0)
				{
					return "Display name cannot be null or blank";
				}
				if (posnPartner.getBusinessId() == 0)
				{
					return "Schema name cannot be null or blank";
				}
				if (posnPartner.getAccountId() == 0)
				{
					return "Account id cannot be 0";
				}
				else
				{					
					try
					{
						em.find(Account.class, posnPartner.getAccountId());
					}
					catch (NoResultException e)
					{
						return "Wrong account id sent to server.";
					}

				}

			}

			// generate unique number as refrence number for this new partner
			UUID uniqueRefreneNumber = UUID.randomUUID();
			posnPartner.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			posnPartner.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			posnPartner.setReferenceNumber(uniqueRefreneNumber.toString());
			posnPartner.setStatus("A");
			referenceNumber = uniqueRefreneNumber.toString();

			// create a session id for this partner and put into our database
			// so that this partner can access our web services now,
			// this session never gets deleted from database
			// String sessionId =
			// ManageSecurityToken.generateUniqueAuthTokenForPOSNPartner(posnPartner);
			// UserSession userSession = new
			// UserSession(posnPartner.getAccountId(), 0,
			// posnPartner.getSchemaName(), sessionId, null);
			// userSession.setLoginTime(new TimezoneTime().getGMTTimeInMilis());
			// posnPartner.setSessionId(sessionId);

			// create this new user session for this partner
			// EntityTransaction tx = em.getTransaction();
			// try {
			// // start transaction
			// tx.begin();
			// em.persist(userSession);
			// tx.commit();
			// } catch (RuntimeException e) {
			// // on error, if transaction active,
			// // rollback
			// if (tx != null && tx.isActive()) {
			// tx.rollback();
			// }
			// throw e;
			// }
			//
			// UserSessionHistory userSessionHistory = new
			// UserSessionHistory(posnPartner.getAccountId(),
			// userSession.getId(), 0, posnPartner.getSchemaName(), sessionId,
			// 0, null,
			// new TimezoneTime().getGMTTimeInMilis(), userSession.getIpAddress(),
			// userSession.getVersionInfo());
			// userSession.setLogoutTime(new TimezoneTime().getGMTTimeInMilis());
			// posnPartner.setSessionId(sessionId);
			//
			// tx = em.getTransaction();
			// try {
			// // start transaction
			// tx.begin();
			// em.persist(userSessionHistory);
			// tx.commit();
			// } catch (RuntimeException e) {
			// // on error, if transaction active,
			// // rollback
			// if (tx != null && tx.isActive()) {
			// tx.rollback();
			// }
			// throw e;
			// }
			//
			EntityTransaction tx = em.getTransaction();

			try
			{
				// start transaction
				tx.begin();
				em.persist(posnPartner);
				tx.commit();
			}
			catch (RuntimeException e)
			{
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive())
				{
					tx.rollback();
				}
				throw e;
			}

		}
		catch (Exception e)
		{

			throw e;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return referenceNumber;

	}

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

}
