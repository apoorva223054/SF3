package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
@Entity
@Table(name = "inventory_order_receipt_for_item")
@NamedQuery(name = "InventoryOrderReceiptForItem.findAll", query = "SELECT i FROM InventoryOrderReceiptForItem i")
public class InventoryOrderReceiptForItem extends POSNirvanaBaseClass{
	
	
	private static final long serialVersionUID = 1L;

	@Column(name = "item_id")
	private String itemId;
	
	@Column(name = "inventory_order_receipt_id")
	private int inventoryOrderReceiptId;
	
	@Column(name = "location_id")
	private String locationId;

	@Column(name = "purchased_quantity", precision = 10, scale = 2)
	private BigDecimal purchasedQuantity;


	@Column(name = "sell_by_date")
	private String sellByDate;


	@Column(name = "unit_of_measure")
	private String unitOfMeasure;

	@Column(name = "price_msrp", precision = 10, scale = 2)
	private BigDecimal priceMsrp;


	private transient String categoryId; 
	
	
	public String getCategoryId() {
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getItemId() {
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public int getInventoryOrderReceiptId() {
		return inventoryOrderReceiptId;
	}

	public void setInventoryOrderReceiptId(int inventoryOrderReceiptId) {
		this.inventoryOrderReceiptId = inventoryOrderReceiptId;
	}
	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public BigDecimal getPurchasedQuantity() {
		return purchasedQuantity;
	}

	public void setPurchasedQuantity(BigDecimal purchasedQuantity) {
		this.purchasedQuantity = purchasedQuantity;
	}

	public String getSellByDate() {
		return sellByDate;
	}

	public void setSellByDate(String sellByDate) {
		this.sellByDate = sellByDate;
	}

	public String getUnitOfMeasure() {
		 if(unitOfMeasure != null && (unitOfMeasure.length()==0 || unitOfMeasure.equals("0"))){return null;}else{	return unitOfMeasure;}
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public BigDecimal getPriceMsrp() {
		return priceMsrp;
	}

	public void setPriceMsrp(BigDecimal priceMsrp) {
		this.priceMsrp = priceMsrp;
	}

	@Override
	public String toString() {
		return "InventoryOrderReceiptForItem [itemId=" + itemId
				+ ", inventoryOrderReceiptId=" + inventoryOrderReceiptId
				+ ", locationId=" + locationId + ", purchasedQuantity="
				+ purchasedQuantity + ", sellByDate=" + sellByDate
				+ ", unitOfMeasure=" + unitOfMeasure + ", priceMsrp="
				+ priceMsrp + "]";
	}
	


}
