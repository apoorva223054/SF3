/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.AuthCodeGenerator;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.mail.SMTPCredentials;
import com.nirvanaxp.common.utils.mail.SendMail;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.Address_;
import com.nirvanaxp.global.types.entities.Business;
import com.nirvanaxp.global.types.entities.DeviceType;
import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UserAuth;
import com.nirvanaxp.global.types.entities.UserAuth_;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.accounts.Account_;
import com.nirvanaxp.global.types.entities.accounts.TempAccount;
import com.nirvanaxp.global.types.entities.accounts.TempAccount_;
import com.nirvanaxp.global.types.entities.countries.Countries;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.security.ManageSecurityToken;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.exceptions.POSExceptionMessage;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.GlobalAccountPacket;
import com.nirvanaxp.services.jaxrs.packets.GlobalUserAuthPacket;
import com.nirvanaxp.storeForward.StoreForwardUtility;
import com.nirvanaxp.storeForward.StoreForwardUtilityForGlobal;

// TODO: Auto-generated Javadoc
/**
 * The Class GlobalAccountService.
 */
@WebListener
@Path("")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class GlobalAccountService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The logger. */
	private NirvanaLogger logger = new NirvanaLogger(GlobalAccountService.class.getName());

	/* (non-Javadoc)
	 * @see com.nirvanaxp.services.jaxrs.AbstractNirvanaService#getNirvanaLogger()
	 */
	@Override
	protected NirvanaLogger getNirvanaLogger()
	{
		return logger;
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

	/*
	 * ************************Create Global Account
	 * *****************************
	 */

	/**
	 * Validate.
	 *
	 * @param account the account
	 * @throws ValidationException the validation exception
	 */
	private void validate(Account account) throws ValidationException
	{
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Account>> constraintViolations = validator.validate(account);

		if (constraintViolations != null && !constraintViolations.isEmpty())
		{
			StringBuilder b = new StringBuilder();

			for (ConstraintViolation<Account> constraintViolation : constraintViolations)
			{
				String str = constraintViolation.getPropertyPath() + " -> " + constraintViolation.getMessage();
				logger.severe(httpRequest, str);
				b.append(str);
				throw new ValidationException(b.toString());
			}

		}
	}

	/**
	 * Adds the temp account.
	 *
	 * @param tempAccount            - { "TempAccount": { "firstName": "Prachi", "lastName":
	 *            "Saxena", "name": "Test", "email": "prachishriv16@gmail.com",
	 *            "phoneNo": 12345, "verificationMethod": 1 } }
	 * @return if success then { "id": 25, "firstName": "Prachi", "lastName":
	 *         "Saxena", "name": "Test", "email": "prachishriv16@gmail.com",
	 *         "phoneNo": "12345", "status": "A", "created": 1404723121855,
	 *         "updated": 1404723121855, "verificationMethod": 2 } else sends
	 *         exception in this form
	 *         {"message":"Temp account packet cannot be null","code":1}
	 *         {"message":"First name cannot be null or blank","code":2}
	 *         {"message":"Last name cannot be null or blank","code":3}
	 *         {"message":"Account name cannot be null or blank","code":4}
	 *         {"message":"Phone and email both cannot be blank","code":5}
	 *         {"message":"Unable to generate verification code","code":6}
	 *         {"message"
	 *         :"email must be sent in packet for email verification","code":7}
	 *         {"message":
	 *         "Unable to send verification email. Please try again later"
	 *         ,"code":8}
	 *         {"message":"phone number must be sent in packet for sms verification"
	 *         ,"code":9} {"message":"Illegal verification method","code":10}
	 * @throws Exception the exception
	 */
	@POST
	@Path("/addTempAccountAndSendVerificationCode")
	public String addTempAccount(TempAccount tempAccount) throws Exception
	{
		EntityManager em = null;

		try
		{
			String validationResponse = validateTempAccount(tempAccount);

			if (validationResponse != null)
			{
				return validationResponse;
			}

			tempAccount.setStatus("A");
			tempAccount.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			tempAccount.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			String verificationCode = AuthCodeGenerator.generateAuthCode();
			if (verificationCode == null || verificationCode.length() == 0)
			{
				// unable to generate verification code
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_GENERATE_VERIFICATION_CODE, MessageConstants.ERROR_MESSAGE_UNABLE_TO_GENERATE_VERIFICATION_CODE, null)
						.toString();
			}

			tempAccount.setVerificationCode(verificationCode);

			// add this account
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			GlobalSchemaEntityManager.persist(em, tempAccount);

			if (tempAccount.getVerificationMethod() == 1)
			{
				if (!sendVerificationCodeEmail(tempAccount))
				{
					return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_SEND_VERIFICATION_EMAIL, MessageConstants.ERROR_MESSAGE_UNABLE_TO_SEND_VERIFICATION_EMAIL, null)
							.toString();
				}
				if (!sendEmailToNirvanaXpForAccountProcess(tempAccount))
				{
					return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_SEND_SINGUP_PROCESS_EMAIL, MessageConstants.ERROR_MESSAGE_UNABLE_TO_SEND_VERIFICATION_EMAIL, null)
							.toString();
				}
			}

			tempAccount.setVerificationCode("");
			return new JSONUtility(httpRequest).convertToJsonString(tempAccount);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Validate temp account.
	 *
	 * @param tempAccount the temp account
	 * @return the string
	 */
	private String validateTempAccount(TempAccount tempAccount)
	{
		if (tempAccount == null)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_TEMP_ACCOUNT_NULL, MessageConstants.ERROR_MESSAGE_TEMP_ACCOUNT_NULL, null).toString();
		}

		if (tempAccount.getFirstName() == null || tempAccount.getFirstName().length() == 0)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_FIRST_NAME_BLANK_NULL, MessageConstants.ERROR_MESSAGE_FIRST_NAME_BLANK_NULL, null).toString();
		}

		if (tempAccount.getLastName() == null || tempAccount.getLastName().length() == 0)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LAST_NAME_BLANK_NULL, MessageConstants.ERROR_MESSAGE_LAST_NAME_BLANK_NULL, null).toString();
		}

		if (tempAccount.getName() == null || tempAccount.getName().length() == 0)
		{
			return new JSONUtility(httpRequest).convertToJsonString(new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ACCOUNT_NAME_BLANK_NULL,
					MessageConstants.ERROR_MESSAGE_ACCOUNT_NAME_BLANK_NULL, null)));

		}

		if ((tempAccount.getPhoneNo() == null || tempAccount.getPhoneNo().length() == 0) && (tempAccount.getEmail() == null || tempAccount.getEmail().length() == 0))
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_PHONE_EMAIL_BLANK_NULL, MessageConstants.ERROR_MESSAGE_PHONE_EMAIL_BLANK_NULL, null).toString();
		}

		if (tempAccount.getVerificationUrl() == null || tempAccount.getVerificationUrl().length() == 0)
		{

			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_VERIFICATION_URL_BLANK_NULL, MessageConstants.ERROR_MESSAGE_VERIFICATION_URL_BLANK_NULL, null).toString();
		}

		switch (tempAccount.getVerificationMethod())
		{
		case 1:
		{

			if (tempAccount.getEmail() == null || tempAccount.getEmail().length() == 0)
			{
				// email must be sent in packet for email verification
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION,
						MessageConstants.ERROR_MESSAGE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION, null).toString();
			}
			break;
		}
		case 2:
		{
			if (tempAccount.getPhoneNo() == null || tempAccount.getPhoneNo().length() == 0)
			{
				// phone must be sent for sms verification
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_PHONE_MUST_SENT_IN_PACKET_FOR_VERIFICATION,
						MessageConstants.ERROR_MESSAGE_PHONE_MUST_SENT_IN_PACKET_FOR_VERIFICATION, null).toString();
			}
			break;
		}
		default:
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ILLEGAL_VERIFICATION_METHOD, MessageConstants.ERROR_MESSAGE_ILLEGAL_VERIFICATION_METHOD, null).toString();

		}
		}
		return null;
	}

	/**
	 * Send verification code email.
	 *
	 * @param tempAccount the temp account
	 * @return true, if successful
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean sendVerificationCodeEmail(TempAccount tempAccount) throws FileNotFoundException, IOException
	{
		// send the verification code via email
		SMTPCredentials smtpCredentials = ConfigFileReader.getSMTPCredentials();
		smtpCredentials.setToEmail(tempAccount.getEmail());
		smtpCredentials.setVerificationUrl(tempAccount.getVerificationUrl());
		// generate text based on what we ned to send
		String text1 = smtpCredentials.getText();
		String url = tempAccount.getVerificationUrl();
		url += tempAccount.getVerificationCode();
		text1 = text1.replace("verificationUrl", url);
		text1 = text1.replace("CustomerName", tempAccount.getFirstName());
		text1 = text1.replace("verificationCode", tempAccount.getVerificationCode());
		// text1 += url;
		smtpCredentials.setText(text1);
		return SendMail.sendHtmlMail(smtpCredentials);
	}
	
	/**
	 * Send email to nirvana xp for account process.
	 *
	 * @param tempAccount the temp account
	 * @return true, if successful
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean sendEmailToNirvanaXpForAccountProcess(TempAccount tempAccount) throws FileNotFoundException, IOException
	{
		// send the verification code via email
		SMTPCredentials smtpCredentials = ConfigFileReader.getSMTPCredentialsForSingupProcess();
		smtpCredentials.setToEmail("support@nirvanaxp.com");
		// generate text based on what we ned to send
		String text1 = smtpCredentials.getText();
		text1 = text1.replace("CUSTOMER_FIRSTNAME", tempAccount.getFirstName());
		text1 = text1.replace("CUSTOMER_ACCOUNT", tempAccount.getName());
		text1 = text1.replace("CUSTOMER_EMAIL", tempAccount.getEmail());
		text1 = text1.replace("CUSTOMER_PHONE", tempAccount.getPhoneNo());
		// text1 += url;
		smtpCredentials.setText(text1);
		return SendMail.sendHtmlMail(smtpCredentials);
	}
	
	/**
	 * Send email to nirvana xp for account success.
	 *
	 * @param tempAccount the temp account
	 * @return true, if successful
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean sendEmailToNirvanaXpForAccountSuccess(Account tempAccount) throws FileNotFoundException, IOException
	{
		// send the verification code via email
		SMTPCredentials smtpCredentials = ConfigFileReader.getSMTPCredentialsForSingupSuccess();
		smtpCredentials.setToEmail("support@nirvanaxp.com");
		// generate text based on what we ned to send
		String text1 = smtpCredentials.getText();
		text1 = text1.replace("CUSTOMER_FIRSTNAME", tempAccount.getFirstName());
		text1 = text1.replace("CUSTOMER_ACCOUNT", tempAccount.getName());
		text1 = text1.replace("CUSTOMER_EMAIL", tempAccount.getEmail());
		// text1 += url;
		smtpCredentials.setText(text1);
		return SendMail.sendHtmlMail(smtpCredentials);
	}



	/**
	 * Adds the.
	 *
	 * @param globalAccountPacket the global account packet
	 * @return the response
	 * @throws Exception the exception
	 */
	@POST
	@Path("/add")
	public Response add(GlobalAccountPacket globalAccountPacket) throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			Response validationResponse = validateAddAccountPacket(em, globalAccountPacket);
			if (validationResponse != null)
			{
				return validationResponse;
			}

			String logInCookieName = ConfigFileReader.getHostNameFromFile();
			if (logInCookieName == null)
			{
				return Response.ok(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_COOKIE_NAME_MISSING, MessageConstants.ERROR_MESSAGE_COOKIE_NAME_MISSING, null).toString()).build();
			}

			DeviceType deviceType = null;
			try
			{
				deviceType = em.find(DeviceType.class, globalAccountPacket.getDeviceTypeId());
			}
			catch (NoResultException e)
			{
				logger.severe(httpRequest, e, "No device type found for id: " + globalAccountPacket.getDeviceTypeId());
				return Response.ok(MessageConstants.ERROR_MESSAGE_WRONG_DEVICE_TYPE).build();
			}

			// check if this user exists in global database or not,
			User user = globalAccountPacket.getUsersList().get(0);

			// user sends global userid after 1st request
