/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.user.utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;

import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.Address_;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.User_;
import com.nirvanaxp.global.types.entities.UsersToAddress;
import com.nirvanaxp.global.types.entities.UsersToAddress_;
import com.nirvanaxp.server.util.NirvanaLogger;

public class GlobalUserUtil
{

	private final static NirvanaLogger logger = new NirvanaLogger(GlobalUserUtil.class.getName());

	/**
	 * @param phoneNumber
	 * @param emailId
	 * @param username
	 * @param userIdDuringUpdate
	 *            - userid when client wants to update user
	 * @return user that already exists in database
	 * @throws Exception
	 */
	public User findGlobalUserByPhoneOrEmailOrUsername(HttpServletRequest httpRequest, EntityManager globalEntityManager, String phoneNumber, String emailId, String username, String userIdDuringUpdate)
	{

		User globalUser = null;

		// search by phone first
		if (phoneNumber != null && phoneNumber.length() > 0)
		{

			// get the user on global that has the same phone number
			globalUser = getGlobalUserByPhoneNumber(globalEntityManager, phoneNumber);

			if (globalUser == null)
			{
				// check if user has supplied username or not, or we need to use
				// phone number as username
				if (username == null || username.trim().length() == 0)
				{
					// now check if there is a user with phone as username
					globalUser = getGlobalUserByUsername(httpRequest, globalEntityManager, phoneNumber);
				}

			}
		}

		// if still not found
		if (globalUser == null)
		{
			// search by email second
			if (emailId != null && emailId.length() > 0)
			{
				// get the user on global that has same emailId
				globalUser = getGlobalUserByEmail(globalEntityManager, emailId);

				if (globalUser == null)
				{
					// if no username is provided, use email as username
					if (username == null || username.trim().length() == 0)
					{
						// now check if there is a user with emailId as username
						globalUser = getGlobalUserByUsername(httpRequest, globalEntityManager, emailId);
					}
				}

			}
		}

		// if still not found
		if (globalUser == null)
		{
			// search by username last
			if (username != null && username.length() > 0)
			{
				globalUser = getGlobalUserByUsername(httpRequest, globalEntityManager, username);
			}
		}

		// if we got a user which has this phone number or email
		// check if this user is same as updateable user
		// or if this method is used for adding a user, the
		// userIdDuringUpdate =0, hence id global userid is found, we
		// must send error back
		if (globalUser != null && (userIdDuringUpdate==null || (userIdDuringUpdate!=null && userIdDuringUpdate != globalUser.getId())))
		{
			return globalUser;
		}

		return null;
	}

