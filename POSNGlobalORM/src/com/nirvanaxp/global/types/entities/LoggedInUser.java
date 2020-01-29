/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
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
 * The persistent class for the logged_in_users database table.
 * 
 */
@Entity
@Table(name="logged_in_users")
@XmlRootElement(name = "logged_in_users")
public class LoggedInUser implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name="login_time")
	private Timestamp loginTime;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name="updated_by")
	private String updatedBy;

	private String username;

	@Column(name="users_id")
	private String usersId;

	public LoggedInUser()
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

	public Timestamp getLoginTime()
	{
		return this.loginTime;
	}

	public void setLoginTime(Timestamp loginTime)
	{
		this.loginTime = loginTime;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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

	public String getUpdatedBy()
	{
		return this.updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}


	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	@Override
	public String toString() {
		return "LoggedInUser [id=" + id + ", loginTime=" + loginTime
				+ ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", username=" + username
				+ ", usersId=" + usersId + "]";
	}

}