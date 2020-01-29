/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.user.utility;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.roles.Role_;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToRole;
import com.nirvanaxp.types.entities.user.UsersToRole_;
import com.nirvanaxp.types.entities.user.UsersToSocialMedia;

public class LocalUserUtil
{

	private static final NirvanaLogger logger = new NirvanaLogger(LocalUserUtil.class.getName());

	/**
	 * @param localUser
	 */
	private void setSqlUniqueConstraint(User localUser)
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
	 * @param localEntityManager
	 * @param phoneNumber
	 * @param emailId
	 * @param username
	 * @return
	 */
	public User findLocalUserByPhoneOrEmailOrUserName(EntityManager localEntityManager, String phoneNumber, String emailId, String username)
	{

		if (phoneNumber != null && phoneNumber.length() > 0)
		{
			// get the user on global that has the same phone number
			try
			{

				User localUser = getLocalUserByPhoneNumber(localEntityManager, phoneNumber);
				// we got a user which has this phone number
				if (localUser != null)
				{
					return localUser;
				}
			}
			catch (NoResultException nre)
			{
				logger.info("No User found when searching by phone:", phoneNumber);
			}

		}

		if (emailId != null && emailId.length() > 0)
		{
			// get the user on global that has same emailId
			try
			{
				User localUser = getLocalUserByEmail(localEntityManager, emailId);
				// we got a user which has this phone number
				if (localUser != null)
				{
					return localUser;
				}
			}
			catch (NoResultException nre)
			{
				logger.info("No User found when searching by Email:", emailId);
			}
		}

		if (username != null && username.length() > 0)
		{

			try
			{
				User localUser = getLocalUserByUsername(localEntityManager, username);
				// we got a user which has this phone number
				if (localUser != null)
				{
					return localUser;
				}
			}
			catch (NoResultException nre)
			{
				logger.info("No User found when searching by username:", username);
			}
		}

		return null;
	}

