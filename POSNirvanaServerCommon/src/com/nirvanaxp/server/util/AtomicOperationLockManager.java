/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.server.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.nirvanaxp.common.utils.ConfigFileReader;
import com.nirvanaxp.common.utils.synchistory.TimezoneTime;
import com.nirvanaxp.global.types.entities.UserSession;
import com.nirvanaxp.services.jaxrs.AbstractNirvanaService;
import com.nirvanaxp.types.entities.concurrent.AtomicOperation.Type;
import com.nirvanaxp.types.entities.concurrent.ProcessLock;

public class AtomicOperationLockManager
{

	private static final NirvanaLogger logger = new NirvanaLogger(AtomicOperationLockManager.class.getName());

	// TODO - this map should be moved to database as a table called
	// atomic_operation_locks (payment_process_type_code, location_id,
	// expire_time)
	// the primary key can be id, but uniqueness should be
	// (payment_process_type_code + location_id)
	private static Map<Type, Map<String, ProcessLock>> CurrentInProgressMap = new HashMap<Type, Map<String, ProcessLock>>();

	private AtomicOperationLockManager()
	{

	}

	private static class AtomicOperationLockManagerSingleton
	{
		private static final AtomicOperationLockManager THE_INSTANCE = new AtomicOperationLockManager();
	}

	public static AtomicOperationLockManager getInstance()
	{
		return AtomicOperationLockManagerSingleton.THE_INSTANCE;
	}

	public synchronized ProcessLock getAtomicOperationLock(HttpServletRequest httpRequest, Type taskType, String locationId)
	{
		String internalKey = getCurrentProgressMapInternalKey(getCurrentSchemaName(httpRequest), locationId);
		Map<String, ProcessLock> internalMap = CurrentInProgressMap.get(taskType);

		// if there is already a lock, which has not expired, return false

		// TODO - there should be a way for authorized users (admin/support) to
		// override or expire locks
		if (internalMap != null && internalMap.get(internalKey) != null && internalMap.get(internalKey).getLockExpirationTime().getTime() > new TimezoneTime().getGMTTimeInMilis())
		{
			return null;
		}

		// initialize Map
		if (internalMap == null)
		{
			CurrentInProgressMap.put(taskType, new HashMap<String, ProcessLock>());
			internalMap = CurrentInProgressMap.get(taskType);
		}

		// init lock
		ProcessLock lock = null;

		try
		{
			long exp = new TimezoneTime().getGMTTimeInMilis() + ConfigFileReader.readLockExpirationLimit();
			String sessionId = getUserSessionId(httpRequest);
			logger.info("Issuing Lock for task", taskType.name(), "to session", sessionId, "which will expire on", new Date(exp).toString());
			lock = new ProcessLock(0, locationId, taskType, new Timestamp(exp), sessionId);

			// add lock for this location, replacing any previous lock, if
			// present
			internalMap.put(internalKey, lock);
		}
		catch (IOException e)
		{
			logger.severe(httpRequest, e, "Could not create lock object");
		}

		return lock;
	}

	private String getUserSessionId(HttpServletRequest httpRequest)
	{
		return ((UserSession) httpRequest.getAttribute(AbstractNirvanaService.NIRVANA_USER_SESSION)).getSession_id();
	}

	private String getCurrentSchemaName(HttpServletRequest httpRequest)
	{
		return ((UserSession) httpRequest.getAttribute(AbstractNirvanaService.NIRVANA_USER_SESSION)).getSchema_name();
	}

	public synchronized boolean removeAtomicOperationLock(HttpServletRequest httpRequest, ProcessLock lock)
	{
		Type lockType = null;
		try
		{
			lockType = lock!=null && lock.getAtomicOperation()!=null?lock.getAtomicOperation():null;
			if (lockType!=null)
			{
				Map<String, ProcessLock> internalMap = CurrentInProgressMap.get(lockType);

				if (internalMap == null)
				{
					logger.severe(httpRequest, "There is no map of locks when attempting to relase lock", lock.toString());
					return true;
				}
				
				if(lock.getLockExpirationTime().getTime() < new TimezoneTime().getGMTTimeInMilis())
				{
					logger.info("Lock has already expired at", lock.getLockExpirationTime().toString());
					return true;
				}

				String internalKey = getCurrentProgressMapInternalKey(getCurrentSchemaName(httpRequest), lock.getLocationId());

				ProcessLock currentLock = internalMap.get(internalKey);
				if (currentLock != null && currentLock.equals(lock))
				{
					internalMap.remove(internalKey);
					return true;
				}
				
				logger.info("Did not find any existing lock to remove for input", lock.toString());
			}
		}
		catch (Throwable t)
		{
			logger.severe(httpRequest, t, "Could not release atomic operation lock for process type", lockType!=null?lockType.name():"No lock type found");
		}

		return false;
	}

	private String getCurrentProgressMapInternalKey(String schemaName, String locationId)
	{
		return schemaName + ":" + locationId;
	}

}
