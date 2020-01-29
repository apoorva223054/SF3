/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the category_to_printers database table.
 * 
 */
@Entity
@Table(name = "item_attribute_to_date")
@XmlRootElement(name = "item_attribute_to_date")
public class ItemAttributeToDate extends POSNirvanaBaseClass 
{
	private static final long serialVersionUID = 1L;

 	@Column(name = "item_attribute_id" )
	private String itemAttributeId;

	 
	@Column(name = "date")
	private String date;
	
	 
	@Column(name = "publish")
	private boolean publish;
	
	@Column(name = "location_id")
	private String locationId;
	
	
	private transient String displayName;

	public String getItemAttributeId() {
		 if(itemAttributeId != null && (itemAttributeId.length()==0 || itemAttributeId.equals("0"))){return null;}else{	return itemAttributeId;}
	}

	public void setItemAttributeId(String itemAttributeId) {
		this.itemAttributeId = itemAttributeId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}

	 
 

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "ItemAttributeToDate [itemAttributeId=" + itemAttributeId + ", date=" + date + ", publish=" + publish
				+ ", locationId=" + locationId + "]";
	}

 
	 
	
}