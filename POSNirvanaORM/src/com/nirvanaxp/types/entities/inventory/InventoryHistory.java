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

/**
 * The persistent class for the inventory database table.
 * 
 */
@Entity
@Table(name = "inventory_history")
@XmlRootElement(name = "inventory_history")
public class InventoryHistory extends POSNirvanaBaseClass
{
	private static final long serialVersionUID = 1L;

	@Column(name = "economic_order_quantity")
	private BigDecimal economicOrderQuantity;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "inventory_id")
	private String inventoryId;

	@Column(name = "minimum_order_quantity")
	private BigDecimal minimumOrderQuantity;

	@Column(name = "total_available_quanity")
	private BigDecimal totalAvailableQuanity;

	@Column(name = "total_used_quanity")
	private BigDecimal totalUsedQuanity;

	@Column(name = "86_d_threshold")
	private int d86Threshold;

	@Column(name = "is_below_threashold")
	private int isBelowThreashold;

	@Column(name = "used_quantity")
	private BigDecimal usedQuantity;
	
	@Column(name = "inventory_status_id")
	private int inventoryStatusId;
	
	@Column(name = "yield_quantity")
	private BigDecimal yieldQuantity;
	
	@Column(name = "total_receive_quantity")
	private BigDecimal totalReceiveQuantity;
	

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
	
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
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


	public InventoryHistory()
	{
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

	
	public int getIsBelowThreashold()
	{
		return isBelowThreashold;
	}

	public void setIsBelowThreashold(int isBelowThreashold)
	{
		this.isBelowThreashold = isBelowThreashold;
	}

	public String getInventoryId()
	{
		 if(inventoryId != null && (inventoryId.length()==0 || inventoryId.equals("0"))){return null;}else{	return inventoryId;}
	}

	public void setInventoryId(String inventoryId)
	{
		this.inventoryId = inventoryId;
	}

	
	public BigDecimal getUsedQuantity() {
		return usedQuantity;
	}

	public void setUsedQuantity(BigDecimal usedQuantity) {
		this.usedQuantity = usedQuantity;
	}

	public int getInventoryStatusId() {
		return inventoryStatusId;
	}


	public void setInventoryStatusId(int inventoryStatusId) {
		this.inventoryStatusId = inventoryStatusId;
	}

	@Override
	public String toString() {
		return "InventoryHistory [economicOrderQuantity="
				+ economicOrderQuantity + ", itemId=" + itemId
				+ ", inventoryId=" + inventoryId + ", minimumOrderQuantity="
				+ minimumOrderQuantity + ", totalAvailableQuanity="
				+ totalAvailableQuanity + ", totalUsedQuanity="
				+ totalUsedQuanity + ", d86Threshold=" + d86Threshold
				+ ", isBelowThreashold=" + isBelowThreashold
				+ ", usedQuantity=" + usedQuantity + ", inventoryStatusId="
				+ inventoryStatusId + ", yieldQuantity=" + yieldQuantity
				+ ", totalReceiveQuantity=" + totalReceiveQuantity
				+ ", localTime=" + localTime + ", purchasingRate="
				+ purchasingRate + ", orderDetailItemId=" + orderDetailItemId
				+ ", grnNumber=" + grnNumber + "]";
	}


	

}