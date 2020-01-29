/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

/**
 * The persistent class for the inventory database table.
 * 
 */
@Entity
@Table(name = "inventory")
@XmlRootElement(name = "inventory")
public class Inventory extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "economic_order_quantity")
	private BigDecimal economicOrderQuantity;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "minimum_order_quantity")
	private BigDecimal minimumOrderQuantity;

	@Column(name = "primary_supplier_id")
	private String primarySupplierId;

	@Column(name = "secondary_supplier_id")
	private String secondarySupplierId;

	@Column(name = "tertiary_supplier_id")
	private String tertiarySupplierId;

	@Column(name = "unit_of_measurement_id")
	private String unitOfMeasurementId;

	@Column(name = "total_available_quanity")
	private BigDecimal totalAvailableQuanity;

	@Column(name = "total_used_quanity")
	private BigDecimal totalUsedQuanity;

	@Column(name = "86_d_threshold")
	private int d86Threshold;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "is_below_threashold")
	private int isBelowThreashold;

	@Column(name = "status_id")
	private int statusId;
	
	@Column(name = "yield_quantity")
	private BigDecimal yieldQuantity;
	
	@Column(name = "total_receive_quantity")
	private BigDecimal totalReceiveQuantity;
	private transient String uomName;
	private transient String itemDisplayName;
	private transient String itemTypeName;
	private transient String supplier1Name;
	private transient String supplier2Name;
	private transient String supplier3Name;

	private transient InventoryItemDefault inventoryItemDefault;

	private transient BigDecimal InventoryThreashold;
	private transient BigDecimal usedQuantity;

	private transient BigDecimal physicalQuantity;
	private transient String categoryName;
	private transient String stockUomName;
	private transient String sellableUomName;
	/*@Column(name = "unit_price")
	private BigDecimal unitPrice;*/

	@Column(name = "order_detail_item_id")
	private String orderDetailItemId;

	@Column(name = "grn_number")
	private String grnNumber;
	
	public String getOrderDetailItemId() {
		 if(orderDetailItemId != null && (orderDetailItemId.length()==0 || orderDetailItemId.equals("0"))){return null;}else{	return orderDetailItemId;}
	}

	public void setOrderDetailItemId(String orderDetailItemId) {
		this.orderDetailItemId = orderDetailItemId;
	}

	public String getGrnNumber() {
		 if(grnNumber != null && (grnNumber.length()==0 || grnNumber.equals("0"))){return null;}else{	return grnNumber;}
	}

	public void setGrnNumber(String grnNumber) {
		this.grnNumber = grnNumber;
	}
	
	
	@Column(name = "local_time")
	private String localTime;

	
	@Column(name = "purchasing_rate")
	private BigDecimal purchasingRate;
	
	public BigDecimal getPurchasingRate() {
		return purchasingRate;
	}

	public void setPurchasingRate(BigDecimal purchasingRate) {
		this.purchasingRate = purchasingRate;
	}
	
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	public Inventory()
	{
	}

	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public BigDecimal getEconomicOrderQuantity()
	{
		if (economicOrderQuantity == null)
		{
			economicOrderQuantity = new BigDecimal(0);
		}
		return this.economicOrderQuantity;
	}

	public void setEconomicOrderQuantity(BigDecimal economicOrderQuantity)
	{
		this.economicOrderQuantity = economicOrderQuantity;
	}

	public String getItemId()
	{
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}}
	}

	public void setItemId(String itemId)
	{
		this.itemId = itemId;
	}

	public BigDecimal getMinimumOrderQuantity()
	{
		return this.minimumOrderQuantity;
	}

	public void setMinimumOrderQuantity(BigDecimal minimumOrderQuantity)
	{
		if (minimumOrderQuantity == null)
		{
			minimumOrderQuantity = new BigDecimal(0);
		}
		this.minimumOrderQuantity = minimumOrderQuantity;
	}

	public String getPrimarySupplierId()
	{
		if(primarySupplierId != null && (primarySupplierId.length()==0 || primarySupplierId.equals("0"))){return null;}else{	return primarySupplierId;}
	}

	public void setPrimarySupplierId(String primarySupplierId)
	{
		this.primarySupplierId = primarySupplierId;
	}

	public String getSecondarySupplierId()
	{
		if(secondarySupplierId != null && (secondarySupplierId.length()==0 || secondarySupplierId.equals("0"))){return null;}else{	return secondarySupplierId;}
	}

	public void setSecondarySupplierId(String secondarySupplierId)
	{
		this.secondarySupplierId = secondarySupplierId;
	}

	public String getTertiarySupplierId()
	{
		if(tertiarySupplierId != null && (tertiarySupplierId.length()==0 || tertiarySupplierId.equals("0"))){return null;}else{	return tertiarySupplierId;}
	}

	public void setTertiarySupplierId(String tertiarySupplierId)
	{
		this.tertiarySupplierId = tertiarySupplierId;
	}

	public String getUnitOfMeasurementId()
	{
		 if(unitOfMeasurementId != null && (unitOfMeasurementId.length()==0 || unitOfMeasurementId.equals("0"))){return null;}else{	return unitOfMeasurementId;}
	}

	public void setUnitOfMeasurementId(String unitOfMeasurementId)
	{
		this.unitOfMeasurementId = unitOfMeasurementId;
	}

	public int getD86Threshold()
	{
		return d86Threshold;
	}

	public void setD86Threshold(int d86Threshold)
	{
		this.d86Threshold = d86Threshold;
	}

	public BigDecimal getTotalAvailableQuanity()
	{
		if (totalAvailableQuanity == null)
		{
			totalAvailableQuanity = new BigDecimal(0);
		}
		return totalAvailableQuanity;
	}

	public void setTotalAvailableQuanity(BigDecimal totalAvailableQuanity)
	{
		this.totalAvailableQuanity = totalAvailableQuanity;
	}

	public BigDecimal getTotalUsedQuanity()
	{
		if (totalUsedQuanity == null)
		{
			totalUsedQuanity = new BigDecimal(0);
		}
		return totalUsedQuanity;
	}

	public void setTotalUsedQuanity(BigDecimal totalUsedQuanity)
	{
		this.totalUsedQuanity = totalUsedQuanity;
	}

	public String getItemDisplayName()
	{
		return itemDisplayName;
	}

	public String getItemTypeName()
	{
		return itemTypeName;
	}

	public String getSupplier1Name()
	{
		return supplier1Name;
	}

	public String getSupplier2Name()
	{
		return supplier2Name;
	}

	public String getSupplier3Name()
	{
		return supplier3Name;
	}

	public void setItemDisplayName(String itemDisplayName)
	{
		this.itemDisplayName = itemDisplayName;
	}

	public void setItemTypeName(String itemTypeName)
	{
		this.itemTypeName = itemTypeName;
	}

	public void setSupplier1Name(String supplier1Name)
	{
		this.supplier1Name = supplier1Name;
	}

	public void setSupplier2Name(String supplier2Name)
	{
		this.supplier2Name = supplier2Name;
	}

	public void setSupplier3Name(String supplier3Name)
	{
		this.supplier3Name = supplier3Name;
	}

	public String getUomName()
	{
		return uomName;
	}

	public void setUomName(String uomName)
	{
		this.uomName = uomName;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public InventoryItemDefault getInventoryItemDefault()
	{
		return inventoryItemDefault;
	}

	public void setInventoryItemDefault(InventoryItemDefault inventoryItemDefault)
	{
		this.inventoryItemDefault = inventoryItemDefault;
	}

	public BigDecimal getInventoryThreashold()
	{
		return InventoryThreashold;
	}

	public void setInventoryThreashold(BigDecimal inventoryThreashold)
	{
		InventoryThreashold = inventoryThreashold;
	}

	public int getIsBelowThreashold()
	{
		return isBelowThreashold;
	}

	public void setIsBelowThreashold(int isBelowThreashold)
	{
		this.isBelowThreashold = isBelowThreashold;
	}

	public BigDecimal getUsedQuantity() {
		return usedQuantity;
	}

	public void setUsedQuantity(BigDecimal usedQuantity) {
		this.usedQuantity = usedQuantity;
	}

	
	public BigDecimal getPhysicalQuantity() {
		return physicalQuantity;
	}

	public void setPhysicalQuantity(BigDecimal physicalQuantity) {
		this.physicalQuantity = physicalQuantity;
	}
	
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getStockUomName()
	{
		return stockUomName;
	}

	public void setStockUomName(String stockUomName)
	{
		this.stockUomName = stockUomName;
	}

	 

	public String getSellableUomName()
	{
		return sellableUomName;
	}

	public void setSellableUomName(String sellableUomName)
	{
		this.sellableUomName = sellableUomName;
	}

	public int getStatusId() {
			return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public BigDecimal getYieldQuantity() {
		return yieldQuantity;
	}

	public void setYieldQuantity(BigDecimal yieldQuantity) {
		this.yieldQuantity = yieldQuantity;
	}

	public BigDecimal getTotalReceiveQuantity() {
		return totalReceiveQuantity;
	}

	public void setTotalReceiveQuantity(BigDecimal totalReceiveQuantity) {
		this.totalReceiveQuantity = totalReceiveQuantity;
	}

	@Override
	public String toString() {
		return "Inventory [displaySequence=" + displaySequence
				+ ", economicOrderQuantity=" + economicOrderQuantity
				+ ", itemId=" + itemId + ", minimumOrderQuantity="
				+ minimumOrderQuantity + ", primarySupplierId="
				+ primarySupplierId + ", secondarySupplierId="
				+ secondarySupplierId + ", tertiarySupplierId="
				+ tertiarySupplierId + ", unitOfMeasurementId="
				+ unitOfMeasurementId + ", totalAvailableQuanity="
				+ totalAvailableQuanity + ", totalUsedQuanity="
				+ totalUsedQuanity + ", d86Threshold=" + d86Threshold
				+ ", locationId=" + locationId + ", isBelowThreashold="
				+ isBelowThreashold + ", statusId=" + statusId
				+ ", yieldQuantity=" + yieldQuantity
				+ ", totalReceiveQuantity=" + totalReceiveQuantity
				+ ", orderDetailItemId=" + orderDetailItemId + ", grnNumber="
				+ grnNumber + ", localTime=" + localTime + ", purchasingRate="
				+ purchasingRate + "]";
	}

	

	
}