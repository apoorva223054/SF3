package com.nirvanaxp.services.jaxrs;


public class ItemsIngredientDisplay {

	 
	private String id;
	private String itemDisplayName;
	private String categorydisplayName;
	private String stockUOMDisplayName;
	private String imageName;
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getItemDisplayName()
	{
		return itemDisplayName;
	}
	public void setItemDisplayName(String itemDisplayName)
	{
		this.itemDisplayName = itemDisplayName;
	}
	public String getCategorydisplayName()
	{
		return categorydisplayName;
	}
	public void setCategorydisplayName(String categorydisplayName)
	{
		this.categorydisplayName = categorydisplayName;
	}
	public String getStockUOMDisplayName()
	{
		return stockUOMDisplayName;
	}
	public void setStockUOMDisplayName(String stockUOMDisplayName)
	{
		this.stockUOMDisplayName = stockUOMDisplayName;
	}
	public String getImageName()
	{
		return imageName;
	}
	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}
	@Override
	public String toString()
	{
		return "ItemsIngredientDisplay [id=" + id + ", itemDisplayName=" + itemDisplayName + ", categorydisplayName=" + categorydisplayName + ", stockUOMDisplayName=" + stockUOMDisplayName
				+ ", imageName=" + imageName + "]";
	}


	
}
