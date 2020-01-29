
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
import com.nirvanaxp.global.types.entities.UserSession_;
import com.nirvanaxp.global.types.entities.accounts.Account;
import com.nirvanaxp.global.types.entities.partners.POSNPartners;
import com.nirvanaxp.global.types.entities.partners.POSNPartners_;
import com.nirvanaxp.services.exceptions.InvalidSessionException;
import com.nirvanaxp.services.jaxrs.INirvanaService;

public class GlobalSchemaEntityManager extends AbstractEntityManager
{

	private static final NirvanaLogger logger = new NirvanaLogger(GlobalSchemaEntityManager.class.getName());

	private static class SingletonHolder
	{
		private static final GlobalSchemaEntityManager INSTANCE = new GlobalSchemaEntityManager();
	}

	private GlobalSchemaEntityManager()
	{
	}

	public static GlobalSchemaEntityManager getInstance()
	{
		logger.finest("returning global schema entity manager wrapper instance:" + SingletonHolder.INSTANCE);
		return SingletonHolder.INSTANCE;
	}

	// TODO - This should be used only from admin flows or login
	public EntityManager getEntityManager() throws FileNotFoundException, IOException
	{
		String globalDatabaseName = ConfigFileReader.getGlobalDatabaseNameFromFile();
		if (globalDatabaseName != null)
		{
			EntityManagerFactory emf = getEntityManagerFactory(globalDatabaseName);
			EntityManager em = emf.createEntityManager();
			return em;
		}
		else
		{
			throw new IOException(MessageConstants.DATABASE_NAME_EXCEPTION);
		}
	}

	private EntityManagerFactory getEntityManagerFactory(String schemaName) throws FileNotFoundException, IOException
	{
		return getEntityManagerFactory(NAME_GLOBAL_SCHEMA_PERSISTENCE_UNIT, schemaName);
	}

	public EntityManager getEntityManager(HttpServletRequest httpRequest, String sessionId) throws IOException, InvalidSessionException
	{
		// TODO check that session id is a UUID and UUID format
		if (sessionId == null || sessionId.length() < MIN_SESSION_ID_LENGTH)
		{
			sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
		}
		
		if(sessionId==null || sessionId.length() < MIN_SESSION_ID_LENGTH)
		{
			throw new InvalidSessionException();
		}
		
		// if we get here then there is no EM on request
		EntityManager em = getEntityManager();
		UserSession session = getUserSession(sessionId, em);
			
		isValidSession(session);

		return em;
		
	}

	public UserSession getUserSession(HttpServletRequest httpRequest, String sessionId) throws FileNotFoundException, IOException, InvalidSessionException
	{
		EntityManager em = getEntityManager();
		try
		{			
			if (sessionId == null || sessionId.length() < MIN_SESSION_ID_LENGTH)
			{
				sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
			}
			UserSession currentUS = (UserSession) httpRequest.getAttribute(INirvanaService.NIRVANA_USER_SESSION);
			if(currentUS==null)
			{
				UserSession us = getUserSession(sessionId, em);
				httpRequest.setAttribute(INirvanaService.NIRVANA_USER_SESSION, us);
				return us;
			}
			else if(!currentUS.getSession_id().equals(sessionId))
			{
				throw new InvalidSessionException();
			}
			return currentUS;
		}
		finally
		{
			closeEntityManager(em);
		}
	}
	public UserSession getUserSessionWithoutSessionCheck(HttpServletRequest httpRequest, String sessionId) throws FileNotFoundException, IOException, InvalidSessionException
	{
		EntityManager em = getEntityManager();
		try
		{			
			if (httpRequest !=null && (sessionId == null || sessionId.length() < MIN_SESSION_ID_LENGTH))
			{
				sessionId = httpRequest.getHeader(INirvanaService.NIRVANA_ACCESS_TOKEN_HEADER_NAME);
			}
			UserSession us = getUserSession(sessionId, em);
			 
			return us;
		}
		finally
		{
			closeEntityManager(em);
		}
	}

	
	@Deprecated
	/**
	 * This method needs to be removed when we can do better payment processing
	 * @param sessionId
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidSessionException
	 */
	public UserSession getUserSessionForPaymentProcessing(String sessionId) throws FileNotFoundException, IOException, InvalidSessionException
	{
		EntityManager em = getEntityManager();
		try
		{
			UserSession us = getUserSession(sessionId, em);
			return us;		
		}
		finally
		{
			closeEntityManager(em);
		}
	}
	