	/**
	 * @param localEntityManager
	 * @param username
	 * @return
	 */
	private User getLocalUserByUsername(EntityManager localEntityManager, String username)
	{

		CriteriaBuilder builder = localEntityManager.getCriteriaBuilder();
		CriteriaQuery<com.nirvanaxp.types.entities.user.User> criteria = builder.createQuery(com.nirvanaxp.types.entities.user.User.class);
		Root<com.nirvanaxp.types.entities.user.User> root = criteria.from(com.nirvanaxp.types.entities.user.User.class);
		TypedQuery<com.nirvanaxp.types.entities.user.User> query = localEntityManager
				.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.types.entities.user.User_.username), username)));
		com.nirvanaxp.types.entities.user.User result = query.getSingleResult();
		return result;

	}

	/**
	 * @param httpRequest
	 * @param localEntityManager
	 * @param authpin
	 * @param existingUserId
	 * @return
	 */
	public boolean doesUserExistsWithAuthPin(HttpServletRequest httpRequest, EntityManager localEntityManager, String authpin, String existingUserId)
	{

		com.nirvanaxp.types.entities.user.User result = null;
		try
		{
			CriteriaBuilder builder = localEntityManager.getCriteriaBuilder();
			CriteriaQuery<com.nirvanaxp.types.entities.user.User> criteria = builder.createQuery(com.nirvanaxp.types.entities.user.User.class);
			Root<com.nirvanaxp.types.entities.user.User> root = criteria.from(com.nirvanaxp.types.entities.user.User.class);
			TypedQuery<com.nirvanaxp.types.entities.user.User> query = localEntityManager
					.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.types.entities.user.User_.authPin), authpin)));
			result = query.getSingleResult();
		}
		catch (NoResultException e)
		{
			logger.info(httpRequest, "No user found with the specified auth pin");
		}
		// this has come while adding a new user
		if (existingUserId==null)
		{
			if (result != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			// check while updating a user
			if (result != null && result.getId().equals(existingUserId))
			{
				return true;
			}
			else
			{
				return true;
			}
		}

	}

	/**
	 * @param localEntityManager
	 * @param globalPhoneNumber
	 * @return
	 */
	private com.nirvanaxp.types.entities.user.User getLocalUserByPhoneNumber(EntityManager localEntityManager, String globalPhoneNumber)
	{

		String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId " + "and r.roleName=? and u.phone=?";
		TypedQuery<com.nirvanaxp.types.entities.user.User> query = localEntityManager.createQuery(queryString, com.nirvanaxp.types.entities.user.User.class)
				.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, globalPhoneNumber);
		User result = query.getSingleResult();
		return result;

	}

	/**
	 * @param localEntityManager
	 * @param globalUserId
	 * @return
	 */
	public com.nirvanaxp.types.entities.user.User getLocalUserByGlobalUserId(EntityManager localEntityManager, String globalUserId)
	{

		CriteriaBuilder builder = localEntityManager.getCriteriaBuilder();
		CriteriaQuery<com.nirvanaxp.types.entities.user.User> criteria = builder.createQuery(com.nirvanaxp.types.entities.user.User.class);
		Root<com.nirvanaxp.types.entities.user.User> root = criteria.from(com.nirvanaxp.types.entities.user.User.class);
		TypedQuery<com.nirvanaxp.types.entities.user.User> query = localEntityManager
				.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.types.entities.user.User_.globalUsersId), globalUserId)));
		com.nirvanaxp.types.entities.user.User result = query.getSingleResult();
		return result;

	}

	/**
	 * @param localEntityManager
	 * @param globalEmailId
	 * @return
	 */
	private com.nirvanaxp.types.entities.user.User getLocalUserByEmail(EntityManager localEntityManager, String globalEmailId)
	{

		String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId " + "and r.roleName=? and u.email like ? ";
		TypedQuery<com.nirvanaxp.types.entities.user.User> query = localEntityManager.createQuery(queryString, com.nirvanaxp.types.entities.user.User.class)
				.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, globalEmailId);
		User result = query.getSingleResult();
		return result;
	}

	/**
	 * @param em
	 * @param localUser
	 * @param addressSet
	 * @return
	 * @throws Exception 
	 */
	public com.nirvanaxp.types.entities.user.User addLocalUserWithAdress(EntityManager em, com.nirvanaxp.types.entities.user.User localUser, Set<Address> addressSet,HttpServletRequest request) throws Exception
	{

		setSqlUniqueConstraint(localUser);
		Set<Address> newAddrs = new HashSet();
		if(addressSet!=null && addressSet.size()>0){
			for(Address add:addressSet){
				if(add.getId()==null || add.getId().equals(BigInteger.ZERO)){
					Location l = new CommonMethods().getBaseLocation(em);
							
					add.setId(new StoreForwardUtility().generateUUID());
				}
				newAddrs.add(add);
			}
			localUser.setAddressesSet(newAddrs);
		}
		
		localUser.setUsername(localUser.getUsername().toUpperCase());
		
		EntityTransaction tx = em.getTransaction();
		try
		{
			if (tx.isActive())
			{
				localUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (localUser.getId()==null)
				{
					localUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					localUser.setId(new StoreForwardUtility().generateUUID());
					localUser = em.merge(localUser);
					tx.commit();
					tx.begin();
					 
				}
				else
				{
					localUser =em.merge(localUser);
					tx.commit();
					tx.begin();
				}
			}
			else
			{
				tx.begin();
				localUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (localUser.getId()==null)
				{
					localUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					localUser.setId(new StoreForwardUtility().generateUUID());	
					em.persist(localUser);
					tx.commit();
					tx.begin();
				}
				else
				{
					
					Set<UsersToRole> usersToRoles = localUser.getUsersToRoles();
					Set<UsersToRole> usersToRolesSet = new HashSet<UsersToRole>();
					if(usersToRoles!=null){
						for(UsersToRole usersToRole:usersToRoles){
							if(usersToRole.getUsersId()==null){
								usersToRole.setUsersId(localUser.getId());
							}
							Location l = new CommonMethods().getBaseLocation(em);
							if(usersToRole.getId()==null){
								usersToRole.setId(new StoreForwardUtility().generateUUID());
							}
							usersToRole = em.merge(usersToRole);
							usersToRolesSet.add(usersToRole);
						}
						localUser.setUsersToRoles(usersToRolesSet);
					}
					
					
					Set<UsersToLocation> usersToLocations= localUser.getUsersToLocations();
					Set<UsersToLocation> usersToLocationSet = new HashSet<UsersToLocation>();
					if(usersToLocations!=null){
						for(UsersToLocation usersToLocati:usersToLocations){
							if(usersToLocati.getUsersId()==null){
								usersToLocati.setUsersId(localUser.getId());
							}
							usersToLocationSet.add(usersToLocati);
						}
						localUser.setUsersToLocations(usersToLocationSet);
					}
					logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+usersToLocationSet.toString());
					logger.severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+localUser.toString());
					localUser = em.merge(localUser);
					tx.commit();
					tx.begin();
				}

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

		return localUser;
	}

	/**
	 * @param localEntityManager
	 * @param usersToRolesSet
	 * @param usersToLocationsSet
	 * @param usersToSocialMedias
	 * @param userId
	 * @throws Exception 
	 */
	public void updateUserRelationshipTables(HttpServletRequest request,EntityManager localEntityManager, Set<UsersToRole> usersToRolesSet, Set<UsersToLocation> usersToLocationsSet, Set<UsersToSocialMedia> usersToSocialMedias,
			String userId) throws Exception
	{
		addUpdateUsersToRole(request,localEntityManager, usersToRolesSet, userId);
		addUpdateUsersToLocation(localEntityManager, usersToLocationsSet, userId);
		addUpdateUsersToSocilaMedia(localEntityManager, usersToSocialMedias, userId);
	}

	/**
	 * @param localEntityManager
	 * @param usersToRolesSet
	 * @param userId
	 * @throws Exception 
	 */
	private void addUpdateUsersToRole(HttpServletRequest request, EntityManager localEntityManager, Set<UsersToRole> usersToRolesSet, String userId) throws Exception
	{
		if (usersToRolesSet != null)
		{
			for (UsersToRole usersToRole : usersToRolesSet)
			{
				usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (usersToRole.getId() == null)
				{
					if(usersToRole.getId()==null){
						Location l = new CommonMethods().getBaseLocation(localEntityManager);
						usersToRole.setId(new StoreForwardUtility().generateDynamicIntId(localEntityManager, l.getId(), request, "users_to_roles"));
					}
					usersToRole.setUsersId(userId);
					usersToRole.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					localEntityManager.persist(usersToRole);
				}
				else
				{
					localEntityManager.merge(usersToRole);
				}

				localEntityManager.getTransaction().commit();
			}
		}
	}

	/**
	 * @param localEntityManager
	 * @param usersToLocationsSet
	 * @param userId
	 */
	private void addUpdateUsersToLocation(EntityManager localEntityManager, Set<UsersToLocation> usersToLocationsSet, String userId)
	{
		if (usersToLocationsSet != null)
		{
			for (UsersToLocation usersToLocation : usersToLocationsSet)
			{
				usersToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (usersToLocation.getId() == 0)
				{
					usersToLocation.setUsersId(userId);
					usersToLocation.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					localEntityManager.persist(usersToLocation);
				}
				else
				{
					localEntityManager.merge(usersToLocation);
				}

				localEntityManager.getTransaction().commit();
			}
		}
	}

	/**
	 * @param localEntityManager
	 * @param usersToSocialMedias
	 * @param userId
	 */
	private void addUpdateUsersToSocilaMedia(EntityManager localEntityManager, Set<UsersToSocialMedia> usersToSocialMedias, String userId)
	{
		if (usersToSocialMedias != null)
		{
			for (UsersToSocialMedia usersToSocialMediaObj : usersToSocialMedias)
			{
				usersToSocialMediaObj.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				if (usersToSocialMediaObj.getId() == 0)
				{
					usersToSocialMediaObj.setUsersId(userId);
					usersToSocialMediaObj.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
					localEntityManager.persist(usersToSocialMediaObj);
				}
				else
				{
					localEntityManager.merge(usersToSocialMediaObj);
				}

				localEntityManager.getTransaction().commit();
			}
		}
	}

	/**
	 * @param localEntityManager
	 * @param userId
	 * @param createdBy
	 * @param updatedBy
	 * @param locationId
	 * @throws Exception 
	 */
	public void addCustomerRoleForUserId(HttpServletRequest httpRequest,EntityManager localEntityManager, String userId, String createdBy, String updatedBy, String locationId) throws Exception
	{
		Role role = null;
		// todo need to handle exception in below method
		try
		{
			role = getCustomerRolesIdForlocalDatabase(localEntityManager, locationId);
		}
		catch (Exception e1)
		{
			logger.severe(e1);
			logger.severe("No roles present for - " + locationId + " \n" + e1);

		}
		if (role != null && role.getId() != null)
		{
			UsersToRole usersToRole =getUsersToRoleByUserIdAndRoleId(localEntityManager, role.getId(), userId);
			if(usersToRole==null){
			 usersToRole = new UsersToRole(createdBy, "A", role.getId(), updatedBy, userId);
			
			usersToRole.setPrimaryRoleInd("" + role.getGlobalRoleId());
			usersToRole.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if(usersToRole.getUsersId()==null){
				usersToRole.setUsersId(userId);
			}

			EntityTransaction tx = localEntityManager.getTransaction();
			try
			{
				// start transaction
				if (tx.isActive())
				{
					if(usersToRole.getId()==null){
						Location l = new CommonMethods().getBaseLocation(localEntityManager);
						usersToRole.setId(new StoreForwardUtility().generateDynamicIntId(localEntityManager, l.getId(), httpRequest, "users_to_roles"));
					}
					usersToRole =localEntityManager.merge(usersToRole);

				}
				else
				{
					tx.begin();
					if(usersToRole.getId()==null){
						Location l = new CommonMethods().getBaseLocation(localEntityManager);
						usersToRole.setId(new StoreForwardUtility().generateDynamicIntId(localEntityManager, l.getId(), httpRequest, "users_to_roles"));
					}
					usersToRole =localEntityManager.merge(usersToRole);
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
	}

	/**
	 * @param localEntityManager
	 * @param locationId
	 * @return
	 */
	public Role getCustomerRolesIdForlocalDatabase(EntityManager localEntityManager, String locationId)
	{

		CriteriaBuilder builder = localEntityManager.getCriteriaBuilder();
		CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
		Root<Role> root = criteria.from(Role.class);
		TypedQuery<Role> query = localEntityManager
				.createQuery(criteria.select(root).where(builder.equal(root.get(Role_.roleName), "POS Customer"), builder.equal(root.get(Role_.locationsId), locationId)));
		Role role = query.getSingleResult();
		return role;

	}
	public UsersToRole getUsersToRoleByUserIdAndRoleId(EntityManager localEntityManager, String roleId,String userId)
	{

		UsersToRole usersToRole=null;
		try
		{
			CriteriaBuilder builder = localEntityManager.getCriteriaBuilder();
			CriteriaQuery<UsersToRole> criteria = builder.createQuery(UsersToRole.class);
			Root<UsersToRole> root = criteria.from(UsersToRole.class);
			TypedQuery<UsersToRole> query = localEntityManager
					.createQuery(criteria.select(root).where(builder.equal(root.get(UsersToRole_.usersId), userId), builder.equal(root.get(UsersToRole_.rolesId), roleId)));
			usersToRole = query.getSingleResult();
		}
		catch (Exception e)
		{
			 logger.severe(e);
		}
		return usersToRole;

	}

}
