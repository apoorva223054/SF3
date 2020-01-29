/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

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
 * The persistent class for the locations_to_functions database table.
 * 
 */
@Entity
@Table(name = "locations_to_functions")
@XmlRootElement(name = "locations_to_functions")
public class LocationsToFunction implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "functions_id", nullable = false)
	private String functionsId;

	@Column(name = "functions_name")
	private String functionsName;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "display_sequence")
	private int displaySequence;

	private transient String name;

	public LocationsToFunction()
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

	public String getFunctionsId()
	{
		 if(functionsId != null && (functionsId.length()==0 || functionsId.equals("0"))){return null;}else{	return functionsId;}
	}

	public void setFunctionsId(String functionsId)
	{
		this.functionsId = functionsId;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
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

	public String getFunctionsName()
	{
		return functionsName;
	}

	public void setFunctionsName(String functionsName)
	{
		this.functionsName = functionsName;
	}

	/**
	 * @return the displaySequence
	 */
	public int getDisplaySequence()
	{
		return displaySequence;
	}

	/**
	 * @param displaySequence
	 *            the displaySequence to set
	 */
	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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
		return "LocationsToFunction [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", functionsId=" + functionsId
				+ ", functionsName=" + functionsName + ", locationsId="
				+ locationsId + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", status=" + status + ", displaySequence="
				+ displaySequence + ", name=" + name + "]";
	}

}