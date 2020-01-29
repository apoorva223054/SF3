/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.Business_;
import com.nirvanaxp.global.types.entities.Payment;
import com.nirvanaxp.global.types.entities.Payment_;
import com.nirvanaxp.global.types.entities.UsersToBusiness;
import com.nirvanaxp.global.types.entities.UsersToBusiness_;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.MathUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.BusinessTypePacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.storeForward.StoreForwardUtilityForGlobal;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class GlobalBusinessService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(GlobalBusinessService.class.getName());

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

	@POST
	@Path("/add")
	public String add(BusinessTypePacket businessTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{

			Business business = businessTypePacket.getBusiness();

			// for adding new business put default value for max devices
			if (business.getMaxAllowedDevices() == 0)
			{
				// read from data files and initialize max devices
				String maxAllowedDevices = ConfigFileReader.getMaxAllowedDevicesForBussinessFromFile();
				if (maxAllowedDevices != null && maxAllowedDevices.trim().length() > 0)
				{
					int maxDevicesAllowedForAccount = Integer.parseInt(maxAllowedDevices);
					business.setMaxAllowedDevices(maxDevicesAllowedForAccount);
				}
			}
			
			if(business.getBilliAddressId().getId()==null)
				business.getBilliAddressId().setId(new StoreForwardUtilityForGlobal().generateDynamicBigIntId(em,  httpRequest, "address"));
			if(business.getShippingAddressId().getId()==null)
				business.getShippingAddressId().setId(new StoreForwardUtilityForGlobal().generateDynamicBigIntId(em,  httpRequest, "address"));
			em.getTransaction().begin();
			business = em.merge(business);
			em.getTransaction().commit();
			
			businessTypePacket.setBusiness(business);
			enterPOSNPartner(em, business);
			// save users to business relationships
			if (businessTypePacket.getGlobalUserIdList() != null)
			{
				for (String globalUserId : businessTypePacket.getGlobalUserIdList())
				{
					UsersToBusiness usersToBusiness = new UsersToBusiness(businessTypePacket.getUserId(), businessTypePacket.getBusiness().getId(), businessTypePacket.getUserId(), globalUserId);
					usersToBusiness.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToBusiness.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					GlobalSchemaEntityManager.persist(em, usersToBusiness);

				}
			}
//			String json = new StoreForwardUtilityForGlobal().returnJsonPacket(businessTypePacket, "BusinessTypePacket", httpRequest);
			// call synchPacket for store forward
//			callSynchPacketsWithServer(json, httpRequest, Integer.parseInt(businessTypePacket.getLocationId()), Integer.parseInt(businessTypePacket.getMerchantId()));
//			new StoreForwardUtilityForGlobal().callSynchPacketsWithServer(json, httpRequest, businessTypePacket.get, accountId);

			return new JSONUtility(httpRequest).convertToJsonString(businessTypePacket);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/update")
	public String update(BusinessTypePacket businessTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			EntityManager emLocal = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,sessionId);
			String locationId = new CommonMethods().getLocationId(emLocal);
			String json = new StoreForwardUtility().returnJsonPacket(businessTypePacket, "BusinessTypePacket",httpRequest);
			logger.severe("json====================================================================="+json);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationId,
					Integer.parseInt(businessTypePacket.getMerchantId()));
			
			// save the address
			if (businessTypePacket.getBusiness() != null)
			{
				businessTypePacket.getBusiness().setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				// if max allowed packet is 0, the set the value that was
				// already in database
				if (businessTypePacket.getBusiness().getMaxAllowedDevices() == 0)
				{
					Business bussinessFromDatabase = em.find(Business.class, businessTypePacket.getBusiness().getId());
					businessTypePacket.getBusiness().setMaxAllowedDevices(bussinessFromDatabase.getMaxAllowedDevices());
				}
				
				GlobalSchemaEntityManager.merge(em, businessTypePacket.getBusiness());
			}
             
			return new JSONUtility(httpRequest).convertToJsonString(businessTypePacket);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/delete")
	public boolean delete(BusinessTypePacket businessTypePacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			// delete the address
			if (businessTypePacket.getAddress() != null)
			{

//				GlobalSchemaEntityManager.removeAfterMerge(em, businessTypePacket.getAddress());
//				GlobalSchemaEntityManager.removeAfterMerge(em, businessTypePacket.getBusiness());

			}

			return true;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getBusinessByAccountId/{accountsId}")
	public String getBusinessByAccountId(@PathParam("accountsId") int accountsId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Business> criteria = builder.createQuery(Business.class);
			Root<Business> business = criteria.from(Business.class);
			TypedQuery<Business> query = em.createQuery(criteria.select(business).where(builder.equal(business.get(Business_.accountId), accountsId)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getBusinessById/{id}")
	public String getBusinessById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			Business business = getBusinessById(id, em);
			return new JSONUtility(httpRequest).convertToJsonString(business);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getBusinessBasedOnLocation/{latitude}/{longitude}/{distance}")
	public String getBusinessById(@PathParam("latitude") double latitude, @PathParam("longitude") double longitude, double radius, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			List<Business> businessesList = getAllBusiness(em);
			List<Business> nearestBusinessList = new ArrayList<Business>();
			for (Business business : businessesList)
			{
				double bussinessLatitude = Double.parseDouble(business.getLattitude());
				double bussinessLongitude = Double.parseDouble(business.getLongitude());
				double distanceBetweenUserAndBussiness = MathUtility.calculateDistance(latitude, longitude, bussinessLatitude, bussinessLongitude, 'K');
				if (distanceBetweenUserAndBussiness <= radius)
				{
					nearestBusinessList.add(business);
				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(nearestBusinessList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllbusiness")
	public String getAllBusiness(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			return new JSONUtility(httpRequest).convertToJsonString(getAllBusiness(em));
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	

	@GET
	@Path("/getAllBusinessForCutomerAppWithOutSession")
	public String getAllBusinessForCutomerAppWithOutSession() throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{
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

	private List<Business> getAllBusiness(EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Business> criteria = builder.createQuery(Business.class);
		Root<Business> business = criteria.from(Business.class);
		TypedQuery<Business> query = em.createQuery(criteria.select(business));
		return query.getResultList();
	}

	private Business getBusinessById(int id, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Business> criteria = builder.createQuery(Business.class);
		Root<Business> business = criteria.from(Business.class);

		TypedQuery<Business> query = em.createQuery(criteria.select(business).where(builder.equal(business.get(Business_.id), id)));
		return query.getSingleResult();
	}

	@GET
	@Path("/getBusinessByUserId/{userId}")
	public String getBusinessByUserId(@PathParam("userId") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			// get users to business list
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToBusiness> criteria = builder.createQuery(UsersToBusiness.class);
			Root<UsersToBusiness> root = criteria.from(UsersToBusiness.class);
			TypedQuery<UsersToBusiness> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(UsersToBusiness_.usersId), userId)));
			List<UsersToBusiness> usersToBusinessesList = query.getResultList();

			List<Business> businessesList = new ArrayList<Business>();
			if (usersToBusinessesList != null)
			{
				for (UsersToBusiness usersToBusiness : usersToBusinessesList)
				{
					if (usersToBusiness != null)
					{
						Business business = null;
						try
						{
							business = getBusinessById(usersToBusiness.getBusinessId(), em);
						}
						catch (Exception e)
						{
							
							logger.severe(httpRequest, e);
						}
						if (business != null)
						{
							businessesList.add(business);
						}
					}
				}
			}
			if (businessesList == null || businessesList.size() == 0)
			{
				return "[]";
			}
			return new JSONUtility(httpRequest).convertToJsonString(businessesList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/insertUsersToBusiness")
	public String insertUsersToBusiness(UsersToBusiness usersToBusiness, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			usersToBusiness.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToBusiness.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			GlobalSchemaEntityManager.merge(em, usersToBusiness);

			return new JSONUtility(httpRequest).convertToJsonString(usersToBusiness);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllPaymentByAccountID/{accountsId}")
	public String getAllBusinessByAccountID(@PathParam("accountsId") int accountsId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Payment> criteria = builder.createQuery(Payment.class);
			Root<Payment> payment = criteria.from(Payment.class);
			TypedQuery<Payment> query = em.createQuery(criteria.select(payment).where(builder.equal(payment.get(Payment_.accountId), accountsId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private POSNPartners enterPOSNPartner(EntityManager em, Business business)
	{
		POSNPartners posnPartner = new POSNPartners();
		posnPartner.setBusinessId(business.getId());
		posnPartner.setAccountId(business.getAccountId());
		UUID uniqueRefreneNumber = UUID.randomUUID();
		posnPartner.setReferenceNumber(uniqueRefreneNumber.toString());
		posnPartner.setPartnerName("BusinessApp");
		posnPartner.setDisplayName("BusinessApp");

		// generate unique number as refrence number for this new partner
		posnPartner.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		posnPartner.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		posnPartner.setStatus("A");

		GlobalSchemaEntityManager.persist(em, posnPartner);
		
		POSNPartners posnCustomerPartner = new POSNPartners();
		posnCustomerPartner.setBusinessId(business.getId());
		posnCustomerPartner.setAccountId(business.getAccountId());
		UUID uniqueCustomerRefreneNumber = UUID.randomUUID();
		posnCustomerPartner.setReferenceNumber(uniqueCustomerRefreneNumber.toString());
		posnCustomerPartner.setPartnerName("CustomerApp");
		posnCustomerPartner.setDisplayName("CustomerApp");

		// generate unique number as refrence number for this new partner
		posnCustomerPartner.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		posnCustomerPartner.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		posnCustomerPartner.setStatus("A");

		GlobalSchemaEntityManager.persist(em, posnCustomerPartner);
		
		POSNPartners posnEwardsPartner = new POSNPartners();
		posnEwardsPartner.setBusinessId(business.getId());
		posnEwardsPartner.setAccountId(business.getAccountId());
		UUID uniqueEwardsRefreneNumber = UUID.randomUUID();
		posnEwardsPartner.setReferenceNumber(uniqueEwardsRefreneNumber.toString());
		posnEwardsPartner.setPartnerName("Ewards");
		posnEwardsPartner.setDisplayName("Ewards");

		// generate unique number as refrence number for this new partner
		posnEwardsPartner.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		posnEwardsPartner.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		posnEwardsPartner.setStatus("A");

		GlobalSchemaEntityManager.persist(em, posnEwardsPartner);
		
		return posnPartner;

	}
}
