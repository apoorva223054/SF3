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
 * The persistent class for the items_attribute_type_to_items_attribute database
 * table.
 * 
 */
@Entity
@Table(name = "items_attribute_type_to_items_attribute")
@XmlRootElement(name = "items_attribute_type_to_items_attribute")
public class ItemsAttributeTypeToItemsAttribute extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_attribute_id")
	private String itemsAttributeId;

	@Column(name = "items_attribute_type_id", nullable = false)
	private String itemsAttributeTypeId;

	public ItemsAttributeTypeToItemsAttribute()
	{
	}

	public String getItemsAttributeId()
	{
		 if(itemsAttributeId != null && (itemsAttributeId.length()==0 || itemsAttributeId.equals("0"))){return null;}else{	return itemsAttributeId;}
	}

	public void setItemsAttributeId(String itemsAttributeId)
	{
		this.itemsAttributeId = itemsAttributeId;
	}

	public String getItemsAttributeTypeId()
	{
		 if(itemsAttributeTypeId != null && (itemsAttributeTypeId.length()==0 || itemsAttributeTypeId.equals("0"))){return null;}else{	return itemsAttributeTypeId;}
	}

	public void setItemsAttributeTypeId(String itemsAttributeTypeId)
	{
		this.itemsAttributeTypeId = itemsAttributeTypeId;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		this.setItemsAttributeTypeId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		this.setItemsAttributeId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "ItemsAttributeTypeToItemsAttribute [itemsAttributeId="
				+ itemsAttributeId + ", itemsAttributeTypeId="
				+ itemsAttributeTypeId + "]";
	}

}