/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OptionalAttribtes")
public class OptionalAttribtes
{

	private String attributeDisplayName;
	
//	private String id;

	private String itemDisplayName;

	private String uomDisplayName;

	private String attributeIdFg;

	private int attributeBomId;

	private String itemIdRm;

	private String rmSellableUom;

	private BigDecimal quantity;

	private String categoryId;

	private String statusAttributeToBOM;

	private String categoryName;

	public String getAttributeDisplayName()
	{
		return attributeDisplayName;
	}

	public void setAttributeDisplayName(String attributeDisplayName)
	{
		this.attributeDisplayName = attributeDisplayName;
	}

	public String getItemDisplayName()
	{
		return itemDisplayName;
	}

	public void setItemDisplayName(String itemDisplayName)
	{
		this.itemDisplayName = itemDisplayName;
	}

	public String getUomDisplayName()
	{
		return uomDisplayName;
	}

	public void setUomDisplayName(String uomDisplayName)
	{
		this.uomDisplayName = uomDisplayName;
	}

	public String getAttributeIdFg()
	{
		return attributeIdFg;
	}

	public void setAttributeIdFg(String attributeIdFg)
	{
		this.attributeIdFg = attributeIdFg;
	}

	public int getAttributeBomId()
	{
		return attributeBomId;
	}

	public void setAttributeBomId(int attributeBomId)
	{
		this.attributeBomId = attributeBomId;
	}

	public String getItemIdRm()
	{
		 if(itemIdRm != null && (itemIdRm.length()==0 || itemIdRm.equals("0"))){return null;}else{	return itemIdRm;}
	}

	public void setItemIdRm(String itemIdRm)
	{
		this.itemIdRm = itemIdRm;
	}

	public String getRmSellableUom()
	{
		 if(rmSellableUom != null && (rmSellableUom.length()==0 || rmSellableUom.equals("0"))){return null;}else{	return rmSellableUom;}
	}

	public void setRmSellableUom(String rmSellableUom)
	{
		this.rmSellableUom = rmSellableUom;
	}

	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
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

	public String getStatusAttributeToBOM()
	{
		return statusAttributeToBOM;
	}

	public void setStatusAttributeToBOM(String statusAttributeToBOM)
	{
		this.statusAttributeToBOM = statusAttributeToBOM;
	}

	@Override
	public boolean equals(Object optionalAttribtes)
	{

		if (optionalAttribtes != null)
		{

			if (((OptionalAttribtes) optionalAttribtes).getAttributeIdFg() == this.getAttributeIdFg())
			{
				return true;
			}
			// check if object status is 'D'
			/*
			 * if (((OptionalAttribtes) optionalAttribtes)
			 * .getStatusAttributeToBOM() != null && ((OptionalAttribtes)
			 * optionalAttribtes)
			 * .getStatusAttributeToBOM().equalsIgnoreCase("D")) { // check
			 * object to compare status is also d if
			 * (this.getStatusAttributeToBOM() != null &&
			 * this.getStatusAttributeToBOM().equals( ((OptionalAttribtes)
			 * optionalAttribtes) .getStatusAttributeToBOM())) { //check if
			 * attribute fg is same if (this.getAttributeIdFg() ==
			 * ((OptionalAttribtes) optionalAttribtes) .getAttributeIdFg()) {
			 * //check if display name is same if
			 * (this.getAttributeDisplayName() != null && ((OptionalAttribtes)
			 * optionalAttribtes) .getAttributeDisplayName() != null &&
			 * this.getAttributeDisplayName().equals( ((OptionalAttribtes)
			 * optionalAttribtes) .getAttributeDisplayName())) { return true; }
			 * } } }
			 */

		}
		return false;

	}

	
	

}
