/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.accounts.Account_;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.services.NirvanaServiceErrorResponse;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.exceptions.NirvanaXPException;
import com.nirvanaxp.services.jaxrs.INirvanaService;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;

public class LocalSchemaEntityManager extends AbstractEntityManager
{

	private static final NirvanaLogger logger = new NirvanaLogger(LocalSchemaEntityManager.class.getName());

	private static class SingletonHolder
	{
		private static final LocalSchemaEntityManager INSTANCE = new LocalSchemaEntityManager();
	}

	private LocalSchemaEntityManager()
	{
		// try
		// {
		// String driverName = "com.mysql.jdbc.Driver";
		// Class.forName(driverName);
		//
		// entityManagers = new HashMap<String, EntityManagerFactory>();
		//
		// }
		// catch (ClassNotFoundException e)
		// {
		// logger.severe(e, "Could not load database driver");
		// }
		// catch (Exception e)
		// {
		// logger.severe(e, "Could not connect to global schema");
		// }
	}

	public static LocalSchemaEntityManager getInstance()
	{
		logger.finest("returning local schema entity manager wrapper instance:" + SingletonHolder.INSTANCE);
		return SingletonHolder.INSTANCE;
	}

	private synchronized EntityManagerFactory getEntityManagerFactory(String schemaName) throws FileNotFoundException, IOException
	{
		EntityManagerFactory emf = getEntityManagerFactory(NAME_LOCAL_SCHEMA_PERSISTENCE_UNIT, schemaName);
		return emf;
	}

