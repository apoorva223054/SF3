/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import com.nirvanaxp.types.entities.catalog.category.Category;

public class CategoryWithItems
{

	private Category category;

	private List<Integer> itemsIdsList;

	private CategoryRevenuePacket categoryRevenuePacket;

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public List<Integer> getItemsIdsList()
	{
		return itemsIdsList;
	}

	public void setItemsIdsList(List<Integer> itemsIdsList)
	{
		this.itemsIdsList = itemsIdsList;
	}

	public CategoryRevenuePacket getCategoryRevenuePacket()
	{
		return categoryRevenuePacket;
	}

	public void setCategoryRevenuePacket(CategoryRevenuePacket categoryRevenuePacket)
	{
		this.categoryRevenuePacket = categoryRevenuePacket;
	}

}
