package com.nirvanaxp.services.packet;

import java.math.BigDecimal;
import java.util.List;

import com.nirvanaxp.types.entities.locations.Location;

/**
 * @author kris
 *
 */
public class RequestOrderDetailItemsDisplayPacket {

	String itemName;
	BigDecimal quantity;
	String globleItemId;
	List<Location> suppliers;
	private String uomName;
	BigDecimal availableQuantity;

	public BigDecimal getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(BigDecimal availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getGlobleItemId() {
		return globleItemId;
	}

	public void setGlobleItemId(String globleItemId) {
		this.globleItemId = globleItemId;
	}

	public List<Location> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Location> suppliers) {
		this.suppliers = suppliers;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	@Override
	public String toString() {
		return "RequestOrderDetailItemsDisplayPacket [itemName=" + itemName + ", quantity=" + quantity
				+ ", globleItemId=" + globleItemId + ", suppliers=" + suppliers + ", uomName=" + uomName
				+ ", availableQuantity=" + availableQuantity + "]";
	}

	
	
}
