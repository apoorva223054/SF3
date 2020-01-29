/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.salestax;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.Location;


/**
 * The persistent class for the business_hours database table.
 * 
 */
@Entity
@Table(name = "sales_tax")
@XmlRootElement(name = "sales_tax")
public class SalesTax implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "tax_name")
	private String taxName;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "rate", nullable = false, precision = 15, scale = 2)
	private BigDecimal rate;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "status")
	private String status;

	@Column(name = "is_item_specific")
	private int isItemSpecific;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "tax_id")
	private String taxId;
	
	@Column(name = "number_of_people")
	private int numberOfPeople;
	
	@Column(name = "global_id")
	private String globalId;
	
	@Column(name = "option_type_id")
	private int optionTypeId;

	private transient List<Location> locationList;
	
	private transient List<String> taxIdList ;
	private transient List<OrderSourceGroupToSalesTax> orderSourceGroupToSalesTaxList ;
	
	private transient BigDecimal priceTax ;
	
	public BigDecimal getPriceTax()
	{
		if(priceTax == null)
		{
			return new BigDecimal(0);
		}
		return priceTax;
	}

	public void setPriceTax(BigDecimal priceTax)
	{
		this.priceTax = priceTax;
	}

	public List<OrderSourceGroupToSalesTax> getOrderSourceGroupToSalesTaxList() {
		return orderSourceGroupToSalesTaxList;
	}

	public void setOrderSourceGroupToSalesTaxList(
			List<OrderSourceGroupToSalesTax> orderSourceGroupToSalesTaxList) {
		this.orderSourceGroupToSalesTaxList = orderSourceGroupToSalesTaxList;
	}

	public List<String> getTaxIdList() {
		return taxIdList;
	}

	public void setTaxIdList(List<String> taxIdList) {
		this.taxIdList = taxIdList;
	}

	public String getTaxId() {
		 if(taxId != null && (taxId.length()==0 || taxId.equals("0"))){return null;}else{	return taxId;}
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public int getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(int numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

	public SalesTax()
	{
	}

	

	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getTaxName()
	{
		return taxName;
	}

	public void setTaxName(String taxName)
	{
		this.taxName = taxName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public BigDecimal getRate()
	{
		return rate;
	}

	public void setRate(BigDecimal rate)
	{
		this.rate = rate;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getIsItemSpecific()
	{
		return isItemSpecific;
	}

	public void setIsItemSpecific(int isItemSpecific)
	{
		this.isItemSpecific = isItemSpecific;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
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
	
	

	public int getOptionTypeId() {
		return optionTypeId;
	}

	public void setOptionTypeId(int optionTypeId) {
		this.optionTypeId = optionTypeId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "SalesTax [id=" + id + ", created=" + created + ", updated="
				+ updated + ", createdBy=" + createdBy + ", taxName=" + taxName
				+ ", displayName=" + displayName + ", rate=" + rate
				+ ", locationsId=" + locationsId + ", status=" + status
				+ ", isItemSpecific=" + isItemSpecific + ", updatedBy="
				+ updatedBy + ", taxId=" + taxId + ", numberOfPeople="
				+ numberOfPeople + ", globalId=" + globalId + ", optionTypeId=" + optionTypeId + "]";
	}

	public SalesTax getSalesTax(SalesTax tax){
		SalesTax t= new SalesTax();
		t.setCreated(tax.getCreated());
		t.setCreatedBy(tax.getCreatedBy());
		t.setDisplayName(tax.getDisplayName());
		t.setGlobalId(tax.getGlobalId());
		t.setIsItemSpecific(tax.getIsItemSpecific());
		t.setLocationList(tax.getLocationList());
		t.setLocationsId(tax.getLocationsId());
		t.setNumberOfPeople(tax.getNumberOfPeople());
		t.setOrderSourceGroupToSalesTaxList(tax.getOrderSourceGroupToSalesTaxList());
		t.setRate(tax.getRate());
		t.setStatus(tax.getStatus());
		t.setTaxId(tax.getTaxId());
		t.setTaxIdList(tax.getTaxIdList());
		t.setTaxName(tax.getTaxName());
		t.setUpdated(tax.getUpdated());
		t.setOptionTypeId(tax.getOptionTypeId());
		t.setUpdatedBy(tax.getUpdatedBy());
		return t;
	}
	 
}