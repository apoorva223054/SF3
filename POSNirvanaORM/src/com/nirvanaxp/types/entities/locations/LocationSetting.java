package com.nirvanaxp.types.entities.locations;


import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the location_setting database table.
 * 
 */

@Entity
@Table(name="location_setting")
@XmlRootElement(name = "location_setting")
public class LocationSetting extends POSNirvanaBaseClassWithoutGeneratedIds {
	private static final long serialVersionUID = 1L;

	@Column(name="display_qrcode")
	private int displayQrcode;

	@Column(name="is_advance_receipt")
	private int isAdvanceReceipt;

	@Column(name="is_attribute_printing")
	private int isAttributePrinting;
		
	@Column(name="is_logo_print_on_receipt")
	private int isLogoPrintOnReceipt;

	@Column(name="is_order_with_tax_name")
	private int isOrderWithTaxName;

	@Column(name="is_plu_scan")
	private int isPluScan;	

	@Column(name="is_realtime_inventory_required")
	private int isRealtimeInventoryRequired;

	@Column(name="is_realtime_product_updates_required")
	private int isRealtimeProductUpdatesRequired;

	@Column(name="is_recipe_show_on_kds")
	private int isRecipeShowOnKds;

	@Column(name="is_roundOff_required")
	private int isRoundOffRequired;

	@Column(name="location_id")
	private String locationId;

	@Column(name="print_batch_settle")
	private int printBatchSettle;

	@Column(name="print_feedback")
	private int printFeedback;

	@Column(name="seat_wise_order_mode")
	private int seatWiseOrderMode;
		
	@Column(name = "is_credit_term_allowed")
	private int isCreditTermAllowed;
	
	@Column(name = "is_negative_balance_allowed")
	private int isNegativeBalanceAllowed;
	
	@Column(name = "is_delivery_charges_include_tax")
	private int isDeliveryChargesIncludeTax;
	
	@Column(name = "is_clockin_validation")
	private int isClockinValidation;
	
	@Column(name = "is_custom_price_button")
	private int isCustomPriceButton;
	
	@Column(name = "is_service_charge")
	private int isServiceCharge;
	
	@Column(name = "order_tax_name")
	private String orderTaxName;
	
	@Column(name = "item_sorting_format")
	private String itemSortingFormat;
	
	@Column(name = "is_pour_my_beer")
	private int isPourMyBeer;	
	
	@Column(name = "is_paid_in_paid_out_validation")
	private int isPaidInPaidOutValidation;

	@Column(name = "is_tip_pooling_needed")
	private boolean isTipPoolingNeeded;
	
	@Column(name = "is_auto_batch_close")
	private int isAutoBatchClose;
	
	@Column(name = "batch_close_timing")
	private String batchCloseTiming;
	
	@Column(name = "is_remove_delete_button")
	private int isRemoveDeleteButton;
	
	@Column(name = "is_detail_payment_transaction_receipt")
	private int isDetailPaymentTransactionReceipt;
	
	@Column(name = "is_plu_required")
	private int isPluRequired;
	
	@Column(name = "is_reporting_category_required")
	private int isReportingCategoryRequired;
	
	@Column(name = "is_peoplesoft_exception_report")
	private boolean isPeoplesoftExceptionReport;
	
	@Column(name = "pour_my_beer_ip_address")
	private String pourMyBeerIpAddress;
	
	@Column(name = "business_code")
	private String businessCode;
	
	@Column(name = "is_order_locking")
	private int isOrderLocking;
	
	@Column(name = "is_role_change_approval_needed")
	private int isRoleChangeApprovalNeeded;
	
	@Column(name = "is_forced_clockout_needed")
	private int isForcedClockoutNeeded;
 
	@Column(name = "is_online_pickup_order_menu")
	private int isOnlinePickupOrderMenu;
	
	@Column(name = "is_online_delivery_order_menu")
	private int isOnlineDeliveryMenu;
	
	@Column(name = "is_auto_tip_settlement")
	private int isAutoTipSettlement;
 
 
	
	@Column(name = "is_unassign_server_on_batch_settle")
	private int isUnassignServerOnBatchSettlement;
 
	
	
	@Column(name = "pre_auth_allowed")
	private boolean preAuthAllowed;
 
	
	@Column(name = "is_signup_sms")
	private int isSignupSms;
	
	@Column(name = "template_id")
	private int templateId;
	
	@Column(name = "default_preauth_amount")
	private BigDecimal defaultPreAuthAmount;
	
	@Column(name = "menu_scheduling")
	private boolean menuScheduling;
 
