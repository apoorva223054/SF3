/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.nio.charset.Charset;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.digest.DigestUtils;

import com.nirvanaxp.common.methods.CommonMethods;
import com.nirvanaxp.common.utils.AuthCodeGenerator;
import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.mail.SMTPCredentials;
import com.nirvanaxp.common.utils.mail.SendMail;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.UserAuth;
import com.nirvanaxp.global.types.entities.UserAuth_;
import com.nirvanaxp.server.util.GlobalSchemaEntityManager;
import com.nirvanaxp.server.util.JSONUtility;
import com.nirvanaxp.server.util.NirvanaLogger;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.constants.DefaultBusinessRoles;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.exceptions.POSExceptionMessage;
import com.nirvanaxp.services.interceptors.LoggerInterceptor;
import com.nirvanaxp.services.jaxrs.packets.GlobalUserAuthPacket;


// TODO: Auto-generated Javadoc
/**
 * The Class CustomerService.
 */
@WebListener
@Path("/CustomerService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LoggerInterceptor
public class CustomerService extends AbstractNirvanaService
{

	/** The http request. */
	@Context
	HttpServletRequest httpRequest;

	/** The Constant logger. */
	private final static NirvanaLogger logger = new NirvanaLogger(CustomerService.class.getName());

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

			String queryString = "SELECT u FROM User u, UsersToRole utr, Role r where u.id=utr.usersId and r.id=utr.rolesId "
					+ "and r.roleName=? and u.email like ? ";
			TypedQuery<com.nirvanaxp.global.types.entities.User> query = em.createQuery(queryString, com.nirvanaxp.global.types.entities.User.class)
					.setParameter(1, DefaultBusinessRoles.POS_Customer.getRoleName()).setParameter(2, globalUserAuthPacket.getEmail());
//			com.nirvanaxp.global.types.entities.User result = query.getSingleResult();
			User user = null;
			try
			{
				user = query.getSingleResult();
			}
			catch (NoResultException e1)
			{
				logger.info("No result found when searching for user by email:", globalUserAuthPacket.getEmail());
				throw new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_PROVIDE_REGISTER_EMAIL, MessageConstants.ERROR_MESSAGE_PROVIDE_REGISTER_EMAIL, null));
			}

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
					return new JSONUtility(httpRequest).convertToJsonString(new NirvanaXPException(new NirvanaServiceErrorResponse(MessageConstants.ERROR_CODE_OTP_EXPIRED,
							MessageConstants.ERROR_MESSAGE_OTP_EXPIRED, null)));
				}
			}
			catch (NoResultException nre)
			{
				POSExceptionMessage posExceptionMessage = new POSExceptionMessage(
						"SORRY, THIS OTP HAS EXPIRED. PLEASE USE LATEST OTP.", 2);
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

			User u = (User) new CommonMethods().getObjectById("User", em,User.class,  globalUserAuthPacket.getUserId());

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
	
	

}