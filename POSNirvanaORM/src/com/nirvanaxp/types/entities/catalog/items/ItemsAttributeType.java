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
 * The persistent class for the items_attribute_type database table.
 * 
 */
@Entity
@Table(name = "items_attribute_type")
@XmlRootElement(name = "items_attribute_type")
public class ItemsAttributeType extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "is_active", nullable = false)
	private int isActive;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "is_required", nullable = false)
	private int isRequired;

	@Column(nullable = false, length = 64)
	private String name;

	@Column(name = "sort_sequence", nullable = false)
	private int sortSequence;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "hex_code_values")
	private String hexCodeValues;
	
	@Column(name = "is_online_attribute_Type")
	private int isOnlineAttributeType;

	@Column(name = "max_attribute_allowed")
	private int maxAttributeAllowed;
	
	@Column(name = "global_items_attribute_type_id")
	private String globalItemAttributeTypeId;
	
	public int getIsOnlineAttributeType() {
		return isOnlineAttributeType;
	}

	public void setIsOnlineAttributeType(int isOnlineAttributeType) {
		this.isOnlineAttributeType = isOnlineAttributeType;
	}

	private String description;

	public transient List<ItemsAttribute> itemsAttributes;
	private transient List<Location> locationList;
	
	public ItemsAttributeType()
	{
	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public int getIsRequired()
	{
		return this.isRequired;
	}

	public void setIsRequired(int isRequired)
	{
		this.isRequired = isRequired;
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

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
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

	public List<ItemsAttribute> getItemsAttributes()
	{
		return itemsAttributes;
	}

	public void setItemsAttributes(List<ItemsAttribute> itemsAttributes)
	{
		this.itemsAttributes = itemsAttributes;
	}

	public int getMaxAttributeAllowed() {
		return maxAttributeAllowed;
	}

	public void setMaxAttributeAllowed(int maxAttributeAllowed) {
		this.maxAttributeAllowed = maxAttributeAllowed;
	}

	public String getGlobalItemAttributeTypeId() {
		 if(globalItemAttributeTypeId != null && (globalItemAttributeTypeId.length()==0 || globalItemAttributeTypeId.equals("0"))){return null;}else{	return globalItemAttributeTypeId;}
	}

	public void setGlobalItemAttributeTypeId(String globalItemAttributeTypeId) {
		this.globalItemAttributeTypeId = globalItemAttributeTypeId;
	}

	@Override
	public String toString() {
		return "ItemsAttributeType [isActive=" + isActive + ", displayName="
				+ displayName + ", locationsId=" + locationsId
				+ ", isRequired=" + isRequired + ", name=" + name
				+ ", sortSequence=" + sortSequence + ", imageName=" + imageName
				+ ", hexCodeValues=" + hexCodeValues
				+ ", isOnlineAttributeType=" + isOnlineAttributeType
				+ ", maxAttributeAllowed=" + maxAttributeAllowed
				+ ", globalItemAttributeTypeId=" + globalItemAttributeTypeId
				+ ", description=" + description + "]";
	}

	public ItemsAttributeType getItemsAttributeTypeObject(
			ItemsAttributeType itemsAttributeType) {
		ItemsAttributeType attributeType = new ItemsAttributeType();
		attributeType.setCreated(itemsAttributeType.getCreated());
		attributeType.setCreatedBy(itemsAttributeType.getCreatedBy());
		attributeType.setDescription(itemsAttributeType.getDescription());
		attributeType.setDisplayName(itemsAttributeType.getDisplayName());
		attributeType.setGlobalItemAttributeTypeId(itemsAttributeType.getGlobalItemAttributeTypeId());
		attributeType.setHexCodeValues(itemsAttributeType.getHexCodeValues());
		attributeType.setImageName(itemsAttributeType.getImageName());
		attributeType.setIsActive(itemsAttributeType.getIsActive());
		attributeType.setIsOnlineAttributeType(itemsAttributeType.getIsOnlineAttributeType());
		attributeType.setIsRequired(itemsAttributeType.getIsRequired());
		attributeType.setItemsAttributes(itemsAttributeType.getItemsAttributes());
		attributeType.setLocationsId(itemsAttributeType.getLocationsId());
		attributeType.setMaxAttributeAllowed(itemsAttributeType.getMaxAttributeAllowed());
		attributeType.setName(itemsAttributeType.getName());
		attributeType.setSortSequence(itemsAttributeType.getSortSequence());
		attributeType.setStatus(itemsAttributeType.getStatus());
		attributeType.setUpdated(itemsAttributeType.getUpdated());
		attributeType.setUpdatedBy(itemsAttributeType.getUpdatedBy());
		 
		return attributeType;
	
	}

	public List<Location> getLocationList()
	{
		return locationList;
	}

	public void setLocationList(List<Location> locationList)
	{
		this.locationList = locationList;
	}

	

}