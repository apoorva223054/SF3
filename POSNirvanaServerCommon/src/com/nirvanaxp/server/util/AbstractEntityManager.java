/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.UserCredentials;
import com.nirvanaxp.common.utils.synchistory.MessageConstants;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.services.exceptions.InvalidSessionException;

abstract class AbstractEntityManager
{

	protected int MIN_SESSION_ID_LENGTH = 25;

	protected String NAME_LOCAL_SCHEMA_PERSISTENCE_UNIT = "POSNirvanaORM";

	protected String NAME_GLOBAL_SCHEMA_PERSISTENCE_UNIT = "POSNGlobalORM";

	protected abstract NirvanaLogger getLogger();

	protected static Map<String, EMFTracker> entityManagers = new HashMap<String, EMFTracker>();
	
	private class EMFTracker {
		
		private EntityManagerFactory emf;
		private long issueTime;
		private long cacheResetTime;
		
		EMFTracker(EntityManagerFactory emf, long issueTime)
		{
			this.emf = emf;
			this.issueTime = issueTime;
			this.cacheResetTime = issueTime;
		}

		EntityManagerFactory getEmf()
		{
			return emf;
		}

		long getIssueTime()
		{
			return issueTime;
		}
		
		void setIssueTime()
		{
			long now = new TimezoneTime().getGMTTimeInMilis();
			this.issueTime = now;
			
			// and if it has been more than 30 minutes since cache reset
			if(cacheResetTime<(now-30*60*1000l))
			{
				// then clear L2 cache
				// emf.getCache().evictAll(); // commenting this out to see if this is the cause of connection leak errors
				cacheResetTime = now;
			}
		}
		
	}

	static String databasePassword = "";
	static String username = "";
	static String databaseString = null;

	private void initializeDBCredentials() throws FileNotFoundException, IOException
	{
		UserCredentials userCredentials = ConfigFileReader.readUsernamePasswordFromFile();
		if (userCredentials != null)
		{
			username = userCredentials.getUsername();
			databasePassword = userCredentials.getPassword();
			databaseString = userCredentials.getDatabaseString();
		}
	}

	private Map<String, String> createMap(String databaseSchema) throws FileNotFoundException, IOException
	{
		initializeDBCredentials();
		Map<String, String> map = new HashMap<String, String>();
		map.put("javax.persistence.jdbc.user", username);
		map.put("javax.persistence.jdbc.password", databasePassword);
		map.put("javax.persistence.jdbc.url", databaseString + databaseSchema + "?useSSL=false");

		return map;

	}

	protected synchronized EntityManagerFactory getEntityManagerFactory(String persistenceUnit, String schemaName) throws FileNotFoundException, IOException
	{
		if(persistenceUnit==null || schemaName==null)
		{
			getLogger().severe("Invalid input to get EMF: PU:", persistenceUnit, "Schema:", schemaName );
			return null;
		}
		
		String fullEMFName = persistenceUnit + ":" + schemaName.toUpperCase();
		
		// take time to clear old and idle factories
		// except the one being asked, even if it is old
		closeIdleFactories(fullEMFName);
					
		EMFTracker emfTracker = entityManagers.get(fullEMFName);
		if (isCachedEMF(emfTracker))
		{
			getLogger().finest("returning entitymanager for schema: ", fullEMFName, " for Thread: ", Thread.currentThread().getName());
						
			// update issue time, because it just got called
			emfTracker.setIssueTime();
			
			return emfTracker.getEmf();
		}
		else
		{			
			EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit, createMap(schemaName));
			entityManagers.put(fullEMFName, new EMFTracker(emf, new TimezoneTime().getGMTTimeInMilis()));
			getLogger().info("adding entitymanager to map for schema: ", fullEMFName, ", for Thread: ", Thread.currentThread().getName(), ", in Map: ", entityManagers.toString(),
					", current EMF count: ", "" + entityManagers.size(), ", Names: ", entityManagers.keySet().toString());

			return emf;
		}
	}

	private boolean isCachedEMF(EMFTracker emfTracker)
	{
		if(emfTracker==null)
		{
			return false;
		}
		
		return emfTracker.getEmf()!=null && emfTracker.getEmf().isOpen();
	}

	private void closeIdleFactories(String askedEMF)
	{
		if (entityManagers != null && entityManagers.values() != null)
		{
			Set<String> delKeys = new HashSet<String>();
			for (String name: entityManagers.keySet())
			{
				if(name!=null && name.equals(askedEMF))
				{
					// do not close the one being asked
					continue;
				}
				
				EMFTracker emft = entityManagers.get(name);
				try
				{
					// if it has been more than 30 minutes since last time this EMF was used, remove it
					if(emft!=null && emft.getEmf()!=null && emft.getIssueTime()<(new TimezoneTime().getGMTTimeInMilis()-30*60*1000l))
					{	
						delKeys.add(name);
					}
				}
				catch (Throwable t)
				{
					getLogger().severe(t, "Error while closing Entity Manager Factory: " + t.getMessage());
				}				
			}

			if(!delKeys.isEmpty())
			{
				for(String name: delKeys)
				{
					entityManagers.get(name).getEmf().close();
					entityManagers.remove(name);	
				}
			}
		}
	}

	protected boolean isValidSession(UserSession session) throws InvalidSessionException
	{
		// TODO, this needs to be more than just a null check
		// we also need to do time check and user principal check
		if (session == null)
		{
			throw new InvalidSessionException();
		}

		return true;
	}

	public void closeEntityManager(EntityManager em)
	{

		try
		{
			if (em != null && em.isOpen())
			{
				try
				{
					// this will detach objects connected to EM
					em.clear();
				}
				finally
				{
					try
					{
						if (em != null)
						{
							em.close();
							em = null;
						}
					}
					catch (Exception e)
					{
						getLogger().severe(e, "Error while closing out EntityManager");
					}
				}
			}
		}
		catch (Throwable e)
		{
			if (!"EntityManager is closed".equals(e.getMessage()))
			{
				getLogger().severe(e, "Error while closing out EntityManager");
			}
		}

	}

	public synchronized void closeEntityManagerFactory()
	{
		if (entityManagers != null && entityManagers.values() != null)
		{
			for (EMFTracker emfTracker : entityManagers.values())
			{
				try
				{
					emfTracker.getEmf().close();
				}
				catch (Throwable t)
				{
					getLogger().severe(t, "Error while closing Entity Manager Factory: " + t.getMessage());
				}
			}

			try
			{
				entityManagers.clear();
			}
			catch (Throwable t)
			{
				getLogger().severe(t, "Error while cleaning Entity Manager List: " + t.getMessage());
			}
		}

	}

	public static void persist(EntityManager em, Object obj)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.persist(obj);
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

	public static void merge(EntityManager em, Object obj)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.merge(obj);
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

	public static void remove(EntityManager em, Object obj)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.remove(obj);
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
	
	public static void removeAfterMerge(EntityManager em, Object obj)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			// start transaction
			tx.begin();
			em.remove(em.merge(obj));
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

	public static void executeNativeQueryUpdate(EntityManager em, Query query)
	{
		EntityTransaction tx = em.getTransaction();
		try
		{
			tx.begin();
			query.executeUpdate();
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

	protected void checkReferenceNumber(String referenceNumber) throws IOException
	{
		// TODO reference number must be of form UUID
		if(referenceNumber==null || referenceNumber.isEmpty())
		{
			throw new IOException(MessageConstants.REFRENCE_NUMBER_EXCEPTION+"-"+referenceNumber);
		}
	}
}
