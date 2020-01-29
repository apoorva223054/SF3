/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.application;

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
 * The persistent class for the application_to_functions database table.
 * 
 */
@Entity
@Table(name = "application_to_functions")
@XmlRootElement(name = "application_to_functions")
public class ApplicationToFunction implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "applications_id")
	private Integer applicationsId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "functions_id")
	private String functionsId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(nullable = false, length = 1)
	private String status;

	public ApplicationToFunction()
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

	public int getApplicationsId()
	{
		return this.applicationsId;
	}

	public void setApplicationsId(int applicationsId)
	{
		this.applicationsId = applicationsId;
	}

	public String getFunctionsId() {
		 if(functionsId != null && (functionsId.length()==0 || functionsId.equals("0"))){return null;}else{	return functionsId;}
	}

	public void setFunctionsId(String functionsId) {
		this.functionsId = functionsId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setApplicationsId(Integer applicationsId)
	{
		this.applicationsId = applicationsId;
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

	@Override
	public String toString() {
		return "ApplicationToFunction [id=" + id + ", applicationsId="
				+ applicationsId + ", created=" + created + ", createdBy="
				+ createdBy + ", functonsId=" + functionsId + ", updated="
				+ updated + ", updatedBy=" + updatedBy + ", status=" + status
				+ "]";
	}

	
}