//			User globalUserCheck = new GlobalUserUtil().findGlobalUserByPhoneOrEmailOrUsername(httpRequest, em, null, null, user.getUsername(), null);
//
//			// user already exists
//			if (globalUserCheck != null)
//			{
//				Response response = Response.ok(MessageConstants.MSG_USER_ALREADY_EXISTS_IN_GLOBAL_DB).build();
//				return response;
//			}

			// -------------- All Checks done -----------------------------//

			// add address
			Address address = globalAccountPacket.getAddress();
			address.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			address.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			
			if (address != null)
			{
				address.setId(new StoreForwardUtilityForGlobal().generateDynamicBigIntId(em, httpRequest, "address"));
				 
				GlobalSchemaEntityManager.persist(em, address);
			}

			// add account
			Account account = globalAccountPacket.getAccount();
			account.setCreatedBy(user.getId());
			account.setUpdatedBy(user.getId());
			if (address != null)
			{
				account.setShippingAddressId(address.getId());
				account.setBillingAddressId(address.getId());
			}

			addAccount(account, em);

			// add global roles for this account
			Set<Role> adminRolesList = new HashSet<Role>();

			// adds roles and stores admin roles in
			// adminrolesList
			List<Role> globalRolesList = addGlobalRolesForAccount(account.getId(), em, account.getCreatedBy(), account.getUpdatedBy(), adminRolesList);

			globalAccountPacket.setGlobalRoles(globalRolesList);

			// either add this user, or update this user
			addUpdateAdminUserForAccount(em, account, user, adminRolesList);

			int adminRoleId = getAccountAdminRoleId(adminRolesList);
			if (adminRoleId > 0)
			{
				// when an account is added, client would always
				// send single
				// user
				// now log them in
				String uniqueToken = ManageSecurityToken.generateUniqueAuthToken(user);
				// this user has logged in as admin for the
				// first time
				UserSession us = ManageSecurityToken.commitToDatabase(httpRequest, uniqueToken, user, em, adminRoleId, account.getId(), globalAccountPacket.getDeviceId(), deviceType,
						globalAccountPacket.getDeviceName(), globalAccountPacket.getIpAddress(), globalAccountPacket.getAccount().getIsLocalAccount(), globalAccountPacket.getAccount()
								.getLocalServerUrl(), globalAccountPacket.getClientVersion(),globalAccountPacket.getBusinessId(), globalAccountPacket.getScope());

				// update the thread
				httpRequest.setAttribute(AbstractNirvanaService.NIRVANA_USER_SESSION, us);

				// delete temp account
				deleteTempAccount(em, globalAccountPacket);

				// create cookie
				int thirtyDays = 30 * 24 * 60 * 60 * 1000;

				NewCookie cookie = new NewCookie("SessionId", uniqueToken, "", logInCookieName, "login cookie", thirtyDays, true);
				Response response = Response.ok(new JSONUtility(httpRequest).convertToJsonIncludeNotNullAndNotEmpty(globalAccountPacket)).cookie(cookie).build();
				
				
				return response;

			}
			else
			{
				logger.severe(httpRequest, "Error while adding Account: Account Admin Role Id was not found.");
				return Response.ok(
						new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_ACCOUNT_ADMIN_ROLE_NOT_FOUND, MessageConstants.ERROR_MESSAGE_ACCOUNT_ADMIN_ROLE_NOT_FOUND, null).toString())
						.build();
			}

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Delete temp account.
	 *
	 * @param em the em
	 * @param globalAccountPacket the global account packet
	 */
	private void deleteTempAccount(EntityManager em, GlobalAccountPacket globalAccountPacket)
	{
		try
		{
			// get verification code and remove it from database as its
			// already been used once
			if (globalAccountPacket.getVerificationCode() != null && globalAccountPacket.getVerificationCode().length() > 0)
			{
				CriteriaBuilder builder = em.getCriteriaBuilder();
				CriteriaQuery<TempAccount> criteria = builder.createQuery(TempAccount.class);
				Root<TempAccount> account = criteria.from(TempAccount.class);
				TypedQuery<TempAccount> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(TempAccount_.verificationCode), globalAccountPacket.getVerificationCode())));
				TempAccount tempAccount = query.getSingleResult();
				GlobalSchemaEntityManager.remove(em, tempAccount);
			}
		}
		catch (Exception e)
		{
			logger.severe(httpRequest, e, "Temp Account not deleted");
		}
	}

	/**
	 * Validate add account packet.
	 *
	 * @param em the em
	 * @param globalAccountPacket the global account packet
	 * @return the response
	 * @throws Exception the exception
	 */
	private Response validateAddAccountPacket(EntityManager em, GlobalAccountPacket globalAccountPacket) throws Exception
	{
		if (globalAccountPacket.getDeviceId() == null || globalAccountPacket.getDeviceId().trim().length() == 0)
		{
			return Response.ok(MessageConstants.ERROR_MESSAGE_DEVICE_CANNOT_EMPTY).build();
		}

		if (globalAccountPacket.getDeviceTypeId() == 0)
		{
			return Response.ok(MessageConstants.ERROR_MESSAGE_DEVICE_TYPE_0).build();
		}
		if (globalAccountPacket.getDeviceName() == null || globalAccountPacket.getDeviceName().trim().length() == 0)
		{
			return Response.ok(MessageConstants.ERROR_MESSAGE_DEVICE_NAME_CANNOT_EMPTY).build();
		}

		if (globalAccountPacket.getIpAddress() == null || globalAccountPacket.getIpAddress().trim().length() == 0)
		{
			return Response.ok(MessageConstants.ERROR_MESSAGE_IPADDRESS_CANNOT_EMPTY).build();
		}

		if (globalAccountPacket == null || globalAccountPacket.getUsersList() == null || globalAccountPacket.getUsersList().isEmpty() || globalAccountPacket.getAccount() == null)
		{
			return Response.ok(MessageConstants.ERROR_MESSAGE_ILLEGAL_PACKET).build();
		}

		return null;
	}

	/**
	 * Gets the account admin role id.
	 *
	 * @param adminRolesList the admin roles list
	 * @return the account admin role id
	 */
	private int getAccountAdminRoleId(Set<Role> adminRolesList)
	{
		for (Role r : adminRolesList)
		{
			if (DefaultBusinessRoles.Account_Admin.getRoleName().equals(r.getRoleName()))
			{
				return r.getId();
			}
		}
		return -1;
	}

	/**
	 * Adds the global roles for account.
	 *
	 * @param accountId the account id
	 * @param em the em
	 * @param createdBy the created by
	 * @param updatedBy the updated by
	 * @param adminRolesList the admin roles list
	 * @return the list
	 */
	private List<Role> addGlobalRolesForAccount(int accountId, EntityManager em, String createdBy, String updatedBy, Set<Role> adminRolesList)
	{
		List<Role> globalRolesList = new ArrayList<Role>();

		for (DefaultBusinessRoles businessRole : DefaultBusinessRoles.values())
		{
			Role role = new Role(createdBy, businessRole.getRoleName(), businessRole.getDisplayName(), "A", updatedBy, accountId);
			role.setFunctionName(" ");
			role.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			role.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			GlobalSchemaEntityManager.persist(em, role);

			globalRolesList.add(role);

			if (businessRole.isAdminRole())
			{
				adminRolesList.add(role);
			}
		}
		return globalRolesList;
	}

	/**
	 * Adds the update admin user for account.
	 *
	 * @param em the em
	 * @param account the account
	 * @param user the user
	 * @param adminRolesSet the admin roles set
	 * @throws Exception the exception
	 */
	private void addUpdateAdminUserForAccount(EntityManager em, Account account, User user, Set<Role> adminRolesSet) throws Exception
	{

		Set<Account> accountsSet = null;
		Set<Role> rolesSet = null;
		Set<Address> addressSet = null;
		Set<Business> bussinessSet = null;
		if (user.getId()!=null)
		{
			User alreadyExistingUser = (User) new CommonMethods().getObjectById("User", em,User.class, user.getId());
			if (alreadyExistingUser != null)
			{
				accountsSet = alreadyExistingUser.getAccountsSet();
				addressSet = alreadyExistingUser.getAddressesSet();
				bussinessSet = alreadyExistingUser.getBusinessesSet();
				rolesSet = alreadyExistingUser.getRolesSet();

			}

		}

		if (accountsSet == null)
		{
			// no entry or relationship with accounts for user
			accountsSet = new HashSet<Account>();
		}
		accountsSet.add(account);
		user.setAccountsSet(accountsSet);

		if (rolesSet == null)
		{
			rolesSet = new HashSet<Role>();
		}
		// set admin roles only

		// set admin roles, along with other roles assigned to user
		rolesSet.addAll(adminRolesSet);

		user.setRolesSet(rolesSet);
		
		

		// add already existing details, so that client doen not have to
		// send these
		user.setAddressesSet(addressSet);
		user.setStatus("A");
		user.setBusinessesSet(bussinessSet);
		// set encrypted password

		String sha512Password = null;

		sha512Password = DigestUtils.sha512Hex(user.getPassword().getBytes(Charset.forName("UTF-8")));

		user.setPassword(sha512Password);
		user.setLastLoginTs(new Date(new TimezoneTime().getGMTTimeInMilis()));
		user.setId(new StoreForwardUtility().generateUUID());
		if (user.getId()== null)
		{
			
			GlobalSchemaEntityManager.merge(em, user);
		}
		else
		{
			GlobalSchemaEntityManager.merge(em, user);
		}

		// update this user information in all schema using thread
		// Query query =
		// em.createNativeQuery("call updateLocalUser( ? )").setParameter(1,
		// user.getId());
		// GlobalSchemaEntityManager.executeNativeQueryUpdate(em, query);
		//
	}

