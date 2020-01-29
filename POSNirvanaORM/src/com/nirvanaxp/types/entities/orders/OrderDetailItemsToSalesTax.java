package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigInt;
@Entity
@Table(name = "order_detail_items_to_sales_tax")
@XmlRootElement(name = "order_detail_items_to_sales_tax")
public class OrderDetailItemsToSalesTax extends POSNirvanaBaseClassWithBigInt implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7472049546038004765L;

	@Column(name = "tax_rate")
	private BigDecimal taxRate;
	
	@Column(name = "tax_name")
	private String taxName;
	
	
	@Column(name = "tax_display_name")
	private String taxDisplayName;
	
	@Column(name = "order_detail_items_id")
	private String orderDetailItemsId;
	
	@Column(name = "tax_value")
	private BigDecimal taxValue;
	
	@Column(name = "tax_id")
	private String taxId;
	// this column is for mainitaing order_header level tax in order detail items
	@Column(name = "is_order_level_tax")
	private int isOrderLevelTax;
	

	public BigDecimal getTaxValue() {
		return taxValue;
	}
	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
	}
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
	public String getTaxName() {
		return taxName;
	}
	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}
	public String getOrderDetailItemsId() {
		 if(orderDetailItemsId != null && (orderDetailItemsId.length()==0 || orderDetailItemsId.equals("0"))){return null;}else{	return orderDetailItemsId;}
	}
	public void setOrderDetailItemsId(String orderDetailItemsId) {
		this.orderDetailItemsId = orderDetailItemsId;
	}
	public String getTaxDisplayName() {
		return taxDisplayName;
	}
	public void setTaxDisplayName(String taxDisplayName) {
		this.taxDisplayName = taxDisplayName;
	}
	public String getTaxId() {
		 if(taxId != null && (taxId.length()==0 || taxId.equals("0"))){return null;}else{	return taxId;}
	}
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}
	public int getIsOrderLevelTax() {
		return isOrderLevelTax;
	}
	public void setIsOrderLevelTax(int isOrderLevelTax) {
		this.isOrderLevelTax = isOrderLevelTax;
	}
	@Override
	public String toString() {
		return "OrderDetailItemsToSalesTax [taxRate=" + taxRate + ", taxName="
				+ taxName + ", taxDisplayName=" + taxDisplayName
				+ ", orderDetailItemsId=" + orderDetailItemsId + ", taxValue="
				+ taxValue + ", taxId=" + taxId + ", isOrderLevelTax="
				+ isOrderLevelTax + "]";
	}

	
	
}
