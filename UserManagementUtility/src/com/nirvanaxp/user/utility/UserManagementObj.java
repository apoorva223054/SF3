/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.user.utility;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.Address;
import com.nirvanaxp.types.entities.user.User;

@XmlRootElement(name = "UserManagementObj")
public class UserManagementObj
{

	User user;
	
	int userExist;

	public int getUserExist() {
		return userExist;
	}

	public void setUserExist(int userExist) {
		this.userExist = userExist;
	}

	Set<Address> addressSet;

	private String response;

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getResponse()
	{
		return response;
	}

	public void setResponse(String response)
	{
		this.response = response;
	}

	public void setAddressSet(Set<Address> addressSet)
	{
		this.addressSet = addressSet;
	}

	public Set<Address> getAddressSet()
	{
		return addressSet;
	}

}
