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
 * The persistent class for the items_to_discounts database table.
 * 
 */
@Entity
@Table(name = "items_to_discounts")
@XmlRootElement(name = "items_to_discounts")
public class ItemsToDiscount extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "discounts_id")
	private String discountsId;

	@Column(name = "items_id")
	private String itemsId;

	public ItemsToDiscount()
	{
	}
	
	public ItemsToDiscount(String id)
	{
		this.itemsId = id;
	}

	public String getDiscountsId()
	{
		 if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
	}

	public void setDiscountsId(String discountsId)
	{
		this.discountsId = discountsId;
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
		setDiscountsId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "ItemsToDiscount [discountsId=" + discountsId + ", itemsId="
				+ itemsId + "]";
	}

}