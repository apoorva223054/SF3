package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.util.List;

import com.nirvanaxp.types.entities.locations.Location;

public class AttributeToBOMDisplayPacket {
	

	private String attributeType;
	private String attributeName;
	private String itemName;
	private String categoryName;
	private String uomName;
	private BigDecimal quantity;
	private String status;
	private int id;
	private String itemId;
	private String categoryId;
	private String uomId;
	private String attributeId;
	private String itemIdRm;
	private String attributeIdFg;
	private transient List<Location> locationList;
	
	public String getItemIdRm() {
		 if(itemIdRm != null && (itemIdRm.length()==0 || itemIdRm.equals("0"))){return null;}else{	return itemIdRm;}
	}
	public void setItemIdRm(String itemIdRm) {
		this.itemIdRm = itemIdRm;
	}
	public String getAttributeIdFg() {
		return attributeIdFg;
	}
	public void setAttributeIdFg(String attributeIdFg) {
		this.attributeIdFg = attributeIdFg;
	}
	public String getAttributeType()
	{
		return attributeType;
	}
	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}
	public String getAttributeName()
	{
		return attributeName;
	}
	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}
	public String getItemName()
	{
		return itemName;
	}
	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}
	public String getCategoryName()
	{
		return categoryName;
	}
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}
	public String getUomName()
	{
		return uomName;
	}
	public void setUomName(String uomName)
	{
		this.uomName = uomName;
	}
	public BigDecimal getQuantity()
	{
		return quantity;
	}
	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItemId()
	{
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}
	public void setItemId(String itemId)
	{
		this.itemId = itemId;
	}
	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}
	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}
	public String getUomId()
	{
		return uomId;
	}
	public void setUomId(String uomId)
	{
		this.uomId = uomId;
	}
	public String getAttributeId()
	{
		return attributeId;
	}
	public void setAttributeId(String attributeId)
	{
		this.attributeId = attributeId;
	}
	
	public List<Location> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	@Override
	public String toString()
	{
		return "AttributeToBOMDisplayPacket [attributeType=" + attributeType + ", attributeName=" + attributeName + ", itemName=" + itemName + ", categoryName=" + categoryName + ", uomName="
				+ uomName + ", quantity=" + quantity + ", status=" + status + ", id=" + id + ", itemId=" + itemId + ", categoryId=" + categoryId + ", uomId=" + uomId + ", attributeId=" + attributeId
				+ "]";
	}
	 
}
