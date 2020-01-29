/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.partners.TimezoneTime;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.catalog.category.ItemToDate;
import com.nirvanaxp.types.entities.locations.Location;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;

/**
 * The persistent class for the items database table.
 * 
 */
@Entity
@Table(name = "items")
@XmlRootElement(name = "items")
public class Item extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Column(name = "course_id")
	private String courseId;

	@Lob()
	@Column(nullable = false)
	private String description;
	@Column(name = "hex_code_values")
	private String hexCodeValues;

	@Temporal(TemporalType.DATE)
	@Column(name = "effective_end_date")
	private Date effectiveEndDate;

	@Column(name = "start_time")
	private String startTime;

	
	@Column(name = "end_time")
	private String endTime;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "effective_start_date")
	private Date effectiveStartDate;
	@Column(name = "image_name", length = 256)
	private String imageName;

	@Column(name = "is_active", nullable = false, length = 1)
	private int isActive;
	
	@Column(name = "is_deleted", nullable = false, length = 1)
	private int isDeleted;

	@Column(name = "is_featured", nullable = false, length = 1)
	private int isFeatured;

	@Column(name = "is_in_stock", nullable = false, length = 1)
	private int isInStock;

	@Column(name = "is_rating_allowed", nullable = false, length = 1)
	private int isRatingAllowed;

	@Column(name = "is_review_allowed", nullable = false, length = 1)
	private int isReviewAllowed;

	@Column(name = "item_number", nullable = false, length = 20)
	private String itemNumber;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "price_msrp", precision = 10, scale = 2)
	private BigDecimal priceMsrp;

	@Column(name = "price_selling", nullable = false, precision = 10, scale = 2)
	private BigDecimal priceSelling;

	@Column(name = "rating_total", nullable = false)
	private int ratingTotal;

	@Column(name = "rating_votes_total")
	private int ratingVotesTotal;

	@Column(name = "short_name", nullable = false, length = 50)
	private String shortName;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "item_type")
	private int itemType;

	@Column(name = "is_below_threashold")
	private int isBelowThreashold;

	@Column(name = "is_realtime_update_needed")
	private int isRealTimeUpdateNeeded;
	
	@Column(name = "item_group_id")
	private String itemGroupId;

	@Column(name = "is_online_item")
	private int isOnlineItem;
	
	@Column(name = "sellable_uom")
	private String sellableUom;
	
	
	
	@Column(name = "purchasing_rate")
	private BigDecimal purchasingRate;

	@Column(name = "plu")
	private String plu;
	
	
	@Column(name = "is_scan_required")
	private int isScanRequired;
	
	@Column(name = "is_manual_Quantity")
	private int isManualQuantity;
	
	@Column(name = "is_manual_price")
	private int isManualPrice;
	
	@Column(name = "price_inclusive_tax")
	private BigDecimal priceInclusiveTax;
	
	@Column(name = "yield_percent")
	private BigDecimal yieldPercent;
	
	@Column(name = "is_online_display")
	private int isOnlineDisplay;
	
	@Column(name = "label_ingredients")
	private String  labelIngredients;
	
	@Column(name = "storage_type_id")
	private String storageTypeId;
	
	@Column(name = "contains")
	private String contains;
	
	@Column(name = "lead_time")
	private String leadTime;
	
	@Column(name = "cut_off_time")
	private String cutOffTime;
	
	@Column(name = "incentive")
	private BigDecimal incentive;
	
	@Column(name = "incentive_id")
	private String incentiveId;
	
	@Column(name = "availability")
	private boolean availability;
	
	private transient String stockUomName;

	private transient String sellableUomName;

	private transient String itemTypeName;

	private transient List<ItemsAttributeType> itemsAttributeTypes;

	private transient List<ItemsChar> itemsChars;

	private transient List<ItemsAttribute> itemsAttributes;

	private transient Set<ItemsAttributeType> itemsAttributeTypesSet;

	private transient Set<ItemsChar> itemsCharsSet;

	private transient Set<ItemsAttribute> itemsAttributesSet;

	private transient List<ItemsToPrinter> itemsToPrinters;
	

	private transient List<ItemsToDiscount> itemsToDiscounts;

	private transient List<ItemsToItemsChar> itemsToItemsChars;
	private transient List<ItemsToItemsAttribute> itemsToItemsAttributes;
	private transient List<ItemsToItemsAttributeType> itemsToItemsAttributesAttributeTypes;
	private transient List<CategoryItem> categoryItems;
	private transient ItemToSupplier itemToSuppliers;
	private transient List<Location> locationList;
	private transient BigDecimal quantity;
	private transient List<ItemsToNutritions> itemsToNutritions;

	private transient List<ItemsToSchedule> itemsToSchedule;
	private transient List<ItemToDate> itemToDate;
	

	public List<ItemsToSchedule> getItemsToSchedule() {
		return itemsToSchedule;
	}

	public void setItemsToSchedule(List<ItemsToSchedule> itemsToSchedule) {
		this.itemsToSchedule = itemsToSchedule;
	}
	
	public BigDecimal getQuantity()
	{
		return quantity;
	}

	public void setQuantity(BigDecimal quantity)
	{
		this.quantity = quantity;
	}

	private transient int isPrinterUpdate;
	
	private transient int isDiscountUpdate;
	
	private transient int isCategoryItemUpdate;
	private transient int isAttributeListUpdate;
	private transient int isAttributeTypeListUpdate;
	private transient int isItemToCharUpdate;
	private transient int isItemsToNutritionUpdate;
	private transient int isItemsToScheduleUpdate;

	

	@Column(name = "sales_tax_1")
	private String salesTax1;

	@Column(name = "sales_tax_2")
	private String salesTax2;

	@Column(name = "sales_tax_3")
	private String salesTax3;

	@Column(name = "sales_tax_4")
	private String salesTax4;

	@Column(name = "stock_uom")
	private String stockUom;

	

	@Column(name = "inventory_accrual")
	private int inventoryAccrual;

	@Column(name = "is_inventory_accrual_overriden")
	private int isinventoryAccrualOverriden;

	@Column(name = "global_item_id")
	private String globalItemId;
	
	@Column(name = "distribution_price", precision = 10, scale = 2)
	private BigDecimal distributionPrice;
	
	@Column(name = "is_weighing_scale")
	private int isWeighingScale;
	
	public BigDecimal getDistributionPrice() {
		return distributionPrice;
	}

	public void setDistributionPrice(BigDecimal distributionPrice) {
		this.distributionPrice = distributionPrice;
	}

	public String getGlobalItemId() {
		 if(globalItemId != null && (globalItemId.length()==0 || globalItemId.equals("0"))){return null;}else{	return globalItemId;}
	}

	public void setGlobalItemId(String globalItemId) {
		this.globalItemId = globalItemId;
	}

	public BigDecimal getPriceInclusiveTax()
	{
		return priceInclusiveTax;
	}

	public void setPriceInclusiveTax(BigDecimal priceInclusiveTax)
	{
		this.priceInclusiveTax = priceInclusiveTax;
	}
	
	
	public int getIsOnlineDisplay()
	{
		return isOnlineDisplay;
	}

	public void setIsOnlineDisplay(int isOnlineDisplay)
	{
		this.isOnlineDisplay = isOnlineDisplay;
	}

	public Item()
	{
	}

	public Item(Object[] obj)
	{
		// the passed object[] must have data in this Item

		if (obj[0] != null)
		{

			setId((String) obj[0]);

		}
		if (obj[1] != null)
		{

			setCourseId((String) obj[1]);

		}
		if (obj[2] != null)
		{

			setCreated((Timestamp) obj[2]);

		}
		if (obj[3] != null)
		{

			setCreatedBy((String) obj[3]);

		}
		if (obj[4] != null)
		{

			setDescription((String) obj[4]);

		}
		if (obj[5] != null)
		{

			setDisplayName((String) obj[5]);

		}
		if (obj[6] != null)
		{
			setDisplaySequence((Integer) obj[6]);
		}
		if (obj[7] != null)
		{
			setEffectiveEndDate((Date) obj[7]);
		}
		if (obj[8] != null)
		{
			setEffectiveStartDate((Date) obj[8]);
		}
		if (obj[9] != null)
		{
			setHexCodeValues((String) obj[9]);
		}
		if (obj[10] != null)
		{
			setImageName((String) obj[10]);
		}
		if (obj[11] != null)
		{
			setIsActive((Integer) obj[11]);
		}
		if (obj[12] != null)
		{
			setIsDeleted((Integer) obj[12]);
		}

		if (obj[13] != null)
		{
			setIsFeatured((Integer) obj[13]);
		}
		if (obj[14] != null)
		{
			setIsInStock((Integer) obj[14]);
		}
		if (obj[15] != null)
		{
			setIsRatingAllowed((Integer) obj[15]);
		}
		if (obj[16] != null)
		{
			setIsReviewAllowed((Integer) obj[16]);
		}
		if (obj[17] != null)
		{
			setItemNumber((String) obj[17]);
		}

		if (obj[18] != null)
		{
			setLocationsId((String) obj[18]);
		}
		if (obj[19] != null)
		{
			setName((String) obj[19]);
		}
		if (obj[20] != null)
		{
			setPriceMsrp((BigDecimal) obj[20]);
		}
		if (obj[21] != null)
		{
			setPriceSelling((BigDecimal) obj[21]);
		}
		if (obj[22] != null)
		{
			setRatingTotal((Integer) obj[22]);
		}

		if (obj[23] != null)
		{
			setRatingVotesTotal((Integer) obj[23]);
		}
		if (obj[24] != null)
		{
			setShortName((String) obj[24]);
		}
		if (obj[25] != null)
		{
			setStatus((String) obj[25]);
		}
		if (obj[26] != null)
		{
			setUpdated((Timestamp) obj[26]);
		}
		if (obj[27] != null)
		{
			setUpdatedBy((String) obj[27]);
		}
		if (obj[28] != null)
		{
			setSalesTax1((String) obj[28]);
		}
		if (obj[29] != null)
		{
			setSalesTax2((String) obj[29]);
		}
		if (obj[30] != null)
		{
			setSalesTax3((String) obj[30]);
		}
		if (obj[31] != null)
		{
			setSalesTax4((String) obj[31]);
		}

	}

	public String getCourseId()
	{
		 if(courseId != null && (courseId.length()==0 || courseId.equals("0"))){return null;}else{	return courseId;}
	}

	public void setCourseId(String courseId)
	{
		this.courseId = courseId;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getEffectiveEndDate()
	{
		return this.effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate)
	{
		this.effectiveEndDate = effectiveEndDate;
	}

	public Date getEffectiveStartDate()
	{
		return this.effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate)
	{
		this.effectiveStartDate = effectiveStartDate;
	}

	public String getImageName()
	{
		return this.imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

 
	public int getIsFeatured()
	{
		return this.isFeatured;
	}

	public void setIsFeatured(int isFeatured)
	{
		this.isFeatured = isFeatured;
	}

	public int getIsInStock()
	{
		return this.isInStock;
	}

	public void setIsInStock(int isInStock)
	{
		this.isInStock = isInStock;
	}

	public int getIsRatingAllowed()
	{
		return this.isRatingAllowed;
	}

	public void setIsRatingAllowed(int isRatingAllowed)
	{
		this.isRatingAllowed = isRatingAllowed;
	}

	public int getIsReviewAllowed()
	{
		return this.isReviewAllowed;
	}

	public void setIsReviewAllowed(int isReviewAllowed)
	{
		this.isReviewAllowed = isReviewAllowed;
	}

	public String getItemNumber()
	{
		return this.itemNumber;
	}

	public void setItemNumber(String itemNumber)
	{
		this.itemNumber = itemNumber;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public BigDecimal getPriceMsrp()
	{
		return this.priceMsrp;
	}

	public void setPriceMsrp(BigDecimal priceMsrp)
	{
		this.priceMsrp = priceMsrp;
	}

	public BigDecimal getPriceSelling()
	{
		return this.priceSelling;
	}

	public void setPriceSelling(BigDecimal priceSelling)
	{
		this.priceSelling = priceSelling;
	}

	public int getRatingTotal()
	{
		return this.ratingTotal;
	}

	public void setRatingTotal(int ratingTotal)
	{
		this.ratingTotal = ratingTotal;
	}

	public int getRatingVotesTotal()
	{
		return this.ratingVotesTotal;
	}

	public void setRatingVotesTotal(int ratingVotesTotal)
	{
		this.ratingVotesTotal = ratingVotesTotal;
	}

	public String getShortName()
	{
		return this.shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getDisplaySequence()
	{
		return displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getHexCodeValues()
	{
		return hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues)
	{
		this.hexCodeValues = hexCodeValues;
	}

	public int getIsActive()
	{
		return isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public String getSalesTax1()
	{
		 if(salesTax1 != null && (salesTax1.length()==0 || salesTax1.equals("0"))){return null;}else{	return salesTax1;}
	}

	public void setSalesTax1(String salesTax1)
	{
		this.salesTax1 = salesTax1;
	}

	public String getSalesTax2()
	{
		 if(salesTax2 != null && (salesTax2.length()==0 || salesTax2.equals("0"))){return null;}else{	return salesTax2;}
	}

	public void setSalesTax2(String salesTax2)
	{
		this.salesTax2 = salesTax2;
	}

	public String getSalesTax3()
	{
		 if(salesTax3 != null && (salesTax3.length()==0 || salesTax3.equals("0"))){return null;}else{	return salesTax3;}
	}

	public void setSalesTax3(String salesTax3)
	{
		this.salesTax3 = salesTax3;
	}

	public String getSalesTax4()
	{
		 if(salesTax4 != null && (salesTax4.length()==0 || salesTax4.equals("0"))){return null;}else{	return salesTax4;}
	}

	public void setSalesTax4(String salesTax4)
	{
		this.salesTax4 = salesTax4;
	}

	public List<ItemsChar> getItemsChars()
	{
		return itemsChars;
	}

	public void setItemsChars(List<ItemsChar> itemsChars)
	{
		this.itemsChars = itemsChars;
	}

	public List<ItemsAttribute> getItemsAttributes()
	{
		return itemsAttributes;
	}

	public void setItemsAttributes(List<ItemsAttribute> itemsAttributes)
	{
		this.itemsAttributes = itemsAttributes;
	}

	public List<ItemsToPrinter> getItemsToPrinters()
	{
		return itemsToPrinters;
	}

	public void setItemsToPrinters(List<ItemsToPrinter> itemsToPrinters)
	{
		this.itemsToPrinters = itemsToPrinters;
	}

	public List<ItemsToDiscount> getItemsToDiscounts()
	{
		return itemsToDiscounts;
	}

	public void setItemsToDiscounts(List<ItemsToDiscount> itemsToDiscounts)
	{
		this.itemsToDiscounts = itemsToDiscounts;
	}

	public List<ItemsToItemsChar> getItemsToItemsChars()
	{
		return itemsToItemsChars;
	}

	public void setItemsToItemsChars(List<ItemsToItemsChar> itemsToItemsChars)
	{
		this.itemsToItemsChars = itemsToItemsChars;
	}

	public List<ItemsToItemsAttribute> getItemsToItemsAttributes()
	{
		return itemsToItemsAttributes;
	}

	public void setItemsToItemsAttributes(List<ItemsToItemsAttribute> itemsToItemsAttributes)
	{
		this.itemsToItemsAttributes = itemsToItemsAttributes;
	}

	public List<ItemsToItemsAttributeType> getItemsToItemsAttributesAttributeTypes()
	{
		return itemsToItemsAttributesAttributeTypes;
	}

	public void setItemsToItemsAttributesAttributeTypes(List<ItemsToItemsAttributeType> itemsToItemsAttributesAttributeTypes)
	{
		this.itemsToItemsAttributesAttributeTypes = itemsToItemsAttributesAttributeTypes;
	}

	public List<CategoryItem> getCategoryItems()
	{
		return categoryItems;
	}

	public void setCategoryItems(List<CategoryItem> categoryItems)
	{
		this.categoryItems = categoryItems;
	}

	public int getItemType()
	{
		return itemType;
	}

	public void setItemType(int itemType)
	{
		this.itemType = itemType;
	}

	public int getIsBelowThreashold()
	{
		return isBelowThreashold;
	}

	public void setIsBelowThreashold(int isBelowThreashold)
	{
		this.isBelowThreashold = isBelowThreashold;
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

	public String getStockUomName()
	{
		return stockUomName;
	}

	public String getSellableUomName()
	{
		return sellableUomName;
	}

	public void setStockUomName(String stockUomName)
	{
		this.stockUomName = stockUomName;
	}

	public void setSellableUomName(String sellableUomName)
	{
		this.sellableUomName = sellableUomName;
	}

	public String getItemTypeName()
	{
		return itemTypeName;
	}

	public void setItemTypeName(String itemTypeName)
	{
		this.itemTypeName = itemTypeName;
	}

	public int getIsRealTimeUpdateNeeded()
	{
		return isRealTimeUpdateNeeded;
	}

	public void setIsRealTimeUpdateNeeded(int isRealTimeUpdateNeeded)
	{
		this.isRealTimeUpdateNeeded = isRealTimeUpdateNeeded;
	}

	public int getInventoryAccrual()
	{
		return inventoryAccrual;
	}

	public void setInventoryAccrual(int inventoryAccrual)
	{
		this.inventoryAccrual = inventoryAccrual;
	}

	public int getIsinventoryAccrualOverriden()
	{
		return isinventoryAccrualOverriden;
	}

	public void setIsinventoryAccrualOverriden(int isinventoryAccrualOverriden)
	{
		this.isinventoryAccrualOverriden = isinventoryAccrualOverriden;
	}

	public List<ItemsAttributeType> getItemsAttributeTypes()
	{
		return itemsAttributeTypes;
	}

	public void setItemsAttributeTypes(List<ItemsAttributeType> itemsAttributeTypes)
	{
		this.itemsAttributeTypes = itemsAttributeTypes;
	}

	public Set<ItemsAttributeType> getItemsAttributeTypesSet()
	{
		return itemsAttributeTypesSet;
	}

	public void setItemsAttributeTypesSet(Set<ItemsAttributeType> itemsAttributeTypesSet)
	{
		this.itemsAttributeTypesSet = itemsAttributeTypesSet;
	}

	public Set<ItemsChar> getItemsCharsSet()
	{
		return itemsCharsSet;
	}

	public void setItemsCharsSet(Set<ItemsChar> itemsCharsSet)
	{
		this.itemsCharsSet = itemsCharsSet;
	}

	public Set<ItemsAttribute> getItemsAttributesSet()
	{
		return itemsAttributesSet;
	}

	public void setItemsAttributesSet(Set<ItemsAttribute> itemsAttributesSet)
	{
		this.itemsAttributesSet = itemsAttributesSet;
	}

	public String getItemGroupId() {
		 if(itemGroupId != null && (itemGroupId.length()==0 || itemGroupId.equals("0")))
		 {
			 return null;
		 }else
		 {	return itemGroupId;
		 }
	}

	public void setItemGroupId(String itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	public int getIsOnlineItem() {
		return isOnlineItem;
	}

	public void setIsOnlineItem(int isOnlineItem) {
		this.isOnlineItem = isOnlineItem;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

 
 

	public ItemToSupplier getItemToSuppliers()
	{
		return itemToSuppliers;
	}

	public void setItemToSuppliers(ItemToSupplier itemToSuppliers)
	{
		this.itemToSuppliers = itemToSuppliers;
	}

	public BigDecimal getPurchasingRate() {
		return purchasingRate;
	}

	public void setPurchasingRate(BigDecimal purchasingRate) {
		this.purchasingRate = purchasingRate;
	}


	public BigDecimal getYieldPercent() {
		return yieldPercent;
	}

	public void setYieldPercent(BigDecimal yieldPercent) {
		this.yieldPercent = yieldPercent;
	}

	
 
	public Item copy(Item item) {
		Item newItem=new Item(); 
		newItem.setCategoryItems(item.getCategoryItems());
		newItem.setDescription(item.getDescription());
		newItem.setHexCodeValues(item.getHexCodeValues());
		newItem.setEffectiveEndDate(item.getEffectiveEndDate()); 
		newItem.setStartTime(item.getStartTime());
		newItem.setEndTime(item.getEndTime());
		newItem.setEffectiveStartDate(item.getEffectiveStartDate());
		newItem.setImageName(item.getImageName());
		newItem.setIsActive(item.getIsActive());
		newItem.setIsDeleted(item.getIsDeleted());
		newItem.setIsFeatured(item.getIsFeatured()); 
		newItem.setIsInStock(item.getIsInStock()); 
		newItem.setIsRatingAllowed(item.getIsRatingAllowed()); 
		newItem.setIsReviewAllowed(item.getIsReviewAllowed());
		newItem.setItemNumber(item.getItemNumber()); 
		newItem.setName(item.getName()); 
		newItem.setPriceMsrp(item.getPriceMsrp()); 
		newItem.setPriceSelling(item.getPriceSelling());
		newItem.setRatingTotal(item.getRatingTotal());
		newItem.setRatingVotesTotal(item.getRatingVotesTotal()); 
		newItem.setShortName(item.getShortName()); 
		newItem.setDisplayName(item.getDisplayName());
		newItem.setDisplaySequence(item.getDisplaySequence()); 
		newItem.setLocationsId(item.getLocationsId()); 
		newItem.setItemType(item.getItemType());
		newItem.setIsBelowThreashold(item.getIsBelowThreashold()); 
		newItem.setIsRealTimeUpdateNeeded(item.getIsRealTimeUpdateNeeded()); 
		newItem.setItemGroupId(item.getItemGroupId());
		newItem.setIsOnlineItem(item.getIsOnlineItem());
		newItem.setStockUomName(item.getStockUomName()); 
		newItem.setSellableUomName(item.getSellableUomName()); 
		newItem.setItemTypeName(item.getItemTypeName());
		newItem.setItemsAttributeTypes(item.getItemsAttributeTypes());
		newItem.setItemsChars(item.getItemsChars());
		newItem.setItemsAttributes(item.getItemsAttributes()); 
		newItem.setItemsAttributeTypesSet(item.getItemsAttributeTypesSet()); 
		newItem.setItemsCharsSet(item.getItemsCharsSet()); 
		newItem.setItemsAttributesSet(item.getItemsAttributesSet()); 
		newItem.setItemsToPrinters(item.getItemsToPrinters()); 
		newItem.setItemsToDiscounts(item.getItemsToDiscounts()); 
		newItem.setItemsToItemsChars(item.getItemsToItemsChars());
		newItem.setItemsToItemsAttributes(item.getItemsToItemsAttributes());
		newItem.setItemsToItemsAttributesAttributeTypes(item.getItemsToItemsAttributesAttributeTypes());
		newItem.setCategoryItems(item.getCategoryItems());
		newItem.setItemToSuppliers(item.getItemToSuppliers());
		newItem.setSalesTax1(item.getSalesTax1());
		newItem.setSalesTax2(item.getSalesTax2());
		newItem.setSalesTax3(item.getSalesTax3());
		newItem.setSalesTax4(item.getSalesTax4());
		newItem.setStockUom(item.getStockUom());
		newItem.setSellableUom(item.getSellableUom());
		newItem.setInventoryAccrual(item.getInventoryAccrual());
		newItem.setIsinventoryAccrualOverriden(item.getIsinventoryAccrualOverriden());
		newItem.setGlobalItemId(item.getGlobalItemId());
		newItem.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		newItem.setUpdatedBy(item.getUpdatedBy());
		newItem.setCreated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		newItem.setCreatedBy(item.getCreatedBy());
		newItem.setStatus(item.getStatus());
		newItem.setCourseId(item.getCourseId());
		newItem.setPurchasingRate(item.getPurchasingRate());
		newItem.setPlu(item.getPlu());
		newItem.setIsScanRequired(item.getIsScanRequired());
		newItem.setIsManualQuantity(item.getIsManualQuantity());
		newItem.setIsManualPrice(item.getIsManualPrice());
		newItem.setDistributionPrice(item.getDistributionPrice());
		
		newItem.setIsPrinterUpdate(item.getIsPrinterUpdate());
		newItem.setIsDiscountUpdate(item.getIsDiscountUpdate());
		newItem.setPriceInclusiveTax(item.getPriceInclusiveTax());
		newItem.setYieldPercent(item.getYieldPercent());
		
		newItem.setIsCategoryItemUpdate(item.getIsCategoryItemUpdate());
		
		newItem.setIsAttributeListUpdate(item.getIsAttributeListUpdate());
		newItem.setIsAttributeTypeListUpdate(item.getIsAttributeTypeListUpdate());
		newItem.setIsItemToCharUpdate(item.getIsItemToCharUpdate());
		newItem.setIsOnlineDisplay(item.getIsOnlineDisplay());
		newItem.setLabelIngredients(item.getLabelIngredients());
		newItem.setContains(item.getContains());
		newItem.setStorageTypeId(item.getStorageTypeId());
		newItem.setAvailability(item.isAvailability());
		newItem.setCutOffTime(item.getCutOffTime());
		newItem.setLeadTime(item.getLeadTime());
		newItem.setIsWeighingScale(item.getIsWeighingScale());
		newItem.setIncentive(item.getIncentive());
		newItem.setIncentiveId(item.getIncentiveId());
		return newItem;
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

	public int getIsWeighingScale() {
		return isWeighingScale;
	}

	public void setIsWeighingScale(int isWeighingScale) {
		this.isWeighingScale = isWeighingScale;
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
	
	public int getIsScanRequired()
	{
		return isScanRequired;
	}

	public void setIsScanRequired(int isScanRequired)
	{
		this.isScanRequired = isScanRequired;
	}

	public int getIsManualQuantity()
	{
		return isManualQuantity;
	}

	public void setIsManualQuantity(int isManualQuantity)
	{
		this.isManualQuantity = isManualQuantity;
	}

	public int getIsManualPrice()
	{
		return isManualPrice;
	}

	public void setIsManualPrice(int isManualPrice)
	{
		this.isManualPrice = isManualPrice;
	}

	public int getIsPrinterUpdate()
	{
		return isPrinterUpdate;
	}

	public void setIsPrinterUpdate(int isPrinterUpdate)
	{
		this.isPrinterUpdate = isPrinterUpdate;
	}
	public int getIsDiscountUpdate()
	{
		return isDiscountUpdate;
	}

	public void setIsDiscountUpdate(int isDiscountUpdate)
	{
		this.isDiscountUpdate = isDiscountUpdate;
	}

	public int getIsCategoryItemUpdate()
	{
		return isCategoryItemUpdate;
	}

	public void setIsCategoryItemUpdate(int isCategoryItemUpdate)
	{
		this.isCategoryItemUpdate = isCategoryItemUpdate;
	}

	public int getIsAttributeListUpdate()
	{
		return isAttributeListUpdate;
	}

	public void setIsAttributeListUpdate(int isAttributeListUpdate)
	{
		this.isAttributeListUpdate = isAttributeListUpdate;
	}

	public int getIsAttributeTypeListUpdate()
	{
		return isAttributeTypeListUpdate;
	}

	public void setIsAttributeTypeListUpdate(int isAttributeTypeListUpdate)
	{
		this.isAttributeTypeListUpdate = isAttributeTypeListUpdate;
	}

	public int getIsItemToCharUpdate()
	{
		return isItemToCharUpdate;
	}

	public void setIsItemToCharUpdate(int isItemToCharUpdate)
	{
		this.isItemToCharUpdate = isItemToCharUpdate;
	}

 
	
	public int getIsDeleted()
	{
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted)
	{
		this.isDeleted = isDeleted;
	}

	public List<ItemsToNutritions> getItemsToNutritions() {
		return itemsToNutritions;
	}

	public void setItemsToNutritions(List<ItemsToNutritions> itemsToNutritions) {
		this.itemsToNutritions = itemsToNutritions;
	}

	public int getIsItemsToNutritionUpdate() {
		return isItemsToNutritionUpdate;
	}

	public void setIsItemsToNutritionUpdate(int isItemsToNutritionUpdate) {
		this.isItemsToNutritionUpdate = isItemsToNutritionUpdate;
	}

	public int getIsItemsToScheduleUpdate() {
		return isItemsToScheduleUpdate;
	}

	public void setIsItemsToScheduleUpdate(int isItemsToScheduleUpdate) {
		this.isItemsToScheduleUpdate = isItemsToScheduleUpdate;
	}

	public String getLabelIngredients() {
		return labelIngredients;
	}

	public void setLabelIngredients(String labelIngredients) {
		this.labelIngredients = labelIngredients;
	}

	public String getStorageTypeId() {
		return storageTypeId;
	}

	public void setStorageTypeId(String storageTypeId) {
		this.storageTypeId = storageTypeId;
	}

	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	public String getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}

	public String getCutOffTime() {
		return cutOffTime;
	}

	public void setCutOffTime(String cutOffTime) {
		this.cutOffTime = cutOffTime;
	}

	public List<ItemToDate> getItemToDate() {
		return itemToDate;
	}

	public void setItemToDate(List<ItemToDate> itemToDate) {
		this.itemToDate = itemToDate;
	}

 

	public boolean isAvailability() {
		return availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}

	@Override
	public String toString() {
		return "Item [courseId=" + courseId + ", description=" + description + ", hexCodeValues=" + hexCodeValues
				+ ", effectiveEndDate=" + effectiveEndDate + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", effectiveStartDate=" + effectiveStartDate + ", imageName=" + imageName + ", isActive=" + isActive
				+ ", isDeleted=" + isDeleted + ", isFeatured=" + isFeatured + ", isInStock=" + isInStock
				+ ", isRatingAllowed=" + isRatingAllowed + ", isReviewAllowed=" + isReviewAllowed + ", itemNumber="
				+ itemNumber + ", name=" + name + ", priceMsrp=" + priceMsrp + ", priceSelling=" + priceSelling
				+ ", ratingTotal=" + ratingTotal + ", ratingVotesTotal=" + ratingVotesTotal + ", shortName=" + shortName
				+ ", displayName=" + displayName + ", displaySequence=" + displaySequence + ", locationsId="
				+ locationsId + ", itemType=" + itemType + ", isBelowThreashold=" + isBelowThreashold
				+ ", isRealTimeUpdateNeeded=" + isRealTimeUpdateNeeded + ", itemGroupId=" + itemGroupId
				+ ", isOnlineItem=" + isOnlineItem + ", sellableUom=" + sellableUom + ", purchasingRate="
				+ purchasingRate + ", plu=" + plu + ", isScanRequired=" + isScanRequired + ", isManualQuantity="
				+ isManualQuantity + ", isManualPrice=" + isManualPrice + ", priceInclusiveTax=" + priceInclusiveTax
				+ ", yieldPercent=" + yieldPercent + ", isOnlineDisplay=" + isOnlineDisplay + ", labelIngredients="
				+ labelIngredients + ", storageTypeId=" + storageTypeId + ", contains=" + contains + ", leadTime="
				+ leadTime + ", cutOffTime=" + cutOffTime + ", incentive=" + incentive + ", incentiveId=" + incentiveId
				+ ", availability=" + availability + ", salesTax1=" + salesTax1 + ", salesTax2=" + salesTax2
				+ ", salesTax3=" + salesTax3 + ", salesTax4=" + salesTax4 + ", stockUom=" + stockUom
				+ ", inventoryAccrual=" + inventoryAccrual + ", isinventoryAccrualOverriden="
				+ isinventoryAccrualOverriden + ", globalItemId=" + globalItemId + ", distributionPrice="
				+ distributionPrice + ", isWeighingScale=" + isWeighingScale + "]";
	}
}