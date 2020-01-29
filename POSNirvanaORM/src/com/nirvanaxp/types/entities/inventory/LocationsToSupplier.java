/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.protocol.RelationalEntitiesForStringId;

/**
 * The persistent class for the locations_to_supplier database table.
 * 
 */
@Entity
@Table(name = "locations_to_supplier")
@NamedQuery(name = "LocationsToSupplier.findAll", query = "SELECT l FROM LocationsToSupplier l")
public class LocationsToSupplier extends POSNirvanaBaseClassWithoutGeneratedIds implements RelationalEntitiesForStringId
{
	private static final long serialVersionUID = 1L;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "supplier_id")
	private String supplierId;

	public LocationsToSupplier()
	{
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getSupplierId()
	{
		if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	 if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	return supplierId;}}
	}

	public void setSupplierId(String supplierId)
	{
		this.supplierId = supplierId;
	}

	@Override
	public String toString() {
		return "LocationsToSupplier [locationsId=" + locationsId
				+ ", supplierId=" + supplierId + "]";
	}

	@Override
	public void setBaseRelation(String baseId) {
		setSupplierId(baseId);
		
	}

	@Override
	public void setBaseToObjectRelation(String baseToObjectId) {
		setLocationsId(baseToObjectId);
		
	}

}