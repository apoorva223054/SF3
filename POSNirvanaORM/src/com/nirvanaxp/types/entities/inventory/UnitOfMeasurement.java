/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.inventory;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the unit_of_measurement database table.
 * 
 */
@Entity
@Table(name = "unit_of_measurement")
@NamedQuery(name = "UnitOfMeasurement.findAll", query = "SELECT u FROM UnitOfMeasurement u")
@XmlRootElement(name = "UnitOfMeasurement")
public class UnitOfMeasurement extends POSNirvanaBaseClassWithoutGeneratedIds 
{
	private static final long serialVersionUID = 1L;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "display_sequence")
	private String displaySequence;

	@Column(name = "name")
	private String name;

	@Column(name = "uom_type_id")
	private int uomTypeId;

	@Column(name = "stock_uom_id")
	private String stockUomId;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "sellable_qty", precision = 10, scale = 4)
	private BigDecimal sellableQty;

	@Column(name = "stock_qty", precision = 10, scale = 4)
	private BigDecimal stockQty;

	@Column(name = "global_id")
	private String globalId;

	
	private transient List<Location> locationList;

	private transient List<UnitConversion> unitConversionList;

	public UnitOfMeasurement()
	{
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(String displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getUomTypeId()
	{
		return uomTypeId;
	}

	public String getStockUomId()
	{
		return stockUomId;
	}

	public void setUomTypeId(int uomTypeId)
	{
		this.uomTypeId = uomTypeId;
	}

	public void setStockUomId(String stockUomId)
	{
		this.stockUomId = stockUomId;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public BigDecimal getSellableQty()
	{
		if (sellableQty == null)
		{
			sellableQty = new BigDecimal(0.0);
		}
		return sellableQty;
	}

	public BigDecimal getStockQty()
	{
		if (stockQty == null)
		{
			stockQty = new BigDecimal(0.0);
		}
		return stockQty;
	}

	public void setSellableQty(BigDecimal sellableQty)
	{
		this.sellableQty = sellableQty;
	}

	public void setStockQty(BigDecimal stockQty)
	{
		this.stockQty = stockQty;
	}
	
	

	public String getGlobalId() {
		 if(globalId != null && (globalId.length()==0 || globalId.equals("0"))){return null;}else{	return globalId;}
	}

	public void setGlobalId(String globalId) {
		this.globalId = globalId;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	

	public List<UnitConversion> getUnitConversionList()
	{
		return unitConversionList;
	}

	public void setUnitConversionList(List<UnitConversion> unitConversionList)
	{
		this.unitConversionList = unitConversionList;
	}

	
	
	public UnitOfMeasurement getUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement){
		UnitOfMeasurement uom = new UnitOfMeasurement();
		uom.setCreated(unitOfMeasurement.getCreated());
		uom.setCreatedBy(unitOfMeasurement.getCreatedBy());
		uom.setName(unitOfMeasurement.getName());
		uom.setDisplayName(unitOfMeasurement.getDisplayName());
		uom.setDisplaySequence(unitOfMeasurement.getDisplaySequence());
		uom.setGlobalId(unitOfMeasurement.getGlobalId());
		uom.setStockQty(unitOfMeasurement.getStockQty());
		uom.setSellableQty(unitOfMeasurement.getSellableQty());
		uom.setStatus(unitOfMeasurement.getStatus());
		uom.setUpdated(unitOfMeasurement.getUpdated());
		uom.setUpdatedBy(unitOfMeasurement.getUpdatedBy());
		uom.setUnitConversionList(unitOfMeasurement.getUnitConversionList());
		return uom;
	}

	@Override
	public String toString()
	{
		return "UnitOfMeasurement [displayName=" + displayName + ", displaySequence=" + displaySequence + ", name=" + name + ", uomTypeId=" + uomTypeId + ", stockUomId=" + stockUomId
				+ ", locationId=" + locationId + ", sellableQty=" + sellableQty + ", stockQty=" + stockQty + ", globalId=" + globalId + "]";
	}
}