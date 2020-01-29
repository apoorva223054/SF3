package com.nirvanaxp.services.jaxrs;

import java.util.List;

import com.nirvanaxp.types.entities.catalog.items.Item;

public class AttributeIngrediantPacket {

	private List<AttributeIngrediantDisplayData> attributeIngredient;

	public List<AttributeIngrediantDisplayData> getAttributeIngredient() {
		return attributeIngredient;
	}

	public void setAttributeIngredient(List<AttributeIngrediantDisplayData> attributeIngredient) {
		this.attributeIngredient = attributeIngredient;
	}

	@Override
	public String toString() {
		return "AttributeIngrediantPacket [attributeIngredient=" + attributeIngredient + "]";
	}

}
