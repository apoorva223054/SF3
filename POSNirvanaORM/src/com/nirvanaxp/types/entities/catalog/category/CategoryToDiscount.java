/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the items_to_discounts database table.
 * 
 */
@Entity
@Table(name = "category_to_discounts")
@XmlRootElement(name = "category_to_discounts")
public class CategoryToDiscount extends POSNirvanaBaseClassWithoutGeneratedIds  implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "discounts_id")
	private String discountsId;

	@Column(name = "category_id")
	private String categoryId;

	public CategoryToDiscount()
	{
	}
	
	public CategoryToDiscount(String id)
	{
		this.categoryId  = id;
	}

	public String getDiscountsId()
	{
		 if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
	}

	public void setDiscountsId(String discountsId)
	{
		this.discountsId = discountsId;
	}

	/**
	 * @return the categoryId
	 */
	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	@Override
	public void setBaseRelation(String baseId)
	{
		setCategoryId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setDiscountsId(baseToObjectId);

	}

	@Override
	public boolean equals(Object obj)
	{
		// we do this as we need to check if prStringer relation exists or not
		if (obj instanceof CategoryToDiscount)
		{
			if (((CategoryToDiscount) obj).getDiscountsId()!=null && this.discountsId.equals(((CategoryToDiscount) obj).getDiscountsId()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "CategoryToDiscount [discountsId=" + discountsId
				+ ", categoryId=" + categoryId + "]";
	}

}