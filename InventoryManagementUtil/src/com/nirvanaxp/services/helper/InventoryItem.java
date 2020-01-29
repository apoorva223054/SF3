package com.nirvanaxp.services.helper;

public class InventoryItem {

	private String id; 
	private String name; 
	private String shortName;
	private String stockUom;
	private String sellableUom;
	private String sellableName;
	private String stockName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getStockUom() {
		 if(stockUom != null && (stockUom.length()==0 || stockUom.equals("0"))){return null;}else{	return stockUom;}
	}
	public void setStockUom(String stockUom) {
		this.stockUom = stockUom;
	}
	public String getSellableUom() {
		 if(sellableUom != null && (sellableUom.length()==0 || sellableUom.equals("0"))){return null;}else{	return sellableUom;}
	}
	public void setSellableUom(String sellableUom) {
		this.sellableUom = sellableUom;
	}
	public String getSellableName() {
		return sellableName;
	}
	public void setSellableName(String sellableName) {
		this.sellableName = sellableName;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	
	

}
