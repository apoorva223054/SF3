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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The persistent class for the feedback_question database table.
 * 
 */
@Entity
@Table(name = "feedback_question")
@XmlRootElement(name = "feedback_question")
public class FeedbackQuestion implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_sequence", nullable = false)
	private int displaySequence;

	@Lob
	@Column(name = "feedback_question", nullable = false)
	private String feedbackQuestion;

	@Column(name = "feedback_type_id", nullable = false)
	private int feedbackTypeId;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 1)
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated")
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	public FeedbackQuestion()
	{
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



	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getFeedbackQuestion()
	{
		return this.feedbackQuestion;
	}

	public void setFeedbackQuestion(String feedbackQuestion)
	{
		this.feedbackQuestion = feedbackQuestion;
	}

	 

	public int getFeedbackTypeId() {
		return feedbackTypeId;
	}



	public void setFeedbackTypeId(int feedbackTypeId) {
		this.feedbackTypeId = feedbackTypeId;
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
		return "FeedbackQuestion [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", displaySequence="
				+ displaySequence + ", feedbackQuestion=" + feedbackQuestion
				+ ", feedbackTypeId=" + feedbackTypeId + ", locationsId="
				+ locationsId + ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + "]";
	}

}