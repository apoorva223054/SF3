/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

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
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name = "roles")
@XmlRootElement(name = "roles")
public class Role implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "application_name")
	private String applicationName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "function_name")
	private String functionName;

	@Column(name = "role_name")
	private String roleName;

	@Column(name = "display_name")
	private String displayName;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "account_id")
	private int accountId;

	public Role()
	{
	}

	public Role(int id)
	{
		this.id = id;
	}

	public Role(String createdBy, String roleName, String displayName, String status, String updatedBy, int accountId)
	{
		super();
		this.createdBy = createdBy;
		this.roleName = roleName;
		this.displayName = displayName;
		this.status = status;
		this.updatedBy = updatedBy;
		this.accountId = accountId;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getApplicationName()
	{
		return this.applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public String getFunctionName()
	{
		return this.functionName;
	}

	public void setFunctionName(String functionName)
	{
		this.functionName = functionName;
	}

	public String getRoleName()
	{
		return this.roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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
	public boolean equals(Object otherObj)
	{
		if (otherObj != null && otherObj instanceof Role)
		{
			Role rolesCompare = (Role) otherObj;
			if (id == rolesCompare.getId())
			{
				return true;

			}
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", applicationName=" + applicationName
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", functionName=" + functionName + ", roleName=" + roleName
				+ ", displayName=" + displayName + ", status=" + status
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", accountId=" + accountId + "]";
	}

}