package com.nirvanaxp.types.entities.feedback;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the customer_experience_to_user_feedback database table.
 * 
 */
@Entity
@Table(name="customer_experience_to_user_feedback")
@NamedQuery(name="CustomerExperienceToUserFeedback.findAll", query="SELECT c FROM CustomerExperienceToUserFeedback c")
public class CustomerExperienceToUserFeedback implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;
	
	@Column(name="customer_experience_id")
	private int customerExperienceId;

	@Column(name="feedback_question_id")
	private int feedbackQuestionId;

	@Column(name="smiley_id")
	private int smileyId;

	@Column(name="created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;

	@Column(name="updated_by")
	private String updatedBy;

	public CustomerExperienceToUserFeedback() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getCustomerExperienceId() {
		return this.customerExperienceId;
	}

	public void setCustomerExperienceId(int customerExperienceId) {
		this.customerExperienceId = customerExperienceId;
	}

	public int getFeedbackQuestionId() {
		return this.feedbackQuestionId;
	}

	public void setFeedbackQuestionId(int feedbackQuestionId) {
		this.feedbackQuestionId = feedbackQuestionId;
	}

	public int getSmileyId() {
		return this.smileyId;
	}

	public void setSmileyId(int smileyId) {
		this.smileyId = smileyId;
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

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public Date getCreated()
	{
		return created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	@Override
	public String toString() {
		return "CustomerExperienceToUserFeedback [id=" + id
				+ ", customerExperienceId=" + customerExperienceId
				+ ", feedbackQuestionId=" + feedbackQuestionId + ", smileyId="
				+ smileyId + ", createdBy=" + createdBy + ", created="
				+ created + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ "]";
	}
}