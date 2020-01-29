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

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the items_to_items_attribute database table.
 * 
 */
@Entity
@Table(name = "items_to_items_attribute")
@XmlRootElement(name = "items_to_items_attributes")
public class ItemsToItemsAttribute extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_attribute_id")
	private String itemsAttributeId;

	@Column(name = "items_id")
	private String itemsId;

	public ItemsToItemsAttribute()
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
		setItemsAttributeId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "ItemsToItemsAttribute [itemsAttributeId=" + itemsAttributeId
				+ ", itemsId=" + itemsId + "]";
	}

}