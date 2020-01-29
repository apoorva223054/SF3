/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
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

import org.apache.commons.codec.digest.DigestUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.AuthCodeGenerator;
import com.nirvanaxp.common.utils.synchistory.InsertIntoHistory;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserAuth;
import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.SyncUserPacket;
import com.nirvanaxp.services.jaxrs.packets.UserPostPacket;
import com.nirvanaxp.services.jaxrs.packets.UserPostPacketForGlobalUser;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.LocationSetting;
import com.nirvanaxp.types.entities.sms.SMSTemplate;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToAddress;
import com.nirvanaxp.types.entities.user.UsersToAddress_;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entities.user.UsersToDiscount_;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entity.snssms.SmsConfig;
import com.nirvanaxp.user.utility.GlobalUsermanagement;
import com.nirvanaxp.user.utility.UserManagementObj;
import com.nirvanaxp.user.utility.UserManagementServiceBean;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * @author nirvanaxp
 *
 */
@WebListener
@Path("/CustomerService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerService extends AbstractNirvanaService
{

	/**  */
	@Context
	HttpServletRequest httpRequest;

	/**  */
	// private EntityManager em;
	private final static NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

	/* (non-Javadoc)
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
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getNirvanaxpUserWithAddressById/{id}")
	public String getNirvanaxpUserWithAddressById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumberForNirvanaXP(httpRequest, auth_token);

			UserPostPacket userPostPacket = new UserPostPacket();
			// fetching existing user
			User user = new UserManagementServiceBean(httpRequest, em).getUserById(id);
			userPostPacket.setUser(user);
			if (user != null && user.getGlobalUsersId()!=null)
			{
				GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
				EntityManager globalEM = null;
				try
				{
					globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
					// fetching user address by global user id
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
	 * @param userPostPacket
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addCustomer")
	public String addCustomer(UserPostPacket userPostPacket) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

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
			int needToSendSNS=userPostPacket.getLocalServerURL();
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			String locationId = userPostPacket.getLocationId();
			User localuser = userPostPacket.getUser();
			Set<Address> localAddressSet = userPostPacket.getAddressList();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			// adding customer in global and local
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
				// checking user info for duplicacy
				String phoneOfAlreadyExistingUser = userManagementObj.getUser().getPhone();
				String emailOfAlreadyExistingUser = userManagementObj.getUser().getEmail();
				String usernameOfAlreadyExistingUser = userManagementObj.getUser().getUsername();

				if (phoneOfUserSentByClient != null && phoneOfUserSentByClient.trim().length() > 0 && phoneOfAlreadyExistingUser != null && phoneOfAlreadyExistingUser.trim().length() > 0)
				{

					if (phoneOfUserSentByClient.trim().equals(phoneOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Phone number already Exists.";
					}
				}

				if (emailOfUserSentByClient != null && emailOfUserSentByClient.trim().length() > 0 && emailOfAlreadyExistingUser != null && emailOfAlreadyExistingUser.trim().length() > 0)
				{

					if (emailOfUserSentByClient.trim().equals(emailOfAlreadyExistingUser.trim()))
					{
						errorMessage += "Email already Exists.";
					}

				}

				if (usernameOfUserSentByClient != null && usernameOfUserSentByClient.trim().length() > 0 && usernameOfAlreadyExistingUser != null && usernameOfAlreadyExistingUser.trim().length() > 0)
				{

					if (usernameOfUserSentByClient.trim().equals(usernameOfAlreadyExistingUser.trim()))
					{
						errorMessage += "Username already Exists.";
					}

				}

				return new NirvanaServiceErrorResponse(errorMessage, errorMessage, null).toString();
			}

			// user was added and appropriate details were returned
			localuser = userManagementObj.getUser();

			userPostPacket.setUser(localuser);
			userPostPacket.setAddressList(localAddressSet);

			sendPacketForBroadcast(createPacketForPush(userPostPacket), POSNServiceOperations.UserManagementService_addCustomer.name(), true);

			
			//Send SNS SMS
			sendSNSByNumber(userPostPacket.getUser(),userPostPacket.getLocationId(), auth_token,false,needToSendSNS);
			
			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}

	/**
	 * @param userPostPacket
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addUpdateCustomer")
	public String addUpdateCustomer(UserPostPacket userPostPacket) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

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
			int needToSendSNS=userPostPacket.getLocalServerURL();
			
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));


			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			String locationId =  userPostPacket.getLocationId();
			User localuser = userPostPacket.getUser();
			Set<Address> localAddressSet = userPostPacket.getAddressList();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			// add update of customer in global and local user
			UserManagementObj userManagementObj = globalUsermanagement.addUpdateCustomer(httpRequest, globalEM, localEM, localuser, locationId, localAddressSet,userPostPacket);

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

				if (usernameOfUserSentByClient != null && usernameOfUserSentByClient.trim().length() > 0 && usernameOfAlreadyExistingUser != null && usernameOfAlreadyExistingUser.trim().length() > 0)
				{

					if (usernameOfUserSentByClient.toUpperCase().trim().equals(usernameOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Username already Exists.";
					}

				}

				if (phoneOfUserSentByClient != null && phoneOfUserSentByClient.trim().length() > 0 && phoneOfAlreadyExistingUser != null && phoneOfAlreadyExistingUser.trim().length() > 0)
				{

					if (phoneOfUserSentByClient.trim().equals(phoneOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Phone number already Exists.";
					}
				}

				if (emailOfUserSentByClient != null && emailOfUserSentByClient.trim().length() > 0 && emailOfAlreadyExistingUser != null && emailOfAlreadyExistingUser.trim().length() > 0)
				{

					if (emailOfUserSentByClient.trim().equals(emailOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Email already Exists.";
					}

				}

				return new NirvanaServiceErrorResponse(errorMessage, errorMessage, null).toString();
			}

			// user was added and appropriate details were returned
			localuser = userManagementObj.getUser();

			userPostPacket.setUser(localuser);
			userPostPacket.setAddressList(localAddressSet);

			sendPacketForBroadcast(createPacketForPush(userPostPacket), POSNServiceOperations.UserManagementService_addCustomer.name(), true);

			if(userManagementObj.getUserExist() == 1)
			{
				sendSNSByNumber(userPostPacket.getUser(),userPostPacket.getLocationId(), auth_token,false,needToSendSNS);	
			}
			
			
			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}

	/**
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
	 * @param id
	 * @return returns global user by id
	 * @throws Exception
	 */
	@GET
	@Path("/getGlobalUserById/{id}")
	public String getGlobalUserById(@PathParam("id") String id) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			return new JSONUtility(httpRequest).convertToJsonString(globalUsermanagement.getGlobalUserById(em, id));

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Adds the nirvana XP user.
	 *
	 * @param userPostPacket
	 *            the user post packet
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	@POST
	@Path("/addNirvanaXPUser")
	public String addNirvanaXPUser(UserPostPacket userPostPacket) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

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
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();

			String locationId = userPostPacket.getLocationId();
			User localuser = userPostPacket.getUser();
			Set<Address> localAddressSet = userPostPacket.getAddressList();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumberForNirvanaXP(httpRequest, auth_token);

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

				if (usernameOfUserSentByClient != null && usernameOfUserSentByClient.trim().length() > 0 && usernameOfAlreadyExistingUser != null && usernameOfAlreadyExistingUser.trim().length() > 0)
				{

					if (usernameOfUserSentByClient.toUpperCase().trim().equals(usernameOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Username already Exists.";
					}

				}

				if (phoneOfUserSentByClient != null && phoneOfUserSentByClient.trim().length() > 0 && phoneOfAlreadyExistingUser != null && phoneOfAlreadyExistingUser.trim().length() > 0)
				{

					if (phoneOfUserSentByClient.trim().equals(phoneOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Phone number already Exists.";
					}
				}
				

				if (emailOfUserSentByClient != null && emailOfUserSentByClient.trim().length() > 0 && emailOfAlreadyExistingUser != null && emailOfAlreadyExistingUser.trim().length() > 0)
				{

					if (emailOfUserSentByClient.trim().equals(emailOfAlreadyExistingUser.trim()))
					{
						errorMessage = "Email already Exists.";
					}

				}
				return new NirvanaServiceErrorResponse(errorMessage, errorMessage, null).toString();
			}

			// user was added and appropriate details were returned
			localuser = userManagementObj.getUser();

			userPostPacket.setUser(localuser);
			userPostPacket.setAddressList(localAddressSet);
		//	insertUserToDiscount(localEM, localuser.getCreatedBy(), code, discountId, localuser.getId());
			
			sendPacketForBroadcast(createPacketForPush(userPostPacket), POSNServiceOperations.UserManagementService_addCustomer.name(), true);

			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}

	private UsersToDiscount insertUserToDiscount(EntityManager em,String createdBy,String code,String discountId,String usersId,String locationId,HttpServletRequest request) throws Exception{
		UsersToDiscount discount =new UsersToDiscount();
		discount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		discount.setCreatedBy(createdBy);
		discount.setUpdatedBy(createdBy);
		discount.setDiscountCode(code);
		discount.setDiscountId(discountId);
		discount.setNumberOfTimeDiscountUsed(0);
		discount.setUsersId(usersId);
		em.getTransaction().begin();
		if(discount.getId()==null)
		discount.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, request, "users_to_discount"));	
		discount = em.merge(discount);
		em.getTransaction().commit();
		return discount;
	}
	/**
	 * @param userPostPacket
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateNirvanaXPUser")
	public String updateNirvanaXPUser(UserPostPacket userPostPacket) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

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
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));

			User user = userPostPacket.getUser();

			if (user == null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
			}

			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumberForNirvanaXP(httpRequest, auth_token);

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

			updateUsersInfoInLocalAndGlobalDb(localEM, globalEM, user);

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			globalUsermanagement.updateAddressInfoInGlobalAndLocalDb(httpRequest, localEM, globalEM, userPostPacket.getAddressList(), user.getId(), user.getGlobalUsersId());

			// Set<Account> accountAssociatedByUserSet = user.getAccountsSet();
			UserPostPacket userPostPacket2 = createPacketForPush(userPostPacket);
			sendPacketForBroadcast(userPostPacket2, POSNServiceOperations.UserManagementService_update.name(), false);

			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
		}

	}

	
	@POST
	@Path("/updateUser")
	public String updateUser(UserPostPacket userPostPacket) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

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
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));

			User user = userPostPacket.getUser();

			if (user == null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
			}

			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();

			updateUsersInfoInLocalAndGlobalDb(localEM, globalEM, user);

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			globalUsermanagement.updateAddressInfoInGlobalAndLocalDb(httpRequest,localEM, globalEM, userPostPacket.getAddressList(), user.getId(), user.getGlobalUsersId());

			// Set<Account> accountAssociatedByUserSet = user.getAccountsSet();
			// sending user info as clients are not getting user info 47055 by ketan
			
			
			UserPostPacket userPostPacket2 = createPacketForPush(userPostPacket);
			sendPacketForBroadcast(userPostPacket2, POSNServiceOperations.UserManagementService_update.name(), true);

			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
		}

	}

	/**
	 * @param localEM
	 * @param globalEM
	 * @param user
	 * @throws NirvanaXPException 
	 */
	private void updateUsersInfoInLocalAndGlobalDb(EntityManager localEM, EntityManager globalEM, User user) throws NirvanaXPException
	{

		String sha512Password = DigestUtils.sha512Hex(user.getPassword().getBytes(Charset.forName("UTF-8")));

		User userInDatabase = (User) new CommonMethods().getObjectById("User", localEM,User.class,  user.getId());
		userInDatabase.setFirstName(user.getFirstName());
		userInDatabase.setLastName(user.getLastName());
		userInDatabase.setDateofbirth(user.getDateofbirth());
		userInDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		userInDatabase.setUpdatedBy(user.getUpdatedBy());
		userInDatabase.setPhone(user.getPhone());
		userInDatabase.setEmail(user.getEmail());
		userInDatabase.setPassword(sha512Password);
		userInDatabase.setCountryId(user.getCountryId());
		userInDatabase.setUsername(user.getUsername());
		
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
		//check whether  phone already assoicated with other number 
		if(userInDatabase!=null && userInDatabase.getPhone()!=null && userInDatabase.getId()!=null){
			 
				String queryString = "select l from User l where l.phone=? and l.id != ? " ;
				TypedQuery<User> query = localEM.createQuery(queryString, User.class).setParameter(1, userInDatabase.getPhone()).setParameter(2, userInDatabase.getId());
				List<User> resultSet=null;
				try {
					resultSet = query.getResultList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.severe(e);
				}
				if(resultSet!=null && resultSet.size()>0){
					throw new NirvanaXPException(new NirvanaServiceErrorResponse("USR10050",
							"Phone is already associated  with other user",
							"Phone is already associated  with other user."));
				}
			 
		}
		//check whether  phone already assoicated with other number
		if(userInDatabase!=null && userInDatabase.getEmail()!=null && userInDatabase.getId()!=null){
		 
				String queryString = "select l from User l where l.email=? and l.id != ? " ;
				TypedQuery<User> query = localEM.createQuery(queryString, User.class).setParameter(1, userInDatabase.getEmail()).setParameter(2, userInDatabase.getId());
				List<User> resultSet=null;
				try {
					resultSet = query.getResultList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.severe(e);
				}
				if(resultSet!=null && resultSet.size()>0){
					throw new NirvanaXPException(new NirvanaServiceErrorResponse("USR10050",
							"Email is already associated  with other user",
							"Email is already associated  with other user."));
				}
			
		}
		
		EntityTransaction tx = localEM.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			localEM.merge(userInDatabase);
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
		// todo - need to modulize this code
		com.nirvanaxp.global.types.entities.User userInGBDatabase = (com.nirvanaxp.global.types.entities.User) new CommonMethods().getObjectById("com.nirvanaxp.global.types.entities.User", globalEM,com.nirvanaxp.global.types.entities.User.class, user.getGlobalUsersId());

		userInGBDatabase.setFirstName(user.getFirstName());
		userInGBDatabase.setLastName(user.getLastName());
		userInGBDatabase.setDateofbirth(user.getDateofbirth());
		userInGBDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		userInGBDatabase.setUpdatedBy(user.getUpdatedBy());
		userInGBDatabase.setPhone(user.getPhone());
		userInGBDatabase.setEmail(user.getEmail());
		userInGBDatabase.setPassword(sha512Password);
		userInGBDatabase.setCountryId(user.getCountryId());
		// set email to null, if blank or empty string is passed, otherwise user
		// will not be added due to unique constraint
		if (userInGBDatabase.getEmail() != null && userInGBDatabase.getEmail().trim().length() == 0)
		{
			userInGBDatabase.setEmail(null);
		}

		// set phone to null, if blank or empty string is passed, otherwise user
		// will not be added due to unique constraint
		if (userInGBDatabase.getPhone() != null && userInGBDatabase.getPhone().trim().length() == 0)
		{
			userInGBDatabase.setPhone(null);
		}

		// null auth pin must go not blank
		if (userInGBDatabase.getAuthPin() != null && userInGBDatabase.getAuthPin().trim().length() == 0)
		{
			userInGBDatabase.setAuthPin(null);
		}

		EntityTransaction txGlobal = globalEM.getTransaction();
		try
		{
			// start transaction
			txGlobal.begin();
			globalEM.merge(userInGBDatabase);
			txGlobal.commit();
		}
		catch (RuntimeException e)
		{
			// on error, if transaction active, rollback
			if (txGlobal != null && txGlobal.isActive())
			{
				txGlobal.rollback();
			}
			throw e;
		}

		user = userInDatabase;
	}

	/**
	 * 
	 *
	 * @param globalUsersId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getNirvanaXpUserByGlobalUsersId/{globalUsersId}")
	public String getNirvanaXpUserByGlobalUsersId(@PathParam("globalUsersId") String globalUsersId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumberForNirvanaXP(httpRequest, auth_token);

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
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getUserByGlobalUsersId/{globalUsersId}")
	public String getUserByGlobalUsersId(@PathParam("globalUsersId") String globalUsersId) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

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
	 * @param id 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getUserById/{id}")
	public String getUserById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

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
	 * @param userPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/update")
	public String update(UserPostPacket userPacket) throws Exception
	{
		User user = null;

		EntityManager localEM = null;
		EntityManager globalEM = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPacket, "UserPostPacket",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest,userPacket.getLocationId(), Integer.parseInt(userPacket.getMerchantId()));

			user = userPacket.getUser();

			if (user == null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
			}

			// Set<Address> addressList = userPacket.getAddressList();
			// if (addressList != null && addressList.size() > 0) {
			// user.setAddressSet(addressList);
			// }

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			updateUsersInfoInLocalDb(localEM, user);

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			globalUsermanagement.updateAddressInfoInGlobalAndLocalDb(httpRequest,localEM, globalEM, userPacket.getAddressList(), user.getId(), user.getGlobalUsersId());

			// Set<Account> accountAssociatedByUserSet = user.getAccountsSet();
			UserPostPacket userPostPacket2 = createPacketForPush(userPacket);
			sendPacketForBroadcast(userPostPacket2, POSNServiceOperations.UserManagementService_update.name(), false);

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
	private void updateUsersInfoInLocalDb(EntityManager em, User user)
	{

		User userInDatabase = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());
		userInDatabase.setFirstName(user.getFirstName());
		userInDatabase.setLastName(user.getLastName());
		userInDatabase.setDateofbirth(user.getDateofbirth());
		userInDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		userInDatabase.setUpdatedBy(user.getUpdatedBy());
		userInDatabase.setPhone(user.getPhone());
		userInDatabase.setEmail(user.getEmail());

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

		user = userInDatabase;
	}

	/**
	 * 
	 *
	 * @param addressId 
	 * @param userId 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/deleteAddress/{addressId}/{userId}")
	public String delete(@PathParam("addressId") String addressId, @PathParam("userId") String userId) throws Exception
	{

		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{

			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			// removing address from db
			Address address = (Address) new CommonMethods().getObjectById("Address", em,Address.class, addressId);

			if (address != null)
			{

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

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	/**
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
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{

			em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);

			com.nirvanaxp.global.types.entities.User users = new UserManagementServiceBean(httpRequest, em).getGlobleUserByEmail(username);
			return new JSONUtility(httpRequest).convertToJsonString(users);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
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
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			com.nirvanaxp.global.types.entities.User user = new UserManagementServiceBean(httpRequest, em).getGlobleUserByPhoneNo(phoneNumber);

			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(user);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * 
	 *
	 * @param userPostPacket 
	 * @return 
	 * @throws Exception 
	 */
	@POST
	@Path("/updateGlobalUser")
	public String updateGlobalUser(UserPostPacketForGlobalUser userPostPacket) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		if (userPostPacket.getLocationId() == null || userPostPacket.getLocationId().length() == 0)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LOCATION_CANNOT_BE_BLANK_EXCEPTION, MessageConstants.ERROR_MESSAGE_LOCATION_CANNOT_BE_BLANK_DISPLAY_MESSAGE, null)
					.toString();
		}

		if (userPostPacket.getUser() == null)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
		}
		EntityManager globalEM = null;
		String json = null;
		try
		{
			// create json packet for store forward
			json = new StoreForwardUtility().returnJsonPacket(userPostPacket, "UserPostPacketForGlobalUser",httpRequest);
			// call synchPacket for store forward
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, userPostPacket.getLocationId(), Integer.parseInt(userPostPacket.getMerchantId()));

			com.nirvanaxp.global.types.entities.User user = userPostPacket.getUser();

			if (user == null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION, MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE, null).toString();
			}

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			// user=updateUsersInfoInGlobalDb(globalEM, user);
			// encrypting the password
			String sha512Password = DigestUtils.sha512Hex(user.getPassword().getBytes(Charset.forName("UTF-8")));
			// fetching existing user from database
			com.nirvanaxp.global.types.entities.User userInGBDatabase = (com.nirvanaxp.global.types.entities.User) new CommonMethods().getObjectById("com.nirvanaxp.global.types.entities.User", globalEM,com.nirvanaxp.global.types.entities.User.class, user.getId());

			if (user.getEmail() != null)
			{
				com.nirvanaxp.global.types.entities.User userFromDB = getUserByEmail(globalEM, user.getEmail(), user.getId());
				if (userFromDB == null)
				{
					userInGBDatabase.setEmail(user.getEmail());
				}
				else
				{
					return new NirvanaServiceErrorResponse("Email already Exists.", "Email already Exists.", null).toString();

				}
			}
			if (user.getPhone() != null)
			{
				com.nirvanaxp.global.types.entities.User userFromDB = getUserByPhoneNo(globalEM, user.getPhone(), user.getId());
				if (userFromDB == null)
				{
					userInGBDatabase.setPhone(user.getPhone());
				}
				else
				{
					return new NirvanaServiceErrorResponse("Phone number already Exists.", "Phone number already Exists.", null).toString();

				}
			}
			if (sha512Password != null)
			{
				userInGBDatabase.setPassword(sha512Password);
			}
			if (user.getUsername() != null)
			{
				com.nirvanaxp.global.types.entities.User userFromDB = getUserByUserName(globalEM, user.getUsername(), user.getId());
				if (userFromDB == null)
				{
					userInGBDatabase.setUsername(user.getUsername());
				}
				else
				{

					return new NirvanaServiceErrorResponse("User Name already Exists.", "User Name already Exists.", null).toString();
				}
			}
			// set email to null, if blank or empty string is passed, otherwise
			// user
			// will not be added due to unique constraint
			if (userInGBDatabase.getEmail() != null && userInGBDatabase.getEmail().trim().length() == 0)
			{
				userInGBDatabase.setEmail(null);
			}
			// set phone to null, if blank or empty string is passed, otherwise
			// user
			// will not be added due to unique constraint
			if (userInGBDatabase.getPhone() != null && userInGBDatabase.getPhone().trim().length() == 0)
			{
				userInGBDatabase.setPhone(null);
			}
			EntityTransaction txGlobal = globalEM.getTransaction();
			try
			{
				// start transaction
				txGlobal.begin();
				globalEM.merge(userInGBDatabase);
				txGlobal.commit();
			}
			catch (RuntimeException e)
			{
				// on error, if transaction active, rollback
				if (txGlobal != null && txGlobal.isActive())
				{
					txGlobal.rollback();
				}
				throw e;
			}

			userPostPacket.setUser(user);
			;
			sendPacketForBroadcastForGlobleUser(userPostPacket, POSNServiceOperations.UserManagementService_update.name(), false);

			return new JSONUtility(httpRequest).convertToJsonString(userPostPacket);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}

	}

	/**
	 * 
	 *
	 * @param userPostPacket 
	 * @param operationName 
	 * @param shouldBroadcastObj 
	 */
	private void sendPacketForBroadcastForGlobleUser(UserPostPacketForGlobalUser userPostPacket, String operationName, boolean shouldBroadcastObj)
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
	 * @param em 
	 * @param phoneNo 
	 * @param id 
	 * @return 
	 */
	public com.nirvanaxp.global.types.entities.User getUserByPhoneNo(EntityManager em, String phoneNo, String id)
	{
		// todo - ap - need to handle exception
		try
		{
			String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId " + "and r.roleName=? and u.phone=? and u.id!=?";
			TypedQuery<com.nirvanaxp.global.types.entities.User> query = em.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class)
					.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, phoneNo).setParameter(3, id);
			com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
			return result;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}

	/**
	 * 
	 *
	 * @param em 
	 * @param email 
	 * @param id 
	 * @return 
	 */
	public com.nirvanaxp.global.types.entities.User getUserByEmail(EntityManager em, String email, String id)
	{
		//todo apoorv how to handle exception
		try
		{
			String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId " + "and r.roleName=? and u.email like ?  and u.id!=?";
			TypedQuery<com.nirvanaxp.global.types.entities.User> query = em.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class)
					.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, email).setParameter(3, id);
			com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
			return result;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}

	/**
	 * 
	 *
	 * @param em 
	 * @param userName 
	 * @param id 
	 * @return 
	 */
	public com.nirvanaxp.global.types.entities.User getUserByUserName(EntityManager em, String userName, String id)
	{
		//todo apoorv how to handle exception
		try
		{
			String queryString = "SELECT u FROM User u where " + " u.username like ?  and u.id!=?";
			TypedQuery<com.nirvanaxp.global.types.entities.User> query = em.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class).setParameter(1, userName).setParameter(2, id);
			com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
			return result;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}
	
	

	/**
	 * 
	 *
	 * @param phoneNumber 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getLocalUserByPhoneNumber/{phoneNumber}")
	public String getUserByPhoneNumber(@PathParam("phoneNumber") String phoneNumber) throws Exception
	{
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);

		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
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
	 * @param username 
	 * @return 
	 * @throws Exception 
	 */
	@GET
	@Path("/getLocalUserByEmail/{email}")
	public String getUserByEmail(@PathParam("email") String username) throws Exception
	{
		EntityManager em = null;
		String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			User users = new UserManagementServiceBean(httpRequest, em).getUserByEmail(username);
			return new JSONUtility(httpRequest).convertToJsonString(users);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
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
			new StoreForwardUtility().callSynchPacketsWithServer(json, httpRequest, syncUserPacket.getLocationId(), Integer.parseInt(syncUserPacket.getMerchantId()));
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

	private static AmazonSNS getSNS() throws IOException {
		String awsAccessKey = System.getProperty("AWS_ACCESS_KEY_ID"); // "YOUR_AWS_ACCESS_KEY";
		String awsSecretKey = System.getProperty("AWS_SECRET_KEY"); // "YOUR_AWS_SECRET_KEY";

		if (awsAccessKey == null)
			awsAccessKey = "AKIAJ4FQSALKK4XTOZOQ";
		if (awsSecretKey == null)
			awsSecretKey = "qLOsioPoWtJWp+EldQMAN2FUXkQiiMv5P0PqpEET";

		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey,
				awsSecretKey);
		AmazonSNS sns = new AmazonSNSClient(credentials);

		sns.setEndpoint("https://sns.us-west-2.amazonaws.com");
		return sns;
	}
	
	
	
	@GET
	@Path("/sendSNSByNumber/{phoneNumber}")
	public String sendSNSByNumber(@PathParam("phoneNumber") String phoneNumber)
			throws NirvanaXPException, IOException, InvalidSessionException {
		
		return sendSNSByNumberdd(phoneNumber);
	}
	public String sendSNSByNumberdd(String phoneNumber)
			throws NirvanaXPException, IOException, InvalidSessionException {
		PublishResult result = null;
		try {
			
			AmazonSNS snsClient = getSNS();//AmazonSNSClientBuilder.defaultClient();//
			//snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
			String message = "Hi Sharath Please Send Screen Shot this msg -NXP Shlok Pardeshi";
	        
	        Map<String, MessageAttributeValue> smsAttributes = 
	                new java.util.HashMap<String, MessageAttributeValue>();
	        
	        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
            .withStringValue("Transactional") //Sets the type to promotional.
            .withDataType("String"));
	        
	        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
            .withStringValue("NXP") //The sender ID shown on the device.
            .withDataType("String"));
	        
	        result = snsClient.publish(new PublishRequest()
	                            .withMessage(message)
	                            .withPhoneNumber(phoneNumber)
	                            .withMessageAttributes(smsAttributes));
	        System.out.println(result); // Prints the message ID.
			

		} catch (Exception e) {
			
			logger.severe(e);
		} 
		return result.getMessageId();
	}
	
	
	public boolean sendSNSByNumber(User user, String locationId, String auth_token, boolean isCheckExistingUTD, int needToSendSNS)   {
		
		try {
			if(needToSendSNS!=1){
			String message = getMessageBodyToSend( user,locationId,auth_token,isCheckExistingUTD);
		        if(message != null)
		        {
		        	sendAmazonSMS(user, message);
		        	return true;
		        }else
		        {
		        	return false;
		        }
		        
		       
		        
			}
		} catch (Exception e) {
			logger.severe(e);
		} 
		return false;
		
		//em.getTransaction().commit();
	}
	
	
	private void sendAmazonSMS(User user, String message)

	{
		try {
			
			/*AmazonSNS snsClient = getSNS();
	        String phoneNumber = user.getPhone();
	       // snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
	        
	        logger.severe("----------------------------------------------- 5 ");
	        
			Map<String, MessageAttributeValue> smsAttributes = 
	                new java.util.HashMap<String, MessageAttributeValue>();
	        
	        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
	        .withStringValue("Transactional") //Sets the type to promotional.
	        .withDataType("String"));
	        
	        
	        logger.severe("----------------------------------------------- 5 1");
	        
	        
	        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
	        .withStringValue("mySenderID") //The sender ID shown on the device.
	        .withDataType("String"));
	        
	        logger.severe("----------------------------------------------- 6 phoneNumber  " + phoneNumber);
	        
	        
	        PublishResult result = snsClient.publish(new PublishRequest()
	                            .withMessage(message)
	                            .withPhoneNumber(phoneNumber)
	                            .withMessageAttributes(smsAttributes));
	        logger.severe("----------------------------------------------- SMS result " + result); // Prints the message ID.
			*/
			PublishResult result = null;
				
				AmazonSNS snsClient = getSNS();//AmazonSNSClientBuilder.defaultClient();//
				//AmazonSNS snsClient = AmazonSNSClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
				//snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
			    Map<String, MessageAttributeValue> smsAttributes = 
		                new java.util.HashMap<String, MessageAttributeValue>();
		        
		        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
	            .withStringValue("Transactional") //Sets the type to promotional.
	            .withDataType("String"));
		        
		        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
	            .withStringValue("NXP") //The sender ID shown on the device.
	            .withDataType("String"));
		        
		        result = snsClient.publish(new PublishRequest()
		                            .withMessage(message)
		                            .withPhoneNumber(user.getPhone())
		                            .withMessageAttributes(smsAttributes));
		    } catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}
		
	}
	
	@SuppressWarnings("unused")
	private String getMessageBodyToSend(User user, String locationId, String auth_token,boolean isCheckExistingUTD) throws FileNotFoundException, IOException
	{
 
		String retrnMsg = null;
		EntityManager em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
		em.getTransaction().begin();
		try {
			
			
			LocationSetting locationSetting = new CommonMethods().getAllLocationSettingByLocationId(em, locationId);
			
			Location location = (Location) new CommonMethods().getObjectById("Location", em,Location.class, locationId);;
			
			
			if(locationSetting.getIsSignupSms() == 1)
			{
				String queryStringT = "select ci from SMSTemplate ci where  ci.status not in ('I','D') "
						+ " and ci.id =  " + locationSetting.getTemplateId() ;
				TypedQuery<SMSTemplate> queryT = em.createQuery(queryStringT, SMSTemplate.class);
				SMSTemplate snsSmsTemplate =  queryT.getSingleResult();
				
				
				List<Discount> discount = new ArrayList<Discount>();
				try
				{
					
					String queryStringD = "select ci from Discount ci where  ci.status not in ('I','D') "
							+ " and locationsId="+locationId+" and ci.isCoupan = 1 and  ci.smsTemplateId = " + snsSmsTemplate.getId();
					TypedQuery<Discount> queryD = em.createQuery(queryStringD, Discount.class);
					discount =  queryD.getResultList();
					
				}catch (Exception e)
				{
					logger.severe("No Entity Find for Discount For Template id " +  snsSmsTemplate.getId());
				}
				
				
				String queryStringConfig = "select ci from SmsConfig ci where  ci.status !='D'and "
						+ "ci.gatewayName  = 'AWS SMS'";
				TypedQuery<SmsConfig> query = em.createQuery(queryStringConfig, SmsConfig.class);
				SmsConfig smsConfig =  query.getSingleResult();
		         
				if(snsSmsTemplate == null)
				{
					logger.severe("Sns Sms Template Not Configure");
					return null;
					
				}else if(smsConfig == null)
				 {
					 logger.severe("Sns Sms Config Not Configure");
					 return null; 
					 
				 }else 
				 {
					 
					
					
					String coupanCode = "";
					String code = "";
					if(discount != null && !discount.isEmpty())
					{
						retrnMsg = snsSmsTemplate.getTemplateText() ;
						coupanCode = "Please use ";
						if(discount.get(0).getIsAutoGenerated() == 1)
						{
							//Auto Generated Coupan Code
							code = AuthCodeGenerator.generateAuthCode();//(100000 + new Random().nextInt(900000)) +"";
							coupanCode = coupanCode + code + "";
							
							
						}else
						{
							code = discount.get(0).getCoupanCode();
							coupanCode = coupanCode + code;
						}	
						
						coupanCode = coupanCode + " coupon code for discount.";
						
						if(discount.get(0).getNumberOfTimeDiscountUsed() == -1)
						{
							retrnMsg = retrnMsg.replace("<NumberOfTimeDiscountUsed>",  "Unlimited");
						}else
						{
							retrnMsg = retrnMsg.replace("<NumberOfTimeDiscountUsed>", discount.get(0).getNumberOfTimeDiscountUsed() + "");	
						}
						retrnMsg =  retrnMsg + " " + coupanCode ;
						
						retrnMsg = retrnMsg.replace("<BusinessName>", location.getName());
					}
					
					
				
					
					if(discount != null && !discount.isEmpty())
					{
						if(isCheckExistingUTD)
						{
							//Task no. #45988, modified by :- SK, comments- Send Discount SMS code while user exist but first time login through app
							try {
								CriteriaBuilder builder = em.getCriteriaBuilder();
								CriteriaQuery<UsersToDiscount> criteria = builder.createQuery(UsersToDiscount.class);
								Root<UsersToDiscount> r = criteria.from(UsersToDiscount.class);
								
								TypedQuery<UsersToDiscount> queryUTD = null;
								queryUTD = em.createQuery(criteria.select(r).where
										(builder.equal(r.get(UsersToDiscount_.usersId), user.getId()),
										/*builder.equal(builder.lower(r.get(UsersToDiscount_.discountCode)), discount.get(0)
												.getCoupanCode().toLowerCase()),*/
										builder.equal(r.get(UsersToDiscount_.locationId), locationId),
										builder.equal(r.get(UsersToDiscount_.discountId), discount.get(0).getId())));
								
								UsersToDiscount usersToDiscount = queryUTD.getSingleResult();
								
								if(usersToDiscount != null)
								{
									return null;
								}
								
							} catch (Exception e) {
								// TODO: handle exception
								logger.severe(e);
							}
						}
						
						
						
						UsersToDiscount usersToDiscount = new UsersToDiscount();
				        usersToDiscount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				        usersToDiscount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				        usersToDiscount.setCreatedBy(user.getId());
				        usersToDiscount.setUpdatedBy(user.getId());
				        usersToDiscount.setDiscountId(discount.get(0).getId());	
				        usersToDiscount.setNumberOfTimeDiscountUsed(0);
				        if(code != null)
				        {
				        	usersToDiscount.setDiscountCode(code);	
				        }
				        
				        usersToDiscount.setUsersId(user.getId());
				        usersToDiscount.setLocationId(locationId);
				    	if(usersToDiscount.getId()==null)
				        usersToDiscount.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, httpRequest, "users_to_discount"));	
				        usersToDiscount = em.merge(usersToDiscount);
			        
					}
			        
			        new InsertIntoHistory().insertSMSIntoHistory(em, user, snsSmsTemplate, retrnMsg, user.getPhone(), smsConfig, null,
			        		locationId);
			        
				 }
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.severe(e);
		}
		
		em.getTransaction().commit();
		
		return retrnMsg;
        
	}
	
	@GET
	@Path("/sendSMSForVerificationCode/{phoneNumber}/{locationId}")
	public boolean sendSMSForVerificationCode(@PathParam("phoneNumber") String phoneNumber,
			@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager localEM = null;
		EntityManager globalEM = null;
		try
		{

			localEM = LocalSchemaEntityManager.getInstance().getEntityManagerForNirvanaXP();

			globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager();
			
			com.nirvanaxp.global.types.entities.User user = new UserManagementServiceBean(httpRequest, globalEM).getGlobleUserByPhoneNo(phoneNumber); 
			
			com.nirvanaxp.types.entities.user.User lUSer = new com.nirvanaxp.types.entities.user.User();
			lUSer.setId(user.getId());
			lUSer.setPhone(user.getPhone());
			
			
			sendSMSForVerificationCode(lUSer,locationId, localEM,globalEM);
			
			return true;

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(localEM);
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
		}
	}
	
	public void sendSMSForVerificationCode(com.nirvanaxp.types.entities.user.User user, String locationId, EntityManager localEM,
			EntityManager globalEM)   {
		
		try {
			
			String message = getMessageBodyToSendForVerificationCode( user,locationId,localEM, globalEM);
		        
	        if(message != null)
	        {
	        	sendAmazonSMS(user, message);
	        	
	        }

		} catch (Exception e) {
			e.printStackTrace();
			logger.severe(e);
		} 
		
	}
	
	
	@SuppressWarnings("unused")
	private String getMessageBodyToSendForVerificationCode(com.nirvanaxp.types.entities.user.User user, String locationId,
			EntityManager localEm, EntityManager globalEM) throws FileNotFoundException, IOException
	{
		String retrnMsg = null;
		localEm.getTransaction().begin();
		globalEM.getTransaction().begin();
		try {
			
			
				String queryStringConfig = "select ci from SmsConfig ci where  ci.status !='D'and "
						+ "ci.gatewayName  = 'AWS SMS'";
				TypedQuery<SmsConfig> query = localEm.createQuery(queryStringConfig, SmsConfig.class);
				SmsConfig smsConfig =  query.getSingleResult();
		         
				if(smsConfig == null)
				 {
					 logger.severe("Sns Sms Config Not Configure");
					 return null; 
					 
				 }else 
				 {
					
					 try{
						 
						 String queryString = "select l from UserAuth l where " + "l.userId =" + user.getId();
							TypedQuery<UserAuth> query1 = globalEM.createQuery(queryString, UserAuth.class);
							List<UserAuth> userAuth = query1.getResultList();
							
							for(UserAuth userA : userAuth)
							{
								userA.setStatus("D");
								globalEM.merge(userA);
							}
							
					 } catch (Exception e) {
							// TODO: handle exception
							logger.severe(e);
						}
					 
					 	UserAuth userAuth = new UserAuth();
						String verificationCode = AuthCodeGenerator.generateAuthCode();

						if (verificationCode == null || verificationCode.length() == 0)
						{
							// unable to generate verification code
							throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_GENERATE_VERIFICATION_CODE,
									MessageConstants.ERROR_MESSAGE_UNABLE_TO_GENERATE_VERIFICATION_CODE, null));
						}

						String sql = "update user_auth set status='D' where user_id= ?  and status='A' ";
						localEm.createNativeQuery(sql).setParameter(1, user.getId());

						userAuth.setUserId(user.getId());
						userAuth.setAuthCode(verificationCode);
						userAuth.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						userAuth.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						userAuth.setCreatedBy(user.getId());
						userAuth.setUpdatedBy(user.getId());
						userAuth.setStatus("A");
						globalEM.persist(userAuth);
					
					retrnMsg = "Dear Customer, " + verificationCode + " is your verification code. Thank you." ;
					
				    new InsertIntoHistory().insertSMSIntoHistory(localEm, user, null, retrnMsg, user.getPhone(), smsConfig, null,
			        		locationId);
			        
				 }
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.severe(e);
		}
		
		localEm.getTransaction().commit();
		globalEM.getTransaction().commit();
		
        return retrnMsg;
        
	}
	@GET
	@Path("/getUserByUserName/{username}")
	public String getUserByUserName(@PathParam("username") String username) throws Exception
	{
		EntityManager em = null;
		try
		{
			String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
			
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			com.nirvanaxp.global.types.entities.User user = getUserByUserName(em, username);
			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	public com.nirvanaxp.global.types.entities.User getUserByUserName(EntityManager em, String userName)
	{
		//todo apoorv how to handle exception
		try
		{
			String queryString = "SELECT u FROM User u where " + " u.username = '"+ userName +"'";
			TypedQuery<com.nirvanaxp.global.types.entities.User> query = em.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class);
			com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
			return result;
		}
		catch (Exception e)
		{

			logger.severe(e);
		}
		return null;
	}
	
	@GET
	@Path("/getUserToDiscountForFirstTimeSignup/{userId}/{locationId}")
	public boolean getUserToDiscountForFirstTimeSignup(@PathParam("userId") String userId,
			@PathParam("locationId") String locationId) throws Exception
	{
		EntityManager em = null;
		try
		{
			String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
			
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			EntityManager emLocal = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			
			//Send SNS SMS
			return sendSNSByNumber(emLocal.find(User.class, userId),locationId , auth_token,true,0);
			
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	@GET
	@Path("/getUserWithAddressById/{id}")
	public String getUserWithAddressById(@PathParam("id") String id) throws Exception
	{
		EntityManager em = null;
		try
		{
			String auth_token = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
			 em = LocalSchemaEntityManager.getInstance().getEntityManagerUsingReferenceNumber(httpRequest, auth_token);
			

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
	
 
}