	@Column(name = "need_label_printing")
	private boolean needLabelPrinting;
	
	@Column(name="is_show_uom")
	private int isShowUOM;
	
	@Column(name="is_self_serve_as_digital_menu")
	private int isSelfServeAsDigitalMenu;

	@Column(name="is_loyalty_integrated")
	private int isLoyaltyIntegrated;
	
	private transient List<LocationBatchTiming> locationBatchTimingList;

	@Column(name="is_summarised_receipt")
	private int isSummarisedReceipt;
	

	
	@Column(name="is_repeat_order")
	private int isRepeatOrder;
	
	
	@Column(name = "is_duplicate_bill")
	private int isDuplicateBill;
	
	@Column(name = "printing_url_ip")
	private String printingUrlIp;
	
	@Column(name = "is_time_on_print_check")
	private int isTimeOnPrintCheck;
	
	@Column(name = "is_token_printing")
	private int isTokenPrinting;
	
	@Column(name = "is_print_check_on")
	private int isPrintCheckOn;
	
	public int getIsTimeOnPrintCheck() {
		return isTimeOnPrintCheck;
	}

	public void setIsTimeOnPrintCheck(int isTimeOnPrintCheck) {
		this.isTimeOnPrintCheck = isTimeOnPrintCheck;
	}

	public int getIsSelfServeAsDigitalMenu() {
		return isSelfServeAsDigitalMenu;
	}

	public void setIsSelfServeAsDigitalMenu(int isSelfServeAsDigitalMenu) {
		this.isSelfServeAsDigitalMenu = isSelfServeAsDigitalMenu;
	}

	public int getIsSignupSms() {
		return isSignupSms;
	}

