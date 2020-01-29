package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedIdWithoutStatus;
@Entity
@Table(name = "order_header_to_seat_detail")
@XmlRootElement(name = "order_header_to_seat_detail")
public class OrderHeaderToSeatDetail extends POSNirvanaBaseClassWithBigIntWithGeneratedIdWithoutStatus implements Serializable
{



	/**
	 * 
	 */
	private static final long serialVersionUID = -2433408044705190138L;


	@Column(name = "seat_id")
	private String seatId;
	
	
	@Column(name = "discount_id")
	private String discountId;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "balance_due")
	private BigDecimal balanceDue;
	
	@Column(name = "amount_paid")
	private BigDecimal amountPaid;
	
	@Column(name = "total")
	private BigDecimal total;
	
	@Column(name = "total_tax")
	private BigDecimal totalTax;
	
	@Column(name = "price_discount")
	private BigDecimal priceDiscount;
	
	@Column(name = "discount_display_name")
	private String discountDisplayName;
	
	@Column(name = "discount_name")
	private String discountName;
	
	@Column(name = "discount_value")
	private BigDecimal discountValue;
	
	@Column(name = "calculated_discount_value")
	private BigDecimal calculatedDiscountValue;
	
	@Column(name = "sub_total")
	private BigDecimal subTotal;
	
	@Column(name = "price_gratuity")
	private BigDecimal priceGratuity;
	
	
	@Column(name = "order_header_id")
	private String orderHeaderId;


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
	
	public String getDiscountId() {
		 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	return discountId;}
	}


	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public BigDecimal getBalanceDue() {
		return balanceDue;
	}


	public void setBalanceDue(BigDecimal balanceDue) {
		this.balanceDue = balanceDue;
	}


	public BigDecimal getAmountPaid() {
		return amountPaid;
	}


	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}


	public BigDecimal getTotal() {
		return total;
	}


	public void setTotal(BigDecimal total) {
		this.total = total;
	}


	public BigDecimal getTotalTax() {
		return totalTax;
	}


	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}


	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}


	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
	}


	public String getDiscountDisplayName() {
		return discountDisplayName;
	}


	public void setDiscountDisplayName(String discountDisplayName) {
		this.discountDisplayName = discountDisplayName;
	}


	public String getDiscountName() {
		return discountName;
	}


	public void setDiscountName(String discountName) {
		this.discountName = discountName;
	}


	public BigDecimal getDiscountValue() {
		return discountValue;
	}


	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}


	public BigDecimal getCalculatedDiscountValue() {
		return calculatedDiscountValue;
	}


	public void setCalculatedDiscountValue(BigDecimal calculatedDiscountValue) {
		this.calculatedDiscountValue = calculatedDiscountValue;
	}


	public BigDecimal getSubTotal() {
		return subTotal;
	}


	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}


	public BigDecimal getPriceGratuity() {
		return priceGratuity;
	}


	public void setPriceGratuity(BigDecimal priceGratuity) {
		this.priceGratuity = priceGratuity;
	}




	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public String getSeatId() {
		return seatId;
	}


	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}


	@Override
	public String toString() {
		return "OrderHeaderToSeatDetail [seatId=" + seatId + ", discountId="
				+ discountId + ", userId=" + userId + ", balanceDue="
				+ balanceDue + ", amountPaid=" + amountPaid + ", total="
				+ total + ", totalTax=" + totalTax + ", priceDiscount="
				+ priceDiscount + ", discountDisplayName="
				+ discountDisplayName + ", discountName=" + discountName
				+ ", discountValue=" + discountValue
				+ ", calculatedDiscountValue=" + calculatedDiscountValue
				+ ", subTotal=" + subTotal + ", priceGratuity=" + priceGratuity
				+ ", orderHeaderId=" + orderHeaderId +  ", localTime=" + localTime +"]";
	}

	
}
