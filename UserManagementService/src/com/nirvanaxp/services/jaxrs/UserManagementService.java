/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
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

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.exceptions.POSExceptionMessage;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.LocationIdPacket;
import com.nirvanaxp.services.jaxrs.packets.SyncUserPacket;
import com.nirvanaxp.services.jaxrs.packets.UserPostPacket;
import com.nirvanaxp.services.jaxrs.packets.UsersToDiscountPacket;
import com.nirvanaxp.services.jaxrs.packets.UsersToPaymentHistoryPacket;
import com.nirvanaxp.services.jaxrs.packets.UsersToPaymentPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.storeForward.StoreForwardUtilityForGlobal;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.roles.Role_;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.tip.EmployeeMasterToJobRoles;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.User_;
import com.nirvanaxp.types.entities.user.UsersToAddress;
import com.nirvanaxp.types.entities.user.UsersToAddress_;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToPayment;
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;
import com.nirvanaxp.types.entities.user.UsersToRole;
import com.nirvanaxp.user.utility.GlobalUserUtil;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.user.utility.LocalUserUtil;
import com.nirvanaxp.user.utility.UserManagementObj;
import com.nirvanaxp.user.utility.UserManagementServiceBean;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

/**
 * @author nirvanaxp
 *
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class UserManagementService extends AbstractNirvanaService
{

	/**  */
	@Context
	HttpServletRequest httpRequest;

	/**  */
	// private EntityManager em;
	private final static NirvanaLogger logger = new NirvanaLogger(UserManagementService.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.INirvanaService#isAlive()
	 */
	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	/**
	 * 
	 *
	 * @param username
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByUserName/{username}")
	public String getUserByUserName(@PathParam("username") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = new UserManagementServiceBean(httpRequest, em).getUserByUserName(username);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param authPin
	 * @param locationId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/authenticateUserByAuthPin/{authPin}/{locationId}")
	public String authenticateUserByAuthPin(@PathParam("authPin") String authPin, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<User> usersList = new UserManagementServiceBean(httpRequest, em).getUserByAuthPin(authPin);
			User user = usersList.get(0);

			for (UsersToLocation usersToLocation : user.getUsersToLocations())
			{
				if (locationId == usersToLocation.getLocationsId())
				{
					return new JSONUtility(httpRequest).convertToJsonString(user);
				}

			}
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return "User does not access to location";
	}

	/**
	 * 
	 *
	 * @param id
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserById/{id}")
	public String getUserById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = new UserManagementServiceBean(httpRequest, em).getUserById(id);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param id
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserWithAddressById/{id}")
	public String getUserWithAddressById(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			UserPostPacket userPostPacket = new UserPostPacket();
			User user = new UserManagementServiceBean(httpRequest, em).getUserById(id);
			List<UsersToLocation> usersToLocationsList = new UserManagementServiceBean(httpRequest, em).getUsersToLocationByUserId(em,id);
			
			for(UsersToLocation usersToLocation:usersToLocationsList){
				Location location=(Location) new CommonMethods().getObjectById("Location", em,Location.class, usersToLocation.getLocationsId());
				usersToLocation.setLocationsTypeId(location.getLocationsTypeId());
			}
			Set<UsersToLocation> usersToLocations = new HashSet<UsersToLocation>(usersToLocationsList);
				
			user.setUsersToLocations(usersToLocations);;
			userPostPacket.setUser(user);

			try
			{
				String queryString1 = "select b from EmployeeMaster b where b.userId= ? and b.status not in('D')";
				EmployeeMaster resultSet = null;

				Query query = em.createQuery(queryString1).setParameter(1, user.getId());
				resultSet = (EmployeeMaster) query.getSingleResult();
				if (resultSet != null)
				{
					user.setEmployeeMaster(resultSet);
				}

			}
			catch (Exception e)
			{
				logger.severe("No Employee Master Found for User Id " + user.getId());
			}

			if (user != null && user.getGlobalUsersId()!=null)
			{
				GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
				EntityManager globalEM = null;
				try
				{
					globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

					List<com.nirvanaxp.global.types.entities.Address> addressList = globalUsermanagement.getAddressByGlobalUserId(globalEM, user.getGlobalUsersId());
					if (addressList != null && addressList.size() > 0)
					{
						Set<Address> localAddressList = new HashSet<Address>();

						for (com.nirvanaxp.global.types.entities.Address globalAddress : addressList)
						{

							Address localAddress = new Address(globalAddress.getAddress1(), globalAddress.getAddress2(), globalAddress.getCity(), globalAddress.getCountryId(),
									globalAddress.getCreatedBy(), globalAddress.getFax(), globalAddress.getLatValue(), globalAddress.getLongValue(), globalAddress.getPhone(), globalAddress.getState(),
									globalAddress.getUpdatedBy(), globalAddress.getZip(), globalAddress.getId(), globalAddress.getStateId(), globalAddress.getCityId());
							localAddressList.add(localAddress);
						}
						userPostPacket.setAddressList(new HashSet<Address>(localAddressList));
					}
				}

				finally
				{
					GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
					LocalSchemaEntityManager.getInstance().closeEntityManager(em);
				}

			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userPostPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/userWithAddress/{id}")
	public String userWithAddress(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			UserPostPacket userPostPacket = new UserPostPacket();
			User user = new UserManagementServiceBean(httpRequest, em).getUserById(id);
			List<UsersToLocation> usersToLocationsList = new UserManagementServiceBean(httpRequest, em).getUsersToLocationByUserId(em,id);
			
			for(UsersToLocation usersToLocation:usersToLocationsList){
				Location location=(Location) new CommonMethods().getObjectById("Location", em,Location.class, usersToLocation.getLocationsId());
				usersToLocation.setLocationsTypeId(location.getLocationsTypeId());
			}
			Set<UsersToLocation> usersToLocations = new HashSet<UsersToLocation>(usersToLocationsList);
				
			user.setUsersToLocations(usersToLocations);;
			userPostPacket.setUser(user);

			if (user != null && user.getGlobalUsersId()!=null)
			{
				GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
				EntityManager globalEM = null;
				try
				{
					globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

					List<com.nirvanaxp.global.types.entities.Address> addressList = globalUsermanagement.getAddressByGlobalUserId(globalEM, user.getGlobalUsersId());
					if (addressList != null && addressList.size() > 0)
					{
						Set<Address> localAddressList = new HashSet<Address>();

						for (com.nirvanaxp.global.types.entities.Address globalAddress : addressList)
						{

							Address localAddress = new Address(globalAddress.getAddress1(), globalAddress.getAddress2(), globalAddress.getCity(), globalAddress.getCountryId(),
									globalAddress.getCreatedBy(), globalAddress.getFax(), globalAddress.getLatValue(), globalAddress.getLongValue(), globalAddress.getPhone(), globalAddress.getState(),
									globalAddress.getUpdatedBy(), globalAddress.getZip(), globalAddress.getId(), globalAddress.getStateId(), globalAddress.getCityId());
							localAddressList.add(localAddress);
						}
						userPostPacket.setAddressList(new HashSet<Address>(localAddressList));
					}
				}

				finally
				{
					GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
					LocalSchemaEntityManager.getInstance().closeEntityManager(em);
				}

			}

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userPostPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param phoneNumber
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserWithAddressByPhoneNumber/{phoneNumber}")
	public String getUserWithAddressByPhoneNumber(@PathParam("phoneNumber") String phoneNumber, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			UserPostPacket userPostPacket = new UserPostPacket();
			User user = new UserManagementServiceBean(httpRequest, em).getUserByPhoneNo(phoneNumber);
			userPostPacket.setUser(user);
			userPostPacket.setAddressList(user.getAddressesSet());

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userPostPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param emailId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserWithAddressByEmailId/{emailId}")
	public String emailId(@PathParam("emailId") String emailId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			UserPostPacket userPostPacket = new UserPostPacket();
			User user = new UserManagementServiceBean(httpRequest, em).getUserByEmail(emailId);
			userPostPacket.setUser(user);
			userPostPacket.setAddressList(user.getAddressesSet());
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userPostPacket);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllUsers")
	public String getAllUsers(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<User> usersList = new UserManagementServiceBean(httpRequest, em).getAllUsers();
			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param username
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByFirstName/{firstName}")
	public String getUserByFirstName(@PathParam("firstName") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<User> usersList = new UserManagementServiceBean(httpRequest, em).getUserByFirstName(username);
			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param username
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByLastName/{lastName}")
	public String getUserByLastName(@PathParam("lastName") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<User> usersList = new UserManagementServiceBean(httpRequest, em).getUserByLastName(username);
			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param username
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByEmail/{email}")
	public String getUserByEmail(@PathParam("email") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User users = new UserManagementServiceBean(httpRequest, em).getUserByEmail(username);
			return new JSONUtility(httpRequest).convertToJsonString(users);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param email
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getGlobalUserUserByEmail/{email}")
	public String getGlobalUserUserByEmail(@PathParam("email") String email, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			EntityManager globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			com.nirvanaxp.global.types.entities.User users = new GlobalUserUtil().getGlobalUserByEmail(globalEM, email);
			return new JSONUtility(httpRequest).convertToJsonString(users);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param locationsId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByLocationsId/{locationsId}")
	public String getUserByLocationsId(@PathParam("locationsId") String locationsId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<User> usersList = UserManagementServiceBean.getUserByLocationsId(em, locationsId);
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param authPin
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByAuthPin/{authPin}")
	public String getUserByAuthPin(@PathParam("authPin") String authPin, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			List<User> usersList = new UserManagementServiceBean(httpRequest, em).getUserByAuthPin(authPin);
			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param globalUsersId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByGlobalUsersIdByAuthToken/{globalUsersId}")
	public String getUserByGlobalUsersIdByAuthToken(@PathParam("globalUsersId") String globalUsersId) throws Exception
	{
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);

		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = new UserManagementServiceBean(httpRequest, em).getUserByGlobalUsersId(globalUsersId);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param globalUsersId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByGlobalUsersId/{globalUsersId}")
	public String getUserByGlobalUsersId(@PathParam("globalUsersId") String globalUsersId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = new UserManagementServiceBean(httpRequest, em).getUserByGlobalUsersId(globalUsersId);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addUserFeedback")
	public String addUserFeedback(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));
						
			return new UserManagementServiceBean(httpRequest, em).addUserFeedback(userPostPacket.getUser());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addCustomer")
	public String addCustomer(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		// sessionId = "cd1be731d97ec346e42bd67c90a79067";
		if (userPostPacket.getLocationId() == null || userPostPacket.getLocationId().length() == 0)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LOCATION_CANNOT_BE_BLANK_EXCEPTION, MessageConstants.ERROR_MESSAGE_LOCATION_CANNOT_BE_BLANK_DISPLAY_MESSAGE, null)
					.toString();
		}

		if (userPostPacket.getUser() == null)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
		}

		EntityManager localEM = null;
		EntityManager globalEM = null;

		String json = null;
		try
		{
			

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			String locationId = userPostPacket.getLocationId();
			User localuser = userPostPacket.getUser();
			Set<Address> localAddressSet = userPostPacket.getAddressList();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			localEM = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, userPostPacket);

			UserManagementObj userManagementObj = globalUsermanagement.addCustomer(httpRequest, globalEM, localEM, localuser, locationId, localAddressSet,userPostPacket);

			// check if user already exists in our database, then return
			// client the reason for not adding the user
			if (userManagementObj != null && userManagementObj.getResponse() != null && userManagementObj.getResponse().equals("Already added user"))
			{

				String errorMessage = "";
				String phoneOfUserSentByClient = userPostPacket.getUser().getPhone();
				String emailOfUserSentByClient = userPostPacket.getUser().getEmail();
				String usernameOfUserSentByClient = userPostPacket.getUser().getUsername();

				if (phoneOfUserSentByClient != null && phoneOfUserSentByClient.trim().length() == 0)
				{
					phoneOfUserSentByClient = null;
					userPostPacket.getUser().setPhone(null);
				}

				if (emailOfUserSentByClient != null && emailOfUserSentByClient.trim().length() == 0)
				{
					emailOfUserSentByClient = null;
					userPostPacket.getUser().setEmail(null);
				}

				String phoneOfAlreadyExistingUser = userManagementObj.getUser().getPhone();
				String emailOfAlreadyExistingUser = userManagementObj.getUser().getEmail();

				String usernameOfAlreadyExistingUser = userManagementObj.getUser().getUsername();

				if (phoneOfUserSentByClient != null && phoneOfUserSentByClient.trim().length() > 0 && phoneOfAlreadyExistingUser != null && phoneOfAlreadyExistingUser.trim().length() > 0)
				{

					if (phoneOfUserSentByClient.trim().equals(phoneOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Phone number already Exists in our database.";
					}
				}

				if (emailOfUserSentByClient != null && emailOfUserSentByClient.trim().length() > 0 && emailOfAlreadyExistingUser != null && emailOfAlreadyExistingUser.trim().length() > 0)
				{

					if (emailOfUserSentByClient.trim().equals(emailOfAlreadyExistingUser.trim()))
					{
						errorMessage += "Email already Exists in our database.";
					}

				}

				if (usernameOfUserSentByClient != null && usernameOfUserSentByClient.trim().length() > 0 && usernameOfAlreadyExistingUser != null && usernameOfAlreadyExistingUser.trim().length() > 0)
				{

					if (usernameOfUserSentByClient.trim().equals(usernameOfAlreadyExistingUser.trim()))
					{
						errorMessage += "Username already Exists in our database.";
					}

				}

				return errorMessage;
			}

			// user was added and appropriate details were returned
			localuser = userManagementObj.getUser();

			userPostPacket.setUser(localuser);
			userPostPacket.setAddressList(localAddressSet);

			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			sendPacketForBroadcast(createPacketForPush(userPostPacket), POSNServiceOperations.UserManagementService_addCustomer.name(), false);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));
						
			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addUser")
	public String addUser(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		
 
		if (userPostPacket.getLocationId() == null || userPostPacket.getLocationId().length() == 0)
		{

			return new NirvanaXPException(
					new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LOCATION_CANNOT_BE_BLANK_EXCEPTION, MessageConstants.ERROR_MESSAGE_LOCATION_CANNOT_BE_BLANK_DISPLAY_MESSAGE, null))
							.toString();
		}

		// process User roles
		// convert local users to roles to global users to roles.

		

		EntityManager em = null;

		EntityManager globalEM = null;
		String json = null;
		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			
			Set<UsersToRole> usersToRoleSet = userPostPacket.getUser().getUsersToRoles();

			Set<com.nirvanaxp.global.types.entities.UsersToRole> globalRolesSet = new HashSet<com.nirvanaxp.global.types.entities.UsersToRole>();

			if (usersToRoleSet != null && usersToRoleSet.size() > 0)
			{

				for (UsersToRole usersToRole : usersToRoleSet)
				{

					// check if this role also has primary role/ global role
					// or not
					if (usersToRole != null && usersToRole.getPrimaryRoleInd() != null && usersToRole.getPrimaryRoleInd().trim().length() > 0)
					{
						com.nirvanaxp.global.types.entities.UsersToRole globalRoles = new com.nirvanaxp.global.types.entities.UsersToRole(usersToRole.getCreatedBy(),
								Integer.parseInt(usersToRole.getPrimaryRoleInd().trim()), usersToRole.getUpdatedBy(), usersToRole.getUsersId());
						if(usersToRole.getId()==null){
							usersToRole.setId(new StoreForwardUtilityForGlobal().generateDynamicIntId(globalEM,  httpRequest, "users_to_roles"));
							
						}
						globalRoles.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						globalRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

						// if current roles is added for user, then do not
						// duplicate it at global level
						if (globalRolesSet != null && globalRolesSet.contains(globalRoles) == false)
						{
							globalRolesSet.add(globalRoles);
						}

					}

				}
			}
			UserManagementObj userManagementObj = globalUsermanagement.addUserToGlobalAndLocalDatabase(httpRequest, globalEM, em, userPostPacket.getUser(),
					userPostPacket.getLocationId(), userPostPacket.getAddressList(), globalRolesSet,httpRequest,userPostPacket);

			// call synchPacket for store forward
			// create json packet for store forward
			userPostPacket.setUser(userManagementObj.getUser());
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			Location l = new CommonMethods().getBaseLocation(em);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, l.getId(), Integer.parseInt(userPostPacket.getMerchantId()));
						
			// usermanagementobj can never be null

			// addUpdateEmployeeMaster(em,
			// userPostPacket.getUser().getEmployeeMaster(),
			// userPostPacket,userManagementObj.getUser());

			if (userManagementObj.getResponse() != null && userManagementObj.getResponse().equals(MessageConstants.MSG_USER_ADDED_GLOBAL_LOCAL_DB))
			{
				// breadcast to client that a user has been added by
				// admin which is bussiness app users
				userPostPacket.setLocationId("-1");
				sendPacketForBroadcast(userPostPacket, POSNServiceOperations.UserManagementService_add.name(), false);
				return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);
			}
			else
			{
				return new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_EXISTS_IN_GLOBAL, userManagementObj.getResponse(), null)).toString();
			}

		}
		finally
		{

			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);

			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addNirvanaXPUser")
	public String addNirvanaXPUser(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		// sessionId = "f8de39b0e33b2db53e8082468ca9685";
		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			//json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
		//	new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));
						
			em = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP(httpRequest, sessionId);
			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			if (userPostPacket.getLocationId() == null || userPostPacket.getLocationId().length() == 0)
			{

				return new NirvanaXPException(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LOCATION_CANNOT_BE_BLANK_EXCEPTION, MessageConstants.ERROR_MESSAGE_LOCATION_CANNOT_BE_BLANK_DISPLAY_MESSAGE, null))
								.toString();
			}
			else
			{
				Set<UsersToRole> usersToRoleSet = userPostPacket.getUser().getUsersToRoles();

				Set<com.nirvanaxp.global.types.entities.UsersToRole> globalRolesSet = new java.util.HashSet<com.nirvanaxp.global.types.entities.UsersToRole>();

				if (usersToRoleSet != null && usersToRoleSet.size() > 0)
				{
					// convert posnirvana local users to roles to global orm
					// users to roles.
					for (UsersToRole usersToRole : usersToRoleSet)
					{

						// check if this role also has primary role/ global role
						// or not

						if (usersToRole != null && usersToRole.getPrimaryRoleInd() != null && usersToRole.getPrimaryRoleInd().trim().length() > 0)
						{
							com.nirvanaxp.global.types.entities.UsersToRole globalRoles = new com.nirvanaxp.global.types.entities.UsersToRole(usersToRole.getCreatedBy(),
									Integer.parseInt(usersToRole.getPrimaryRoleInd().trim()), usersToRole.getUpdatedBy(), usersToRole.getUsersId());
							if(usersToRole.getId()==null){
								usersToRole.setId(new StoreForwardUtilityForGlobal().generateDynamicIntId(em,  httpRequest, "users_to_roles"));
								
							}
							globalRoles.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							globalRoles.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							// if current roles is added for user, then do not
							// duplicate it at global level
							if (globalRolesSet != null && globalRolesSet.contains(globalRoles) == false)
							{
								globalRolesSet.add(globalRoles);
							}

						}

					}

				}
				EntityManager globalEM = null;
				try
				{
					globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

					UserManagementObj userManagementObj = globalUsermanagement.addNirvavaXPUserToGlobalAndLocalDatabase(httpRequest, globalEM, em, userPostPacket.getUser(),
							userPostPacket.getLocationId(), userPostPacket.getAddressList(), globalRolesSet,userPostPacket);
					
					if (userManagementObj != null)
					{
						if (userManagementObj.getResponse() != null && userManagementObj.getResponse().equals(MessageConstants.MSG_USER_ADDED_GLOBAL_LOCAL_DB))
						{
							// breadcast to client that a user has been added by
							// admin which is bussiness app users
							userPostPacket.setLocationId("-1");
							sendPacketForBroadcast(userPostPacket, POSNServiceOperations.UserManagementService_add.name(), false);
							return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);
						}
						else
						{
							return userManagementObj.getResponse();
						}
					}
					else
					{
						return "Error occured while adding user.";
					}
				}
				finally
				{
					GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
				}
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param syncUserPacket
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addUserUsingGlobalUserId")
	public String addGlobalUserIntoLocalDatabase(SyncUserPacket syncUserPacket) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(syncUserPacket, "SyncUserPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,syncUserPacket.getLocationId(), Integer.parseInt(syncUserPacket.getMerchantId()));
					
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			String loctionId;

			if (syncUserPacket.getLocationId() != null)
			{
				try
				{
					loctionId = syncUserPacket.getLocationId();
					if (loctionId == null)
						return "location id cannot be blank or 0";
				}
				catch (Exception e)
				{
					return "An Exception occured while parsing locationId";
				}
			}
			else
			{
				return "location id cannot be blank or 0";
			}

			EntityManager globalEM = null;
			try
			{
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

				User localUser = globalUsermanagement.addGlobalUserToLocalDatabaseIfNotExixts(globalEM, em, syncUserPacket.getGlobalUserId(), loctionId, syncUserPacket.getUsersToRolesSet(),httpRequest);

				if (localUser != null)
				{
					return new JSONUtility(httpRequest).convertToJsonString(localUser);
				}
				else
				{
					return "Error occured while adding user.";
				}
			}
			finally
			{
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateUserRelationships")
	public String updateUserRelationships(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			LocalUserUtil localUserUtil = new LocalUserUtil();
			localUserUtil.updateUserRelationshipTables(httpRequest, em, userPostPacket.getUser().getUsersToRoles(), userPostPacket.getUser().getUsersToLocations(),
					userPostPacket.getUser().getUsersToSocialMedias(), userPostPacket.getUser().getId());
			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket.getUser());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @param isPasswordUpdate
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateAdminUser/{isPasswordUpdate}")
	public String updateAdminUser(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("isPasswordUpdate") boolean isPasswordUpdate) throws Exception
	{

		EntityManager globalEM = null;

		EntityManager em = null;

		try
		{
			// create json packet for store forward
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, userPostPacket);

			
			// call synchPacket for store forward
			
			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			UserManagementObj userManagementObj = globalUsermanagement.updateAdminUser(httpRequest, globalEM, em, userPostPacket.getUser(), isPasswordUpdate, userPostPacket.getAddressList(),httpRequest,userPostPacket.getLocalServerURL());

			// addUpdateEmployeeMaster(em,
			// userPostPacket.getUser().getEmployeeMaster(),
			// userManagementObj.getUser());

			// usermanagementobj can never be null
			
			Location l = new CommonMethods().getBaseLocation(em);
			String json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, l.getId(), Integer.parseInt(userPostPacket.getMerchantId()));
	
			if (userManagementObj.getResponse() != null && userManagementObj.getResponse().length() > 0 && userManagementObj.getResponse().equals(MessageConstants.MSG_USER_UPDATED_GLOBAL_LOCAL_DB))
			{

				userPostPacket.setUser((User) new CommonMethods().getObjectById("User", em,User.class, userPostPacket.getUser().getId()));

				// update this user information in all other schemas as well
				// updateUserInfoInAllSchemas(sessionId,
				// userPostPacket.getUser());
				userPostPacket.setLocationId("-1");
				sendPacketForBroadcast(userPostPacket, POSNServiceOperations.UserManagementService_update.name(), false);
				return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);
			}
			else
			{
				return new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_EXISTS_IN_GLOBAL, userManagementObj.getResponse(), null)).toString();
			}

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/delete")
	public String delete(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			UserManagementServiceBean bean = new UserManagementServiceBean(httpRequest, em);
			String locationsId = null;
			Location location = null;
			User user = null;
			try
			{
				locationsId = userPostPacket.getLocationId();
			}
			catch (Exception e)
			{
				return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("location id cannot be blank or string", 1));
			}
			location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationsId);
			if (location == null)
			{
				return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("No location found for given location id", 2));
			}
			if (userPostPacket.getUser() == null)
			{
				return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("User cannot be null", 3));
			}
			if (userPostPacket.getUser().getId()==null)
			{
				return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("User id cannot be 0", 4));
			}
			user = (User) new CommonMethods().getObjectById("User", em,User.class, userPostPacket.getUser().getId());
			if (user == null)
			{
				return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("No user found for userId", 5));
			}

			bean.delete(sessionId, user, location, userPostPacket.getUpdatedBy());
			if (user != null)
			{
				String queryString = "select b from EmployeeMaster b where b.userId= ? and b.status not in('D') ";
				EmployeeMaster resultSet = null;

				Query query = em.createQuery(queryString).setParameter(1, user.getId());

				try
				{
					resultSet = (EmployeeMaster) query.getSingleResult();
					resultSet.setStatus("D");

					addUpdateEmployeeMaster(em, resultSet, user);
				}
				catch (Exception e)
				{
					// TODO: handle exception
					logger.severe("No Entity Found for Employee Master for user id " + user.getId());
				}

			}

			sendPacketForBroadcast(userPostPacket, POSNServiceOperations.UserManagementService_delete.name(), true);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteForAccount")
	public String deleteForAccount(UserPostPacket userPostPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			UserManagementServiceBean bean = new UserManagementServiceBean(httpRequest, em);
			Location location = null;
			User user = null;

			if (userPostPacket.getLocationsList() != null && userPostPacket.getLocationsList().size() > 0)
			{

				for (String locationId : userPostPacket.getLocationsList())
				{

					if (locationId != null)
					{
						location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;

						if (location == null)
						{
							return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("No location found for given location id", 2));
						}

						if (userPostPacket.getUser() == null)
						{
							return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("User cannot be null", 3));
						}
						if (userPostPacket.getUser().getId()==null)
						{
							return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("User id cannot be 0", 4));
						}
						user = (User) new CommonMethods().getObjectById("User", em,User.class, userPostPacket.getUser().getId());
						if (user == null)
						{
							return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("No user found for userId", 5));
						}

						bean.delete(sessionId, user, location, userPostPacket.getUpdatedBy());
					}
				}
			}

			sendPacketForBroadcast(userPostPacket, POSNServiceOperations.UserManagementService_delete.name(), false);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param id
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByUserID/{id}")
	public String getUserByUserId(@PathParam("id") String id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<User> usersList = new ArrayList<User>();
			String queryString = " select u.id,u.first_name,u.last_name,u.phone,u.email from users u where u.id=?";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, id).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				User user = new User();
				user.setFirstName((String) objRow[0]);
				user.setLastName((String) objRow[1]);
				user.setUsername((String) objRow[2]);

				usersList.add(user);
			}
			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param phoneNo
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllUsersStartingWithPhoneNo/{phone}")
	public String getAllUsersStartingWithPhoneNo(@PathParam("phone") String phoneNo, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

			Root<User> from = criteriaQuery.from(User.class);
			CriteriaQuery<User> select = criteriaQuery.select(from);

			Predicate predicate = criteriaBuilder.like(from.get(User_.phone), phoneNo + "%");

			criteriaQuery.where(predicate);

			TypedQuery<User> typedQuery = em.createQuery(select);
			List<User> resultList = typedQuery.getResultList();

			for (User user : resultList)
			{
				user.setPassword("");
				// user.setAuthPin("");
				user.setUsersToRoles(null);
				user.setUsersToSocialMedias(null);
				user.setUsersToSocialMedias(null);
				user.setUsersToLocations(null);

			}

			return new JSONUtility(httpRequest).convertToJsonString(resultList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param emailId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllUsersStartingWithEmailId/{emailId}")
	public String getAllUsersStartingWithEmailId(@PathParam("emailId") String emailId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// only customer query by Apoorva July 23 , 2015
			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
			Root<User> userRoot = criteriaQuery.from(User.class);
			TypedQuery<User> typedQuery = em.createQuery(criteriaQuery.select(userRoot).where(criteriaBuilder.like(userRoot.get(User_.email), emailId + "%")));

			List<User> resultList = typedQuery.getResultList();
			for (User user : resultList)
			{
				user.setPassword("");
				// user.setAuthPin("");
				user.setUsersToRoles(null);
				user.setUsersToSocialMedias(null);
				user.setUsersToSocialMedias(null);
				user.setUsersToLocations(null);
			}

			return new JSONUtility(httpRequest).convertToJsonString(resultList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param emailId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllEmailIdsStartingWith/{emailId}")
	public String getEmailSimilarToEmailId(@PathParam("emailId") String emailId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "SELECT u.email FROM users u, users_to_roles utr, roles r " + " where u.id=utr.users_id and r.id=utr.roles_id and r.role_Name='POS Customer' and u.email like  ? ";
			Query q = em.createNativeQuery(queryString).setParameter(1, emailId + "%");
			@SuppressWarnings("unchecked")
			List<String> objList = q.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(objList);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param phoneNo
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllPhoneNumberStartingWith/{phone}")
	public String getPhoneNumberSimilarToPhoneNo(@PathParam("phone") String phoneNo, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "SELECT u.phone FROM users u, users_to_roles utr, roles r where u.id=utr.users_id and r.id=utr.roles_id and r.role_Name='POS Customer' and u.phone like ? ";
			Query q = em.createNativeQuery(queryString).setParameter(1, phoneNo + "%");
			@SuppressWarnings("unchecked")
			List<String> objList = q.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(objList);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param phoneNumber
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserByPhoneNumber/{phoneNumber}")
	public String getUserByPhoneNumber(@PathParam("phoneNumber") String phoneNumber, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = new UserManagementServiceBean(httpRequest, em).getUserByPhoneNo(phoneNumber);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param phoneNumber
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getGlobalUserByPhoneNumber/{phoneNumber}")
	public String getGlobalUserByPhoneNumber(@PathParam("phoneNumber") String phoneNumber, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager globalEM = null;
		try
		{
			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			com.nirvanaxp.global.types.entities.User users = new GlobalUserUtil().getGlobalUserByPhoneNumber(globalEM, phoneNumber);
			return new JSONUtility(httpRequest).convertToJsonString(users);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	/**
	 * 
	 *
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUsersWithoutCustomer")
	public String getUsersWithoutCustomer(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<User> usersList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = " select u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status from users u left join users_to_roles utr on u.id=utr.users_id "
					+ " left join roles r on r.id=utr.roles_id where r.role_name != 'POS Customer' and u.status !='D' group by u.id ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);
				user.setStatus((String) objRow[6].toString());

				usersList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param roleId
	 * @param locationId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUsersByRoleIdLocationId/{roleId}/{locationId}")
	public String getUsersByRoleIdLocationId(@PathParam("roleId") int roleId, @PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<User> usersList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = " select u.id,u.first_name,u.last_name,u.email,u.phone,u.username from users u left join users_to_roles utr on u.id=utr.users_id "
					+ " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id=? and utr.roles_id=?";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, roleId).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);

				usersList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
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
	@Path("/getAllAdminUsersByLocationId/{locationId}")
	public String getAllAdminUsersByLocationId(@PathParam("locationId") String locationId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			List<User> usersList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = "select u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status "
					+ "from users u left join users_to_roles utr on u.id=utr.users_id left join roles r on r.id=utr.roles_id"
					+ " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id= ?  and  "
					+ "r.role_name != 'POS Customer' and u.status != 'D' group by u.id"
					+ " order by u.first_name, u.last_name asc ";
			

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);
				user.setStatus((String) objRow[6].toString());

				try
				{
					/*
					 * CriteriaBuilder builder = em.getCriteriaBuilder();
					 * CriteriaQuery<EmployeeMaster> criteria =
					 * builder.createQuery(EmployeeMaster.class);
					 * Root<EmployeeMaster> r =
					 * criteria.from(EmployeeMaster.class);
					 * TypedQuery<EmployeeMaster> query =
					 * em.createQuery(criteria.select(r).where(builder.equal(r.
					 * get(EmployeeMaster_.userId), user.getId()),
					 * builder.notEqual(r.get(EmployeeMaster_.status), 'D')));
					 * user.setEmployeeMaster(query.getSingleResult());
					 */

					String queryString1 = "select b from EmployeeMaster b where b.userId= ? and b.status not in('D')";
					EmployeeMaster resultSet = null;

					Query query = em.createQuery(queryString1).setParameter(1, user.getId());
					resultSet = (EmployeeMaster) query.getSingleResult();
					if (resultSet != null)
					{
					
						List<EmployeeMasterToJobRoles> resultSetRolls = null;
						try
						{
							String queryStringRolls = "select b from EmployeeMasterToJobRoles b where b.status not in('D','I') and b.userId = " + user.getId();

							Query queryRolls = em.createQuery(queryStringRolls);
							resultSetRolls = queryRolls.getResultList();

						}
						catch (Exception e)
						{
							// todo shlok need
							// handel proper exception

							logger.severe("No result Found for Employee Master To Job Rolls ");
						}
						
						if (resultSetRolls != null && resultSetRolls.size() > 0)
						{
							resultSet.setEmployeeMasterToJobRoles(resultSetRolls);
						}
						
						user.setEmployeeMaster(resultSet);
					}

				}
				catch (Exception e)
				{
					logger.severe("No Employee Master Found for User Id " + user.getId());
				}

				usersList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}


	@SuppressWarnings("unchecked")
	@GET
	@Path("/getAllAdminUsersByLocationIdAndLocationType/{locationId}/{locationTypeId}")
	public String getAllAdminUsersByLocationIdAndLocationType(@PathParam("locationId") String locationId,@PathParam("locationTypeId") int locationTypeId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			List<User> usersList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = "select u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status "
				+ "from users u left join users_to_roles utr on u.id=utr.users_id left join roles r on r.id=utr.roles_id "
				+ "left join users_to_locations utl on u.id=utl.users_id "
				+ "left join locations l on l.id=utl.locations_id where "
				+ "((r.role_name != 'POS Customer' and utl.locations_id=?) "
				+ "or (u.id in (select uu.id from users uu join users_to_locations uutl on uutl.users_id=uu.id "
				+ "join locations ul on ul.id=uutl.locations_id where ul.id in (select ls.supplier_id from locations_to_supplier ls "
				+ "left join locations l on l.id = ls.supplier_id "
				+ "where ls.locations_id = ? and  ls.status not in  ('D','I') and "
				+ "l.status  not in  ('D','I') and l.locations_type_id = ?))))"
				+ " and u.status != 'D' group by u.id"; 
		// removed by Ap date 12-09-18 for hiding 25000 customers	
		/*	String queryString = "select u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status "
					+ "from users u left join users_to_roles utr on u.id=utr.users_id left join roles r on r.id=utr.roles_id "
					+ "left join users_to_locations utl on u.id=utl.users_id "
					+ "left join locations l on l.id=utl.locations_id where "
					+ "((utl.locations_id=?) "
					+ "or (u.id in (select uu.id from users uu join users_to_locations uutl on uutl.users_id=uu.id "
					+ "join locations ul on ul.id=uutl.locations_id where ul.id in (select ls.supplier_id from locations_to_supplier ls "
					+ "left join locations l on l.id = ls.supplier_id "
					+ "where ls.locations_id = ? and  ls.status not in  ('D','I') and "
					+ "l.status  not in  ('D','I') and l.locations_type_id = ?))))"
					+ " and u.status != 'D' group by u.id";
*/
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationId).setParameter(2, locationId).setParameter(3, locationTypeId).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);
				user.setStatus((String) objRow[6].toString());

				try
				{

					String queryString1 = "select b from EmployeeMaster b where b.userId= ? and b.status not in('D')";
					EmployeeMaster resultSet = null;

					Query query = em.createQuery(queryString1).setParameter(1, user.getId());
					resultSet = (EmployeeMaster) query.getSingleResult();
					if (resultSet != null)
					{
						user.setEmployeeMaster(resultSet);
					}

				}
				catch (Exception e)
				{
					logger.severe("No Employee Master Found for User Id " + user.getId());
				}
				
				
				usersList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllUsersFromLocalDatabase")
	public String getAllUsersFromLocalDatabase(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<User> usersList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = " select u.id,u.first_name,u.last_name,u.email,u.phone,u.username from users u";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList)
			{
				// if this has primary key not 0
				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);
				user.setStatus((String) objRow[6]);

				usersList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(usersList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	// private void updateUserInfoInAllSchemas(String sessionId, User user)
	// throws IOException, InvalidSessionException {
	//
	// EntityManager em = null;
	// em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
	// sessionId);
	//
	// String queryString = "call updateLocalUser( " + user.getId() + " )";
	//
	// EntityTransaction tx = em.getTransaction();
	// try {
	// // start transaction
	// tx.begin();
	// em.createNativeQuery(queryString).executeUpdate();
	// tx.commit();
	// }
	// catch (RuntimeException e) {
	// // on error, if transaction active,
	// // rollback
	// if (tx != null && tx.isActive()) {
	// tx.rollback();
	// }
	// throw e;
	// }
	// }

	/**
	 * 
	 *
	 * @param userPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/update")
	public String update(UserPostPacket userPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		User user = null;

		EntityManager localEM = null;
		EntityManager globalEM = null;
		String json = null;
		User user2= null;
		try
		{
			user = userPacket.getUser();

			if (user == null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
			}

			// Set<Address> addressList = userPacket.getAddressList();
			// if (addressList != null && addressList.size() > 0) {
			// user.setAddressSet(addressList);
			// }

			localEM = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			// create json packet for store forward
			
			user2 = updateUsersInfoInLocalDb(localEM, user);

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			Set<Address> addressSet = globalUsermanagement.updateAddressInfoInGlobalAndLocalDb(httpRequest,localEM, globalEM, userPacket.getAddressList(), user.getId(), user.getGlobalUsersId());
			userPacket.setAddressList(addressSet);
			// Set<Account> accountAssociatedByUserSet = user.getAccountsSet();
			UserPostPacket userPostPacket2 = createPacketForPush(userPacket);
			sendPacketForBroadcast(userPostPacket2, POSNServiceOperations.UserManagementService_update.name(), false);
			// if (accountAssociatedByUserSet != null &&
			// accountAssociatedByUserSet.size() > 0) {
			//
			// for (Account account : accountAssociatedByUserSet) {
			// userPostPacket2.setMerchantId("" + account.getId());
			// // when we set location id -1, then packet will be
			// // broadcasted on basis of merchant info only
			// // in this way all schemas where user must have existed
			// // will get the push
			// userPostPacket2.setLocationId("" + "-1");
			// sendPacketForBroadcast(userPostPacket2,
			// POSNServiceOperations.GlobalUserManagementService_updateCustomer.name());
			// }
			//
			// }
			userPacket.setUser(user2);
			json = new StoreForwardUtility().returnJsonPacket(userPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPacket.getLocationId(), Integer.parseInt(userPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(userPacket);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
		}

	}

	/**
	 * 
	 *
	 * @param em
	 * @param user
	 */
	private User updateUsersInfoInLocalDb(EntityManager em, User user)
	{

		User userInDatabase = new UserManagementServiceBean(httpRequest, em).getUserById(user.getId());
		if(userInDatabase== null){
			LocalUserUtil localUserUtil = new LocalUserUtil();

			// check if phone or email is duplicate or not
			userInDatabase = localUserUtil.findLocalUserByPhoneOrEmailOrUserName(em, user.getPhone(), user.getEmail(), user.getUsername());
		
		}
		userInDatabase.setFirstName(user.getFirstName());
		userInDatabase.setLastName(user.getLastName());
		userInDatabase.setDateofbirth(user.getDateofbirth());
		userInDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		userInDatabase.setUpdatedBy(user.getUpdatedBy());
		userInDatabase.setPhone(user.getPhone());
		userInDatabase.setEmail(user.getEmail());
		userInDatabase.setCountryId(user.getCountryId());
		// set email to null, if blank or empty string is passed, otherwise user
		// will not be added due to unique constraint
		if (userInDatabase.getEmail() != null && userInDatabase.getEmail().trim().length() == 0)
		{
			userInDatabase.setEmail(null);
		}

		// set phone to null, if blank or empty string is passed, otherwise user
		// will not be added due to unique constraint
		if (userInDatabase.getPhone() != null && userInDatabase.getPhone().trim().length() == 0)
		{
			userInDatabase.setPhone(null);
		}

		// null auth pin must go not blank
		if (userInDatabase.getAuthPin() != null && userInDatabase.getAuthPin().trim().length() == 0)
		{
			userInDatabase.setAuthPin(null);
		}

		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(userInDatabase);
			tx.commit();
		}
		catch (RuntimeException e)
		{
			// on error, if transaction active, rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}

		return userInDatabase;
	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @return
	 */
	private UserPostPacket createPacketForPush(UserPostPacket userPostPacket)
	{

		UserPostPacket userPostPacketForPush = new UserPostPacket();
		userPostPacketForPush.setClientId(userPostPacket.getClientId());
		userPostPacketForPush.setMerchantId(userPostPacket.getMerchantId());
		userPostPacketForPush.setLocationId(userPostPacket.getLocationId());
		userPostPacketForPush.setSchemaName(userPostPacket.getSchemaName());
		userPostPacketForPush.setEchoString(userPostPacket.getEchoString());
		User userForPush = new User();
		userForPush.setFirstName(userPostPacket.getUser().getFirstName());
		userForPush.setLastName(userPostPacket.getUser().getLastName());
		userForPush.setEmail(userPostPacket.getUser().getEmail());
		userForPush.setPhone(userPostPacket.getUser().getPhone());
		userForPush.setId(userPostPacket.getUser().getId());
		userForPush.setGlobalUsersId(userPostPacket.getUser().getGlobalUsersId());
		userPostPacketForPush.setUser(userForPush);
		return userPostPacketForPush;

	}

	/**
	 * 
	 *
	 * @param userPostPacket
	 * @param operationName
	 * @param shouldBroadcastObj
	 */
	private void sendPacketForBroadcast(UserPostPacket userPostPacket, String operationName, boolean shouldBroadcastObj)
	{

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
			String internalJson = objectMapper.writeValueAsString(userPostPacket.getUser());

			operationName = ServiceOperationsUtility.getOperationName(operationName);
			MessageSender messageSender = new MessageSender();

			if (shouldBroadcastObj)
			{
				messageSender.sendMessage(httpRequest, userPostPacket.getClientId(), POSNServices.UserManagementService.name(), operationName, internalJson, userPostPacket.getMerchantId(),
						userPostPacket.getLocationId(), userPostPacket.getEchoString(), userPostPacket.getSchemaName());
			}
			else
			{
				messageSender.sendMessage(httpRequest, userPostPacket.getClientId(), POSNServices.UserManagementService.name(), operationName, null, userPostPacket.getMerchantId(),
						userPostPacket.getLocationId(), userPostPacket.getEchoString(), userPostPacket.getSchemaName());
			}

		}
		catch (JsonGenerationException e)
		{
			logger.severe(httpRequest, e);
		}
		catch (JsonMappingException e)
		{
			logger.severe(httpRequest, e);
		}
		catch (IOException e)
		{
			logger.severe(httpRequest, e);
		}

	}

	/**
	 * 
	 *
	 * @param globalRoleId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllRolesByGlobalRoleId/{globalRoleId}")
	public String getAllRolesByGlobalRoleId(@PathParam("globalRoleId") int globalRoleId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> r = criteria.from(Role.class);
			TypedQuery<Role> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(Role_.globalRoleId), globalRoleId), builder.notEqual(r.get(Role_.status), "D")));

			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param locationId
	 * @param toDate
	 * @param fromDate
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCustomerByLocationId/{locationId}/{toDate}/{fromDate}")
	public String getAllCustomerByLocationId(@PathParam("locationId") String locationId, @PathParam("toDate") String toDate, @PathParam("fromDate") String fromDate,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<UserDetailWithVisitCountDetails> userList = new ArrayList<UserDetailWithVisitCountDetails>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = "select distinct u.id,u.first_name,u.last_name,u.email,u.phone,u.username, (select count(*) from reservations where users_id=u.id) as visitCount from users u left join users_to_roles utr on u.id=utr.users_id "
					+ " left join roles r on r.id=utr.roles_id" + " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id='" + locationId + "' and  r.role_name = 'POS Customer' "
					+ " and u.created between  ?  and ? ";

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, toDate + " 00:00:00").setParameter(2, fromDate + " 23:59:59").getResultList();
			for (Object[] objRow : resultList)
			{

				// if this has primary key not 0
				UserDetailWithVisitCountDetails countDetails = new UserDetailWithVisitCountDetails();
				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);
				countDetails.setUser(user);
				countDetails.setVisitCount((Integer) objRow[6]);
				userList.add(countDetails);
			}

			return new JSONUtility(httpRequest).convertToJsonString(userList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * @param locationIdPacket
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getAllAdminUsersByLocationIdList/")
	public String getAllAdminUsersByLocationIdList(LocationIdPacket locationIdPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(locationIdPacket, "LocationIdPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,  locationIdPacket.getLocationId(), Integer.parseInt(locationIdPacket.getMerchantId()));
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			return new JSONUtility(httpRequest).convertToJsonString(new UserManagementServiceBean(httpRequest, em).getAllAdminUsersByLocationIdList(locationIdPacket.getLocationsId()));
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param addressId
	 * @param userId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/deleteAddress/{addressId}/{userId}")
	public String delete(@PathParam("addressId") String addressId, @PathParam("userId") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			Address address = (Address) new CommonMethods().getObjectById("Address", em,Address.class, addressId);

			if (address != null)
			{
//				new StoreForwardUtility().callSynchPacketsWithServer(null, httpRequest, address.getl, 0);
				
				EntityTransaction tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.remove(em.merge(address));
					tx.commit();
				}
				catch (RuntimeException e)
				{
					// on error, if transaction active, rollback
					if (tx != null && tx.isActive())
					{
						tx.rollback();
					}
					throw e;
				}

			}

			UsersToAddress usersToAddress = getUsersToAddressByAddressIdAndUserId(em, addressId, userId);

			if (usersToAddress != null)
			{
				EntityTransaction tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.remove(em.merge(usersToAddress));
					tx.commit();
				}
				catch (RuntimeException e)
				{
					// on error, if transaction active, rollback
					if (tx != null && tx.isActive())
					{
						tx.rollback();
					}
					throw e;
				}

			}

			return "true";
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserToPaymentByUserId/{userId}")
	public String getUserToPaymentByUserId(@PathParam("userId") String userId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);

			UsersToPayment userToPayment = new UserManagementServiceBean(httpRequest, em).getUserToPaymentByUserId(userId);
			return new JSONUtility(httpRequest).convertToJsonString(userToPayment);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * 
	 *
	 * @param usersToPaymentPacket
	 * @param sessionId
	 * @return
	 * @throws FileNotFoundException
	 * @throws InvalidSessionException
	 * @throws IOException
	 */
	@POST
	@Path("/updateUsersToPayment")
	public String updateUsersToPayment(UsersToPaymentPacket usersToPaymentPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws FileNotFoundException, InvalidSessionException, IOException
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, usersToPaymentPacket);
			tx = em.getTransaction();
			tx.begin();
			UsersToPayment result = new UserManagementServiceBean(httpRequest, em).updateUsersToPayment(usersToPaymentPacket.getUsersToPayment());
			tx.commit();
			usersToPaymentPacket.setUsersToPayment(result);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(usersToPaymentPacket, "UsersToPaymentPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, usersToPaymentPacket.getLocationId(), Integer.parseInt(usersToPaymentPacket.getMerchantId()));
					
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param usersToPaymentPacket
	 * @param sessionId
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Path("/addUsersToPayment")
	public String addUsersToPayment(UsersToPaymentPacket usersToPaymentPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try
		{
			
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, usersToPaymentPacket);
			tx = em.getTransaction();
			tx.begin();
			UsersToPayment result = new UserManagementServiceBean(httpRequest, em).addUsersToPayment(usersToPaymentPacket.getUsersToPayment(),httpRequest,usersToPaymentPacket.getLocationId());
			tx.commit();
			usersToPaymentPacket.setUsersToPayment(result);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(usersToPaymentPacket, "UsersToPaymentPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,usersToPaymentPacket.getLocationId(), Integer.parseInt(usersToPaymentPacket.getMerchantId()));
					
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param usersToPaymentHistoryPacket
	 * @return
	 * @throws Exception 
	 */
	@POST
	@Path("/addUpdateUsersToPaymentHistory")
	public String addUpdateUsersToPaymentHistory(UsersToPaymentHistoryPacket usersToPaymentHistoryPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			UsersToPaymentHistory result = new UserManagementServiceBean(httpRequest, em).addUpdateUsersToPaymentHistory(usersToPaymentHistoryPacket.getUsersToPaymentHistory(),usersToPaymentHistoryPacket.getLocationId(),httpRequest,usersToPaymentHistoryPacket.getLocalServerURL());
			tx.commit();
			usersToPaymentHistoryPacket.setUsersToPaymentHistory(result);
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(usersToPaymentHistoryPacket, "UsersToPaymentHistoryPacket",httpRequest);
			// call synchPacket for store forward
						
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, usersToPaymentHistoryPacket.getLocationId(), Integer.parseInt(usersToPaymentHistoryPacket.getMerchantId()));
			
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param em
	 * @param addressId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private UsersToAddress getUsersToAddressByAddressIdAndUserId(EntityManager em, String addressId, String userId) throws Exception
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToAddress> criteria = builder.createQuery(UsersToAddress.class);
		Root<UsersToAddress> r = criteria.from(UsersToAddress.class);
		TypedQuery<UsersToAddress> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToAddress_.addressId), addressId), builder.equal(r.get(UsersToAddress_.usersId), userId)));
		UsersToAddress result = query.getSingleResult();
		return result;
	}

	/**
	 * 
	 *
	 * @param userId
	 * @param startIndex
	 * @param endIndex
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserToPaymentHistoryByUserId/{userId}/{startIndex}/{endIndex}")
	public String getUserToPaymentHistoryByUserId(@PathParam("userId") String userId, @PathParam("startIndex") int startIndex, @PathParam("endIndex") int endIndex) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			UserToPaymentDisplayPacket displayPacket = new UserToPaymentDisplayPacket();
			UsersToPayment userToPayment = new UserManagementServiceBean(httpRequest, em).getUserToPaymentByUserId(userId);

			displayPacket.setUsersToPayment(userToPayment);

			startIndex = startIndex - 1;


			String sql = "SELECT uph.amount_paid, uph.balance_due, " + "uph.payment_type_id,uph.users_to_payment_id  ,oh.id,  oh.order_number, uph.created,uph.status ,uph.payment_method_type_id "
					+ "from users_to_payment_history uph " 
					+ "left join order_payment_details opd on opd.id = uph.order_payment_details_id "
					+ "left join order_header oh on opd.order_header_id = oh.id " 
					+ "where users_to_payment_id = ?   order by uph.id desc "

					+ "limit " + startIndex + "," + endIndex;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(sql).setParameter(1, userToPayment.getId()).getResultList();
			List<UserToPaymentHistoryDisplayPacket> ans = new ArrayList<UserToPaymentHistoryDisplayPacket>();

			for (Object[] objRow : resultList)
			{
				UserToPaymentHistoryDisplayPacket detailDisplayPacket = new UserToPaymentHistoryDisplayPacket();
				detailDisplayPacket.setAmountPaid((BigDecimal) objRow[0]);
				detailDisplayPacket.setBalanceDue((BigDecimal) objRow[1]);
				detailDisplayPacket.setPaymentTypeId((Integer) objRow[2]);
				detailDisplayPacket.setUsersToPaymentId((String) objRow[3]);

				if (objRow[4] != null)
					detailDisplayPacket.setOrderId((String) objRow[4]);
				if (objRow[5] != null)
					detailDisplayPacket.setOrderNumber((String) objRow[5]);

				detailDisplayPacket.setCreated((Timestamp) objRow[6]);
				detailDisplayPacket.setStatus((Character) objRow[7]);
				detailDisplayPacket.setPaymentMethodTypeId((String) objRow[8]);
				ans.add(detailDisplayPacket);
			}
			tx.commit();
			displayPacket.setUsersToPaymentHistoryDisplayPacket(ans);
			return new JSONUtility(httpRequest).convertToJsonString(displayPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getUserToPaymentHistoryCountByUserId/{userId}")
	public BigInteger getUserToPaymentHistoryCountByUserId(@PathParam("userId") String userId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String sql = "SELECT count(*)" + "from users_to_payment_history uph " + "left join order_payment_details opd on opd.id = uph.order_payment_details_id "
					+ "join users_to_payment up on up.id = uph.users_to_payment_id " + "left join order_header oh on opd.order_header_id = oh.id where up.users_id = ?  ";

			BigInteger resultList = (BigInteger) em.createNativeQuery(sql).setParameter(1, userId).getSingleResult();

			return resultList;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	/**
	 * 
	 *
	 * @param em
	 * @param o
	 * @param user
	 */
	private void addUpdateEmployeeMaster(EntityManager em, EmployeeMaster o, User user)
	{

		if (o != null)
		{
			o.setUserId(user.getId());
			try
			{
				String queryString = "select b from EmployeeMaster b where b.userId= ? and b.status not in('D') ";
				EmployeeMaster resultSet = null;

				Query query = em.createQuery(queryString).setParameter(1, o.getUserId());
				resultSet = (EmployeeMaster) query.getSingleResult();

				if (resultSet != null)
				{
					o.setId(resultSet.getId());
				}
			}
			catch (Exception e)
			{
				logger.severe("No result Found for Employee Master");
			}

			if (o.getId() == null)
			{
				o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			}
			else
			{
				o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			}
			em.merge(o);

			EntityTransaction tx = em.getTransaction();
			try
			{
				if (tx.isActive())
				{

					tx.commit();

				}
				else
				{
					tx.begin();
					tx.commit();
				}

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

	}
	@GET
	@Path("/getAllCustomerByLocationEmailPhoneAndName/{locationId}/{phoneNo}/{emailId}/{userName}/{startIndex}/{endIndex}")
	public String getAllCustomerByLocationEmailPhoneAndName(@PathParam("locationId") String locationId, @PathParam("phoneNo") String phoneNo,
			 @PathParam("emailId") String emailId, @PathParam("userName") String userName,@PathParam("startIndex") String startIndex, @PathParam("endIndex") String endIndex,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<User> userList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = "select distinct u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status, (select count(*) from reservations where users_id=u.id) as visitCount from users u left join users_to_roles utr on u.id=utr.users_id "
					+ " left join roles r on r.id=utr.roles_id" + " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id='" + locationId + "' and  r.role_name = 'POS Customer' ";
					
			if(userName!=null && !userName.equals("null")){
				queryString+=" and u.username="+userName;	
			}if(emailId!=null && !emailId.equals("null")){
				queryString+=" and u.email="+emailId;
			}if(phoneNo!=null && !phoneNo.equals("null")){
				queryString+=" and u.phone="+phoneNo;
			}
			queryString+= "and u.status!='D' limit "+ startIndex + "," + endIndex;


			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList)
			{

				User user = new User();
				user.setId((String) objRow[0]);
				user.setFirstName((String) objRow[1]);
				user.setLastName((String) objRow[2]);
				user.setEmail((String) objRow[3]);
				user.setPhone((String) objRow[4]);
				user.setUsername((String) objRow[5]);
				user.setStatus((String)""+ objRow[6]);
				userList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(userList);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getCountOfAllCustomerByLocationEmailPhoneAndName/{locationId}/{phoneNo}/{emailId}/{userName}")
	public String getCountOfAllCustomerByLocationEmailPhoneAndName(@PathParam("locationId") String locationId, @PathParam("phoneNo") String phoneNo,
			 @PathParam("emailId") String emailId, @PathParam("userName") String userName,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			List<User> userList = new ArrayList<User>();
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = "select distinct u.id,u.first_name,u.last_name,u.email,u.phone,u.username,u.status, (select count(*) from reservations where users_id=u.id) as visitCount from users u left join users_to_roles utr on u.id=utr.users_id "
					+ " left join roles r on r.id=utr.roles_id" + " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id='" + locationId + "' and  r.role_name = 'POS Customer' ";
					
			if(userName!=null && !userName.equals("null")){
				queryString+=" and u.username="+userName;	
			}if(emailId!=null && !emailId.equals("null")){
				queryString+=" and u.email="+emailId;
			}if(phoneNo!=null && !phoneNo.equals("null")){
				queryString+=" and u.phone="+phoneNo;
			}
			queryString+= "and u.status!='D' ";


			
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).getResultList();
			for (Object[] objRow : resultList)
			{

				User user = new User();
				user.setId((String) objRow[0]);
				
				userList.add(user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(userList.size());
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@GET
	@Path("/getUserToDiscountByUserId/{id}")
	public String getUserToDiscountByUserId(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
		
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = new UserManagementServiceBean(httpRequest, em).getUserToDiscountByUserId(id);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	@POST
	@Path("/addUpdateUserToDiscount")
	public String addUpdateUserToDiscount(UsersToDiscountPacket usersToDiscountPacket) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(usersToDiscountPacket, "UsersToDiscountPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,usersToDiscountPacket.getLocationId(), Integer.parseInt(usersToDiscountPacket.getMerchantId()));
					em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			tx = em.getTransaction();
			tx.begin();
			List<UsersToDiscount> result = new UserManagementServiceBean(httpRequest, em).addUpdateUserToDiscount(usersToDiscountPacket.getUsersToDiscountList(),usersToDiscountPacket.getUsersId(),httpRequest);
			tx.commit();
			return new JSONUtility(httpRequest).convertToJsonString(result);
		}
		catch (Exception e)
		{
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}
	
/*	@GET
	@Path("/gg/")
	public String gg() throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			StoreForwardUtility forwardUtility = new  StoreForwardUtility();
			int d = forwardUtility.getMaxIntIdByTablename(em, "server_Config");
			return new JSONUtility(httpRequest).convertToJsonString(d);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}*/
}
