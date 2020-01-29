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
@Table(name = "nutritions")
@XmlRootElement(name = "nutritions")
public class Nutritions extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 64)
	private String name;

	@Column(name = "sort_sequence")
	private int sortSequence;

	@Column(name = "hex_code_values")
	private String hexCodeValues;
	
	@Column(name = "global_id")
	private String globalId;

	private transient List<Location> locationList;
	private transient String nutritionsValue;

	private String description;

	public Nutritions()
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



	public List<Location> getLocationList() {
		return locationList;
	}

	public String getGlobalId() {
		 if(globalId != null && (globalId.length()==0 || globalId.equals("0"))){return null;}else{	return globalId;}
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

 

	@Override
	public String toString() {
		return "Nutritions [  locationsId=" + locationsId + ", name=" + name
				+ ", sortSequence=" + sortSequence + ", hexCodeValues=" + hexCodeValues + ", globalId=" + globalId
				+ ", description=" + description + "]";
	}

	

	public String getNutritionsValue() {
		 if(nutritionsValue != null && (nutritionsValue.length()==0 || nutritionsValue.equals("0"))){return null;}else{	return nutritionsValue;}
	}



	public void setNutritionsValue(String nutritionsValue) {
		this.nutritionsValue = nutritionsValue;
	}



	public Nutritions getNutritions(Nutritions nutrition){
		Nutritions i = new Nutritions();
		i.setCreated(nutrition.getCreated());
		i.setCreatedBy(nutrition.getCreatedBy());
		i.setDescription(nutrition.getDescription());
		i.setGlobalId(nutrition.getGlobalId());
		i.setHexCodeValues(nutrition.getHexCodeValues());
		i.setLocationList(nutrition.getLocationList());
		i.setLocationsId(nutrition.getLocationsId());
		i.setName(nutrition.getName());
		i.setSortSequence(nutrition.getSortSequence());
		i.setStatus(nutrition.getStatus());
		i.setUpdated(nutrition.getUpdated());
		i.setUpdatedBy(nutrition.getUpdatedBy());
		i.setNutritionsValue(nutrition.getNutritionsValue());
		return i;
		
	}
	
}