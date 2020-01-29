/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.feedback;

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
 * The persistent class for the feedback_type database table.
 * 
 */
@Entity
@Table(name = "feedback_type")
@XmlRootElement(name = "feedback_type")
public class FeedbackType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "feedback_type_name", nullable = false, length = 50)
	private String feedbackTypeName;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "updated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	@Column(name = "average_feedback_notification")
	private Integer averageFeedbackNotification;

	public FeedbackType()
	{
	}

	 

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getFeedbackTypeName()
	{
		return this.feedbackTypeName;
	}

	public void setFeedbackTypeName(String feedbackTypeName)
	{
		this.feedbackTypeName = feedbackTypeName;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getStatus()
	{
		return this.status;
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

	public Integer getAverageFeedbackNotification() {
		return averageFeedbackNotification;
	}

	public void setAverageFeedbackNotification(Integer averageFeedbackNotification) {
		this.averageFeedbackNotification = averageFeedbackNotification;
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
		return "FeedbackType [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", feedbackTypeName="
				+ feedbackTypeName + ", locationsId=" + locationsId
				+ ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", averageFeedbackNotification="
				+ averageFeedbackNotification + "]";
	}

	

	
}