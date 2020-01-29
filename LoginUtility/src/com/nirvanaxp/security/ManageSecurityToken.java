/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.DeviceType;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.UserSessionHistory;
import com.nirvanaxp.global.types.entities.UserSessionHistory_;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.accounts.AccountsToBusiness;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToAccounts;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfoToBusiness;
import com.nirvanaxp.global.types.entities.devicemgmt.DeviceInfo_;
import com.nirvanaxp.global.types.entities.devicemgmt.EncryptionKey;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;

public class ManageSecurityToken
{

	private static final NirvanaLogger logger = new NirvanaLogger(ManageSecurityToken.class.getName());

	public static UserSession commitToDatabase(HttpServletRequest httpRequest, String uniqueToken, User user, EntityManager em, int rolesId, int accountId, String deviceId, DeviceType deviceType,
			String deviceName, String ipAddress, int isLocalAccount, String serverUrl, String versionInfo
			, int businessId, String scope) throws FileNotFoundException, IOException
	{

		// update user's password
		DeviceInfo deviceInfo = manageDeviceInformation(httpRequest, em, deviceId, deviceType, deviceName, user, accountId);
		if (deviceInfo.getId() == 0)
		{
			// save this into the database first
			GlobalSchemaEntityManager.persist(em, deviceInfo);

		}

		// also register device of not register with account
		registerDevicesToAccount(em, accountId, deviceInfo, user);

		String serverName = ConfigFileReader.getHostNameFromFile();

		if (isLocalAccount == 0 || (isLocalAccount == 1 && serverName != null && serverUrl != null && serverName.trim().equalsIgnoreCase(serverUrl.trim())))
		{
			UserSession userSession = new UserSession();

			userSession.setSession_id(uniqueToken);
			userSession.setUser_id(user.getId());
			long currentTime = new TimezoneTime().getGMTTimeInMilis();
			userSession.setLoginTime(currentTime);
			userSession.setLogoutTime(currentTime);
			userSession.setIpAddress(ipAddress);
			userSession.setScope(scope);
			userSession.setBusinessId(businessId);
			userSession.setIpAddress(ipAddress);
			userSession.setVersionInfo(versionInfo);
		// role id will be sent 0 for customers
			if (rolesId != 0)
			{
				if (user.getAccountsSet() != null)
				{

					for (Account account : user.getAccountsSet())
					{
						if (accountId == account.getId())
						{
							userSession.setSchema_name(account.getSchemaName());
							userSession.setMerchant_id(account.getId());
						}
					}

				}
				userSession.setUsersRolesId(rolesId);
			}

			userSession.setDeviceInfo(deviceInfo);
			// remove all previous user sessions for android and ios devices
			DeviceType deviceTypeOfCurrentDevice = em.find(DeviceType.class, deviceType.getId());
			if (deviceTypeOfCurrentDevice != null
					&& (deviceTypeOfCurrentDevice.getName().equalsIgnoreCase("Iphone") || deviceTypeOfCurrentDevice.getName().equalsIgnoreCase("Android") || deviceTypeOfCurrentDevice.getName()
							.equalsIgnoreCase("Windows")))
			{

				logOutPreviosSessionOnReloginWithoutLogout(httpRequest, deviceId, em, deviceType.getId(), deviceInfo.getId());

			}
			GlobalSchemaEntityManager.persist(em, userSession);

			// create a copy in history also
			UserSessionHistory userSessionHistory = new UserSessionHistory(userSession.getMerchant_id(), userSession.getId(), userSession.getUser_id(), userSession.getSchema_name(),
					userSession.getSession_id(), userSession.getUsersRolesId(), userSession.getDeviceInfo(), userSession.getLoginTime(), ipAddress, userSession.getVersionInfo());

			GlobalSchemaEntityManager.persist(em, userSessionHistory);

			return userSession;

		}

		return null;

	}

