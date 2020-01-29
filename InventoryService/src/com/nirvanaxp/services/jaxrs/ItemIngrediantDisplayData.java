package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;

public class ItemIngrediantDisplayData {

	private String uomId;
	private String ingredientId;
	private String ingredientName;
	private String ingredientUOMName;
	private BigDecimal ingredientQuantity;

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public String getIngredientUOMName() {
		return ingredientUOMName;
	}

	public void setIngredientUOMName(String ingredientUOMName) {
		this.ingredientUOMName = ingredientUOMName;
	}

	public BigDecimal getIngredientQuantity() {
		return ingredientQuantity;
	}

	public void setIngredientQuantity(BigDecimal ingredientQuantity) {
		this.ingredientQuantity = ingredientQuantity;
	}

	
	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	 

	public String getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(String ingredientId) {
		this.ingredientId = ingredientId;
	}

	@Override
	public String toString() {
		return "ItemIngrediantDisplayData [ingredientName=" + ingredientName + ", ingredientUOMName="
				+ ingredientUOMName + ", ingredientQuantity=" + ingredientQuantity + "]";
	}

}
