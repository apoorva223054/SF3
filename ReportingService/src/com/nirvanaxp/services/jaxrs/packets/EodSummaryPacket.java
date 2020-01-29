/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

public class EodSummaryPacket
{
	private String batchId;
	private BigDecimal pointOfServiceCount;
	private BigDecimal orderCount;
	private BigDecimal subTotal;
	private BigDecimal priceDiscount;
	private BigDecimal totalTax;
	private BigDecimal total;
	private BigDecimal gratuity;
	private BigDecimal amountPaid;
	private BigDecimal balanceDue;
	private BigDecimal changeDue;
	private BigDecimal cashAmount;
	private BigDecimal cashTipAmount;
	private BigDecimal totalCash;
	private BigDecimal creditCardAmount;
	private BigDecimal creditCardTipAmount;
	private BigDecimal totalCard;
	private BigDecimal totalCreditTerm;
	private BigDecimal totalOrderDuration;
	private int totalOrders;
	private BigDecimal totalCheque;
	private BigDecimal totalCashRefund;
	private BigDecimal totalCardRefund;
	private BigDecimal totalDeliveryCharge;
	private BigDecimal totalServiceCharge;
	private BigDecimal creditTermTip;
	private BigDecimal nc;
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public BigDecimal getPointOfServiceCount() {
		return pointOfServiceCount;
	}
	public void setPointOfServiceCount(BigDecimal pointOfServiceCount) {
		this.pointOfServiceCount = pointOfServiceCount;
	}
	public BigDecimal getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(BigDecimal orderCount) {
		this.orderCount = orderCount;
	}
	public BigDecimal getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}
	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
	}
	public BigDecimal getTotalTax() {
		return totalTax;
	}
	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public BigDecimal getGratuity() {
		return gratuity;
	}
	public void setGratuity(BigDecimal gratuity) {
		this.gratuity = gratuity;
	}
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}
	public BigDecimal getBalanceDue() {
		return balanceDue;
	}
	public void setBalanceDue(BigDecimal balanceDue) {
		this.balanceDue = balanceDue;
	}
	public BigDecimal getChangeDue() {
		return changeDue;
	}
	public void setChangeDue(BigDecimal changeDue) {
		this.changeDue = changeDue;
	}
	public BigDecimal getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}
	public BigDecimal getCashTipAmount() {
		return cashTipAmount;
	}
	public void setCashTipAmount(BigDecimal cashTipAmount) {
		this.cashTipAmount = cashTipAmount;
	}
	public BigDecimal getTotalCash() {
		return totalCash;
	}
	public void setTotalCash(BigDecimal totalCash) {
		this.totalCash = totalCash;
	}
	public BigDecimal getCreditCardAmount() {
		return creditCardAmount;
	}
	public void setCreditCardAmount(BigDecimal creditCardAmount) {
		this.creditCardAmount = creditCardAmount;
	}
	public BigDecimal getCreditCardTipAmount() {
		return creditCardTipAmount;
	}
	public void setCreditCardTipAmount(BigDecimal creditCardTipAmount) {
		this.creditCardTipAmount = creditCardTipAmount;
	}
	public BigDecimal getTotalCard() {
		return totalCard;
	}
	public void setTotalCard(BigDecimal totalCard) {
		this.totalCard = totalCard;
	}
	public BigDecimal getTotalCreditTerm() {
		return totalCreditTerm;
	}
	public void setTotalCreditTerm(BigDecimal totalCreditTerm) {
		this.totalCreditTerm = totalCreditTerm;
	}
	public BigDecimal getTotalOrderDuration() {
		return totalOrderDuration;
	}
	public void setTotalOrderDuration(BigDecimal totalOrderDuration) {
		this.totalOrderDuration = totalOrderDuration;
	}
	public int getTotalOrders() {
		return totalOrders;
	}
	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
	}
	public BigDecimal getTotalCheque() {
		return totalCheque;
	}
	public void setTotalCheque(BigDecimal totalCheque) {
		this.totalCheque = totalCheque;
	}
	public BigDecimal getTotalCashRefund() {
		return totalCashRefund;
	}
	public void setTotalCashRefund(BigDecimal totalCashRefund) {
		this.totalCashRefund = totalCashRefund;
	}
	public BigDecimal getTotalCardRefund() {
		return totalCardRefund;
	}
	public void setTotalCardRefund(BigDecimal totalCardRefund) {
		this.totalCardRefund = totalCardRefund;
	}
	public BigDecimal getTotalDeliveryCharge() {
		return totalDeliveryCharge;
	}
	public void setTotalDeliveryCharge(BigDecimal totalDeliveryCharge) {
		this.totalDeliveryCharge = totalDeliveryCharge;
	}
	public BigDecimal getTotalServiceCharge() {
		return totalServiceCharge;
	}
	public void setTotalServiceCharge(BigDecimal totalServiceCharge) {
		this.totalServiceCharge = totalServiceCharge;
	}
	public BigDecimal getCreditTermTip() {
		return creditTermTip;
	}
	public void setCreditTermTip(BigDecimal creditTermTip) {
		this.creditTermTip = creditTermTip;
	}
	public BigDecimal getNc() {
		return nc;
	}
	public void setNc(BigDecimal nc) {
		this.nc = nc;
	}
	@Override
	public String toString() {
		return "EodSummaryPacket [batchId=" + batchId + ", pointOfServiceCount=" + pointOfServiceCount + ", orderCount="
				+ orderCount + ", subTotal=" + subTotal + ", priceDiscount=" + priceDiscount + ", totalTax=" + totalTax
				+ ", total=" + total + ", gratuity=" + gratuity + ", amountPaid=" + amountPaid + ", balanceDue="
				+ balanceDue + ", changeDue=" + changeDue + ", cashAmount=" + cashAmount + ", cashTipAmount="
				+ cashTipAmount + ", totalCash=" + totalCash + ", creditCardAmount=" + creditCardAmount
				+ ", creditCardTipAmount=" + creditCardTipAmount + ", totalCard=" + totalCard + ", totalCreditTerm="
				+ totalCreditTerm + ", totalOrderDuration=" + totalOrderDuration + ", totalOrders=" + totalOrders
				+ ", totalCheque=" + totalCheque + ", totalCashRefund=" + totalCashRefund + ", totalCardRefund="
				+ totalCardRefund + ", totalDeliveryCharge=" + totalDeliveryCharge + ", totalServiceCharge="
				+ totalServiceCharge + ", creditTermTip=" + creditTermTip + ", nc=" + nc + "]";
	}

	
}
