/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.user.utility;

import java.math.BigInteger;
import java.nio.charset.Charset;
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
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.Business_;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.Role_;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UsersToAccount;
import com.nirvanaxp.global.types.entities.UsersToAddress;
import com.nirvanaxp.global.types.entities.UsersToBusiness;
import com.nirvanaxp.global.types.entities.UsersToRole;
import com.nirvanaxp.global.types.entities.UsersToRole_;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.LocalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.storeForward.StoreForwardUtilityForGlobal;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.locations.Location_;
import com.nirvanaxp.types.entities.tip.EmployeeMaster;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToLocation_;

public class GlobalUsermanagement
{

	// private User localUser;
	private int accountId;

	private NirvanaLogger logger = new NirvanaLogger(GlobalUsermanagement.class.getName());

	/**
	 * This method adds user to local database and global database only if user
	 * does not exists in global database
	 * 
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public UserManagementObj addUserToGlobalAndLocalDatabase(HttpServletRequest httpRequest, EntityManager globalEntityManager, EntityManager localEntityManager,
			com.nirvanaxp.types.entities.user.User localUser, String locationId, Set<com.nirvanaxp.types.entities.address.Address> localAddressList, Set<UsersToRole> globalRoles,
			HttpServletRequest request,PostPacket postPacket) throws Exception
	{

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		LocalUserUtil localUserUtil = new LocalUserUtil();

		// check if user exits in global database or not
		User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, globalEntityManager, localUser.getPhone(), localUser.getEmail(), localUser.getUsername(),
				localUser.getGlobalUsersId());

		// if a global user is found, return error response
		if (globalUserCheck != null)
		{
			UserManagementObj userManagementObj = new UserManagementObj();
			userManagementObj.setResponse(MessageConstants.MSG_USER_ALREADY_EXISTS_IN_GLOBAL_DB);
			return userManagementObj;
		}

		// if a local user exists with the passed Auth Pin already, then we
		// cannot add local user
		if (localUser.getAuthPin() != null && localUser.getAuthPin().length() > 0)
		{
			boolean doesUserExistsWithAuthPin = localUserUtil.doesUserExistsWithAuthPin(httpRequest, localEntityManager, localUser.getAuthPin(), null);
			if (doesUserExistsWithAuthPin)
			{
				String duplicateAuthPinMsg = MessageConstants.MSG_USER_ALREADY_EXISTS_WITH_SAME_AUTH_PIN;
				// return the object saying that user already exists
				UserManagementObj userManagementObj = new UserManagementObj();
				userManagementObj.setResponse(duplicateAuthPinMsg);
				return userManagementObj;
			}
		}

		// if we got here then it means all conditions for new User have been
		// checked
		// and no existing User was found, so now create a new User
		User globalUser = addGlobalUserUsingLocalUserInfo(globalEntityManager, localEntityManager, localUser, locationId, localUser.getAuthPin(), true, httpRequest, globalRoles,postPacket);
		localUser.setGlobalUsersId(globalUser.getId());
		if (localUser.getId() == null)
		{
			localUser.setId(globalUser.getId());
		}
		Set<Address> globalAddressList = new HashSet<Address>();
		if (localAddressList != null)
		{
			for (com.nirvanaxp.types.entities.address.Address localAddress : localAddressList)
			{
				// creating user object
				Address globalAddress = new Address(localAddress.getAddress1(), localAddress.getAddress2(), localAddress.getCity(), localAddress.getCountryId(), localAddress.getCreatedBy(),
						localAddress.getFax(), localAddress.getLatValue(), localAddress.getLongValue(), localAddress.getPhone(), localAddress.getState(), localAddress.getUpdatedBy(),
						localAddress.getZip(), localAddress.getId(), localAddress.getStateId(), localAddress.getCityId(), localAddress.getIsDefaultAddress());
				globalAddressList.add(globalAddress);

			}
		}

		// adding user to address and roles
		addUpdateUsersAddress(localEntityManager, httpRequest, globalEntityManager, globalAddressList, globalUser);
	//	addUpdateUsersToRoles(httpRequest, globalEntityManager, globalRoles, globalUser.getId(), accountId);

		// add this user to local database
		// users to roles and users to location will be supplied by
		// client only, we dont need to manage it

		localUser.setCountryId(globalUser.getCountryId());
		EmployeeMaster employeeMaster = null;
		if (localUser.getEmployeeMaster() != null)
		{
			employeeMaster = localUser.getEmployeeMaster();
		}

		localUser = localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, localAddressList,  httpRequest);
		// managing employee master for tip
		localUser.setEmployeeMaster(addUpdateEmployeeMaster(localEntityManager, employeeMaster, localUser, request));

		// get local user usersToLocations set
		Set<UsersToLocation> usersToLocationSet = localUser.getUsersToLocations();

		// if we have more than one,then manage all
		if (usersToLocationSet != null && usersToLocationSet.size() > 1)
		{
			for (UsersToLocation usersToLocation : usersToLocationSet)
			{
				int bussinessId = getBusinessIdUsingLocationId(localEntityManager, usersToLocation.getLocationsId());
				manageUsersToBuissness(globalEntityManager, bussinessId, globalUser.getId(), localUser);
			}

		}
		else
		{
			// set users to location, needed for customer
			// application
			manageUsersToBuissness(globalEntityManager, getBusinessIdUsingLocationId(localEntityManager, locationId), globalUser.getId(), localUser);
		}
		addUsersToAccount(globalEntityManager, accountId, globalUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy());

		// prepare response
		UserManagementObj userManagementObj = new UserManagementObj();
		userManagementObj.setResponse(MessageConstants.MSG_USER_ADDED_GLOBAL_LOCAL_DB);
		userManagementObj.setUser(localUser);

		return userManagementObj;

	}

	/**
	 * @param em
	 * @param o
	 * @param user
	 * @return employee master object
	 * @throws Exception
	 */
	private EmployeeMaster addUpdateEmployeeMaster(EntityManager em, EmployeeMaster o, com.nirvanaxp.types.entities.user.User user, HttpServletRequest request) throws Exception
	{

		if (o != null)
		{
			o.setUserId(user.getId());
			// checking any active entity is present in database for given user
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
				logger.severe("No result Found for Employee Master-" + o.getUserId());
			}

			// adding employee master if not exist in db
			if (o.getId() == null)
			{
				o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				o.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				// get global location
				Location global = new CommonMethods().getBaseLocation(em);
				if (o.getId() == null)
					o.setId(new StoreForwardUtility().generateDynamicIntId(em, global.getId(), request, "users"));
				o = em.merge(o);

			}
			// updating existing employee master entry in db
			else
			{
				o.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				o = em.merge(o);

			}

		}

		// checing transactions
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

