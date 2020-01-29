package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutStatus;
@Entity
@Table(name = "order_payment_details_to_sales_tax")
@XmlRootElement(name = "order_payment_details_to_sales_tax")
public class OrderPaymentDetailsToSalesTax extends POSNirvanaBaseClassWithoutStatus implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 629220891234051223L;

	@Column(name = "tax_rate")
	private BigDecimal taxRate;
	
	@Column(name = "tax_name")
	private String taxName;
	
	@Column(name = "tax_display_name")
	private String taxDisplayName;
	
	@Column(name = "order_payment_details_id")
	private String orderPaymentDetailsId;
	
	@Column(name = "tax_value")
	private BigDecimal taxValue;
	
	@Column(name = "tax_id")
	private String taxId;
	
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

	public String getOrderPaymentDetailsId() {
		if(orderPaymentDetailsId != null && (orderPaymentDetailsId.length()==0 || orderPaymentDetailsId.equals("0"))){return null;}else{	return orderPaymentDetailsId;}
	}
	public void setOrderPaymentDetailsId(String orderPaymentDetailsId) {
		this.orderPaymentDetailsId = orderPaymentDetailsId;
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
		return "OrderPaymentDetailsToSalesTax [taxRate=" + taxRate
				+ ", taxName=" + taxName + ", taxDisplayName=" + taxDisplayName
				+ ", orderPaymentDetailsId=" + orderPaymentDetailsId
				+ ", taxValue=" + taxValue + ", taxId=" + taxId + "]";
	}
	

}
