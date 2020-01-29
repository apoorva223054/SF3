/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.authentication;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.nirvanaxp.global.types.entities.Role;
import com.nirvanaxp.global.types.entities.User;
import com.nirvanaxp.global.types.entities.accounts.ServerConfig;

public class POSNirvanaUser
{

	private User user;
	private String authenticationToken;
	private String encryptionKey;

	private String localServerUrl;

	private ServerConfig serverConfig;

	/**
	 * @param name
	 */
	public POSNirvanaUser(User user)
	{

		if (user == null)
		{
			throw new NullPointerException("NULL user");
		}
		this.user = user;
	}

	public User getNirvanaUser()
	{
		return user;
	}

	public String getName()
	{
		return user.getUsername();
	}

	@Override
	public String toString()
	{
		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			Writer strWriter = new StringWriter();
			user.setPassword(null);
			objectMapper.writeValue(strWriter, user);
			String jsonString = strWriter.toString();
			return jsonString;
		}
		catch (IOException ioe)
		{
			throw new RuntimeException(ioe.getMessage());
		}

	}

	public Set<Role> getUserRoles()
	{
		if (user != null)
		{
			return user.getRolesSet();
		}
		return null;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user.getUsername() == null) ? 0 : user.getUsername().hashCode());
		return result;
	}

	public String getAuthenticationToken()
	{
		return authenticationToken;
	}

	public void setAuthenticationToken(String authenticationToken)
	{
		this.authenticationToken = authenticationToken;
	}

	public String getEncryptionKey()
	{
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey)
	{
		this.encryptionKey = encryptionKey;
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
		POSNirvanaUser other = (POSNirvanaUser) obj;
		if (user.getUsername() == null)
		{
			if (other.user.getUsername() != null)
				return false;
		}
		else if (!user.getUsername().equals(other.user.getUsername()))
			return false;

		return true;
	}

	public String getLocalServerUrl()
	{
		return localServerUrl;
	}

	public void setLocalServerUrl(String localServerUrl)
	{
		this.localServerUrl = localServerUrl;
	}

	public ServerConfig getServerConfig()
	{
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig)
	{
		this.serverConfig = serverConfig;
	}


}
