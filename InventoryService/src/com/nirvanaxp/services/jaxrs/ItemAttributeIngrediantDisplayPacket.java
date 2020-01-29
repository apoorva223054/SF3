package com.nirvanaxp.services.jaxrs;

public class ItemAttributeIngrediantDisplayPacket {

	private ItemIngrediantPacket itemIngrediantPackets;
	private AttributeIngrediantPacket attributeIngrediantPackets;
	
	 
	public ItemIngrediantPacket getItemIngrediantPackets() {
		return itemIngrediantPackets;
	}
	
	public void setItemIngrediantPackets(ItemIngrediantPacket itemIngrediantPackets) {
		this.itemIngrediantPackets = itemIngrediantPackets;
	}
	
	 
	
	public AttributeIngrediantPacket getAttributeIngrediantPackets() {
		return attributeIngrediantPackets;
	}

	public void setAttributeIngrediantPackets(AttributeIngrediantPacket attributeIngrediantPackets) {
		this.attributeIngrediantPackets = attributeIngrediantPackets;
	}

	@Override
	public String toString() {
		return "ItemAttributeIngrediantDisplayPacket [itemIngrediantPackets=" + itemIngrediantPackets
				+ ", attributeIngrediantPackets=" + attributeIngrediantPackets + "]";
	}

	
}
