/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.authentication;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.DeviceType;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.accounts.ServerConfig;
import com.nirvanaxp.global.types.entities.devicemgmt.EncryptionKey;
import com.nirvanaxp.security.ManageSecurityToken;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.services.exceptions.POSNirvanaLoginException;

public class POSNLoginModule
{

	private final static NirvanaLogger logger = new NirvanaLogger(POSNLoginModule.class.getName());

	private EntityManager em = null;
	private User user;
	private String appType = "";
	private int rolesId = 0;
	private int accountId = 0;
	private String deviceId;
	private int deviceTypeId;
	private DeviceType deviceType;
	private String deviceName;
	private String ipAddress;
	private int isLocalAccount;
	private String localServerUrl;
	private String versionInfo;
	private String username;
	private String password;
	private String scope;
	private int businessId;
	private ServerConfig serverConfig;

	// user principal
	private POSNirvanaUser userPrincipal = null;

	public UserSession login(HttpServletRequest httpRequest, EntityManager em, String username, String password, String appType, String deviceId, int deviceTypeId, String deviceName,
			 String versionInfo,int businessId, String scope) throws POSNirvanaLoginException
	{

		try
		{
			this.em = em;
			this.username = username;
			this.password = password;
			this.appType = appType;
			this.deviceId = deviceId;
			this.deviceTypeId = deviceTypeId;
			this.deviceName = deviceName;
			this.versionInfo = versionInfo;
			this.scope = scope;
			this.businessId = businessId;
			this.ipAddress = httpRequest.getRemoteAddr();

			// all input valid?
			validate();

			// continue with login
			user = shouldAllowLogin(username, password);
			if (user != null)
			{

				// update last login time
				user.setLastLoginTs(new Date(new TimezoneTime().getGMTTimeInMilis()));
				EntityTransaction tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.merge(user);
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
					logger.severe(e, "Could not update last login date for user", user.getUsername());
				}

				return saveToUI(httpRequest);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			logger.severe(httpRequest, e, "Could not build login token");
			throw new POSNirvanaLoginException("Cannot process login because of unexpected error");
		}

		return null;
	}

	private void validate() throws POSNirvanaLoginException
	{
		if (em == null || !em.isOpen())
		{
			throw new POSNirvanaLoginException("Unable to connect to database");
		}

		if (username == null || username.isEmpty())
		{
			throw new POSNirvanaLoginException("username cannot be null or empty");
		}

		if (password == null || password.isEmpty())
		{
			throw new POSNirvanaLoginException("password cannot be null or empty");
		}

		if (appType == null || appType.trim().length() == 0)
		{
			throw new POSNirvanaLoginException("App Type cannot be null or blank");
		}

		if (deviceId == null || deviceId.trim().length() == 0)
		{
			throw new POSNirvanaLoginException("device Id cannot be null or empty");
		}

		if (deviceTypeId == 0)
		{
			throw new POSNirvanaLoginException("deviceTypeId cannot be 0");
		}
		if (deviceName == null || deviceName.trim().length() == 0)
		{
			throw new POSNirvanaLoginException("device Name cannot be null or empty");
		}
		if (ipAddress == null || ipAddress.trim().length() == 0)
		{
			throw new POSNirvanaLoginException("IP Address cannot be null or empty");
		}
		if (scope == null || scope.trim().length() == 0)
		{
			throw new POSNirvanaLoginException("Scope cannot be null or empty");
		}

		// check if this device type id exists in out database or not, if not
		// send device type does not exists in our database
		deviceType = getDeviceTypeById(deviceTypeId);
		if (deviceType == null)
		{
			throw new POSNirvanaLoginException("Wrong device type id sent to server. Plese recheck the device Id");
		}

		logger.info("Starting Login Process for: Username", username, ", AppType", appType, ", DeviceId", deviceId, ", DeviceTypeId", "" + deviceTypeId, ", DeviceName", deviceName, ", IP Address",
				ipAddress);
	}

