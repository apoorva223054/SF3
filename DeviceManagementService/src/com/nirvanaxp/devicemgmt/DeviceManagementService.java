/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.devicemgmt;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
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

import com.nirvanaxp.server.common.MessageSender;
import com.nirvanaxp.server.context.UserSessionInfo;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.server.util.ServiceOperationsUtility;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;
import com.nirvanaxp.services.jaxrs.packets.DeviceToRegisterPacket;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.device.DeviceToRegister;
import com.nirvanaxp.websocket.protocol.POSNServiceOperations;
import com.nirvanaxp.websocket.protocol.POSNServices;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceManagementService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class DeviceManagementService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private static final NirvanaLogger logger = new NirvanaLogger(DeviceManagementService.class.getName());

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	/**
	 * Gets the all logged in devices list for account id.
	 *
	 * @param sessionId the session id
	 * @param deviceId the device id
	 * @param accountId the account id
	 * @return the all logged in devices list for account id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForDeviceIdAndAccountId/{deviceId}/{accountId}")
	public String getAllLoggedInDevicesListForAccountId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("deviceId") String deviceId, @PathParam("accountId") int accountId) throws Exception
	{
		 EntityManager em = null;
			try
			{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			String queryString = "select temp.session_id from(select us.session_id,us.merchant_id,us.login_time,us.user_id from user_session  us  LEFT JOIN device_info deviceinfo on "
					+ " us.device_info_id = deviceinfo.id where deviceinfo.device_id = ? and us.merchant_id = ? order by us.login_time desc ) as temp group by temp.merchant_id";
			
			 return (String) em.createNativeQuery(queryString).setParameter(1, deviceId).setParameter(2, accountId)
					.getSingleResult();
		 
			
			
		}catch (Exception e) {
			return "No active sessionId found in database for this device. You must log in to get session id.";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the active user session for android and IOS and windows for account id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @return the active user session for android and IOS and windows for account id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForMobileWithAdminUserForAccountId/{accountId}")
	public String getActiveUserSessionForAndroidAndIOSAndWindowsForAccountId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId) throws Exception
	{

		 EntityManager em = null;
			try
			{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = ? and r.role_name != 'POS Customer' and (di.device_type_id =1 or di.device_type_id =  2 "
					+ " or di.device_type_id = 3)";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the active user session for for account id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @return the active user session for for account id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForForAccountId/{accountId}")
	public String getActiveUserSessionForForAccountId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId) throws Exception
	{

		 EntityManager em = null;
			try
			{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = ?";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the active user session for admin user for account id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @return the active user session for admin user for account id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForAdminUserForAccountId/{accountId}")
	public String getActiveUserSessionForAdminUserForAccountId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId) throws Exception
	{

		 EntityManager em = null;
			try
			{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = ? and r.role_name != 'POS Customer'";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the active user session for customers for account id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @return the active user session for customers for account id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForCustomersForAccountId/{accountId}")
	public String getActiveUserSessionForCustomersForAccountId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId) throws Exception
	{

		 EntityManager em = null;
			try
			{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = ? and r.role_name = 'POS Customer'";
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the active user session admin user for bussiness id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @param bussinessId the bussiness id
	 * @return the active user session admin user for bussiness id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForAdminUserForAccountIdAndBussinessId/{accountId}/{bussinessId}")
	public String getActiveUserSessionAdminUserForBussinessId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId, @PathParam("bussinessId") int bussinessId)
			throws Exception
	{

		 EntityManager em = null;
			try
			{
				em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = ? and r.role_name != 'POS Customer' and utb.business_id=?" ;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId).setParameter(2, bussinessId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the active user session for mobile devices with admin user for bussiness id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @param bussinessId the bussiness id
	 * @return the active user session for mobile devices with admin user for bussiness id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForMobileWithAdminUserForBussinessId/{accountId}/{bussinessId}")
	public String getActiveUserSessionForMobileDevicesWithAdminUserForBussinessId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId,
			@PathParam("bussinessId") int bussinessId) throws Exception
	{

	 EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = " + accountId + " and r.role_name != 'POS Customer' and (di.device_type_id =1 or di.device_type_id =  2 "
					+ " or di.device_type_id = 3) and utb.business_id=" + bussinessId;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId).setParameter(2, bussinessId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the active user session for for account id and bussiness id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @param bussinessId the bussiness id
	 * @return the active user session for for account id and bussiness id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForForAccountIdAndBussinessId/{accountId}/{bussinessId}")
	public String getActiveUserSessionForForAccountIdAndBussinessId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId, @PathParam("bussinessId") int bussinessId)
			throws Exception
	{

	 EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = " + accountId + " and  utb.business_id=" + bussinessId;
			;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId).setParameter(2, bussinessId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the active user session for customers for account id and bussiness id.
	 *
	 * @param sessionId the session id
	 * @param accountId the account id
	 * @param bussinessId the bussiness id
	 * @return the active user session for customers for account id and bussiness id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getActiveUserSessionForCustomersForAccountIdAndBussinessId/{accountId}/{bussinessId}")
	public String getActiveUserSessionForCustomersForAccountIdAndBussinessId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("accountId") int accountId,
			@PathParam("bussinessId") int bussinessId) throws Exception
	{

		EntityManager em =null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<UserSessionInfo> userSessionInfosList = new ArrayList<UserSessionInfo>();
			String queryString = "select us.id,us.schema_name,us.session_id,us.merchant_id,us.user_id,"
					+ "us.users_roles_id,us.device_info_id,us.login_time,us.logout_time,r.id,r.role_name,r.display_name,"
					+ "di.id,di.device_id,dt.id,dt.name,dt.display_name,u.id,u.username,u.first_name,u.last_name,u.phone,u.email" + " from user_session  us LEFT JOIN roles r on "
					+ " us.users_roles_id = r.id LEFT JOIN device_info di on "
					+ " us.device_info_id = di.id LEFT JOIN device_type dt on di.device_type_id = dt.id LEFT JOIN users u on us.user_id = u.id LEFT JOIN users_to_business utb on "
					+ " us.user_id = utb.users_id where us.merchant_id = ? and r.role_name = 'POS Customer' and utb.business_id=? "  ;
			@SuppressWarnings("unchecked")
			List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, accountId).setParameter(2, bussinessId)
					.getResultList();
		 
			for (Object[] objRow : resultList)
			{
				try
				{
					// if this has primary key not 0
					UserSessionInfo userSessionInfos = new UserSessionInfo();
					userSessionInfos.initilizeVariablesUsingResulSet(objRow);
					userSessionInfosList.add(userSessionInfos);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonStringViaEliminatingNullValues(userSessionInfosList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	 

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
	 * Adds the employee operation.
	 *
	 * @param deviceToRegisterPacket the device to register packet
	 * @param sessionId the session id
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/addDeviceToRegister")
	public String addEmployeeOperation(DeviceToRegisterPacket deviceToRegisterPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		EntityTransaction tx =null;
		try
		{	
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId, deviceToRegisterPacket);
			tx = em.getTransaction();
			tx.begin();
			List<DeviceToRegister> deviceToRegisters = new DeviceManagementHelper().addDeviceToRegister(em, deviceToRegisterPacket);
			tx.commit();
			deviceToRegisterPacket.setDeviceToRegisters(deviceToRegisters);
			sendPacketForBroadcast(POSNServiceOperations.DeviceManagementService_addUpdate.name(), deviceToRegisterPacket);
			return new JSONUtility(httpRequest).convertToJsonString(deviceToRegisters);
		}catch (RuntimeException e)
		{
			// on error, if transaction active,
			// rollback
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
	 * Gets the device to register by location id.
	 *
	 * @param locationId the location id
	 * @param deviceId the device id
	 * @param sessionId the session id
	 * @return the device to register by location id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getDeviceToRegisterByLocationId/{locationId}/{deviceId}")
	public String getDeviceToRegisterByLocationId(@PathParam("locationId") String locationId,@PathParam("deviceId") String deviceId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			List<DeviceToRegister> deviceToRegisters = new DeviceManagementHelper().getDeviceToRegisterByLocationId(em, httpRequest, locationId, deviceId);
			return new JSONUtility(httpRequest).convertToJsonString(deviceToRegisters);

		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Send packet for broadcast.
	 *
	 * @param operation the operation
	 * @param postPacket the post packet
	 */
	private void sendPacketForBroadcast(String operation, PostPacket postPacket)
	{
		try
		{
			operation = ServiceOperationsUtility.getOperationName(operation);
			MessageSender messageSender = new MessageSender();

			messageSender.sendMessage(httpRequest, postPacket.getClientId(), POSNServices.DeviceManagementService.name(),
					operation, null, postPacket.getMerchantId(), postPacket.getLocationId(),
					postPacket.getEchoString(), postPacket.getSchemaName());

		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, e.getMessage());
		}
	}

	

}
