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
 * The persistent class for the customer_experience_to_user_detail database table.
 * 
 */
@Entity
@Table(name="customer_experience_to_user_detail")
@NamedQuery(name="CustomerExperienceToUserDetail.findAll", query="SELECT c FROM CustomerExperienceToUserDetail c")
public class CustomerExperienceToUserDetail implements Serializable {
	@Override
	public String toString() {
		return "CustomerExperienceToUserDetail [id=" + id + ", createdBy="
				+ createdBy + ", customerExperienceId=" + customerExperienceId
				+ ", detailsValue=" + detailsValue + ", feedbackDetailsId="
				+ feedbackDetailsId + ", created=" + created + ", updated="
				+ updated + ", updatedBy=" + updatedBy + "]";
	}

	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	

	@Column(name="created_by")
	private String createdBy;

	@Column(name="customer_experience_id")
	private int customerExperienceId;

	@Column(name="details_value")
	private String detailsValue;

	@Column(name="feedback_details_id")
	private String feedbackDetailsId;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;


	@Column(name="updated_by")
	private String updatedBy;

	public CustomerExperienceToUserDetail() {
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

	public String getDetailsValue() {
		return this.detailsValue;
	}

	public void setDetailsValue(String detailsValue) {
		this.detailsValue = detailsValue;
	}

	public String getFeedbackDetailsId() {
		return this.feedbackDetailsId;
	}

	public void setFeedbackDetailsId(String feedbackDetailsId) {
		this.feedbackDetailsId = feedbackDetailsId;
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
	
}