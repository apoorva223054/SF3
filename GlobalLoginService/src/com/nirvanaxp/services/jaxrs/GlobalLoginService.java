/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
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
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.UserSessionHistory;
import com.nirvanaxp.global.types.entities.UserSessionHistory_;
import com.nirvanaxp.global.types.entities.UserSession_;
import com.nirvanaxp.global.types.entities.accounts.AccountToServerConfig;
import com.nirvanaxp.global.types.entities.accounts.ServerConfig;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToBusiness;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToBusiness_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.authentication.LoginPacket;
import com.nirvanaxp.services.authentication.POSNLoginModule;
import com.nirvanaxp.services.authentication.POSNirvanaUser;
import com.nirvanaxp.services.exceptions.POSExceptionMessage;
import com.nirvanaxp.services.exceptions.POSNirvanaLoginException;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class GlobalLoginService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(GlobalLoginService.class.getName());

	/**
	 * example params
	 * <ul>
	 * <li>username=nirvana</li>
	 * <li>password=nirvanac</li>
	 * <li>deviceId=674e274dc1857313</li>
	 * <li>deviceTypeId=2</li>
	 * <li>deviceName=Nexus 7</li>
	 * <li>ipAddress=192.168.2.37</li>
	 * <li>rolesName=POS Operator</li>
	 * <li>versionInfo=v1.6.42</li>
	 * </ul>
	 * @param username
	 * @param password
	 * @param appType
	 * @param deviceId
	 * @param deviceTypeId
	 * @param deviceName
	 * @param versionInfo
	 * @return
	 * @throws POSNirvanaLoginException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DatabaseException
	 */
	@POST
	@Path("/login")
	public Response login(@FormParam("username") String username, @FormParam("password") String password,
			@FormParam("rolesName") String appType, @FormParam("deviceId") String deviceId,
			@FormParam("deviceTypeId") int deviceTypeId,
			@FormParam("deviceName") String deviceName, 
			@FormParam("versionInfo") String versionInfo, 
			@FormParam("businessId") int businessId, 
			@FormParam("scope") String scope) throws POSNirvanaLoginException,
			FileNotFoundException, IOException
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			POSNLoginModule posNirvanaLoginModule = new POSNLoginModule();
			// TODO - cookie domain name should be hostname from tomee
			String logInCookieName = ConfigFileReader.getHostNameFromFile();
			
			if(scope==null){
				scope = "Default";
			}
			
			if (logInCookieName != null)
			{
				// get http request

				UserSession userSession = posNirvanaLoginModule.login(httpRequest, em, username, password, 
						appType, deviceId, deviceTypeId, deviceName, versionInfo, businessId, scope);
				
				if (userSession != null)
				{
					POSNirvanaUser posNirvanaUser = posNirvanaLoginModule.getUserPrincipal();
					if (posNirvanaUser != null)
					{

						// get this server name and compare if its the same
						// server or not, if same server send user, else
						// send the local sever url
						String serverName = ConfigFileReader.getHostNameFromFile();
						if (posNirvanaUser.getLocalServerUrl() != null)
						{
							// if data has not been initalized in the database
							// for local installation
							if ("Please enter local server url for an account".equalsIgnoreCase(posNirvanaUser.getLocalServerUrl()))
							{
								Response response = Response.ok(posNirvanaUser.getLocalServerUrl()).build();
								return response;
							}

							else
							{
								if (posNirvanaUser.getServerConfig() != null && (!serverName.trim().equalsIgnoreCase(posNirvanaUser.getServerConfig().getName().trim())))
								{
									String res = new JSONUtility(httpRequest).convertToJsonString(posNirvanaUser.getServerConfig());
									Response response = Response.ok("{\"ServerConfig\" :" + res + "}").build();
									return response;
								}
								else
								{
									return createUserResponse(posNirvanaUser, logInCookieName,scope);
								}
							}
						}
						else
						{
							return createUserResponse(posNirvanaUser, logInCookieName,scope);
						}

					}
				}
				else
				{
					throw new POSNirvanaLoginException("Login failed due to inability to create session");
				}
			}
			else
			{
				throw new POSNirvanaLoginException("Could not get server host name");
			}
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return null;

	}

	private Response createUserResponse(POSNirvanaUser posNirvanaUserPrincipal, String logInCookieName,String scope)
	{
		int expiryDays;
		if(scope=="Customer")
			expiryDays = 3 * 30 * 24 * 60 * 60 * 1000;
		else
			expiryDays = 30 * 24 * 60 * 60 * 1000;
		// todo :- INirvanaService.NIRVANA_SESSION_COOKIE_NAME check why this is changing to small char
		NewCookie httpSecureCookie = new NewCookie("SessionId", posNirvanaUserPrincipal.getAuthenticationToken(), "", logInCookieName, "login cookie", expiryDays * 24, true, true);
		NewCookie encyptionKeyCookie = new NewCookie("encyptionKey", posNirvanaUserPrincipal.getEncryptionKey(), "", logInCookieName, "login cookie", expiryDays * 24, true);
		// 	ask client
		// secure cookie
		//ignore https remove
		
		// device management every KMS 
		Response response = Response.ok(posNirvanaUserPrincipal.toString()).header(NIRVANA_ACCESS_TOKEN_HEADER_NAME, posNirvanaUserPrincipal.getAuthenticationToken()).cookie(httpSecureCookie).cookie(encyptionKeyCookie).build();

		return response;
	}

	/**
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/logout")
	public String logout(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// deleted from the database
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UserSession> criteria = builder.createQuery(UserSession.class);
			Root<UserSession> root = criteria.from(UserSession.class);
			TypedQuery<UserSession> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(UserSession_.session_id), sessionId)));

			// get the session for the user and remove from the database
			UserSession userSession = query.getSingleResult();
			GlobalSchemaEntityManager.remove(em, userSession);

			// also set the logout time in user session history table
			CriteriaBuilder builder2 = em.getCriteriaBuilder();
			CriteriaQuery<UserSessionHistory> criteria2 = builder2.createQuery(UserSessionHistory.class);
			Root<UserSessionHistory> root2 = criteria2.from(UserSessionHistory.class);
			TypedQuery<UserSessionHistory> query2 = em.createQuery(criteria2.select(root2).where(builder.equal(root2.get(UserSessionHistory_.session_id), sessionId)));

			// get the session from history and update the logout time for this session
			UserSessionHistory userSessionHistory = query2.getSingleResult();
			userSessionHistory.setLogoutTime(new TimezoneTime().getGMTTimeInMilis());
			GlobalSchemaEntityManager.merge(em, userSessionHistory);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return new JSONUtility(httpRequest).convertToJsonString(true);

	}

	@GET
	@Path("/registerDeviceForBussinessId/{businessId}")
	public String registerDeviceForBussinessId(@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId, @PathParam("businessId") int businessId) throws Exception
	{
		EntityManager em = null;
		UserSession userSession = null;
		try
		{
			
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UserSession> criteria2 = builder.createQuery(UserSession.class);
			Root<UserSession> root2 = criteria2.from(UserSession.class);
			TypedQuery<UserSession> query2 = em.createQuery(criteria2.select(root2).where(builder.equal(root2.get(UserSession_.session_id), sessionId)));

			// get the session from history and update the logout time for
			// this
			// session
			userSession = query2.getSingleResult();
			

			if (userSession != null)
			{

				Business business = em.find(Business.class, businessId);
				if (business == null)
				{
					return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("invalid business Id", 2));
				}
				List<DeviceInfoToBusiness> deviceInfoToBusinessesList = getDeviceInfoToBusinessForBusinessId(businessId, em);
				int maxNoallowedDeviceForBusiness = business.getMaxAllowedDevices();

				if (deviceInfoToBusinessesList != null && deviceInfoToBusinessesList.size() > 0)
				{
					int totalDeviceAfterThisDeviceRegisters = deviceInfoToBusinessesList.size() + 1;
					if (totalDeviceAfterThisDeviceRegisters > maxNoallowedDeviceForBusiness)
					{
						// this device cannot register as max allowed devices is
						// exceeding
						return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("This device cannot register as max allowed devices is exceeding", 3));
					}
					else
					{
						registerDeviceForBusiness(businessId, userSession, em);
						return "true";
					}

				}
				else
				{
					// no device is registered for business yet
					if (maxNoallowedDeviceForBusiness > 0)
					{
						registerDeviceForBusiness(businessId, userSession, em);
						return "true";
					}
					else
					{
						return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("Max allowed devices for bussiness is 0", 4));
					}
				}

			}
			else
			{
				return new JSONUtility(httpRequest).convertToJsonString(new POSExceptionMessage("Invalid user session", 1));
			}

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	private List<DeviceInfoToBusiness> getDeviceInfoToBusinessForDeviceId(int deviceInfoId, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DeviceInfoToBusiness> criteria = builder.createQuery(DeviceInfoToBusiness.class);
		Root<DeviceInfoToBusiness> root = criteria.from(DeviceInfoToBusiness.class);
		TypedQuery<DeviceInfoToBusiness> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(DeviceInfoToBusiness_.deviceInfoId), deviceInfoId),
				builder.equal(root.get(DeviceInfoToBusiness_.status), "A")));
		return query.getResultList();
	}

	private List<DeviceInfoToBusiness> getDeviceInfoToBusinessForBusinessId(int businessId, EntityManager em) throws Exception
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DeviceInfoToBusiness> criteria = builder.createQuery(DeviceInfoToBusiness.class);
		Root<DeviceInfoToBusiness> root = criteria.from(DeviceInfoToBusiness.class);
		TypedQuery<DeviceInfoToBusiness> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(DeviceInfoToBusiness_.businessId), businessId),

		builder.equal(root.get(DeviceInfoToBusiness_.status), "A")));
		return query.getResultList();
	}

	

	private void registerDeviceForBusiness(int businessId, UserSession userSession, EntityManager em)
	{
		// register this device for this business and unregister
		// from other business

		List<DeviceInfoToBusiness> deviceInfoToBusinessesListForDeviceId = getDeviceInfoToBusinessForDeviceId(userSession.getDeviceInfo().getId(), em);
		boolean shouldRegisterDevice = true;
		if (deviceInfoToBusinessesListForDeviceId != null && deviceInfoToBusinessesListForDeviceId.size() > 0)
		{
			for (DeviceInfoToBusiness deviceInfoToBusiness : deviceInfoToBusinessesListForDeviceId)
			{

				if (deviceInfoToBusiness != null)
				{
					if (deviceInfoToBusiness.getBusinessId() != businessId)
					{
						deviceInfoToBusiness.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						deviceInfoToBusiness.setStatus("D");
						deviceInfoToBusiness.setUpdatedBy(userSession.getUser_id());

						EntityTransaction tx = em.getTransaction();
						try
						{
							// start transaction
							tx.begin();
							em.merge(deviceInfoToBusiness);
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
					else
					{
						// this device is already registered for this
						// business
						shouldRegisterDevice = false;
					}

				}

			}
		}
		// now register for this bussiness
		if (shouldRegisterDevice)
		{
			DeviceInfoToBusiness deviceInfoToBusiness = new DeviceInfoToBusiness();
			deviceInfoToBusiness.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			deviceInfoToBusiness.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			deviceInfoToBusiness.setCreatedBy(userSession.getUser_id());
			deviceInfoToBusiness.setUpdatedBy(userSession.getUser_id());
			deviceInfoToBusiness.setDeviceInfoId(userSession.getDeviceInfo().getId());
			deviceInfoToBusiness.setBusinessId(businessId);
			deviceInfoToBusiness.setStatus("A");
			GlobalSchemaEntityManager.persist(em, deviceInfoToBusiness);
		}

	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	
	/**
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/logoutForKds")
	public String logoutForKds() throws Exception
	{
		EntityManager em = null;
		String sessionId = httpRequest.getHeader(NIRVANA_ACCESS_TOKEN_HEADER_NAME);

		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// deleted from the database
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UserSession> criteria = builder.createQuery(UserSession.class);
			Root<UserSession> root = criteria.from(UserSession.class);
			TypedQuery<UserSession> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(UserSession_.session_id), sessionId)));

			// get the session for the user and remove from the database
			UserSession userSession = query.getSingleResult();
			GlobalSchemaEntityManager.remove(em, userSession);

			// also set the logout time in user session history table
			CriteriaBuilder builder2 = em.getCriteriaBuilder();
			CriteriaQuery<UserSessionHistory> criteria2 = builder2.createQuery(UserSessionHistory.class);
			Root<UserSessionHistory> root2 = criteria2.from(UserSessionHistory.class);
			TypedQuery<UserSessionHistory> query2 = em.createQuery(criteria2.select(root2).where(builder.equal(root2.get(UserSessionHistory_.session_id), sessionId)));

			// get the session from history and update the logout time for this session
			UserSessionHistory userSessionHistory = query2.getSingleResult();
			userSessionHistory.setLogoutTime(new TimezoneTime().getGMTTimeInMilis());
			GlobalSchemaEntityManager.merge(em, userSessionHistory);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
		return new JSONUtility(httpRequest).convertToJsonString(true);

	}

	@POST
	@Path("/loginService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response loginService(LoginPacket loginPacket) throws POSNirvanaLoginException,
			FileNotFoundException, IOException
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			POSNLoginModule posNirvanaLoginModule = new POSNLoginModule();
			// TODO - cookie domain name should be hostname from tomee
			String logInCookieName = ConfigFileReader.getHostNameFromFile();
			
			if(loginPacket.getScope()==null){
				loginPacket.setScope("Default");
			}
			
			if (logInCookieName != null)
			{
				// get http request

				UserSession userSession = posNirvanaLoginModule.login(httpRequest, em, loginPacket.getUsername(), loginPacket.getPassword(), 
						loginPacket.getAppType(), loginPacket.getDeviceId(), loginPacket.getDeviceTypeId(), loginPacket.getDeviceName(), loginPacket.getVersionInfo(), loginPacket.getBusinessId(), loginPacket.getScope());
				if (userSession != null)
				{
					POSNirvanaUser posNirvanaUser = posNirvanaLoginModule.getUserPrincipal();
					if (posNirvanaUser != null)
					{

						// get this server name and compare if its the same
						// server or not, if same server send user, else
						// send the local sever url
						String serverName = ConfigFileReader.getHostNameFromFile();
						if (posNirvanaUser.getLocalServerUrl() != null)
						{
							// if data has not been initalized in the database
							// for local installation
							if ("Please enter local server url for an account".equalsIgnoreCase(posNirvanaUser.getLocalServerUrl()))
							{
								Response response = Response.ok(posNirvanaUser.getLocalServerUrl()).build();
								return response;
							}

							else
							{
								if (posNirvanaUser.getServerConfig() != null && (!serverName.trim().equalsIgnoreCase(posNirvanaUser.getServerConfig().getName().trim())))
								{
									String res = new JSONUtility(httpRequest).convertToJsonString(posNirvanaUser.getServerConfig());
									Response response = Response.ok("{\"ServerConfig\" :" + res + "}").build();
									return response;
								}
								else
								{
									return createUserResponse(posNirvanaUser, logInCookieName,loginPacket.getScope());
								}
							}
						}
						else
						{
							return createUserResponse(posNirvanaUser, logInCookieName,loginPacket.getScope());
						}

					}
				}
				else
				{
					throw new POSNirvanaLoginException("Login failed due to inability to create session");
				}
			}
			else
			{
				throw new POSNirvanaLoginException("Could not get server host name");
			}
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

		return null;

	}
	
	@GET
	@Path("/serverConfig/{locationId}/{accountId}")
	public String serverConfig(@PathParam("locationId") String locationId, @PathParam("accountId") int accountId) throws Exception
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, null);
			String queryString = "select p from AccountToServerConfig p where   p.accountsId = " + accountId + " and p.locationId = '" + locationId  + "'  ";
			TypedQuery<AccountToServerConfig> query = em.createQuery(queryString, AccountToServerConfig.class);
			
			List<AccountToServerConfig> list = query.getResultList();
			List<ServerConfig> configs = new ArrayList<ServerConfig>();
			for(AccountToServerConfig config:list){
				ServerConfig serverConfig = em.find(ServerConfig.class, config.getServerConfigId());
				configs.add(serverConfig);
			}
			return new JSONUtility(httpRequest).convertToJsonString(configs);
		}
		finally
		{
			LocalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
}
