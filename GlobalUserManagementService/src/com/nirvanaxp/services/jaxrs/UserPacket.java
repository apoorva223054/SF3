/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.services.jaxrs.packets.PostPacket;

@XmlRootElement(name = "UserPostPacket")
public class UserPacket extends PostPacket
{

	private User user;

	private Set<Address> addressList;

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public Set<Address> getAddressList()
	{
		return addressList;
	}

	public void setAddressList(Set<Address> addressList)
	{
		this.addressList = addressList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "UserPacket [user=" + user + ", addressList=" + addressList + "]";
	}

}
