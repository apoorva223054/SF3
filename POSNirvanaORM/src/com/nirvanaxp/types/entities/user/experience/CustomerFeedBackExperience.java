/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user.experience;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.feedback.CustomerExperienceToUserDetail;
import com.nirvanaxp.types.entities.feedback.CustomerExperienceToUserFeedback;

/**
 * The persistent class for the customer_experience database table.
 * 
 */
@Entity
@Table(name = "customer_experience")
@XmlRootElement(name = "customer_feedback_experience")
public class CustomerFeedBackExperience implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(nullable = false, length = 1200)
	private String comments;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "order_header_id", nullable = false)
	private String orderHeaderId;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	@Column(name = "manager_response", nullable = false)
	private int managerResponse;
	
	private transient List<CustomerExperienceToUserDetail> customerExperienceToUserDetailsList;

	 private transient List<CustomerExperienceToUserFeedback> customerExperienceToUserFeedbackList;
	
	public List<CustomerExperienceToUserDetail> getCustomerExperienceToUserDetailsList() {
		return customerExperienceToUserDetailsList;
	}

	public void setCustomerExperienceToUserDetailsList(
			List<CustomerExperienceToUserDetail> customerExperienceToUserDetailsList) {
		this.customerExperienceToUserDetailsList = customerExperienceToUserDetailsList;
	}

	

	public List<CustomerExperienceToUserFeedback> getCustomerExperienceToUserFeedbackList() {
		return customerExperienceToUserFeedbackList;
	}

	public void setCustomerExperienceToUserFeedbackList(
			List<CustomerExperienceToUserFeedback> customerExperienceToUserFeedbackList) {
		this.customerExperienceToUserFeedbackList = customerExperienceToUserFeedbackList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}



	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public int getManagerResponse() {
		return managerResponse;
	}

	public void setManagerResponse(int managerResponse) {
		this.managerResponse = managerResponse;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
		return "CustomerFeedBackExperience [id=" + id + ", comments="
				+ comments + ", created=" + created + ", createdBy="
				+ createdBy + ", updated=" + updated + ", usersId=" + usersId
				+ ", locationsId=" + locationsId + ", orderHeaderId="
				+ orderHeaderId + ", updatedBy=" + updatedBy
				+ ", managerResponse=" + managerResponse
				+ ", customerExperienceToUserDetailsList="
				+ customerExperienceToUserDetailsList
				+ ", customerExperienceToUserFeedbackList="
				+ customerExperienceToUserFeedbackList + "]";
	}

	
	
	 
	 
	

	

	

}