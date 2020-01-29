/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.swing.text.Position.Bias;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonManagedReference;

// import java.util.Set;

/**
 * The persistent class for the order_header_history database table.
 * 
 */
@Entity
@Table(name = "order_header_history")
@XmlRootElement(name = "order_header_history")
public class OrderHeaderHistory implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "address_billing_id", length = 256)
	private String addressBillingId;

	@Column(name = "address_shipping_id", length = 256)
	private String addressShippingId;

	@Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
	private BigDecimal amountPaid;

	@Column(name = "balance_due", nullable = false, precision = 10, scale = 2)
	private BigDecimal balanceDue;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "discounts_id")
	private String discountsId;

	@Column(name = "discounts_name", length = 128)
	private String discountsName;

	@Column(name = "discounts_type_id")
	private String discountsTypeId;

	@Column(name = "discounts_type_name", length = 64)
	private String discountsTypeName;

	@Column(name = "discounts_value", precision = 10, scale = 2)
	private BigDecimal discountsValue;

	@Column(precision = 10, scale = 2)
	private BigDecimal gratuity;

	@Column(name = "ip_address", nullable = false, length = 20)
	private String ipAddress;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "order_header_id", nullable = false)
	private String orderHeaderId;

	@Column(name = "order_source_id", nullable = false)
	private String orderSourceId;

	@Column(name = "order_status_id", nullable = false)
	private String orderStatusId;

	@Column(name = "point_of_service_count", nullable = false)
	private Integer pointOfServiceCount;

	@Column(name = "price_discount", precision = 10, scale = 2)
	private BigDecimal priceDiscount;

	@Column(name = "price_extended", precision = 10, scale = 2)
	private BigDecimal priceExtended;

	@Column(name = "price_gratuity", precision = 10, scale = 2)
	private BigDecimal priceGratuity;

	@Column(name = "price_tax_1", precision = 10, scale = 2)
	private BigDecimal priceTax1;

	@Column(name = "price_tax_2", precision = 10, scale = 2)
	private BigDecimal priceTax2;

	@Column(name = "price_tax_3", precision = 10, scale = 2)
	private BigDecimal priceTax3;

	@Column(name = "price_tax_4", precision = 10, scale = 2)
	private BigDecimal priceTax4;

	@Column(name = "reservations_id")
	private String reservationsId;

	@Column(name = "service_tax", nullable = false, precision = 10, scale = 2)
	private BigDecimal serviceTax;

	@Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal subTotal;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	@Column(name = "payment_ways_id")
	private Integer paymentWaysId;

	@Column(name = "split_count")
	private Integer splitCount;

	@Column(name = "date")
	private String date;

	@Column(name = "verification_code")
	private String verificationCode;

	@Column(name = "qrcode")
	private String qrcode;

	@Column(name = "session_id")
	private Integer sessionId;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "server_id")
	private String serverId;

	@Column(name = "cashier_id")
	private String cashierId;

	@Column(name = "void_reason_id")
	private String voidReasonId;

	@Column(name = "tax_name_1")
	private String taxName1;

	@Column(name = "tax_name_2")
	private String taxName2;

	@Column(name = "tax_name_3")
	private String taxName3;

	@Column(name = "tax_name_4")
	private String taxName4;

	@Column(name = "total_tax")
	private BigDecimal totalTax;

	@Column(name = "tax_display_name_1")
	private String taxDisplayName1;

	@Column(name = "tax_display_name_2")
	private String taxDisplayName2;

	@Column(name = "tax_display_name_3")
	private String taxDisplayName3;

	@Column(name = "tax_display_name_4")
	private String taxDisplayName4;

	@Column(name = "tax_rate_1")
	private BigDecimal taxRate1;

	@Column(name = "tax_rate_2")
	private BigDecimal taxRate2;

	@Column(name = "tax_rate_3")
	private BigDecimal taxRate3;

	@Column(name = "tax_rate_4")
	private BigDecimal taxRate4;

	@JsonManagedReference
	@JoinColumn(name = "order_header_history_id")
	@OneToMany(cascade =
	{ CascadeType.PERSIST, CascadeType.REMOVE }, fetch = FetchType.EAGER)
	private Set<OrderDetailItemsHistory> orderDetailItemsHistories;

	@Column(name = "merged_locations_id")
	private String mergedLocationsId;

	@Column(name = "is_gratuity_applied")
	private int isGratuityApplied;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "open_time")
	private Date openTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "close_time")
	private Date closeTime;

	@Column(name = "discount_display_name")
	private String discountDisplayName;

	@Column(name = "round_off_total")
	private BigDecimal roundOffTotal;

	@Column(name = "is_tab_order")
	private int isTabOrder;

	@Column(name = "is_order_reopened")
	private int isOrderReopened;

	@Column(name = "server_name")
	private String servername;

	@Column(name = "cashier_name")
	private String cashierName;

	@Column(name = "void_reason_name")
	private String voidReasonName;

	@Column(name = "schedule_date_time")
	private String scheduleDateTime;

	@Column(name = "tax_exempt_id")
	private String taxExemptId;
	
	@Column(name = "nirvanaxp_batch_number")
	private String nirvanaXpBatchNumber;

	@Column(name = "order_number")
	private String orderNumber;
	
	@Column(name = "is_seat_wise_order")
	private int isSeatWiseOrder;
	
	@Column(name = "calculated_discount_value", precision = 10, scale = 2)
	private BigDecimal calculatedDiscountValue;
	
	@Column(name = "shift_slot_id")
	private int shiftSlotId;
	
	@Column(name = "price_discount_item_level", precision = 10, scale = 2)
	private BigDecimal priceDiscountItemLevel;
	
	@Column(name = "merge_order_id")
	private String mergeOrderId;

	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "tax_no")
	private String taxNo;
	
	@Column(name = "tax_display_name")
	private String taxDisplayName;
	
	@Column(name = "delivery_charges")
	private BigDecimal deliveryCharges;
	
	@Column(name = "delivery_tax")
	private BigDecimal deliveryTax;
	
	@Column(name = "delivery_option_id")
	private String deliveryOptionId;
	
	@Column(name = "service_charges")
	private BigDecimal serviceCharges;
	
	@Column(name = "preassigned_server_id")
	private String preassignedServerId;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "po_refrence_number")
	private String poRefrenceNumber;
	
	@Column(name = "requested_location_id")
	private String requestedLocationId;
	
	@Column(name = "order_type_id")
	private int orderTypeId;
	
	@Column(name = "driver_id")
	private String  driverId;
	@Column(name = "start_date")
	private String startDate;

	@Column(name = "end_date")
	private String endDate;

	@Column(name = "event_name")
	private String eventName;
	
	@Column(name = "transferred_order_id")
	private String transferredOrderId;

	 


	public String getDriverId()
	{
		 if(driverId != null && (driverId.length()==0 || driverId.equals("0"))){return null;}else{	return driverId;}
	}

	public void setDriverId(String driverId)
	{
		this.driverId = driverId;
	}

	public String getPoRefrenceNumber() {
		 if(poRefrenceNumber != null && (poRefrenceNumber.length()==0 || poRefrenceNumber.equals("0"))){return null;}else{	return poRefrenceNumber;}
	}

	public void setPoRefrenceNumber(String poRefrenceNumber) {
		this.poRefrenceNumber = poRefrenceNumber;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public BigDecimal getPriceDiscountItemLevel() {
		return priceDiscountItemLevel;
	}

	public void setPriceDiscountItemLevel(BigDecimal priceDiscountItemLevel) {
		this.priceDiscountItemLevel = priceDiscountItemLevel;
	}

	public int getShiftSlotId() {
		return shiftSlotId;
	}

	public void setShiftSlotId(int shiftSlotId) {
		this.shiftSlotId = shiftSlotId;
	}

	public String getRequestedLocationId()
	{
		 if(requestedLocationId != null && (requestedLocationId.length()==0 || requestedLocationId.equals("0"))){return null;}else{	return requestedLocationId;}
	}

	public void setRequestedLocationId(String requestedLocationId)
	{
		this.requestedLocationId = requestedLocationId;
	}

	
	public OrderHeaderHistory()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

 

	public String getAddressBillingId()
	{
		 if(addressBillingId != null && (addressBillingId.length()==0 || addressBillingId.equals("0"))){return null;}else{	return addressBillingId;}
	}

	public void setAddressBillingId(String addressBillingId)
	{
		this.addressBillingId = addressBillingId;
	}

	public String getAddressShippingId()
	{
		 if(addressShippingId != null && (addressShippingId.length()==0 || addressShippingId.equals("0"))){return null;}else{	return addressShippingId;}
	}

	public void setAddressShippingId(String addressShippingId)
	{
		this.addressShippingId = addressShippingId;
	}

	public BigDecimal getAmountPaid()
	{
		return this.amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid)
	{
		this.amountPaid = amountPaid;
	}

	public BigDecimal getBalanceDue()
	{
		return this.balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue)
	{
		this.balanceDue = balanceDue;
	}

	public String getDiscountsId()
	{
		 if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
	}

	public void setDiscountsId(String discountsId)
	{
		this.discountsId = discountsId;
	}

	public String getDiscountsName()
	{
		return this.discountsName;
	}

	public void setDiscountsName(String discountsName)
	{
		this.discountsName = discountsName;
	}

	public String getDiscountsTypeId()
	{
		 if(discountsTypeId != null && (discountsTypeId.length()==0 || discountsTypeId.equals("0"))){return null;}else{	return discountsTypeId;}
	}

	public void setDiscountsTypeId(String discountsTypeId)
	{
		this.discountsTypeId = discountsTypeId;
	}

	public String getDiscountsTypeName()
	{
		return this.discountsTypeName;
	}

	public void setDiscountsTypeName(String discountsTypeName)
	{
		this.discountsTypeName = discountsTypeName;
	}

	public BigDecimal getDiscountsValue()
	{
		return this.discountsValue;
	}

	public void setDiscountsValue(BigDecimal discountsValue)
	{
		this.discountsValue = discountsValue;
	}

	public BigDecimal getGratuity()
	{
		return this.gratuity;
	}

	public void setGratuity(BigDecimal gratuity)
	{
		this.gratuity = gratuity;
	}

	public String getIpAddress()
	{
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public String getOrderSourceId()
	{
		 if(orderSourceId != null && (orderSourceId.length()==0 || orderSourceId.equals("0"))){return null;}else{	return orderSourceId;}
	}

	public void setOrderSourceId(String orderSourceId)
	{
		this.orderSourceId = orderSourceId;
	}

	public String getOrderStatusId()
	{
		 if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	public void setOrderStatusId(String orderStatusId)
	{
		this.orderStatusId = orderStatusId;
	}

	public Integer getPointOfServiceCount()
	{
		return this.pointOfServiceCount;
	}

	public void setPointOfServiceCount(Integer pointOfServiceCount)
	{
		this.pointOfServiceCount = pointOfServiceCount;
	}

	public BigDecimal getPriceDiscount()
	{
		return this.priceDiscount;
	}

	public void setPriceDiscount(BigDecimal priceDiscount)
	{
		this.priceDiscount = priceDiscount;
	}

	public BigDecimal getPriceExtended()
	{
		return this.priceExtended;
	}

	public void setPriceExtended(BigDecimal priceExtended)
	{
		this.priceExtended = priceExtended;
	}

	public BigDecimal getPriceGratuity()
	{
		return this.priceGratuity;
	}

	public void setPriceGratuity(BigDecimal priceGratuity)
	{
		this.priceGratuity = priceGratuity;
	}

	public BigDecimal getPriceTax4()
	{
		return priceTax4;
	}

	public void setPriceTax4(BigDecimal priceTax4)
	{
		this.priceTax4 = priceTax4;
	}

	public BigDecimal getPriceTax1()
	{
		return this.priceTax1;
	}

	public void setPriceTax1(BigDecimal priceTax1)
	{
		this.priceTax1 = priceTax1;
	}

	public BigDecimal getPriceTax2()
	{
		return this.priceTax2;
	}

	public void setPriceTax2(BigDecimal priceTax2)
	{
		this.priceTax2 = priceTax2;
	}

	public BigDecimal getPriceTax3()
	{
		return this.priceTax3;
	}

	public void setPriceTax3(BigDecimal priceTax3)
	{
		this.priceTax3 = priceTax3;
	}

	 
	public String getReservationsId()
	{
		if(reservationsId != null && (reservationsId.length()==0 || reservationsId.equals("0"))){return null;}else{	return reservationsId;}
	}

	public void setReservationsId(String reservationsId)
	{
		this.reservationsId = reservationsId;
	}

	public BigDecimal getServiceTax()
	{
		return this.serviceTax;
	}

	public void setServiceTax(BigDecimal serviceTax)
	{
		this.serviceTax = serviceTax;
	}

	public BigDecimal getSubTotal()
	{
		return this.subTotal;
	}

	public void setSubTotal(BigDecimal subTotal)
	{
		this.subTotal = subTotal;
	}

	public BigDecimal getTotal()
	{
		return this.total;
	}

	public void setTotal(BigDecimal total)
	{
		this.total = total;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}



	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public Set<OrderDetailItemsHistory> getOrderDetailItemsHistories()
	{
		return this.orderDetailItemsHistories;
	}

	public void setOrderDetailItemsHistories(Set<OrderDetailItemsHistory> orderDetailItemsHistories)
	{
		this.orderDetailItemsHistories = orderDetailItemsHistories;
	}

	public Integer getPaymentWaysId()
	{
		return paymentWaysId;
	}

	public void setPaymentWaysId(Integer paymentWaysId)
	{
		this.paymentWaysId = paymentWaysId;
	}

	public Integer getSplitCount()
	{
		return splitCount;
	}

	public void setSplitCount(Integer splitCount)
	{
		this.splitCount = splitCount;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getVerificationCode()
	{
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode)
	{
		this.verificationCode = verificationCode;
	}

	public String getQrcode()
	{
		return qrcode;
	}

	public void setQrcode(String qrcode)
	{
		this.qrcode = qrcode;
	}

	public Integer getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(Integer sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}


	public String getServerId() {
		 if(serverId != null && (serverId.length()==0 || serverId.equals("0"))){return null;}else{	return serverId;}
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getCashierId() {
		 if(cashierId != null && (cashierId.length()==0 || cashierId.equals("0"))){return null;}else{	return cashierId;}
	}

	public void setCashierId(String cashierId) {
		this.cashierId = cashierId;
	}

	public String getVoidReasonId()
	{
		 if(voidReasonId != null && (voidReasonId.length()==0 || voidReasonId.equals("0"))){return null;}else{	return voidReasonId;}
	}

	public void setVoidReasonId(String voidReasonId)
	{
		this.voidReasonId = voidReasonId;
	}

	public String getMergedLocationsId()
	{
		 if(mergedLocationsId != null && (mergedLocationsId.length()==0 || mergedLocationsId.equals("0"))){return null;}else{	return mergedLocationsId;}
	}

	public void setMergedLocationsId(String mergedLocationsId)
	{
		this.mergedLocationsId = mergedLocationsId;
	}

	public long getOpenTime()
	{
		if (this.openTime != null)
		{
			return this.openTime.getTime();
		}
		return 0;
	}

	public void setOpenTime(long openTime)
	{
		if (openTime != 0)
		{
			this.openTime = new Date(openTime);
		}

	}

	public long getCloseTime()
	{
		if (this.closeTime != null)
		{
			return this.closeTime.getTime();
		}
		return 0;
	}

	public void setCloseTime(long closeTime)
	{
		if (closeTime != 0)
		{
			this.closeTime = new Date(closeTime);
		}

	}

	public String getTaxName1()
	{
		return taxName1;
	}

	public void setTaxName1(String taxName1)
	{
		this.taxName1 = taxName1;
	}

	public String getTaxName2()
	{
		return taxName2;
	}

	public void setTaxName2(String taxName2)
	{
		this.taxName2 = taxName2;
	}

	public String getTaxName3()
	{
		return taxName3;
	}

	public void setTaxName3(String taxName3)
	{
		this.taxName3 = taxName3;
	}

	public String getTaxName4()
	{
		return taxName4;
	}

	public void setTaxName4(String taxName4)
	{
		this.taxName4 = taxName4;
	}

	public String getTaxDisplayName1()
	{
		return taxDisplayName1;
	}

	public void setTaxDisplayName1(String taxDisplayName1)
	{
		this.taxDisplayName1 = taxDisplayName1;
	}

	public String getTaxDisplayName2()
	{
		return taxDisplayName2;
	}

	public void setTaxDisplayName2(String taxDisplayName2)
	{
		this.taxDisplayName2 = taxDisplayName2;
	}

	public String getTaxDisplayName3()
	{
		return taxDisplayName3;
	}

	public void setTaxDisplayName3(String taxDisplayName3)
	{
		this.taxDisplayName3 = taxDisplayName3;
	}

	public String getTaxDisplayName4()
	{
		return taxDisplayName4;
	}

	public void setTaxDisplayName4(String taxDisplayName4)
	{
		this.taxDisplayName4 = taxDisplayName4;
	}

	public BigDecimal getTotalTax()
	{
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax)
	{
		this.totalTax = totalTax;
	}

	public BigDecimal getTaxRate1()
	{
		return taxRate1;
	}

	public void setTaxRate1(BigDecimal taxRate1)
	{
		this.taxRate1 = taxRate1;
	}

	public BigDecimal getTaxRate2()
	{
		return taxRate2;
	}

	public void setTaxRate2(BigDecimal taxRate2)
	{
		this.taxRate2 = taxRate2;
	}

	public BigDecimal getTaxRate3()
	{
		return taxRate3;
	}

	public void setTaxRate3(BigDecimal taxRate3)
	{
		this.taxRate3 = taxRate3;
	}

	public BigDecimal getTaxRate4()
	{
		return taxRate4;
	}

	public void setTaxRate4(BigDecimal taxRate4)
	{
		this.taxRate4 = taxRate4;
	}

	public String getDiscountDisplayName()
	{
		return discountDisplayName;
	}

	public void setDiscountDisplayName(String discountDisplayName)
	{
		this.discountDisplayName = discountDisplayName;
	}

	public int getIsGratuityApplied()
	{
		return isGratuityApplied;
	}

	public void setIsGratuityApplied(int isGratuityApplied)
	{
		this.isGratuityApplied = isGratuityApplied;
	}

	/**
	 * @return the roundOffTotal
	 */
	public BigDecimal getRoundOffTotal()
	{
		return roundOffTotal;
	}

	/**
	 * @param roundOffTotal
	 *            the roundOffTotal to set
	 */
	public void setRoundOffTotal(BigDecimal roundOffTotal)
	{
		this.roundOffTotal = roundOffTotal;
	}

	public int getIsTabOrder()
	{
		return isTabOrder;
	}

	public void setIsTabOrder(int isTabOrder)
	{
		this.isTabOrder = isTabOrder;
	}

	/**
	 * @return the isOrderReopened
	 */
	public int getIsOrderReopened()
	{
		return isOrderReopened;
	}

	/**
	 * @param isOrderReopened
	 *            the isOrderReopened to set
	 */
	public void setIsOrderReopened(int isOrderReopened)
	{
		this.isOrderReopened = isOrderReopened;
	}

	public String getServername()
	{
		return servername;
	}

	public String getCashierName()
	{
		return cashierName;
	}

	public String getVoidReasonName()
	{
		return voidReasonName;
	}

	public void setServername(String servername)
	{
		this.servername = servername;
	}

	public void setCashierName(String cashierName)
	{
		this.cashierName = cashierName;
	}

	public void setVoidReasonName(String voidReasonName)
	{
		this.voidReasonName = voidReasonName;
	}

	public String getScheduleDateTime()
	{
		return scheduleDateTime;
	}

	public void setScheduleDateTime(String scheduleDateTime)
	{
		this.scheduleDateTime = scheduleDateTime;
	}

	/**
	 * @return the taxExemptId
	 */
	public String getTaxExemptId()
	{
		return taxExemptId;
	}

	/**
	 * @param taxExemptId
	 *            the taxExemptId to set
	 */
	public void setTaxExemptId(String taxExemptId)
	{
		this.taxExemptId = taxExemptId;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public String getNirvanaXpBatchNumber() {
		return nirvanaXpBatchNumber;
	}

	public void setNirvanaXpBatchNumber(String nirvanaXpBatchNumber) {
		this.nirvanaXpBatchNumber = nirvanaXpBatchNumber;
	}

	
	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getIsSeatWiseOrder() {
		return isSeatWiseOrder;
	}

	public void setIsSeatWiseOrder(int isSeatWiseOrder) {
		this.isSeatWiseOrder = isSeatWiseOrder;
	}

	public BigDecimal getCalculatedDiscountValue() {
		return calculatedDiscountValue;
	}

	public void setCalculatedDiscountValue(BigDecimal calculatedDiscountValue) {
		this.calculatedDiscountValue = calculatedDiscountValue;
	}
	
	
	public String getMergeOrderId() {
		 if(mergeOrderId != null && (mergeOrderId.length()==0 || mergeOrderId.equals("0"))){return null;}else{	return mergeOrderId;}
	}

	public void setMergeOrderId(String mergeOrderId) {
		this.mergeOrderId = mergeOrderId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getTaxNo() {
		return taxNo;
	}

	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}

	public String getTaxDisplayName() {
		return taxDisplayName;
	}

	public void setTaxDisplayName(String taxDisplayName) {
		this.taxDisplayName = taxDisplayName;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

	public BigDecimal getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(BigDecimal deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	
	public BigDecimal getDeliveryTax() {
		return deliveryTax;
	}

	public void setDeliveryTax(BigDecimal deliveryTax) {
		this.deliveryTax = deliveryTax;
	}
	
	

	public String getDeliveryOptionId() {
		 if(deliveryOptionId != null && (deliveryOptionId.length()==0 || deliveryOptionId.equals("0"))){return null;}else{	return deliveryOptionId;}
	}

	public void setDeliveryOptionId(String deliveryOptionId) {
		this.deliveryOptionId = deliveryOptionId;
	}
	
	

	public BigDecimal getServiceCharges() {
		return serviceCharges;
	}

	public void setServiceCharges(BigDecimal serviceCharges) {
		this.serviceCharges = serviceCharges;
	}


	public String getPreassignedServerId() {
		 if(preassignedServerId != null && (preassignedServerId.length()==0 || preassignedServerId.equals("0"))){return null;}else{	return preassignedServerId;}
	}

	public void setPreassignedServerId(String preassignedServerId) {
		this.preassignedServerId = preassignedServerId;
	}

	public int getOrderTypeId() {
		return orderTypeId;
	}

	public void setOrderTypeId(int orderTypeId) {
		this.orderTypeId = orderTypeId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getTransferredOrderId() {
		 if(transferredOrderId != null && (transferredOrderId.length()==0 || transferredOrderId.equals("0"))){return null;}else{	return transferredOrderId;}
	}

	public void setTransferredOrderId(String transferredOrderId) {
		this.transferredOrderId = transferredOrderId;
	}

	@Override
	public String toString() {
		return "OrderHeaderHistory [id=" + id + ", addressBillingId="
				+ addressBillingId + ", addressShippingId=" + addressShippingId
				+ ", amountPaid=" + amountPaid + ", balanceDue=" + balanceDue
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", discountsId=" + discountsId + ", discountsName="
				+ discountsName + ", discountsTypeId=" + discountsTypeId
				+ ", discountsTypeName=" + discountsTypeName
				+ ", discountsValue=" + discountsValue + ", gratuity="
				+ gratuity + ", ipAddress=" + ipAddress + ", locationsId="
				+ locationsId + ", orderHeaderId=" + orderHeaderId
				+ ", orderSourceId=" + orderSourceId + ", orderStatusId="
				+ orderStatusId + ", pointOfServiceCount="
				+ pointOfServiceCount + ", priceDiscount=" + priceDiscount
				+ ", priceExtended=" + priceExtended + ", priceGratuity="
				+ priceGratuity + ", priceTax1=" + priceTax1 + ", priceTax2="
				+ priceTax2 + ", priceTax3=" + priceTax3 + ", priceTax4="
				+ priceTax4 + ", reservationsId=" + reservationsId
				+ ", serviceTax=" + serviceTax + ", subTotal=" + subTotal
				+ ", total=" + total + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", usersId=" + usersId + ", paymentWaysId="
				+ paymentWaysId + ", splitCount=" + splitCount + ", date="
				+ date + ", verificationCode=" + verificationCode + ", qrcode="
				+ qrcode + ", sessionId=" + sessionId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", serverId="
				+ serverId + ", cashierId=" + cashierId + ", voidReasonId="
				+ voidReasonId + ", taxName1=" + taxName1 + ", taxName2="
				+ taxName2 + ", taxName3=" + taxName3 + ", taxName4="
				+ taxName4 + ", totalTax=" + totalTax + ", taxDisplayName1="
				+ taxDisplayName1 + ", taxDisplayName2=" + taxDisplayName2
				+ ", taxDisplayName3=" + taxDisplayName3 + ", taxDisplayName4="
				+ taxDisplayName4 + ", taxRate1=" + taxRate1 + ", taxRate2="
				+ taxRate2 + ", taxRate3=" + taxRate3 + ", taxRate4="
				+ taxRate4 + ", orderDetailItemsHistories="
				+ orderDetailItemsHistories + ", mergedLocationsId="
				+ mergedLocationsId + ", isGratuityApplied="
				+ isGratuityApplied + ", openTime=" + openTime + ", closeTime="
				+ closeTime + ", discountDisplayName=" + discountDisplayName
				+ ", roundOffTotal=" + roundOffTotal + ", isTabOrder="
				+ isTabOrder + ", isOrderReopened=" + isOrderReopened
				+ ", servername=" + servername + ", cashierName=" + cashierName
				+ ", voidReasonName=" + voidReasonName + ", scheduleDateTime="
				+ scheduleDateTime + ", taxExemptId=" + taxExemptId
				+ ", nirvanaXpBatchNumber=" + nirvanaXpBatchNumber
				+ ", orderNumber=" + orderNumber + ", isSeatWiseOrder="
				+ isSeatWiseOrder + ", calculatedDiscountValue="
				+ calculatedDiscountValue + ", shiftSlotId=" + shiftSlotId
				+ ", priceDiscountItemLevel=" + priceDiscountItemLevel
				+ ", mergeOrderId=" + mergeOrderId + ", companyName="
				+ companyName + ", taxNo=" + taxNo + ", taxDisplayName="
				+ taxDisplayName + ", deliveryCharges=" + deliveryCharges
				+ ", deliveryTax=" + deliveryTax + ", deliveryOptionId="
				+ deliveryOptionId + ", serviceCharges=" + serviceCharges
				+ ", preassignedServerId=" + preassignedServerId
				+ ", localTime=" + localTime + ", poRefrenceNumber="
				+ poRefrenceNumber + ", requestedLocationId="
				+ requestedLocationId + ", orderTypeId=" + orderTypeId
				+ ", driverId=" + driverId + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", eventName=" + eventName
				+ ", transferredOrderId=" + transferredOrderId + "]";
	}





	

	
}