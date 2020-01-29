/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.category.Category;
import com.nirvanaxp.types.entities.catalog.items.Item;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeType;
import com.nirvanaxp.types.entities.catalog.items.ItemsAttributeTypeToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsChar;
import com.nirvanaxp.types.entities.catalog.items.ItemsCharToItemsAttribute;
import com.nirvanaxp.types.entities.catalog.items.ItemsToLocation;
import com.nirvanaxp.types.entities.catalog.items.ItemsToSchedule;
import com.nirvanaxp.types.entities.catalog.items.Nutritions;
import com.nirvanaxp.types.entities.discounts.Discount;
import com.nirvanaxp.types.entities.orders.ItemToSupplier;
import com.nirvanaxp.types.entities.printers.Printer;

@XmlRootElement(name = "ItemPacket")
public class ItemPacket extends PostPacket
{

	Item item;

	private List<ItemsAttributeType> itemsAttributeTypesList;
	
	private List<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttributeList;

	private List<ItemsCharToItemsAttribute> itemsCharToItemsAttribute;
    private String locationsListId;
	
	public String getLocationsListId() {
		return locationsListId;
	}

	public void setLocationsListId(String locationsListId) {
		this.locationsListId = locationsListId;
	}

	private List<ItemsChar> itemCharsList;

	private List<ItemsAttribute> itemsAttributesList;

	private List<Category> categoryList;

	private List<Printer> printerList;

	private List<Discount> discountsList;

	private ItemToSupplier itemToSupplier;
	
	private List<ItemsToLocation> itemsToLocations;

	private int isBaseLocationUpdate;
	
	private int isRawMaterialUpdate;
	private int businessId;
	private String date;
	
	 
	
	private List<Nutritions> nutritionList;

	private List<ItemsToSchedule> itemsToSchedule;
	public List<ItemsToSchedule> getItemsToSchedule() {
		return itemsToSchedule;
	}

	public void setItemsToSchedule(List<ItemsToSchedule> itemsToSchedule) {
		this.itemsToSchedule = itemsToSchedule;
	}
	
	
	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}

	public List<Discount> getDiscountsList()
	{
		return discountsList;
	}

	public void setDiscountsList(List<Discount> discountsList)
	{
		this.discountsList = discountsList;
	}

	public List<ItemsAttributeType> getItemsAttributeTypesList()
	{
		return itemsAttributeTypesList;
	}

	public void setItemsAttributeTypesList(List<ItemsAttributeType> itemsAttributeTypesList)
	{
		this.itemsAttributeTypesList = itemsAttributeTypesList;
	}

	public List<ItemsChar> getItemCharsList()
	{
		return itemCharsList;
	}

	public void setItemCharsList(List<ItemsChar> itemCharsList)
	{
		this.itemCharsList = itemCharsList;
	}

	public List<ItemsAttribute> getItemsAttributesList()
	{
		return itemsAttributesList;
	}

	public void setItemsAttributesList(List<ItemsAttribute> itemsAttributesList)
	{
		this.itemsAttributesList = itemsAttributesList;
	}

	public List<Category> getCategoryList()
	{
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList)
	{
		this.categoryList = categoryList;
	}

	@Override
	public String toString() {
		return "ItemPacket [item=" + item + ", itemsAttributeTypesList=" + itemsAttributeTypesList
				+ ", itemsAttributeTypeToItemsAttributeList=" + itemsAttributeTypeToItemsAttributeList
				+ ", itemsCharToItemsAttribute=" + itemsCharToItemsAttribute + ", itemCharsList=" + itemCharsList
				+ ", itemsAttributesList=" + itemsAttributesList + ", categoryList=" + categoryList + ", printerList="
				+ printerList + ", discountsList=" + discountsList + ", itemToSupplier=" + itemToSupplier
				+ ", itemsToLocations=" + itemsToLocations + ", isBaseLocationUpdate=" + isBaseLocationUpdate
				+ ", isRawMaterialUpdate=" + isRawMaterialUpdate + ", businessId=" + businessId + ", date=" + date
				+ ", nutritionList=" + nutritionList + "]";
	}

	public List<Printer> getPrinterList()
	{
		return printerList;
	}

	public void setPrinterList(List<Printer> printerList)
	{
		this.printerList = printerList;
	}

	public void ListItem(Item item)
	{
		this.item = item;
	}

	

	 
	 
	public ItemToSupplier getItemToSupplier()
	{
		return itemToSupplier;
	}

	public void setItemToSupplier(ItemToSupplier itemToSupplier)
	{
		this.itemToSupplier = itemToSupplier;
	}

	public int getIsBaseLocationUpdate()
	{
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate)
	{
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	

	public List<ItemsAttributeTypeToItemsAttribute> getItemsAttributeTypeToItemsAttributeList() {
		return itemsAttributeTypeToItemsAttributeList;
	}

	public void setItemsAttributeTypeToItemsAttributeList(
			List<ItemsAttributeTypeToItemsAttribute> itemsAttributeTypeToItemsAttributeList) {
		this.itemsAttributeTypeToItemsAttributeList = itemsAttributeTypeToItemsAttributeList;
	}


	public int getIsRawMaterialUpdate()
	{
		return isRawMaterialUpdate;
	}

	public void setIsRawMaterialUpdate(int isRawMaterialUpdate)
	{
		this.isRawMaterialUpdate = isRawMaterialUpdate;
	}

	public List<ItemsCharToItemsAttribute> getItemsCharToItemsAttribute()
	{
		return itemsCharToItemsAttribute;
	}

	public void setItemsCharToItemsAttribute(List<ItemsCharToItemsAttribute> itemsCharToItemsAttribute)
	{
		this.itemsCharToItemsAttribute = itemsCharToItemsAttribute;
	}
	
	public List<ItemsToLocation> getItemsToLocations() {
		return itemsToLocations;
	}

	public void setItemsToLocations(List<ItemsToLocation> itemsToLocations) {
		this.itemsToLocations = itemsToLocations;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}
	
	public int getBusinessId()
	{
		return businessId;
	}

	public void setBusinessId(int businessId)
	{
		this.businessId = businessId;
	}
	

	public List<Nutritions> getNutritionList() {
		return nutritionList;
	}

	public void setNutritionList(List<Nutritions> nutritionList) {
		this.nutritionList = nutritionList;
	}

	
	public ItemPacket getItemPacket(ItemPacket itemPacket)
	{
		ItemPacket iPacket = new ItemPacket();
		iPacket.setCategoryList(itemPacket.getCategoryList());
		iPacket.setDiscountsList(itemPacket.getDiscountsList());
		iPacket.setItem(itemPacket.getItem());
		iPacket.setItemCharsList(itemPacket.getItemCharsList());
		iPacket.setItemsAttributesList(itemPacket.getItemsAttributesList());
		iPacket.setItemToSupplier(itemPacket.getItemToSupplier());
		iPacket.setItemsAttributeTypesList(itemPacket.getItemsAttributeTypesList());
		iPacket.setPrinterList(itemPacket.getPrinterList());
		iPacket.setIsBaseLocationUpdate(itemPacket.getIsBaseLocationUpdate());
		iPacket.setIsRawMaterialUpdate(itemPacket.getIsRawMaterialUpdate());
		iPacket.setItemsAttributeTypeToItemsAttributeList(itemPacket.getItemsAttributeTypeToItemsAttributeList());
		iPacket.setItemsCharToItemsAttribute(itemPacket.getItemsCharToItemsAttribute());
		iPacket.setItemsToLocations(itemPacket.getItemsToLocations());
		iPacket.setNutritionList(itemPacket.getNutritionList());
		
		return iPacket;

	}
}
