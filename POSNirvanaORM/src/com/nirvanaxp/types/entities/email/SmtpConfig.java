/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.email;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the smtp_config database table.
 * 
 */
@Entity
@Table(name = "smtp_config")
@NamedQuery(name = "SmtpConfig.findAll", query = "SELECT s FROM SmtpConfig s")
public class SmtpConfig extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "sender_email")
	private String senderEmail;

	@Column(name = "smtp_host")
	private String smtpHost;

	@Column(name = "smtp_password")
	private String smtpPassword;

	@Column(name = "smtp_port")
	private String smtpPort;

	@Column(name = "smtp_username")
	private String smtpUsername;
	
	
	public SmtpConfig()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getSenderEmail()
	{
		return this.senderEmail;
	}

	public void setSenderEmail(String senderEmail)
	{
		this.senderEmail = senderEmail;
	}

	public String getSmtpHost()
	{
		return this.smtpHost;
	}

	public void setSmtpHost(String smtpHost)
	{
		this.smtpHost = smtpHost;
	}

	public String getSmtpPassword()
	{
		return this.smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword)
	{
		this.smtpPassword = smtpPassword;
	}

	public String getSmtpPort()
	{
		return this.smtpPort;
	}

	public void setSmtpPort(String smtpPort)
	{
		this.smtpPort = smtpPort;
	}

	public String getSmtpUsername()
	{
		return this.smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername)
	{
		this.smtpUsername = smtpUsername;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	@Override
	public String toString() {
		return "SmtpConfig [locationId=" + locationId + ", senderEmail="
				+ senderEmail + ", smtpHost=" + smtpHost + ", smtpPassword="
				+ smtpPassword + ", smtpPort=" + smtpPort + ", smtpUsername="
				+ smtpUsername + "]";
	}

}