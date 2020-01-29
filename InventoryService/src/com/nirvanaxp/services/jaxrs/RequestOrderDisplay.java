package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.math.BigInteger;


public class RequestOrderDisplay {

	private String id;

	private String supplierName;
	private String poNumber;
	private String date;
	private String name;
	private String locationName;
	private String locationId;
	private int supplierTypeId;
	
	private String grnNumber;

	private BigDecimal taxRate1;
	private BigDecimal taxRate2;
	private BigDecimal taxRate3;
	private BigDecimal taxRate4;

	
	public String getGrnNumber() {
		 if(grnNumber != null && (grnNumber.length()==0 || grnNumber.equals("0"))){return null;}else{	return grnNumber;}
	}

	public void setGrnNumber(String grnNumber) {
		this.grnNumber = grnNumber;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	public int getSupplierTypeId()
	{
		return supplierTypeId;
	}

	public void setSupplierTypeId(int supplierTypeId)
	{
		this.supplierTypeId = supplierTypeId;
	}

	public BigDecimal getTaxRate1() {
		return taxRate1;
	}

	public void setTaxRate1(BigDecimal taxRate1) {
		this.taxRate1 = taxRate1;
	}

	public BigDecimal getTaxRate2() {
		return taxRate2;
	}

	public void setTaxRate2(BigDecimal taxRate2) {
		this.taxRate2 = taxRate2;
	}

	public BigDecimal getTaxRate3() {
		return taxRate3;
	}

	public void setTaxRate3(BigDecimal taxRate3) {
		this.taxRate3 = taxRate3;
	}

	public BigDecimal getTaxRate4() {
		return taxRate4;
	}

	public void setTaxRate4(BigDecimal taxRate4) {
		this.taxRate4 = taxRate4;
	}

	@Override
	public String toString() {
		return "RequestOrderDisplay [id=" + id + ", supplierName="
				+ supplierName + ", poNumber=" + poNumber + ", date=" + date
				+ ", name=" + name + ", locationName=" + locationName
				+ ", locationId=" + locationId + ", supplierTypeId="
				+ supplierTypeId + ", grnNumber=" + grnNumber + ", taxRate1="
				+ taxRate1 + ", taxRate2=" + taxRate2 + ", taxRate3="
				+ taxRate3 + ", taxRate4=" + taxRate4 + "]";
	}
	

	

	

}
