/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

public class ItemInformation
{

	private String name;
	private long itemNo;
	private String shortName;
	private String displayName;
	private String description;
	private String courseName;
	private String categoryName;
	private long attributeTypeId;
	private String attributeTypeName;
	private int isRequired;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getItemNo()
	{
		return itemNo;
	}

	public void setItemNo(long itemNo)
	{
		this.itemNo = itemNo;
	}

	public String getShortName()
	{
		return shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getCourseName()
	{
		return courseName;
	}

	public void setCourseName(String courseName)
	{
		this.courseName = courseName;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public long getAttributeTypeId()
	{
		return attributeTypeId;
	}

	public void setAttributeTypeId(long attributeTypeId)
	{
		this.attributeTypeId = attributeTypeId;
	}

	public String getAttributeTypeName()
	{
		return attributeTypeName;
	}

	public void setAttributeTypeName(String attributeTypeName)
	{
		this.attributeTypeName = attributeTypeName;
	}

	public int getIsRequired()
	{
		return isRequired;
	}

	public void setIsRequired(int isRequired)
	{
		this.isRequired = isRequired;
	}

	@Override
	public String toString() {
		return "ItemInformation [name=" + name + ", itemNo=" + itemNo
				+ ", shortName=" + shortName + ", displayName=" + displayName
				+ ", description=" + description + ", courseName=" + courseName
				+ ", categoryName=" + categoryName + ", attributeTypeId="
				+ attributeTypeId + ", attributeTypeName=" + attributeTypeName
				+ ", isRequired=" + isRequired + "]";
	}

}
