/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.catalog.category.ItemAttributeToDate;
import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the items_attribute database table.
 * 
 */
@Entity
@Table(name = "items_attribute")
@XmlRootElement(name = "items_attribute")
public class ItemsAttribute extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "is_active")
	private int isActive;

	@Column(name = "msr_price", precision = 10, scale = 2)
	private BigDecimal msrPrice;

	@Column(name = "multi_select")
	private int multiSelect;

	@Column(nullable = false, length = 64)
	private String name;

	@Column(name = "selling_price", precision = 10, scale = 2)
	private BigDecimal sellingPrice;

	@Column(name = "sort_sequence", nullable = false)
	private int sortSequence;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "short_name", length = 64)
	private String shortName;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "hex_code_values")
	private String hexCodeValues;

	private String description;
	
	@Column(name = "global_id")
	private String globalId;

	@Column(name = "availability")
	private boolean availability;
	
	private transient List<Location> locationList;
	private transient List<ItemAttributeToDate> itemAttributeToDateList;

	@JoinColumn(name = "items_attribute_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ItemsCharToItemsAttribute> itemsCharToItemsAttributes;

	@JoinColumn(name = "items_attribute_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttributes;
	
	
	@JoinColumn(name = "items_attribute_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ItemsAttributeToNutritions> nutritionsToItemsAttributes;

	private transient String itemsAttributeTypeId;
	private transient int isRequired;

	@Column(name = "stock_uom")
	private String stockUom;

	@Column(name = "sellable_uom")
	private String sellableUom;
	
	@Column(name = "is_online_attribute")
	private int isOnlineAttribute;
	
	@Column(name = "incentive")
	private BigDecimal incentive;
	
	@Column(name = "incentive_id")
	private String incentiveId;


	public int getIsOnlineAttribute() {
		return isOnlineAttribute;
	}

	public void setIsOnlineAttribute(int isOnlineAttribute) {
		this.isOnlineAttribute = isOnlineAttribute;
	}
	
	@Column(name = "plu")
	private String plu;
	
	@Column(name = "price_inclusive_tax")
	private BigDecimal priceInclusiveTax;

	public ItemsAttribute()
	{
	}

	public ItemsAttribute(Object[] obj)
	{
		// the passed object[] must have data in this Item

		if (obj[0] != null)
		{

			setId((String) obj[0]);

		}
		if (obj[2] != null)
		{

			setCreatedBy((String) obj[2]);

		}
		if (obj[3] != null)
		{

			setIsActive((Byte) obj[3]);

		}
		if (obj[4] != null)
		{

			setMsrPrice((BigDecimal) obj[4]);

		}
		if (obj[5] != null)
		{

			setMultiSelect((Byte) obj[5]);

		}
		if (obj[6] != null)
		{

			setSellingPrice((BigDecimal) obj[6]);

		}
		if (obj[7] != null)
		{

			setSortSequence((Integer) obj[7]);

		}
		if (obj[8] != null)
		{

			setDescription((String) obj[8]);

		}
		if (obj[9] != null)
		{

			setDisplayName((String) obj[9]);

		}
		if (obj[10] != null)
		{

			setHexCodeValues((String) obj[10]);

		}
		if (obj[11] != null)
		{

			setImageName((String) obj[11]);

		}
		if (obj[12] != null)
		{

			setLocationsId((String) obj[12]);

		}
		if (obj[13] != null)
		{

			setName((String) obj[13]);

		}
		if (obj[14] != null)
		{

			setShortName((String) obj[14]);

		}
		if (obj[15] != null)
		{

			setUpdated((Timestamp) obj[15]);

		}
		if (obj[16] != null)
		{

			setUpdatedBy((String) obj[16]);

		}
		if (obj[17] != null)
		{

			setStatus("" + (Character) obj[17]);

		}
		if (obj[1] != null)
		{

			setCreated((Timestamp) obj[1]);

		}

	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public BigDecimal getMsrPrice()
	{
		return this.msrPrice;
	}

	public void setMsrPrice(BigDecimal msrPrice)
	{
		this.msrPrice = msrPrice;
	}

	public int getMultiSelect()
	{
		return this.multiSelect;
	}

	public void setMultiSelect(int multiSelect)
	{
		this.multiSelect = multiSelect;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public BigDecimal getSellingPrice()
	{
		return this.sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice)
	{
		this.sellingPrice = sellingPrice;
	}

	public int getSortSequence()
	{
		return this.sortSequence;
	}

	public void setSortSequence(int sortSequence)
	{
		this.sortSequence = sortSequence;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getHexCodeValues()
	{
		return hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues)
	{
		this.hexCodeValues = hexCodeValues;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Set<ItemsAttributeTypeToItemsAttribute> getItemsAttributeTypeToItemsAttributes()
	{
		return itemsAttributeTypeToItemsAttributes;
	}

	public void setItemsAttributeTypeToItemsAttributes(Set<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttributes)
	{
		this.itemsAttributeTypeToItemsAttributes = itemsAttributeTypeToItemsAttributes;
	}

	public String getShortName()
	{
		return shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public Set<ItemsCharToItemsAttribute> getItemsCharToItemsAttributes()
	{
		return itemsCharToItemsAttributes;
	}

	public void setItemsCharToItemsAttributes(Set<ItemsCharToItemsAttribute> itemsCharToItemsAttributes)
	{
		this.itemsCharToItemsAttributes = itemsCharToItemsAttributes;
	}

	public String getStockUom()
	{
		 if(stockUom != null && (stockUom.length()==0 || stockUom.equals("0"))){return null;}else{	return stockUom;}
	}

	public String getSellableUom()
	{
		 if(sellableUom != null && (sellableUom.length()==0 || sellableUom.equals("0"))){return null;}else{	return sellableUom;}
	}

	public void setStockUom(String stockUom)
	{
		this.stockUom = stockUom;
	}

	public void setSellableUom(String sellableUom)
	{
		this.sellableUom = sellableUom;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public String getItemsAttributeTypeId()
	{
		return itemsAttributeTypeId;
	}

	public void setItemsAttributeTypeId(String itemsAttributeTypeId)
	{
		this.itemsAttributeTypeId = itemsAttributeTypeId;
	}

	public int getIsRequired()
	{
		return isRequired;
	}

	public void setIsRequired(int isRequired)
	{
		this.isRequired = isRequired;
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

	public String getPlu()
	{
		return plu;
	}

	public void setPlu(String plu)
	{
		this.plu = plu;
	}

	public BigDecimal getPriceInclusiveTax()
	{
		return priceInclusiveTax;
	}

	public void setPriceInclusiveTax(BigDecimal priceInclusiveTax)
	{
		this.priceInclusiveTax = priceInclusiveTax;
	}

	

	

	public Set<ItemsAttributeToNutritions> getNutritionsToItemsAttributes() {
		return nutritionsToItemsAttributes;
	}

	public void setNutritionsToItemsAttributes(Set<ItemsAttributeToNutritions> nutritionsToItemsAttributes) {
		this.nutritionsToItemsAttributes = nutritionsToItemsAttributes;
	}

 

	public List<ItemAttributeToDate> getItemAttributeToDateList() {
		return itemAttributeToDateList;
	}

	public void setItemAttributeToDateList(List<ItemAttributeToDate> itemAttributeToDateList) {
		this.itemAttributeToDateList = itemAttributeToDateList;
	}

	@Override
	public String toString() {
		return "ItemsAttribute [isActive=" + isActive + ", msrPrice=" + msrPrice + ", multiSelect=" + multiSelect
				+ ", name=" + name + ", sellingPrice=" + sellingPrice + ", sortSequence=" + sortSequence
				+ ", displayName=" + displayName + ", shortName=" + shortName + ", locationsId=" + locationsId
				+ ", imageName=" + imageName + ", hexCodeValues=" + hexCodeValues + ", description=" + description
				+ ", globalId=" + globalId + ", availability=" + availability + ", itemsCharToItemsAttributes="
				+ itemsCharToItemsAttributes + ", itemsAttributeTypeToItemsAttributes="
				+ itemsAttributeTypeToItemsAttributes + ", nutritionsToItemsAttributes=" + nutritionsToItemsAttributes
				+ ", stockUom=" + stockUom + ", sellableUom=" + sellableUom + ", isOnlineAttribute=" + isOnlineAttribute
				+ ", incentive=" + incentive + ", incentiveId=" + incentiveId + ", plu=" + plu + ", priceInclusiveTax="
				+ priceInclusiveTax + "]";
	}

	public ItemsAttribute getItemsAttribute(ItemsAttribute  item){
		ItemsAttribute attribute= new ItemsAttribute();
		attribute.setCreated(item.getCreated());
		attribute.setCreatedBy(item.getCreatedBy());
		attribute.setDescription(item.getDescription());
		attribute.setDisplayName(item.getDisplayName());
		attribute.setGlobalId(item.getGlobalId());
		attribute.setHexCodeValues(item.getHexCodeValues());
		attribute.setImageName(item.getImageName());
		attribute.setIsActive(item.getIsActive());
		attribute.setIsOnlineAttribute(item.getIsOnlineAttribute());
		attribute.setIsRequired(item.getIsRequired());
		attribute.setItemsAttributeTypeId(item.getItemsAttributeTypeId());
		attribute.setItemsAttributeTypeToItemsAttributes(item.getItemsAttributeTypeToItemsAttributes());
		attribute.setItemsCharToItemsAttributes(item.getItemsCharToItemsAttributes());
		attribute.setNutritionsToItemsAttributes(item.getNutritionsToItemsAttributes());
		attribute.setLocationList(item.getLocationList());
		attribute.setLocationsId(item.getLocationsId());
		attribute.setMsrPrice(item.getMsrPrice());
		attribute.setMultiSelect(item.getMultiSelect());
		attribute.setName(item.getName());
		attribute.setSellableUom(item.getSellableUom());
		attribute.setSellingPrice(item.getSellingPrice());
		attribute.setShortName(item.getShortName());
		attribute.setSortSequence(item.getSortSequence());
		attribute.setStatus(item.getStatus());
		attribute.setStockUom(item.getStockUom());
		attribute.setUpdated(item.getUpdated());
		attribute.setUpdatedBy(item.getUpdatedBy());
		attribute.setPlu(item.getPlu());
		attribute.setPriceInclusiveTax(item.getPriceInclusiveTax());
		attribute.setItemAttributeToDateList(item.getItemAttributeToDateList());
		attribute.setAvailability(item.isAvailability());
		attribute.setIncentiveId(item.getIncentiveId());
		attribute.setIncentive(item.getIncentive());
		return attribute;
	}

	public boolean isAvailability() {
		return availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}

	public BigDecimal getIncentive() {
		return incentive;
	}

	public void setIncentive(BigDecimal incentive) {
		this.incentive = incentive;
	}

	public String getIncentiveId() {
		return incentiveId;
	}

	public void setIncentiveId(String incentiveId) {
		this.incentiveId = incentiveId;
	}
	
	
}