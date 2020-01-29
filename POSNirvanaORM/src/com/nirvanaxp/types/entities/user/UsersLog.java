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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the users_log database table.
 * 
 */
@Entity
@Table(name = "users_log")
@XmlRootElement(name = "users_log")
public class UsersLog implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "login_time", nullable = false)
	private Date loginTime;

	@Column(name = "logout_time", nullable = false)
	private Date logoutTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	// uni-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "users_id", nullable = false)
	private User user;

	public UsersLog()
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


	public Date getLoginTime()
	{
		return this.loginTime;
	}

	public void setLoginTime(Date loginTime)
	{
		this.loginTime = loginTime;
	}

	public Date getLogoutTime()
	{
		return this.logoutTime;
	}

	public void setLogoutTime(Date logoutTime)
	{
		this.logoutTime = logoutTime;
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

	public User getUser()
	{
		return this.user;
	}

	public void setUser(User user)
	{
		this.user = user;
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
		return "UsersLog [id=" + id + ", created=" + created + ", createdBy="
				+ createdBy + ", loginTime=" + loginTime + ", logoutTime="
				+ logoutTime + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", user=" + user + "]";
	}

}