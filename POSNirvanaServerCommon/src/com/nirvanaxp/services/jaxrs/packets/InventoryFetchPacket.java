/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "InventoryFetchPacket")
public class InventoryFetchPacket
{
	
	private String locationId;
	private int isAdmin;
	private int start;
	private int end;
	private String categoryId;
	private String categoryList;
	private String supplierId;
	private String itemName;
	private String date;
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getCategoryId() {
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSupplierId() {
		 if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	return supplierId;}
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	

	public int getIsAdmin()
	{
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin)
	{
		this.isAdmin = isAdmin;
	}

	
	public String getCategoryList()
	{
		return categoryList;
	}

	public void setCategoryList(String categoryList)
	{
		this.categoryList = categoryList;
	}

	@Override
	public String toString()
	{
		return "InventoryFetchPacket [locationId=" + locationId + ", isAdmin=" + isAdmin + ", start=" + start + ", end=" + end + ", categoryId=" + categoryId + ", categoryList=" + categoryList
				+ ", supplierId=" + supplierId + ", itemName=" + itemName + ", date=" + date + "]";
	}
 
	 
}