		return o;

	}

	/**
	 * This method adds user to local database and global database only if user
	 * does not exists in global database
	 * 
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public UserManagementObj addNirvavaXPUserToGlobalAndLocalDatabase(HttpServletRequest httpRequest, EntityManager globalEntityManager, EntityManager localEntityManager,
			com.nirvanaxp.types.entities.user.User localUser, String locationId, Set<com.nirvanaxp.types.entities.address.Address> localAddressList, Set<UsersToRole> globalRoles,PostPacket packet) throws Exception
	{

		UserManagementObj userManagementObj = new UserManagementObj();

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		LocalUserUtil localUserUtil = new LocalUserUtil();
		// check if user exits in global database or not
		User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, globalEntityManager, localUser.getPhone(), localUser.getEmail(), localUser.getUsername(),
				localUser.getGlobalUsersId());
		// User globalUser = globalUserUtil.getSearchedGlobalUser();
		if (globalUserCheck == null)
		{
			// search in local db for given user details
			com.nirvanaxp.types.entities.user.User localUserCheck = localUserUtil.findLocalUserByPhoneOrEmailOrUserName(localEntityManager, localUser.getPhone(), localUser.getEmail(),
					localUser.getUsername());
			if (localUserCheck == null)
			{

				if (localUser.getAuthPin() != null && localUser.getAuthPin().length() > 0)
				{
					// checking auth pin duplicacy
					boolean doesUserExistsWithAuthPin = localUserUtil.doesUserExistsWithAuthPin(httpRequest, localEntityManager, localUser.getAuthPin(), null);
					if (doesUserExistsWithAuthPin)
					{
						String duplicateAuthPinMsg = MessageConstants.MSG_USER_ALREADY_EXISTS_WITH_SAME_AUTH_PIN;
						// return the object saying that user already exists
						userManagementObj.setResponse(duplicateAuthPinMsg);
						return userManagementObj;
					}
				}

				userManagementObj.setResponse(MessageConstants.MSG_USER_ADDED_GLOBAL_LOCAL_DB);
				// adding global user from local user
				User globalUser = addGlobalUserUsingLocalUserInfo(globalEntityManager, localEntityManager, localUser, locationId, localUser.getAuthPin(), true, httpRequest, globalRoles,packet);

				Set<Address> globalAddressList = new HashSet<Address>();
				// managing address of user
				for (com.nirvanaxp.types.entities.address.Address localAddress : localAddressList)
				{

					Address globalAddress = new Address(localAddress.getAddress1(), localAddress.getAddress2(), localAddress.getCity(), localAddress.getCountryId(), localAddress.getCreatedBy(),
							localAddress.getFax(), localAddress.getLatValue(), localAddress.getLongValue(), localAddress.getPhone(), localAddress.getState(), localAddress.getUpdatedBy(),
							localAddress.getZip(), localAddress.getId(), localAddress.getStateId(), localAddress.getCityId(), localAddress.getIsDefaultAddress());
					globalAddressList.add(globalAddress);

				}

				addUpdateUsersAddress(localEntityManager, httpRequest, globalEntityManager, globalAddressList, globalUser);
				addUpdateUsersToRoles(httpRequest, globalEntityManager, globalRoles, globalUser.getId(), accountId);
				// add this user to local database
				// users to roles and users to location will be supplied by
				// client only, we dont need to manage it
				localUser = localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, localAddressList,  httpRequest);

				// get local user usersToLocations set
				Set<UsersToLocation> usersToLocationSet = localUser.getUsersToLocations();
				// we have more than one,then manage all
				if (usersToLocationSet != null && usersToLocationSet.size() > 1)
				{
					for (UsersToLocation usersToLocation : usersToLocationSet)
					{
						int bussinessId = getBusinessIdUsingLocationId(localEntityManager, usersToLocation.getLocationsId());
						manageUsersToBuissness(globalEntityManager, bussinessId, globalUser.getId(), localUser);
					}

				}
				else
				{
					// set users to location, needed for customer
					// application
					manageUsersToBuissness(globalEntityManager, getBusinessIdUsingLocationId(localEntityManager, locationId), globalUser.getId(), localUser);
				}
				addUsersToAccount(globalEntityManager, accountId, globalUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy());
				userManagementObj.setUser(localUser);
				localUser.setGlobalUsersId(globalUser.getId());

			}
			else
			{
				userManagementObj.setResponse("User already exist in local database");
			}
		}
		else
		{
			userManagementObj.setResponse("User already exist in global database");
		}
		return userManagementObj;

	}

	/**
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param globalUserId
	 * @param locationId
	 * @param usersToRolesSet
	 * @return its adds global user to local database and returns local user
	 *         object
	 * @throws Exception
	 */
	public com.nirvanaxp.types.entities.user.User addGlobalUserToLocalDatabaseIfNotExixts(EntityManager globalEntityManager, EntityManager localEntityManager, String globalUserId, String locationId,
			Set<com.nirvanaxp.types.entities.user.UsersToRole> usersToRolesSet, HttpServletRequest request) throws Exception
	{

		UserManagementObj userManagementObj = new UserManagementObj();

		// check if this user exists is local database or not
		LocalUserUtil localUserUtil = new LocalUserUtil();
		com.nirvanaxp.types.entities.user.User user = localUserUtil.getLocalUserByGlobalUserId(localEntityManager, globalUserId);

		// if exists return the user for local user
		if (user != null)
		{

			// check if this user has users to locations and users to role
			// maintained for this location too or not, if not maintain it

			com.nirvanaxp.types.entities.roles.Role customerRoleForLocation = localUserUtil.getCustomerRolesIdForlocalDatabase(localEntityManager, locationId);
			if (customerRoleForLocation != null && customerRoleForLocation.getId() != null)
			{
				addUsersToRolesIfNotAddedForUserAsCustomer(localEntityManager, user, locationId, customerRoleForLocation.getId(), user.getCreatedBy(), user.getUpdatedBy(), localUserUtil, request);
			}

			addUsersToLocationIfNotAddedForUserAsCustomer(localEntityManager, user, locationId, user.getCreatedBy(), user.getUpdatedBy());

			return user;
		}
		else
		{

			// get the global user object from global database
			GlobalUserUtil globalUserUtil = new GlobalUserUtil();
			User globalUser = globalUserUtil.getGlobalUserById(globalEntityManager, globalUserId);
			int countryId = globalUser.getCountryId();
			int businessId = getBusinessIdUsingLocationId(localEntityManager, locationId);
			accountId = getAccountIdUsingBuissnessId(globalEntityManager, businessId);

			// check if user has to be added as customer role
			// below code would also handle users to bussiness and users to
			// account

			if (usersToRolesSet == null || usersToRolesSet.size() == 0)
			{

				com.nirvanaxp.types.entities.user.User localUser = addGlobalUserToLocalDatabase(globalEntityManager, localEntityManager, userManagementObj, businessId, globalUser, localUserUtil,
						locationId, true, null, countryId, request);
				return localUser;
			}
			else
			{
				// user needs to be given specific role
				com.nirvanaxp.types.entities.user.User localUser = addGlobalUserToLocalDatabase(globalEntityManager, localEntityManager, userManagementObj, businessId, globalUser, localUserUtil,
						locationId, true, usersToRolesSet, countryId, request);
				return localUser;
			}
		}

	}

	/**
	 * @param httpRequest
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param localUser
	 * @param isPasswordUpdate
	 * @param localAddressSet
	 * @return
	 * @throws Exception
	 */
	public UserManagementObj updateAdminUser(HttpServletRequest httpRequest, EntityManager globalEntityManager, EntityManager localEntityManager, com.nirvanaxp.types.entities.user.User localUser,
			boolean isPasswordUpdate, Set<com.nirvanaxp.types.entities.address.Address> localAddressSet,   HttpServletRequest request,int isLocalServerURL) throws Exception
	{

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		LocalUserUtil localUserUtil = new LocalUserUtil();

		// check if phone or email is duplicate or not
		com.nirvanaxp.types.entities.user.User existingUser = localUserUtil.findLocalUserByPhoneOrEmailOrUserName(localEntityManager, localUser.getPhone(), localUser.getEmail(), null);
		
		if (existingUser != null && !existingUser.getId().equals(localUser.getId()))
		{
			logger.severe(existingUser.getId() +""+localUser.getId());
			UserManagementObj userManagementObj = new UserManagementObj();
			userManagementObj.setResponse("User exists in database already for provided phone or email.");
			return userManagementObj;
		}

		// check if password has been updated or not
		if (isPasswordUpdate )
		{
			if(isLocalServerURL==0){
			// encrypt the password
			String sha512Password = null;
			sha512Password = DigestUtils.sha512Hex(localUser.getPassword().getBytes(Charset.forName("UTF-8")));
			localUser.setPassword(sha512Password);
			}
			
		}
		// updating existing password
		else
		{
			com.nirvanaxp.types.entities.user.User searchedLocalUser = localUserUtil.getLocalUserByGlobalUserId(localEntityManager, localUser.getGlobalUsersId());
			localUser.setPassword(searchedLocalUser.getPassword());
		}

		// fetching user from database
		com.nirvanaxp.types.entities.user.User tempUser = localEntityManager.find(com.nirvanaxp.types.entities.user.User.class, localUser.getId());
		if (tempUser != null)
		{
			localUser.setIsAllowedLogin(tempUser.getIsAllowedLogin());
		}

		Set<Address> globalAddressList = new HashSet<Address>();

		for (com.nirvanaxp.types.entities.address.Address localAddress : localAddressSet)
		{

			Address globalAddress = new Address(localAddress.getAddress1(), localAddress.getAddress2(), localAddress.getCity(), localAddress.getCountryId(), localAddress.getCreatedBy(),
					localAddress.getFax(), localAddress.getLatValue(), localAddress.getLongValue(), localAddress.getPhone(), localAddress.getState(), localAddress.getUpdatedBy(),
					localAddress.getZip(), localAddress.getId(), localAddress.getStateId(), localAddress.getCityId(), localAddress.getIsDefaultAddress());
			globalAddressList.add(globalAddress);
		}

		Set<com.nirvanaxp.types.entities.address.Address> localAddressList = localUser.getAddressesSet();

		// update local user
		EmployeeMaster employeeMaster = null;
		if (localUser.getEmployeeMaster() != null)
		{
			employeeMaster = localUser.getEmployeeMaster();
		}

		localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, localAddressList,  httpRequest);

		localUser.setEmployeeMaster(addUpdateEmployeeMaster(localEntityManager, employeeMaster, localUser, httpRequest));

		User globalUser = globalUserUtil.getGlobalUserById(globalEntityManager, localUser.getGlobalUsersId());

		Set<UsersToRole> newerRolesSet = new HashSet<UsersToRole>();
		if (localUser.getUsersToRoles() != null)
		{
			for (com.nirvanaxp.types.entities.user.UsersToRole userToRole : localUser.getUsersToRoles())
			{

				if (userToRole.getPrimaryRoleInd() != null && userToRole.getPrimaryRoleInd().trim().length() > 0)
				{
					// add this role only if status of not D for this
					// record, as in that case user has deleted that role
					// from local db, hence we need to apply the same on
					// global db as well
					if (userToRole.getStatus() != null && userToRole.getStatus().trim().length() > 0 && userToRole.getStatus().equals("D") == false)
					{

						UsersToRole role = getUserToRolesFromGlobalDatabase(globalEntityManager, globalUser.getId(), Integer.parseInt(userToRole.getPrimaryRoleInd().trim()));
						if (role == null)
						{
							UsersToRole toRole = new UsersToRole();
							toRole.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							toRole.setRolesId(Integer.parseInt(userToRole.getPrimaryRoleInd()));
							toRole.setCreatedBy(userToRole.getCreatedBy());
							toRole.setUpdatedBy(userToRole.getUpdatedBy());
							toRole.setUsersId(globalUser.getId());
							toRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
//							if (toRole.getId() == null)
//							{
//								toRole.setId(new StoreForwardUtilityForGlobal().generateDynamicIntId(globalEntityManager, httpRequest, "users_to_roles"));
//
//							}
							newerRolesSet.add(toRole);

						}
						else
						{
							role.setUpdatedBy(userToRole.getUpdatedBy());
							role.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							role.setRolesId(Integer.parseInt(userToRole.getPrimaryRoleInd()));
							role.setUsersId(globalUser.getId());
							newerRolesSet.add(role);
						}
					}
					else
					{
						UsersToRole role = getUserToRolesFromGlobalDatabase(globalEntityManager, globalUser.getId(), Integer.parseInt(userToRole.getPrimaryRoleInd().trim()));
						if (role != null)
						{
							globalEntityManager.remove(role);
						}
					}

				}
			}

		}
		addUpdateUsersToRoles(httpRequest, globalEntityManager, newerRolesSet, globalUser.getId(), 0);
		if (localAddressSet != null)
		{
			globalUser.setAddressesSet(globalAddressList);
		}

		// also check for usersToLocations for this user and manage
		// accordingly

		Set<UsersToLocation> usersToLocations = localUser.getUsersToLocations();
		if (usersToLocations != null && usersToLocations.size() > 0)
		{
			boolean isBusinessActive = false;
			Set<Business> bussinessSet = new HashSet<Business>();
			for (UsersToLocation usersToLocation : usersToLocations)
			{
				if (usersToLocation != null && (usersToLocation.getStatus().equals("D") == false) && (usersToLocation.getStatus().equals("I") == false))
				{
					int bussinessId = getBusinessIdUsingLocationId(localEntityManager, usersToLocation.getLocationsId());
					Business business = new Business();
					business.setId(bussinessId);
					bussinessSet.add(business);
					isBusinessActive = true;

				}

			}
			// deleting user session By Apoorva Chourasiya so that deleted user
			// cannot do any activity
			if (!isBusinessActive || localUser.getStatus().equals("I"))
			{
				String queryString = "delete from user_session  where user_id = ? ";
				Query query = globalEntityManager.createNativeQuery(queryString).setParameter(1, globalUser.getId());

				try
				{
					GlobalSchemaEntityManager.executeNativeQueryUpdate(globalEntityManager, query);

				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e, "Could not remove any existing user session for global user: " + globalUser.getId());
				}
			}
			globalUser.setBusinessesSet(bussinessSet);
		}

		List<UsersToDiscount> usersToDiscount = localUser.getUsersToDiscounts();
		if (usersToDiscount != null && usersToDiscount.size() > 0)
		{
			localEntityManager.persist(usersToDiscount);
		}

		// now update global user also

		globalUser.updateUser(localUser.getCreatedBy(), localUser.getPassword(), localUser.getStatus(), localUser.getUpdatedBy(), localUser.getUsername(), localUser.getAuthPin(),
				localUser.getFirstName(), localUser.getLastName(), localUser.getPhone(), localUser.getEmail(), localUser.getDateofbirth());

		globalUserUtil.addUpdateGlobalUser(globalEntityManager, globalUser);

		UserManagementObj userManagementObj = new UserManagementObj();
		userManagementObj.setUser(localUser);
		userManagementObj.setResponse(MessageConstants.MSG_USER_UPDATED_GLOBAL_LOCAL_DB);
		return userManagementObj;

	}

	/**
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param userManagementObj
	 * @param businessId
	 * @param globalUser
	 * @param localUserUtil
	 * @param locationId
	 * @param isCustomerRole
	 * @param localUser_UsersToRoles
	 * @param countryId
	 * @return
	 * @throws Exception
	 */
	private com.nirvanaxp.types.entities.user.User addGlobalUserToLocalDatabase(EntityManager globalEntityManager, EntityManager localEntityManager, UserManagementObj userManagementObj,
			int businessId, User globalUser, LocalUserUtil localUserUtil, String locationId, boolean isCustomerRole, Set<com.nirvanaxp.types.entities.user.UsersToRole> localUser_UsersToRoles,
			int countryId, HttpServletRequest request) throws Exception
	{

		com.nirvanaxp.types.entities.user.User localUser = new com.nirvanaxp.types.entities.user.User(globalUser.getCreatedBy(), globalUser.getDateofbirth(), globalUser.getEmail(),
				globalUser.getAuthPin(), globalUser.getFirstName(), globalUser.getLastName(), globalUser.getPassword(), globalUser.getPhone(), "A", globalUser.getUpdatedBy(), globalUser.getUsername(),
				globalUser.getId(), countryId);
		localUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		localUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// if not exists, copy the values from global database and insert
		// into local database
		// add this user to local database
		if (localUser_UsersToRoles != null && localUser_UsersToRoles.size() > 0)
		{
			localUser.setUsersToRoles(localUser_UsersToRoles);
		}

		Set<com.nirvanaxp.types.entities.address.Address> localAddressList = new HashSet<com.nirvanaxp.types.entities.address.Address>();

		for (Address globalAddress : globalUser.getAddressesSet())
		{

			com.nirvanaxp.types.entities.address.Address localAddress = new com.nirvanaxp.types.entities.address.Address(globalAddress.getAddress1(), globalAddress.getAddress2(),
					globalAddress.getCity(), globalAddress.getCountryId(), globalAddress.getCreatedBy(), globalAddress.getFax(), globalAddress.getLatValue(), globalAddress.getLongValue(),
					globalAddress.getPhone(), globalAddress.getState(), globalAddress.getUpdatedBy(), globalAddress.getZip(), null, globalAddress.getStateId(), globalAddress.getCityId());
			localAddressList.add(localAddress);

		}

		localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, localAddressList,  request);
		if (isCustomerRole)
		{
			localUserUtil.addCustomerRoleForUserId(request, localEntityManager, localUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy(), locationId);
		}
		manageUsersToLocation(localEntityManager, locationId, localUser);
		// set users to location, needed for customer application
		manageUsersToBuissness(globalEntityManager, businessId, globalUser.getId(), localUser);
		addUsersToAccount(globalEntityManager, accountId, globalUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy());

		userManagementObj.setUser(localUser);
		return localUser;
	}

	/**
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @return
	 * @throws Exception
	 */
	public com.nirvanaxp.types.entities.user.User addUserToLocalDatabaseProgramatically(HttpServletRequest httpRequest, EntityManager globalEntityManager, EntityManager localEntityManager,
			com.nirvanaxp.types.entities.user.User localUser, String locationId,PostPacket packet) throws Exception
	{

		String username = null;
		if (localUser.getUsername() != null)
		{
			username = localUser.getUsername();
		}
		if (username == null || localUser.getUsername().trim().length() == 0)
		{
			if (localUser.getPhone() != null && localUser.getPhone().length() > 0)
			{
				username = localUser.getPhone();
			}
			else
			{
				username = localUser.getEmail();
			}
		}
		// finding users in db by phone,username,email
		LocalUserUtil localUserUtil = new LocalUserUtil();
		com.nirvanaxp.types.entities.user.User localUserCheck = localUserUtil.findLocalUserByPhoneOrEmailOrUserName(localEntityManager, localUser.getPhone(), localUser.getEmail(), username);

		// if user already exists return the user details, else add the
		// user.
		if (localUserCheck != null)
		{

			// check if this user has users to locations and users to role
			// maintained for this location too or not, if not maintain it
			localUser.setId(localUserCheck.getId());
			com.nirvanaxp.types.entities.roles.Role customerRoleForLocation = localUserUtil.getCustomerRolesIdForlocalDatabase(localEntityManager, locationId);
			if (customerRoleForLocation != null && customerRoleForLocation.getId() != null)
			{
				addUsersToRolesIfNotAddedForUserAsCustomer(localEntityManager, localUser, locationId, customerRoleForLocation.getId(), localUserCheck.getCreatedBy(), localUserCheck.getUpdatedBy(),
						localUserUtil, httpRequest);
			}

			addUsersToLocationIfNotAddedForUserAsCustomer(localEntityManager, localUser, locationId, localUserCheck.getCreatedBy(), localUserCheck.getUpdatedBy());

			return localUserCheck;

		}

		// else proceed to check further if user exists in global or not

		// check if user exits in global database or not
		GlobalUserUtil globalUserUtil = new GlobalUserUtil();

		User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, globalEntityManager, localUser.getPhone(), localUser.getEmail(), username, null);

		int businessId = getBusinessIdUsingLocationId(localEntityManager, locationId);
		if (globalUserCheck != null)
		{
			if (globalUserCheck.getFirstName() == null)
			{
				globalUserCheck.setFirstName("");
			}
			if (globalUserCheck.getLastName() == null)
			{
				globalUserCheck.setLastName("");
			}
		}
		// user is not existing, SO creating new
		if (globalUserCheck == null)
		{

			// send auth-pin null as this is customer for this local
			// database
			User globalUser = addGlobalUserUsingLocalUserInfo(globalEntityManager, localEntityManager, localUser, locationId, null, true, httpRequest, null,packet);

			// assign customer role
			globalUserUtil.addCustomerRoleForUserId(httpRequest, globalEntityManager, globalUser.getId(), globalUser.getCreatedBy(), globalUser.getUpdatedBy(), accountId,globalUser);
			localUser.setUsername(globalUser.getUsername());
			localUser.setGlobalUsersId(globalUser.getId());
		}
		else
		{
			// user must have existed , hence do business mapping
			// add user to locations business and account relationship too

			accountId = getAccountIdUsingBuissnessId(globalEntityManager, businessId);

			// put all global details into local user, override that info
			localUser.setPassword(globalUserCheck.getPassword());
			localUser.setGlobalUsersId(globalUserCheck.getId());
			localUser.setCountryId(globalUserCheck.getCountryId());
			localUser.setEmail(globalUserCheck.getEmail());
			localUser.setPhone(globalUserCheck.getPhone());
			localUser.setFirstName(globalUserCheck.getFirstName());
			localUser.setLastName(globalUserCheck.getLastName());
			localUser.setDateofbirth(globalUserCheck.getDateofbirth());
			localUser.setUsername(globalUserCheck.getUsername());
		}

		// add this user to local database
		localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, null,  httpRequest);
		;

		// get the user customer role
		localUserUtil.addCustomerRoleForUserId(httpRequest, localEntityManager, localUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy(), locationId);

		manageUsersToLocation(localEntityManager, locationId, localUser);

		// set users to location, needed for customer application
		manageUsersToBuissness(globalEntityManager, businessId, localUser.getGlobalUsersId(), localUser);

		addUsersToAccount(globalEntityManager, accountId, localUser.getGlobalUsersId(), localUser.getCreatedBy(), localUser.getUpdatedBy());

		return localUser;

	}

	/**
	 * @param httpRequest
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @param localAddressSet
	 * @return
	 * @throws Exception
	 */
	public UserManagementObj addCustomer(HttpServletRequest httpRequest, EntityManager globalEntityManager, EntityManager localEntityManager, com.nirvanaxp.types.entities.user.User localUser,
			String locationId, Set<com.nirvanaxp.types.entities.address.Address> localAddressSet,PostPacket packet) throws Exception
	{

		UserManagementObj userManagementObj = new UserManagementObj();
		String username = localUser.getUsername();
		if (username == null || username.trim().length() == 0)
		{
			if (localUser.getPhone() != null && localUser.getPhone().trim().length() > 0)
			{
				username = localUser.getPhone();
			}
			else
			{
				username = localUser.getEmail();
			}
		}
		// finding user in local db
		LocalUserUtil localUserUtil = new LocalUserUtil();
		com.nirvanaxp.types.entities.user.User localUserCheck = localUserUtil.findLocalUserByPhoneOrEmailOrUserName(localEntityManager, localUser.getPhone(), localUser.getEmail(), username);

		// if user already exists return the user details, else add the
		// user.
		if (localUserCheck != null)
		{
			userManagementObj.setResponse("Already added user");
			userManagementObj.setUser(localUserCheck);

			return userManagementObj;
		}

		// else proceed to check further if user exists in global or not

		// check if user exits in global database or not
		GlobalUserUtil globalUserUtil = new GlobalUserUtil();

		User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, globalEntityManager, localUser.getPhone(), localUser.getEmail(), username, null);
		// User globalUser = globalUserUtil.getSearchedGlobalUser();
		int buissnessId = getBusinessIdUsingLocationId(localEntityManager, locationId);
		accountId = getAccountIdUsingBuissnessId(globalEntityManager, buissnessId);
		if (globalUserCheck == null)
		{
			userManagementObj.setResponse(MessageConstants.MSG_USER_ADDED_GLOBAL_LOCAL_DB);

			// send auth-pin null as this is customer for this local
			// database
			Set<UsersToRole> newSet = new HashSet<UsersToRole>();
		
			
			User globalUser = addGlobalUserUsingLocalUserInfo(globalEntityManager, localEntityManager, localUser, locationId, null, true, httpRequest, newSet,packet);
			localUser.setId(globalUser.getId());
			localUser.setGlobalUsersId(globalUser.getId());
			// assign customer role
			
			globalUserUtil.addCustomerRoleForUserId(httpRequest, globalEntityManager, globalUser.getId(), globalUser.getCreatedBy(), globalUser.getUpdatedBy(), accountId,globalUser);

			// this user has no address set associated hence add the
			// mentioned address for user

			Set<Address> globalAddressList = new HashSet<Address>();
			if (localAddressSet != null)
			{
				for (com.nirvanaxp.types.entities.address.Address localAddress : localAddressSet)
				{

					Address globalAddress = new Address(localAddress.getAddress1(), localAddress.getAddress2(), localAddress.getCity(), localAddress.getCountryId(), localAddress.getCreatedBy(),
							localAddress.getFax(), localAddress.getLatValue(), localAddress.getLongValue(), localAddress.getPhone(), localAddress.getState(), localAddress.getUpdatedBy(),
							localAddress.getZip(), localAddress.getId(), localAddress.getStateId(), localAddress.getCityId(), localAddress.getIsDefaultAddress());
					Location l = new CommonMethods().getBaseLocation(localEntityManager);
					globalAddress.setId(new StoreForwardUtility().generateDynamicBigIntId(localEntityManager, locationId, httpRequest, "address"));
					globalAddressList.add(globalAddress);

				}
			}
			if (globalUser.getAddressesSet() == null || globalUser.getAddressesSet().size() == 0)
			{
				if (globalAddressList != null && globalAddressList.size() > 0)
				{
					// Set<Address> globalAdressSet = globalAddressList;

					globalUser.setAddressesSet(globalAddressList);
					if (globalUser.getCreated() == null)
						globalUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					if(globalUser.getId()==null){
						globalUser.setId(new StoreForwardUtility().generateUUID());
					}
					GlobalSchemaEntityManager.merge(globalEntityManager, globalUser);
					try
					{
						boolean result = new CommonMethods().createQRCodeForUser(globalEntityManager, globalUser, httpRequest);
					}
					catch (Exception e)
					{
						logger.severe(e);
					}
				}
			}
			else
			{
				globalAddressList = globalUser.getAddressesSet();
			}
			// this is global user object
			userManagementObj.setAddressSet(globalUser.getAddressesSet());
			localUser.setUsername(globalUser.getUsername());
			localUser.setGlobalUsersId(globalUser.getId());

		}
		else
		{

			// user must have existed , hence do business mapping
			// add user to locations business and account relationship too

			userManagementObj.setResponse("Added user to local database");
			userManagementObj.setAddressSet(globalUserCheck.getAddressesSet());

			

			// put all global details into local user, override that info
			localUser.setPassword(globalUserCheck.getPassword());
			localUser.setGlobalUsersId(globalUserCheck.getId());
			localUser.setEmail(globalUserCheck.getEmail());
			localUser.setPhone(globalUserCheck.getPhone());
			localUser.setFirstName(globalUserCheck.getFirstName());
			localUser.setLastName(globalUserCheck.getLastName());
			localUser.setDateofbirth(globalUserCheck.getDateofbirth());
			localUser.setUsername(globalUserCheck.getUsername());
			localUser.setCountryId(globalUserCheck.getCountryId());

			if (localAddressSet == null)

				if (globalUserCheck.getAddressesSet() != null)
				{
					localAddressSet = new HashSet<com.nirvanaxp.types.entities.address.Address>();
					for (Address globalPreviousAddress : globalUserCheck.getAddressesSet())
					{

						com.nirvanaxp.types.entities.address.Address localAddress = new com.nirvanaxp.types.entities.address.Address(globalPreviousAddress.getAddress1(),
								globalPreviousAddress.getAddress2(), globalPreviousAddress.getCity(), globalPreviousAddress.getCountryId(), globalPreviousAddress.getCreatedBy(),
								globalPreviousAddress.getFax(), globalPreviousAddress.getLatValue(), globalPreviousAddress.getLongValue(), globalPreviousAddress.getPhone(),
								globalPreviousAddress.getState(), globalPreviousAddress.getUpdatedBy(), globalPreviousAddress.getZip(), null, globalPreviousAddress.getStateId(),
								globalPreviousAddress.getCityId());
						if (localAddress.getId() == null)
						{
							localAddress.setId(new StoreForwardUtility().generateDynamicBigIntId(localEntityManager, locationId, httpRequest, "address"));
						}
						localAddressSet.add(localAddress);

					}
				}
		}

		// add this user to local database
		localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, localAddressSet,  httpRequest);

		// get the user customer role
		localUserUtil.addCustomerRoleForUserId(httpRequest, localEntityManager, localUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy(), locationId);

		manageUsersToLocation(localEntityManager, locationId, localUser);

		// set users to location, needed for customer application
		manageUsersToBuissness(globalEntityManager, buissnessId, localUser.getGlobalUsersId(), localUser);

		addUsersToAccount(globalEntityManager, accountId, localUser.getGlobalUsersId(), localUser.getCreatedBy(), localUser.getUpdatedBy());

		userManagementObj.setUser(localUser);
		return userManagementObj;
	}

	/**
	 * @param globalEntityManager
	 * @param buissnessId
	 * @param globalUserId
	 * @param localUser
	 */
	private void manageUsersToBuissness(EntityManager globalEntityManager, int buissnessId, String globalUserId, com.nirvanaxp.types.entities.user.User localUser)
	{
		UsersToBusiness usersToBusiness = new UsersToBusiness(localUser.getCreatedBy(), buissnessId, localUser.getUpdatedBy(), globalUserId);

		usersToBusiness.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		usersToBusiness.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		GlobalSchemaEntityManager.persist(globalEntityManager, usersToBusiness);

	}

	/**
	 * @param localEntityManager
	 * @param locationId
	 * @param localUser
	 */
	private void manageUsersToLocation(EntityManager localEntityManager, String locationId, com.nirvanaxp.types.entities.user.User localUser)
	{
		UsersToLocation usersToLocation = getUserToLocation(localEntityManager, localUser.getId(), locationId);
		if(usersToLocation==null){
		usersToLocation = new UsersToLocation("A", localUser.getCreatedBy(), locationId, localUser.getCreatedBy(), localUser.getId());
		usersToLocation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		}
		usersToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		EntityTransaction tx = localEntityManager.getTransaction();
		try
		{
			// start transaction
			if (tx.isActive())
			{
				localEntityManager.persist(usersToLocation);
			}
			else
			{
				tx.begin();
				localEntityManager.persist(usersToLocation);
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

	/**
	 * @param localUser
	 */
	private void setSqlUniqueConstraint(com.nirvanaxp.types.entities.user.User localUser)
	{
		// set email to null, if blank or empty string is passed, otherwise user
		// will not be added due to unique constraint
		if (localUser.getEmail() != null && localUser.getEmail().trim().length() == 0)
		{
			localUser.setEmail(null);
		}

		// set phone to null, if blank or empty string is passed, otherwise user
		// will not be added due to unique constraint
		if (localUser.getPhone() != null && localUser.getPhone().trim().length() == 0)
		{
			localUser.setPhone(null);
		}

		// null auth pin must go not blank
		if (localUser.getAuthPin() != null && localUser.getAuthPin().trim().length() == 0)
		{
			localUser.setAuthPin(null);
		}
	}

	/**
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @param authPin
	 * @param shouldEncryptPassword
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws WriterException
	 */
	/**
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @param authPin
	 * @param shouldEncryptPassword
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private User addGlobalUserUsingLocalUserInfo(EntityManager globalEntityManager, EntityManager localEntityManager, com.nirvanaxp.types.entities.user.User localUser, String locationId, String authPin,
			boolean shouldEncryptPassword, HttpServletRequest request, Set<UsersToRole> globalRoles,PostPacket packet) throws Exception
	{

		String username = null;
		if (localUser != null && localUser.getUsername() != null && localUser.getUsername().trim().length() > 0)
		{
			username = localUser.getUsername();
		}
		if (username == null || username.trim().length() == 0)
		{
			boolean isUsernameSet = false;
			if (localUser.getPhone() != null && localUser.getPhone().trim().length() > 0)
			{
				isUsernameSet = true;
				username = localUser.getPhone();
			}
			// if username is not set yet
			if (localUser.getEmail() != null && localUser.getEmail().trim().length() > 0 && isUsernameSet == false)
			{
				username = localUser.getEmail();

			}
		}
		Set<Role> rolesSet = new HashSet<Role>();
		if(globalRoles!=null){
			for (UsersToRole usersToRole : globalRoles)
			{
				
				CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
				CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
				Root<Role> role = criteria.from(Role.class);
				TypedQuery<Role> query = globalEntityManager.createQuery(criteria.select(role).where(builder.and(builder.equal(role.get(Role_.id), usersToRole.getRolesId()))));

				Role r = query.getSingleResult();
				rolesSet.add(r);
			}
		}
		
		int buissnessId = getBusinessIdUsingLocationId(localEntityManager, locationId);
		accountId = getAccountIdUsingBuissnessId(globalEntityManager, buissnessId);

		// generate and encrypt the password
		String sha512Password = null;
		if (shouldEncryptPassword && packet.getLocalServerURL()==0 )
		{
			if (localUser.getPassword() != null)
			{
				sha512Password = DigestUtils.sha512Hex(localUser.getPassword().getBytes(Charset.forName("UTF-8")));
			}
			else
			{
				localUser.setPassword("");
				sha512Password = "";
			}
		}
		else
		{
			if (localUser.getPassword() != null)
			{
				sha512Password = localUser.getPassword();
			}
			// setting password as blank in case of null
			else
			{
				localUser.setPassword("");
				sha512Password = "";
			}

		}

		localUser.setPassword(sha512Password);
		setSqlUniqueConstraint(localUser);

		User globalUser = new User(localUser.getCreatedBy(), localUser.getPassword(), "A", localUser.getUpdatedBy(), username.toUpperCase(), authPin, localUser.getFirstName(), localUser.getLastName(),
				localUser.getPhone(), localUser.getEmail(), localUser.getDateofbirth(), localUser.getCountryId());
		if (localUser.getGlobalUsersId() != null && localUser.getGlobalUsersId().length() > 0)
		{
			globalUser.setId(localUser.getGlobalUsersId());
		}

		globalUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		globalUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if(rolesSet!=null && rolesSet.size()>0){
			globalUser.setRolesSet(rolesSet);
		}
		globalUser.setUsername(username.toUpperCase());
		EntityTransaction tx = globalEntityManager.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			if (globalUser.getId() == null || globalUser.getId().equals("0") || globalUser.getId().equals(0))
			{
				String userId = new StoreForwardUtility().generateUUID();
				globalUser.setId(userId);
			}

			globalUser = globalEntityManager.merge(globalUser);
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

		try
		{
			boolean result = new CommonMethods().createQRCodeForUser(globalEntityManager, globalUser, request);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			logger.severe(e);
		}

		localUser.setGlobalUsersId(globalUser.getId());
		localUser.setPassword(sha512Password);
		localUser.setUsername(username.toUpperCase());

		logger.info("global user created, username is", globalUser.getUsername());

		return globalUser;
	}

	/**
	 * @param globalEntityManager
	 * @param buissnessId
	 * @return
	 */
	private int getAccountIdUsingBuissnessId(EntityManager globalEntityManager, int buissnessId)
	{

		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<Business> criteria = builder.createQuery(Business.class);
		Root<Business> business = criteria.from(Business.class);

		TypedQuery<Business> query = globalEntityManager.createQuery(criteria.select(business).where(builder.equal(business.get(Business_.id), buissnessId)));
		Business buissness = query.getSingleResult();

		accountId = buissness.getAccountId();
		return accountId;

	}

	/**
	 * @param localEntityManager
	 * @param locationId
	 * @return
	 */
	private int getBusinessIdUsingLocationId(EntityManager localEntityManager, String locationId)
	{
		// get location which will have buizness id

		CriteriaBuilder builder = localEntityManager.getCriteriaBuilder();
		CriteriaQuery<Location> cl = builder.createQuery(Location.class);
		Root<Location> l = cl.from(Location.class);
		TypedQuery<Location> query = localEntityManager.createQuery(cl.select(l).where(builder.and(builder.equal(l.get(Location_.id), locationId))));
		Location location = query.getSingleResult();

		return location.getBusinessId();

	}

	/**
	 * @param globalEntityManager
	 * @param accountId
	 * @param globalUserId
	 * @param createdBy
	 * @param updatedBy
	 */
	private void addUsersToAccount(EntityManager globalEntityManager, int accountId, String globalUserId, String createdBy, String updatedBy)
	{

		UsersToAccount usersToAccount = new UsersToAccount(accountId, createdBy, updatedBy, globalUserId);
		usersToAccount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		usersToAccount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		GlobalSchemaEntityManager.persist(globalEntityManager, usersToAccount);

	}

	/**
	 * @param globalEntityManager
	 * @param addressesList
	 * @param user
	 * @throws Exception
	 */
	private void addUpdateUsersAddress(EntityManager em, HttpServletRequest request, EntityManager globalEntityManager, Set<Address> addressesList, User user) throws Exception
	{
		List<String> idsOfAddedAddress = new ArrayList<String>();
		// update this user address information
		if (addressesList != null)
		{
			// adding address of iser in global address
			for (Address address : addressesList)
			{

				boolean isNewelyAddedAddress = false;

				address.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (address.getId() == null)
				{
					isNewelyAddedAddress = true;
					// add new address
					address.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					if (address.getId() == null)
						address.setId(new StoreForwardUtilityForGlobal().generateDynamicBigIntId(globalEntityManager, request, "address"));

					GlobalSchemaEntityManager.persist(globalEntityManager, address);
				}
				else
				{
					// update already added address
					GlobalSchemaEntityManager.merge(globalEntityManager, address);
				}

				if (isNewelyAddedAddress)
				{
					idsOfAddedAddress.add(address.getId());
				}

			}

		}

		for (String addressId : idsOfAddedAddress)
		{
			// inserting user to address
			UsersToAddress usersToAddress = new UsersToAddress(addressId, user.getUpdatedBy(), user.getUpdatedBy(), user.getId());
			usersToAddress.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToAddress.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			GlobalSchemaEntityManager.persist(globalEntityManager, usersToAddress);

		}

	}

	/**
	 * @param globalEntityManager
	 * @param usersToRolesSet
	 * @param userId
	 * @param accountId
	 * @throws Exception
	 */
	private void addUpdateUsersToRoles(HttpServletRequest request, EntityManager globalEntityManager, Set<UsersToRole> usersToRolesSet, String userId, int accountId) throws Exception
	{
		if (usersToRolesSet != null)
		{
			for (UsersToRole usersToRole : usersToRolesSet)
			{
				usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

				if (usersToRole.getId() == 0)
				{
					// usersToRole.setId(new
					// StoreForwardUtilityForGlobal().generateDynamicIntId(globalEntityManager,
					// request, "users_to_roles"));
					usersToRole.setUsersId(userId);
					usersToRole.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					//usersToRole.setId(new StoreForwardUtility().generateUUID());
					GlobalSchemaEntityManager.persist(globalEntityManager, usersToRole);
				}
				else
				{
					GlobalSchemaEntityManager.merge(globalEntityManager, usersToRole);
				}

			}
		}
	}

	/**
	 * @param globalEntityManager
	 * @param globalUserId
	 * @return
	 * @throws Exception
	 */
	public List<Address> getAddressByGlobalUserId(EntityManager globalEntityManager, String globalUserId) throws Exception
	{

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		return globalUserUtil.getAddressByGlobalUserId(globalEntityManager, globalUserId);

	}

	/**
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @param customerRolesId
	 * @param createdBy
	 * @param updatedBy
	 * @param localUserUtil
	 * @throws Exception
	 */
	private void addUsersToRolesIfNotAddedForUserAsCustomer(EntityManager localEntityManager, com.nirvanaxp.types.entities.user.User localUser, String locationId, String customerRolesId, String createdBy,
			String updatedBy, LocalUserUtil localUserUtil, HttpServletRequest httpRequest) throws Exception
	{
		if (localUser != null)
		{
			boolean isCustomerRoleAssignedForLocation = false;

			// user has already some role, check if he has role of the new
			// location id that we want
			if (localUser.getUsersToRoles() != null && localUser.getUsersToRoles().size() > 0)
			{

				for (com.nirvanaxp.types.entities.user.UsersToRole usersToRole : localUser.getUsersToRoles())
				{
					// roles are location specific, hence this client needs to
					// be customer of this location already

					if (usersToRole.getRolesId() == customerRolesId)
					{

						if (usersToRole.getStatus() != null && usersToRole.getStatus().trim().length() > 0 && usersToRole.getStatus().equals("D") == false)
						{

						}
						else
						{
							// status could be D, hence change the startus to A
							usersToRole.setStatus("A");
							usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							if (usersToRole.getId() == null)
							{
								Location l = new CommonMethods().getBaseLocation(localEntityManager);
								usersToRole.setId(new StoreForwardUtility().generateDynamicIntId(localEntityManager, l.getId(), httpRequest, "users_to_roles"));
							}
							LocalSchemaEntityManager.merge(localEntityManager, usersToRole);

						}
						isCustomerRoleAssignedForLocation = true;
						break;
					}
				}

			}
			// user does not have customer role yet for this location
			if (isCustomerRoleAssignedForLocation == false)
			{

				// we need to add customer role for this location and merge this
				// user into database

				localUserUtil.addCustomerRoleForUserId(httpRequest, localEntityManager, localUser.getId(), createdBy, updatedBy, locationId);

			}
		}
	}

	/**
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @param createdBy
	 * @param updatedBy
	 */
	private void addUsersToLocationIfNotAddedForUserAsCustomer(EntityManager localEntityManager, com.nirvanaxp.types.entities.user.User localUser, String locationId, String createdBy, String updatedBy)
	{
		if (localUser != null)
		{

			boolean isUserToLocationAssignedForLocation = false;

			// user has already some role, check if he has role of the new
			// location id that we want

			if (localUser.getUsersToLocations() != null && localUser.getUsersToLocations().size() > 0)
			{

				for (com.nirvanaxp.types.entities.user.UsersToLocation usersToLocation : localUser.getUsersToLocations())
				{
					// roles are location specific, hence this client needs to
					// be customer of this location already
					if (usersToLocation.getLocationsId() == locationId)
					{

						if (usersToLocation.getStatus() != null && usersToLocation.getStatus().trim().length() > 0 && usersToLocation.getStatus().equals("D") == false)
						{

						}
						else
						{
							// status could be D, hence change the startus to A
							usersToLocation.setStatus("A");
							usersToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

							EntityTransaction tx = localEntityManager.getTransaction();
							try
							{
								// start transaction
								tx.begin();
								localEntityManager.merge(usersToLocation);
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
						isUserToLocationAssignedForLocation = true;
						break;
					}
				}

			}
			// user does not have customer role yet for this location
			if (isUserToLocationAssignedForLocation == false)
			{

				// we need to add customer role for this location and merge this
				// user into database
				manageUsersToLocation(localEntityManager, locationId, localUser);
			}
		}
	}

	/**
	 * @param globalEntityManager
	 * @param businessId
	 * @param globalUserId
	 * @param primaryRolesIdSet
	 */
	public void deleteUser(EntityManager globalEntityManager, int businessId, String globalUserId, List<String> primaryRolesIdSet)
	{

		com.nirvanaxp.global.types.entities.User globaUser = globalEntityManager.find(com.nirvanaxp.global.types.entities.User.class, globalUserId);
		Set<Business> businesseSet = globaUser.getBusinessesSet();
		if (businesseSet != null)
		{
			for (Business business : businesseSet)
			{
				if (business.getId() == businessId)
				{
					accountId = business.getAccountId();
					businesseSet.remove(business);
				}
			}
		}

		if (businesseSet == null || businesseSet.size() == 0)
		{
			// remove account relation if not bussiness is accessible to
			// user
			Set<Account> accountsSet = globaUser.getAccountsSet();
			if (accountsSet != null && accountsSet.size() > 0 && accountId != 0)
			{
				for (Account account : accountsSet)
				{
					if (account.getId() == accountId)
					{
						accountsSet.remove(account);
					}
				}
			}

		}

		// remove user to roles also
		Set<com.nirvanaxp.global.types.entities.Role> roleSet = globaUser.getRolesSet();
		if (primaryRolesIdSet != null && primaryRolesIdSet.size() > 0)
		{
			for (String globalRolesId : primaryRolesIdSet)
			{
				com.nirvanaxp.global.types.entities.Role roleToRemove = new com.nirvanaxp.global.types.entities.Role(Integer.parseInt(globalRolesId.trim()));
				if (roleSet.contains(roleToRemove))
				{
					roleSet.remove(roleToRemove);
				}
			}

		}
		globaUser.setStatus("D");

		GlobalSchemaEntityManager.merge(globalEntityManager, globaUser);

	}

	/**
	 * @param localEM
	 * @param globalEM
	 * @param localAddressSet
	 * @param userId
	 * @param globalUsersId
	 * @throws Exception
	 */
	public Set<com.nirvanaxp.types.entities.address.Address> updateAddressInfoInGlobalAndLocalDb(HttpServletRequest request, EntityManager localEM, EntityManager globalEM,
			Set<com.nirvanaxp.types.entities.address.Address> localAddressSet, String userId, String globalUsersId) throws Exception
	{

		List<Integer> idsOfAddedAddress = new ArrayList<Integer>();

		Set<Address> globalAddressList = new HashSet<Address>();
		Set<com.nirvanaxp.types.entities.address.Address> localAddressList = new HashSet<com.nirvanaxp.types.entities.address.Address>();

		if (localAddressSet != null)
		{
			for (com.nirvanaxp.types.entities.address.Address localAddress : localAddressSet)
			{

				// doing for local address
				Address globalAddress = new Address(localAddress.getAddress1(), localAddress.getAddress2(), localAddress.getCity(), localAddress.getCountryId(), localAddress.getCreatedBy(),
						localAddress.getFax(), localAddress.getLatValue(), localAddress.getLongValue(), localAddress.getPhone(), localAddress.getState(), localAddress.getUpdatedBy(),
						localAddress.getZip(), localAddress.getId(), localAddress.getStateId(), localAddress.getCityId(), localAddress.getIsDefaultAddress());

				if (globalAddress.getId() == null || globalAddress.getId().equals(BigInteger.ZERO))
				{

					globalAddress.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					globalAddress.setId(new StoreForwardUtilityForGlobal().generateDynamicBigIntId(globalEM, request, "address"));
					GlobalSchemaEntityManager.persist(globalEM, globalAddress);

					UsersToAddress usersToAddress = new UsersToAddress(globalAddress.getId(), globalUsersId, globalUsersId, globalUsersId);
					usersToAddress.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToAddress.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					GlobalSchemaEntityManager.persist(globalEM, usersToAddress);
				}
				globalAddressList.add(globalAddress);

				// updating for local
				localAddress.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (localAddress.getId() == null ||  localAddress.getId().equals(BigInteger.ZERO))
				{

					// add new address
					localAddress.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					// updating previous address to 0 default address
					// added by Apoorva 29593 Multiple address get selected when
					// select one address.IOS
					updateIsDefaultWhenNewAddressAdded(localEM, userId);
					if (localAddress.getId() == null || localAddress.getId().equals(BigInteger.ZERO))
					{
						Location l = new CommonMethods().getBaseLocation(localEM);
						if (localAddress.getId() == null || localAddress.getId().equals(BigInteger.ZERO))
							localAddress.setId(new StoreForwardUtility().generateDynamicBigIntId(localEM, l.getId(), request, "address"));
					}
					LocalSchemaEntityManager.merge(localEM, localAddress);
					com.nirvanaxp.types.entities.user.UsersToAddress usersToAddressLocal = new com.nirvanaxp.types.entities.user.UsersToAddress(localAddress.getId(), userId, userId, userId);
					usersToAddressLocal.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					usersToAddressLocal.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

					LocalSchemaEntityManager.merge(localEM, usersToAddressLocal);

					// localEM.persist(address);
				}
				else
				{
					try
					{
						localEM.getTransaction().begin();
						localAddress = localEM.merge(localAddress);
						localEM.getTransaction().commit();
					}
					catch (RuntimeException e)
					{
						// on error, if transaction active, rollback
						if (localEM.getTransaction() != null && localEM.getTransaction().isActive())
						{
							localEM.getTransaction().rollback();
						}
						throw e;
					}
					// localEM.merge(address);
				}
				localAddressList.add(localAddress);
			}
		}

		return localAddressList;

	}

	/**
	 * @param httpRequest
	 * @param globalEntityManager
	 * @param localEntityManager
	 * @param localUser
	 * @param locationId
	 * @param localAddressSet
	 * @return
	 * @throws Exception
	 */
	public UserManagementObj addUpdateCustomer(HttpServletRequest httpRequest, EntityManager globalEntityManager, EntityManager localEntityManager, com.nirvanaxp.types.entities.user.User localUser,
			String locationId, Set<com.nirvanaxp.types.entities.address.Address> localAddressSet,PostPacket packet) throws Exception
	{

		UserManagementObj userManagementObj = new UserManagementObj();
		String username = localUser.getUsername();
		if (username == null || username.trim().length() == 0)
		{
			if (localUser.getPhone() != null && localUser.getPhone().trim().length() > 0)
			{
				username = localUser.getPhone();
			}
			else
			{
				username = localUser.getEmail();
			}
		}

		LocalUserUtil localUserUtil = new LocalUserUtil();
		com.nirvanaxp.types.entities.user.User localUserCheck = localUserUtil.findLocalUserByPhoneOrEmailOrUserName(localEntityManager, localUser.getPhone(), localUser.getEmail(), username);

		// if user already exists return the user details, else add the
		// user.
		if (localUserCheck == null)
		{

			userManagementObj.setUserExist(1);
			// else proceed to check further if user exists in global or not

			// check if user exits in global database or not
			GlobalUserUtil globalUserUtil = new GlobalUserUtil();

			User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, globalEntityManager, localUser.getPhone(), localUser.getEmail(), username, null);
			// User globalUser = globalUserUtil.getSearchedGlobalUser();
			int buissnessId = getBusinessIdUsingLocationId(localEntityManager, locationId);

			if (globalUserCheck == null)
			{
				userManagementObj.setResponse(MessageConstants.MSG_USER_ADDED_GLOBAL_LOCAL_DB);

				// send auth-pin null as this is customer for this local
				// database
				User globalUser = addGlobalUserUsingLocalUserInfo(globalEntityManager, localEntityManager, localUser, locationId, null, true, httpRequest, null,packet);

				// assign customer role
				globalUserUtil.addCustomerRoleForUserId(httpRequest, globalEntityManager, globalUser.getId(), globalUser.getCreatedBy(), globalUser.getUpdatedBy(), accountId,globalUser);

				// this user has no address set associated hence add the
				// mentioned address for user

				Set<Address> globalAddressList = new HashSet<Address>();
				if (localAddressSet != null)
				{
					for (com.nirvanaxp.types.entities.address.Address localAddress : localAddressSet)
					{

						Address globalAddress = new Address(localAddress.getAddress1(), localAddress.getAddress2(), localAddress.getCity(), localAddress.getCountryId(), localAddress.getCreatedBy(),
								localAddress.getFax(), localAddress.getLatValue(), localAddress.getLongValue(), localAddress.getPhone(), localAddress.getState(), localAddress.getUpdatedBy(),
								localAddress.getZip(), localAddress.getId(), localAddress.getStateId(), localAddress.getCityId(), localAddress.getIsDefaultAddress());
						globalAddressList.add(globalAddress);

					}
				}
				if (globalUser.getAddressesSet() == null || globalUser.getAddressesSet().size() == 0)
				{
					if (globalAddressList != null && globalAddressList.size() > 0)
					{
						// Set<Address> globalAdressSet = globalAddressList;

						globalUser.setAddressesSet(globalAddressList);

						GlobalSchemaEntityManager.merge(globalEntityManager, globalUser);

					}

				}
				else
				{
					globalAddressList = globalUser.getAddressesSet();
				}
				// this is global user object
				userManagementObj.setAddressSet(globalUser.getAddressesSet());
				localUser.setUsername(globalUser.getUsername());
				localUser.setGlobalUsersId(globalUser.getId());

			}
			else
			{

				// user must have existed , hence do business mapping
				// add user to locations business and account relationship too

				userManagementObj.setResponse("Added user to local database");
				userManagementObj.setAddressSet(globalUserCheck.getAddressesSet());

				accountId = getAccountIdUsingBuissnessId(globalEntityManager, buissnessId);

				// put all global details into local user, override that info
				localUser.setPassword(globalUserCheck.getPassword());
				localUser.setGlobalUsersId(globalUserCheck.getId());
				localUser.setEmail(globalUserCheck.getEmail());
				localUser.setPhone(globalUserCheck.getPhone());
				localUser.setFirstName(globalUserCheck.getFirstName());
				localUser.setLastName(globalUserCheck.getLastName());
				localUser.setDateofbirth(globalUserCheck.getDateofbirth());
				localUser.setUsername(globalUserCheck.getUsername());
				localUser.setCountryId(globalUserCheck.getCountryId());
				if (localAddressSet == null)
					localAddressSet = new HashSet<com.nirvanaxp.types.entities.address.Address>();

				if (globalUserCheck.getAddressesSet() != null)
				{
					for (Address globalPreviousAddress : globalUserCheck.getAddressesSet())
					{

						com.nirvanaxp.types.entities.address.Address localAddress = new com.nirvanaxp.types.entities.address.Address(globalPreviousAddress.getAddress1(),
								globalPreviousAddress.getAddress2(), globalPreviousAddress.getCity(), globalPreviousAddress.getCountryId(), globalPreviousAddress.getCreatedBy(),
								globalPreviousAddress.getFax(), globalPreviousAddress.getLatValue(), globalPreviousAddress.getLongValue(), globalPreviousAddress.getPhone(),
								globalPreviousAddress.getState(), globalPreviousAddress.getUpdatedBy(), globalPreviousAddress.getZip(),null, globalPreviousAddress.getStateId(),
								globalPreviousAddress.getCityId());
						localAddressSet.add(localAddress);

					}
				}
			}

			// add this user to local database
			localUserUtil.addLocalUserWithAdress(localEntityManager, localUser, localAddressSet,  httpRequest);

			// get the user customer role
			localUserUtil.addCustomerRoleForUserId(httpRequest, localEntityManager, localUser.getId(), localUser.getCreatedBy(), localUser.getUpdatedBy(), locationId);

			manageUsersToLocation(localEntityManager, locationId, localUser);

			// set users to location, needed for customer application
			manageUsersToBuissness(globalEntityManager, buissnessId, localUser.getGlobalUsersId(), localUser);

			addUsersToAccount(globalEntityManager, accountId, localUser.getGlobalUsersId(), localUser.getCreatedBy(), localUser.getUpdatedBy());
		}
		else
		{
			userManagementObj.setUserExist(2);
			localUser = localUserCheck;
		}
		localUser.setAddressesSet(localAddressSet);
		userManagementObj.setUser(localUser);
		return userManagementObj;
	}

	/**
	 * @param globalEntityManager
	 * @param username
	 * @return returning user object by username
	 */
	public User getUserByUsername(EntityManager globalEntityManager, String username)
	{

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		return globalUserUtil.getUserByUserName(globalEntityManager, username);

	}

	/**
	 * @param globalEntityManager
	 * @param id
	 * @return returns global user by id
	 */
	public User getGlobalUserById(EntityManager globalEntityManager, String id)
	{

		GlobalUserUtil globalUserUtil = new GlobalUserUtil();
		return globalUserUtil.getGlobalUserById(globalEntityManager, id);

	}

	/**
	 * @param em
	 * @param userId
	 * @return boolean value for address update
	 */
	private boolean updateIsDefaultWhenNewAddressAdded(EntityManager em, String userId)
	{
		boolean result = false;
		int count = 0;
		String sql = "update address set is_default_address=0 where id in  (select address_id from users_to_address where users_id = ?)";
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			count = em.createNativeQuery(sql).setParameter(1, userId).executeUpdate();
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
		if (count > 0)
		{
			result = true;
		}

		return result;
	}

	/**
	 * @param globalEM
	 * @param userId
	 * @param roleId
	 * @return returns user to roles from global database
	 */
	private UsersToRole getUserToRolesFromGlobalDatabase(EntityManager globalEM, String userId, int roleId)
	{
		try
		{
			CriteriaBuilder builder = globalEM.getCriteriaBuilder();
			CriteriaQuery<UsersToRole> criteria = builder.createQuery(UsersToRole.class);
			Root<UsersToRole> r = criteria.from(UsersToRole.class);
			TypedQuery<UsersToRole> query = globalEM.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToRole_.usersId), userId), builder.equal(r.get(UsersToRole_.rolesId), roleId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}
	private UsersToLocation getUserToLocation(EntityManager globalEM, String userId, String locationId)
	{
		try
		{
			CriteriaBuilder builder = globalEM.getCriteriaBuilder();
			CriteriaQuery<UsersToLocation> criteria = builder.createQuery(UsersToLocation.class);
			Root<UsersToLocation> r = criteria.from(UsersToLocation.class);
			TypedQuery<UsersToLocation> query = globalEM.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToLocation_.usersId), userId), builder.equal(r.get(UsersToLocation_.locationsId), locationId)));
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return null;
	}
}
