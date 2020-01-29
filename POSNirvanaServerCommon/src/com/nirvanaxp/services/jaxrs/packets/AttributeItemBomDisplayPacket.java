/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.InventoryAttributeBOM;
@XmlRootElement(name = "AttributeItemBomDisplayPacket")
public class AttributeItemBomDisplayPacket
{

	private InventoryAttributeBOM inventoryAttributeBOM;

	private String itemName;

	private String itemDisplayName;

	private String categoryId;

	private String categpryName;

	private String uomDisplayName;

	public InventoryAttributeBOM getInventoryAttributeBOM()
	{
		return inventoryAttributeBOM;
	}

	public void setInventoryAttributeBOM(InventoryAttributeBOM inventoryAttributeBOM)
	{
		this.inventoryAttributeBOM = inventoryAttributeBOM;
	}

	public String getItemName()
	{
		return itemName;
	}

	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

	public String getItemDisplayName()
	{
		return itemDisplayName;
	}

	public void setItemDisplayName(String itemDisplayName)
	{
		this.itemDisplayName = itemDisplayName;
	}

	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public String getCategpryName()
	{
		return categpryName;
	}

	public void setCategpryName(String categpryName)
	{
		this.categpryName = categpryName;
	}

	public String getUomDisplayName()
	{
		return uomDisplayName;
	}

	public void setUomDisplayName(String uomDisplayName)
	{
		this.uomDisplayName = uomDisplayName;
	}

	@Override
	public String toString()
	{
		return "AttributeItemBomDisplayPacket [inventoryAttributeBOM=" + inventoryAttributeBOM + ", itemName=" + itemName + ", itemDisplayName=" + itemDisplayName + ", categoryId=" + categoryId
				+ ", categpryName=" + categpryName + ", uomDisplayName=" + uomDisplayName + "]";
	}

}
