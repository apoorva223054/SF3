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
 * The persistent class for the business_email_setting database table.
 * 
 */
@Entity
@Table(name = "business_email_setting")
@NamedQuery(name = "BusinessEmailSetting.findAll", query = "SELECT b FROM BusinessEmailSetting b")
public class BusinessEmailSetting extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "email_template_id")
	private int emailTemplateId;

	@Column(name = "location_id")
	private String locationId;

	public BusinessEmailSetting()
	{
	}

	public int getEmailTemplateId()
	{
		return this.emailTemplateId;
	}

	public void setEmailTemplateId(int emailTemplateId)
	{
		this.emailTemplateId = emailTemplateId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "BusinessEmailSetting [emailTemplateId=" + emailTemplateId
				+ ", locationId=" + locationId + "]";
	}

}