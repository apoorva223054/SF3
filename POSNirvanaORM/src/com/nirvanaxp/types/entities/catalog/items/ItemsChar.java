/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the items_char database table.
 * 
 */
@Entity
@Table(name = "items_char")
@XmlRootElement(name = "items_char")
public class ItemsChar extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "image_url", length = 256)
	private String imageUrl;

	@Column(name = "is_active", nullable = false)
	private int isActive;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 64)
	private String name;

	@Column(name = "sort_sequence")
	private int sortSequence;

	@Column(name = "hex_code_values")
	private String hexCodeValues;
	
	@Column(name = "global_item_char_id")
	private String globalItemCharId;

	private transient List<Location> locationList;

	private String description;

	public ItemsChar()
	{
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getImageUrl()
	{
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getSortSequence()
	{
		return this.sortSequence;
	}

	public void setSortSequence(int sortSequence)
	{
		this.sortSequence = sortSequence;
	}

	public String getHexCodeValues()
	{
		return hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues)
	{
		this.hexCodeValues = hexCodeValues;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getGlobalItemCharId() {
		 if(globalItemCharId != null && (globalItemCharId.length()==0 || globalItemCharId.equals("0"))){return null;}else{	return globalItemCharId;}
	}

	public void setGlobalItemCharId(String globalItemCharId) {
		this.globalItemCharId = globalItemCharId;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	@Override
	public String toString() {
		return "ItemsChar [displayName=" + displayName + ", imageUrl="
				+ imageUrl + ", isActive=" + isActive + ", locationsId="
				+ locationsId + ", name=" + name + ", sortSequence="
				+ sortSequence + ", hexCodeValues=" + hexCodeValues
				+ ", globalItemCharId=" + globalItemCharId + ", locationList="
				+ locationList + ", description=" + description + "]";
	}

	public ItemsChar getItemChar(ItemsChar itemsChar){
		ItemsChar i = new ItemsChar();
		i.setCreated(itemsChar.getCreated());
		i.setCreatedBy(itemsChar.getCreatedBy());
		i.setDescription(itemsChar.getDescription());
		i.setDisplayName(itemsChar.getDisplayName());
		i.setGlobalItemCharId(itemsChar.getGlobalItemCharId());
		i.setHexCodeValues(itemsChar.getHexCodeValues());
		i.setImageUrl(itemsChar.getImageUrl());
		i.setIsActive(itemsChar.getIsActive());
		i.setLocationList(itemsChar.getLocationList());
		i.setLocationsId(itemsChar.getLocationsId());
		i.setName(itemsChar.getName());
		i.setSortSequence(itemsChar.getSortSequence());
		i.setStatus(itemsChar.getStatus());
		i.setUpdated(itemsChar.getUpdated());
		i.setUpdatedBy(itemsChar.getUpdatedBy());
		return i;
		
	}
	
}