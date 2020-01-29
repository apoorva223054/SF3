/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.address.Address;
import com.nirvanaxp.types.entities.user.UsersToDiscount;

/**
 * The persistent class for the order_header database table.
 * 
 */
@Entity
@Table(name = "order_header")
@XmlRootElement(name = "order_header")
public class OrderHeader implements Serializable
{
	

	private static final Logger logger = Logger.getLogger(OrderHeader.class.getName());

	private static final long serialVersionUID = 1L;
 
	@Id
	@Column(unique = true, nullable = false)
	private String id;

	// uni-directional many-to-one association to ContactPreference
	@ManyToOne(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "address_billing_id")
	private Address addressBilling;

	// uni-directional many-to-one association to ContactPreference
	@ManyToOne(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "address_shipping_id")
	private Address addressShipping;

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

	@Column(name = "discount_display_name", length = 128)
	private String discountDisplayName;

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

	@Column(name = "order_source_id", nullable = false)
	private String orderSourceId;
	
	private transient String orderSourceGroupName;

	@Column(name = "order_status_id", nullable = false)
	private String orderStatusId;

	@Column(name = "point_of_service_count")
	private int pointOfServiceCount;

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

	@Column(name = "split_count")
	private Integer splitCount;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "payment_ways_id")
	private Integer paymentWaysId;

	@Column(name = "date")
	private String date;

	@Column(name = "schedule_date_time")
	private String scheduleDateTime;

	@Column(name = "verification_code")
	private String verificationCode;

	@Column(name = "qrcode")
	private String qrcode;

	@Column(name = "session_id")
	private Integer sessionKey;

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

	@Column(name = "tax_name_1")
	private String taxName1;

	@Column(name = "tax_name_2")
	private String taxName2;

	@Column(name = "tax_name_3")
	private String taxName3;

	@Column(name = "tax_name_4")
	private String taxName4;

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

	@Column(name = "total_tax")
	private BigDecimal totalTax;

	@Column(name = "round_off_total")
	private BigDecimal roundOffTotal;

	@Column(name = "is_tab_order")
	private int isTabOrder;

	@Column(name = "is_order_reopened")
	private int isOrderReopened;

	@Column(name = "shift_slot_id")
	private int shiftSlotId;
	
	@Column(name = "price_discount_item_level", precision = 10, scale = 2)
	private BigDecimal priceDiscountItemLevel;
	

	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "tax_no")
	private String taxNo;
	
	@Column(name = "tax_display_name")
	private String taxDisplayName;
	
	@Column(name = "delivery_charges")
	private BigDecimal deliveryCharges;
	
	@Column(name = "service_charges")
	private BigDecimal serviceCharges;
	
	@Column(name = "delivery_option_id")
	private String deliveryOptionId;
	
	@Column(name = "delivery_tax")
	private BigDecimal deliveryTax;	
	
	@Column(name = "preassigned_server_id")
	private String preassignedServerId;
	
	@Column(name = "requested_location_id")
	private String requestedLocationId;
	
	@Column(name = "order_type_id")
	private int orderTypeId;

	@Column(name = "comment")
	private String comment;

	
	@Column(name = "transferred_order_id")
	private String transferredOrderId;
	
	@Column(name = "start_date")
	private String startDate;

	@Column(name = "end_date")
	private String endDate;

	@Column(name = "event_name")
	private String eventName;

	private transient List<OrderPaymentDetail> orderPaymentDetailsList;

	private transient List<OrderDetailItem> orderDetailItemsList;
	private transient UsersToDiscount usersToDiscounts;
	
	
	public UsersToDiscount getUsersToDiscounts() {
		return usersToDiscounts;
	}

	public void setUsersToDiscounts(UsersToDiscount usersToDiscounts) {
		this.usersToDiscounts = usersToDiscounts;
	}

	private transient List<OrderHeaderToSalesTax> orderHeaderToSalesTax;
	private transient List<OrderHeaderToSeatDetail> orderHeaderToSeatDetails;

	
	public String getComment()
	{
		return this.comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public BigDecimal getPriceDiscountItemLevel() {
		
		if(priceDiscountItemLevel == null)
		{
			return new BigDecimal(0);
		}
		return priceDiscountItemLevel;
	}

	public void setPriceDiscountItemLevel(BigDecimal priceDiscountItemLevel) {
		if (priceDiscountItemLevel != null && priceDiscountItemLevel != new BigDecimal(0))
		{
			this.priceDiscountItemLevel = priceDiscountItemLevel.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
		this.priceDiscountItemLevel = priceDiscountItemLevel;
		}
	}

	public int getShiftSlotId() {
		return shiftSlotId;
	}

	public void setShiftSlotId(int shiftSlotId) {
		this.shiftSlotId = shiftSlotId;
	}

	public List<OrderHeaderToSeatDetail> getOrderHeaderToSeatDetails()
	{
		return orderHeaderToSeatDetails;
	}

	public void setOrderHeaderToSeatDetails(List<OrderHeaderToSeatDetail> orderHeaderToSeatDetails)
	{
		this.orderHeaderToSeatDetails = orderHeaderToSeatDetails;
	}

	private transient boolean isPartySizeUpdated;

	private transient String serverName;

	private transient String locationName;
	
	private transient String orderSourceGroupId;

	private transient String sectionId;

	private transient String inventoryPostPacket;

	// to determine whether packet is for refund /// in updatePayment validation
	// of duplicate payment // By Apoorva
	private transient Integer isRefundedPacket;

	@Column(name = "server_name")
	private String servername;

	@Column(name = "cashier_name")
	private String cashierName;

	@Column(name = "void_reason_name")
	private String voidReasonName;

	@Column(name = "tax_exempt_id")
	private String taxExemptId;

	@Column(name = "refrence_number")
	private String referenceNumber;

	@Column(name = "nirvanaxp_batch_number")
	private String nirvanaXpBatchNumber;

	@Column(name = "order_number")
	private String orderNumber;

	@Column(name = "is_seat_wise_order")
	private int isSeatWiseOrder;

	@Column(name = "calculated_discount_value", precision = 10, scale = 2)
	private BigDecimal calculatedDiscountValue;
	
	@Column(name = "merge_order_id")
	private String mergeOrderId;

	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "po_refrence_number")
	private String poRefrenceNumber;
	
	@Column(name = "driver_id")
	private String driverId;
	
	



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
	

	public BigDecimal getDeliveryTax() {
		return deliveryTax;
	}

	public void setDeliveryTax(BigDecimal deliveryTax) {
		this.deliveryTax = deliveryTax;
	}
	
	public String getOrderSourceGroupId() {
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceGroupId) {
		this.orderSourceGroupId = orderSourceGroupId;
	}

	public String getSectionId() {
		 if(sectionId != null && (sectionId.length()==0 || sectionId.equals("0"))){return null;}else{	return sectionId;}
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public OrderHeader()
	{
	}

	public OrderHeader(String id, Address addressBilling, Address addressShipping, BigDecimal amountPaid, BigDecimal balanceDue, Date created, String createdBy, String discountsId,
			String discountsName, String discountsTypeId, String discountsTypeName, BigDecimal discountsValue, BigDecimal gratuity, String ipAddress, String orderSourceId, String orderStatusId,
			int pointOfServiceCount, BigDecimal priceDiscount, BigDecimal priceExtended, BigDecimal priceGratuity, BigDecimal priceTax, BigDecimal priceTax1, BigDecimal priceTax2,
			BigDecimal priceTax3, String reservationsId, BigDecimal serviceTax, BigDecimal subTotal, BigDecimal total, Integer splitCount, Date updated, String updatedBy, String usersId,
			String locationsId, Integer paymentWaysId, String date, String verificationCode, String qrcode, int sessionKey, String firstName, String lastName, String serverId, String cashierId,
			String voidReasonId, String mergedLocations, long openTime, long closeTime, String discountDisplayName, String taxDisplayName1, String taxDisplayName2, String taxDisplayName3,
			String taxDisplayName4, BigDecimal taxRate1, BigDecimal taxRate2, BigDecimal taxRate3, BigDecimal taxRate4, BigDecimal totalTax, String taxName1, String taxName2, String taxName3,
			String taxName4, int isGratuityApplied, BigDecimal roundOffTotal, int isTabOrder, String serverName, String cahierName, String voidReasonName, int isOrderReopened, String taxExemptId,
			String scheduleDateTime, String referenceNumber, String nirvanaXpBatchNumber, String orderNumber, int isSeatWiseOrder, BigDecimal calculatedDiscountValue, int shiftSlotId,BigDecimal priceDiscountItemLevel
			,String mergeOrderId,String companyName,String taxNo, String taxDisplayName, BigDecimal deliveryCharges, 
			BigDecimal deliveryTax, String deliveryOptionId, BigDecimal serviceCharges, String preassignedServerId,String localTime,
			String poRefrenceNumber, String requestedLocationId,int orderTypeId, String driverId, String comment, String startDate
			, String endDate, String eventName)

	{

		super();
		this.id = id;
		this.addressBilling = addressBilling;
		this.addressShipping = addressShipping;
		this.amountPaid = amountPaid;
		this.balanceDue = balanceDue;
		this.createdBy = createdBy;
		this.discountsId = discountsId;
		this.discountsName = discountsName;
		this.discountsTypeId = discountsTypeId;
		this.discountsTypeName = discountsTypeName;
		this.discountsValue = discountsValue;
		this.gratuity = gratuity;
		this.ipAddress = ipAddress;
		this.orderSourceId = orderSourceId;
		this.orderStatusId = orderStatusId;
		this.pointOfServiceCount = pointOfServiceCount;
		this.priceDiscount = priceDiscount;
		this.priceExtended = priceExtended;
		this.priceGratuity = priceGratuity;
		this.priceTax1 = priceTax;
		this.priceTax2 = priceTax1;
		this.priceTax3 = priceTax2;
		this.priceTax4 = priceTax3;
		this.reservationsId = reservationsId;
		this.serviceTax = serviceTax;
		this.subTotal = subTotal;
		this.total = total;
		this.splitCount = splitCount;
		this.updatedBy = updatedBy;
		this.usersId = usersId;
		this.locationsId = locationsId;
		this.paymentWaysId = paymentWaysId;
		this.date = date;
		setCreated(created);
		setUpdated(updated);
		this.verificationCode = verificationCode;
		this.qrcode = qrcode;
		this.sessionKey = sessionKey;
		this.firstName = firstName;
		this.lastName = lastName;
		this.serverId = serverId;
		this.cashierId = cashierId;
		this.voidReasonId = voidReasonId;
		this.mergedLocationsId = mergedLocations;
		setOpenTime(openTime);
		setCloseTime(closeTime);
		this.discountDisplayName = discountDisplayName;
		this.taxDisplayName1 = taxDisplayName1;
		this.taxDisplayName2 = taxDisplayName2;
		this.taxDisplayName3 = taxDisplayName3;
		this.taxDisplayName4 = taxDisplayName4;
		this.taxRate1 = taxRate1;
		this.taxRate2 = taxRate2;
		this.taxRate3 = taxRate3;
		this.taxRate4 = taxRate4;
		this.totalTax = totalTax;
		this.taxName1 = taxName1;
		this.taxName2 = taxName2;
		this.taxName3 = taxName3;
		this.taxName4 = taxName4;
		this.isGratuityApplied = isGratuityApplied;
		this.roundOffTotal = roundOffTotal;
		this.isTabOrder = isTabOrder;
		this.isOrderReopened = isOrderReopened;
		this.servername = serverName;
		this.cashierName = cahierName;
		this.voidReasonName = voidReasonName;
		this.taxExemptId = taxExemptId;
		this.scheduleDateTime = scheduleDateTime;
		this.referenceNumber = referenceNumber;
		this.nirvanaXpBatchNumber = nirvanaXpBatchNumber;
		this.orderNumber = orderNumber;
		this.isSeatWiseOrder = isSeatWiseOrder;
		this.calculatedDiscountValue = calculatedDiscountValue;
		this.shiftSlotId = shiftSlotId;
		this.priceDiscountItemLevel=priceDiscountItemLevel;
		this.mergeOrderId = mergeOrderId; 
		
		this.companyName = companyName;
		this.taxNo = taxNo;
		this.taxDisplayName = taxDisplayName; 
		this.deliveryOptionId = deliveryOptionId; 
		this.deliveryCharges = deliveryCharges; 
		this.deliveryTax = deliveryTax; 
		this.serviceCharges	=	serviceCharges;
		this.preassignedServerId = preassignedServerId;
		this.localTime = localTime;
		this.poRefrenceNumber = poRefrenceNumber;
		this.requestedLocationId = requestedLocationId;
		this.orderTypeId=orderTypeId;
		this.driverId = driverId;
		this.comment = comment;
		this.startDate =startDate;
		this.endDate = endDate;
		this.eventName = eventName;
	
	}
 
	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public Address getAddressBilling()
	{
		return this.addressBilling;
	}

	public void setAddressBilling(Address addressBilling)
	{
		this.addressBilling = addressBilling;
	}

	public Address getAddressShipping()
	{
		return this.addressShipping;
	}

	public void setAddressShipping(Address addressShipping)
	{
		this.addressShipping = addressShipping;
	}

	public BigDecimal getAmountPaid()
	{
		return this.amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid)
	{
		if (amountPaid != null && amountPaid != new BigDecimal(0))
		{
			this.amountPaid = amountPaid.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.amountPaid = amountPaid;
		}
	}

	public BigDecimal getBalanceDue()
	{
		return this.balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue)
	{
		if (balanceDue != null && balanceDue != new BigDecimal(0))
		{
			this.balanceDue = balanceDue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.balanceDue = balanceDue;
		}
	}

	public String getDiscountsId()
	{
		 // if(discountsId != null && (discountsId.length()==0 || discountsId.equals("0"))){return null;}else{	return discountsId;}
		return discountsId;
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
		// if(discountsTypeId != null && (discountsTypeId.length()==0 || discountsTypeId.equals("0"))){return null;}else{	return discountsTypeId;}
		return discountsTypeId;
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
		if (discountsValue != null && discountsValue != new BigDecimal(0))
		{
			this.discountsValue = discountsValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.discountsValue = discountsValue;
		}

	}

	public BigDecimal getGratuity()
	{
		return this.gratuity;
	}

	public void setGratuity(BigDecimal gratuity)
	{
		if (gratuity != null && gratuity != new BigDecimal(0))
		{
			this.gratuity = gratuity.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.gratuity = gratuity;
		}
	}

	public String getIpAddress()
	{
		return this.ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getOrderSourceId()
	{
		 if(orderSourceId != null && (orderSourceId.length()==0 || orderSourceId.equals("0"))){return null;}else{	return orderSourceId;}
	}

	public void setOrderSourceId(String orderSourceId)
	{
		this.orderSourceId = orderSourceId;
	}

 

	public String getOrderStatusId() {
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}

	public int getPointOfServiceCount()
	{
		return this.pointOfServiceCount;
	}

	public void setPointOfServiceCount(int pointOfServiceCount)
	{
		this.pointOfServiceCount = pointOfServiceCount;
	}

	public BigDecimal getPriceDiscount()
	{
		if(priceDiscount == null)
		{
			return new BigDecimal(0);
		}
		return this.priceDiscount;
	}

	public void setPriceDiscount(BigDecimal priceDiscount)
	{
		if (priceDiscount != null && priceDiscount != new BigDecimal(0))
		{
			this.priceDiscount = priceDiscount.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.priceDiscount = priceDiscount;
		}
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
		if (priceGratuity != null && priceGratuity != new BigDecimal(0))
		{
			this.priceGratuity = priceGratuity.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.priceGratuity = priceGratuity;
		}
	}

	public BigDecimal getPriceTax4()
	{
		if(priceTax4 == null)
		{
			return new BigDecimal(0);
		}
		return priceTax4;
	}

	public void setPriceTax4(BigDecimal priceTax4)
	{
		if (priceTax4 != null && priceTax4 != new BigDecimal(0))
		{
			this.priceTax4 = priceTax4.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.priceTax4 = priceTax4;
		}
	}

	public BigDecimal getPriceTax1()
	{
		if(priceTax1 == null)
		{
			return new BigDecimal(0);
		}
		return this.priceTax1;
	}

	public void setPriceTax1(BigDecimal priceTax1)
	{
		if (priceTax1 != null && priceTax1 != new BigDecimal(0))
		{
			this.priceTax1 = priceTax1.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.priceTax1 = priceTax1;
		}
	}

	public BigDecimal getPriceTax2()
	{
		if(priceTax2 == null)
		{
			return new BigDecimal(0);
		}
		return this.priceTax2;
	}

	public void setPriceTax2(BigDecimal priceTax2)
	{
		if (priceTax2 != null && priceTax2 != new BigDecimal(0))
		{
			this.priceTax2 = priceTax2.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.priceTax2 = priceTax2;
		}
	}

	public BigDecimal getPriceTax3()
	{
		if(priceTax3 == null)
		{
			return new BigDecimal(0);
		}
		return this.priceTax3;
	}

	public void setPriceTax3(BigDecimal priceTax3)
	{
		if (priceTax3 != null && priceTax3 != new BigDecimal(0))
		{
			this.priceTax3 = priceTax3.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.priceTax3 = priceTax3;
		}
	}

 

	 

	public String getReservationsId() {
		if(reservationsId != null && (reservationsId.length()==0 || reservationsId.equals("0"))){return null;}else{	return reservationsId;}
	}

	public void setReservationsId(String reservationsId) {
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
		if(subTotal == null )
		{
			return new BigDecimal(0);
		}
		return this.subTotal;
	}

	public void setSubTotal(BigDecimal subTotal)
	{
		if (subTotal != null && subTotal != new BigDecimal(0))
		{
			this.subTotal = subTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.subTotal = subTotal;
		}
	}

	public BigDecimal getTotal()
	{
		return this.total;
	}

	public void setTotal(BigDecimal total)
	{
		if (total != null && total != new BigDecimal(0))
		{
			this.total = total.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.total = total;
		}
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

	public List<OrderDetailItem> getOrderDetailItems()
	{
		return this.orderDetailItemsList;
	}

	public void setOrderDetailItems(List<OrderDetailItem> orderDetailItems)
	{
		this.orderDetailItemsList = orderDetailItems;
	}

	public List<OrderPaymentDetail> getOrderPaymentDetails()
	{
		return this.orderPaymentDetailsList;
	}

	public void setOrderPaymentDetails(List<OrderPaymentDetail> orderPaymentDetails)
	{
		this.orderPaymentDetailsList = orderPaymentDetails;
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
	
	

	 

	public String getDeliveryOptionId() {
		 if(deliveryOptionId != null && (deliveryOptionId.length()==0 || deliveryOptionId.equals("0"))){return null;}else{	return deliveryOptionId;}
	}

	public void setDeliveryOptionId(String deliveryOptionId) {
		this.deliveryOptionId = deliveryOptionId;
	}

	public void setOrderHeaderByResultSet(Object[] rs, int row) throws Exception
	{
		int index = 0;
		this.setId((String) rs[index]);
		index++;
		this.setIpAddress((String) rs[index]);
		index++;
		this.setReservationsId((String) rs[index]);
		index++;
		this.setUsersId((String) rs[index]);
		index++;
		this.setOrderStatusId((String) rs[index]);
		index++;
		this.setOrderSourceId((String) rs[index]);
		index++;
		this.setLocationsId((String) rs[index]);
		index++;
		if (rs[index] != null)
		{
			this.setAddressShipping((Address) rs[index]);
		}
		index++;
		if (rs[index] != null)
		{
			this.setAddressBilling((Address) rs[index]);
		}
		index++;
		this.setPointOfServiceCount((Integer) rs[index]);
		index++;
		this.setPriceExtended((BigDecimal) rs[index]);
		index++;
		this.setPriceGratuity((BigDecimal) rs[index]);
		index++;
		this.setPriceTax1((BigDecimal) rs[index]);
		index++;
		this.setPriceTax2((BigDecimal) rs[index]);
		index++;
		this.setPriceTax3((BigDecimal) rs[index]);
		index++;
		this.setPriceTax4((BigDecimal) rs[index]);
		index++;
		if ((String) rs[index] != null)
		{
			this.setDiscountsName((String) rs[index]);
		}
		index++;
		this.setDiscountsTypeId((String) rs[index]);
		index++;
		if ((String) rs[index] != null)
		{
			this.setDiscountsTypeName((String) rs[index]);
		}
		index++;
		this.setDiscountsValue((BigDecimal) rs[index]);
		index++;
		this.setDiscountsId((String) rs[index]);
		index++;
		this.setPaymentWaysId((Integer) rs[index]);
		index++;
		this.setSplitCount((Integer) rs[index]);
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setPriceDiscount((BigDecimal) rs[index]);
		}
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setServiceTax((BigDecimal) rs[index]);
		}
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setSubTotal((BigDecimal) rs[index]);
		}
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setGratuity((BigDecimal) rs[index]);
		}
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setTotal((BigDecimal) rs[index]);
		}
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setAmountPaid((BigDecimal) rs[index]);
		}
		index++;
		if ((BigDecimal) rs[index] != null)
		{
			this.setBalanceDue((BigDecimal) rs[index]);
		}
		index++;

		this.setCreated(new Date(((Timestamp) rs[index]).getTime()));

		index++;
		this.setCreatedBy((String) rs[index]);
		index++;

		this.setUpdated(new Date(((Timestamp) rs[index]).getTime()));

		index++;
		this.setUpdatedBy((String) rs[index]);
		index++;

		if (((String) rs[index]) != null)
		{
			if (((String) rs[index]).contains("-") && ((String) rs[index]).length() < 10)
			{
				try
				{
					// this string has one 0 missing
					String dateArr[] = ((String) rs[index]).split("-");
					if (dateArr[1].length() == 1)
					{
						dateArr[1] = "0" + dateArr[1];
					}
					if (dateArr[2].length() == 1)
					{
						dateArr[2] = "0" + dateArr[2];
					}
					String date = dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2];
					this.setDate(date);
				}
				catch (Exception e)
				{
					this.setDate((String) rs[index]);
					logger.log(Level.SEVERE, "Error while setting date on order header: " + e.getMessage(), e);
				}
			}
			else
			{
				this.setDate((String) rs[index]);
			}

		}

		index++;
		// naman 36 -> 35
		if ((String) rs[index] != null)
		{
			this.setVerificationCode((String) rs[index]);
		}
		index++;
		if ((String) rs[index] != null)
		{
			this.setQrcode((String) rs[index]);
		}
		index++;
		if ((String) rs[index] != null)
		{
			this.setFirstName((String) rs[index]);
		}
		index++;
		if ((String) rs[index] != null)
		{
			this.setLastName((String) rs[index]);
		}
		index++;
		if ((String) rs[index] != null)
		{
			this.setServerId((String) rs[index]);
		}
		index++;
		if ((String) rs[index] != null)
		{
			this.setCashierId((String) rs[index]);
		}
		index++;
		if ((String) rs[index] != null)
		{
			this.setVoidReasonId((String) rs[index]);
		}
		index++;
		if ((Timestamp) rs[index] != null)
		{
			if ((((Timestamp) rs[index])).getTime() != 0)
			{
				this.setOpenTime((((Timestamp) rs[index])).getTime());
			}
		}
		index++;
		if (((Timestamp) rs[index]) != null)
		{
			if ((((Timestamp) rs[index])).getTime() != 0)
			{
				this.setCloseTime((((Timestamp) rs[index])).getTime());
			}
		}
		index++;
		this.setTaxName1((String) rs[index]);
		index++;
		this.setTaxName2((String) rs[index]);
		index++;
		this.setTaxName3((String) rs[index]);
		index++;
		this.setTaxName4((String) rs[index]);
		index++;
		this.setTaxDisplayName1((String) rs[index]);
		index++;
		this.setTaxDisplayName2((String) rs[index]);
		index++;
		this.setTaxDisplayName3((String) rs[index]);
		index++;
		this.setTaxDisplayName4((String) rs[index]);
		index++;
		this.setTaxRate1((BigDecimal) rs[index]);
		index++;
		this.setTaxRate2((BigDecimal) rs[index]);
		index++;
		this.setTaxRate3((BigDecimal) rs[index]);
		index++;
		this.setTaxRate4((BigDecimal) rs[index]);
		index++;
		this.setTotalTax((BigDecimal) rs[index]);
		index++;
		this.setIsGratuityApplied((Integer) rs[index]);
		index++;
		this.setRoundOffTotal((BigDecimal) rs[index]);
		index++;
		this.setIsTabOrder((Integer) rs[index]);
		index++;
		this.setSessionKey((Integer) rs[index]);
		index++;
		this.setIsOrderReopened((Integer) rs[index]);
		index++;
		this.setServername((String) rs[index]);
		index++;
		this.setCashierName((String) rs[index]);
		index++;
		this.setVoidReasonName((String) rs[index]);
		index++;
		this.setTaxExemptId((String) rs[index]);
		index++;
		this.setScheduleDateTime((String) rs[index]);
		index++;
		this.setNirvanaXpBatchNumber((String) rs[index]);
		index++;
		this.setOrderNumber((String) rs[index]);
		index++;
		this.setIsSeatWiseOrder((int) rs[index]);
		index++;

		if( rs[index]!=null){
			this.setCalculatedDiscountValue((BigDecimal) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setShiftSlotId((int) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setPriceDiscountItemLevel((BigDecimal) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setMergeOrderId((String) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setRequestedLocationId((String) rs[index]);
		}
		index++;
		
		if( rs[index]!=null){
			this.setPoRefrenceNumber((String) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setOrderTypeId((int) rs[index]);
		}
		
		index++;
		if( rs[index]!=null){
			this.setDriverId((String) rs[index]);
		}
		index++;

		if( rs[index]!=null){
			this.setComment((String) rs[index]);
		}
		index++;
		
		if( rs[index]!=null){
			this.setStartDate((String) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setEndDate((String) rs[index]);
		}
		index++;
		if( rs[index]!=null){
			this.setEventName((String) rs[index]);
		}
		index++;
		
		if(rs[index]!=null)
		{
			this.setDeliveryCharges(((BigDecimal) rs[index]));
		}
			
		index++;


	}

	public void setOrderHeaderByResultSetForPayment(ResultSet rs, int index) throws SQLException
	{

		index = 73;
		this.setLocationsId(rs.getString(index));
		index++;
		this.setId( rs.getString(index));
		index++;
		this.setCreatedBy((rs.getString(index)));
		index++;
		if (rs.getBigDecimal(index) != null)
		{
			this.setSubTotal(rs.getBigDecimal(index));
		}
		index++;
		if (rs.getBigDecimal(index) != null)
		{
			this.setPriceDiscount(rs.getBigDecimal(index));
		}

		index++;
		if (rs.getBigDecimal(index) != null)
		{
			this.setServiceTax(rs.getBigDecimal(index));
		}

		index++;
		if (rs.getBigDecimal(index) != null)
		{
			this.setTotal(rs.getBigDecimal(index));
		}

		index++;
		if (rs.getBigDecimal(index) != null)
		{
			this.setGratuity(rs.getBigDecimal(index));
		}

		index++;
		if (rs.getBigDecimal(index) != null)
		{
			this.setBalanceDue(rs.getBigDecimal(index));
		}

		index++;
		this.setPointOfServiceCount(rs.getInt(index));

	}

	public void setOrderHeaderByResultSetAllValue(Object[] objRow, int index)
	{
		
		setId(((String) objRow[1 + index]));
		setIpAddress(((String) objRow[2 + index]));
		setReservationsId(((String) objRow[3 + index]));
		setUsersId(((String) objRow[4 + index]));
		setOrderStatusId(((String) objRow[5 + index]));
		setOrderSourceId(((String) objRow[6 + index]));
		setLocationsId(((String) objRow[7 + index]));
		setAddressShipping(((Address) objRow[8 + index]));
		setAddressBilling(((Address) objRow[9 + index]));
		setPointOfServiceCount(((Integer) objRow[10 + index]));
		setPriceExtended(((BigDecimal) objRow[11 + index]));
		setPriceGratuity(((BigDecimal) objRow[12 + index]));
		setPriceTax1(((BigDecimal) objRow[13 + index]));
		setPriceTax2(((BigDecimal) objRow[14 + index]));
		setPriceTax3(((BigDecimal) objRow[15 + index]));
		setPriceTax4(((BigDecimal) objRow[16 + index]));
		setDiscountsName(((String) objRow[17 + index]));
		setDiscountsTypeId(((String) objRow[18 + index]));
		setDiscountsTypeName(((String) objRow[19 + index]));
		setDiscountsValue(((BigDecimal) objRow[20 + index]));
		setDiscountsId(((String) objRow[21 + index]));
		setPaymentWaysId(((Integer) objRow[22 + index]));
		setSplitCount(((Integer) objRow[23 + index]));
		setPriceDiscount(((BigDecimal) objRow[24 + index]));
		setServiceTax(((BigDecimal) objRow[25 + index]));
		setSubTotal(((BigDecimal) objRow[26 + index]));
		setGratuity(((BigDecimal) objRow[27 + index]));
		setTotal(((BigDecimal) objRow[28 + index]));
		setAmountPaid(((BigDecimal) objRow[29 + index]));
		setBalanceDue(((BigDecimal) objRow[30 + index]));
		if (((Timestamp) objRow[31 + index]) != null)
		{
			setCreated(new Date(((Timestamp) objRow[31 + index]).getTime()));
		}

		setCreatedBy(((String) objRow[32 + index]));
		if (((Timestamp) objRow[33 + index]) != null)
		{
			setUpdated(new Date(((Timestamp) objRow[33 + index]).getTime()));
		}

		setUpdatedBy(((String) objRow[34 + index]));
		setDate(((String) objRow[35 + index]));
		setVerificationCode(((String) objRow[36 + index]));
		setQrcode(((String) objRow[37 + index]));
		setFirstName(((String) objRow[38 + index]));
		setLastName(((String) objRow[39 + index]));
		setServerId(((String) objRow[40 + index]));
		setCashierId(((String) objRow[41 + index]));
		setVoidReasonId(((String) objRow[42 + index]));
		if (((Timestamp) objRow[43 + index]) != null)
		{
			setOpenTime(((Timestamp) objRow[43 + index]).getTime());
		}
		if (((Timestamp) objRow[44 + index]) != null)
		{
			setCloseTime(((Timestamp) objRow[44 + index]).getTime());
		}
		setTaxName1(((String) objRow[45 + index]));
		setTaxName2(((String) objRow[46 + index]));
		setTaxName3(((String) objRow[47 + index]));
		setTaxName4(((String) objRow[48 + index]));
		setTaxDisplayName1((String) objRow[49 + index]);
		setTaxDisplayName2((String) objRow[50 + index]);
		setTaxDisplayName3((String) objRow[51 + index]);
		setTaxDisplayName4((String) objRow[52 + index]);
		setTaxRate1(((BigDecimal) objRow[53 + index]));
		setTaxRate2(((BigDecimal) objRow[54 + index]));
		setTaxRate3(((BigDecimal) objRow[55 + index]));
		setTaxRate4(((BigDecimal) objRow[56 + index]));
		setTotalTax(((BigDecimal) objRow[57 + index]));
		setIsGratuityApplied(((Integer) objRow[58 + index]));
		setRoundOffTotal(((BigDecimal) objRow[59 + index]));
		setSessionKey(((Integer) objRow[60 + index]));
		setTaxExemptId((String) objRow[61 + index]);
		setScheduleDateTime((String) objRow[62 + index]);
		setServerName((String) objRow[63 + index]);
		setCashierName((String) objRow[64 + index]);
		setLocationName((String) objRow[65 + index]);
		setNirvanaXpBatchNumber((String) objRow[66 + index]);
		setOrderNumber((String) objRow[67 + index]);
		setPriceDiscountItemLevel(((BigDecimal) objRow[68 + index]));
		if(objRow[69 + index]!=null)
			setMergeOrderId(((String) objRow[69 + index]));
		if(objRow[70 + index] != null)
			setDeliveryCharges(((BigDecimal) objRow[70 + index]));
		if(objRow[71 + index]!=null)
			setDeliveryOptionId(((String) objRow[71 + index]));
		if(objRow[72 + index]!=null)
			setDeliveryTax(((BigDecimal) objRow[72 + index]));
			setOrderSourceGroupId(((String) objRow[73 + index]));	
		
			setSectionId(((String) objRow[74 + index]));	
		
		if((objRow[75 + index]) != null)
		{
			setPreassignedServerId(((String) objRow[75 + index]));	
		}
		
		if((objRow[76 + index]) != null)
		{
			setServiceCharges(((BigDecimal) objRow[76 + index]));	
		}
		
		if((objRow[77 + index]) != null)
		  {
		   setLocalTime(((String) objRow[77 + index])); 
		  }		
		
		
	}

	public boolean isPartySizeUpdated()
	{
		return isPartySizeUpdated;
	}

	public void setPartySizeUpdated(boolean isPartySizeUpdated)
	{
		this.isPartySizeUpdated = isPartySizeUpdated;
	}

	public Integer getSessionKey()
	{
		if (sessionKey == null)
		{
			return 0;
		}
		return sessionKey;
	}

	public void setSessionKey(Integer sessionKey)
	{
		if (sessionKey == null)
		{
			this.sessionKey = 0;
		}
		else
		{
			this.sessionKey = sessionKey;
		}
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
		 return mergedLocationsId;
	}

	public void setMergedLocationsId(String mergedLocationsId)
	{
		this.mergedLocationsId = mergedLocationsId;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
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

	public String getDiscountDisplayName()
	{
		return discountDisplayName;
	}

	public void setDiscountDisplayName(String discountDisplayName)
	{
		this.discountDisplayName = discountDisplayName;
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

	public BigDecimal getTotalTax()
	{
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax)
	{
		if (totalTax != null && totalTax != new BigDecimal(0))
		{
			this.totalTax = totalTax.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.totalTax = totalTax;
		}
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
		if (roundOffTotal != null && roundOffTotal != new BigDecimal(0))
		{
			this.roundOffTotal = roundOffTotal.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.roundOffTotal = roundOffTotal;
		}
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

	public String getInventoryPostPacket()
	{
		return inventoryPostPacket;
	}

	public void setInventoryPostPacket(String inventoryPostPacket)
	{
		this.inventoryPostPacket = inventoryPostPacket;
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

	public String getTaxExemptId()
	{
		return taxExemptId;
	}

	public void setTaxExemptId(String taxExemptId)
	{
		this.taxExemptId = taxExemptId;
	}

	public String getScheduleDateTime()
	{
		return scheduleDateTime;
	}

	public void setScheduleDateTime(String scheduleDateTime)
	{
		this.scheduleDateTime = scheduleDateTime;
	}

	public String getReferenceNumber()
	{
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber)
	{
		this.referenceNumber = referenceNumber;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public void setLocationName(String locationName)
	{
		this.locationName = locationName;
	}

	public Integer getIsRefundedPacket()
	{
		return isRefundedPacket;
	}

	public void setIsRefundedPacket(Integer isRefundedPacket)
	{
		this.isRefundedPacket = isRefundedPacket;
	}

	public String getNirvanaXpBatchNumber()
	{
		return nirvanaXpBatchNumber;
	}

	public void setNirvanaXpBatchNumber(String nirvanaXpBatchNumber)
	{
		this.nirvanaXpBatchNumber = nirvanaXpBatchNumber;
	}

	

	public int getIsSeatWiseOrder()
	{
		return isSeatWiseOrder;
	}

	public void setIsSeatWiseOrder(int isSeatWiseOrder)
	{
		this.isSeatWiseOrder = isSeatWiseOrder;
	}

	public BigDecimal getCalculatedDiscountValue()
	{
		return calculatedDiscountValue;
	}

	public void setCalculatedDiscountValue(BigDecimal calculatedDiscountValue)
	{
		if (calculatedDiscountValue != null && calculatedDiscountValue != new BigDecimal(0))
		{
			this.calculatedDiscountValue = calculatedDiscountValue.setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}
		else
		{
			this.calculatedDiscountValue = calculatedDiscountValue;
		}
	}


	public String getMergeOrderId() {
		 if(mergeOrderId != null && (mergeOrderId.length()==0 || mergeOrderId.equals("0"))){return null;}else{	return mergeOrderId;}
	}

	public void setMergeOrderId(String mergeOrderId) {
		this.mergeOrderId = mergeOrderId;
	}

	public List<OrderHeaderToSalesTax> getOrderHeaderToSalesTax()
	{
		return orderHeaderToSalesTax;
	}

	public void setOrderHeaderToSalesTax(List<OrderHeaderToSalesTax> orderHeaderToSalesTax)
	{
		this.orderHeaderToSalesTax = orderHeaderToSalesTax;
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
	
	public BigDecimal getDeliveryCharges() {
		return deliveryCharges;
	}
	public void setDeliveryCharges(BigDecimal deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}
	
	public BigDecimal getServiceCharges() {
		/*if(serviceCharges == null )
		{
			return new BigDecimal(0);
		}*/
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

	public String getOrderSourceGroupName() {
		return orderSourceGroupName;
	}

	public void setOrderSourceGroupName(String orderSourceGroupName) {
		this.orderSourceGroupName = orderSourceGroupName;
	}

	public String getRequestedLocationId()
	{
		 if(requestedLocationId != null && (requestedLocationId.length()==0 || requestedLocationId.equals("0"))){return null;}else{	return requestedLocationId;}
	}

	public void setRequestedLocationId(String requestedLocationId)
	{
		this.requestedLocationId = requestedLocationId;
	}

	public int getOrderTypeId() {
		if(orderTypeId == 0)
		{
			orderTypeId = 1;
		}
		return orderTypeId;
	}

	public void setOrderTypeId(int orderTypeId) {
		
		if(orderTypeId == 0)
		{
			this.orderTypeId = 1;
		}else
		{
			this.orderTypeId = orderTypeId;
		}
		
	}

	public String getTransferredOrderId() {
		 if(transferredOrderId != null && (transferredOrderId.length()==0 || transferredOrderId.equals("0"))){return null;}else{	return transferredOrderId;}
	}

	public void setTransferredOrderId(String transferredOrderId) {
		this.transferredOrderId = transferredOrderId;
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

	public List<OrderPaymentDetail> getOrderPaymentDetailsList() {
		return orderPaymentDetailsList;
	}

	public void setOrderPaymentDetailsList(
			List<OrderPaymentDetail> orderPaymentDetailsList) {
		this.orderPaymentDetailsList = orderPaymentDetailsList;
	}

	public List<OrderDetailItem> getOrderDetailItemsList() {
		return orderDetailItemsList;
	}

	public void setOrderDetailItemsList(List<OrderDetailItem> orderDetailItemsList) {
		this.orderDetailItemsList = orderDetailItemsList;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	 

	public String getId() {
		if(id != null && (id.length()==0 || id.equals("0"))){
			return null;
		}else{
			return id;
		}
	 
	}

	public void setId(String id) {
		this.id = id;
	}

 

	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String toString() {
		return "OrderHeader [id=" + id + ", addressBilling=" + addressBilling
				+ ", addressShipping=" + addressShipping + ", amountPaid="
				+ amountPaid + ", balanceDue=" + balanceDue + ", created="
				+ created + ", createdBy=" + createdBy + ", discountsId="
				+ discountsId + ", discountsName=" + discountsName
				+ ", discountDisplayName=" + discountDisplayName
				+ ", discountsTypeId=" + discountsTypeId
				+ ", discountsTypeName=" + discountsTypeName
				+ ", discountsValue=" + discountsValue + ", gratuity="
				+ gratuity + ", ipAddress=" + ipAddress + ", orderSourceId="
				+ orderSourceId + ", orderStatusId=" + orderStatusId
				+ ", pointOfServiceCount=" + pointOfServiceCount
				+ ", priceDiscount=" + priceDiscount + ", priceExtended="
				+ priceExtended + ", priceGratuity=" + priceGratuity
				+ ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2
				+ ", priceTax3=" + priceTax3 + ", priceTax4=" + priceTax4
				+ ", reservationsId=" + reservationsId + ", serviceTax="
				+ serviceTax + ", subTotal=" + subTotal + ", total=" + total
				+ ", splitCount=" + splitCount + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", usersId=" + usersId
				+ ", locationsId=" + locationsId + ", paymentWaysId="
				+ paymentWaysId + ", date=" + date + ", scheduleDateTime="
				+ scheduleDateTime + ", verificationCode=" + verificationCode
				+ ", qrcode=" + qrcode + ", sessionKey=" + sessionKey
				+ ", firstName=" + firstName + ", lastName=" + lastName
				+ ", serverId=" + serverId + ", cashierId=" + cashierId
				+ ", voidReasonId=" + voidReasonId + ", mergedLocationsId="
				+ mergedLocationsId + ", isGratuityApplied="
				+ isGratuityApplied + ", openTime=" + openTime + ", closeTime="
				+ closeTime + ", taxName1=" + taxName1 + ", taxName2="
				+ taxName2 + ", taxName3=" + taxName3 + ", taxName4="
				+ taxName4 + ", taxDisplayName1=" + taxDisplayName1
				+ ", taxDisplayName2=" + taxDisplayName2 + ", taxDisplayName3="
				+ taxDisplayName3 + ", taxDisplayName4=" + taxDisplayName4
				+ ", taxRate1=" + taxRate1 + ", taxRate2=" + taxRate2
				+ ", taxRate3=" + taxRate3 + ", taxRate4=" + taxRate4
				+ ", totalTax=" + totalTax + ", roundOffTotal=" + roundOffTotal
				+ ", isTabOrder=" + isTabOrder + ", isOrderReopened="
				+ isOrderReopened + ", shiftSlotId=" + shiftSlotId
				+ ", priceDiscountItemLevel=" + priceDiscountItemLevel
				+ ", companyName=" + companyName + ", taxNo=" + taxNo
				+ ", taxDisplayName=" + taxDisplayName + ", deliveryCharges="
				+ deliveryCharges + ", serviceCharges=" + serviceCharges
				+ ", deliveryOptionId=" + deliveryOptionId + ", deliveryTax="
				+ deliveryTax + ", preassignedServerId=" + preassignedServerId
				+ ", requestedLocationId=" + requestedLocationId
				+ ", orderTypeId=" + orderTypeId + ", comment=" + comment
				+ ", transferredOrderId=" + transferredOrderId + ", startDate="
				+ startDate + ", endDate=" + endDate + ", eventName="
				+ eventName + ", servername=" + servername + ", cashierName="
				+ cashierName + ", voidReasonName=" + voidReasonName
				+ ", taxExemptId=" + taxExemptId + ", referenceNumber="
				+ referenceNumber + ", nirvanaXpBatchNumber="
				+ nirvanaXpBatchNumber + ", orderNumber=" + orderNumber
				+ ", isSeatWiseOrder=" + isSeatWiseOrder
				+ ", calculatedDiscountValue=" + calculatedDiscountValue
				+ ", mergeOrderId=" + mergeOrderId + ", localTime=" + localTime
				+ ", poRefrenceNumber=" + poRefrenceNumber + ", driverId="
				+ driverId + "]";
	}






	
	
	
	

}