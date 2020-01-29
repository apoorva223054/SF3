package com.nirvanaxp.services.jaxrs;

import java.util.List;



public class CategoryDisplayPacket {

	private CategoryDetailDisplayPacket category;
	private List<CategoryDetailDisplayPacket> subCategory;
	public CategoryDetailDisplayPacket getCategory()
	{
		return category;
	}
	public void setCategory(CategoryDetailDisplayPacket category)
	{
		this.category = category;
	}
	public List<CategoryDetailDisplayPacket> getSubCategory()
	{
		return subCategory;
	}
	public void setSubCategory(List<CategoryDetailDisplayPacket> subCategory)
	{
		this.subCategory = subCategory;
	}
	 
	
 
}
