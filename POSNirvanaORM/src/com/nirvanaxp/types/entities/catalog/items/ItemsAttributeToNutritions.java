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
import com.nirvanaxp.types.entities.protocol.RelationalEntities;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the items_char_to_items_attribute database table.
 * 
 */
@Entity
@Table(name = "items_attribute_to_nutritions")
@XmlRootElement(name = "items_attribute_to_nutritions")
public class ItemsAttributeToNutritions extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_attribute_id")
	private String itemsAttributeId;

	@Column(name = "nutritions_id", nullable = false)
	private String nutritionsId;
	
	@Column(name = "nutritions_value")
	private String nutritionsValue;

	public ItemsAttributeToNutritions()
	{
	}

	 
	public String getItemsAttributeId() {
		 if(itemsAttributeId != null && (itemsAttributeId.length()==0 || itemsAttributeId.equals("0"))){return null;}else{	return itemsAttributeId;}
	}




	public void setItemsAttributeId(String itemsAttributeId) {
		this.itemsAttributeId = itemsAttributeId;
	}




	public String getNutritionsId() {
		 if(nutritionsId != null && (nutritionsId.length()==0 || nutritionsId.equals("0"))){return null;}else{	return nutritionsId;}
	}

	public void setNutritionsId(String nutritionsId) {
		this.nutritionsId = nutritionsId;
	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setItemsAttributeId(baseToObjectId);

	}


 
	@Override
	public String toString() {
		return "ItemsAttributeToNutritions [itemsAttributeId=" + itemsAttributeId + ", nutritionsId=" + nutritionsId
				+ ", nutritionsValue=" + nutritionsValue + "]";
	}

	public String getNutritionsValue() {
		 if(nutritionsValue != null && (nutritionsValue.length()==0 || nutritionsValue.equals("0"))){return null;}else{	return nutritionsValue;}
	}

	public void setNutritionsValue(String nutritionsValue) {
		this.nutritionsValue = nutritionsValue;
	}

	@Override
	public void setBaseRelation(String baseId) {
		setItemsAttributeId(baseId);
		
	}
	
	
	 
}