package com.nirvanaxp.services.packet;

import java.math.BigDecimal;

public class ItemToSupplierPacket {
//	item name, item id, available qty(inventory), primary, sec, ter, id and name

	String itemName;
	String itemId;
	BigDecimal availableQty;
	String uomName;
	String primarySupplierName;
	String secondarySupplierName;
	String tertiarySupplierName;
	String primarySupplierId;
	String secondarySupplierId;
	String tertiarySupplierId;
	BigDecimal unitPrice;
	String categoryName;
	
	BigDecimal productionQty;
	BigDecimal requestedFromAllLocationproductionQty;
	
	
	
	
	BigDecimal unitPurchasedPrice;
	BigDecimal unitTaxRate;
	
	BigDecimal currentAvailableQty;
	

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
	
	public BigDecimal getCurrentAvailableQty()
	{
		return currentAvailableQty;
	}
	public void setCurrentAvailableQty(BigDecimal currentAvailableQty)
	{
		this.currentAvailableQty = currentAvailableQty;
	}
	BigDecimal distributionPrice;
	
	public BigDecimal getDistributionPrice()
	{
		return distributionPrice;
	}
	public void setDistributionPrice(BigDecimal distributionPrice)
	{
		this.distributionPrice = distributionPrice;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemId() {
		if(itemId != null && (itemId.length()==0 || itemId.equals("0"))){return null;}else{	return itemId;}
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
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
	public String getPrimarySupplierName() {
		return primarySupplierName;
	}
	public void setPrimarySupplierName(String primarySupplierName) {
		this.primarySupplierName = primarySupplierName;
	}
	public String getSecondarySupplierName() {
		return secondarySupplierName;
	}
	public void setSecondarySupplierName(String secondarySupplierName) {
		this.secondarySupplierName = secondarySupplierName;
	}
	public String getTertiarySupplierName() {
		return tertiarySupplierName;
	}
	public void setTertiarySupplierName(String tertiarySupplierName) {
		this.tertiarySupplierName = tertiarySupplierName;
	}
	public String getPrimarySupplierId() {
		if(primarySupplierId != null && (primarySupplierId.length()==0 || primarySupplierId.equals("0"))){return null;}else{	return primarySupplierId;}
	}
	public void setPrimarySupplierId(String primarySupplierId) {
		this.primarySupplierId = primarySupplierId;
	}
	public String getSecondarySupplierId() {
		if(secondarySupplierId != null && (secondarySupplierId.length()==0 || secondarySupplierId.equals("0"))){return null;}else{	return secondarySupplierId;}
	}
	public void setSecondarySupplierId(String secondarySupplierId) {
		this.secondarySupplierId = secondarySupplierId;
	}
	public String getTertiarySupplierId() {
		if(tertiarySupplierId != null && (tertiarySupplierId.length()==0 || tertiarySupplierId.equals("0"))){return null;}else{	return tertiarySupplierId;}
	}
	public void setTertiarySupplierId(String tertiarySupplierId) {
		this.tertiarySupplierId = tertiarySupplierId;
	}
	
	

	
	@Override
	public String toString() {
		return "ItemToSupplierPacket [itemName=" + itemName + ", itemId="
				+ itemId + ", availableQty=" + availableQty + ", uomName="
				+ uomName + ", primarySupplierName=" + primarySupplierName
				+ ", secondarySupplierName=" + secondarySupplierName
				+ ", tertiarySupplierName=" + tertiarySupplierName
				+ ", primarySupplierId=" + primarySupplierId
				+ ", secondarySupplierId=" + secondarySupplierId
				+ ", tertiarySupplierId=" + tertiarySupplierId + ", unitPrice="
				+ unitPrice + ", categoryName=" + categoryName + ", taxName1="
				+ taxName1 + ", taxName2=" + taxName2 + ", taxName3="
				+ taxName3 + ", taxName4=" + taxName4 + ", taxDisplayName1="
				+ taxDisplayName1 + ", taxDisplayName2=" + taxDisplayName2
				+ ", taxDisplayName3=" + taxDisplayName3 + ", taxDisplayName4="
				+ taxDisplayName4 + ", taxRate1=" + taxRate1 + ", taxRate2="
				+ taxRate2 + ", taxRate3=" + taxRate3 + ", taxRate4="
				+ taxRate4 + ", productionQty=" + productionQty
				+ ", requestedFromAllLocationproductionQty="
				+ requestedFromAllLocationproductionQty
				+ ", unitPurchasedPrice=" + unitPurchasedPrice
				+ ", unitTaxRate=" + unitTaxRate + ", currentAvailableQty="
				+ currentAvailableQty + ", distributionPrice="
				+ distributionPrice + "]";
	}
	public BigDecimal getUnitPrice()
	{
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice)
	{
		this.unitPrice = unitPrice;
	}
	public BigDecimal getUnitPurchasedPrice()
	{
		return unitPurchasedPrice;
	}
	public void setUnitPurchasedPrice(BigDecimal unitPurchasedPrice)
	{
		this.unitPurchasedPrice = unitPurchasedPrice;
	}
	public BigDecimal getUnitTaxRate()
	{
		return unitTaxRate;
	}
	public void setUnitTaxRate(BigDecimal unitTaxRate)
	{
		this.unitTaxRate = unitTaxRate;
	}
	
	public String getCategoryName()
	{
		return categoryName;
	}
	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}
	public BigDecimal getProductionQty()
	{
		return productionQty;
	}
	public void setProductionQty(BigDecimal productionQty)
	{
		this.productionQty = productionQty;
	}
	public BigDecimal getRequestedFromAllLocationproductionQty()
	{
		return requestedFromAllLocationproductionQty;
	}
	public void setRequestedFromAllLocationproductionQty(BigDecimal requestedFromAllLocationproductionQty)
	{
		this.requestedFromAllLocationproductionQty = requestedFromAllLocationproductionQty;
	}
	
	
	
}