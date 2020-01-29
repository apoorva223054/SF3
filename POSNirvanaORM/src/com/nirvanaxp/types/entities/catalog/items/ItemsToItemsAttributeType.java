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
 * The persistent class for the items_to_items_attribute_type database table.
 * 
 */
@Entity
@Table(name = "items_to_items_attribute_type")
@XmlRootElement(name = "items_to_items_attribute_type")
public class ItemsToItemsAttributeType extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_attribute_type_id", nullable = false)
	private String itemsAttributeTypeId;

	@Column(name = "items_id", nullable = false)
	private String itemsId;

	public ItemsToItemsAttributeType()
	{
	}

	public String getItemsAttributeTypeId()
	{
		 if(itemsAttributeTypeId != null && (itemsAttributeTypeId.length()==0 || itemsAttributeTypeId.equals("0"))){return null;}else{	return itemsAttributeTypeId;}
	}

	public void setItemsAttributeTypeId(String itemsAttributeTypeId)
	{
		this.itemsAttributeTypeId = itemsAttributeTypeId;
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
		setItemsAttributeTypeId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "ItemsToItemsAttributeType [itemsAttributeTypeId="
				+ itemsAttributeTypeId + ", itemsId=" + itemsId + "]";
	}

}