/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.UnitConversion;

@XmlRootElement(name = "UnitConversionPostPacket")
public class UnitConversionPostPacket extends PostPacket
{

	private UnitConversion unitConversion;
	public UnitConversion getUnitConversion() {
		return unitConversion;
	}

	public void setUnitConversion(UnitConversion unitConversion) {
		this.unitConversion = unitConversion;
	}

	private int isBaseLocationUpdate;

	
	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	@Override
	public String toString() {
		return "UnitConversionPostPacket [unitConversion=" + unitConversion
				+ ", isBaseLocationUpdate=" + isBaseLocationUpdate + "]";
	}

	

}
