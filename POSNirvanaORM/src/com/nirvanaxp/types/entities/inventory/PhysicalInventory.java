/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;

@Entity
@Table(name = "physical_inventory")
@XmlRootElement(name = "PhysicalInventory")
public class PhysicalInventory extends POSNirvanaBaseClassWithoutGeneratedIds
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "inventory_id")
	private String inventoryId;

	@Column(name = "quantity")
	private BigDecimal quantity;

	@Column(name = "date")
	private String date;
	
	@Column(name = "actual_quantity")
	private BigDecimal actualQuantity;

	@Column(name = "location_id")
	private String locationId;
	
	@Column(name = "excess_shortage")
	private BigDecimal excessShortage;
	
	private transient String itemDisplayName;
	
	@Column(name = "local_time")
	private String localTime;

	@Column(name = "reason_id")
	private int reasonId;
	
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public BigDecimal getQuantity()
	{
		if(quantity == null)
		{
			return new BigDecimal(0);
		}
		return quantity;
	}

	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
	}

	public String getInventoryId() {
		 if(inventoryId != null && (inventoryId.length()==0 || inventoryId.equals("0"))){return null;}else{	return inventoryId;}
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public BigDecimal getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(BigDecimal actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getItemDisplayName() {
		return itemDisplayName;
	}

	public void setItemDisplayName(String itemDisplayName) {
		this.itemDisplayName = itemDisplayName;
	}

	public BigDecimal getExcessShortage() {
		return excessShortage;
	}

	public void setExcessShortage(BigDecimal excessShortage) {
		this.excessShortage = excessShortage;
	}

	public int getReasonId() {
		return reasonId;
	}

	public void setReasonId(int reasonId) {
		this.reasonId = reasonId;
	}

	@Override
	public String toString() {
		return "PhysicalInventory [inventoryId=" + inventoryId + ", quantity="
				+ quantity + ", date=" + date + ", actualQuantity="
				+ actualQuantity + ", locationId=" + locationId
				+ ", excessShortage=" + excessShortage + ", itemDisplayName="
				+ itemDisplayName + ", localTime=" + localTime + ", reasonId="
				+ reasonId + "]";
	}

	


}
