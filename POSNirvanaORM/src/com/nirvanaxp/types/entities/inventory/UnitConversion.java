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
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the unit_of_measurement database table.
 * 
 */
@Entity
@Table(name = "unit_conversion")
@NamedQuery(name = "UnitConversion.findAll", query = "SELECT u FROM UnitConversion u")
@XmlRootElement(name = "UnitConversion")
public class UnitConversion extends POSNirvanaBaseClass
{
	

	private static final long serialVersionUID = 1L;

	@Column(name = "from_uom_id")
	private String fromUOMId;

	@Column(name = "to_uom_id")
	private String toUOMId;
	
	

	@Column(name = "conversion_ratio")
	private BigDecimal conversionRatio;

	private transient String toUOMIdName;
	private transient String fromUOMIdName;
	public String getFromUOMId()
	{
		 if(fromUOMId != null && (fromUOMId.length()==0 || fromUOMId.equals("0"))){return null;}else{	return fromUOMId;}
	}

	public void setFromUOMId(String fromUOMId)
	{
		this.fromUOMId = fromUOMId;
	}

	public String getToUOMId()
	{
		 if(toUOMId != null && (toUOMId.length()==0 || toUOMId.equals("0"))){return null;}else{	return toUOMId;}
	}

	public void setToUOMId(String toUOMId)
	{
		this.toUOMId = toUOMId;
	}

	public BigDecimal getConversionRatio()
	{
		return conversionRatio;
	}

	public void setConversionRatio(BigDecimal conversionRatio)
	{
		this.conversionRatio = conversionRatio;
	}

	@Override
	public String toString()
	{
		return "UnitConversion [fromUOMId=" + fromUOMId + ", toUOMId=" + toUOMId + ", conversionRatio=" + conversionRatio + "]";
	}
	
	public String getToUOMIdName() {
		return toUOMIdName;
	}

	public void setToUOMIdName(String toUOMIdName) {
		this.toUOMIdName = toUOMIdName;
	}

	public String getFromUOMIdName() {
		return fromUOMIdName;
	}

	public void setFromUOMIdName(String fromUOMIdName) {
		this.fromUOMIdName = fromUOMIdName;
	}
	public UnitConversion getUnitConversion(UnitConversion unitConversion){
		UnitConversion uom = new UnitConversion();
		uom.setCreated(unitConversion.getCreated());
		uom.setCreatedBy(unitConversion.getCreatedBy());
		uom.setFromUOMId(unitConversion.getFromUOMId());
		uom.setToUOMId(unitConversion.getToUOMId());
		uom.setStatus(unitConversion.getStatus());
		uom.setUpdated(unitConversion.getUpdated());
		uom.setUpdatedBy(unitConversion.getUpdatedBy());
		uom.setConversionRatio(unitConversion.getConversionRatio());
		
		return uom;
	}
	
}

	 
