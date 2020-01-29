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
@Table(name = "locations_to_images")
@XmlRootElement(name = "locations_to_images")
public class LocationsToImages  extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	
	@Column(name = "images", nullable = false)
	private String images;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "display_sequence")
	private int displaySequence;


	public LocationsToImages()
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

	
	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	
	@Override
	public String toString() {
		return "LocationsToImages [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", images=" + images
				+ ", locationsId="
				+ locationsId + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", status=" + status + ", displaySequence="
				+ displaySequence + "]";
	}

}