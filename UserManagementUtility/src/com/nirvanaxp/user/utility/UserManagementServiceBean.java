/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.user.utility;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
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
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.roles.Role;
import com.nirvanaxp.types.entities.user.User;
import com.nirvanaxp.types.entities.user.User_;
import com.nirvanaxp.types.entities.user.UsersToDiscount;
import com.nirvanaxp.types.entities.user.UsersToLocation;
import com.nirvanaxp.types.entities.user.UsersToLocation_;
import com.nirvanaxp.types.entities.user.UsersToPayment;
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;
import com.nirvanaxp.types.entities.user.UsersToPayment_;
import com.nirvanaxp.types.entities.user.UsersToRole;
import com.nirvanaxp.types.entities.user.UsersToSocialMedia;

/**
 * Session Bean implementation class UserManagementServiceBean
 */
public class UserManagementServiceBean { // implements IUserManagementService {

	private EntityManager em = null;
	private HttpServletRequest httpRequest;

	private static final NirvanaLogger logger = new NirvanaLogger(UserManagementServiceBean.class.getName());

	public UserManagementServiceBean(HttpServletRequest httpRequest, EntityManager em) {
		this.httpRequest = httpRequest;
		this.em = em;
	}

	void validate(User user) throws ValidationException {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

		if (constraintViolations != null && !constraintViolations.isEmpty()) {
			StringBuilder b = new StringBuilder();

			for (ConstraintViolation<User> constraintViolation : constraintViolations) {
				String str = constraintViolation.getPropertyPath() + " -> " + constraintViolation.getMessage();
				b.append(str + "; ");
			}

			logger.severe(httpRequest, b.toString());
			throw new ValidationException(b.toString());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.IUserManagementService#createUser(com.
	 * posnirvana.types.entities.User)
	 */
	// @Override
	User add(User user,HttpServletRequest  request) throws Exception {

		// validate
		validate(user);

		// upper the username
		user.setUsername(user.getUsername().toUpperCase());
		user.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			// get global location
			Location global = new CommonMethods().getBaseLocation(em);
			if(user.getId()==null){
				user.setId(new StoreForwardUtility().generateUUID());
			}
			user= em.merge(user);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return user;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.IUserManagementService#createUser(com.
	 * posnirvana.types.entities.User)
	 */
	// @Override
	public String addUserFeedback(User user) {

		// int uid = user.getId();

		User u = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());

		u.setUsersToFeebackDetail(user.getUsersToFeebackDetail());
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			em.merge(u);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return "1";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.IUserManagementService#createUser(com.
	 * posnirvana.types.entities.User)
	 */
	// @Override
	User addLocalUser(User user,HttpServletRequest request) throws Exception {
		// validate
		validate(user);

		Set<UsersToRole> usersToRolesSet = user.getUsersToRoles();
		Set<UsersToLocation> userLocationsSet = user.getUsersToLocations();
		Set<UsersToSocialMedia> usersToSocialMediasSet = user.getUsersToSocialMedias();

		// set null, so that when we add user, we do not get any exception
		// due to wrong relatipship
		user.setUsersToRoles(null);
		user.setUsersToLocations(null);
		user.setUsersToSocialMedias(null);

		// now add user
		addOrUpdateUser(user,request);

		addUsersToRolesSet(usersToRolesSet, user);
		addUsersToLocation(userLocationsSet, user);
		addUsersToSocialMedia(usersToSocialMediasSet, user);

		user.setUsersToRoles(usersToRolesSet);
		user.setUsersToLocations(userLocationsSet);
		user.setUsersToSocialMedias(usersToSocialMediasSet);
		return user;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.IUserManagementService#updateUser(com.
	 * posnirvana.types.entities.User)
	 */
	// @Override
	User update(User user,HttpServletRequest request) throws Exception {
		// first get user
		// User u = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());

		Set<UsersToRole> usersToRolesSet = user.getUsersToRoles();
		Set<UsersToLocation> userLocationsSet = user.getUsersToLocations();
		Set<UsersToSocialMedia> usersToSocialMediasSet = user.getUsersToSocialMedias();

		addUsersToRolesSet(usersToRolesSet, null);
		addUsersToLocation(userLocationsSet, null);
		addUsersToSocialMedia(usersToSocialMediasSet, null);

		user.setUsersToRoles(null);
		user.setUsersToLocations(null);
		user.setUsersToSocialMedias(null);

		addOrUpdateUser(user,request);

		user.setUsersToRoles(usersToRolesSet);
		user.setUsersToLocations(userLocationsSet);
		user.setUsersToSocialMedias(usersToSocialMediasSet);

		return user;

	}