	@Deprecated
	public EntityManager getEntityManagerUsingName(String schemaName) throws IOException
	{
		EntityManager em = null;
		try
		{
			EntityManagerFactory emf = getEntityManagerFactory(schemaName);

			if (emf == null)
			{
				throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE);
			}

			em = emf.createEntityManager();

			if (em == null)
			{
				throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE);
			}
		}
		catch (Exception e)
		{
			throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE, e);
		}

		return em;
	}

	public String getSchemaNameUsingSessionId(HttpServletRequest httpRequest, String sessionId) throws FileNotFoundException, IOException, InvalidSessionException
	{
		UserSession session = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);
		if (isValidSession(session))
		{
			return session.getSchema_name();
		}

		throw new IOException(MessageConstants.INVALID_SESSION_ID_MSG);
	}
	
	public UserSession getUserSessionUsingSessionId(HttpServletRequest httpRequest, String sessionId) throws FileNotFoundException, IOException, InvalidSessionException
	{
		UserSession session = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);
		if (isValidSession(session))
		{
			return session;
		}

		throw new IOException(MessageConstants.INVALID_SESSION_ID_MSG);
	}
	
	public String getSchemaNameUsingWithoutSessionCheck(HttpServletRequest httpRequest, String sessionId) throws FileNotFoundException, IOException, InvalidSessionException
	{
		UserSession session = GlobalSchemaEntityManager.getInstance().getUserSessionWithoutSessionCheck(httpRequest, sessionId);
		return session.getSchema_name();
		
	}
	

	public String getSchemaNameUsingReffrenceNo(HttpServletRequest httpRequest, String referenceNumber) throws Exception
	{

		referenceNumber = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		checkReferenceNumber(referenceNumber);;
		EntityManager globalEm = null;
		try
		{
			globalEm = GlobalSchemaEntityManager.getInstance().getEntityManager();

			CriteriaBuilder builder = globalEm.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);
			TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

			POSNPartners posnPartner = query.getSingleResult();
			if (posnPartner == null)
			{
				throw new IOException(MessageConstants.REFRENCE_NUMBER_EXCEPTION);
			}
			int accountId = posnPartner.getAccountId();
			Account account = getAccountById(accountId, globalEm);
			if (account == null)
			{
				throw new IOException(MessageConstants.ACCOUNT_EXCEPTION);
			}
			String schemaName = account.getSchemaName();
			if (schemaName == null)
			{
				throw new IOException(MessageConstants.SCHEMA_NAME_EXCEPTION);
			}
			return schemaName;
		}
		finally
		{

			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEm);
		}

	}
	

	public EntityManager getEntityManager(HttpServletRequest httpRequest, String sessionId) throws IOException, InvalidSessionException
	{
		if (sessionId == null || sessionId.length() < MIN_SESSION_ID_LENGTH)
		{
			sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		}

		if (sessionId != null && sessionId.length() > MIN_SESSION_ID_LENGTH)
		{
			String schema = getSchemaNameUsingSessionId(httpRequest, sessionId);

			return getEntityManagerUsingName(schema);

		}
		else
		{
			throw new InvalidSessionException();
		}

	}

	public EntityManager getEntityManager(HttpServletRequest httpRequest, String sessionId, PostPacket postPacket) throws FileNotFoundException, InvalidSessionException, IOException
	{

		return getEntityManager(httpRequest, sessionId, postPacket, true, false);

	}

	public EntityManager getEntityManager(HttpServletRequest httpRequest, String sessionId, PostPacket postPacket, boolean isSchemaNameNeeded) throws FileNotFoundException, InvalidSessionException,
			IOException
	{

		return getEntityManager(httpRequest, sessionId, postPacket, isSchemaNameNeeded, false);

	}

	public EntityManager getEntityManagerWithSessionIdInPostPacket(HttpServletRequest httpRequest, String sessionId, PostPacket postPacket) throws FileNotFoundException, InvalidSessionException,
			IOException
	{

		return getEntityManager(httpRequest, sessionId, postPacket, false, true);

	}

	public EntityManager getEntityManager(HttpServletRequest httpRequest, String sessionId, PostPacket postPacket, boolean isSchemNameNeeded, boolean isSessionIdNeedInPostPacket)
			throws FileNotFoundException, InvalidSessionException, IOException
	{
		// assign session id from nirvanaxp token
		if (sessionId == null || sessionId.length() < MIN_SESSION_ID_LENGTH)
		{
			sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		}

		if (sessionId != null && sessionId.length() > MIN_SESSION_ID_LENGTH)
		{
			UserSession userSession = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);

			if (isValidSession(userSession))
			{
				postPacket.setMerchantId("" + userSession.getMerchant_id());
				// so that schema name does not get broadcasted
				if (isSchemNameNeeded == false)
				{
					postPacket.setSchemaName("");
				}
				else
				{
					postPacket.setSchemaName(userSession.getSchema_name());
				}

				if (isSessionIdNeedInPostPacket)
				{
					postPacket.setIdOfSessionUsedByPacket(userSession.getId());
				}

				EntityManagerFactory emf = getEntityManagerFactory(userSession.getSchema_name());

				return emf.createEntityManager();
			}

		}

		throw new InvalidSessionException();

	}

	public EntityManager getEntityManagerForCustomer(HttpServletRequest httpRequest, String sessionId, PostPacket postPacket, String schemaName) throws FileNotFoundException, InvalidSessionException,
			IOException
	{

		if (sessionId != null && sessionId.length() > MIN_SESSION_ID_LENGTH)
		{
			if (schemaName != null)
			{

				UserSession postPacketUserSession = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);
				if (isValidSession(postPacketUserSession))
				{
					// so that schema name does not get broadcasted
					if (schemaName != null)
					{
						postPacket.setSchemaName(schemaName);
					}

					postPacket.setIdOfSessionUsedByPacket(postPacketUserSession.getId());

					EntityManagerFactory emf = getEntityManagerFactory(postPacketUserSession.getSchema_name());

					return emf.createEntityManager();
				}

			}
			else
			{
				throw new IOException(MessageConstants.DATABASE_NAME_EXCEPTION);
			}
		}

		throw new InvalidSessionException();

	}

	@Deprecated
	public EntityManager getEntityManagerForCustomer(HttpServletRequest httpRequest, String sessionId, String schemaName) throws FileNotFoundException, IOException, InvalidSessionException
	{

		if (sessionId != null && sessionId.length() > MIN_SESSION_ID_LENGTH)
		{
			if (schemaName != null)
			{

				String schema = getSchemaNameUsingSessionId(httpRequest, sessionId);

				EntityManagerFactory emf = getEntityManagerFactory(schema.toUpperCase());

				return emf.createEntityManager();

			}
			else
			{
				throw new IOException(MessageConstants.DATABASE_NAME_EXCEPTION);
			}
		}

		throw new InvalidSessionException();

	}

	public EntityManager getEntityManagerUsingReferenceNumber(HttpServletRequest httpRequest, String referenceNumber) throws FileNotFoundException, IOException
	{
		// checkReferenceNumber(referenceNumber);
		if(referenceNumber == null)
		{
			referenceNumber = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);	
		}
		checkReferenceNumber(referenceNumber);
		EntityManager globalEm = null;

		try
		{
			globalEm = GlobalSchemaEntityManager.getInstance().getEntityManager();
			CriteriaBuilder builder = globalEm.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);
			TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

			POSNPartners posnPartner = query.getSingleResult();
			if (posnPartner == null)
			{
				throw new IOException(MessageConstants.REFRENCE_NUMBER_EXCEPTION);
			}

			int accountId = posnPartner.getAccountId();
			Account account = getAccountById(accountId, globalEm);
			String schemaName = account.getSchemaName();	
			UserSession userSession = new UserSession();
			userSession.setSchema_name(schemaName);

			httpRequest.setAttribute(INirvanaService.NIRVANA_USER_SESSION, userSession);
			UserSession postPacketWithSchemaName = new UserSession();
			postPacketWithSchemaName.setSchema_name(schemaName);

			if (schemaName == null)
			{
				throw new IOException(MessageConstants.SCHEMA_NAME_EXCEPTION);
			}

			EntityManagerFactory emf = getEntityManagerFactory(schemaName);

			if (emf == null)
			{
				throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE);
			}

			EntityManager LocalEm = emf.createEntityManager();

			return LocalEm;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEm);
		}
	}

	private Account getAccountById(int accountId, EntityManager em)
	{

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
		Root<Account> account = criteria.from(Account.class);
		TypedQuery<Account> query = em.createQuery(criteria.select(account).where(builder.equal(account.get(Account_.id), accountId)));
		return query.getSingleResult();

	}

	@Override
	protected NirvanaLogger getLogger()
	{
		return logger;
	}

	public EntityManager getEntityManagerForNirvanaXP(HttpServletRequest httpRequest, String sessionId) throws FileNotFoundException, InvalidSessionException, IOException
	{
		String nirvanaXPDatabaseName = ConfigFileReader.getNirvanaXPDatabaseNameFromFile();
		// assign session id from nirvanaxp token
		if (sessionId == null || sessionId.length() < MIN_SESSION_ID_LENGTH)
		{
			sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		}

		if (sessionId != null && sessionId.length() > MIN_SESSION_ID_LENGTH)
		{
			if (nirvanaXPDatabaseName != null)
			{

				UserSession postPacketUserSession = GlobalSchemaEntityManager.getInstance().getUserSession(httpRequest, sessionId);
				if (isValidSession(postPacketUserSession))
				{
					// so that schema name does not get broadcasted
					EntityManagerFactory emf = getEntityManagerFactory(nirvanaXPDatabaseName);
					return emf.createEntityManager();
				}

			}
			else
			{
				throw new IOException(MessageConstants.DATABASE_NAME_EXCEPTION);
			}
		}

		throw new InvalidSessionException();

	}

	public EntityManager getEntityManagerUsingReferenceNumberForNirvanaXP(HttpServletRequest httpRequest, String referenceNumber) throws Exception
	{
		referenceNumber = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		checkReferenceNumber(referenceNumber);
		EntityManager globalEm = null;
		try
		{
			globalEm = GlobalSchemaEntityManager.getInstance().getEntityManager();

			EntityManager em = getEntityManagerForNirvanaXP();
			CriteriaBuilder builder = globalEm.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);
			TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

			POSNPartners posnPartner = query.getSingleResult();
			if (posnPartner == null)
			{
				throw new NirvanaXPException(new NirvanaServiceErrorResponse("DB1001", MessageConstants.DATABASE_NAME_EXCEPTION, MessageConstants.DATABASE_NAME_EXCEPTION));
			}

			return em;
		}
		finally
		{
			GlobalSchemaEntityManager.getInstance().closeEntityManager(globalEm);
		}
	}

	public EntityManager getEntityManagerForNirvanaXP() throws FileNotFoundException, IOException, NirvanaXPException
	{
		String nirvanaXPDatabaseName = ConfigFileReader.getNirvanaXPDatabaseNameFromFile();
		if (nirvanaXPDatabaseName != null)
		{
			EntityManagerFactory emf = getEntityManagerFactory(nirvanaXPDatabaseName);
			EntityManager em = emf.createEntityManager();
			return em;
		}
		else
		{
			throw new NirvanaXPException(new NirvanaServiceErrorResponse("DB1001", MessageConstants.DATABASE_NAME_EXCEPTION, MessageConstants.DATABASE_NAME_EXCEPTION));
		}
	}
	public EntityManager getEntityManagerForThread(HttpServletRequest httpServletRequest,String sessionId) throws IOException
	{
		EntityManager em = null;
		try
		{ 
			String schemaName = getSchemaNameUsingWithoutSessionCheck(httpServletRequest, sessionId);
			em = getEntityManagerUsingName(schemaName);
		}
		catch (Exception e)
		{
			throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE, e);
		}

		return em;
	}
}
