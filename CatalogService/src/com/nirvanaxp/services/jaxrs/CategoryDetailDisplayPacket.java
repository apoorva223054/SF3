package com.nirvanaxp.services.jaxrs;


public class CategoryDetailDisplayPacket {

	private String categoryDisplayName;
	private String id;
	private String description;
	private String categoryToPrinters;
	private String categoryToDiscounts;
	private String categoryToPrintersName;
	private String categoryToDiscountsName;
	
	public String getCategoryDisplayName() {
		return categoryDisplayName;
	}
	public void setCategoryDisplayName(String categoryDisplayName) {
		this.categoryDisplayName = categoryDisplayName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategoryToPrinters() {
		return categoryToPrinters;
	}
	public void setCategoryToPrinters(String categoryToPrinters) {
		this.categoryToPrinters = categoryToPrinters;
	}
	public String getCategoryToDiscounts() {
		return categoryToDiscounts;
	}
	public void setCategoryToDiscounts(String categoryToDiscounts) {
		this.categoryToDiscounts = categoryToDiscounts;
	}
	public String getCategoryToPrintersName() {
		return categoryToPrintersName;
	}
	public void setCategoryToPrintersName(String categoryToPrintersName) {
		this.categoryToPrintersName = categoryToPrintersName;
	}
	public String getCategoryToDiscountsName() {
		return categoryToDiscountsName;
	}
	public void setCategoryToDiscountsName(String categoryToDiscountsName) {
		this.categoryToDiscountsName = categoryToDiscountsName;
	}
	

	
}
