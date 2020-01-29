/**

 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;
import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.application.Application;

/**
 * The persistent class for the locations database table.
 * 
 */
@Entity
@Table(name = "locations")
@XmlRootElement(name = "locations")
public class Location extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	@OneToOne(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "address_id")
	private Address address;

	@Column(name = "business_id")
	private int businessId;

	@Column(name = "email", length = 128)
	private String email;

	@Column(name = "functional_currency", length = 256)
	private String functionalCurrency;

	@Column(name = "image_url", length = 256)
	private String imageUrl;

	@Column(name = "max_point_of_service_num")
	private int maxPointOfServiceNum;

	
	private String name;

	@Column(name = "pos_x")
	private float posX;

	@Column(name = "pos_y")
	private float posY;

	@Column(name = "reporting_currency", length = 256)
	private String reportingCurrency;

	@Column(name = "sales_tax_1")
	private String salesTax1;

	@Column(name = "sales_tax_2")
	private String salesTax2;

	@Column(name = "sales_tax_3")
	private String salesTax3;

	@Column(name = "sales_tax_4")
	private String salesTax4;

	@Column(name = "timezone_id")
	private Integer timezoneId;

	@Column(name = "transactional_currency_id")
	private Integer transactionalCurrencyId;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "locations_type_id")
	private int locationsTypeId;

	@Column(length = 128)
	private String website;

	@Column(name = "number_of_people")
	private int numberofpeople;
	@Column(name = "gratuity")
	private float gratuity;

	@Column(name = "floor_timer", nullable = false)
	private int floorTimer;

	@Column(name = "preassigned_server_id")
	private String preassignedServerId;

	@Column(name = "business_type_id")
	private int businessTypeId;

	@Column(name = "preassigned_cashier_id")
	private String preassignedCashierId;

	@Column(name = "manager_user_id")
	private String managerUserId;

	@Column(name = "seat_wise_order_mode")
	private int seatWiseOrderMode;

	private transient Application application;

	@Column(name = "is_currenlty_merged", length = 1)
	private int isCurrentlyMerged;

	@Column(name = "is_roundOff_required")
	private int isRoundOffRequired;

	private transient boolean isSeletedForSupplier;

	@Column(name = "is_realtime_inventory_required")
	private int isRealTimeInventoryRequired;

	@Column(name = "is_realtime_product_updates_required")
	private int isRealTimeProductUpdateRequired;

	@Column(name = "is_third_party_location")
	private int isThirdPartyLocation;

	@Column(name = "is_logging_required")
	private int isLoggingRequired;

	@Column(name = "is_payment_receipt_required")
	private int isPaymentReceiptRequired;

	private transient boolean isNullRequired = false;
	@Column(name = "display_qrcode")
	private int displayQrcode;

	@Column(name = "is_advance_receipt")
	private int isAdvanceReceipt;

	@Column(name = "print_batch_settle")
	private int printBatchSettlement;

	@Column(name = "print_feedback")
	private int printFeedback;

	@Column(name = "is_global_location")
	private int isGlobalLocation;
	
	@Column(name = "is_plu_scan")
	private int isPluScan;
	
	@Column(name = "is_order_number_sequencing")
	private int isOrderNumberSequencing;
	
	@Column(name = "inventory_deduction_business_id")
	private String inventoryDeductionBusinessId;
	
	@Column(name = "is_tax_including")
	private int isTaxIncluding;

	@Column(name = "tax_reg_number", length = 256)
	private String taxRegNumber;
	
	@Column(name = "is_logo_print_on_receipt")
	private int isLogoPrintOnReceipt;
	
	@Column(name = "is_recipe_show_on_kds")
	private int isRecipeShowOnKds;
	
	@Column(name = "is_attribute_printing")
	private int isAttributePrinting;
	
	@Column(name = "is_credit_term_allowed")
	private int isCreditTermAllowed;
	
	@Column(name = "is_negative_balance_allowed")
	private int isNegativeBalanceAllowed;
	
	@Column(name = "preassigned_server_name")
	private String preassignedServerName;
	
	
	
	public String getPreassignedServerName()
	{
		if(preassignedServerName == null)
		{
			return "";
		}
		return preassignedServerName;
	}

	public void setPreassignedServerName(String preassignedServerName)
	{
		this.preassignedServerName = preassignedServerName;
	}

	public int getIsTaxIncluding()
	{
		return isTaxIncluding;
	}

	public void setIsTaxIncluding(int isTaxIncluding)
	{
		this.isTaxIncluding = isTaxIncluding;
	}
	
	public String getTaxRegNumber()
	{
		return taxRegNumber;
	}

	public void setTaxRegNumber(String taxRegNumber)
	{
		this.taxRegNumber = taxRegNumber;
	}

	public int getIsPluScan()
	{
		return isPluScan;
	}

	public void setIsPluScan(int isPluScan)
	{
		this.isPluScan = isPluScan;
	}

	public int getIsGlobalLocation()
	{
		return isGlobalLocation;
	}

	public void setIsGlobalLocation(int isGlobalLocation)
	{
		this.isGlobalLocation = isGlobalLocation;
	}

	public String getInventoryDeductionBusinessId() {
		 if(inventoryDeductionBusinessId != null && (inventoryDeductionBusinessId.length()==0 || inventoryDeductionBusinessId.equals("0"))){return null;}else{	return inventoryDeductionBusinessId;}
	}

	public void setInventoryDeductionBusinessId(String inventoryDeductionBusinessId) {
		this.inventoryDeductionBusinessId = inventoryDeductionBusinessId;
	}

	public Location()
	{
	}

	public Location(String id)
	{
		this.id = id;
	}

	public String getLocationsId()
	{
		if (isNullRequired && this.locationsId == null)
		{
			return null;
		}
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		
		this.locationsId = locationsId;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public Integer getBusinessId()
	{
		if (isNullRequired && this.businessId == 0)
		{
			return null;
		}
		return this.businessId;
	}

	public void setBusinessId(Integer businessId)
	{
		if (businessId == null)
		{
			businessId = 0;
		}
		this.businessId = businessId;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFunctionalCurrency()
	{
		return this.functionalCurrency;
	}

	public void setFunctionalCurrency(String functionalCurrency)
	{
		this.functionalCurrency = functionalCurrency;
	}

	public String getImageUrl()
	{
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public Integer getMaxPointOfServiceNum()
	{
		if (isNullRequired && this.maxPointOfServiceNum == 0)
		{
			return null;
		}
		return this.maxPointOfServiceNum;
	}

	public void setMaxPointOfServiceNum(Integer maxPointOfServiceNum)
	{
		if (maxPointOfServiceNum == null)
		{
			maxPointOfServiceNum = 0;
		}
		this.maxPointOfServiceNum = maxPointOfServiceNum;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Float getPosX()
	{
		if (isNullRequired && this.posX == 0)
		{
			return null;
		}
		return this.posX;
	}

	public void setPosX(Float posX)
	{
		if (posX == null)
		{
			posX = new Float(0.0);
		}
		this.posX = posX;
	}

	public Float getPosY()
	{
		if (isNullRequired && this.posY == 0)
		{
			return null;
		}
		return this.posY;
	}

	public void setPosY(Float posY)
	{
		if (posY == null)
		{
			posY = new Float(0.0);
		}
		this.posY = posY;
	}

	public String getReportingCurrency()
	{
		return this.reportingCurrency;
	}

	public void setReportingCurrency(String reportingCurrency)
	{
		this.reportingCurrency = reportingCurrency;
	}

	public Float getGratuity()
	{
		if (isNullRequired && this.gratuity == 0)
		{
			return null;
		}
		return gratuity;
	}

	public void setGratuity(Float gratuity)
	{
		if (gratuity == null)
		{
			gratuity = new Float(0.0);
		}
		this.gratuity = gratuity;
	}

	public Integer getTimezoneId()
	{
		return timezoneId;
	}

	public void setTimezoneId(Integer timezoneId)
	{

		this.timezoneId = timezoneId;
	}

	public Integer getTransactionalCurrencyId()
	{
		return transactionalCurrencyId;
	}

	public void setTransactionalCurrencyId(Integer transactionalCurrencyId)
	{
		this.transactionalCurrencyId = transactionalCurrencyId;
	}

	public String getWebsite()
	{
		return this.website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}

	public Integer getLocationsTypeId()
	{
		if (isNullRequired && this.locationsTypeId == 0)
		{
			return null;
		}
		return locationsTypeId;
	}

	public void setLocationsTypeId(Integer locationsTypeId)
	{
		if (locationsTypeId == null)
		{
			locationsTypeId = 0;
		}
		this.locationsTypeId = locationsTypeId;
	}

	public Integer getFloorTimer()
	{
		if (isNullRequired && this.floorTimer == 0)
		{
			return null;
		}
		return floorTimer;
	}

	public void setFloorTimer(Integer floorTimer)
	{
		if (floorTimer == null)
		{
			floorTimer = 0;
		}
		this.floorTimer = floorTimer;
	}

	public boolean equals(Location location)
	{
		if (location instanceof Location && ((Location) location).getId() == this.id)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Integer getNumberofpeople()
	{
		if (isNullRequired && this.numberofpeople == 0)
		{
			return null;
		}
		return numberofpeople;
	}

	public void setNumberofpeople(Integer numberofpeople)
	{
		if (numberofpeople == null)
		{
			numberofpeople = 0;
		}
		this.numberofpeople = numberofpeople;
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

	public String getPreassignedServerId()
	{
		 if(preassignedServerId != null && (preassignedServerId.length()==0 || preassignedServerId.equals("0"))){return null;}else{	return preassignedServerId;}
	}

	public void setPreassignedServerId(String preassignedServerId)
	{
		this.preassignedServerId = preassignedServerId;
	}

	 

	public Integer getBusinessTypeId()
	{

		if (isNullRequired && this.businessTypeId == 0)
		{
			return null;
		}
		return businessTypeId;
	}

	public void setBusinessTypeId(Integer businessTypeId)
	{
		if (businessTypeId == null)
		{
			businessTypeId = 0;
		}
		this.businessTypeId = businessTypeId;
	}

	public Integer getIsCurrentlyMerged()
	{
		if (isNullRequired && this.isCurrentlyMerged == 0)
		{
			return null;
		}
		return isCurrentlyMerged;
	}

	public void setIsCurrentlyMerged(Integer isCurrentlyMerged)
	{
		if (isCurrentlyMerged == null)
		{
			isCurrentlyMerged = 0;
		}
		this.isCurrentlyMerged = isCurrentlyMerged;
	}

	 
	public String getPreassignedCashierId() {
		 if(preassignedCashierId != null && (preassignedCashierId.length()==0 || preassignedCashierId.equals("0"))){return null;}else{	return preassignedCashierId;}
	}

	public void setPreassignedCashierId(String preassignedCashierId) {
		this.preassignedCashierId = preassignedCashierId;
	}

	public String getManagerUserId() {
		 if(managerUserId != null && (managerUserId.length()==0 || managerUserId.equals("0"))){return null;}else{	return managerUserId;}
	}

	public void setManagerUserId(String managerUserId) {
		this.managerUserId = managerUserId;
	}

	/**
	 * @return the isRoundOffRequired
	 */
	public Integer getIsRoundOffRequired()
	{
		if (isNullRequired && this.isRoundOffRequired == 0)
		{
			return null;
		}
		return isRoundOffRequired;
	}

	/**
	 * @param isRoundOffRequired
	 *            the isRoundOffRequired to set
	 */
	public void setIsRoundOffRequired(Integer isRoundOffRequired)
	{
		if (isRoundOffRequired == null)
		{
			isRoundOffRequired = 0;
		}
		this.isRoundOffRequired = isRoundOffRequired;
	}

	public boolean isSeletedForSupplier()
	{
		return isSeletedForSupplier;
	}

	public void setSeletedForSupplier(boolean isSeletedForSupplier)
	{
		this.isSeletedForSupplier = isSeletedForSupplier;
	}

	public Integer getIsRealTimeInventoryRequired()
	{
		if (isNullRequired && this.isRealTimeInventoryRequired == 0)
		{
			return null;
		}
		return isRealTimeInventoryRequired;
	}

	public void setIsRealTimeInventoryRequired(Integer isRealTimeInventoryRequired)
	{
		if (isRealTimeInventoryRequired == null)
		{
			isRealTimeInventoryRequired = 0;
		}
		this.isRealTimeInventoryRequired = isRealTimeInventoryRequired;
	}

	public Integer getIsRealTimeProductUpdateRequired()
	{
		if (isNullRequired && this.isRealTimeProductUpdateRequired == 0)
		{
			return null;
		}
		return isRealTimeProductUpdateRequired;
	}

	public void setIsRealTimeProductUpdateRequired(Integer isRealTimeProductUpdateRequired)
	{
		if (isRealTimeProductUpdateRequired == null)
		{
			isRealTimeProductUpdateRequired = 0;
		}
		this.isRealTimeProductUpdateRequired = isRealTimeProductUpdateRequired;
	}

	public Integer getIsThirdPartyLocation()
	{
		if (isNullRequired && this.isThirdPartyLocation == 0)
		{
			return null;
		}
		return isThirdPartyLocation;
	}

	public void setIsThirdPartyLocation(Integer isThirdPartyLocation)
	{
		if (isThirdPartyLocation == null)
		{
			isThirdPartyLocation = 0;
		}
		this.isThirdPartyLocation = isThirdPartyLocation;
	}

	/**
	 * @return the application
	 */
	public Application getApplication()
	{
		return application;
	}

	/**
	 * @param application
	 *            the application to set
	 */
	public void setApplication(Application application)
	{
		this.application = application;
	}

	public boolean isNullRequired()
	{
		return isNullRequired;
	}

	public void setNullRequired(boolean isNullRequired)
	{
		this.isNullRequired = isNullRequired;
	}

	public int getIsLoggingRequired()
	{
		return isLoggingRequired;
	}

	public void setIsLoggingRequired(int isLoggingRequired)
	{
		this.isLoggingRequired = isLoggingRequired;
	}

	public int getIsPaymentReceiptRequired()
	{
		return isPaymentReceiptRequired;
	}

	public void setIsPaymentReceiptRequired(int isPaymentReceiptRequired)
	{
		this.isPaymentReceiptRequired = isPaymentReceiptRequired;
	}

	public int getDisplayQrcode()
	{
		return displayQrcode;
	}

	public void setDisplayQrcode(int displayQrcode)
	{
		this.displayQrcode = displayQrcode;
	}

	public int getIsAdvanceReceipt()
	{
		return isAdvanceReceipt;
	}

	public void setIsAdvanceReceipt(int isAdvanceReceipt)
	{
		this.isAdvanceReceipt = isAdvanceReceipt;
	}

	public int getPrintBatchSettlement()
	{
		return printBatchSettlement;
	}

	public void setPrintBatchSettlement(int printBatchSettlement)
	{
		this.printBatchSettlement = printBatchSettlement;
	}

	public int getPrintFeedback()
	{
		return printFeedback;
	}

	public void setPrintFeedback(int printFeedback)
	{
		this.printFeedback = printFeedback;
	}

	public int getSeatWiseOrderMode()
	{
		return seatWiseOrderMode;
	}

	public void setSeatWiseOrderMode(int seatWiseOrderMode)
	{
		this.seatWiseOrderMode = seatWiseOrderMode;
	}

	public int getIsOrderNumberSequencing()
	{
		return isOrderNumberSequencing;
	}

	public void setIsOrderNumberSequencing(int isOrderNumberSequencing)
	{
		this.isOrderNumberSequencing = isOrderNumberSequencing;
	}
	
	public int getIsLogoPrintOnReceipt() {
		return isLogoPrintOnReceipt;
	}

	public void setIsLogoPrintOnReceipt(int isLogoPrintOnReceipt) {
		this.isLogoPrintOnReceipt = isLogoPrintOnReceipt;
	}

	public int getIsRecipeShowOnKds() {
		return isRecipeShowOnKds;
	}

	public void setIsRecipeShowOnKds(int isRecipeShowOnKds) {
		this.isRecipeShowOnKds = isRecipeShowOnKds;
	}


	public int getIsAttributePrinting() {
		return isAttributePrinting;
	}

	public void setIsAttributePrinting(int isAttributePrinting) {
		this.isAttributePrinting = isAttributePrinting;
	}

	
	public int getIsCreditTermAllowed() {
		return isCreditTermAllowed;
	}

	public void setIsCreditTermAllowed(int isCreditTermAllowed) {
		this.isCreditTermAllowed = isCreditTermAllowed;
	}
	public int getIsNegativeBalanceAllowed() {
		return isNegativeBalanceAllowed;
	}

	public void setIsNegativeBalanceAllowed(int isNegativeBalanceAllowed) {
		this.isNegativeBalanceAllowed = isNegativeBalanceAllowed;
	}

	@Override
	public String toString()
	{
		return "Location [address=" + address + ", businessId=" + businessId + ", email=" + email + ", functionalCurrency=" + functionalCurrency + ", imageUrl=" + imageUrl + ", maxPointOfServiceNum="
				+ maxPointOfServiceNum + ", name=" + name + ", posX=" + posX + ", posY=" + posY + ", reportingCurrency=" + reportingCurrency + ", salesTax1=" + salesTax1 + ", salesTax2=" + salesTax2
				+ ", salesTax3=" + salesTax3 + ", salesTax4=" + salesTax4 + ", timezoneId=" + timezoneId + ", transactionalCurrencyId=" + transactionalCurrencyId + ", locationsId=" + locationsId
				+ ", locationsTypeId=" + locationsTypeId + ", website=" + website + ", numberofpeople=" + numberofpeople + ", gratuity=" + gratuity + ", floorTimer=" + floorTimer
				+ ", preassignedServerId=" + preassignedServerId + ", businessTypeId=" + businessTypeId + ", preassignedCashierId=" + preassignedCashierId + ", managerUserId=" + managerUserId
				+ ", seatWiseOrderMode=" + seatWiseOrderMode + ", application=" + application + ", isCurrentlyMerged=" + isCurrentlyMerged + ", isRoundOffRequired=" + isRoundOffRequired
				+ ", isRealTimeInventoryRequired=" + isRealTimeInventoryRequired + ", isRealTimeProductUpdateRequired=" + isRealTimeProductUpdateRequired + ", isThirdPartyLocation="
				+ isThirdPartyLocation + ", isLoggingRequired=" + isLoggingRequired + ", isPaymentReceiptRequired=" + isPaymentReceiptRequired + ", displayQrcode=" + displayQrcode
				+ ", isAdvanceReceipt=" + isAdvanceReceipt + ", printBatchSettlement=" + printBatchSettlement + ", printFeedback=" + printFeedback + ", isGlobalLocation=" + isGlobalLocation
				+ ", isPluScan=" + isPluScan + ", isOrderNumberSequencing=" + isOrderNumberSequencing + ", inventoryDeductionBusinessId=" + inventoryDeductionBusinessId + ", isTaxIncluding="
				+ isTaxIncluding + ", taxRegNumber=" + taxRegNumber + ", isLogoPrintOnReceipt=" + isLogoPrintOnReceipt + ", isRecipeShowOnKds=" + isRecipeShowOnKds + ", isAttributePrinting="
				+ isAttributePrinting + ", isCreditTermAllowed=" + isCreditTermAllowed + ", isNegativeBalanceAllowed=" + isNegativeBalanceAllowed + ", preassignedServerName=" + preassignedServerName
				+ "]";
	}


	
}