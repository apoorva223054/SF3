package com.nirvanaxp.types.entities.salestax;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "order_source_group_to_sales_tax")
@XmlRootElement(name = "order_source_group_to_sales_tax")
public class OrderSourceGroupToSalesTax extends POSNirvanaBaseClass implements Serializable{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5304411880182465431L;

	@Column(name = "source_group_id")
	private String sourceGroupId;
	
	@Column(name = "tax_id")
	private String taxId;
	
	@Column(name = "locations_id", nullable = false)
	private String locationsId;


	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	public String getSourceGroupId() {
		 if(sourceGroupId != null && (sourceGroupId.length()==0 || sourceGroupId.equals("0"))){return null;}else{	return sourceGroupId;}
	}

	public void setSourceGroupId(String sourceGroupId) {
		this.sourceGroupId = sourceGroupId;
	}

	public String getTaxId() {
		 if(taxId != null && (taxId.length()==0 || taxId.equals("0"))){return null;}else{	return taxId;}
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

		
}