	private static void registerDevicesToAccount(EntityManager em, int accountId, DeviceInfo deviceInfo, User user)
	{

		ManageDevice manageDevice = new ManageDevice();
		List<DeviceInfoToAccounts> deviceInfoList = manageDevice.getDeviceInfoToAccountsForDeviceIdAndAccountId(em, accountId, deviceInfo.getId());

		if (deviceInfoList == null || deviceInfoList.size() == 0)
		{
			// register device for account
			DeviceInfoToAccounts deviceInfoToAccounts = new DeviceInfoToAccounts();
			deviceInfoToAccounts.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			deviceInfoToAccounts.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			deviceInfoToAccounts.setStatus("A");
			deviceInfoToAccounts.setAccountId(accountId);
			deviceInfoToAccounts.setDeviceInfoId(deviceInfo.getId());
			deviceInfoToAccounts.setCreatedBy(user.getId());
			deviceInfoToAccounts.setUpdatedBy(user.getId());

			GlobalSchemaEntityManager.persist(em, deviceInfoToAccounts);

		}

		List<AccountsToBusiness> acoAccountsToBusinessesList = manageDevice.getAcccountToBusinessForAccountId(em, accountId);
		if (acoAccountsToBusinessesList != null && acoAccountsToBusinessesList.size() == 1)
		{
			// account has only 1 business, register for this device to
			// business
			// check if device already registered.
			List<DeviceInfoToBusiness> deviceInfoToBusinesses = manageDevice.getDeviceInfoToBusinessForDeviceIdAndBusinessId(acoAccountsToBusinessesList.get(0).getId(), deviceInfo.getId(), em);

			if (deviceInfoToBusinesses == null || deviceInfoToBusinesses.size() == 0)
			{
				// register device for account
				DeviceInfoToBusiness deviceInfoToBusiness = new DeviceInfoToBusiness();
				deviceInfoToBusiness.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				deviceInfoToBusiness.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				deviceInfoToBusiness.setStatus("A");
				deviceInfoToBusiness.setBusinessId(acoAccountsToBusinessesList.get(0).getId());
				deviceInfoToBusiness.setDeviceInfoId(deviceInfo.getId());
				deviceInfoToBusiness.setCreatedBy(user.getId());
				deviceInfoToBusiness.setUpdatedBy(user.getId());

				GlobalSchemaEntityManager.persist(em, deviceInfoToBusiness);

			}

		}

	}

