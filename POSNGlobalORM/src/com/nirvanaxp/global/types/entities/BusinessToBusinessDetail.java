package com.nirvanaxp.global.types.entities;

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
 * The persistent class for the business_to_business_details database table.
 * 
 */
@Entity
@Table(name="business_to_business_details")
@XmlRootElement(name = "business_to_business_details")
public class BusinessToBusinessDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name="business_details_id")
	private int businessDetailsId;

	@Column(name="business_id")
	private int businessId;

	private String comments;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="display_sequence")
	private int displaySequence;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name="updated_by")
	private String updatedBy;

	public BusinessToBusinessDetail() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBusinessDetailsId() {
		return this.businessDetailsId;
	}

	public void setBusinessDetailsId(int businessDetailsId) {
		this.businessDetailsId = businessDetailsId;
	}

	public int getBusinessId() {
		return this.businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}



	public int getDisplaySequence() {
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
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