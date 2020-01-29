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
 * The persistent class for the items_to_items_char database table.
 * 
 */
@Entity
@Table(name = "items_to_items_char")
@XmlRootElement(name = "items_to_items_char")
public class ItemsToItemsChar extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_char_id")
	private String itemsCharId;

	@Column(name = "items_id", nullable = false)
	private String itemsId;

	public ItemsToItemsChar()
	{
	}

	public String getItemsCharId()
	{
		 if(itemsCharId != null && (itemsCharId.length()==0 || itemsCharId.equals("0"))){return null;}else{	return itemsCharId;}
	}

	public void setItemsCharId(String itemsCharId)
	{
		this.itemsCharId = itemsCharId;
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
	public String toString() {
		return "ItemsToItemsChar [itemsCharId=" + itemsCharId + ", itemsId="
				+ itemsId + "]";
	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId) {
		setItemsCharId(baseToObjectId);
		
	}
	
}