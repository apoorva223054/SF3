/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.concurrent;

import java.sql.Timestamp;

import com.nirvanaxp.types.entities.concurrent.AtomicOperation.Type;

// TODO - convert this to entity when we have database table
public class ProcessLock
{

	private int id;

	private String locationId;

	private Type atomicOperation;

	private Timestamp lockExpirationTime;

	private String sessionId;

	public ProcessLock(int id, String locationId, Type atomicOperation, Timestamp lockExpirationTime, String sessionId)
	{
		super();
		this.id = id;
		this.locationId = locationId;
		this.atomicOperation = atomicOperation;
		this.lockExpirationTime = lockExpirationTime;
		this.sessionId = sessionId;
	}

	public int getId()
	{
		return id;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public Type getAtomicOperation()
	{
		return atomicOperation;
	}

	public Timestamp getLockExpirationTime()
	{
		return lockExpirationTime;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atomicOperation == null) ? 0 : atomicOperation.hashCode());
		result = prime * result + id;
	
		result = prime * result + ((lockExpirationTime == null) ? 0 : lockExpirationTime.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessLock other = (ProcessLock) obj;
		if (atomicOperation != other.atomicOperation)
			return false;
		if (id != other.id)
			return false;
		if (locationId != other.locationId)
			return false;
		if (lockExpirationTime == null)
		{
			if (other.lockExpirationTime != null)
				return false;
		}
		else if (!lockExpirationTime.equals(other.lockExpirationTime))
			return false;
		if (sessionId == null)
		{
			if (other.sessionId != null)
				return false;
		}
		else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "ProcessLock [id=" + id + ", locationId=" + locationId + ", atomicOperation=" + atomicOperation + ", lockExpirationTime=" + lockExpirationTime + ", sessionId=" + sessionId + "]";
	}
	
}
