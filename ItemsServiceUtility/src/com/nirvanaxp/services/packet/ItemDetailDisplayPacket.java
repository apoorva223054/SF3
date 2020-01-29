package com.nirvanaxp.services.packet;

import java.math.BigDecimal;
import java.util.List;

import com.nirvanaxp.types.entities.locations.Location;

public class ItemDetailDisplayPacket {

	
	private String id;
	private String name;
//	private String shortName;
	private BigDecimal price;
	private String courseName;
	private String imageName;
	private String itemToPrinter;
	private String itemToPrinterName;
	private String itemDisplayName;
	private String categoryName;
	private String stockUom;
	private String itemTypeName;
	private List<Location> locationList;
	private String uomName;
	BigDecimal availableQty;
	private String sellableUomId;
	
	
	private String primarySupplierName;
	private String sellableUomName;
	private String stockUomName;
	

	private String taxName1;
	private String taxName2;
	private String taxName3;
	private String taxName4;
	private String taxDisplayName1;
	private String taxDisplayName2;
	private String taxDisplayName3;
	private String taxDisplayName4;
	private BigDecimal taxRate1;
	private BigDecimal taxRate2;
	private BigDecimal taxRate3;
	private BigDecimal taxRate4;
	private BigDecimal purchasingRate;
	private BigDecimal distributionPrice;
	

	public String getTaxName1() {
		return taxName1;
	}
	public void setTaxName1(String taxName1) {
		this.taxName1 = taxName1;
	}
	public String getTaxName2() {
		return taxName2;
	}
	public void setTaxName2(String taxName2) {
		this.taxName2 = taxName2;
	}
	public String getTaxName3() {
		return taxName3;
	}
	public void setTaxName3(String taxName3) {
		this.taxName3 = taxName3;
	}
	public String getTaxName4() {
		return taxName4;
	}
	public void setTaxName4(String taxName4) {
		this.taxName4 = taxName4;
	}
	public String getTaxDisplayName1() {
		return taxDisplayName1;
	}
	public void setTaxDisplayName1(String taxDisplayName1) {
		this.taxDisplayName1 = taxDisplayName1;
	}
	public String getTaxDisplayName2() {
		return taxDisplayName2;
	}
	public void setTaxDisplayName2(String taxDisplayName2) {
		this.taxDisplayName2 = taxDisplayName2;
	}
	public String getTaxDisplayName3() {
		return taxDisplayName3;
	}
	public void setTaxDisplayName3(String taxDisplayName3) {
		this.taxDisplayName3 = taxDisplayName3;
	}
	public String getTaxDisplayName4() {
		return taxDisplayName4;
	}
	public void setTaxDisplayName4(String taxDisplayName4) {
		this.taxDisplayName4 = taxDisplayName4;
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
	public BigDecimal getPurchasingRate() {
		return purchasingRate;
	}
	public void setPurchasingRate(BigDecimal purchasingRate) {
		this.purchasingRate = purchasingRate;
	}
	public BigDecimal getDistributionPrice() {
		return distributionPrice;
	}
	public void setDistributionPrice(BigDecimal distributionPrice) {
		this.distributionPrice = distributionPrice;
	}
	public BigDecimal getAvailableQty() {
		return availableQty;
	}
	public void setAvailableQty(BigDecimal availableQty) {
		this.availableQty = availableQty;
	}
	public String getUomName() {
		return uomName;
	}
	public void setUomName(String uomName) {
		this.uomName = uomName;
	}
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
		return categoryName;
	}
	public void setShortName(String shortName) {
		this.categoryName = shortName;
	}

	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getItemToPrinter() {
		return itemToPrinter;
	}
	public void setItemToPrinter(String itemToPrinter) {
		this.itemToPrinter = itemToPrinter;
	}
	public String getItemToPrinterName() {
		return itemToPrinterName;
	}
	public void setItemToPrinterName(String itemToPrinterName) {
		this.itemToPrinterName = itemToPrinterName;
	}
	public String getItemDisplayName() {
		return itemDisplayName;
	}
	public void setItemDisplayName(String itemDisplayName) {
		this.itemDisplayName = itemDisplayName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getItemTypeName() {
		return itemTypeName;
	}
	public void setItemTypeName(String itemTypeName) {
		this.itemTypeName = itemTypeName;
	}
	public String getStockUom() {
		 if(stockUom != null && (stockUom.length()==0 || stockUom.equals("0"))){return null;}else{	return stockUom;}
	}
	public void setStockUom(String stockUom) {
		this.stockUom = stockUom;
	}
	public List<Location> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
	}
	
	public String getPrimarySupplierName() {
		return primarySupplierName;
	}
	public void setPrimarySupplierName(String primarySupplierName) {
		this.primarySupplierName = primarySupplierName;
	}
	public String getSellableUomName() {
		return sellableUomName;
	}
	public void setSellableUomName(String sellableUomName) {
		this.sellableUomName = sellableUomName;
	}
	public String getStockUomName() {
		return stockUomName;
	}
	public void setStockUomName(String stockUomName) {
		this.stockUomName = stockUomName;
	}
	
	public String getSellableUomId() {
		return sellableUomId;
	}
	public void setSellableUomId(String sellableUomId) {
		this.sellableUomId = sellableUomId;
	}
	

}
