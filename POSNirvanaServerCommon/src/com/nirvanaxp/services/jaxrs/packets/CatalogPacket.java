/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.printers.Printer;

/**
 * @author
 * 
 *         { "CatalogPacket": { "printerList": [ { "id": 1 }, { "id": 3 } ],
 *         "category": { "id": 4, "updated": 1399454658000, "updatedBy": 1778,
 *         "created": 1292101920000, "createdBy": 1778, "status": "I",
 *         "description": "Lunch Buffet ordering", "iconColour": "#5c5147",
 *         "isActive": 0, "isDeleted": 0, "name": "Buffet", "displayName":
 *         "Buffet", "imageName": "", "sortSequence": 1, "locationsId": 1,
 *         "categoryId": 0, "categoryToPrinters": null, "categoryToDiscounts":
 *         null } } }
 *
 */
@XmlRootElement(name = "CatalogPacket")
public class CatalogPacket extends PostPacket
{
	Category category;

	private List<Printer> printerList;

	private List<Discount> discountsList;
	
	private int isBaseLocationUpdate;
	private String locationsListId;
	
	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public List<Printer> getPrinterList()
	{
		return printerList;
	}

	public void setPrinterList(List<Printer> printerList)
	{
		this.printerList = printerList;
	}

	public List<Discount> getDiscountsList()
	{
		return discountsList;
	}

	public void setDiscountsList(List<Discount> discountsList)
	{
		this.discountsList = discountsList;
	}

	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	 
}
