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
 * The persistent class for the items_to_printers database table.
 * 
 */
@Entity
@Table(name = "items_to_printers")
@XmlRootElement(name = "items_to_printers")
public class ItemsToPrinter extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "items_id")
	private String itemsId;

	@Column(name = "printers_id")
	private String printersId;

	public ItemsToPrinter()
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

	public String getPrintersId()
	{
		 if(printersId != null && (printersId.length()==0 || printersId.equals("0"))){return null;}else{	return printersId;}
	}

	public void setPrintersId(String printersId)
	{
		this.printersId = printersId;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		setItemsId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setPrintersId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "ItemsToPrinter [itemsId=" + itemsId + ", printersId="
				+ printersId + "]";
	}

}