	public void setIsSignupSms(int isSignupSms) {
		this.isSignupSms = isSignupSms;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getIsRemoveDeleteButton()
	{
		return isRemoveDeleteButton;
	}

	public void setIsRemoveDeleteButton(int isRemoveDeleteButton)
	{
		this.isRemoveDeleteButton = isRemoveDeleteButton;
	}

 
	
 

	public boolean isTipPoolingNeeded()
	{
 
		return isTipPoolingNeeded;
	}

 
	public void setTipPoolingNeeded(boolean isTipPoolingNeeded)
	{
		this.isTipPoolingNeeded = isTipPoolingNeeded;
	}

	public int getIsPourMyBeer()
	{
		return isPourMyBeer;
	}

	public void setIsPourMyBeer(int isPourMyBeer)
	{
		this.isPourMyBeer = isPourMyBeer;
	}

	public LocationSetting() {
	}

	public int getDisplayQrcode() {
		return this.displayQrcode;
	}

	public void setDisplayQrcode(int displayQrcode) {
		this.displayQrcode = displayQrcode;
	}

	public int getIsAdvanceReceipt() {
		return this.isAdvanceReceipt;
	}

	public void setIsAdvanceReceipt(int isAdvanceReceipt) {
		this.isAdvanceReceipt = isAdvanceReceipt;
	}

	public int getIsAttributePrinting() {
		return this.isAttributePrinting;
	}

	public void setIsAttributePrinting(int isAttributePrinting) {
		this.isAttributePrinting = isAttributePrinting;
	}


	public int getIsLogoPrintOnReceipt() {
		return this.isLogoPrintOnReceipt;
	}

	public void setIsLogoPrintOnReceipt(int isLogoPrintOnReceipt) {
		this.isLogoPrintOnReceipt = isLogoPrintOnReceipt;
	}

	
	

	public int getIsOrderWithTaxName() {
		return this.isOrderWithTaxName;
	}

	public void setIsOrderWithTaxName(int isOrderWithTaxName) {
		this.isOrderWithTaxName = isOrderWithTaxName;
	}

	
	public int getIsPluScan() {
		return this.isPluScan;
	}

	public void setIsPluScan(int isPluScan) {
		this.isPluScan = isPluScan;
	}

	public int getIsRealtimeInventoryRequired() {
		return this.isRealtimeInventoryRequired;
	}

	public void setIsRealtimeInventoryRequired(int isRealtimeInventoryRequired) {
		this.isRealtimeInventoryRequired = isRealtimeInventoryRequired;
	}

	public int getIsRealtimeProductUpdatesRequired() {
		return this.isRealtimeProductUpdatesRequired;
	}

	public void setIsRealtimeProductUpdatesRequired(int isRealtimeProductUpdatesRequired) {
		this.isRealtimeProductUpdatesRequired = isRealtimeProductUpdatesRequired;
	}

	public int getIsRecipeShowOnKds() {
		return this.isRecipeShowOnKds;
	}

	public void setIsRecipeShowOnKds(int isRecipeShowOnKds) {
		this.isRecipeShowOnKds = isRecipeShowOnKds;
	}

	public int getIsRoundOffRequired() {
		return isRoundOffRequired;
	}

	public void setIsRoundOffRequired(int isRoundOffRequired) {
		this.isRoundOffRequired = isRoundOffRequired;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public int getPrintBatchSettle() {
		return this.printBatchSettle;
	}

	public void setPrintBatchSettle(int printBatchSettle) {
		this.printBatchSettle = printBatchSettle;
	}

	public int getPrintFeedback() {
		return this.printFeedback;
	}

	public void setPrintFeedback(int printFeedback) {
		this.printFeedback = printFeedback;
	}

	public int getSeatWiseOrderMode() {
		return this.seatWiseOrderMode;
	}

	public void setSeatWiseOrderMode(int seatWiseOrderMode) {
		this.seatWiseOrderMode = seatWiseOrderMode;
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
	
	public String getOrderTaxName() {
		return orderTaxName;
	}

	public void setOrderTaxName(String orderTaxName) {
		this.orderTaxName = orderTaxName;
	}
	
	

	public String getItemSortingFormat()
	{
		return itemSortingFormat;
	}

	public void setItemSortingFormat(String itemSortingFormat)
	{
		this.itemSortingFormat = itemSortingFormat;
	}

	public int getIsDeliveryChargesIncludeTax() {
		return isDeliveryChargesIncludeTax;
	}

	public void setIsDeliveryChargesIncludeTax(int isDeliveryChargesIncludeTax) {
		this.isDeliveryChargesIncludeTax = isDeliveryChargesIncludeTax;
	}
	
	

	public int getIsClockinValidation() {
		return isClockinValidation;
	}

	public void setIsClockinValidation(int isClockinValidation) {
		this.isClockinValidation = isClockinValidation;
	}

	public int getIsCustomPriceButton() {
		return isCustomPriceButton;
	}

	public void setIsCustomPriceButton(int isCustomPriceButton) {
		this.isCustomPriceButton = isCustomPriceButton;
	}

	public int getIsServiceCharge() {
		return isServiceCharge;
	}

	public void setIsServiceCharge(int isServiceCharge) {
		this.isServiceCharge = isServiceCharge;
	}

	
	public int getIsPaidInPaidOutValidation() {
		return isPaidInPaidOutValidation;
	}

	public void setIsPaidInPaidOutValidation(int isPaidInPaidOutValidation) {
		this.isPaidInPaidOutValidation = isPaidInPaidOutValidation;
	}

	
	public int getIsAutoBatchClose() {
		return isAutoBatchClose;
	}

	public void setIsAutoBatchClose(int isAutoBatchClose) {
		this.isAutoBatchClose = isAutoBatchClose;
	}

	public String getBatchCloseTiming() {
		return batchCloseTiming;
	}

	public void setBatchCloseTiming(String batchCloseTiming) {
		this.batchCloseTiming = batchCloseTiming;
	}

	
	public int getIsDetailPaymentTransactionReceipt() {
		return isDetailPaymentTransactionReceipt;
	}

	public void setIsDetailPaymentTransactionReceipt(
			int isDetailPaymentTransactionReceipt) {
		this.isDetailPaymentTransactionReceipt = isDetailPaymentTransactionReceipt;
	}

	public int getIsPluRequired() {
		return isPluRequired;
	}

	public void setIsPluRequired(int isPluRequired) {
		this.isPluRequired = isPluRequired;
	}

	public int getIsReportingCategoryRequired() {
		return isReportingCategoryRequired;
	}

	public void setIsReportingCategoryRequired(int isReportingCategoryRequired) {
		this.isReportingCategoryRequired = isReportingCategoryRequired;
	}

	public boolean isPeoplesoftExceptionReport() {
		return isPeoplesoftExceptionReport;
	}

	public void setPeoplesoftExceptionReport(boolean isPeoplesoftExceptionReport) {
		this.isPeoplesoftExceptionReport = isPeoplesoftExceptionReport;
	}

	public String getPourMyBeerIpAddress() {
		return pourMyBeerIpAddress;
	}

	public int getIsOrderLocking() {
		return isOrderLocking;
	}

	public void setIsOrderLocking(int isOrderLocking) {
		this.isOrderLocking = isOrderLocking;
	}

	public void setPourMyBeerIpAddress(String pourMyBeerIpAddress) {
		this.pourMyBeerIpAddress = pourMyBeerIpAddress;
	}

	public String getBusinessCode()
	{
		return businessCode;
	}

	public void setBusinessCode(String businessCode)
	{
		this.businessCode = businessCode;
	}

	public int getIsRoleChangeApprovalNeeded() {
		return isRoleChangeApprovalNeeded;
	}

	public void setIsRoleChangeApprovalNeeded(int isRoleChangeApprovalNeeded) {
		this.isRoleChangeApprovalNeeded = isRoleChangeApprovalNeeded;
	}

	public int getIsForcedClockoutNeeded() {
		return isForcedClockoutNeeded;
	}

	public void setIsForcedClockoutNeeded(int isForcedClockoutNeeded) {
		this.isForcedClockoutNeeded = isForcedClockoutNeeded;
	}

 
	public int getIsOnlinePickupOrderMenu() {
		return isOnlinePickupOrderMenu;
	}

	public int getIsOnlineDeliveryMenu() {
		return isOnlineDeliveryMenu;
	}

 

	public void setIsOnlinePickupOrderMenu(int isOnlinePickupOrderMenu) {
		this.isOnlinePickupOrderMenu = isOnlinePickupOrderMenu;
	}

	public void setIsOnlineDeliveryMenu(int isOnlineDeliveryMenu) {
		this.isOnlineDeliveryMenu = isOnlineDeliveryMenu;
	}

 

	public int getIsUnassignServerOnBatchSettlement()
	{
		return isUnassignServerOnBatchSettlement;
	}

	public void setIsUnassignServerOnBatchSettlement(int isUnassignServerOnBatchSettlement)
	{
		this.isUnassignServerOnBatchSettlement = isUnassignServerOnBatchSettlement;
	}

	public int getIsAutoTipSettlement() {
		return isAutoTipSettlement;
	}

	public void setIsAutoTipSettlement(int isAutoTipSettlement) {
		this.isAutoTipSettlement = isAutoTipSettlement;
	}

	
	
	public boolean isPreAuthAllowed() {
		return preAuthAllowed;
	}

	public void setPreAuthAllowed(boolean preAuthAllowed) {
		this.preAuthAllowed = preAuthAllowed;
	}

	
	public BigDecimal getDefaultPreAuthAmount() {
		return defaultPreAuthAmount;
	}

	public void setDefaultPreAuthAmount(BigDecimal defaultPreAuthAmount) {
		this.defaultPreAuthAmount = defaultPreAuthAmount;
	}

	
 
	public boolean isMenuScheduling() {
		return menuScheduling;
	}

	public void setMenuScheduling(boolean menuScheduling) {
		this.menuScheduling = menuScheduling;
	}

	public boolean isNeedLabelPrinting() {
		return needLabelPrinting;
	}

	public void setNeedLabelPrinting(boolean needLabelPrinting) {
		this.needLabelPrinting = needLabelPrinting;
	}

	public int getIsShowUOM() {
		return isShowUOM;
	}

	public void setIsShowUOM(int isShowUOM) {
		this.isShowUOM = isShowUOM;
	}
	

	public List<LocationBatchTiming> getLocationBatchTimingList()
	{
		return locationBatchTimingList;
	}

	public void setLocationBatchTimingList(List<LocationBatchTiming> locationBatchTimingList)
	{
		this.locationBatchTimingList = locationBatchTimingList;
	}
	

	public int getIsLoyaltyIntegrated() {
		return isLoyaltyIntegrated;
	}

	public void setIsLoyaltyIntegrated(int isLoyaltyIntegrated) {
		this.isLoyaltyIntegrated = isLoyaltyIntegrated;
	}

	public int getIsSummarisedReceipt() {
		return isSummarisedReceipt;
	}

	public void setIsSummarisedReceipt(int isSummarisedReceipt) {
		this.isSummarisedReceipt = isSummarisedReceipt;
	}

	public int getIsRepeatOrder() {
		return isRepeatOrder;
	}

	public void setIsRepeatOrder(int isRepeatOrder) {
		this.isRepeatOrder = isRepeatOrder;
	}

	public int getIsDuplicateBill() {
		return isDuplicateBill;
	}

	public void setIsDuplicateBill(int isDuplicateBill) {
		this.isDuplicateBill = isDuplicateBill;
	}

	public String getPrintingUrlIp() {
		return printingUrlIp;
	}

	public void setPrintingUrlIp(String printingUrlIp) {
		this.printingUrlIp = printingUrlIp;
	}

	public int getIsTokenPrinting() {
		return isTokenPrinting;
	}

	public void setIsTokenPrinting(int isTokenPrinting) {
		this.isTokenPrinting = isTokenPrinting;
	}

	public int getIsPrintCheckOn() {
		return isPrintCheckOn;
	}

	public void setIsPrintCheckOn(int isPrintCheckOn) {
		this.isPrintCheckOn = isPrintCheckOn;
	}

	@Override
	public String toString() {
		return "LocationSetting [displayQrcode=" + displayQrcode
				+ ", isAdvanceReceipt=" + isAdvanceReceipt
				+ ", isAttributePrinting=" + isAttributePrinting
				+ ", isLogoPrintOnReceipt=" + isLogoPrintOnReceipt
				+ ", isOrderWithTaxName=" + isOrderWithTaxName + ", isPluScan="
				+ isPluScan + ", isRealtimeInventoryRequired="
				+ isRealtimeInventoryRequired
				+ ", isRealtimeProductUpdatesRequired="
				+ isRealtimeProductUpdatesRequired + ", isRecipeShowOnKds="
				+ isRecipeShowOnKds + ", isRoundOffRequired="
				+ isRoundOffRequired + ", locationId=" + locationId
				+ ", printBatchSettle=" + printBatchSettle + ", printFeedback="
				+ printFeedback + ", seatWiseOrderMode=" + seatWiseOrderMode
				+ ", isCreditTermAllowed=" + isCreditTermAllowed
				+ ", isNegativeBalanceAllowed=" + isNegativeBalanceAllowed
				+ ", isDeliveryChargesIncludeTax="
				+ isDeliveryChargesIncludeTax + ", isClockinValidation="
				+ isClockinValidation + ", isCustomPriceButton="
				+ isCustomPriceButton + ", isServiceCharge=" + isServiceCharge
				+ ", orderTaxName=" + orderTaxName + ", itemSortingFormat="
				+ itemSortingFormat + ", isPourMyBeer=" + isPourMyBeer
				+ ", isPaidInPaidOutValidation=" + isPaidInPaidOutValidation
				+ ", isTipPoolingNeeded=" + isTipPoolingNeeded
				+ ", isAutoBatchClose=" + isAutoBatchClose
				+ ", batchCloseTiming=" + batchCloseTiming
				+ ", isRemoveDeleteButton=" + isRemoveDeleteButton
				+ ", isDetailPaymentTransactionReceipt="
				+ isDetailPaymentTransactionReceipt + ", isPluRequired="
				+ isPluRequired + ", isReportingCategoryRequired="
				+ isReportingCategoryRequired
				+ ", isPeoplesoftExceptionReport="
				+ isPeoplesoftExceptionReport + ", pourMyBeerIpAddress="
				+ pourMyBeerIpAddress + ", businessCode=" + businessCode
				+ ", isOrderLocking=" + isOrderLocking
				+ ", isRoleChangeApprovalNeeded=" + isRoleChangeApprovalNeeded
				+ ", isForcedClockoutNeeded=" + isForcedClockoutNeeded
				+ ", isOnlinePickupOrderMenu=" + isOnlinePickupOrderMenu
				+ ", isOnlineDeliveryMenu=" + isOnlineDeliveryMenu
				+ ", isAutoTipSettlement=" + isAutoTipSettlement
				+ ", isUnassignServerOnBatchSettlement="
				+ isUnassignServerOnBatchSettlement + ", preAuthAllowed="
				+ preAuthAllowed + ", isSignupSms=" + isSignupSms
				+ ", templateId=" + templateId + ", defaultPreAuthAmount="
				+ defaultPreAuthAmount + ", menuScheduling=" + menuScheduling
				+ ", needLabelPrinting=" + needLabelPrinting + ", isShowUOM="
				+ isShowUOM + ", isSelfServeAsDigitalMenu="
				+ isSelfServeAsDigitalMenu + ", isLoyaltyIntegrated="
				+ isLoyaltyIntegrated + ", isSummarisedReceipt="
				+ isSummarisedReceipt + ", isRepeatOrder=" + isRepeatOrder
				+ ", isDuplicateBill=" + isDuplicateBill + ", printingUrlIp="
				+ printingUrlIp + ", isTimeOnPrintCheck=" + isTimeOnPrintCheck
				+ ", isTokenPrinting=" + isTokenPrinting + ", isPrintCheckOn="
				+ isPrintCheckOn + "]";
	}


	

	
	

	 
	
 
 }