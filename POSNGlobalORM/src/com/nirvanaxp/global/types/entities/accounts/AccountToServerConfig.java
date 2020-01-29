/**
\ * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.accounts;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "account_to_server_config")
@XmlRootElement(name = "account_to_server_config")
public class AccountToServerConfig implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "accounts_id")
	private int accountsId;

	@Column(name = "server_config_id")
	private int serverConfigId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "subscriber_server_id")
	private int subscriberServerId; 
	 
	@Column(name = "authentication_token")
	private String authenticationToken;
	
	@Column(name = "is_server_url")
	private int isServerUrl;
	
	@Column(name = "location_id")
	private String locationId;
	
	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	public int getAccountsId()
	{
		return accountsId;
	}


	public void setAccountsId(int accountsId)
	{
		this.accountsId = accountsId;
	}


	public int getServerConfigId()
	{
		return serverConfigId;
	}


	public void setServerConfigId(int serverConfigId)
	{
		this.serverConfigId = serverConfigId;
	}


	public Date getUpdated()
	{
		return updated;
	}


	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

 


	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	

	public int getSubscriberServerId()
	{
		return subscriberServerId;
	}


	public void setSubscriberServerId(int subscriberServerId)
	{
		this.subscriberServerId = subscriberServerId;
	}


	public String getAuthenticationToken()
	{
		return authenticationToken;
	}


	public void setAuthenticationToken(String authenticationToken)
	{
		this.authenticationToken = authenticationToken;
	}


	public int getIsServerUrl()
	{
		return isServerUrl;
	}


	public void setIsServerUrl(int isServerUrl)
	{
		this.isServerUrl = isServerUrl;
	}


	@Override
	public String toString()
	{
		return "AccountToServerConfig [id=" + id + ", accountsId=" + accountsId + ", serverConfigId=" + serverConfigId + ", updated=" + updated + ", subscriberServerId=" + subscriberServerId
				+ ", authenticationToken=" + authenticationToken + ", isServerUrl=" + isServerUrl + "]";
	}


	 
 
 
	
	
}
