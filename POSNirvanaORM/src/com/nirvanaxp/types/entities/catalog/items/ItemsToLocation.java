/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.protocol.RelationalEntities;

/**
 * The persistent class for the items_to_location database table.
 * 
 */
@Entity
@Table(name = "items_to_location")
@XmlRootElement(name = "items_to_location")
public class ItemsToLocation extends POSNirvanaBaseClass implements RelationalEntities
{
	private static final long serialVersionUID = 1L;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "items_id")
	private String itemsId;
	
	@Column(name = "price")
	private BigDecimal price;
	
	@Column(name = "items_status")
	private String itemsStatus;
	

	public ItemsToLocation()
	{
	}


	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}


	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}


	public String getItemsId()
	{
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}


	public void setItemsId(String itemsId)
	{
		this.itemsId = itemsId;
	}


	public BigDecimal getPrice()
	{
		return price;
	}


	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}


	public String getItemsStatus()
	{
		return itemsStatus;
	}


	public void setItemsStatus(String itemsStatus)
	{
		this.itemsStatus = itemsStatus;
	}


	@Override
	public String toString()
	{
		return "ItemsToLocation [locationId=" + locationId + ", itemsId=" + itemsId 
				+ ", price=" + price + ", itemsStatus=" + itemsStatus + "]";
	}


	@Override
	public void setBaseRelation(int baseId)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setBaseToObjectRelation(int baseToObjectId)
	{
		// TODO Auto-generated method stub
		
	}

}