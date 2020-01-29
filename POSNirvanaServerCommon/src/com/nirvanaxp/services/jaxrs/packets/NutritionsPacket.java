/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.Nutritions;
@XmlRootElement(name = "NutritionsPacket")
public class NutritionsPacket extends PostPacket
{

	private Nutritions nutritions;
	private int isBaseLocationUpdate;
	private String locationsListId;

	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	 
	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	public Nutritions getNutritions() {
		return nutritions;
	}

	public void setNutritions(Nutritions nutritions) {
		this.nutritions = nutritions;
	}

	@Override
	public String toString() {
		return "NutritionsPacket [nutritions=" + nutritions + ", isBaseLocationUpdate=" + isBaseLocationUpdate + "]";
	}
 
	 
}