//	private String addUsersList(List<User> userList, EntityManager em, Account account, boolean isUpdate) throws Exception
//	{
//
//		Set<Account> accountsSet = new HashSet<Account>();
//		accountsSet.add(account);
//		for (User user : userList)
//		{
//
//			// set encrypted password
//			String sha512Password = "";
//			user.setPassword(sha512Password);
//			user.setLastLoginTs(new Date(new TimezoneTime().getGMTTimeInMilis()));
//			EntityTransaction tx = em.getTransaction();
//			try
//			{
//				// start transaction
//				tx.begin();
//
//				GlobalUserUtil globalUserUtil = new GlobalUserUtil();
//				if (!isUpdate)
//				{
//
//					User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, em, user.getPhone(), user.getEmail(), user.getUsername(), 0);
//					if (globalUserCheck == null)
//					{
//						user.setAccountsSet(accountsSet);
//						em.persist(user);
//					}
//					else
//					{
//						// user already exists, pass the exception back
//						user.setError("User already exists in Global database.");
//					}
//
//				}
//				else
//				{
//					User globalUserCheck = globalUserUtil.findGlobalUserByPhoneOrEmailOrUsername(httpRequest, em, user.getPhone(), user.getEmail(), user.getUsername(), user.getId());
//					if (globalUserCheck == null)
//					{
//						em.merge(user);
//					}
//					else
//					{
//						user.setError("User already exists in Global database.");
//					}
//				}
//				tx.commit();
//			}
//			catch (RuntimeException e)
//			{
//				// on error, if transaction active, rollback
//				if (tx != null && tx.isActive())
//				{
//					tx.rollback();
//				}
//				throw e;
//			}
//
//		}
//
//		return new JSONUtility(httpRequest).convertToJsonString(userList);
//
//	}

	/**
 * Adds the account.
 *
 * @param account the account
 * @param em the em
 * @return the string
 * @throws IOException Signals that an I/O exception has occurred.
 * @throws NirvanaXPException the nirvana XP exception
 */
