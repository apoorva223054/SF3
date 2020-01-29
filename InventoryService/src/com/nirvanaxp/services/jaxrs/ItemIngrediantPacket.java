package com.nirvanaxp.services.jaxrs;

import java.util.List;

import com.nirvanaxp.types.entities.catalog.items.Item;

public class ItemIngrediantPacket {

	private String itemId;

	private List<ItemIngrediantDisplayData> itemIngredient;

	public String getItemId() {
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public List<ItemIngrediantDisplayData> getItemIngredient() {
		return itemIngredient;
	}

	public void setItemIngredient(List<ItemIngrediantDisplayData> itemIngredient) {
		this.itemIngredient = itemIngredient;
	}

	@Override
	public String toString() {
		return "ItemIngrediantPacket [itemId=" + itemId + ", itemIngredient=" + itemIngredient + "]";
	}
	
	
	
}
