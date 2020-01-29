/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.annotation.Generated;
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
 * The persistent class for the users_to_address database table.
 * 
 */
@Entity
@Table(name = "users_to_messaging")
@XmlRootElement(name = "users_to_messaging")
public class UsersToMessaging implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "staff_messaging_id")
	private int staffMessagingId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "users_id")
	private String usersId;
	
	@Column(name = "status")
	private String status;

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
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

	
	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
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

	public int getStaffMessagingId() {
		return staffMessagingId;
	}

	public void setStaffMessagingId(int staffMessagingId) {
		this.staffMessagingId = staffMessagingId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UsersToMessaging [id=" + id + ", staffMessagingId=" + staffMessagingId + ", created=" + created
				+ ", createdBy=" + createdBy + ", updated=" + updated + ", updatedBy=" + updatedBy + ", usersId="
				+ usersId + ", status=" + status + "]";
	}

}