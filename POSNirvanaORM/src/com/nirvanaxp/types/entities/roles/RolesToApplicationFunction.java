/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.roles;

import java.io.Serializable;
import java.math.BigInteger;
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


/**
 * The persistent class for the roles_to_application_function database table.
 * 
 */
@Entity
@Table(name = "roles_to_application_function")
@XmlRootElement(name = "roles_to_application_function")
public class RolesToApplicationFunction implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "application_id")
	private int applicationId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "functions_id", nullable = false)
	private String functionsId;

	@Column(name = "role_id", nullable = false)
	private int roleId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	public RolesToApplicationFunction()
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

	public int getApplicationId()
	{
		return this.applicationId;
	}

	public void setApplicationId(int applicationId)
	{
		this.applicationId = applicationId;
	}

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

	}

	public String getFunctionsId()
	{
		 if(functionsId != null && (functionsId.length()==0 || functionsId.equals("0"))){return null;}else{	return functionsId;}
	}

	public void setFunctionsId(String functionsId)
	{
		this.functionsId = functionsId;
	}

	public int getRoleId()
	{
		return this.roleId;
	}

	public void setRoleId(int roleId)
	{
		this.roleId = roleId;
	}

	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}

	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
		}
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	 

	@Override
	public String toString() {
		return "RolesToApplicationFunction [id=" + id + ", applicationId="
				+ applicationId + ", created=" + created + ", createdBy="
				+ createdBy + ", functionsId=" + functionsId + ", roleId="
				+ roleId + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ "]";
	}

}