	private void addOrUpdateUser(User user,HttpServletRequest request) throws Exception {
		// check if password changed
		if (!user.getPassword().equals(user.getPassword())) {
			// update user's password
			String sha512Password = null;
			sha512Password = DigestUtils.sha512Hex(user.getPassword().getBytes(Charset.forName("UTF-8")));
			user.setPassword(sha512Password);
		}

		// upper the username
		user.setUsername(user.getUsername().toUpperCase());
		user.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		// now add user
		EntityTransaction tx = em.getTransaction();

		try {
			// start transaction
			tx.begin();
			if (user.getId()==null) {
				user.setId(new StoreForwardUtility().generateUUID());
				user= em.merge(user);
			} else {
				em.merge(user);
			}

			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}
	}

	private void addUsersToRolesSet(Set<UsersToRole> usersToRolesSet, User user) throws Exception {
		if (usersToRolesSet != null) {
			for (UsersToRole usersToRole : usersToRolesSet) {

				// check if user is send to method, then this userid must be set
				if (user != null) {
					usersToRole.setUsersId(user.getId());
				}
				usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				EntityTransaction tx = em.getTransaction();

				try {
					// start transaction
					tx.begin();
					if (usersToRole.getId() == null) {
						Location l = new CommonMethods().getBaseLocation(em);
						usersToRole.setId(new StoreForwardUtility().generateDynamicIntId(em, l.getId(), httpRequest, "users_to_roles"));
						em.persist(usersToRole);
					} else {
						em.merge(usersToRole);
					}

					tx.commit();
				} catch (RuntimeException e) {
					// on error, if transaction active,
					// rollback
					if (tx != null && tx.isActive()) {
						tx.rollback();
					}
					throw e;
				}

			}
		}

	}

	private void addUsersToLocation(Set<UsersToLocation> userLocationsSet, User user) {
		if (userLocationsSet != null) {

			for (UsersToLocation usersToLocation : userLocationsSet) {

				if (user != null) {
					usersToLocation.setUsersId(user.getId());
				}

				usersToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				EntityTransaction tx = em.getTransaction();
				try {
					em.getTransaction().begin();

					if (usersToLocation.getId() == 0) {
						em.persist(usersToLocation);
					} else {
						em.merge(usersToLocation);
					}

					em.getTransaction().commit();
				} catch (RuntimeException e) {
					// on error, if transaction active,
					// rollback
					if (tx != null && tx.isActive()) {
						tx.rollback();
					}
					throw e;
				}

			}
		}
	}

