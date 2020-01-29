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
 * The persistent class for the category_items database table.
 * 
 */
@Entity
@Table(name = "category_items")
@XmlRootElement(name = "category_items")
public class CategoryItem extends POSNirvanaBaseClass implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "category_id", nullable = false)
	private String categoryId;

	@Column(name = "items_id", nullable = false)
	private String itemsId;

	@Column(name = "is_active", nullable = false)
	private int isActive;

	@Column(name = "is_featured_product", nullable = false)
	private int isFeaturedProduct;

	@Column(name = "sort_sequence")
	private int sortSequence;

	public CategoryItem()
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

	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public int getIsFeaturedProduct()
	{
		return this.isFeaturedProduct;
	}

	public void setIsFeaturedProduct(int isFeaturedProduct)
	{
		this.isFeaturedProduct = isFeaturedProduct;
	}

	public int getSortSequence()
	{
		return this.sortSequence;
	}

	public void setSortSequence(int sortSequence)
	{
		this.sortSequence = sortSequence;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		setItemsId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setCategoryId(baseToObjectId);

	}

	@Override
	public String toString() {
		return "CategoryItem [categoryId=" + categoryId + ", itemsId="
				+ itemsId + ", isActive=" + isActive + ", isFeaturedProduct="
				+ isFeaturedProduct + ", sortSequence=" + sortSequence + "]";
	}

}