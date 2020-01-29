package com.nirvanaxp.services.jaxrs;

import java.util.List;

public class ItemAttributeIngrediantGetPacket {

	
	
	private String itemId;

	private List<String> attributeId;
	
	
	public String getItemId() {
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public List<String> getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(List<String> attributeId) {
		this.attributeId = attributeId;
	}
	@Override
	public String toString() {
		return "ItemAttributeIngrediantGetPacket [itemId=" + itemId + ", attributeId=" + attributeId + "]";
	}
	 
	
}
