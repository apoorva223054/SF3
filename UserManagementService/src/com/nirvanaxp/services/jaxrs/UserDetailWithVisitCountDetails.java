/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs;

import com.nirvanaxp.types.entities.user.User;

public class UserDetailWithVisitCountDetails
{
	private User user;
	private int visitCount;

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public int getVisitCount()
	{
		return visitCount;
	}

	public void setVisitCount(int visitCount)
	{
		this.visitCount = visitCount;
	}

}
