/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the items_to_items_char database table.
 * 
 */
@Entity
@Table(name = "items_to_nutritions")
@XmlRootElement(name = "items_to_nutritions")
public class ItemsToNutritions extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "nutritions_id", nullable = false)
	private String nutritionsId;
	
	@Column(name = "nutritions_value")
	private String nutritionsValue;

	@Column(name = "items_id", nullable = false)
	private String itemsId;

	public ItemsToNutritions()
	{
	}

	 
	public String getItemsId()
	{
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}

	public void setItemsId(String itemsId)
	{
		this.itemsId = itemsId;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		setItemsId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setNutritionsId(baseToObjectId);

	}

	 
	public String getNutritionsId() {
		 if(nutritionsId != null && (nutritionsId.length()==0 || nutritionsId.equals("0"))){return null;}else{	return nutritionsId;}
	}


	public void setNutritionsId(String nutritionsId) {
		this.nutritionsId = nutritionsId;
	}


	public String getNutritionsValue() {
		 if(nutritionsValue != null && (nutritionsValue.length()==0 || nutritionsValue.equals("0"))){return null;}else{	return nutritionsValue;}
	}


	public void setNutritionsValue(String nutritionsValue) {
		this.nutritionsValue = nutritionsValue;
	}


	@Override
	public String toString() {
		return "ItemsToNutritions [nutritionsId=" + nutritionsId + ", nutritionsValue=" + nutritionsValue + ", itemsId="
				+ itemsId + "]";
	}

 
 

}