	private UserSession saveToUI(HttpServletRequest httpRequest) throws POSNirvanaLoginException, NoSuchAlgorithmException
	{
		UserSession userSession = null;

		userPrincipal = new POSNirvanaUser(user);

		String uniqueToken = ManageSecurityToken.generateUniqueAuthToken(user);
		userPrincipal.setAuthenticationToken(uniqueToken);

		// commit the session information onto the database
		try
		{
			userSession = ManageSecurityToken.commitToDatabase(httpRequest, uniqueToken, user, em, rolesId, 
					accountId, deviceId, deviceType, deviceName, ipAddress, isLocalAccount, localServerUrl,
					versionInfo, businessId, scope);

			// add the encyption key to the user principle
			if (userSession != null)
			{
				Set<EncryptionKey> encyptionKeySet = userSession.getDeviceInfo().getEncyptionKeyinfos();
				if (encyptionKeySet != null && encyptionKeySet.size() > 0)
				{
					for (EncryptionKey encryptionKey : encyptionKeySet)
					{
						if (encryptionKey != null && encryptionKey.getAccountId() == accountId)
						{
							userPrincipal.setEncryptionKey(encryptionKey.getEncryptionKey());
						}
					}
				}

			}

			if (isLocalAccount == 1)
			{
				/*
				 * if (localServerUrl == null || localServerUrl.trim().length()
				 * == 0) { localServerUrl =
				 * "Please enter local server url for an account"; }
				 */
				if (serverConfig == null)
				{
					localServerUrl = "Please enter local server url for an account";
				}
				userPrincipal.setLocalServerUrl(localServerUrl);
				userPrincipal.setServerConfig(serverConfig);
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
			throw new POSNirvanaLoginException("failed to generate valid user authentication token. Please try again later");
		}

		return userSession;

	}

	private User shouldAllowLogin(String username, String password) throws POSNirvanaLoginException
	{
		// check if app type is supported
		if (!"Admin".equalsIgnoreCase(appType) && !"POS Operator".equalsIgnoreCase(appType) && !"POS Customer".equalsIgnoreCase(appType)&& !"Enterprise Report".equalsIgnoreCase(appType) && !"Global Setting".equalsIgnoreCase(appType) && !"Driver".equalsIgnoreCase(appType)&& !"Analytics".equalsIgnoreCase(appType))
		{

			throw new POSNirvanaLoginException("App is not supported");
		}

		// DeviceInfo deviceInfo = getDeviceInfoByDeviceName(deviceName);
		try
		{
			user = getSingleUserByUserName(username, em);
		}
		catch (NoResultException e)
		{
			throw new POSNirvanaLoginException("No User found for username.");
		}

		if (user == null)
		{
			// this happens when username exists but the password is
			// wrong
			throw new POSNirvanaLoginException("No User found for username");
		}

		if (user.getBusinessesSet() == null || user.getBusinessesSet().isEmpty())
		{
			throw new POSNirvanaLoginException("No active location assigned to user");
		}

		if (!user.getPassword().equals(DigestUtils.sha512Hex(password)))
		{
			throw new POSNirvanaLoginException("No User found for username and password combination.");
		}

		if (user.getStatus() != null)
		{
			if (user.getStatus().equalsIgnoreCase("D"))
			{
				throw new POSNirvanaLoginException("User is deleted from the database");
			}

			if (user.getStatus().equalsIgnoreCase("I"))
			{
				throw new POSNirvanaLoginException("Inactive user");
			}

		}

		if (user.getRolesSet() == null)
		{
			throw new POSNirvanaLoginException("No Roles found for user");
		}

		if ("Admin".equalsIgnoreCase(appType) && !hasAdminRoleForAdminAppLogin())
		{
			throw new POSNirvanaLoginException("User does not have admin role");
		}

		if ("POS Operator".equalsIgnoreCase(appType) && !hasEmployeeRoleForBusinessAppLogin())
		{
			throw new POSNirvanaLoginException("User does not have employee role");
		}

		if ("POS Customer".equalsIgnoreCase(appType) && !hasCustomerRoleForBusinessAppLogin())
		{
			throw new POSNirvanaLoginException("User does not have customer role");
		}if ("Enterprise Report".equalsIgnoreCase(appType) && !hasCustomerRoleForBusinessAppLogin())
		{
			throw new POSNirvanaLoginException("User does not have enterprise role");
		}if ("Global Setting".equalsIgnoreCase(appType) && !hasCustomerRoleForBusinessAppLogin())
		{
			throw new POSNirvanaLoginException("User does not have Global Setting");
		}
		if ("Driver".equalsIgnoreCase(appType) && !hasDriverRoleForDriverAppLogin())
		{
			throw new POSNirvanaLoginException("User does not have Driver role");
		}
		if ("Analytics".equalsIgnoreCase(appType) && !hasAnalyticsRoleForAnalyticsLogin())
		{
			throw new POSNirvanaLoginException("User does not have Analytics role");
		}

		processServerConfig();

		checkIfDeviceCanLoginUsingDeviceManagement();

		// return the user details
		return user;
	}

	private boolean hasEmployeeRoleForBusinessAppLogin()
	{
		for (Role role : user.getRolesSet())
		{
			if(role.getRoleName() != null && DefaultBusinessRoles.getByRoleName(role.getRoleName())!=null)
			{
				if (DefaultBusinessRoles.getByRoleName(role.getRoleName()).isEmployeeRole())
				{
					rolesId = role.getId();
					accountId = role.getAccountId();
					return true;
				}
			}
				
		}

		return false;
	}

	private boolean hasCustomerRoleForBusinessAppLogin()
	{
		for (Role role : user.getRolesSet())
		{
			if(role.getRoleName() != null && DefaultBusinessRoles.getByRoleName(role.getRoleName())!=null)
			{
			if (DefaultBusinessRoles.getByRoleName(role.getRoleName()).isCustomerRole())
			{
				rolesId = role.getId();
				accountId = role.getAccountId();
				return true;
			}
			}
		}

		return false;
	}
	private boolean hasDriverRoleForDriverAppLogin()
	{
		for (Role role : user.getRolesSet())
		{
			if(role.getRoleName() != null && DefaultBusinessRoles.getByRoleName(role.getRoleName())!=null)
			{
			if (DefaultBusinessRoles.getByRoleName(role.getRoleName()).isDriverRole())
			{
				rolesId = role.getId();
				accountId = role.getAccountId();
				return true;
			}
			}
		}
			

		return false;
	}
	private boolean hasAnalyticsRoleForAnalyticsLogin()
	{
		for (Role role : user.getRolesSet())
		{
			if(role.getRoleName() != null && DefaultBusinessRoles.getByRoleName(role.getRoleName())!=null)
			{
			if (DefaultBusinessRoles.getByRoleName(role.getRoleName()).isAnalyticsRole())
			{
				rolesId = role.getId();
				accountId = role.getAccountId();
				return true;
			}
			}
			
		}

		return false;
	}

	
	private boolean hasAdminRoleForAdminAppLogin()
	{
		// now find out if this is an admin user coming from admin app
		
	 
		 
		for (Role role : user.getRolesSet())
		{
			if(role.getRoleName() != null && DefaultBusinessRoles.getByRoleName(role.getRoleName())!=null)
			{
				if (DefaultBusinessRoles.getByRoleName(role.getRoleName()).isAdminRole())
				{
					rolesId = role.getId();
					accountId = role.getAccountId();

					return true;
				}
			}
		}

		return false;
	}

	private void processServerConfig()
	{
		if (user.getAccountsSet() != null)
		{
			for (Account account : user.getAccountsSet())
			{
				if (account != null)
				{
					isLocalAccount = account.getIsLocalAccount();
					localServerUrl = account.getLocalServerUrl();

					if (account.getServerConfigs() != null && account.getServerConfigs().size() > 0 && isLocalAccount == 1)
					{

						for (ServerConfig serverConfigs : account.getServerConfigs())
						{
							if (serverConfigs != null && serverConfigs.getResource().equalsIgnoreCase("serverUrl"))
							{
								
								this.serverConfig = serverConfigs;
								break;
							}
						}
					}
				}

			}
		}
	}

	private boolean checkIfDeviceCanLoginUsingDeviceManagement() throws POSNirvanaLoginException
	{
		int maxAllowedDevicesForAccount = 0;
		int currentlyActiveDevices = getNumberOfActiveDevicesForAccount(accountId);
		Account userAccount = null;
		if (user.getAccountsSet() != null && user.getAccountsSet().size() > 0)
		{
			for (Account account : user.getAccountsSet())
			{
				if (account != null && account.getId() == accountId)
				{
					maxAllowedDevicesForAccount = account.getMaxAllowedDevices();
					userAccount = account;
				}
			}
		}
		// removed by Apoorva on 04-09-2017 because we are not using device management and we need to reimplement device implementation
//
//		if (currentlyActiveDevices >= maxAllowedDevicesForAccount)
//		{
//			POSNirvanaLoginException ex = new POSNirvanaLoginException(
//					"You must logout on other currently active device to log into the application on this device. You can also register for more devices by calling customer support. ");
//			logger.severe(ex, "Cannot Login User", "max Allowed Devices For Account = " + maxAllowedDevicesForAccount, ", account name =", userAccount.getName(), ", currenlty active user count = "
//					+ currentlyActiveDevices);
//			throw ex;
//		}

		return true;

	}

	private User getSingleUserByUserName(String username, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> user = criteria.from(User.class);
		TypedQuery<User> query = em.createQuery(criteria.select(user).where(builder.equal(user.get(com.nirvanaxp.global.types.entities.User_.username), username.trim())));
		return query.getSingleResult();
	}

	public POSNirvanaUser getUserPrincipal()
	{
		return userPrincipal;
	}

	private DeviceType getDeviceTypeById(int deviceypeId)
	{
		DeviceType deviceType = em.find(DeviceType.class, deviceypeId);
		return deviceType;
	}

	// private int getNumberOfDevicesForBussinessIdAndAccountId(int accountId,
	// int businessId) {
	//
	// // count how many people have logged in from business application (not
	// // as customers and from iphone and android and windows devices type)
	// // having the same account type as this user and associated the the same
	// // busssiness and this user
	// String queryString =
	// "select count(*) from user_session us LEFT JOIN roles r on " +
	// " us.users_roles_id = r.id LEFT JOIN device_info di on "
	// + " us.device_info_id = di.id LEFT JOIN users_to_business utb on " +
	// " us.user_id = utb.users_id where us.merchant_id = ? "
	// +
	// " and r.role_name != ? and (di.device_type_id =1 or di.device_type_id =  2 "
	// + " or di.device_type_id = 3) and utb.business_id=?";
	// Query q = em.createNativeQuery(queryString).setParameter(1,
	// accountId).setParameter(2,
	// DefaultBusinessRoles.POS_Customer.getRoleName())
	// .setParameter(3, businessId);
	// Object totalActiveDevicesForBussiness = q.getSingleResult();
	// BigInteger bigInteger = (BigInteger) totalActiveDevicesForBussiness;
	// return bigInteger.intValue();
	// }

	private int getNumberOfActiveDevicesForAccount(int accountId)
	{

		
		// select users session
		// count how many people have logged in from business application (not
		// as customers and from iphone and android and windows devices type)
		// having the same account type as this user
		String queryString = "select count(*) from user_session us  JOIN roles r on " + " us.users_roles_id = r.id   and r.account_id = us.merchant_id JOIN device_info di on "
				+ "us.device_info_id = di.id where us.merchant_id = ? " + " and r.role_name != ? and (di.device_name in ('Iphone','Android','Windows')) ";

		Query q = em.createNativeQuery(queryString).setParameter(1, accountId).setParameter(2, DefaultBusinessRoles.POS_Customer.getRoleName());
		Object totalActiveDevicesForAccount = q.getSingleResult();
		BigInteger bigInteger = (BigInteger) totalActiveDevicesForAccount;
		return bigInteger.intValue();

	}

}