/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.category;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.locations.Location;

/**
 * The persistent class for the category database table.
 * 
 */
@Entity
@Table(name = "category")
@XmlRootElement(name = "category")
public class Category extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@Lob
	private String description;

	@Column(name = "icon_colour", length = 8)
	private String iconColour;

	@Column(name = "is_active", nullable = false)
	private int isActive;

	@Column(name = "is_deleted", nullable = false)
	private int isDeleted;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "sort_sequence")
	private int sortSequence;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;
	@Column(name = "category_id")
	private String categoryId;

	@Column(name = "is_realtime_update_needed")
	private int isRealTimeUpdateNeeded;

	@Column(name = "is_update_overridden")
	private int isUpdateOverridden;

	@Column(name = "inventory_accrual")
	private int inventoryAccrual;

	@Column(name = "is_inventory_accrual_overriden")
	private int isinventoryAccrualOverriden;
	
	@Column(name = "is_online_category")
	private int isOnlineCategory;
	

	
	@Column(name = " global_category_id")
	private String globalCategoryId;
	
	@Column(name = " is_manual_qty")
	private int isManualQty;
	
	@Column(name = "item_group_id")
	private String itemGroupId;

	
	

	private transient List<CategoryToPrinter> categoryToPrinters;
	private transient List<CategoryToDiscount> categoryToDiscounts;
	private transient List<Location> locationList;
	private transient List<ItemToDate> categoryToSchedule;
	

	

	public Category()
	{
	}

	public Category(String id)
	{
		this.id = id;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getIconColour()
	{
		return this.iconColour;
	}

	public void setIconColour(String iconColour)
	{
		this.iconColour = iconColour;
	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public int getIsDeleted()
	{
		return this.isDeleted;
	}

	public void setIsDeleted(int isDeleted)
	{
		this.isDeleted = isDeleted;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getSortSequence()
	{
		return this.sortSequence;
	}

	public void setSortSequence(int sortSequence)
	{
		this.sortSequence = sortSequence;
	}

	public String getCategoryId()
	{
		 if(categoryId != null && (categoryId.length()==0 || categoryId.equals("0"))){return null;}else{	return categoryId;}
	}

	public void setCategoryId(String categoryId)
	{
		if(categoryId!=null && categoryId.length()==0){
			categoryId=null;
		}
		this.categoryId = categoryId;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public List<CategoryToPrinter> getCategoryToPrinters()
	{
		return categoryToPrinters;
	}

	public void setCategoryToPrinters(List<CategoryToPrinter> categoryToPrinters)
	{
		this.categoryToPrinters = categoryToPrinters;
	}

	public List<CategoryToDiscount> getCategoryToDiscounts()
	{
		return categoryToDiscounts;
	}

	public void setCategoryToDiscounts(List<CategoryToDiscount> categoryToDiscounts)
	{
		this.categoryToDiscounts = categoryToDiscounts;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Category)
		{
			if (this.id == ((Category) obj).getId())
			{
				return true;
			}
		}
		return false;
	}

	public int getIsRealTimeUpdateNeeded()
	{
		return isRealTimeUpdateNeeded;
	}

	public void setIsRealTimeUpdateNeeded(int isRealTimeUpdateNeeded)
	{
		this.isRealTimeUpdateNeeded = isRealTimeUpdateNeeded;
	}

	public int getIsUpdateOverridden()
	{
		return isUpdateOverridden;
	}

	public void setIsUpdateOverridden(int isUpdateOverridden)
	{
		this.isUpdateOverridden = isUpdateOverridden;
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
	public int getIsOnlineCategory() {
		return isOnlineCategory;
	}

	public void setIsOnlineCategory(int isOnlineCategory) {
		this.isOnlineCategory = isOnlineCategory;
	}


	


	public String getGlobalCategoryId() {
		 if(globalCategoryId != null && (globalCategoryId.length()==0 || globalCategoryId.equals("0"))){return null;}else{	return globalCategoryId;}
	}

	public void setGlobalCategoryId(String globalCategoryId) {
		this.globalCategoryId = globalCategoryId;
	}

	public Category getCategory(Category category){
		Category localCategory = new Category();
		localCategory.setCategoryId(category.getCategoryId());
		localCategory.setCategoryToDiscounts(category.getCategoryToDiscounts());
		localCategory.setCategoryToPrinters(category.getCategoryToPrinters());
		localCategory.setCreated(category.getCreated());
		localCategory.setCreatedBy(category.getCreatedBy());
		localCategory.setDescription(category.getDescription());
		localCategory.setDisplayName(category.getDisplayName());
		localCategory.setIconColour(category.getIconColour());
		localCategory.setImageName(category.getImageName());
		localCategory.setInventoryAccrual(category.getInventoryAccrual());
		localCategory.setIsActive(localCategory.getIsActive());
		localCategory.setIsDeleted(category.getIsDeleted());
		localCategory.setIsinventoryAccrualOverriden(category.getIsinventoryAccrualOverriden());
		localCategory.setIsOnlineCategory(category.getIsOnlineCategory());
		localCategory.setIsRealTimeUpdateNeeded(category.getIsRealTimeUpdateNeeded());
		localCategory.setIsUpdateOverridden(category.getIsUpdateOverridden());
		localCategory.setLocationsId(category.getLocationsId());
		localCategory.setName(category.getName());
		localCategory.setSortSequence(category.getSortSequence());
		localCategory.setStatus(category.getStatus());
		localCategory.setUpdated(category.getUpdated());
		localCategory.setUpdatedBy(category.getUpdatedBy());
		localCategory.setGlobalCategoryId(category.getGlobalCategoryId());
		localCategory.setIsManualQty(category.getIsManualQty());
		localCategory.setCategoryToSchedule(category.getCategoryToSchedule());
		return localCategory;
		
		
		
		
	}
	
	public String getItemGroupId()
	{
		 if(itemGroupId != null && (itemGroupId.length()==0 || itemGroupId.equals("0"))){return null;}else{	return itemGroupId;}
	}

	public void setItemGroupId(String itemGroupId)
	{
		this.itemGroupId = itemGroupId;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	
	public int getIsManualQty() {
		return isManualQty;
	}

	public void setIsManualQty(int isManualQty) {
		this.isManualQty = isManualQty;
	}

 

	public List<ItemToDate> getCategoryToSchedule() {
		return categoryToSchedule;
	}

	public void setCategoryToSchedule(List<ItemToDate> categoryToSchedule) {
		this.categoryToSchedule = categoryToSchedule;
	}

	@Override
	public String toString() {
		return "Category [description=" + description + ", iconColour=" + iconColour + ", isActive=" + isActive
				+ ", isDeleted=" + isDeleted + ", name=" + name + ", displayName=" + displayName + ", imageName="
				+ imageName + ", sortSequence=" + sortSequence + ", locationsId=" + locationsId + ", categoryId="
				+ categoryId + ", isRealTimeUpdateNeeded=" + isRealTimeUpdateNeeded + ", isUpdateOverridden="
				+ isUpdateOverridden + ", inventoryAccrual=" + inventoryAccrual + ", isinventoryAccrualOverriden="
				+ isinventoryAccrualOverriden + ", isOnlineCategory=" + isOnlineCategory + ", globalCategoryId="
				+ globalCategoryId + ", isManualQty=" + isManualQty + ", itemGroupId=" + itemGroupId + "]";
	}

	

}