	public UserSession getUserSession(String sessionId, EntityManager em) throws InvalidSessionException 
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserSession> criteria = builder.createQuery(UserSession.class);
		Root<UserSession> root = criteria.from(UserSession.class);
		TypedQuery<UserSession> query = em.createQuery(criteria.select(root).where(builder.equal(root.get(UserSession_.session_id), sessionId)));
		try
		{
			return query.getSingleResult();
		}
		catch (Exception e)
		{
			logger.severe(e, "Error while getting User Session for Session Id: ", sessionId, ", message= " + e.getMessage());
			throw new InvalidSessionException();
		}

	}

	public EntityManager getEntityManagerUsingName(String schemaName) throws IOException, InvalidSessionException
	{
		EntityManager em = null;
		try
		{
			EntityManagerFactory emf = getEntityManagerFactory(schemaName);

			if (emf == null)
			{
				throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE);
			}

			String globalDatabaseName = ConfigFileReader.getGlobalDatabaseNameFromFile();
			if (globalDatabaseName != null)
			{
				emf = getEntityManagerFactory(globalDatabaseName);
				em = emf.createEntityManager();
				return em;
			}
			else
			{
				throw new IOException(MessageConstants.DATABASE_NAME_EXCEPTION);
			}
		}
		catch (Exception e)
		{
			throw new IOException(MessageConstants.UNABLE_TO_OBTAIN_ENTITY_MANAGER_FROM_LOCAL_DATABASE);
		}
	}
	
	public EntityManager getEntityManagerUsingReferenceNumber(HttpServletRequest httpRequest, String referenceNumber) throws IOException
	{
		referenceNumber = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		checkReferenceNumber(referenceNumber);
		EntityManager globalEm = getEntityManager();
		CriteriaBuilder builder = globalEm.getCriteriaBuilder();
		CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
		Root<POSNPartners> root = criteria.from(POSNPartners.class);
		TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

		POSNPartners posnPartner = query.getSingleResult();
		if (posnPartner == null)
		{
			throw new IOException(MessageConstants.REFRENCE_NUMBER_EXCEPTION);
		}

		
		return globalEm;
	}
	
	public EntityManager getEntityManagerUsingAccountId(HttpServletRequest httpRequest, String referenceNumber) throws IOException
	{
		EntityManager globalEm = getEntityManager();
		CriteriaBuilder builder = globalEm.getCriteriaBuilder();
		CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
		Root<POSNPartners> root = criteria.from(POSNPartners.class);
		TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

		POSNPartners posnPartner = query.getSingleResult();
		if (posnPartner == null)
		{
			throw new IOException(MessageConstants.REFRENCE_NUMBER_EXCEPTION);
		}

		
		return globalEm;
	}
	public String getSchemaNameUsingReferenceNumber(HttpServletRequest httpRequest, String referenceNumber) throws FileNotFoundException, IOException
	{
		checkReferenceNumber(referenceNumber);
		String schemaName = null;
		EntityManager globalEm = null;
		try {
			globalEm = getEntityManager();
			CriteriaBuilder builder = globalEm.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);
			TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

			POSNPartners posnPartner = query.getSingleResult();
			
			if (posnPartner == null)
			{
				throw new IOException(MessageConstants.REFRENCE_NUMBER_EXCEPTION);
			}else{
				Account account = globalEm.find(Account.class, posnPartner.getAccountId());
				if(account!= null){
					schemaName = account.getSchemaName();
				}
			}
			return schemaName;
		} finally {
			closeEntityManager(globalEm);
		}
	}
	public int getPOSNPartnerUsingReferenceNumber(HttpServletRequest httpRequest, String referenceNumber) throws FileNotFoundException, IOException
	{
		referenceNumber = httpRequest.getHeader(INirvanaService.NIRVANA_AUTH_TOKEN);
		checkReferenceNumber(referenceNumber);	EntityManager globalEm = null;
		try {
			globalEm = getEntityManager();
			CriteriaBuilder builder = globalEm.getCriteriaBuilder();
			CriteriaQuery<POSNPartners> criteria = builder.createQuery(POSNPartners.class);
			Root<POSNPartners> root = criteria.from(POSNPartners.class);
			TypedQuery<POSNPartners> query = globalEm.createQuery(criteria.select(root).where(builder.equal(root.get(POSNPartners_.referenceNumber), referenceNumber)));

			POSNPartners posnPartner = query.getSingleResult();
			if(posnPartner!=null){
				return posnPartner.getId();
			}
			
			return 0;
			
		} finally {
			closeEntityManager(globalEm);
		}
	}

	@Override
	protected NirvanaLogger getLogger()
	{
		return logger;
	}

}
