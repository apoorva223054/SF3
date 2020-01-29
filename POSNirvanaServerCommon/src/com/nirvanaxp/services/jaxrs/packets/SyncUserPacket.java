/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.user.UsersToRole;

@XmlRootElement(name = "SyncUserPacket")
public class SyncUserPacket extends PostPacket
{

	private String globalUserId;

	private Set<UsersToRole> usersToRolesSet;

	

	public String getGlobalUserId()
	{
		return globalUserId;
	}

	public void setGlobalUserId(String globalUserId)
	{
		this.globalUserId = globalUserId;
	}

	public Set<UsersToRole> getUsersToRolesSet()
	{
		return usersToRolesSet;
	}

	public void setUsersToRolesSet(Set<UsersToRole> usersToRolesSet)
	{
		this.usersToRolesSet = usersToRolesSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "SyncUserPacket [globalUserId=" + globalUserId + ", usersToRolesSet=" + usersToRolesSet + "]";
	}

}
