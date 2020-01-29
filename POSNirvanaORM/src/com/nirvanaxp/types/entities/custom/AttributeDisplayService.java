/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AttributeDisplayService")
public class AttributeDisplayService implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3952497475086418103L;
	private String id;
	private String itemsAttributeTypeName;
	private String itemsAttributeId;
	private String imageName;
	private BigDecimal price;
	private String itemsAttributeName;
	private String itemsAttributeDisplayName;
	private String itemsAttributeShortName;

	private int isRequired;

	public AttributeDisplayService()
	{

	}

	public AttributeDisplayService(Object[] obj)
	{
		setId((String) obj[0]);
		setItemsAttributeTypeName((String) obj[1]);
		setItemsAttributeId((String) obj[2]);
		setItemsAttributeName((String) obj[3]);
		setImageName((String) obj[4]);
		setPrice((BigDecimal) obj[5]);
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getItemsAttributeTypeName()
	{
		return itemsAttributeTypeName;
	}

	public void setItemsAttributeTypeName(String itemsAttributeTypeName)
	{
		this.itemsAttributeTypeName = itemsAttributeTypeName;
	}

	public String getItemsAttributeId()
	{
		 if(itemsAttributeId != null && (itemsAttributeId.length()==0 || itemsAttributeId.equals("0"))){return null;}else{	return itemsAttributeId;}
	}

	public void setItemsAttributeId(String itemsAttributeId)
	{
		this.itemsAttributeId = itemsAttributeId;
	}

	public String getItemsAttributeName()
	{
		return itemsAttributeName;
	}

	public void setItemsAttributeName(String itemsAttributeName)
	{
		this.itemsAttributeName = itemsAttributeName;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public BigDecimal getPrice()
	{
		return price;
	}

	public void setPrice(BigDecimal price)
	{
		this.price = price;
	}

	public int getIsRequired()
	{
		return isRequired;
	}

	public void setIsRequired(int isRequired)
	{
		this.isRequired = isRequired;
	}

	public String getItemsAttributeDisplayName() {
		return itemsAttributeDisplayName;
	}

	public void setItemsAttributeDisplayName(String itemsAttributeDisplayName) {
		this.itemsAttributeDisplayName = itemsAttributeDisplayName;
	}

	public String getItemsAttributeShortName() {
		return itemsAttributeShortName;
	}

	public void setItemsAttributeShortName(String itemsAttributeShortName) {
		this.itemsAttributeShortName = itemsAttributeShortName;
	}
	

}
