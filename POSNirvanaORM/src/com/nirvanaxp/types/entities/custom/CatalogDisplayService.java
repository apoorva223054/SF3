/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.custom;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class CatalogDisplayService implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5863692610882854157L;
	@Id
	@Column(name = "category_id")
	private String id;
	@Column(name = "category_name")
	private String categorylist;
	@Column(name = "item_id")
	private String itemsId;
	@Column(name = "item_name")
	private String itemsName;
	@Column(name = "image_name")
	private String imageName;
	@Column(name = "")
	private String discription;
	@Column(name = "short_name")
	private String shortName;
	@Column(name = "course_name")
	private String courseName;
	@Column(name = "printer_name")
	private String printersName;
	@Column(name = "")
	private float priceMrp;
	@Column(name = "price_selling")
	private float priceSelling;
	@Column(name = "category_to_printer_name")
	private String categoryPrintersName;
	@Column(name = "category_to_discounts_name")
	private String categoryDiscountsName;
	@Column(name = "discount_id")
	private String discountsId;
	@Column(name = "category_to_discount")
	private String categoryDiscountsId;
	@Column(name = "printer_id")
	private String printersId;
	@Column(name = "category_to_printer")
	private String categoryPrintersId;
	@Column(name = "item_type")
	private int itemType;
	@Column(name = "stock_uom")
	private String stockUom;
	@Column(name = "sellable_uom")
	private String sellableUom;
	@Column(name = "description")
	private String description;
	@Column(name = "itemCharId")
	private String itemCharId;
	@Column(name = "itemCharHexCode")
	private String itemCharHexCode;
	@Column(name = "itemCharImageUrl")
	private String itemCharImageUrl;
	@Column(name = "realtime_update")
	private Integer realTimeUpdate;
	@Column(name = "attributeTypeCount")
	private Integer attributeTypeCount;
	
	@Column(name = "start_time")
	private String startTime;

	
	@Column(name = "end_time")
	private String endTime;
	private String courseId;
	
	@Column(name = "availability")
	private boolean availability;
	@Column(name = "status")
	private String status;
	

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public Integer getRealTimeUpdate()
	{
		return realTimeUpdate;
	}

	public void setRealTimeUpdate(Integer realTimeUpdate)
	{
		this.realTimeUpdate = realTimeUpdate;
	}

	public CatalogDisplayService()
	{

	}

	public CatalogDisplayService(String itemsId)
	{
		super();
		this.itemsId = itemsId;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getCategorylist()
	{
		return categorylist;
	}

	public void setCategorylist(String categorylist)
	{
		this.categorylist = categorylist;
	}

	public String getItemsId()
	{
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}

	public void setItemsId(String itemsId)
	{
		this.itemsId = itemsId;
	}

	public String getItemsName()
	{
		return itemsName;
	}

	public void setItemsName(String itemsName)
	{
		this.itemsName = itemsName;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getDiscription()
	{
		return discription;
	}

	public void setDiscription(String discription)
	{
		this.discription = discription;
	}

	public float getPriceMrp()
	{
		return priceMrp;
	}

	public void setPriceMrp(float priceMrp)
	{
		this.priceMrp = priceMrp;
	}

	public float getPriceSelling()
	{
		return priceSelling;
	}

	public void setPriceSelling(float priceSelling)
	{
		this.priceSelling = priceSelling;
	}

	public String getShortName()
	{
		return shortName;
	}

	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}

	public String getCourseName()
	{
		return courseName;
	}

	public void setCourseName(String courseName)
	{
		this.courseName = courseName;
	}

	public String getPrintersName()
	{
		return printersName;
	}

	public void setPrintersName(String printersName)
	{
		this.printersName = printersName;
	}

	public String getCategoryPrintersName()
	{
		return categoryPrintersName;
	}

	public void setCategoryPrintersName(String categoryPrintersName)
	{
		this.categoryPrintersName = categoryPrintersName;
	}

	public String getCategoryDiscountsName()
	{
		return categoryDiscountsName;
	}

	public void setCategoryDiscountsName(String categoryDiscountsName)
	{
		this.categoryDiscountsName = categoryDiscountsName;
	}

	public String getDiscountsId()
	{
		 if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
	}

	public void setDiscountsId(String discountsId)
	{
		this.discountsId = discountsId;
	}

	public String getCategoryDiscountsId()
	{
		return categoryDiscountsId;
	}

	public void setCategoryDiscountsId(String categoryDiscountsId)
	{
		this.categoryDiscountsId = categoryDiscountsId;
	}

	public String getPrintersId()
	{
		 if(printersId != null && (printersId.length()==0 || printersId.equals("0"))){return null;}else{	return printersId;}
	}

	public void setPrintersId(String printersId)
	{
		this.printersId = printersId;
	}

	public String getCategoryPrintersId()
	{
		return categoryPrintersId;
	}

	public void setCategoryPrintersId(String categoryPrintersId)
	{
		this.categoryPrintersId = categoryPrintersId;
	}

	public int getItemType()
	{
		return itemType;
	}

	public String getStockUom()
	{
		 if(stockUom != null && (stockUom.length()==0 || stockUom.equals("0"))){return null;}else{	return stockUom;}
	}

	public String getSellableUom()
	{
		 if(sellableUom != null && (sellableUom.length()==0 || sellableUom.equals("0"))){return null;}else{	return sellableUom;}
	}

	public void setItemType(int itemType)
	{
		this.itemType = itemType;
	}

	public void setStockUom(String stockUom)
	{
		this.stockUom = stockUom;
	}

	public void setSellableUom(String sellableUom)
	{
		this.sellableUom = sellableUom;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CatalogDisplayService)
		{
			CatalogDisplayService catalogDisplayService = (CatalogDisplayService) obj;
			if (this.itemsId.equals(catalogDisplayService.getItemsId()))
			{
				return true;
			}
		}
		return false;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getItemCharId()
	{
		return itemCharId;
	}

	public void setItemCharId(String itemCharId)
	{
		this.itemCharId = itemCharId;
	}

	public String getItemCharHexCode()
	{
		return itemCharHexCode;
	}

	public void setItemCharHexCode(String itemCharHexCode)
	{
		this.itemCharHexCode = itemCharHexCode;
	}

	public String getItemCharImageUrl()
	{
		return itemCharImageUrl;
	}

	public void setItemCharImageUrl(String itemCharImageUrl)
	{
		this.itemCharImageUrl = itemCharImageUrl;
	}

	public Integer getAttributeTypeCount()
	{
		return attributeTypeCount;
	}

	public void setAttributeTypeCount(Integer attributeTypeCount)
	{
		this.attributeTypeCount = attributeTypeCount;
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

	public boolean isAvailability() {
		return availability;
	}

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CatalogDisplayService [id=" + id + ", categorylist=" + categorylist + ", itemsId=" + itemsId
				+ ", itemsName=" + itemsName + ", imageName=" + imageName + ", discription=" + discription
				+ ", shortName=" + shortName + ", courseName=" + courseName + ", printersName=" + printersName
				+ ", priceMrp=" + priceMrp + ", priceSelling=" + priceSelling + ", categoryPrintersName="
				+ categoryPrintersName + ", categoryDiscountsName=" + categoryDiscountsName + ", discountsId="
				+ discountsId + ", categoryDiscountsId=" + categoryDiscountsId + ", printersId=" + printersId
				+ ", categoryPrintersId=" + categoryPrintersId + ", itemType=" + itemType + ", stockUom=" + stockUom
				+ ", sellableUom=" + sellableUom + ", description=" + description + ", itemCharId=" + itemCharId
				+ ", itemCharHexCode=" + itemCharHexCode + ", itemCharImageUrl=" + itemCharImageUrl
				+ ", realTimeUpdate=" + realTimeUpdate + ", attributeTypeCount=" + attributeTypeCount + ", startTime="
				+ startTime + ", endTime=" + endTime + ", courseId=" + courseId + ", availability=" + availability
				+ ", status=" + status + "]";
	}
	
	
}
