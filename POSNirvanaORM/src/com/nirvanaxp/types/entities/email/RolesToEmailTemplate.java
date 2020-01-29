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
 * The persistent class for the roles_to_email_template database table.
 * 
 */
@Entity
@Table(name = "roles_to_email_template")
@NamedQuery(name = "RolesToEmailTemplate.findAll", query = "SELECT b FROM RolesToEmailTemplate b")
public class RolesToEmailTemplate extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Column(name = "roles_id")
	private String rolesId;
	
	public String getRolesId() {
		if(rolesId != null && (rolesId.length()==0 || rolesId.equals("0"))){return null;}else{	return rolesId;}
	}

	public void setRolesId(String rolesId) {
		this.rolesId = rolesId;
	}

	@Column(name = "email_template_id")
	private int emailTemplateId;

	@Column(name = "location_id")
	private String locationId;

	public RolesToEmailTemplate()
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
		return "RolesToEmailTemplate [rolesId=" + rolesId
				+ ", emailTemplateId=" + emailTemplateId + ", locationId="
				+ locationId + "]";
	}

	
}