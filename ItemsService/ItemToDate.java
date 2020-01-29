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
@Table(name = "item_to_date")
@XmlRootElement(name = "item_to_date")
public class ItemToDate extends POSNirvanaBaseClass 
{
	private static final long serialVersionUID = 1L;

	@Column(name = "category_id" )
	private int categoryId;
	
	@Column(name = "item_id" )
	private int itemId;

	 
	@Column(name = "date")
	private String date;
	
	 
	@Column(name = "publish")
	private boolean publish;
	
	@Column(name = "location_id")
	private String locationId;

	public int getCategoryId() {
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getItemId() {
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
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

	public int getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "ItemToDate [categoryId=" + categoryId + ", itemId=" + itemId + ", date=" + date + ", publish=" + publish
				+ ", locationId=" + locationId + "]";
	}

 
	 
	
}