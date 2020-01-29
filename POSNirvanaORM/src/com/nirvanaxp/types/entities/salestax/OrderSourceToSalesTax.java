package com.nirvanaxp.types.entities.salestax;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "order_source_to_sales_tax")
@XmlRootElement(name = "order_source_to_sales_tax")
public class OrderSourceToSalesTax extends POSNirvanaBaseClass implements Serializable{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3338236656383294436L;

	@Column(name = "source_id", nullable = false)
	private String sourceId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tax_id")
	private SalesTax taxId;
	
	@Column(name = "locations_id", nullable = false)
	private String locationsId;



	public String getSourceId() {
		 if(sourceId != null && (sourceId.length()==0 || sourceId.equals("0"))){return null;}else{	return sourceId;}
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	
	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	public SalesTax getTaxId() {
		return taxId;
	}

	public void setTaxId(SalesTax taxId) {
		this.taxId = taxId;
	}

	
	
}
