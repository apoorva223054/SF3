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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonBackReference;

/**
 * The persistent class for the order_detail_attribute_history database table.
 * 
 */
@Entity
@Table(name = "order_detail_attribute_history")
@XmlRootElement(name = "order_detail_attribute_history")
public class OrderDetailAttributeHistory implements Serializable
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

	@Column(precision = 10, scale = 2)
	private BigDecimal gratuity;

	@Column(name = "items_attribute_id")
	private String itemsAttributeId;
	

	@Column(name = "items_id")
	private String itemsId;	
	
	@Column(name = "item_qty")
	private int itemQty;
	
	@Column(name = "items_attribute_name", length = 64)
	private String itemsAttributeName;

	@Column(name = "order_detail_attribute_id")
	private String orderDetailAttributeId;

	@Column(name = "order_detail_status_id", nullable = false)
	private Integer orderDetailStatusId;

	@Column(name = "order_detail_items_id")
	private String orderDetailItemsId;

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

	@Column(name = "price_tax", precision = 10, scale = 2)
	private BigDecimal priceTax;

	@Column(name = "price_tax_1", precision = 10, scale = 2)
	private BigDecimal priceTax1;

	@Column(name = "price_tax_2", precision = 10, scale = 2)
	private BigDecimal priceTax2;

	@Column(name = "price_tax_3", precision = 10, scale = 2)
	private BigDecimal priceTax3;

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

	@Column(name = "round_off_total")
	private BigDecimal roundOffTotal;
	
	@Column(name = "plu")
	private String plu;

	// bi-directional many-to-one association to OrderDetailItem
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "order_detail_items_history_id", nullable = false)
	private OrderDetailItemsHistory orderDetailItemsHistories;

	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public OrderDetailAttributeHistory()
	{
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

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

	}


	public BigDecimal getGratuity()
	{
		return this.gratuity;
	}

	public void setGratuity(BigDecimal gratuity)
	{
		this.gratuity = gratuity;
	}

	public String getItemsAttributeId()
	{
		 if(itemsAttributeId != null && (itemsAttributeId.length()==0 || itemsAttributeId.equals("0"))){return null;}else{	return itemsAttributeId;}
	}

	public void setItemsAttributeId(String itemsAttributeId)
	{
		this.itemsAttributeId = itemsAttributeId;
	}

	public String getItemsAttributeName()
	{
		return this.itemsAttributeName;
	}

	public void setItemsAttributeName(String itemsAttributeName)
	{
		this.itemsAttributeName = itemsAttributeName;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getOrderDetailAttributeId() {
		 if(orderDetailAttributeId != null && (orderDetailAttributeId.length()==0 || orderDetailAttributeId.equals("0"))){return null;}else{	return orderDetailAttributeId;}
	}

	public void setOrderDetailAttributeId(String orderDetailAttributeId) {
		this.orderDetailAttributeId = orderDetailAttributeId;
	}

	public String getOrderDetailItemsId() {
		 if(orderDetailItemsId != null && (orderDetailItemsId.length()==0 || orderDetailItemsId.equals("0"))){return null;}else{	return orderDetailItemsId;}
	}

	public void setOrderDetailItemsId(String orderDetailItemsId) {
		this.orderDetailItemsId = orderDetailItemsId;
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

	public BigDecimal getPriceTax()
	{
		return this.priceTax;
	}

	public void setPriceTax(BigDecimal priceTax)
	{
		this.priceTax = priceTax;
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

	public long getUpdated()
	{
		if (this.updated != null)
		{
			return this.updated.getTime();
		}
		return 0;
	}
	
	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
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

	public OrderDetailItemsHistory getOrderDetailItemsHistories()
	{
		return orderDetailItemsHistories;
	}

	public void setOrderDetailItemsHistories(OrderDetailItemsHistory orderDetailItemsHistories)
	{
		this.orderDetailItemsHistories = orderDetailItemsHistories;
	}

	public Integer getOrderDetailStatusId()
	{
		return orderDetailStatusId;
	}

	public void setOrderDetailStatusId(Integer orderDetailStatusId)
	{
		this.orderDetailStatusId = orderDetailStatusId;
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
	
	public String getPlu()
	{
		return plu;
	}

	public void setPlu(String plu)
	{
		this.plu = plu;
	}
	
	

	public String getItemsId() {
		 if(itemsId != null && (itemsId.length()==0 || itemsId.equals("0"))){return null;}else{	return itemsId;}
	}
	
	public int getItemQty() {
		return itemQty;
	}

	public void setItemQty(int itemQty) {
		this.itemQty = itemQty;
	}

	public void setItemsId(String itemsId) {
		this.itemsId = itemsId;
	}

	@Override
	public String toString()
	{
		return "OrderDetailAttributeHistory [id=" + id + ", amountPaid=" + amountPaid + ", balanceDue=" + balanceDue + ", created=" + created + ", createdBy=" + createdBy + ", gratuity=" + gratuity
				+ ", itemsAttributeId=" + itemsAttributeId + ", itemsAttributeName=" + itemsAttributeName + ", orderDetailAttributeId=" + orderDetailAttributeId + ", orderDetailStatusId="
				+ orderDetailStatusId + ", orderDetailItemsId=" + orderDetailItemsId + ", priceDiscount=" + priceDiscount + ", priceExtended=" + priceExtended + ", priceGratuity=" + priceGratuity
				+ ", priceMsrp=" + priceMsrp + ", priceSelling=" + priceSelling + ", priceTax=" + priceTax + ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2 + ", priceTax3=" + priceTax3
				+ ", serviceTax=" + serviceTax + ", subTotal=" + subTotal + ", total=" + total + ", updated=" + updated + ", updatedBy=" + updatedBy + ", roundOffTotal=" + roundOffTotal + ", plu="
				+ plu + ", itemsId=" + itemsId + ", itemQty=" + itemQty + ", orderDetailItemsHistories=" + orderDetailItemsHistories + 
				 ", localTime=" + localTime +"]";
	}

	

}