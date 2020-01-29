/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the inventory_order_receipt database table.
 * 
 */
@Entity
@Table(name = "inventory_order_receipt")
@NamedQuery(name = "InventoryOrderReceipt.findAll", query = "SELECT i FROM InventoryOrderReceipt i")
public class InventoryOrderReceipt extends POSNirvanaBaseClass
{
	private static final long serialVersionUID = 1L;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "purchase_order_sales_id")
	private String purchaseOrderSalesId;

	@Column(name = "purchased_quantity", precision = 10, scale = 2)
	private BigDecimal purchasedQuantity;

	@Column(name = "received_date")
	private String receivedDate;

	@Column(name = "sell_by_date")
	private String sellByDate;

	@Column(name = "supplier_id")
	private String supplierId;

	@Column(name = "unit_of_measure")
	private String unitOfMeasure;

	@Column(name = "price_msrp", precision = 10, scale = 2)
	private BigDecimal priceMsrp;

	private transient String supplierName;

	private transient String unitOfMeasureName;

	private transient String itemName;

	private transient String categoriesName;

	public InventoryOrderReceipt()
	{
	}

	public String getItemId()
	{
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}}
	}

	public void setItemId(String itemId)
	{
		this.itemId = itemId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getPurchaseOrderSalesId()
	{
		 if(purchaseOrderSalesId != null && (purchaseOrderSalesId.length()==0 || purchaseOrderSalesId.equals("0"))){return null;}else{	return purchaseOrderSalesId;}
	}

	public void setPurchaseOrderSalesId(String purchaseOrderSalesId)
	{
		this.purchaseOrderSalesId = purchaseOrderSalesId;
	}

	public BigDecimal getPurchasedQuantity()
	{
		return this.purchasedQuantity;
	}

	public void setPurchasedQuantity(BigDecimal purchasedQuantity)
	{
		this.purchasedQuantity = purchasedQuantity;
	}

	public String getReceivedDate()
	{
		return this.receivedDate;
	}

	public void setReceivedDate(String receivedDate)
	{
		this.receivedDate = receivedDate;
	}

	public String getSellByDate()
	{
		return this.sellByDate;
	}

	public void setSellByDate(String sellByDate)
	{
		this.sellByDate = sellByDate;
	}

	public String getSupplierId()
	{
		if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	 if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	return supplierId;}}
	}

	public void setSupplierId(String supplierId)
	{
		this.supplierId = supplierId;
	}

	public String getUnitOfMeasure()
	{
		 if(unitOfMeasure != null && (unitOfMeasure.length()==0 || unitOfMeasure.equals("0"))){return null;}else{	 if(unitOfMeasure != null && (unitOfMeasure.length()==0 || unitOfMeasure.equals("0"))){return null;}else{	return unitOfMeasure;}}
	}

	public void setUnitOfMeasure(String unitOfMeasure)
	{
		this.unitOfMeasure = unitOfMeasure;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public String getSupplierName()
	{
		return supplierName;
	}

	public String getUnitOfMeasureName()
	{
		return unitOfMeasureName;
	}

	public String getItemName()
	{
		return itemName;
	}

	public String getCategoriesName()
	{
		return categoriesName;
	}

	public void setSupplierName(String supplierName)
	{
		this.supplierName = supplierName;
	}

	public void setUnitOfMeasureName(String unitOfMeasureName)
	{
		this.unitOfMeasureName = unitOfMeasureName;
	}

	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

	public void setCategoriesName(String categoriesName)
	{
		this.categoriesName = categoriesName;
	}

	public BigDecimal getPriceMsrp()
	{
		return priceMsrp;
	}

	public void setPriceMsrp(BigDecimal priceMsrp)
	{
		this.priceMsrp = priceMsrp;
	}

	@Override
	public String toString() {
		return "InventoryOrderReceipt [itemId=" + itemId + ", locationId="
				+ locationId + ", purchaseOrderSalesId=" + purchaseOrderSalesId
				+ ", purchasedQuantity=" + purchasedQuantity
				+ ", receivedDate=" + receivedDate + ", sellByDate="
				+ sellByDate + ", supplierId=" + supplierId
				+ ", unitOfMeasure=" + unitOfMeasure + ", priceMsrp="
				+ priceMsrp + ", supplierName=" + supplierName
				+ ", unitOfMeasureName=" + unitOfMeasureName + ", itemName="
				+ itemName + ", categoriesName=" + categoriesName + "]";
	}

}