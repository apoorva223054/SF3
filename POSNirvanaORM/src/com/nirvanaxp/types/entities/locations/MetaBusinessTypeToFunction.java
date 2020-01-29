/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the meta_business_type_to_functions database table.
 * 
 */
@Entity
@Table(name = "meta_business_type_to_functions")
@NamedQuery(name = "MetaBusinessTypeToFunction.findAll", query = "SELECT m FROM MetaBusinessTypeToFunction m")
public class MetaBusinessTypeToFunction implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	@Column(name = "business_type_id")
	private int businessTypeId;

	private Timestamp created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "functions_id")
	private String functionsId;

	@Column(name = "functions_name")
	private String functionsName;

	private String status;

	private Timestamp updated;

	@Column(name = "updated_by")
	private String updatedBy;

	public MetaBusinessTypeToFunction()
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

	public int getBusinessTypeId()
	{
		return this.businessTypeId;
	}

	public void setBusinessTypeId(int businessTypeId)
	{
		this.businessTypeId = businessTypeId;
	}

	public Timestamp getCreated()
	{
		return this.created;
	}

	public void setCreated(Timestamp created)
	{
		this.created = created;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getFunctionsId()
	{
		 if(functionsId != null && (functionsId.length()==0 || functionsId.equals("0"))){return null;}else{	return functionsId;}
	}

	public void setFunctionsId(String functionsId)
	{
		this.functionsId = functionsId;
	}

	public String getFunctionsName()
	{
		return this.functionsName;
	}

	public void setFunctionsName(String functionsName)
	{
		this.functionsName = functionsName;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Timestamp getUpdated()
	{
		return this.updated;
	}

	public void setUpdated(Timestamp updated)
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
		return "MetaBusinessTypeToFunction [id=" + id + ", businessTypeId="
				+ businessTypeId + ", created=" + created + ", createdBy="
				+ createdBy + ", displayName=" + displayName + ", functionsId="
				+ functionsId + ", functionsName=" + functionsName
				+ ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + "]";
	}

}