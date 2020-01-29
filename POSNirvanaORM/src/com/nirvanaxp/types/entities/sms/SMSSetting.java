/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.sms;

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
@Table(name = "sms_setting")
@NamedQuery(name = "SMSSetting.findAll", query = "SELECT s FROM SMSSetting s")
public class SMSSetting extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "sender_name")
	private String senderName;

	@Column(name = "password")
	private String password;
	
	@Column(name = "sms_subscriber")
	private String smsSubscriber;

	@Column(name = "url")
	private String url;

	@Column(name = "username")
	private String username;

	public SMSSetting()
	{
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSmsSubscriber() {
		return smsSubscriber;
	}

	public void setSmsSubscriber(String smsSubscriber) {
		this.smsSubscriber = smsSubscriber;
	}



}