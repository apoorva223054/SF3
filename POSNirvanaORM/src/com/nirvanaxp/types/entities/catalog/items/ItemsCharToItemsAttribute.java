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
 * The persistent class for the items_char_to_items_attribute database table.
 * 
 */
@Entity
@Table(name = "items_char_to_items_attribute")
@XmlRootElement(name = "items_char_to_items_attribute")
public class ItemsCharToItemsAttribute extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_attribute_id")
	private String itemsAttributeId;

	@Column(name = "items_char_id")
	private String itemsCharId;

	public ItemsCharToItemsAttribute()
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

	public String getItemsCharId()
	{
		 if(itemsCharId != null && (itemsCharId.length()==0 || itemsCharId.equals("0"))){return null;}else{	return itemsCharId;}
	}

	public void setItemsCharId(String itemsCharId)
	{
		this.itemsCharId = itemsCharId;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		setItemsCharId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setItemsAttributeId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "ItemsCharToItemsAttribute [itemsAttributeId="
				+ itemsAttributeId + ", itemsCharId=" + itemsCharId + "]";
	}

}