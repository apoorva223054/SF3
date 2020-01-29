/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the users_to_feeback_details database table.
 * 
 */
@Entity
@Table(name = "users_to_feeback_details")
@XmlRootElement(name = "users_to_feeback_details")
public class UsersToFeebackDetail implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "created")
	private Timestamp created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "details_value", nullable = false, length = 50)
	private String detailsValue;

	@Column(name = "feedback_details_id", nullable = false)
	private int feedbackDetailsId;

	@Column(name = "updated")
	private Timestamp updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	@Column(name = "order_header_id", nullable = false)
	private String orderHeaderId;
	
	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public UsersToFeebackDetail()
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
			this.created = new Timestamp(created);
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

	public String getDetailsValue()
	{
		return this.detailsValue;
	}

	public void setDetailsValue(String detailsValue)
	{
		this.detailsValue = detailsValue;
	}

	public int getFeedbackDetailsId()
	{
		return this.feedbackDetailsId;
	}

	public void setFeedbackDetailsId(int feedbackDetailsId)
	{
		this.feedbackDetailsId = feedbackDetailsId;
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
			this.updated = new Timestamp(updated);
		}
	}


	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	@Override
	public String toString() {
		return "UsersToFeebackDetail [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", detailsValue=" + detailsValue
				+ ", feedbackDetailsId=" + feedbackDetailsId + ", updated="
				+ updated + ", updatedBy=" + updatedBy + ", usersId=" + usersId
				+ ", orderHeaderId=" + orderHeaderId +  ", localTime=" + localTime +"]";
	}

}