	/**
	 * @param globalEM
	 * @param globalPhoneNumber
	 * @return
	 */
	public User getGlobalUserByPhoneNumber(EntityManager globalEM, String globalPhoneNumber)
	{
		CriteriaBuilder builder = globalEM.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEM.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.global.types.entities.User_.phone), globalPhoneNumber)));
		List<User> usersList = query.getResultList();
		if (usersList != null && usersList.size() > 0)
		{
			User result = usersList.get(0);
			return result;
		}
		return null;
	}

	/**
	 * @param globalEntityManager
	 * @param globalEmailId
	 * @return
	 */
	public User getGlobalUserByEmail(EntityManager globalEntityManager, String globalEmailId)
	{
		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.global.types.entities.User_.email), globalEmailId)));
		List<User> usersList = query.getResultList();
		if (usersList != null && usersList.size() > 0)
		{
			User result = usersList.get(0);
			return result;
		}
		return null;
	}

	/**
	 * @param httpRequest
	 * @param globalEntityManager
	 * @param username
	 * @return
	 */
	private User getGlobalUserByUsername(HttpServletRequest httpRequest, EntityManager globalEntityManager, String username)
	{
		// finding global user by username
		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.global.types.entities.User_.username), username)));

		User result = null;
		try
		{
			result = query.getSingleResult();
		}
		catch (NoResultException nre)
		{
			// log
			logger.info(httpRequest, "No User found for username: ", username);
		}
		return result;

	}

	/**
	 * @param globalEntityManager
	 * @param globalUserId
	 * @return
	 */
	public User getGlobalUserById(EntityManager globalEntityManager, String globalUserId)
	{
		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> root = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.global.types.entities.User_.id), globalUserId)));
		User result = query.getSingleResult();
		return result;
	}

	/**
	 * @param globalEntityManager
	 * @param userId
	 * @param createdBy
	 * @param updatedBy
	 * @param accountId
	 * @throws Exception 
	 */
	public User addCustomerRoleForUserId(HttpServletRequest httpRequest,EntityManager globalEntityManager, String userId, String createdBy, String updatedBy, int accountId,User user) throws Exception
	{
		Role role = getCustomerRolesIdForGlobalDatabase(globalEntityManager, accountId);
		Set<Role>  roleSet = user.getRolesSet();
		if(roleSet==null ){
			roleSet = new HashSet<Role>();
		}
		roleSet.add(role);
		user.setRolesSet(roleSet);
		EntityTransaction tx = globalEntityManager.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			user= globalEntityManager.merge(user);
			tx.commit();
			return user;
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
	 * @param globalEntityManager
	 * @param accoutId
	 * @return
	 */
	public Role getCustomerRolesIdForGlobalDatabase(EntityManager globalEntityManager, int accoutId)
	{
		try
		{
			CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
			CriteriaQuery<Role> criteria = builder.createQuery(Role.class);
			Root<Role> root = criteria.from(Role.class);
			TypedQuery<Role> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.global.types.entities.Role_.roleName), "POS Customer"),
					builder.equal(root.get(com.nirvanaxp.global.types.entities.Role_.accountId), accoutId)));
			Role role = query.getSingleResult();
			return role;
		}
		catch (NoResultException noResultException)
		{
			logger.info("no role found for the customer");
			return null;
		}
		catch (Exception e)
		{
			logger.severe(e, "Error getting user by id in global database: ", e.getMessage());
		}
		return null;
	}

	/**
	 * @param globalEntityManager
	 * @param globalUser
	 * @return
	 * @throws Exception
	 */
	public User addUpdateGlobalUser(EntityManager globalEntityManager, User globalUser) throws Exception
	{

		globalUser.setUsername(globalUser.getUsername().toUpperCase());
		EntityTransaction tx = globalEntityManager.getTransaction();
		try
		{
			globalEntityManager.getTransaction().begin();
			globalUser.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if (globalUser.getId()==null)
			{
				globalUser.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				globalEntityManager.persist(globalUser);
			}
			else
			{
				globalEntityManager.merge(globalUser);
			}

			globalEntityManager.getTransaction().commit();
		}
		catch (Exception e)
		{
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive())
			{
				tx.rollback();
			}
			throw e;
		}
		return globalUser;
	}

	/**
	 * @param globalEntityManager
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<Address> getAddressByGlobalUserId(EntityManager globalEntityManager, @PathParam("userId") String userId) throws Exception
	{
		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<UsersToAddress> criteria = builder.createQuery(UsersToAddress.class);
		Root<UsersToAddress> usersToAddress = criteria.from(UsersToAddress.class);
		TypedQuery<UsersToAddress> query = globalEntityManager.createQuery(criteria.select(usersToAddress).where(builder.equal(usersToAddress.get(UsersToAddress_.usersId), userId)));
		List<UsersToAddress> usersToAddressList = query.getResultList();
		List<Address> addressList = new ArrayList<Address>();
		if (usersToAddressList != null)
		{
			for (UsersToAddress usersToAddressObj : usersToAddressList)
			{
				CriteriaQuery<Address> criteriaAddress = builder.createQuery(Address.class);
				Root<Address> addressRoot = criteriaAddress.from(Address.class);
				TypedQuery<Address> addressQuery = globalEntityManager
						.createQuery(criteriaAddress.select(addressRoot).where(builder.equal(addressRoot.get(Address_.id), usersToAddressObj.getAddressId())));
				Address address = addressQuery.getSingleResult();
				addressList.add(address);
			}
		}
		return addressList;

	}

	/**
	 * @param globalEntityManager
	 * @param username
	 * @return
	 */
	public User getUserByUserName(EntityManager globalEntityManager, String username)
	{
		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(r).where(builder.equal(r.get(User_.username), username)));
		User user = query.getSingleResult();
		user.setPassword("");
		user.setAuthPin("");
		return user;
	}

	/**
	 * @param globalEntityManager
	 * @param email
	 * @return
	 */
	public User getUserByEmail(EntityManager globalEntityManager, String email)
	{
		User user = null;
		// todo need to handle exception in below method
		try
		{
			CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> r = criteria.from(User.class);
			TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(r).where(builder.equal(r.get(User_.email), email)));
			user = query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e);
		}
		return user;
	}

}
