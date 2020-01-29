/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

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
 * The persistent class for the users_to_roles database table.
 * 
 */
@Entity
@Table(name = "users_to_roles")
@XmlRootElement(name = "users_to_roles")
public class UsersToRole implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "status")
	private String status;

	@Column(name = "primary_role_ind", length = 1)
	private String primaryRoleInd;

	@Column(name = "roles_id", nullable = false)
	private String rolesId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	public UsersToRole()
	{
	}

	public UsersToRole(String createdBy, String status, String rolesId, String updatedBy, String usersId)
	{
		super();
		this.createdBy = createdBy;
		this.status = status;
		this.rolesId = rolesId;
		this.updatedBy = updatedBy;
		this.usersId = usersId;
	}

 

	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrimaryRoleInd()
	{
		return this.primaryRoleInd;
	}

	public void setPrimaryRoleInd(String primaryRoleInd)
	{
		this.primaryRoleInd = primaryRoleInd;
	}

 

	public String getRolesId() {
		if(rolesId != null && (rolesId.length()==0 || rolesId.equals("0"))){return null;}else{	return rolesId;}
	}

	public void setRolesId(String rolesId) {
		this.rolesId = rolesId;
	}

	public String getUsersId()
	{
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId)
	{
		this.usersId = usersId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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
		return "UsersToRole [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", status=" + status
				+ ", primaryRoleInd=" + primaryRoleInd + ", rolesId=" + rolesId
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", usersId=" + usersId + "]";
	}

}