	private void addUsersToSocialMedia(Set<UsersToSocialMedia> usersToSocialMediasSet, User user) {
		if (usersToSocialMediasSet != null) {

			for (UsersToSocialMedia usersToSocialMedia : usersToSocialMediasSet) {

				if (usersToSocialMedia != null) {
					usersToSocialMedia.setUsersId(user.getId());
				}

				usersToSocialMedia.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				EntityTransaction tx = em.getTransaction();
				try {
					em.getTransaction().begin();

					if (usersToSocialMedia.getId() == 0) {
						em.persist(usersToSocialMedia);
					} else {
						em.merge(usersToSocialMedia);
					}

					em.getTransaction().commit();
				} catch (RuntimeException e) {
					// on error, if transaction active,
					// rollback
					if (tx != null && tx.isActive()) {
						tx.rollback();
					}
					throw e;
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.IUserManagementService#deleteUser(com.
	 * posnirvana.types.entities.User)
	 */
	// @Override
	User delete(User user) {
		User userFromDb = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());

		// UserRelationHelper userRelationHelper = new UserRelationHelper();
		userFromDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		userFromDb.setUpdatedBy(user.getUpdatedBy());
		EntityTransaction tx = em.getTransaction();
		try {
			// start transaction
			tx.begin();
			userFromDb.setStatus("D");
			em.merge(userFromDb);
			tx.commit();
		} catch (RuntimeException e) {
			// on error, if transaction active,
			// rollback
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			throw e;
		}

		return userFromDb;

	}

	// @Override
	public User getUserByUserName(String username) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(User_.username), username)));
		User result = (User) query.getSingleResult();
		return result;
	}

	// @Override
	@SuppressWarnings("unchecked")
	public User getUserById(String id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(User_.id), id)));
		User result=null;
		try
		{
			result = (User) query.getSingleResult();
			
			try
			{

				String queryString1 = "select b from UsersToDiscount b where b.usersId= ?";
				List<UsersToDiscount> resultSet = null;

				Query query1 = em.createQuery(queryString1).setParameter(1, result.getId());
				resultSet = query1.getResultList();
				if (resultSet != null)
				{
					result.setUsersToDiscounts(resultSet);
				}
				

			}
			catch (Exception e)
			{
				logger.severe("No Users To Discount Found for User Id " + result.getId());
			}
		}
		catch (Exception e)
		{
			logger.severe(e);
		}

		
		return result;

	}

	// @Override
	public List<User> getAllUsers() {

		CriteriaQuery<User> criteria = em.getCriteriaBuilder().createQuery(User.class);
		criteria.select(criteria.from(User.class));
		List<User> ListOfEmailDomains = em.createQuery(criteria).getResultList();
		return ListOfEmailDomains;

	}

	// @Override
	public List<User> getUserByFirstName(String username) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em
				.createQuery(criteria.select(r).where(builder.like(r.get(User_.firstName), username + "%")));
		List<User> result = query.getResultList();
		return result;

	}

	// @Override
	public List<User> getUserByLastName(String username) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em
				.createQuery(criteria.select(r).where(builder.like(r.get(User_.lastName), username + "%")));
		List<User> result = query.getResultList();
		return result;

	}

	// @Override
	/**
	 * @param email
	 * @return
	 */
	public com.nirvanaxp.global.types.entities.User getGlobleUserByEmail(String email) {

		String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId "
				+ "and r.roleName=? and u.email like ? ";
		TypedQuery<com.nirvanaxp.global.types.entities.User> query = em
				.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class)
				.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, email);
		com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
		return result;
	}

	// @Override
	public static List<User> getUserByLocationsId(EntityManager em, String locationsId) {
		List<User> usersList = new ArrayList<User>();
		String queryString = "SELECT u.first_name,u.last_name,u.id,u.username FROM users u, users_to_locations ul where u.id=ul.users_id and ul.locations_id=?";
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, locationsId).getResultList();
		for (Object[] objRow : resultList) {
			// if this has primary key not 0
			User user = new User();
			user.setFirstName((String) objRow[0]);
			user.setLastName((String) objRow[1]);
			user.setUsername((String) objRow[2]);

			usersList.add(user);
		}

		return usersList;
	}

	// @Override
	public List<User> getUserByAuthPin(String authPin) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(User_.authPin), authPin.trim())));
		List<User> result = query.getResultList();
		return result;

	}

	// @Override
	public User getUserByGlobalUsersId(String globalUsersId) {

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(User_.globalUsersId), globalUsersId)));

		return query.getSingleResult();

	}

	/**
	 * @param phoneNo
	 * @return
	 */
	public com.nirvanaxp.global.types.entities.User getGlobleUserByPhoneNo(String phoneNo) {

		// CriteriaBuilder builder = em.getCriteriaBuilder();
		// CriteriaQuery<User> criteria = builder.createQuery(User.class);
		// Root<User> r = criteria.from(User.class);
		// create JPQL query
		String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId "
				+ "and r.roleName=? and u.phone=?";
		TypedQuery<com.nirvanaxp.global.types.entities.User> query = em
				.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class)
				.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, phoneNo);
		com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
		return result;
	}

	/**
	 * @param locationIds
	 * @return
	 * @throws Exception
	 */
	public List<User> getAllAdminUsersByLocationIdList(List<String> locationIds) throws Exception {

		List<User> usersList = new ArrayList<User>();

		String whereClause = "";
		for (int i = 0; i < locationIds.size(); i++) {
			if (i == (locationIds.size() - 1)) {
				whereClause += "?";
			} else {
				whereClause += "?,";
			}
		}
		String queryString = "select u.id,u.first_name,u.last_name,u.email,u.phone,u.username from users u left join users_to_roles utr on u.id=utr.users_id "
				+ " left join roles r on r.id=utr.roles_id"
				+ " left join users_to_locations utl on u.id=utl.users_id where utl.locations_id in (?)"
				+ " and  r.role_name != 'POS Customer' group by u.id  ";

		@SuppressWarnings("unchecked")
		List<Object[]> resultList = em.createNativeQuery(queryString).setParameter(1, whereClause).getResultList();
		for (Object[] objRow : resultList) {
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

		return usersList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.IUserManagementService#deleteUser(com.
	 * posnirvana.types.entities.User)
	 */

	public User delete(String sessionId, User user, Location location, String updatedBy) throws Exception {

		try {
			// get location for
			User userFromDb = user;
			Set<UsersToLocation> usersToLocations = userFromDb.getUsersToLocations();
			if (usersToLocations != null && usersToLocations.size() > 0) {
				for (UsersToLocation usersToLocation : usersToLocations) {
					if (usersToLocation != null) {
						if (usersToLocation.getLocationsId() == location.getId()) {
							usersToLocation.setStatus("D");
							usersToLocation.setUpdatedBy(updatedBy);
							usersToLocation.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
						}
					}
				}
			}

			Set<UsersToRole> userToRoles = userFromDb.getUsersToRoles();
			List<String> primaryRolesIdSet = new ArrayList<String>();
			if (userToRoles != null && userToRoles.size() > 0) {
				for (UsersToRole usersToRole : userToRoles) {
					if (usersToRole != null) {
						Role role = (Role) new CommonMethods().getObjectById("Role", em,Role.class, usersToRole.getRolesId());
						if (role.getLocationsId() == location.getId()) {
							primaryRolesIdSet.add(usersToRole.getPrimaryRoleInd());
							usersToRole.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
							usersToRole.setStatus("D");
							usersToRole.setUpdatedBy(updatedBy);
						}
					}
				}
			}

			userFromDb.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			userFromDb.setUpdatedBy(updatedBy);
			userFromDb.setStatus("D");
			EntityTransaction tx = em.getTransaction();
			try {
				// start transaction
				tx.begin();
				em.merge(userFromDb);
				tx.commit();
			} catch (RuntimeException e) {
				// on error, if transaction active,
				// rollback
				if (tx != null && tx.isActive()) {
					tx.rollback();
				}
				throw e;
			}

			int businessId = location.getBusinessId();
			// int accountId = 0;

			GlobalUsermanagement globalUsermanagement = new GlobalUsermanagement();
			EntityManager globalEM = null;
			try {
				globalEM = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

				globalUsermanagement.deleteUser(globalEM, businessId, userFromDb.getGlobalUsersId(), primaryRolesIdSet);
			} finally {
				GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEM);
			}

			return userFromDb;
		} catch (Exception e) {
			logger.severe(httpRequest, e, "Error deleting user: ", e.getMessage());
			throw e;
		}
	}

	public User getUserByEmail(String email) {

		String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId "
				+ "and r.roleName=? and u.email like ? ";
		TypedQuery<User> query = em.createQuery(queryString, User.class)
				.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, email);
		User result = query.getSingleResult();
		return result;
	}

	public User getUserByPhoneNo(String phoneNo) {

		// CriteriaBuilder builder = em.getCriteriaBuilder();
		// CriteriaQuery<User> criteria = builder.createQuery(User.class);
		// Root<User> r = criteria.from(User.class);
		// create JPQL query
		String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId "
				+ "and r.roleName=? and u.phone=?";
		TypedQuery<User> query = em.createQuery(queryString, User.class)
				.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, phoneNo);
		User result = query.getSingleResult();
		return result;
	}

	// @Override
	public UsersToPayment getUserToPaymentByUserId(String userId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToPayment> criteria = builder.createQuery(UsersToPayment.class);
		Root<UsersToPayment> r = criteria.from(UsersToPayment.class);
		TypedQuery<UsersToPayment> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToPayment_.usersId), userId),
						builder.equal(r.get(UsersToPayment_.status), "A")));
		UsersToPayment result = query.getSingleResult();
		return result;

	}

	public UsersToPayment updateUsersToPayment(UsersToPayment usersToPayment) {
		UsersToPayment prev_u = em.find(UsersToPayment.class, usersToPayment.getId());
		usersToPayment.setAmount(prev_u.getAmount().add(usersToPayment.getAmount()));

		usersToPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		em.merge(usersToPayment);

		return usersToPayment;
	}

	public UsersToPayment addUsersToPayment(UsersToPayment usersToPayment,HttpServletRequest  request,String locationId) throws Exception {

		usersToPayment.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));

		/*
		 * PaymentType paymentType = em.find(PaymentType.class,
		 * usersToPayment.getPaymentTypeId()); if(paymentType != null) {
		 * usersToPayment.setLocalTime(new
		 * TimezoneTime().getLocationSpecificTimeToAdd(paymentType.
		 * getlocationId, em)); }
		 */
		usersToPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		if(usersToPayment.getId() ==null)
		usersToPayment.setId(new StoreForwardUtility().generateDynamicIntId(em, locationId, request, "users_to_payment"));	
		usersToPayment = em.merge(usersToPayment);

		return usersToPayment;
	}

	public UsersToPaymentHistory addUpdateUsersToPaymentHistory(UsersToPaymentHistory usersToPaymentHistory,String locationId,HttpServletRequest request,int synchPacket) throws Exception {

		UsersToPayment usersToPayment = new UsersToPayment();

		logger.severe("usersToPaymentHistory.getUsersToPaymentId()====================================================="+usersToPaymentHistory.getUsersToPaymentId());
		if (usersToPaymentHistory.getUsersToPaymentId() == null || synchPacket==1) {

			usersToPayment.setAmount(usersToPaymentHistory.getAmountPaid());
			usersToPayment.setPaymentTypeId(usersToPaymentHistory.getPaymentTypeId());
			usersToPayment.setUsersId(usersToPaymentHistory.getUserId());
			// usersToPayment.setLocalTime(new
			// TimezoneTime().getLocationSpecificTimeToAdd(locationId, em));
			usersToPayment.setCreatedBy(usersToPaymentHistory.getCreatedBy());
			usersToPayment.setUpdatedBy(usersToPaymentHistory.getUpdatedBy());
			usersToPayment.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToPayment.setStatus(usersToPaymentHistory.getStatus());
			usersToPayment.setPaymentMethodTypeId(usersToPaymentHistory.getPaymentMethodTypeId());
			if(usersToPayment.getId()==null){
				usersToPayment.setId(new StoreForwardUtility().generateUUID());
			}
			usersToPayment = em.merge(usersToPayment);
			

		} else {
			usersToPayment =(UsersToPayment) new CommonMethods().getObjectById("UsersToPayment", em, UsersToPayment.class, usersToPaymentHistory.getUsersToPaymentId());
			logger.severe("usersToPayment====================================================="+usersToPayment);
			
			usersToPaymentHistory.setBalanceDue(usersToPayment.getAmount().add(usersToPaymentHistory.getAmountPaid()));

			usersToPayment.setCreatedBy(usersToPaymentHistory.getCreatedBy());
			usersToPayment.setUpdatedBy(usersToPaymentHistory.getUpdatedBy());
			usersToPayment.setAmount(usersToPayment.getAmount().add(usersToPaymentHistory.getAmountPaid()));
			usersToPayment.setPaymentTypeId(usersToPaymentHistory.getPaymentTypeId());
			usersToPayment.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToPayment.setUsersId(usersToPaymentHistory.getUserId());
			usersToPayment.setPaymentMethodTypeId(usersToPaymentHistory.getPaymentMethodTypeId());
			em.merge(usersToPayment);

		}

		usersToPaymentHistory.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		usersToPaymentHistory.setUsersToPaymentId(usersToPayment.getId());
		usersToPaymentHistory.setAmountPaid(usersToPaymentHistory.getAmountPaid().negate());
		usersToPaymentHistory.setLocalTime(
				new TimezoneTime().getLocationSpecificTimeToAdd(usersToPaymentHistory.getLocationId(), em));
		usersToPaymentHistory.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		String userId=usersToPaymentHistory.getUserId();
		try
		{
			usersToPaymentHistory =em.merge(usersToPaymentHistory);
		}
		
		catch (Exception e)
		{
			logger.severe(e);
		}
		if(usersToPaymentHistory.getUserId()==null){
		usersToPaymentHistory.setUserId(userId);
		}
		return usersToPaymentHistory;
	}

	public List<UsersToLocation> getUsersToLocationByUserId(EntityManager em, String userId) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UsersToLocation> criteria = builder.createQuery(UsersToLocation.class);
		Root<UsersToLocation> r = criteria.from(UsersToLocation.class);
		TypedQuery<UsersToLocation> query = em
				.createQuery(criteria.select(r).where(builder.equal(r.get(UsersToLocation_.usersId), userId)));
		List<UsersToLocation> result = (List<UsersToLocation>) query.getResultList();
		return result;
	}
	public User getUserToDiscountByUserId(int id) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteria = builder.createQuery(User.class);
		Root<User> r = criteria.from(User.class);
		TypedQuery<User> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(User_.id), id)));
		User result = (User) query.getSingleResult();
		
		try
		{

			String queryString1 = "select b from UsersToDiscount b where b.usersId= ? and b.status!='D'";
			List<UsersToDiscount> resultSet = null;

			Query query1 = em.createQuery(queryString1).setParameter(1, result.getId());
			resultSet = query1.getResultList();
			if (resultSet != null)
			{
				result.setUsersToDiscounts(resultSet);
			}
			

		}
		catch (Exception e)
		{
			logger.severe("No Users To Discount Found for User Id " + result.getId());
		}

		
		return result;

	}
	public List<UsersToDiscount> addUpdateUserToDiscount(List<UsersToDiscount> usersToDiscountList,String userId,HttpServletRequest request) throws Exception {

		String ids="";
		for(UsersToDiscount usersToDiscount:usersToDiscountList){
		if (usersToDiscount.getId() == null) {
			usersToDiscount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			usersToDiscount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if(usersToDiscount.getId() ==null)
			usersToDiscount.setId(new StoreForwardUtility().generateDynamicIntId(em, usersToDiscount.getLocationId(), request, "users_to_discount"));	
			usersToDiscount = em.merge(usersToDiscount);

		} else {
			if(ids.length()==0){
				ids=""+usersToDiscount.getId();
			}else {
				ids+=ids+","+usersToDiscount.getId();
			}
		}

		String queryString = "delete from users_to_discount where users_id=? and id not in ("+ids+")";
		em.createNativeQuery(queryString).setParameter(1, userId).executeUpdate();

		}

		return usersToDiscountList;
	}

}
