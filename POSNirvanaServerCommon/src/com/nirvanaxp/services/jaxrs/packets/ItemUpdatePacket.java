/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.course.Course;
import com.nirvanaxp.types.entities.catalog.items.CategoryItem;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.printers.Printer;
import com.nirvanaxp.types.entities.salestax.SalesTax;
@XmlRootElement(name = "ItemUpdatePacket")
public class ItemUpdatePacket
{

	private Item item;

	private List<Printer> printersList;

	private List<Discount> discountsList;

	List<ItemsAttribute> itemsAttributesList;

	List<ItemsAttributeType> itemsAttributesTypesList;

	List<ItemsChar> itemsCharsList;

	List<CategoryItem> categoryItemsList;

	List<SalesTax> salesTaxList;

	List<Course> coursesList;

	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}

	public List<Printer> getPrintersList()
	{
		return printersList;
	}

	public void setPrintersList(List<Printer> printersList)
	{
		this.printersList = printersList;
	}

	public List<Discount> getDiscountsList()
	{
		return discountsList;
	}

	public void setDiscountsList(List<Discount> discountsList)
	{
		this.discountsList = discountsList;
	}

	public List<ItemsAttribute> getItemsAttributesList()
	{
		return itemsAttributesList;
	}

	public void setItemsAttributesList(List<ItemsAttribute> itemsAttributesList)
	{
		this.itemsAttributesList = itemsAttributesList;
	}

	public List<ItemsAttributeType> getItemsAttributesTypesList()
	{
		return itemsAttributesTypesList;
	}

	public void setItemsAttributesTypesList(List<ItemsAttributeType> itemsAttributesTypesList)
	{
		this.itemsAttributesTypesList = itemsAttributesTypesList;
	}

	public List<ItemsChar> getItemsCharsList()
	{
		return itemsCharsList;
	}

	public void setItemsCharsList(List<ItemsChar> itemsCharsList)
	{
		this.itemsCharsList = itemsCharsList;
	}

	public List<CategoryItem> getCategoryItemsList()
	{
		return categoryItemsList;
	}

	public void setCategoryItemsList(List<CategoryItem> categoryItemsList)
	{
		this.categoryItemsList = categoryItemsList;
	}

	public List<SalesTax> getSalesTaxList()
	{
		return salesTaxList;
	}

	public void setSalesTaxList(List<SalesTax> salesTaxList)
	{
		this.salesTaxList = salesTaxList;
	}

	public List<Course> getCoursesList()
	{
		return coursesList;
	}

	public void setCoursesList(List<Course> coursesList)
	{
		this.coursesList = coursesList;
	}

	@Override
	public String toString()
	{
		return "ItemUpdatePacket [item=" + item + ", printersList=" + printersList + ", discountsList=" + discountsList + ", itemsAttributesList=" + itemsAttributesList
				+ ", itemsAttributesTypesList=" + itemsAttributesTypesList + ", itemsCharsList=" + itemsCharsList + ", categoryItemsList=" + categoryItemsList + ", salesTaxList=" + salesTaxList
				+ ", coursesList=" + coursesList + "]";
	}

}
