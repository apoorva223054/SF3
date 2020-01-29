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
@Table(name = "order_header_to_sales_tax")
@XmlRootElement(name = "order_header_to_sales_tax")
public class OrderHeaderToSalesTax extends POSNirvanaBaseClassWithBigInt implements Serializable
{



	/**
	 * 
	 */
	private static final long serialVersionUID = -1724164270632363486L;

	@Column(name = "tax_rate")
	private BigDecimal taxRate;
	
	@Column(name = "tax_name")
	private String taxName;
	
	@Column(name = "tax_display_name")
	private String taxDisplayName;
	
	@Column(name = "order_header_id")
	private String orderHeaderId;
	
	@Column(name = "tax_id")
	private String taxId;
	
	@Column(name = "tax_value")
	private BigDecimal taxValue;
	
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


	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}
	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
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
	@Override
	public String toString() {
		return "OrderHeaderToSalesTax [taxRate=" + taxRate + ", taxName="
				+ taxName + ", taxDisplayName=" + taxDisplayName
				+ ", orderHeaderId=" + orderHeaderId + ", taxId=" + taxId
				+ ", taxValue=" + taxValue + "]";
	}
	
	
}
