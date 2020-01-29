/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.mail.SMTPCredentials;
import com.nirvanaxp.common.utils.mail.SendMail;
import com.nirvanaxp.common.utils.relationalentity.EntityRelationshipManager;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.DeliveryRulesPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationListPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationSettingPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationToFunctionPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationToLocationDetailsPacket;
import com.nirvanaxp.services.jaxrs.packets.LocationsToImagesPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.services.jaxrs.packets.RootLocationPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.TransactionalCurrency;
import com.nirvanaxp.types.entities.application.Application;
import com.nirvanaxp.types.entities.custom.LocationWithOrderInfo;
import com.nirvanaxp.types.entities.email.EmailTemplate;
import com.nirvanaxp.types.entities.email.EmailTemplate_;
import com.nirvanaxp.types.entities.inventory.LocationsToSupplier;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationBatchTiming;
import com.nirvanaxp.types.entities.locations.LocationBatchTiming_;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.locations.LocationSetting_;
import com.nirvanaxp.types.entities.locations.LocationToApplication;
import com.nirvanaxp.types.entities.locations.LocationToLocationDetails;
import com.nirvanaxp.types.entities.locations.LocationsDetail;
import com.nirvanaxp.types.entities.locations.LocationsToFunction;
import com.nirvanaxp.types.entities.locations.LocationsToImages;
import com.nirvanaxp.types.entities.locations.MetaBusinessTypeToApplication;
import com.nirvanaxp.types.entities.locations.MetaBusinessTypeToApplication_;
import com.nirvanaxp.types.entities.orders.DeliveryRules;
import com.nirvanaxp.types.entities.payment.Paymentgateway;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToRole;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class LocationService extends AbstractNirvanaService {

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(LocationService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger() {
		return logger;
	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive() {
		return true;
	}

	@GET
	@Path("/getRootLocationsByBusiness/{businessId}")
	public String getRootLocationsByAccount(@PathParam("businessId") int businessId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getRootLocationsByAccount(httpRequest, em, businessId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getNirvanaXpRootLocationsByCountryId/{countryId}")
	public String getNirvanaXpRootLocationsByCountryId(@PathParam("countryId") int countryId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		// sessionId = "bf1f878ff4386e53c0a4c79545494eea";
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			countryId = isValidCountry(em, countryId);
			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getNirvanaXpRootLocationsByCountryId(httpRequest, em, countryId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private int isValidCountry(EntityManager em, int countryId) {

		String queryString = "SELECT a.country_id FROM address a JOIN locations l ON l.address_id = a.id WHERE a.country_id = ? AND (l.locations_id ='0' or l.locations_id is null) AND l.status =  'A'";

		@SuppressWarnings("rawtypes")
		List resultList = em.createNativeQuery(queryString).setParameter(1, countryId).getResultList();
		int validCountryId = 0;
		if (resultList.size() > 0) {
			for (Object objRow : resultList) {
				validCountryId = (int) objRow;
			}
		} else {
			queryString = "select c.id from countries c where c.name='UNITED STATES'";
			@SuppressWarnings("rawtypes")
			List resultListDefault = em.createNativeQuery(queryString).getResultList();
			if (resultListDefault.size() > 0) {
				for (Object objRow : resultListDefault) {
					validCountryId = (int) objRow;
				}
			}
		}
		return validCountryId;
	}

	@GET
	@Path("/getAllSuppliers")
	public String getAllSuppliers(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getAllSuppliers(httpRequest, em));

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getLocationTypeIdForSupplier")
	public String getLocationTypeIdForSupplier(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getLocationTypeByName(httpRequest, em, "Supplier"));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getSupplierLocationById/{id}/{userId}")
	public String getSupplierLocationById(@PathParam("id") String id, @PathParam("userId") String userId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(
					new LocationsServiceBean().getSupplierLocationById(em, id, userId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAdminLocationByUserId/{userId}")
	public String getAdminLocationByUserId(@PathParam("userId") String userId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager emLocal = null;
		EntityTransaction tx = null;
		try {
			emLocal = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<Business> businesses = new LocationsServiceBean().getAdminLocationByUserId(httpRequest, sessionId,
					emLocal, userId);

			return new JSONUtility(httpRequest).convertToJsonString(businesses);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(emLocal);
		}

	}

	@GET
	@Path("/getLocationSpecificSalesTax/{locationId}")
	public String getLocationSpecificSalesTax(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getLocationSpecificSalesTax(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllSalesTax/{locationId}")
	public String getAllSalesTax(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationId") String locationId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getAllSalesTax(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getSalesTaxById/{id}")
	public String getSalesTaxById(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getSalesTaxById(httpRequest, em, id));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllReasonType")
	public String getAllReasonType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getAllReasonType(httpRequest, em));

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllReasons")
	public String getAllReasons(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getAllReasons(httpRequest, em));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getReasonById/{id}")
	public String getReasonById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getReasonById(httpRequest, em, id));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getReasonTypeById/{id}")
	public String getReasonTypeById(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getReasonTypeById(httpRequest, em, id));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getLocationsById/{id}")
	public String getLocationsById(@PathParam("id") String id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getLocationsById(httpRequest, em, id));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getTimeDifferenceFromServer/{gmtTimeInLongFormat}")
	public String getTimeDiffrence(@PathParam("gmtTimeInLongFormat") long gmtTimeInLongFormat) {
		long systemTimeInMillis = new TimezoneTime().getGMTTimeInMilis();
		return new JSONUtility(httpRequest).convertToJsonString(systemTimeInMillis - gmtTimeInLongFormat);

	}

	@GET
	@Path("/getRootLocations")
	public String getRootLocations(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getRootLocations(httpRequest, em));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getSubLocations/{locationId}")
	public String getSubLocations(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getSubLocations(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	// with time included
	//

	@GET
	@Path("/getOrderWithTimer/{locationId}")
	public String getOrderWithTimer(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, IOException, InvalidSessionException, SQLException {

		List<LocationWithOrderInfo> ans = new ArrayList<LocationWithOrderInfo>(10);
		EntityManager em = null;
		try {

			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String sql = " select orderheade0_.id orderheade0_id, orderheade0_.order_status_id orderheade0_order_status_id, orderheade0_.created orderheade0_created, orderheade0_.locations_id orderheade0_locations_id, orderheade0_.reservations_id orderheade0_reservations_id, "
					+ " orderheade0_.point_of_service_count orderheade0_point_of_service_count,orderheade0_.balance_due orderheade0_balance_due, orderheade0_.merged_locations_id orderheade0_merged_locations_id,orderheade0_.order_number as orderheade0_order_number,orderheade0_.updated orderheade0_updated,  "
					+ "orderheade0_.first_name,orderheade0_.last_name, u.id u_id,u.first_name u_first_name,u.last_name u_last_name,u.email u_email,u.phone u_phone,u.username u_username,u.global_users_id u_global_users_id "
					+ " from order_header orderheade0_ left join users u on u.id=orderheade0_.users_id cross join locations location1_ cross join order_status orderStatus2_ "
					+ " where orderheade0_.order_status_id = orderStatus2_.id AND orderStatus2_.name!='Order Suspend' AND orderStatus2_.name!='Ready to Order' "
					+ " AND orderStatus2_.name not in ('Void Order','Cancel Order')  and orderheade0_.locations_id=location1_.id "
					+ " and orderheade0_.is_order_reopened =0 " + " and (location1_.locations_id "
					+ " in (select location0_.id as id1 from locations location0_ where location0_.locations_id=? and location0_.status not in ('D','I')))";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList) {
				int i = 0;

				LocationWithOrderInfo locationWithOrderInfo = new LocationWithOrderInfo();
				locationWithOrderInfo.setOrderId((String) objRow[i++]);
				locationWithOrderInfo.setOrderStatusId((String) objRow[i++]);
				locationWithOrderInfo.setCreated((Timestamp) objRow[i++]);
				locationWithOrderInfo.setId((String) objRow[i++]);
				locationWithOrderInfo.setReservationId((String) objRow[i++]);
				locationWithOrderInfo.setPartySize((int) objRow[i++]);
				locationWithOrderInfo.setBalanceDue((BigDecimal) objRow[i++]);
				locationWithOrderInfo.setMergedLocationsId((String) objRow[i++]);
				locationWithOrderInfo.setOrderNumber((String) objRow[i++]);
				locationWithOrderInfo.setUpdated((Timestamp) objRow[i++]);
				locationWithOrderInfo.setFirstName((String) objRow[i++]);
				locationWithOrderInfo.setLastName((String) objRow[i++]);
				// check for userid != 0

				if (objRow[i] != null/*
										 * && (((String)objRow[i]).compareTo(
										 * BigInteger.ZERO) > 0)
										 */) {
					User user = new User();
					user.setId((String) (objRow[i]));
					i++;
					user.setFirstName((String) objRow[i++]);
					user.setLastName((String) objRow[i++]);
					user.setEmail((String) objRow[i++]);
					user.setPhone((String) objRow[i++]);
					user.setUsername((String) objRow[i++]);
					user.setGlobalUsersId((String) objRow[i++]);
					locationWithOrderInfo.setUser(user);
				}

				ans.add(locationWithOrderInfo);
			}

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		if (ans != null && ans.size() > 0) {
			return new JSONUtility(httpRequest).convertToJsonString(ans);
		}
		return "[]";

	}

	// To get the root location by use specific
	// added by uzma
	@GET
	@Path("/getRootLocationsByUserIdForBussiness/{userId}")
	public String getRootLocationsByUserId(@PathParam("userId") String userId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, IOException, InvalidSessionException, SQLException {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(
					new LocationsServiceBean().getRootLocationsByUserId(httpRequest, em, userId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/insertUsersToLocation")
	public String insertUsersToLocation(UsersToLocation usersToLocation,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			em.merge(usersToLocation);
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(usersToLocation);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/add")
	public String add(LocationPacket locationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, locationPacket);
			tx = em.getTransaction();
			tx.begin();
			Location result = new LocationsServiceBean().add(httpRequest, em, locationPacket.getLocation());
			tx.commit();
			locationPacket.setLocation(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationPacket, "LocationPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationPacket.getLocationId(),
					Integer.parseInt(locationPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_add.name(), locationPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/addRootLocation")
	public String addRootLocation(RootLocationPacket rootLocationPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		logger.severe(new JSONUtility(httpRequest).convertToJsonString(rootLocationPacket));
		EntityManager em = null;
		EntityTransaction tx = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, rootLocationPacket);
			tx = em.getTransaction();
			tx.begin();
			Location myLocation = rootLocationPacket.getLocation();

			// for temp we are adding
			if (myLocation.getBusinessTypeId() == 0) {
				myLocation.setBusinessTypeId(1);
			}

			try {
				if (myLocation.getId() == null) {
					myLocation.setId(new StoreForwardUtility().generateUUID());

				}
				if (myLocation.getAddress().getId() == null) {
					myLocation.getAddress().setId(new StoreForwardUtility().generateUUID());

				}

				em.persist(myLocation);
				tx.commit();
				tx.begin();
			} catch (Exception e) {
				logger.severe(e);
			}
		//	rootLocationPacket.setLocation(myLocation);
			// add roles provided by admin
			if (rootLocationPacket.getRoleList() != null) {
				for (Role role : rootLocationPacket.getRoleList()) {
					role.setLocationsId(rootLocationPacket.getLocation().getId());
					try {
						if (role.getId() == null) {

							role.setId(new StoreForwardUtility().generateUUID());

						}

					} catch (Exception e) {
						logger.severe(e);
					}

					role = em.merge(role);
				}
			}

			// add location setting provided by admin
			if (rootLocationPacket.getLocationSetting() != null) {
				rootLocationPacket.getLocationSetting().setStatus("A");
				rootLocationPacket.getLocationSetting().setLocationId(rootLocationPacket.getLocation().getId());
				rootLocationPacket.getLocationSetting().setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (rootLocationPacket.getLocationSetting().getId() == null) {
					rootLocationPacket.getLocationSetting().setId(new StoreForwardUtility().generateUUID());
				}
				em.persist(rootLocationPacket.getLocationSetting());
			}

			// iterate over users and add the necessary relationships
			if (rootLocationPacket.getUserList() != null) {
				for (User user : rootLocationPacket.getUserList()) {

					// add user if not already added
					if (user.getId() == null || user.getId().length() == 0) {
						user.setId(new StoreForwardUtility().generateUUID());
						user = em.merge(user);
					}

					// add users to locations

					// add user to roles
					for (Role role : rootLocationPacket.getRoleList()) {
						// only assign these roles to user
						if (role.getRoleName().equals("Account Admin") || role.getRoleName().equals("POS Operator")
								|| role.getRoleName().equals("Business") || role.getRoleName().equals("POS Supervisor")
								|| role.getRoleName().equals("Enterprise Report")
								|| role.getRoleName().equals("Global Setting")) {
							UsersToRole usersToRole = new UsersToRole(rootLocationPacket.getLocation().getCreatedBy(),
									"F", role.getId(), rootLocationPacket.getLocation().getCreatedBy(), user.getId());
							if (usersToRole.getId() == null) {

								usersToRole.setId(new StoreForwardUtility().generateUUID());
							}
							usersToRole.setPrimaryRoleInd("" + role.getGlobalRoleId());
							usersToRole.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							em.persist(usersToRole);

						}

					}

					UsersToLocation usersToLocation = new UsersToLocation("F",
							rootLocationPacket.getLocation().getCreatedBy(), rootLocationPacket.getLocation().getId(),
							rootLocationPacket.getLocation().getUpdatedBy(), user.getId());
					usersToLocation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					// usersToLocation.setId(1);

					em.persist(usersToLocation);
		
				}
			}

			if (rootLocationPacket.getPaymentgateway() != null) {
				if (rootLocationPacket.getLocation() != null && rootLocationPacket.getLocation().getId() != null) {
					List<Paymentgateway> paymentgateway = rootLocationPacket.getPaymentgateway();

					for (Paymentgateway paymentgateway2 : paymentgateway) {
						paymentgateway2.setLocationsId(rootLocationPacket.getLocation().getId());
						paymentgateway2.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						paymentgateway2.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						em.persist(paymentgateway2);

					}

				}
			}
			Application applicati = rootLocationPacket.getLocation().getApplication();
			if (applicati != null) {
				LocationToApplication locationToApplication = new LocationToApplication();
				locationToApplication.setLocationsId(rootLocationPacket.getLocation().getId());
				locationToApplication.setApplicationsId(applicati.getId());
				locationToApplication.setUpdatedBy(rootLocationPacket.getLocation().getCreatedBy());
				locationToApplication.setCreatedBy(rootLocationPacket.getLocation().getUpdatedBy());
				locationToApplication.setCreated(new TimezoneTime().getGMTTimeInMilis());
				locationToApplication.setUpdated(new TimezoneTime().getGMTTimeInMilis());
				em.persist(locationToApplication);
			}
			if (rootLocationPacket.getLocationsList() != null && rootLocationPacket.getLocationsList().size() > 0) {
				LocationRelationalHelper locationRelationalHelper = new LocationRelationalHelper();
				List<LocationsToSupplier> locationsToSuppliers = locationRelationalHelper
						.getAllLocationForSupplierId(rootLocationPacket.getLocation().getId(), em);
				if (locationsToSuppliers != null && locationsToSuppliers.size() > 0) {
					LocationsToSupplier supplier = new LocationsToSupplier();
					List<Location> locationList = new LocationsServiceBean().getMasterLocation(httpRequest, em);
					if (locationList != null && locationList.size() > 0) {
						Location location = locationList.get(0);
						supplier.setLocationsId(location.getId());
						supplier.setLocationsId(rootLocationPacket.getLocation().getId());
						locationsToSuppliers.add(supplier);

					}

				}
				new EntityRelationshipManager().manageRelations(em, rootLocationPacket.getLocation(),
						locationsToSuppliers, rootLocationPacket.getLocationsList(), LocationsToSupplier.class);
				for (Location location : rootLocationPacket.getLocationsList()) {
					rootLocationPacket.setLocationId(location.getId() + "");
					sendPacketForBroadcast(POSNServiceOperations.LocationsService_addLocationsToSupplier.name(),
							rootLocationPacket);
				}

			}
			tx.commit();
			Location globalLocation;
			String json;
			try {
				globalLocation = new CommonMethods().getBaseLocation(em);
				rootLocationPacket.setLocationId(globalLocation.getId());
					
				json = new StoreForwardUtility().returnJsonPacket(rootLocationPacket, "RootLocationPacket",httpRequest);
				logger.severe("json====================================================================="+json);
				new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, globalLocation.getId(),
						Integer.parseInt(rootLocationPacket.getMerchantId()));
			} catch (Exception e) {
				logger.severe(e);
			}
			// call synchPacket for store forward
			
			rootLocationPacket.setLocationId(rootLocationPacket.getLocation().getId() + "");
			sendPacketForBroadcast(POSNServiceOperations.LocationsService_add.name(), rootLocationPacket);

			if (rootLocationPacket.getLocation().getLocationsId() == null
					&& rootLocationPacket.getLocation().getLocationsTypeId() == 1) {
				if (!sendEmailToNirvanaXpForBusinessSuccess(rootLocationPacket.getLocation())) {
					return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_SEND_EMAIL,
							MessageConstants.ERROR_MESSAGE_UNABLE_TO_SEND_EMAIL, null).toString();
				}
			}
			rootLocationPacket.setLocation(myLocation);
			logger.severe(new JSONUtility(httpRequest).convertToJsonString(rootLocationPacket));
			return new JSONUtility(httpRequest).convertToJsonString(rootLocationPacket);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private boolean sendEmailToNirvanaXpForBusinessSuccess(Location tempAccount)
			throws FileNotFoundException, IOException {
		// send the verification code via email
		SMTPCredentials smtpCredentials = ConfigFileReader.getSMTPCredentialsForSingupSuccess();
		smtpCredentials.setFromEmail("support@nirvanaxp.com");
		smtpCredentials.setToEmail(tempAccount.getEmail());
		// generate text based on what we ned to send
		String text1 = smtpCredentials.getText();
		text1 = text1.replace("CUSTOMER_FIRSTNAME", "");
		text1 = text1.replace("CUSTOMER_ACCOUNT", tempAccount.getName());
		text1 = text1.replace("CUSTOMER_EMAIL", tempAccount.getEmail());
		// text1 += url;
		smtpCredentials.setText(text1);
		return SendMail.sendHtmlMail(smtpCredentials);
	}

	@POST
	@Path("/update")
	public String update(LocationPacket locationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, locationPacket);
			tx = em.getTransaction();
			tx.begin();
			Location result = new LocationsServiceBean().update(httpRequest, em, locationPacket.getLocation());
			tx.commit();
			locationPacket.setLocation(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationPacket, "LocationPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationPacket.getLocationId(),
					Integer.parseInt(locationPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_update.name(), locationPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	EntityManager em = null;

	@POST
	@Path("/updateLocationList")
	public String updateLocationList(LocationListPacket locationPacket)
			throws FileNotFoundException, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {

			String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			List<Location> result = new LocationsServiceBean().updateLocationList(httpRequest, em, locationPacket);
			tx.commit();
			locationPacket.setLocation(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationPacket, "LocationListPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationPacket.getLocationId(),
					Integer.parseInt(locationPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_update.name(), locationPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param rootlocationPacket
	 * @param sessionId
	 * @return
	 * @throws FileNotFoundException
	 * @throws DatabaseException
	 * @throws InvalidSessionException
	 * @throws IOException
	 * @throws SQLException
	 */
	@POST
	@Path("/updateRootLocation")
	public String updateRootLocation(RootLocationPacket rootlocationPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException, SQLException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, rootlocationPacket);
			tx = em.getTransaction();
			tx.begin();
			logger.severe("json============================11111111111111============================================"+new JSONUtility(httpRequest).convertToJsonString(rootlocationPacket));
			
			List<Paymentgateway> paymentgateway = new LocationsServiceBean().updateRootLocation(httpRequest, em,
					rootlocationPacket.getPaymentgateway());
			if (rootlocationPacket.getLocation().getApplication() != null) {
				rootlocationPacket.getLocation().getApplication().setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			}

			// temp change of code
			if (rootlocationPacket.getLocation() != null) {
				if (rootlocationPacket.getLocation().getBusinessTypeId() == 0) {
					Location prevLocation = (Location) new CommonMethods().getObjectById("Location", em, Location.class,
							rootlocationPacket.getLocation().getId());
					if (prevLocation != null && prevLocation.getBusinessTypeId() != 0) {
						rootlocationPacket.getLocation().setBusinessTypeId(prevLocation.getBusinessTypeId());
					} else {
						rootlocationPacket.getLocation().setBusinessTypeId(1);
					}
				}
			}

			if (rootlocationPacket.getLocationsList() != null && rootlocationPacket.getLocationsList().size() > 0) {
				LocationRelationalHelper locationRelationalHelper = new LocationRelationalHelper();
				List<LocationsToSupplier> locationsToSuppliers = locationRelationalHelper
						.getAllLocationForSupplierId(rootlocationPacket.getLocation().getId(), em);

				new EntityRelationshipManager().manageRelations(em, rootlocationPacket.getLocation(),
						locationsToSuppliers, rootlocationPacket.getLocationsList(), LocationsToSupplier.class);

				// Code commented due to deleted entry get active
				/*
				 * for(LocationsToSupplier
				 * locationsToSupplier:locationsToSuppliers){
				 * locationsToSupplier.setStatus(rootlocationPacket.getLocation(
				 * ).getStatus()); em.merge(locationsToSupplier); }
				 */

				for (LocationsToSupplier locationsToSupplier : locationsToSuppliers) {
					rootlocationPacket.setLocationId(locationsToSupplier.getLocationsId() + "");
					sendPacketForBroadcast(POSNServiceOperations.LocationsService_updateLocationsToSupplier.name(),
							rootlocationPacket);
				}
			}

			Location result = new LocationsServiceBean().updateRootLoc(httpRequest, em,
					rootlocationPacket.getLocation(), sessionId);

			rootlocationPacket.setPaymentgateway(paymentgateway);
			rootlocationPacket.setLocation(result);

			String json = new StoreForwardUtility().returnJsonPacket(rootlocationPacket, "RootLocationPacket",
					httpRequest);
			logger.severe("json========================================================================"+json);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, rootlocationPacket.getLocationId(),
					Integer.parseInt(rootlocationPacket.getMerchantId()));

			rootlocationPacket.setLocationId(rootlocationPacket.getLocation().getId());
			sendPacketForBroadcast(POSNServiceOperations.LocationsService_update.name(), rootlocationPacket);

			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(rootlocationPacket);

		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param locationPacket
	 * @param sessionId
	 * @return
	 * @throws FileNotFoundException
	 * @throws DatabaseException
	 * @throws InvalidSessionException
	 * @throws IOException
	 */
	@POST
	@Path("/delete")
	public String delete(LocationPacket locationPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, locationPacket);
			tx = em.getTransaction();
			tx.begin();

			Location result = new LocationsServiceBean().delete(em, locationPacket.getLocation());
			tx.commit();
			locationPacket.setLocation(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationPacket, "LocationPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationPacket.getLocationId(),
					Integer.parseInt(locationPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_delete.name(), locationPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/updateFloorTimer")
	public String updateFloorTimer(LocationPacket locationPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, locationPacket);
			tx = em.getTransaction();
			tx.begin();
			Location result = new LocationsServiceBean().updateFloorTimer(httpRequest, em,
					locationPacket.getLocation());
			tx.commit();
			locationPacket.setLocation(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationPacket, "LocationPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationPacket.getLocationId(),
					Integer.parseInt(locationPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_updateFloorTimer.name(), locationPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private void sendPacketForBroadcast(String operation, LocationPacket locationPacket) {
		try {
			operation = ServiceOperationsUtility.getOperationName(operation);

			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, locationPacket.getClientId(), POSNServices.LocationsService.name(),
					operation, null, locationPacket.getMerchantId(), locationPacket.getLocationId(),
					locationPacket.getEchoString(), locationPacket.getSchemaName());
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not broadcast location packet");
		}

	}

	private void sendPacketForBroadcast(String operation, LocationToFunctionPacket locationPacket) {
		try {
			operation = ServiceOperationsUtility.getOperationName(operation);

			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, locationPacket.getClientId(), POSNServices.LocationsService.name(),
					operation, null, locationPacket.getMerchantId(), locationPacket.getLocationId(),
					locationPacket.getEchoString(), locationPacket.getSchemaName());
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not broadcast location to function packet");
		}
	}

	private void sendPacketForBroadcast(String operation, PostPacket postPacket) {
		try {
			operation = ServiceOperationsUtility.getOperationName(operation);

			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.LocationsService.name(),
					operation, null, postPacket.getMerchantId(), postPacket.getLocationId(), postPacket.getEchoString(),
					postPacket.getSchemaName());
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Could not broadcast location to function packet");
		}
	}

	// To get the payment gateway by location id
	@GET
	@Path("/getPaymentGatewayByLocationId/{locationId}")
	public String getPaymentGatewayByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getPaymentGatewayByLocationId(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getLocationByUserId/{userId}")
	public String getLocationByUserIdList(@PathParam("userId") String userId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getLocationByUserIdList(httpRequest, em, userId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getMetroNotificationByLocationIdAndDate/{locationId}/{date}")
	public String getMetroNotificationByLocationIdAndDate(@PathParam("locationId") String locationId,
			@PathParam("date") String date, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException, SQLException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getMetroNotificationByLocationIdAndDate(em, locationId, date));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getPaymentGatewayListByLocationId/{locationId}")
	public String getPaymentGatewayListByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getPaymentGatewayListByLocationId(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllPackageByBusinessTypeId/{businessTypeId}")
	public String getAllPackageByBusinessTypeId(@PathParam("businessTypeId") int businessTypeId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<MetaBusinessTypeToApplication> criteria = builder
					.createQuery(MetaBusinessTypeToApplication.class);
			Root<MetaBusinessTypeToApplication> r = criteria.from(MetaBusinessTypeToApplication.class);
			TypedQuery<MetaBusinessTypeToApplication> query = em.createQuery(criteria.select(r)
					.where(builder.equal(r.get(MetaBusinessTypeToApplication_.businessTypeId), businessTypeId)));

			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllReasonsByTypeId/{typeId}/{locationId}")
	public String getAllReasonsByTypeId(@PathParam("typeId") String typeId, @PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getAllReasonsByTypeId(httpRequest, em, typeId, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/deleteSupplier")
	public String deleteSupplier(LocationPacket locationPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws IOException, InvalidSessionException {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			tx = em.getTransaction();
			tx.begin();
			Location result = new LocationsServiceBean().deleteSupplier(httpRequest, em,
					locationPacket.getLocation().getId(), locationPacket.getLocation().getUpdatedBy());
			tx.commit();
			locationPacket.setLocation(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationPacket, "LocationPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, locationPacket.getLocationId(),
					Integer.parseInt(locationPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllLocationToFunctionByLocationId/{locationId}")
	public String getAllLocationToFunctionByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getAllLocationToFunctionByLocationId(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getLocationToFunctionById/{id}")
	public String getLocationToFunctionById(@PathParam("id") int id,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, IOException, InvalidSessionException, SQLException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getLocationToFunctionById(httpRequest, em, id));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getLocationToFunctionByLocationIdAndDisplaySequence/{locationId}/{displaySequence}")
	public String getLocationToFunctionByLocationIdAndDisplaySequence(@PathParam("locationId") String locationId,
			@PathParam("displaySequence") int displaySequence,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, IOException, InvalidSessionException, SQLException {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(new LocationsServiceBean()
					.getLocationToFunctionByLocationIdAndDisplaySequence(httpRequest, em, locationId, displaySequence));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/updateLocationToFunctionName")
	public String updateLocationToFunctionName(LocationToFunctionPacket locationToFunctionPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					locationToFunctionPacket);
			tx = em.getTransaction();
			tx.begin();
			LocationsToFunction result = new LocationsServiceBean().updateLocationToFunctionName(httpRequest, em,
					locationToFunctionPacket.getLocationsToFunction());
			tx.commit();
			locationToFunctionPacket.setLocationsToFunction(result);
			String json = new StoreForwardUtility().returnJsonPacket(locationToFunctionPacket,
					"LocationToFunctionPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					locationToFunctionPacket.getLocationId(),
					Integer.parseInt(locationToFunctionPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_updateLocationsToFunctions.name(),
					locationToFunctionPacket);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param locationId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getBusinessCompletionInfoByLocationId/{locationId}")
	public String getBusinessCompletionInfoByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getBusinessCompletionInfoByLocationId(httpRequest, em, locationId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getNirvanaXpSalesTaxInfoBySalesTaxId/{salesTaxId}")
	public String getNirvanaXpSalesTaxInfoBySalesTaxId(@PathParam("salesTaxId") int salesTaxId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getNirvanaXpSalesTaxInfoBySalesTaxId(httpRequest, em, salesTaxId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllLocationsDetails")
	public String getAllLocationsDetails(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getAllLocationsDetails(httpRequest, em));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllLocationToLocationDetailsByLocationId/{locationId}")
	public String getAllLocationToLocationDetailsByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select l from LocationToLocationDetails l where l.locationsId=? and l.status !='D' order by l.displaySequence asc";
			TypedQuery<LocationToLocationDetails> query = em.createQuery(queryString, LocationToLocationDetails.class)
					.setParameter(1, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/addLocationsToImages")
	public String addLocationsToImages(LocationsToImagesPacket LocationsToImagesPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					LocationsToImagesPacket);

			tx = em.getTransaction();
			tx.begin();
			LocationsToImages LocationsToImages = new LocationsServiceBean()
					.addLocationsToImages(LocationsToImagesPacket.getLocationsToImages(), httpRequest, em);
			tx.commit();
			sendPacketForBroadcast(POSNServiceOperations.LocationsService_addLocationsToImages.name(),
					LocationsToImagesPacket);
			LocationsToImagesPacket.setLocationsToImages(LocationsToImages);
			String json = new StoreForwardUtility().returnJsonPacket(LocationsToImagesPacket, "LocationsToImagesPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					LocationsToImagesPacket.getLocationId(), Integer.parseInt(LocationsToImagesPacket.getMerchantId()));

			return new JSONUtility(httpRequest).convertToJsonString(LocationsToImages);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateLocationsToImages")
	public String updateLocationsToImages(LocationsToImagesPacket LocationsToImagesPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					LocationsToImagesPacket);

			tx = em.getTransaction();
			tx.begin();
			LocationsToImages LocationsToImages = new LocationsServiceBean()
					.updateLocationsToImages(LocationsToImagesPacket.getLocationsToImages(), httpRequest, em);
			tx.commit();
			LocationsToImagesPacket.setLocationsToImages(LocationsToImages);
			String json = new StoreForwardUtility().returnJsonPacket(LocationsToImagesPacket, "LocationsToImagesPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					LocationsToImagesPacket.getLocationId(), Integer.parseInt(LocationsToImagesPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_updateLocationsToImages.name(),
					LocationsToImagesPacket);
			return new JSONUtility(httpRequest).convertToJsonString(LocationsToImages);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteLocationsToImages")
	public String deleteLocationsToImages(LocationsToImagesPacket LocationsToImagesPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId,
					LocationsToImagesPacket);

			tx = em.getTransaction();
			tx.begin();
			LocationsToImages LocationsToImages = new LocationsServiceBean()
					.deleteLocationsToImages(LocationsToImagesPacket.getLocationsToImages(), httpRequest, em);
			tx.commit();
			LocationsToImagesPacket.setLocationsToImages(LocationsToImages);
			String json = new StoreForwardUtility().returnJsonPacket(LocationsToImagesPacket, "LocationsToImagesPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					LocationsToImagesPacket.getLocationId(), Integer.parseInt(LocationsToImagesPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_deleteLocationsToImages.name(),
					LocationsToImagesPacket);
			return new JSONUtility(httpRequest).convertToJsonString(LocationsToImages);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllLocationsToImagesByLocationId/{locationId}")
	public String getAllLocationsToImagesByLocationId(@PathParam("locationId") String locationId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception {
		EntityManager em = null;
		try {
			// removed the collection sort logic :- giving null pointer in new
			// implementation of display sequence
			// by Apoorva July 6, 2015 sprint 7
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select r from LocationsToImages r where r.locationsId=? and r.status !='D' order by r.displaySequence asc";
			TypedQuery<LocationsToImages> query = em.createQuery(queryString, LocationsToImages.class).setParameter(1,
					locationId);
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/updateLocationToLocationDetails")
	public String updateLocationToLocationDetails(LocationToLocationDetailsPacket locationToLocationDetailsPacket,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException {
		EntityManager em = null;
		EntityManager globalEM = null;
		EntityTransaction tx_new = null;

		EntityTransaction tx = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			locationToLocationDetailsPacket.getLocationToLocationDetails()
					.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			int businessId = locationToLocationDetailsPacket.getLocationToLocationDetails().getBusinessId();

			LocationsDetail locationDetail = (LocationsDetail) new CommonMethods().getObjectById("LocationsDetail", em,
					LocationsDetail.class,
					locationToLocationDetailsPacket.getLocationToLocationDetails().getLocationDetailsId());

			LocationToLocationDetails locationToLocationDetails = locationToLocationDetailsPacket
					.getLocationToLocationDetails();

			try {
				logger.info("in Location to Locatioon detail find");
				locationToLocationDetails = new LocationsServiceBean().checkLocationDetailExist(
						locationToLocationDetailsPacket.getLocationToLocationDetails().getLocationDetailsId(),
						locationToLocationDetailsPacket.getLocationToLocationDetails().getLocationsId(), httpRequest,
						em);

				locationToLocationDetails
						.setComments(locationToLocationDetailsPacket.getLocationToLocationDetails().getComments());
				locationToLocationDetails
						.setStatus(locationToLocationDetailsPacket.getLocationToLocationDetails().getStatus());

			} catch (NoResultException nre) {
				logger.info("No Location to Locatioon detail find");
			}

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

			com.nirvanaxp.global.types.entities.BusinessToBusinessDetail businessToBusinessDetail = new com.nirvanaxp.global.types.entities.BusinessToBusinessDetail();
			businessToBusinessDetail.setBusinessId(businessId);
			businessToBusinessDetail.setBusinessDetailsId(locationDetail.getGlobalBusinessDetailsId());
			businessToBusinessDetail
					.setComments(locationToLocationDetailsPacket.getLocationToLocationDetails().getComments());
			businessToBusinessDetail
					.setStatus(locationToLocationDetailsPacket.getLocationToLocationDetails().getStatus());
			businessToBusinessDetail
					.setCreatedBy(locationToLocationDetailsPacket.getLocationToLocationDetails().getCreatedBy());
			businessToBusinessDetail
					.setUpdatedBy(locationToLocationDetailsPacket.getLocationToLocationDetails().getUpdatedBy());
			businessToBusinessDetail.setId(locationToLocationDetailsPacket.getLocationToLocationDetails()
					.getGlobalBusinessToBusinessDetailsId());
			businessToBusinessDetail.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			tx_new = globalEM.getTransaction();
			tx_new.begin();
			if (businessToBusinessDetail.getId() == 0) {
				globalEM.persist(businessToBusinessDetail);
			} else {
				globalEM.merge(businessToBusinessDetail);
			}
			tx_new.commit();

			tx = em.getTransaction();
			tx.begin();
			if (locationToLocationDetails.getId() == null) {
				locationToLocationDetails.setGlobalBusinessToBusinessDetailsId(businessToBusinessDetail.getId());
				try {
					if (locationToLocationDetails.getId() == null) {
						locationToLocationDetails.setId(new StoreForwardUtility().generateUUID());
					}
				} catch (Exception e) {
					logger.severe(e);
				}
				em.merge(locationToLocationDetails);
			} else {
				em.merge(locationToLocationDetails);
			}

			tx.commit();
			locationToLocationDetailsPacket.setLocationToLocationDetails(locationToLocationDetails);
			String json = new StoreForwardUtility().returnJsonPacket(locationToLocationDetailsPacket,
					"LocationToLocationDetailsPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					locationToLocationDetailsPacket.getLocationId(),
					Integer.parseInt(locationToLocationDetailsPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_update.name(),
					locationToLocationDetailsPacket);
			return new JSONUtility(httpRequest).convertToJsonString(locationToLocationDetails);
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			if (tx_new != null && tx_new.isActive()) {
				tx_new.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	@GET
	@Path("/getAllRootLocationsWithoutGlobalBusinessId")
	public String getAllRootLocationsWithoutGlobalBusinessId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest).convertToJsonString(
					new LocationsServiceBean().getAllRootLocationsWithoutGlobalBusinessId(httpRequest, em));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getMasterLocation")
	public String getMasterLocation(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws IOException, InvalidSessionException {

		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getMasterLocation(httpRequest, em));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getLocationByUserIdForKDS/{userId}")
	public String getLocationByUserIdForKDS(@PathParam("userId") String userId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			return new JSONUtility(httpRequest)
					.convertToJsonString(new LocationsServiceBean().getLocationByUserIdForKDS(httpRequest, em, userId));
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@POST
	@Path("/updateLocationSetting")
	public String updateLocationSetting(LocationSettingPacket locationSettingPacket) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
//			String json = new StoreForwardUtility().returnJsonPacket(locationSettingPacket, "LocationSettingPacket",
//					httpRequest);
//			logger.severe(json);
			LocationSetting LocationSetting = new LocationsServiceBean()
					.updateLocationSetting(locationSettingPacket.getLocationSetting(), httpRequest, em);
			tx.commit();
			locationSettingPacket.setLocationSetting(LocationSetting);
			String json  = new StoreForwardUtility().returnJsonPacket(locationSettingPacket, "LocationSettingPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					locationSettingPacket.getLocationId(), Integer.parseInt(locationSettingPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_updateLocationSetting.name(),
					locationSettingPacket);
			return new JSONUtility(httpRequest).convertToJsonString(LocationSetting);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/addLocationSetting")
	public String addLocationSetting(LocationSettingPacket locationSettingPacket) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			LocationSetting LocationSetting = new LocationsServiceBean()
					.addLocationSetting(locationSettingPacket.getLocationSetting(), httpRequest, em);
			tx.commit();
			locationSettingPacket.setLocationSetting(LocationSetting);
			String json = new StoreForwardUtility().returnJsonPacket(locationSettingPacket, "LocationSettingPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					locationSettingPacket.getLocationId(), Integer.parseInt(locationSettingPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_addLocationSetting.name(),
					locationSettingPacket);
			return new JSONUtility(httpRequest).convertToJsonString(LocationSetting);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteLocationSetting")
	public String deleteLocationSetting(LocationSettingPacket locationSettingPacket) throws Exception {

		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			LocationSetting LocationSetting = new LocationsServiceBean()
					.deleteLocationSetting(locationSettingPacket.getLocationSetting(), httpRequest, em);
			tx.commit();
			locationSettingPacket.setLocationSetting(LocationSetting);
			String json = new StoreForwardUtility().returnJsonPacket(locationSettingPacket, "LocationSettingPacket",
					httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,
					locationSettingPacket.getLocationId(), Integer.parseInt(locationSettingPacket.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_deleteLocationSetting.name(),
					locationSettingPacket);
			return new JSONUtility(httpRequest).convertToJsonString(LocationSetting);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param locationId
	 * @param sessionId
	 * @return list of reservation status order by display sequence
	 * @throws Exception
	 */
	@GET
	@Path("/getAllLocationSettingByLocationId/{locationId}")
	public String getAllLocationSettingByLocationId(@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationSetting> criteria = builder.createQuery(LocationSetting.class);
			Root<LocationSetting> r = criteria.from(LocationSetting.class);
			TypedQuery<LocationSetting> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(LocationSetting_.locationId), locationId),
							builder.notEqual(r.get(LocationSetting_.status), "D")));

			LocationSetting l = query.getSingleResult();
			l.setLocationBatchTimingList(getLocationBatchTiming(em, l.getId()));
			;

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/threadExection")
	public void threadExection() throws Exception {

		try {
			DaylightSavingThread thread = new DaylightSavingThread();
			Thread t = new Thread(thread);
			t.start();
		} catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}

	}

	@GET
	@Path("/getAllLocationsByLocationTypeAndLocationId/{locationTypeId}/{locationId}")
	public String getAllLocationsByLocationType(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId,
			@PathParam("locationTypeId") int locationTypeId, @PathParam("locationId") String locationId)
			throws IOException, InvalidSessionException {
		EntityManager em = null;
		try {

			List<Location> suppliersList = new ArrayList<Location>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em
					.createNativeQuery("call p_get_locations_by_locationId_and_location_type( ? , ?)")
					.setParameter(1, locationId).setParameter(2, locationTypeId).getResultList();
			for (Object[] objRow : resultList) { // if this has primary key not
													// 0

				if ((String) objRow[0] != null) {
					Location supplier = new Location((String) objRow[0]);
					supplier.setName((String) objRow[1]);
					supplier.setLocationsTypeId((int) objRow[2]);
					suppliersList.add(supplier);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(suppliersList);

		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getAllEmailTemplateByLocationId/{locationId}")
	public String getAllEmailTemplateByLocationId(@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<EmailTemplate> criteria = builder.createQuery(EmailTemplate.class);
			Root<EmailTemplate> r = criteria.from(EmailTemplate.class);
			TypedQuery<EmailTemplate> query = em
					.createQuery(criteria.select(r).where(builder.equal(r.get(EmailTemplate_.locationId), locationId),
							builder.notEqual(r.get(EmailTemplate_.status), "D")));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/addUpdateDeliveryRules")
	public String addUpdateDeliveryRules(DeliveryRulesPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			DeliveryRulesPacket result = new LocationsServiceBean().addDeliveryRules(em, packet);
			tx.commit();

			String json = new StoreForwardUtility().returnJsonPacket(packet, "DeliveryRulesPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_addDeliveryRules.name(), packet);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@POST
	@Path("/deleteDeliveryRules")
	public String deleteDeliveryRules(DeliveryRulesPacket packet) throws Exception {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			tx = em.getTransaction();
			tx.begin();
			DeliveryRulesPacket result = new LocationsServiceBean().deleteDeliveryRules(em, packet);
			tx.commit();
			String json = new StoreForwardUtility().returnJsonPacket(packet, "DeliveryRulesPacket", httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, packet.getLocationId(),
					Integer.parseInt(packet.getMerchantId()));

			sendPacketForBroadcast(POSNServiceOperations.LocationsService_deleteDeliveryRules.name(), result);
			return new JSONUtility(httpRequest).convertToJsonString(result);
		} catch (Exception e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/deliveryRules/{locationId}")
	public String getItemToDate(@PathParam("locationId") String locationId) throws Exception {
		EntityManager em = null;

		try {
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			List<DeliveryRules> items = new LocationsServiceBean().getDeliveryRules(em, locationId);
			return new JSONUtility(httpRequest).convertToJsonString(items);
		} finally {
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	private List<LocationBatchTiming> getLocationBatchTiming(EntityManager em, String locationSettingId) {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<LocationBatchTiming> criteria = builder.createQuery(LocationBatchTiming.class);
			Root<LocationBatchTiming> r = criteria.from(LocationBatchTiming.class);
			TypedQuery<LocationBatchTiming> query = em.createQuery(criteria.select(r).where(
					builder.equal(r.get(LocationBatchTiming_.locationSettingId), locationSettingId),
					builder.notEqual(r.get(LocationSetting_.status), "D")));
			return query.getResultList();
		} catch (Exception e) {
			logger.severe(e);
		}
		return null;
	}

	@GET
	@Path("/test/")
	public long test() throws Exception {
		 return new TimezoneTime().getGMTTimeInMilis();
	}
	
	@GET
	@Path("/getTransactionalCurrencyById/{id}")
	public String getTransactionalCurrencyById(@PathParam("id") int id) throws Exception
	{
		EntityManager em = null;

		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
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

}