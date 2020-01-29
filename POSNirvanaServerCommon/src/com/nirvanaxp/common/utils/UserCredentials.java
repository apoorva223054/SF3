/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils;

public class UserCredentials
{

	private String username;

	private String password;

	private String databaseString;

	public UserCredentials(String username, String password, String databaseString)
	{
		super();
		this.username = username;
		this.password = password;
		this.databaseString = databaseString;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getDatabaseString()
	{
		return databaseString;
	}

	public void setDatabaseString(String databaseString)
	{
		this.databaseString = databaseString;
	}

}
