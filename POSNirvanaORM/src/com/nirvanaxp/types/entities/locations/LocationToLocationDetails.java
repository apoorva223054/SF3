/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

/**
 * The persistent class for the locations_to_functions database table.
 * 
 */
@Entity
@Table(name = "location_to_location_details")
@XmlRootElement(name = "location_to_location_details")
public class LocationToLocationDetails extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "location_details_id", nullable = false)
	private String locationDetailsId;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;
	
	@Column(name = "global_business_to_business_details_id", nullable = false)
	private int globalBusinessToBusinessDetailsId;
	
	@Column(name = "comments", nullable = false)
	private String comments;
	
	@Column(name = "display_sequence")
	private int displaySequence;

	private transient int businessId;

	public LocationToLocationDetails()
	{
	}

	
	

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
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


 

	public String getLocationDetailsId() {
		 if(locationDetailsId != null && (locationDetailsId.length()==0 || locationDetailsId.equals("0"))){return null;}else{	return locationDetailsId;}
	}




	public void setLocationDetailsId(String locationDetailsId) {
		this.locationDetailsId = locationDetailsId;
	}




	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}
	
	

	public int getGlobalBusinessToBusinessDetailsId() {
		return globalBusinessToBusinessDetailsId;
	}

	public void setGlobalBusinessToBusinessDetailsId(
			int globalBusinessToBusinessDetailsId) {
		this.globalBusinessToBusinessDetailsId = globalBusinessToBusinessDetailsId;
	}

	@Override
	public String toString() {
		return "LocationsToImages [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", locationDetailsId=" + locationDetailsId 
				+", globalBusinessToBusinessDetailsId=" + globalBusinessToBusinessDetailsId
				+ ", comments=" + comments + ", businessId=" + businessId + ", locationsId="
				+ locationsId + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", status=" + status + ", displaySequence="
				+ displaySequence + "]";
	}

}