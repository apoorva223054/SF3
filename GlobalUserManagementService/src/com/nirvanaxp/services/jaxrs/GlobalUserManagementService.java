/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.Address_;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.User_;
import com.nirvanaxp.global.types.entities.UsersToAccount;
import com.nirvanaxp.global.types.entities.UsersToAccount_;
import com.nirvanaxp.global.types.entities.UsersToAddress;
import com.nirvanaxp.global.types.entities.UsersToAddress_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.user.utility.GlobalUserUtil;

@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class GlobalUserManagementService extends AbstractNirvanaService
{

	@Context
	HttpServletRequest httpRequest;

	private final static NirvanaLogger logger = new NirvanaLogger(GlobalUserManagementService.class.getName());

	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
	}

	@Override
	@GET
	@Path("/isAlive")
	public boolean isAlive()
	{
		return true;
	}

	private void validate(User user) throws ValidationException
	{
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

		if (constraintViolations != null && !constraintViolations.isEmpty())
		{
			StringBuilder b = new StringBuilder();

			for (ConstraintViolation<User> constraintViolation : constraintViolations)
			{
				String str = constraintViolation.getPropertyPath() + " -> " + constraintViolation.getMessage();
				logger.severe(httpRequest, str);
				b.append(str);
				throw new ValidationException(b.toString());
			}

		}
	}

	@POST
	@Path("/add")
	public String add(User user, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = null;
		try
		{
			if (user == null)
			{
				return "User cannot be blank or null";
			}
			// validate
			validate(user);

			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			// update user's password
			String sha512Password = null;
			sha512Password = DigestUtils.sha512Hex(user.getPassword().getBytes(Charset.forName("UTF-8")));

			user.setPassword(sha512Password);

			// upper the user name
			if (user.getUsername() != null)
			{
				user.setUsername(user.getUsername().toUpperCase());
			}
			else
			{
				if (user.getPhone() != null && user.getPhone().length() > 0)
				{
					user.setUsername(user.getPhone());
				}
				else
				{
					if (user.getEmail() != null && user.getEmail().length() > 0)
					{
						user.setUsername(user.getEmail());
					}
					else
					{
						return "Phone number and Email Id both cannot be blank.";
					}
				}
			}
			user.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			GlobalUserUtil globalUserUtil = new GlobalUserUtil();
			User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, em, user.getPhone(), user.getEmail(), user.getUsername(), null);

			if (globalUserCheck != null)
			{
				return "User already exists in Global.";
			}
			else
			{

				addUsersInfoInGlobalDb(em, user);
			}

			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	// @POST
	// @Path("/update")
	// public String update(UserPacket userPacket, @CookieParam(value =
	// "SessionId") String sessionId) throws Exception {
	//
	// User user = null;
	//
	// EntityManager em = null;
	// try {
	//
	//
	// user = userPacket.getUser();
	//
	// if (user == null) {
	// return new
	// NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_CANNOT_BE_NULL_EXCEPTION,
	// MessageConstants.ERROR_MESSAGE_USER_CANNOT_BE_NULL_DISPLAY_MESSAGE,
	// null).toString();
	// }
	//
	// Set<Address> addressList = userPacket.getAddressList();
	// if (addressList != null && addressList.size() > 0) {
	// user.setAddressesSet(addressList);
	// }
	//
	// em = LocalSchemaEntityManager.getInstance().getEntityManager(httpRequest,
	// sessionId);
	//
	// updateUsersInfoInLocalDb(em, user);
	//
	//
	// // Set<Account> accountAssociatedByUserSet = user.getAccountsSet();
	// UserPacket userPostPacket2 = createPacketForPush(userPacket, user);
	// sendPacketForBroadcast(userPostPacket2,
	// POSNServiceOperations.GlobalUserManagementService_updateCustomer.name());
	// // if (accountAssociatedByUserSet != null &&
	// accountAssociatedByUserSet.size() > 0) {
	// //
	// // for (Account account : accountAssociatedByUserSet) {
	// // userPostPacket2.setMerchantId("" + account.getId());
	// // // when we set location id -1, then packet will be
	// // // broadcasted on basis of merchant info only
	// // // in this way all schemas where user must have existed
	// // // will get the push
	// // userPostPacket2.setLocationId("" + "-1");
	// // sendPacketForBroadcast(userPostPacket2,
	// POSNServiceOperations.GlobalUserManagementService_updateCustomer.name());
	// // }
	// //
	// // }
	//
	// return new JSONUtility(httpRequest).convertToJsonString(userPacket);
	//
	// }
	// finally {
	// GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
	// }
	//
	// }

	private void addUsersInfoInGlobalDb(EntityManager em, User user)
	{
		user.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		user.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		GlobalSchemaEntityManager.persist(em, user);
	}

	// private void updateUsersInfoInLocalDb(EntityManager em, User user) {
	// User userInDatabase = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());
	// userInDatabase.setFirstName(user.getFirstName());
	// userInDatabase.setLastName(user.getLastName());
	// if (user.getAddressesSet() != null && user.getAddressesSet().size() > 0)
	// {
	// userInDatabase.setAddressesSet(user.getAddressesSet());
	// }
	// userInDatabase.setDateofbirth(user.getDateofbirth());
	// userInDatabase.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
	// userInDatabase.setUpdatedBy(user.getUpdatedBy());
	// userInDatabase.setPhone(user.getPhone());
	// userInDatabase.setEmail(user.getEmail());
	//
	// EntityTransaction tx = em.getTransaction();
	// try {
	// // start transaction
	// tx.begin();
	// em.merge(userInDatabase);
	// tx.commit();
	// }
	// catch (RuntimeException e) {
	// // on error, if transaction active, rollback
	// if (tx != null && tx.isActive()) {
	// tx.rollback();
	// }
	// throw e;
	// }
	//
	// user = userInDatabase;
	// }

	@POST
	@Path("/delete")
	public String delete(User user, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{

			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User u = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());
			u.setStatus("D");
			u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			GlobalSchemaEntityManager.merge(em, u);

			return new JSONUtility(httpRequest).convertToJsonString(u);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/deleteAddress/{addressId}/{userId}")
	public String delete(@PathParam("addressId") String addressId, @PathParam("userId") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{

			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			User user = (User) new CommonMethods().getObjectById("User", em,User.class, userId);
			if (user != null)
			{
				Set<Address> userAddressesSet = user.getAddressesSet();
				if (userAddressesSet != null && userAddressesSet.size() > 0)
				{

					Address addressToRemove = (Address) new CommonMethods().getObjectById("Address", em,Address.class, addressId);
					userAddressesSet.remove(addressToRemove);
					GlobalSchemaEntityManager.merge(em, user);
					GlobalSchemaEntityManager.merge(em, addressToRemove);
					GlobalSchemaEntityManager.remove(em, addressToRemove);

				}
			}

			return "true";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getUserById/{id}")
	public String getUserById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> root = criteria.from(User.class);
			TypedQuery<User> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(User_.id), id)));
			User user = query.getSingleResult();
			user.setPassword("");
			user.setAuthPin("");

			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getUserByAccountId/{accountId}")
	public String getUserByAccountId(@PathParam("accountId") int accountId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToAccount> criteria = builder.createQuery(UsersToAccount.class);
			Root<UsersToAccount> user = criteria.from(UsersToAccount.class);

			TypedQuery<UsersToAccount> query = em.createQuery(criteria.select(user).where(builder.equal(user.get(UsersToAccount_.accountsId), accountId)));
			List<UsersToAccount> usersToAccountList = query.getResultList();
			List<User> usersList = new ArrayList<User>();
			if (usersToAccountList != null)
			{
				for (UsersToAccount usersToAccount : usersToAccountList)
				{
					if (usersToAccount != null)
					{
						try
						{
							CriteriaQuery<User> criteriaAccount = builder.createQuery(User.class);
							Root<User> root = criteriaAccount.from(User.class);

							TypedQuery<User> queryAccount = em.createQuery(criteriaAccount.select(root).where(builder.equal(root.get(User_.id), usersToAccount.getAccountsId())));
							User user2 = queryAccount.getSingleResult();

							user2.setPassword("");
							user2.setAuthPin("");

							usersList.add(user2);
						}
						catch (Exception e)
						{
							
							logger.severe(httpRequest, e, "Error while performing global user management service operation: " + e.getMessage());
						}
					}
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(usersToAccountList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getUserByUserName/{username}")
	public String getUserByUserName(@PathParam("username") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> r = criteria.from(User.class);
			TypedQuery<User> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(User_.username), username)));
			User user = query.getSingleResult();
			user.setPassword("");
			user.setAuthPin("");

			return new JSONUtility(httpRequest).convertToJsonString(user);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/checkForUniquePhone/{phoneNumber}")
	public String checkForUniquePhone(@PathParam("phoneNumber") String phoneNumber) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			User user = globalManagementUtility.getUserByPhoneNumber(phoneNumber, em);
			if (user != null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_PHONE_ALREADY_EXIST_EXCEPTION, MessageConstants.ERROR_MESSAGE_PHONE_ALREADY_EXIST_DISPLAY_MESSAGE, null).toString();
			}

			return "true";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkForUniqueEmail/{email}")
	public String checkForUniqueEmail(@PathParam("email") String email) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			User user = globalManagementUtility.getUserByEmail(email, em);

			if (user != null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_EMAIL_ALREADY_EXIST_EXCEPTION, MessageConstants.ERROR_MESSAGE_EMAIL_ALREADY_EXIST_DISPLAY_MESSAGE, null).toString();
			}

			return "true";

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkForUniqueUsername/{username}")
	public String checkForQniqueUsername(@PathParam("username") String username) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			User user = globalManagementUtility.getUserByUserName(username, em);
			if (user != null)
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USERNAME_ALREADY_EXIST_EXCEPTION, MessageConstants.ERROR_MESSAGE_USERNAME_ALREADY_EXIST_DISPLAY_MESSAGE, null)
						.toString();
			}

			return "true";

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkForUniquePhoneAndUsername/{phoneNumber}/{username}")
	public String checkForQniquePhoneAndUsername(@PathParam("phoneNumber") String phoneNumber, @PathParam("username") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId)
			throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			if (phoneNumber != null && phoneNumber.trim().length() > 0)
			{

				User user = globalManagementUtility.getUserByPhoneNumber(phoneNumber, em);
				if (user == null)
				{
					return "Phone number already exists in database.";
				}
			}

			if (username != null && username.trim().length() > 0)
			{
				User user = globalManagementUtility.getUserByUserName(username, em);
				if (user == null)
				{
					return "Username already exists in database.";
				}
			}

			return "true";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkForUniquePhoneAndUsername/{phoneNumber}/{username}/{emailId}")
	public String checkForQniquePhoneAndUsername(@PathParam("phoneNumber") String phoneNumber, @PathParam("username") String username, @PathParam("emailId") String emailId,
			@CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			if (phoneNumber != null && phoneNumber.trim().length() > 0)
			{

				User user = globalManagementUtility.getUserByPhoneNumber(phoneNumber, em);
				if (user == null)
				{
					return "Phone number already exists in database.";
				}
			}

			if (username != null && username.trim().length() > 0)
			{
				User user = globalManagementUtility.getUserByUserName(username, em);
				if (user == null)
				{
					return "Username already exists in database.";
				}
			}

			if (emailId != null && emailId.trim().length() > 0)
			{
				User user = globalManagementUtility.getUserByEmail(emailId, em);
				if (user == null)
				{
					return "Email already exists in database.";
				}
			}

			return "true";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/checkForUniqueEmailAndUsername/{email}/{username}")
	public String checkForQniqueEmailAndUsername(@PathParam("email") String email, @PathParam("username") String username, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			if (email != null && email.trim().length() > 0)
			{

				User user = globalManagementUtility.getUserByEmail(email, em);
				if (user == null)
				{
					return "Email already exists in database.";
				}
			}

			if (username != null && username.trim().length() > 0)
			{
				User user = globalManagementUtility.getUserByUserName(username, em);
				if (user == null)
				{
					return "Username already exists in database.";
				}
			}

			return "true";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getUserByPhoneNumber/{phoneNumber}")
	public String getUserByPhoneNumber(@PathParam("phoneNumber") String phoneNumber, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			if (phoneNumber != null && phoneNumber.trim().length() > 0)
			{

				User user = globalManagementUtility.getUserByPhoneNumber(phoneNumber, em);
				if (user != null)
				{
					return new JSONUtility(httpRequest).convertToJsonString(user);
				}
				return "{}";
			}
			else
			{
				return "Phone Number cannot be blank";
			}

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/getUserByEmail/{email}")
	public String getUserByEmail(@PathParam("email") String email, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			GlobalManagementUtility globalManagementUtility = new GlobalManagementUtility();
			if (email != null && email.trim().length() > 0)
			{

				User user = globalManagementUtility.getUserByEmail(email, em);
				if (user != null)
				{
					return new JSONUtility(httpRequest).convertToJsonString(user);
				}
				return "{}";
			}
			else
			{
				return "Email cannot be blank";
			}

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	@GET
	@Path("/updatePasswordByUsername/{username}/{password}")
	public boolean updatePasswordByUsername(@PathParam("username") String username, @PathParam("password") String password, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;

		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> r = criteria.from(User.class);
			TypedQuery<User> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(User_.username), username)));
			User user = query.getSingleResult();
			String sha512Password = null;
			sha512Password = DigestUtils.sha512Hex(user.getPassword().getBytes(Charset.forName("UTF-8")));
			user.setPassword(sha512Password);

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
				// on error, if transaction active, rollback
				if (tx != null && tx.isActive())
				{
					tx.rollback();
				}
				throw e;
			}
			return true;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllUsersStartingWithPhoneNo/{phone}")
	public String getAllUsersStartingWithPhoneNo(@PathParam("phone") String phoneNo, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
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
				user.setAccountsSet(null);
				user.setAuthPin("");
				user.setRolesSet(null);
				user.setBusinessesSet(null);

			}

			return new JSONUtility(httpRequest).convertToJsonString(resultList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	@GET
	@Path("/getAllUsersStartingWithEmailId/{emailId}")
	public String getAllUsersStartingWithEmailId(@PathParam("emailId") String emailId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);

			CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
			Root<User> from = criteriaQuery.from(User.class);
			CriteriaQuery<User> select = criteriaQuery.select(from);

			Predicate predicate = criteriaBuilder.like(from.get(User_.email), emailId + "%");

			criteriaQuery.where(predicate);

			TypedQuery<User> typedQuery = em.createQuery(select);
			List<User> resultList = typedQuery.getResultList();

			for (User user : resultList)
			{
				user.setPassword("");
				user.setAccountsSet(null);
				user.setAuthPin("");
				user.setRolesSet(null);
				user.setBusinessesSet(null);

			}

			return new JSONUtility(httpRequest).convertToJsonString(resultList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
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
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select u.email from users u where u.email like ?";
			Query q = em.createNativeQuery(queryString).setParameter(1, emailId +"%");
			@SuppressWarnings("unchecked")
			List<String> objList = q.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(objList);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
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
			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			String queryString = "select u.phone from users u where u.phone like ?";
			Query q = em.createNativeQuery(queryString).setParameter(1,  phoneNo + "%");
			@SuppressWarnings("unchecked")
			List<String> objList = q.getResultList();
			return new JSONUtility(httpRequest).convertToJsonString(objList);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * @param userId
	 * @param sessionId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAddressByUserId/{userId}")
	public String getAddressById(@PathParam("userId") String userId, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = null;
		try
		{

			em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UsersToAddress> criteria = builder.createQuery(UsersToAddress.class);
			Root<UsersToAddress> usersToAddress = criteria.from(UsersToAddress.class);
			TypedQuery<UsersToAddress> query = em.createQuery(criteria.select(usersToAddress).where(builder.equal(usersToAddress.get(UsersToAddress_.usersId), userId)));
			List<UsersToAddress> usersToAddressList = query.getResultList();
			List<Address> addressList = new ArrayList<Address>();
			if (usersToAddressList != null)
			{
				for (UsersToAddress usersToAddressObj : usersToAddressList)
				{
					CriteriaQuery<Address> criteriaAddress = builder.createQuery(Address.class);
					Root<Address> addressRoot = criteriaAddress.from(Address.class);
					TypedQuery<Address> addressQuery = em.createQuery(criteriaAddress.select(addressRoot).where(builder.equal(addressRoot.get(Address_.id), usersToAddressObj.getAddressId())));
					Address address = addressQuery.getSingleResult();
					addressList.add(address);
				}
			}
			return new JSONUtility(httpRequest).convertToJsonString(addressList);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

//	private UserPacket createPacketForPush(UserPacket userPostPacket, User localuser)
//	{
//
//		UserPacket userPostPacketForPush = new UserPacket();
//		userPostPacketForPush.setClientId(userPostPacket.getClientId());
//		userPostPacketForPush.setMerchantId(userPostPacket.getMerchantId());
//		userPostPacketForPush.setLocationId(userPostPacket.getLocationId());
//		userPostPacketForPush.setSchemaName(userPostPacket.getSchemaName());
//		userPostPacketForPush.setEchoString(userPostPacket.getEchoString());
//		User userForPush = new User();
//		userForPush.setFirstName(localuser.getFirstName());
//		userForPush.setLastName(localuser.getLastName());
//		userForPush.setEmail(localuser.getEmail());
//		userForPush.setPhone(localuser.getPhone());
//		userForPush.setId(localuser.getId());
//		userPostPacketForPush.setUser(userForPush);
//		return userPostPacketForPush;
//
//	}

//	private void sendPacketForBroadcast(UserPacket userPostPacket, String operationName)
//	{
//		try
//		{
//			ObjectMapper objectMapper = new ObjectMapper();
//			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
//			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
//			String internalJson = objectMapper.writeValueAsString(userPostPacket.getUser());
//
//			operationName = ServiceOperationsUtility.getOperationName(operationName);
//			MessageSender messageSender = new MessageSender();
//
//			messageSender.sendMessage(httpRequest, userPostPacket.getClientId(), POSNServices.GlobalUserManagementService.name(), operationName, internalJson, userPostPacket.getMerchantId(),
//					userPostPacket.getLocationId(), userPostPacket.getEchoString(), userPostPacket.getSchemaName());
//		}
//		catch (JsonGenerationException e)
//		{
//			logger.severe(httpRequest, e, "Error while performing global user management service operation: " + e.getMessage());
//		}
//		catch (JsonMappingException e)
//		{
//			logger.severe(httpRequest, e, "Error while performing global user management service operation: " + e.getMessage());
//		}
//		catch (IOException e)
//		{
//			logger.severe(httpRequest, e, "Error while performing global user management service operation: " + e.getMessage());
//		}
//
//	}

}
