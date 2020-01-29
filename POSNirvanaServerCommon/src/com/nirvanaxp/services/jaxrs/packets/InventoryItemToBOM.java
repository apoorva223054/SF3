/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;
import java.util.List;

import com.nirvanaxp.types.entities.locations.Location;

public class InventoryItemToBOM
{

	private int id;

	private String itemFG;

	private String itemRM;

	private String itemNameRM;

	private String sellableUomRMName;

	private String itemNameRMDisplayName;

	private String sellableUomRM;

	private BigDecimal quantity;

	private String sellableUomRMDisplayName;

	private transient String categoryId;

	private transient String categoryName;

	private transient String categoryDisplayName;
	private transient List<Location> locationList;

	public String getItemFG()
	{
		return itemFG;
	}

	public void setItemFG(String itemFG)
	{
		this.itemFG = itemFG;
	}

	public String getItemRM()
	{
		return itemRM;
	}

	public void setItemRM(String itemRM)
	{
		this.itemRM = itemRM;
	}

	public String getItemNameRM()
	{
		return itemNameRM;
	}

	public void setItemNameRM(String itemNameRM)
	{
		this.itemNameRM = itemNameRM;
	}

	public String getSellableUomRMName()
	{
		return sellableUomRMName;
	}

	public void setSellableUomRMName(String sellableUomRMName)
	{
		this.sellableUomRMName = sellableUomRMName;
	}

	public String getSellableUomRM()
	{
		return sellableUomRM;
	}

	public void setSellableUomRM(String sellableUomRM)
	{
		this.sellableUomRM = sellableUomRM;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getItemNameRMDisplayName()
	{
		return itemNameRMDisplayName;
	}

	public void setItemNameRMDisplayName(String itemNameRMDisplayName)
	{
		this.itemNameRMDisplayName = itemNameRMDisplayName;
	}

	public String getSellableUomRMDisplayName()
	{
		return sellableUomRMDisplayName;
	}

	public void setSellableUomRMDisplayName(String sellableUomRMDisplayName)
	{
		this.sellableUomRMDisplayName = sellableUomRMDisplayName;
	}

	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public String getCategoryDisplayName()
	{
		return categoryDisplayName;
	}

	public void setCategoryDisplayName(String categoryDisplayName)
	{
		this.categoryDisplayName = categoryDisplayName;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}

	@Override
	public String toString() {
		return "InventoryItemToBOM [id=" + id + ", itemFG=" + itemFG
				+ ", itemRM=" + itemRM + ", itemNameRM=" + itemNameRM
				+ ", sellableUomRMName=" + sellableUomRMName
				+ ", itemNameRMDisplayName=" + itemNameRMDisplayName
				+ ", sellableUomRM=" + sellableUomRM + ", quantity=" + quantity
				+ ", sellableUomRMDisplayName=" + sellableUomRMDisplayName
				+ "]";
	}

}