private String addAccount(Account account, EntityManager em) throws IOException, NirvanaXPException
	{

		// for adding new account put default value for max devices
		if (account.getMaxAllowedDevices() == 0)
		{
			// read from data files and initialize max devices
			String maxAllowedDevices = ConfigFileReader.getMaxAllowedDevicesForAccountFromFile();
			if (maxAllowedDevices != null && maxAllowedDevices.trim().length() > 0)
			{
				int maxDevicesAllowedForAccount = Integer.parseInt(maxAllowedDevices);
				account.setMaxAllowedDevices(maxDevicesAllowedForAccount);
			}
		}

		// validate
		validate(account);
		account.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		account.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		GlobalSchemaEntityManager.persist(em, account);

		// schema name

		String schema_name = account.getSchemaName() + "_" + account.getId();
		account.setSchemaName(schema_name);

		GlobalSchemaEntityManager.merge(em, account);

		// host name
		logger.info(httpRequest, "Starting script -------------- ");
		String localhost = ConfigFileReader.getAccountURLFromFile();
		if (localhost == null)
		{

			logger.severe(httpRequest, "-------------- account url file name not found -------------- ");
			// throw new Exception("Account url not present");

		}
		else
		{

			// get template name from template table
			String template_name = "template_fsr";

			logger.info(httpRequest, "taking name  -------------- " + localhost);

			// command to execute
			String myCommand = "sh -x creacct.ksh " + localhost + " " + localhost + " " + template_name + " " + schema_name + "";

			String scriptPath = ConfigFileReader.getCreateAccountScriptPathFromFile();
			logger.info(httpRequest, myCommand+"-------------- scriptPath -------------- " + scriptPath);
			File executorDirectory = new File(scriptPath);

			// create a ProcessBuilder for the shell file // run a shell command

			ProcessBuilder pb = new ProcessBuilder("bash", "-c", myCommand);

			pb.directory(executorDirectory);
			Process p = pb.start();

			logger.info(httpRequest, "-------------- pb.start -------------- ");
			// int sleepCount = 0;
			// while(p.isAlive() && sleepCount<10) {
			// try {
			// ++sleepCount;
			// Thread.sleep(1000);
			// }
			// catch (InterruptedException ie) {
			// logger.severe(httpRequest, ie,
			// "Error while adding Account:------------------------------------------------ "
			// + ie.getMessage());
			// }
			// }

			String waitTimeForAcount = ConfigFileReader.getWaitTimeForAcountFromFile();
			logger.info(httpRequest, "-------------- ACCOUNT CREATED-------------- ", " Script start wait time : ");

			// while(p.isAlive()) {
			try
			{
				p.waitFor(Integer.parseInt(waitTimeForAcount), TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException ie)
			{
				logger.severe(httpRequest, "Error while adding Account:------------------------------------------------ " + ie.getMessage());
			}
			// }

			if (p.isAlive())
			{
				logger.severe(httpRequest, "process is not finish", "Error while adding Account");
				p.destroyForcibly();
				logger.severe(httpRequest, "process is destroy forcefully", "Error while adding Account");
			}
			else
			{
				logger.info(httpRequest, "-------------- ACCOUNT CREATED-------------- ", " Script exit value : " + p.exitValue());
			}

			if (!sendEmailToNirvanaXpForAccountSuccess(account))
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_SEND_SINGUP_SUCCESS_EMAIL, MessageConstants.ERROR_MESSAGE_UNABLE_TO_SEND_VERIFICATION_EMAIL, null)
						.toString();
			}
			// String waitTimeForAcount =
			// ConfigFileReader.getWaitTimeForAcountFromFile();
			// while(p.exitValue()==0) {
			// try {
			// p.waitFor(Integer.parseInt(waitTimeForAcount) ,
			// TimeUnit.MILLISECONDS);
			// }
			// catch (InterruptedException ie) {
			// logger.severe(httpRequest, ie,
			// "Error while adding Account:------------------------------------------------ "
			// + ie.getMessage());
			// }
			// }

			// logger.info(httpRequest,
			// "-------------- ACCOUNT CREATED-------------- ",
			// " Script exit value : "+p.exitValue());
		}

		return new JSONUtility(httpRequest).convertToJsonString(account);
	}

	/**
	 * Update.
	 *
	 * @param globalAccountPacket the global account packet
	 * @param sessionId the session id
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/update")
	public String update(GlobalAccountPacket globalAccountPacket, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{

			if (globalAccountPacket.getAddress() != null)
			{

				EntityTransaction tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.merge(globalAccountPacket.getAddress());
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
			if (globalAccountPacket.getUsersList() != null)
			{
				try
				{
					for (User user : globalAccountPacket.getUsersList())
					{
						GlobalUserAuthPacket globalUserAuthPacket = new GlobalUserAuthPacket();
						globalUserAuthPacket.setUserId(user.getId());
						globalUserAuthPacket.setPassword(user.getPassword());
						globalUserAuthPacket.setAuthCode(user.getAuthPin());
						globalUserAuthPacket.setUsername(user.getUsername());
						updateUserPasswordAndUserName(globalUserAuthPacket);
					}
				}
				catch (Exception e)
				{
					logger.severe(httpRequest, e);
				}
			}
			if (globalAccountPacket.getAccount() != null)
			{

				// if max allowed packet is 0, the set the value that was
				// already in database
				if (globalAccountPacket.getAccount().getMaxAllowedDevices() == 0)
				{
					Account accountFromDatabase = em.find(Account.class, globalAccountPacket.getAccount().getId());
					globalAccountPacket.getAccount().setMaxAllowedDevices(accountFromDatabase.getMaxAllowedDevices());

				}

				Account account = globalAccountPacket.getAccount();

				EntityTransaction tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.merge(account);
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
// removed by uzma :- 29395
//				if (globalAccountPacket.getUsersList() != null)
//				{
//					addUsersList(globalAccountPacket.getUsersList(), em, account, true);
//				}
			}

			return new JSONUtility(httpRequest).convertToJsonString(globalAccountPacket);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Validate verification code.
	 *
	 * @param verificationCode the verification code
	 * @return the string
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GET
	@Path("/validateVerificationCode/{verificationCode}")
	public String validateVerificationCode(@PathParam("verificationCode") String verificationCode) throws FileNotFoundException,  IOException
	{
		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<TempAccount> criteria = builder.createQuery(TempAccount.class);
			Root<TempAccount> account = criteria.from(TempAccount.class);
			TypedQuery<TempAccount> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(TempAccount_.verificationCode), verificationCode)));

			TempAccount tempAccount = query.getSingleResult();

			long expiryTime = tempAccount.getCreated() != null ? (tempAccount.getCreated().getTime() + 24 * 3600000l) : 24 * 3600000l;
			if (new TimezoneTime().getGMTTimeInMilis() < expiryTime)
			{
				return new JSONUtility(httpRequest).convertToJsonString(tempAccount);
			}
			else
			{
				return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_VERIFICATION_CODE_EXPIRED, MessageConstants.ERROR_MESSAGE_VERIFICATION_CODE_LINK_EXPIRED, null).toString();
			}
		}
		catch (NoResultException nre)
		{
			return new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_VERIFICATION_CODE_NOT_FOUND, MessageConstants.ERROR_MESSAGE_VERIFICATION_CODE_NOT_FOUND, null).toString();
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}

	}

	/**
	 * Gets the account by id.
	 *
	 * @param id the id
	 * @param sessionId the session id
	 * @return the account by id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getAccountById/{id}")
	public String getAccountById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
			Root<Account> account = criteria.from(Account.class);
			TypedQuery<Account> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(Account_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	/**
	 * Gets the account by id.
	 *
	 * @param id the id
	 * @return the account by id
	 * @throws Exception the exception
	 */
	@SuppressWarnings("null")
	@GET
	@Path("/getAccountDetailsById/{id}")
	public String getAccountById(@PathParam("id") int id) throws Exception
	{

		//EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{
			Account responseObj = null;
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
			Root<Account> account = criteria.from(Account.class);
			TypedQuery<Account> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(Account_.id), id)));
			
			responseObj = query.getSingleResult();
			
			CriteriaQuery<Address> criteriaAdd = builder.createQuery(Address.class);
			Root<Address> accountAdd = criteriaAdd.from(Address.class);
			TypedQuery<Address> queryAdd = em.createQuery(criteriaAdd.select(accountAdd).where(builder.equal(accountAdd.get(Address_.id), responseObj.getBillingAddressId())));
			
			if(queryAdd.getSingleResult() != null)
			{
				responseObj.setBillingAddress(queryAdd.getSingleResult());	
			}
			
			
			
			return new JSONUtility(httpRequest).convertToJsonString(responseObj);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the address by id.
	 *
	 * @param id the id
	 * @param sessionId the session id
	 * @return the address by id
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getAddressById/{id}")
	public String getAddressById(@PathParam("id") int id, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Address> criteria = builder.createQuery(Address.class);
			Root<Address> address = criteria.from(Address.class);
			TypedQuery<Address> query = em.createQuery(criteria.select(address).where(builder.equal(address.get(Address_.id), id)));
			return new JSONUtility(httpRequest).convertToJsonString(query.getSingleResult());
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update local server url.
	 *
	 * @param updateLocalServerInfo the update local server info
	 * @param sessionId the session id
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/updateLocalServerUrl")
	public String updateLocalServerUrl(UpdateLocalServerInfo updateLocalServerInfo, @CookieParam(NIRVANA_SESSION_COOKIE_NAME) String sessionId) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager(httpRequest, sessionId);
		try
		{
			Account account = em.find(Account.class, updateLocalServerInfo.getAccountId());
			account.setLocalServerUrl(updateLocalServerInfo.getLocalServerUrl());
			account.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			account.setIsLocalAccount(1);
			account.setUpdatedBy(updateLocalServerInfo.getUpdatedBy());

			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.merge(account);
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

			return new JSONUtility(httpRequest).convertToJsonString(account);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the user auth code.
	 *
	 * @param globalUserAuthPacket the global user auth packet
	 * @return the user auth code
	 * @throws Exception the exception
	 */
	@POST
	@Path("/getUserAuthCode")
	public String getUserAuthCode(GlobalUserAuthPacket globalUserAuthPacket) throws Exception
	{

		SMTPCredentials smtpCredentials = new SMTPCredentials();
		ConfigFileReader.initSMTPCredentials(smtpCredentials, ConfigFileReader.CONFIG_FILE_NAME_FOR_FORGOT_PASSWORD_SMTP);
		UserAuth userAuth = null;

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> r = criteria.from(User.class);

			TypedQuery<User> query = em.createQuery(criteria.select(r).where(builder.equal(r.get(com.nirvanaxp.global.types.entities.User_.username), globalUserAuthPacket.getUsername())));

			User user = null;
			try
			{
				user = query.getSingleResult();
			}
			catch (NoResultException e1)
			{
				logger.info("No result found when searching for user by username:", globalUserAuthPacket.getUsername());
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USERNAME_DOES_NOT_EXIST, MessageConstants.ERROR_MESSAGE_USERNAME_DOES_NOT_EXIST, null));
			}

			String userEmail = user.getEmail();

			// TODO uzma- check for null condition 

			if (!userEmail.equals(globalUserAuthPacket.getEmail()))
			{
				// unable to generate verification code
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_PROVIDE_REGISTER_EMAIL, MessageConstants.ERROR_MESSAGE_PROVIDE_REGISTER_EMAIL, null));

			}
			else
			{

          String userId = user.getId();

				userAuth = new UserAuth();
				String verificationCode = AuthCodeGenerator.generateAuthCode();

				if (verificationCode == null || verificationCode.length() == 0)
				{
					// unable to generate verification code
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_GENERATE_VERIFICATION_CODE,
							MessageConstants.ERROR_MESSAGE_UNABLE_TO_GENERATE_VERIFICATION_CODE, null));
				}

				String sql = "update user_auth set status='D' where user_id= ?  and status='A' ";
				em.createNativeQuery(sql).setParameter(1, userId);

				userAuth.setUserId(userId);
				userAuth.setAuthCode(verificationCode);
				userAuth.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				userAuth.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				userAuth.setCreatedBy(userId);
				userAuth.setUpdatedBy(userId);
				userAuth.setStatus("A");

				EntityTransaction tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.persist(userAuth);
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

				if (globalUserAuthPacket.getEmail() == null && globalUserAuthPacket.getPhone() == null)
				{
					// email must be sent in packet for email verification
					throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_PHONE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION,
							MessageConstants.ERROR_MESSAGE_PHONE_EMAIL_MUST_SENT_IN_PACKET_FOR_VERIFICATION, null));
				}

				if (globalUserAuthPacket.getEmail() != null || globalUserAuthPacket.getEmail().length() != 0)
				{

					// send the verification code via email
					smtpCredentials.setToEmail(globalUserAuthPacket.getEmail());
					smtpCredentials.setVerificationUrl(globalUserAuthPacket.getVerificationUrl());
					// generate text based on what we ned to send
					String bodyString = smtpCredentials.getText();
					String url = globalUserAuthPacket.getVerificationUrl();
					url += verificationCode;
					String resultantString = bodyString.replaceAll("verficationsUrl", url);
					// SETTING USERNAME IN EMAIL
					resultantString = resultantString.replaceAll("USERNAME", user.getUsername());

					smtpCredentials.setText(resultantString);

					boolean isEmailSent = SendMail.sendHtmlMail(smtpCredentials);
					if (isEmailSent == false)
					{
						throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_UNABLE_TO_SEND_VERIFICATION_EMAIL,
								MessageConstants.ERROR_MESSAGE_UNABLE_TO_SEND_VERIFICATION_EMAIL, null));
					}
				}

			}
			return new JSONUtility(httpRequest).convertToJsonString(userAuth);
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Validate user auth code.
	 *
	 * @param verificationCode the verification code
	 * @return the string
	 * @throws Exception the exception
	 */
	@GET
	@Path("/validateUserAuthCode/{verificationCode}")
	public String validateUserAuthCode(@PathParam("verificationCode") String verificationCode) throws Exception
	{
		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UserAuth> criteria = builder.createQuery(UserAuth.class);
			Root<UserAuth> user = criteria.from(UserAuth.class);
			TypedQuery<UserAuth> query = em.createQuery(criteria.select(user).where(builder.equal(user.get(UserAuth_.authCode), verificationCode), builder.equal(user.get(UserAuth_.status), "A")));
			try
			{
				UserAuth userAuth = query.getSingleResult();

				long expiryTime = userAuth.getCreated() != null ? (userAuth.getCreated().getTime() + 24 * 3600000l) : 24 * 3600000l;
				if (new TimezoneTime().getGMTTimeInMilis() < expiryTime)
				{
					return new JSONUtility(httpRequest).convertToJsonString(userAuth);
				}
				else
				{
					POSExceptionMessage posExceptionMessage = new POSExceptionMessage(
							"SORRY, THIS LINK HAS EXPIRED.", 2);
					return new JSONUtility(httpRequest).convertToJsonString(posExceptionMessage);
					//return new JSONUtility(httpRequest).(new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_LINK_EXPIRED,
					//MessageConstants.ERROR_MESSAGE_LINK_EXPIRED, null)));
				}
			}
			catch (NoResultException nre)
			{
				POSExceptionMessage posExceptionMessage = new POSExceptionMessage(
						"SORRY, THIS LINK HAS EXPIRED. PLEASE CLICK ON THE LINK IN THE LATEST EMAIL SENT IN RESPONSE TO YOUR LAST REQUEST FOR PASSWORD RESET.", 2);
				return new JSONUtility(httpRequest).convertToJsonString(posExceptionMessage);
			}
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update user password.
	 *
	 * @param globalUserAuthPacket the global user auth packet
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/updateUserPassword")
	public String updateUserPassword(GlobalUserAuthPacket globalUserAuthPacket) throws Exception
	{

		SMTPCredentials smtpCredentials = new SMTPCredentials();
		ConfigFileReader.initSMTPCredentials(smtpCredentials, ConfigFileReader.CONFIG_FILE_NAME_FOR_FORGOT_PASSWORD_SMTP);

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{

			User u = (User) new CommonMethods().getObjectById("User", em,User.class, globalUserAuthPacket.getUserId());

			String sha512Password = DigestUtils.sha512Hex(globalUserAuthPacket.getPassword().getBytes(Charset.forName("UTF-8")));

			u.setPassword(sha512Password);
			u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));

			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.merge(u);
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

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<UserAuth> criteria = builder.createQuery(UserAuth.class);
			Root<UserAuth> user = criteria.from(UserAuth.class);
			TypedQuery<UserAuth> query = em.createQuery(criteria.select(user).where(builder.equal(user.get(UserAuth_.authCode), globalUserAuthPacket.getAuthCode()),
					builder.equal(user.get(UserAuth_.userId), globalUserAuthPacket.getUserId())));

			UserAuth userAuth = query.getSingleResult();
			userAuth.setStatus("D");

			tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.merge(userAuth);
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

			return new JSONUtility(httpRequest).convertToJsonString(u);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Update user password and user name.
	 *
	 * @param globalUserAuthPacket the global user auth packet
	 * @return the string
	 * @throws Exception the exception
	 */
	@POST
	@Path("/updateUserPasswordAndUserName")
	public String updateUserPasswordAndUserName(GlobalUserAuthPacket globalUserAuthPacket) throws Exception
	{

		EntityManager em = GlobalSchemaEntityManager.getInstance().getEntityManager();
		try
		{
			String username = globalUserAuthPacket.getUsername();
			if (username != null && username.length() > 0)
			{

				User globalUser = getGlobalUserByUsername(em, username);
				String userIdDuringUpdate = globalUserAuthPacket.getUserId();
				// we got a user which has this phone number
				if (globalUser != null && (userIdDuringUpdate == null || (userIdDuringUpdate!=null && userIdDuringUpdate != globalUser.getId())))
				{
					return new JSONUtility(httpRequest).convertToJsonString(new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_USER_EXISTS_IN_GLOBAL,
							MessageConstants.ERROR_MESSAGE_USER_EXISTS_IN_GLOBAL, null)));

				}
			}
			User u = (User) new CommonMethods().getObjectById("User", em,User.class, globalUserAuthPacket.getUserId());

			String sha512Password = DigestUtils.sha512Hex(globalUserAuthPacket.getPassword().getBytes(Charset.forName("UTF-8")));

			u.setPassword(sha512Password);
			u.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
			if (globalUserAuthPacket.getUsername() != null)
			{
				u.setUsername(globalUserAuthPacket.getUsername());
			}
			EntityTransaction tx = em.getTransaction();
			try
			{
				// start transaction
				tx.begin();
				em.merge(u);
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

			CriteriaBuilder builder = em.getCriteriaBuilder();
			UserAuth userAuth = null;
			try
			{
				CriteriaQuery<UserAuth> criteria = builder.createQuery(UserAuth.class);
				Root<UserAuth> user = criteria.from(UserAuth.class);
				TypedQuery<UserAuth> query = em.createQuery(criteria.select(user).where(builder.equal(user.get(UserAuth_.authCode), globalUserAuthPacket.getAuthCode()),
						builder.equal(user.get(UserAuth_.userId), globalUserAuthPacket.getUserId())));

				userAuth = query.getSingleResult();
				userAuth.setStatus("D");

				tx = em.getTransaction();
				try
				{
					// start transaction
					tx.begin();
					em.merge(userAuth);
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
			catch (Exception e1)
			{
				 
				logger.severe(httpRequest, "Could not find any user");
			}

			return new JSONUtility(httpRequest).convertToJsonString(u);

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Gets the global user by username.
	 *
	 * @param globalEntityManager the global entity manager
	 * @param username the username
	 * @return the global user by username
	 * @throws Exception the exception
	 */
	private User getGlobalUserByUsername(EntityManager globalEntityManager, String username) throws Exception
	{

		CriteriaBuilder builder = globalEntityManager.getCriteriaBuilder();
		User result = null;
		// addedd try catch to avoid skip of flow of code when user not found
		// By Apoorva 28373
		try
		{
			CriteriaQuery<User> criteria = builder.createQuery(User.class);
			Root<User> root = criteria.from(User.class);
			TypedQuery<User> query = globalEntityManager.createQuery(criteria.select(root).where(builder.equal(root.get(com.nirvanaxp.global.types.entities.User_.username), username)));
			result = query.getSingleResult();
		}
		catch (Exception e)
		{
			 
			logger.severe(httpRequest, "Could not find any user");
		}
		return result;

	}

	/**
	 * Gets the all countries.
	 *
	 * @return the all countries
	 * @throws Exception the exception
	 */
	@GET
	@Path("/getAllCountries")
	public String getAllCountries() throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Countries> criteria = builder.createQuery(Countries.class);
			Root<Countries> account = criteria.from(Countries.class);
			TypedQuery<Countries> query = em.createQuery(criteria.select(account));
			return new JSONUtility(httpRequest).convertToJsonString(query.getResultList());

		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}

	/**
	 * Adds the reference number.
	 *
	 * @return the string
	 * @throws Exception the exception
	 */
	@GET
	@Path("/addReferenceNumber")
	public String addReferenceNumber() throws Exception
	{

		EntityManager em = null;
		try
		{
			em = GlobalSchemaEntityManager.getInstance().getEntityManager();

			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Business> criteria = builder.createQuery(Business.class);
			Root<Business> business = criteria.from(Business.class);
			TypedQuery<Business> query = em.createQuery(criteria.select(business));
			List<Business> businesses = query.getResultList();

			EntityTransaction tx = em.getTransaction();

			for (Business b : businesses)
			{

				POSNPartners posnPartners = new POSNPartners();
				posnPartners.setAccountId(b.getAccountId());
				posnPartners.setBusinessId(b.getId());
				posnPartners.setDisplayName("CustomerApp");
				posnPartners.setPartnerName("CustomerApp");
				posnPartners.setStatus("A");
				posnPartners.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
				posnPartners.setUpdatedBy("22");
				posnPartners.setCreatedBy("22");
				UUID uniqueRefreneNumber = UUID.randomUUID();
				posnPartners.setReferenceNumber(uniqueRefreneNumber.toString());

				try
				{
					// start transaction
					tx.begin();
					em.merge(posnPartners);
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
			return "true";
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(em);
		}
	}
	
	

}