	private static List<UserSessionHistory> getUserSessionForSessionId(EntityManager em, int userSessionId)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserSessionHistory> criteria = builder.createQuery(UserSessionHistory.class);
		Root<UserSessionHistory> r = criteria.from(UserSessionHistory.class);
		TypedQuery<UserSessionHistory> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(UserSessionHistory_.userSessionId), userSessionId)));
		List<UserSessionHistory> userSessionHistroyList = query.getResultList();
		return userSessionHistroyList;

	}

	private static void logOutPreviosSessionOnReloginWithoutLogout(
			HttpServletRequest httpRequest, String deviceId, EntityManager em,
			int deviceTypeId, int deviceInfoId) {

			em.getTransaction().begin();
		 
			// log out this user session
			 
				try {
					
					String my = "update user_session_history set logout_time=now() where user_session_id in   (select id from user_session where device_info_id = "+deviceInfoId+")";
					@SuppressWarnings("unchecked")
					int result = em.createNativeQuery(my).executeUpdate();
					
					 my = "update user_session set logout_time=now() where  device_info_id = "+deviceInfoId+"";
					@SuppressWarnings("unchecked")
					int resultList = em.createNativeQuery(my).executeUpdate();
					
					 

				} catch (Exception e) {
					logger.severe(httpRequest, e, e.getMessage());
				}
			 
		em.getTransaction().commit();
	
	}
	private static DeviceInfo manageDeviceInformation(HttpServletRequest httpRequest, EntityManager em, String deviceId, DeviceType deviceType, String deviceName, User user, int accountId)
	{
		DeviceInfo deviceInfo = null;

		try
		{
			deviceInfo = getDeviceInfoUsingDeviceId(deviceId, deviceType, em);
		}
		catch (NoResultException nre)
		{
			logger.fine(httpRequest, "No device info found for: ", deviceId, deviceType.getName(), "will construct new info data");
		}
		if (deviceInfo == null)
		{
			// check if device type is of type that might need encryption
			// information or not
			deviceInfo = constructDeviceInfoObj(httpRequest, deviceId, deviceType, user, accountId, deviceName);
			return deviceInfo;
		}
		else
		{
			// device already exists with us,
			if (deviceType != null)
			{

				boolean shouldUpdateDeviceName = false;
				// check if device name already exits with us or not
				if (deviceInfo.getDeviceName() == null && deviceName != null && deviceName.trim().length() != 0)
				{
					shouldUpdateDeviceName = true;

				}
				else
				{
					// check if the device name is changed or not
					if (deviceInfo.getDeviceName() != null && deviceInfo.getDeviceName().isEmpty() == false && deviceName != null && deviceName.isEmpty() == false
							&& (deviceInfo.getDeviceName().trim().equals(deviceName.trim()) == false))
					{
						shouldUpdateDeviceName = true;
					}
				}
				if (shouldUpdateDeviceName)
				{
					deviceInfo.setDeviceName(deviceName);
					GlobalSchemaEntityManager.merge(em, deviceInfo);
				}
				// check if it requires encyption keys or not
				int isEncryptionKeysRequired = deviceType.getIsEncryptionkeyRequired();
				if (isEncryptionKeysRequired == 1)
				{

					// check if we have encryption keys available for this
					// device id and account id combination or not
					boolean isEncrytionKeyAvailableForAccountAndDeviceIdCombination = false;
					Set<EncryptionKey> encyptionKeysSet = deviceInfo.getEncyptionKeyinfos();
					if (encyptionKeysSet != null && encyptionKeysSet.size() > 0)
					{
						for (EncryptionKey encyptionKey : encyptionKeysSet)
						{
							if (encyptionKey != null)
							{
								if (encyptionKey.getAccountId() == accountId)
								{
									isEncrytionKeyAvailableForAccountAndDeviceIdCombination = true;
								}
							}
						}
					}
					// initialize the set of its null
					if (encyptionKeysSet == null)
					{
						encyptionKeysSet = new HashSet<EncryptionKey>();
					}
					// if we don't have the encryption keys for this device and
					// account id combination, then generate one
					if (isEncrytionKeyAvailableForAccountAndDeviceIdCombination == false)
					{
						try
						{
							EncryptionKey encryptionKey = getEncrytionkeyForDeviceAndAccountId(deviceId, deviceType, user, accountId);
							encyptionKeysSet.add(encryptionKey);
						}
						catch (NoSuchAlgorithmException e)
						{
							logger.severe(e);
						}
					}

					deviceInfo.setEncyptionKeyinfos(encyptionKeysSet);
					return deviceInfo;

				}
				else
				{
					// no EncyptionKey required, return the device info object
					return deviceInfo;
				}
			}
			// if not generate keys for this account id too
		}
		return deviceInfo;
	}

	private static DeviceInfo constructDeviceInfoObj(HttpServletRequest httpRequest, String deviceId, DeviceType deviceType, User user, int accountId, String deviceName)
	{

		if (deviceType != null)
		{
			int isEncryptionKeysRequired = deviceType.getIsEncryptionkeyRequired();
			Set<EncryptionKey> encryptionKeysSet = new HashSet<EncryptionKey>();
			if (isEncryptionKeysRequired == 1)
			{
				// yes encryption keys are required by this device
				try
				{
					EncryptionKey encryptionKey = getEncrytionkeyForDeviceAndAccountId(deviceId, deviceType, user, accountId);
					encryptionKeysSet.add(encryptionKey);
				}
				catch (NoSuchAlgorithmException e)
				{
					logger.severe(httpRequest, e, "Could not generate encryption key for device");
				}
			}

			DeviceInfo newDeviceInfo = new DeviceInfo(deviceId, deviceType, user.getId(), user.getId(), encryptionKeysSet, deviceName);
			newDeviceInfo.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			newDeviceInfo.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			return newDeviceInfo;
		}
		return null;
	}

	private static EncryptionKey getEncrytionkeyForDeviceAndAccountId(String deviceId, DeviceType deviceType, User user, int accountId) throws NoSuchAlgorithmException
	{
		String encryptionKeyForAccountAndDevice = generateEncyptionKeyForDevice(user, deviceId, deviceType.getId(), accountId);
		EncryptionKey encryptionKey = new EncryptionKey(user.getId(), user.getId(), encryptionKeyForAccountAndDevice, accountId);
		encryptionKey.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		encryptionKey.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		return encryptionKey;
	}

	private static DeviceInfo getDeviceInfoUsingDeviceId(String deviceId, DeviceType deviceType, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<DeviceInfo> criteria = builder.createQuery(DeviceInfo.class);
		Root<DeviceInfo> r = criteria.from(DeviceInfo.class);
		TypedQuery<DeviceInfo> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(DeviceInfo_.deviceId), deviceId), builder.equal(r.get(DeviceInfo_.deviceType), deviceType)));
		DeviceInfo deviceInfo = query.getSingleResult();
		return deviceInfo;

	}

	public static String generateUniqueAuthToken(User user) throws NoSuchAlgorithmException
	{
		StringBuffer tokenStr = new StringBuffer().append(UUID.randomUUID().toString().toUpperCase()).append(user.getId()).append(user.getUsername()).append(new TimezoneTime().getGMTTimeInMilis());

		// Create MD5 Hash and return back
		return getMd5HexString(tokenStr.toString());

	}
	public static String generateUniqueAuthTokenForPOSNPartner(POSNPartners posnPartners) throws NoSuchAlgorithmException
	{
		StringBuffer tokenStr = new StringBuffer().append(UUID.randomUUID().toString().toUpperCase()).append(posnPartners.getAccountId()).append(posnPartners.getBusinessId())
				.append(new TimezoneTime().getGMTTimeInMilis());

		// Create MD5 Hash and return back
		return getMd5HexString(tokenStr.toString());

	}

	public static String generateEncyptionKeyForDevice(User user, String deviceId, int deviceTypeId, int accountId) throws NoSuchAlgorithmException
	{
		StringBuffer tokenStr = new StringBuffer().append(UUID.randomUUID().toString().toUpperCase()).append(deviceId).append(deviceTypeId).append(accountId).append(new TimezoneTime().getGMTTimeInMilis());

		// Create MD5 Hash and return back
		return getMd5HexString(tokenStr.toString());

	}

	private static String getMd5HexString(String tokenStr) throws NoSuchAlgorithmException
	{
		// Create MD5 Hash
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(tokenStr.getBytes());
		byte messageDigest[] = digest.digest();

		// Create Hex String
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++)
		{
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}
		return hexString.toString();
	}

}
