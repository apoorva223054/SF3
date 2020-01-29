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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 * The persistent class for the order_detail_items_history database table.
 * 
 */
@Entity
@Table(name = "order_detail_items_history")
@XmlRootElement(name = "order_detail_items_history")
public class OrderDetailItemsHistory implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private BigInteger id;

	@Column(name = "amount_paid", precision = 10, scale = 2)
	private BigDecimal amountPaid;

	@Column(name = "balance_due", precision = 10, scale = 2)
	private BigDecimal balanceDue;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "order_header_id", nullable = false)
	private String orderHeaderId;

	@Column(name = "discount_id", nullable = false)
	private String discountId;

	@Column(precision = 10, scale = 2)
	private BigDecimal gratuity;

	@Column(name = "items_id", nullable = false)
	private String itemsId;

	@Column(name = "items_qty", precision = 10, scale = 2)
	private BigDecimal itemsQty;

	@Column(name = "items_short_name", length = 64)
	private String itemsShortName;

	@Column(name = "order_detail_items_id", nullable = false)
	private String orderDetailItemsId;

	@Column(name = "order_detail_status_id", nullable = false)
	private Integer orderDetailStatusId;

	@Column(name = "point_of_service_num", nullable = false)
	private Integer pointOfServiceNum;

	@Column(name = "price_discount", precision = 10, scale = 2)
	private BigDecimal priceDiscount;

	@Column(name = "price_extended", precision = 10, scale = 2)
	private BigDecimal priceExtended;

	@Column(name = "price_gratuity", precision = 10, scale = 2)
	private BigDecimal priceGratuity;

	@Column(name = "price_msrp", precision = 10, scale = 2)
	private BigDecimal priceMsrp;

	@Column(name = "price_selling", precision = 10, scale = 2)
	private BigDecimal priceSelling;

	@Column(name = "price_tax_1", precision = 10, scale = 2)
	private BigDecimal priceTax1;

	@Column(name = "price_tax_2", precision = 10, scale = 2)
	private BigDecimal priceTax2;

	@Column(name = "price_tax_3", precision = 10, scale = 2)
	private BigDecimal priceTax3;

	@Column(name = "price_tax_4", precision = 10, scale = 2)
	private BigDecimal priceTax4;

	@Column(name = "seat_id")
	private Integer seatId;

	@Column(name = "sent_course_id", nullable = false)
	private String sentCourseId;

	@Column(name = "service_tax", precision = 10, scale = 2)
	private BigDecimal serviceTax;

	@Column(name = "sub_total", precision = 10, scale = 2)
	private BigDecimal subTotal;

	@Column(precision = 10, scale = 2)
	private BigDecimal total;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	// bi-directional many-to-one association to OrderDetailAttribute
	@JsonManagedReference
	@JoinColumn(name = "order_detail_items_history_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<OrderDetailAttributeHistory> orderDetailAttributesHistories;

	// bi-directional many-to-one association to OrderHeader
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "order_header_history_id", nullable = false)
	private OrderHeaderHistory orderHeaderHistories;

	@Column(name = "discount_reason")
	private String discountReason;

	@Column(name = "discount_reason_name")
	private String discountReasonName;

	@Column(name = "discount_name")
	private String discountName;

	@Column(name = "discount_type_id")
	private String discountTypeId;

	@Column(name = "discount_type_name")
	private String discountTypeName;

	@Column(name = "discount_value")
	private int discountValue;

	@Column(name = "discount_display_name")
	private String discountDisplayName;

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

	@Column(name = "recall_reason")
	private String recallReason;

	@Column(name = "recall_reason_name")
	private String recallReasonName;

	@Column(name = "parent_category_id")
	private String parentCategoryId;

	@Column(name = "root_category_id")
	private String rootCategoryId;

	@Column(name = "is_tab_order_item")
	private int isTabOrderItem;

	@Column(name = "tax_exempt_id")
	private String taxExemptId;

	@Column(name = "calculated_discount_value", precision = 10, scale = 2)
	private BigDecimal calculatedDiscountValue;

	@Column(name = "discount_ways_id")
	private int discountWaysId;
	
	@Column(name = "plu")
	private String plu;
	
	@Column(name = "order_header_to_seat_detail_id")
	private BigInteger orderHeaderToSeatDetailId;
	
	@Column(name = "item_transferred_from")
	private String itemTransferredFrom;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "discount_code")
	private String discountCode;
	
	@Column(name = "po_item_refrence_number")
	private String poItemRefrenceNumber;
	
	@Column(name = "delivery_charges")
	private BigDecimal deliveryCharges;
	
	@Column(name = "service_charges")
	private BigDecimal serviceCharges;
	
	@Column(name = "schedule_date_time")
	private String scheduleDateTime;
	
	@Column(name = "driver_id")
	private String driverId;

	public String getPoItemRefrenceNumber()
	{
		 if(poItemRefrenceNumber != null && (poItemRefrenceNumber.length()==0 || poItemRefrenceNumber.equals("0"))){return null;}else{	return poItemRefrenceNumber;}
	}

	public void setPoItemRefrenceNumber(String poItemRefrenceNumber)
	{
		this.poItemRefrenceNumber = poItemRefrenceNumber;
	}

	
	public String getDiscountCode()
	{
		return discountCode;
	}

	public void setDiscountCode(String discountCode)
	{
		this.discountCode = discountCode;
	}
	


	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public int getDiscountWaysId() {
		return discountWaysId;
	}

	public void setDiscountWaysId(int discountWaysId) {
		this.discountWaysId = discountWaysId;
	}

	public OrderDetailItemsHistory()
	{
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getOrderDetailItemsId() {
		 if(orderDetailItemsId != null && (orderDetailItemsId.length()==0 || orderDetailItemsId.equals("0"))){return null;}else{	return orderDetailItemsId;}
	}

	public void setOrderDetailItemsId(String orderDetailItemsId) {
		this.orderDetailItemsId = orderDetailItemsId;
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

	public String getDiscountId()
	{
		 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	return discountId;}}
	}

	public void setDiscountId(String discountId)
	{
		this.discountId = discountId;
	}

	public BigDecimal getTaxRate3()
	{
		return taxRate3;
	}

	public void setTaxRate3(BigDecimal taxRate3)
	{
		this.taxRate3 = taxRate3;
	}

	public BigDecimal getTotalTax()
	{
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax)
	{
		this.totalTax = totalTax;
	}

	public BigDecimal getGratuity()
	{
		return this.gratuity;
	}

	public void setGratuity(BigDecimal gratuity)
	{
		this.gratuity = gratuity;
	}

	public String getItemsId()
	{
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}

	public void setItemsId(String itemsId)
	{
		this.itemsId = itemsId;
	}

	public BigDecimal getItemsQty()
	{
		return this.itemsQty;
	}

	public void setItemsQty(BigDecimal itemsQty)
	{
		this.itemsQty = itemsQty;
	}

	public String getItemsShortName()
	{
		return this.itemsShortName;
	}

	public void setItemsShortName(String itemsShortName)
	{
		this.itemsShortName = itemsShortName;
	}


	public Integer getOrderDetailStatusId()
	{
		return this.orderDetailStatusId;
	}

	public void setOrderDetailStatusId(Integer orderDetailStatusId)
	{
		this.orderDetailStatusId = orderDetailStatusId;
	}

	public Integer getPointOfServiceNum()
	{
		return this.pointOfServiceNum;
	}

	public void setPointOfServiceNum(Integer pointOfServiceNum)
	{
		this.pointOfServiceNum = pointOfServiceNum;
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

	public BigDecimal getPriceMsrp()
	{
		return this.priceMsrp;
	}

	public void setPriceMsrp(BigDecimal priceMsrp)
	{
		this.priceMsrp = priceMsrp;
	}

	public BigDecimal getPriceSelling()
	{
		return this.priceSelling;
	}

	public void setPriceSelling(BigDecimal priceSelling)
	{
		this.priceSelling = priceSelling;
	}

	public BigDecimal getPriceTax1()
	{
		return this.priceTax1;
	}

	public void setPriceTax1(BigDecimal priceTax1)
	{
		this.priceTax1 = priceTax1;
	}

	public BigDecimal getTaxRate1()
	{
		return taxRate1;
	}

	public void setTaxRate1(BigDecimal taxRate1)
	{
		this.taxRate1 = taxRate1;
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

	public BigDecimal getPriceTax4()
	{
		return priceTax4;
	}

	public void setPriceTax4(BigDecimal priceTax4)
	{
		this.priceTax4 = priceTax4;
	}

	public BigDecimal getTaxRate2()
	{
		return taxRate2;
	}

	public void setTaxRate2(BigDecimal taxRate2)
	{
		this.taxRate2 = taxRate2;
	}

	public BigDecimal getTaxRate4()
	{
		return taxRate4;
	}

	public void setTaxRate4(BigDecimal taxRate4)
	{
		this.taxRate4 = taxRate4;
	}

	public Integer getSeatId()
	{
		return this.seatId;
	}

	public void setSeatId(Integer seatId)
	{
		this.seatId = seatId;
	}

	public String getSentCourseId()
	{
		 if(sentCourseId != null && (sentCourseId.length()==0 || sentCourseId.equals("0"))){return null;}else{	return sentCourseId;}
	}

	public void setSentCourseId(String sentCourseId)
	{
		this.sentCourseId = sentCourseId;
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

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public Set<OrderDetailAttributeHistory> getOrderDetailAttributesHistories()
	{
		return orderDetailAttributesHistories;
	}

	public void setOrderDetailAttributesHistories(Set<OrderDetailAttributeHistory> orderDetailAttributesHistories)
	{
		this.orderDetailAttributesHistories = orderDetailAttributesHistories;
	}

	public OrderHeaderHistory getOrderHeaderHistories()
	{
		return orderHeaderHistories;
	}

	public void setOrderHeaderHistories(OrderHeaderHistory orderHeaderHistories)
	{
		this.orderHeaderHistories = orderHeaderHistories;
	}

	public String getDiscountReason()
	{
		 if(discountReason != null && (discountReason.length()==0 || discountReason.equals("0"))){return null;}else{	return discountReason;}
	}

	public void setDiscountReason(String discountReason)
	{
		this.discountReason = discountReason;
	}

	public String getDiscountReasonName()
	{
		return discountReasonName;
	}

	public void setDiscountReasonName(String discountReasonName)
	{
		this.discountReasonName = discountReasonName;
	}

	public String getDiscountName()
	{
		return discountName;
	}

	public void setDiscountName(String discountName)
	{
		this.discountName = discountName;
	}

	public String getDiscountTypeId()
	{
		 if(discountTypeId != null && (discountTypeId.length()==0 || discountTypeId.equals("0"))){return null;}else{	return discountTypeId;}
	}

	public void setDiscountTypeId(String discountTypeId)
	{
		this.discountTypeId = discountTypeId;
	}

	public String getDiscountTypeName()
	{
		return discountTypeName;
	}

	public void setDiscountTypeName(String discountTypeName)
	{
		this.discountTypeName = discountTypeName;
	}

	public int getDiscountValue()
	{
		return discountValue;
	}

	public void setDiscountValue(int discountValue)
	{
		this.discountValue = discountValue;
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

	/**
	 * @return the recallReason
	 */
	public String getRecallReason()
	{
		 if(recallReason != null && (recallReason.length()==0 || recallReason.equals("0"))){return null;}else{	return recallReason;}
	}

	/**
	 * @param recallReason
	 *            the recallReason to set
	 */
	public void setRecallReason(String recallReason)
	{
		this.recallReason = recallReason;
	}

	/**
	 * @return the recallReasonName
	 */
	public String getRecallReasonName()
	{
		return recallReasonName;
	}

	/**
	 * @param recallReasonName
	 *            the recallReasonName to set
	 */
	public void setRecallReasonName(String recallReasonName)
	{
		this.recallReasonName = recallReasonName;
	}

 

	public String getParentCategoryId() {
		 if(parentCategoryId != null && (parentCategoryId.length()==0 || parentCategoryId.equals("0"))){return null;}else{	return parentCategoryId;}
	}

	public void setParentCategoryId(String parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public String getRootCategoryId() {
		 if(rootCategoryId != null && (rootCategoryId.length()==0 || rootCategoryId.equals("0"))){return null;}else{	return rootCategoryId;}
	}

	public void setRootCategoryId(String rootCategoryId) {
		this.rootCategoryId = rootCategoryId;
	}

	public int getIsTabOrderItem()
	{
		return isTabOrderItem;
	}

	public void setIsTabOrderItem(int isTabOrderItem)
	{
		this.isTabOrderItem = isTabOrderItem;
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
	public BigDecimal getCalculatedDiscountValue() {
		return calculatedDiscountValue;
	}

	public void setCalculatedDiscountValue(BigDecimal calculatedDiscountValue) {
		this.calculatedDiscountValue = calculatedDiscountValue;
	}
	
	public String getPlu()
	{
		return plu;
	}

	public void setPlu(String plu)
	{
		this.plu = plu;
	}

	public String getItemTransferredFrom() {
		return itemTransferredFrom;
	}

	public void setItemTransferredFrom(String itemTransferredFrom) {
		this.itemTransferredFrom = itemTransferredFrom;
	}


	public BigInteger getOrderHeaderToSeatDetailId() {
		return orderHeaderToSeatDetailId;
	}

	public void setOrderHeaderToSeatDetailId(BigInteger orderHeaderToSeatDetailId) {
		this.orderHeaderToSeatDetailId = orderHeaderToSeatDetailId;
	}

	public BigDecimal getDeliveryCharges() {
		return deliveryCharges;
	}

	public void setDeliveryCharges(BigDecimal deliveryCharges) {
		this.deliveryCharges = deliveryCharges;
	}

	public BigDecimal getServiceCharges() {
		return serviceCharges;
	}

	public void setServiceCharges(BigDecimal serviceCharges) {
		this.serviceCharges = serviceCharges;
	}

	public String getScheduleDateTime() {
		return scheduleDateTime;
	}

	public void setScheduleDateTime(String scheduleDateTime) {
		this.scheduleDateTime = scheduleDateTime;
	}

	public String getDriverId() {
		 if(driverId != null && (driverId.length()==0 || driverId.equals("0"))){return null;}else{	return driverId;}
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	@Override
	public String toString() {
		return "OrderDetailItemsHistory [id=" + id + ", amountPaid="
				+ amountPaid + ", balanceDue=" + balanceDue + ", created="
				+ created + ", createdBy=" + createdBy + ", orderHeaderId="
				+ orderHeaderId + ", discountId=" + discountId + ", gratuity="
				+ gratuity + ", itemsId=" + itemsId + ", itemsQty=" + itemsQty
				+ ", itemsShortName=" + itemsShortName
				+ ", orderDetailItemsId=" + orderDetailItemsId
				+ ", orderDetailStatusId=" + orderDetailStatusId
				+ ", pointOfServiceNum=" + pointOfServiceNum
				+ ", priceDiscount=" + priceDiscount + ", priceExtended="
				+ priceExtended + ", priceGratuity=" + priceGratuity
				+ ", priceMsrp=" + priceMsrp + ", priceSelling=" + priceSelling
				+ ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2
				+ ", priceTax3=" + priceTax3 + ", priceTax4=" + priceTax4
				+ ", seatId=" + seatId + ", sentCourseId=" + sentCourseId
				+ ", serviceTax=" + serviceTax + ", subTotal=" + subTotal
				+ ", total=" + total + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", orderDetailAttributesHistories="
				+ orderDetailAttributesHistories + ", orderHeaderHistories="
				+ orderHeaderHistories + ", discountReason=" + discountReason
				+ ", discountReasonName=" + discountReasonName
				+ ", discountName=" + discountName + ", discountTypeId="
				+ discountTypeId + ", discountTypeName=" + discountTypeName
				+ ", discountValue=" + discountValue + ", discountDisplayName="
				+ discountDisplayName + ", taxName1=" + taxName1
				+ ", taxName2=" + taxName2 + ", taxName3=" + taxName3
				+ ", taxName4=" + taxName4 + ", taxDisplayName1="
				+ taxDisplayName1 + ", taxDisplayName2=" + taxDisplayName2
				+ ", taxDisplayName3=" + taxDisplayName3 + ", taxDisplayName4="
				+ taxDisplayName4 + ", taxRate1=" + taxRate1 + ", taxRate2="
				+ taxRate2 + ", taxRate3=" + taxRate3 + ", taxRate4="
				+ taxRate4 + ", totalTax=" + totalTax + ", roundOffTotal="
				+ roundOffTotal + ", recallReason=" + recallReason
				+ ", recallReasonName=" + recallReasonName
				+ ", parentCategoryId=" + parentCategoryId
				+ ", rootCategoryId=" + rootCategoryId + ", isTabOrderItem="
				+ isTabOrderItem + ", taxExemptId=" + taxExemptId
				+ ", calculatedDiscountValue=" + calculatedDiscountValue
				+ ", discountWaysId=" + discountWaysId + ", plu=" + plu
				+ ", orderHeaderToSeatDetailId=" + orderHeaderToSeatDetailId
				+ ", itemTransferredFrom=" + itemTransferredFrom
				+ ", localTime=" + localTime + ", discountCode=" + discountCode
				+ ", poItemRefrenceNumber=" + poItemRefrenceNumber
				+ ", deliveryCharges=" + deliveryCharges + ", serviceCharges="
				+ serviceCharges + ", scheduleDateTime=" + scheduleDateTime
				+ ", driverId=" + driverId + "]";
	}





	

	

	


}