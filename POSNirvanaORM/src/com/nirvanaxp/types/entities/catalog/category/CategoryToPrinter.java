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
 * The persistent class for the category_to_printers database table.
 * 
 */
@Entity
@Table(name = "category_to_printers")
@XmlRootElement(name = "category_to_printers")
public class CategoryToPrinter extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "category_id", nullable = false)
	private String categoryId;

	@Column(name = "printers_id", nullable = false)
	private String printersId;

	public CategoryToPrinter()
	{
	}

	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
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
		setCategoryId(baseId);

	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId)
	{
		setPrintersId(baseToObjectId);

	}

	@Override
	public boolean equals(Object obj)
	{
		// we do this as we need to check if printer relation exists or not
		if (obj instanceof CategoryToPrinter)
		{
			if (this.printersId == ((CategoryToPrinter) obj).getPrintersId())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "CategoryToPrinter [categoryId=" + categoryId + ", printersId="
				+ printersId + "]";
	}

}