/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets.receiptPacket;

import java.util.List;

import com.nirvanaxp.services.jaxrs.packets.PaidInOutCash;
import com.nirvanaxp.services.jaxrs.packets.RevenueByCategoryPacket;
import com.nirvanaxp.services.jaxrs.packets.RevenueByDiscountPacket;
import com.nirvanaxp.services.jaxrs.packets.RevenueByReportingCategoryPacket;
import com.nirvanaxp.services.jaxrs.packets.RevenueByTax;
import com.nirvanaxp.services.jaxrs.packets.RevenueByVoidOrderPacket;

public class RestaurantDetails
{
 
	private String name;
	private String address1;
	private String address2;
	
	private String startTime;
	private String endTime;
	private String printingTime;
	private String openOrderCount;
	private String orderCount;
	private String guestCount;
	private String cashIn;
	private String cashOut;
	private String cashReceived;
	private String cardReceived;
	
	private String cashTips;
	private String balanceDue;
	private String totalCashDeposit;
	private String totalCardDeposit;
	private String totalCardTips;
	private String totalTaxes;
	private String total;
	private String subtotal;
	private String priceDiscount;
	private String gratuity;
	private String amountPaid;
	private String changeDue;
	private String totalCreditTerm;
	
	private List<RevenueByPaymentMethodType> paymentMethodTypes;
	private List<PaidInOutCash> paidInOutCash;
	private List<PaidInOutCash> cashRegister;
	private List<PaidInOutCash> userLedger;
	private RevenueByDiscountPacket revenueByDiscountPacket;
	private RevenueByDiscountPacket revenueByDiscountItemPacket;
	private RevenueByCategoryPacket revenueByGrossCategoriesPacket;
	private RevenueByCategoryPacket revenueByNetCategoriesPacket;	
	private RevenueByCategoryPacket	revenueByGrossReportingCategoriesPacket;
	private RevenueByCategoryPacket revenueByNetReportingCategoriesPacket;
	private RevenueByVoidOrderPacket revenueByVoidOrdersPacket;
	private RevenueByVoidOrderPacket revenueByCancelItemPacket;
	private RevenueByTax revenueByTax;
	private RevenueByReportingCategoryPacket revenueByReportingCategoriesPacket;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPrintingTime() {
		return printingTime;
	}
	public void setPrintingTime(String printingTime) {
		this.printingTime = printingTime;
	}
	public String getOpenOrderCount() {
		return openOrderCount;
	}
	public void setOpenOrderCount(String openOrderCount) {
		this.openOrderCount = openOrderCount;
	}
	public String getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(String orderCount) {
		this.orderCount = orderCount;
	}
	public String getGuestCount() {
		return guestCount;
	}
	public void setGuestCount(String guestCount) {
		this.guestCount = guestCount;
	}
	public String getCashIn() {
		return cashIn;
	}
	public void setCashIn(String cashIn) {
		this.cashIn = cashIn;
	}
	public String getCashOut() {
		return cashOut;
	}
	public void setCashOut(String cashOut) {
		this.cashOut = cashOut;
	}
	public String getCashReceived() {
		return cashReceived;
	}
	public void setCashReceived(String cashReceived) {
		this.cashReceived = cashReceived;
	}
	public String getCardReceived() {
		return cardReceived;
	}
	public void setCardReceived(String cardReceived) {
		this.cardReceived = cardReceived;
	}
	public String getCashTips() {
		return cashTips;
	}
	public void setCashTips(String cashTips) {
		this.cashTips = cashTips;
	}
	public String getBalanceDue() {
		return balanceDue;
	}
	public void setBalanceDue(String balanceDue) {
		this.balanceDue = balanceDue;
	}
	public String getTotalCashDeposit() {
		return totalCashDeposit;
	}
	public void setTotalCashDeposit(String totalCashDeposit) {
		this.totalCashDeposit = totalCashDeposit;
	}
	public String getTotalCardDeposit() {
		return totalCardDeposit;
	}
	public void setTotalCardDeposit(String totalCardDeposit) {
		this.totalCardDeposit = totalCardDeposit;
	}
	public String getTotalCardTips() {
		return totalCardTips;
	}
	public void setTotalCardTips(String totalCardTips) {
		this.totalCardTips = totalCardTips;
	}
	public String getTotalTaxes() {
		return totalTaxes;
	}
	public void setTotalTaxes(String totalTaxes) {
		this.totalTaxes = totalTaxes;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}
	public String getPriceDiscount() {
		return priceDiscount;
	}
	public void setPriceDiscount(String priceDiscount) {
		this.priceDiscount = priceDiscount;
	}
	public String getGratuity() {
		return gratuity;
	}
	public void setGratuity(String gratuity) {
		this.gratuity = gratuity;
	}
	public String getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	public String getChangeDue() {
		return changeDue;
	}
	public void setChangeDue(String changeDue) {
		this.changeDue = changeDue;
	}
	public String getTotalCreditTerm() {
		return totalCreditTerm;
	}
	public void setTotalCreditTerm(String totalCreditTerm) {
		this.totalCreditTerm = totalCreditTerm;
	}
	public List<RevenueByPaymentMethodType> getPaymentMethodTypes() {
		return paymentMethodTypes;
	}
	public void setPaymentMethodTypes(List<RevenueByPaymentMethodType> paymentMethodTypes) {
		this.paymentMethodTypes = paymentMethodTypes;
	}

	
	public RevenueByCategoryPacket getRevenueByGrossCategoriesPacket() {
		return revenueByGrossCategoriesPacket;
	}
	public void setRevenueByGrossCategoriesPacket(
			RevenueByCategoryPacket revenueByGrossCategoriesPacket) {
		this.revenueByGrossCategoriesPacket = revenueByGrossCategoriesPacket;
	}
	public RevenueByCategoryPacket getRevenueByNetCategoriesPacket() {
		return revenueByNetCategoriesPacket;
	}
	public void setRevenueByNetCategoriesPacket(
			RevenueByCategoryPacket revenueByNetCategoriesPacket) {
		this.revenueByNetCategoriesPacket = revenueByNetCategoriesPacket;
	}
	
	public RevenueByVoidOrderPacket getRevenueByVoidOrdersPacket() {
		return revenueByVoidOrdersPacket;
	}
	public void setRevenueByVoidOrdersPacket(
			RevenueByVoidOrderPacket revenueByVoidOrdersPacket) {
		this.revenueByVoidOrdersPacket = revenueByVoidOrdersPacket;
	}
	public RevenueByDiscountPacket getRevenueByDiscountPacket() {
		return revenueByDiscountPacket;
	}
	public void setRevenueByDiscountPacket(
			RevenueByDiscountPacket revenueByDiscountPacket) {
		this.revenueByDiscountPacket = revenueByDiscountPacket;
	}
	
	public RevenueByTax getRevenueByTax() {
		return revenueByTax;
	}
	public void setRevenueByTax(RevenueByTax revenueByTax) {
		this.revenueByTax = revenueByTax;
	}
		
	public RevenueByDiscountPacket getRevenueByDiscountItemPacket() {
		return revenueByDiscountItemPacket;
	}
	public void setRevenueByDiscountItemPacket(
			RevenueByDiscountPacket revenueByDiscountItemPacket) {
		this.revenueByDiscountItemPacket = revenueByDiscountItemPacket;
	}
	public RevenueByCategoryPacket getRevenueByGrossReportingCategoriesPacket() {
		return revenueByGrossReportingCategoriesPacket;
	}
	public void setRevenueByGrossReportingCategoriesPacket(
			RevenueByCategoryPacket revenueByGrossReportingCategoriesPacket) {
		this.revenueByGrossReportingCategoriesPacket = revenueByGrossReportingCategoriesPacket;
	}
	public RevenueByCategoryPacket getRevenueByNetReportingCategoriesPacket() {
		return revenueByNetReportingCategoriesPacket;
	}
	public void setRevenueByNetReportingCategoriesPacket(
			RevenueByCategoryPacket revenueByNetReportingCategoriesPacket) {
		this.revenueByNetReportingCategoriesPacket = revenueByNetReportingCategoriesPacket;
	}
	
	
	public RevenueByVoidOrderPacket getRevenueByCancelItemPacket() {
		return revenueByCancelItemPacket;
	}
	public void setRevenueByCancelItemPacket(
			RevenueByVoidOrderPacket revenueByCancelItemPacket) {
		this.revenueByCancelItemPacket = revenueByCancelItemPacket;
	}
		
	public List<PaidInOutCash> getPaidInOutCash() {
		return paidInOutCash;
	}
	public void setPaidInOutCash(List<PaidInOutCash> paidInOutCash) {
		this.paidInOutCash = paidInOutCash;
	}
	
		
	public List<PaidInOutCash> getCashRegister() {
		return cashRegister;
	}
	public void setCashRegister(List<PaidInOutCash> cashRegister) {
		this.cashRegister = cashRegister;
	}
	
	
	public List<PaidInOutCash> getUserLedger() {
		return userLedger;
	}
	public void setUserLedger(List<PaidInOutCash> userLedger) {
		this.userLedger = userLedger;
	}
	public RevenueByReportingCategoryPacket getRevenueByReportingCategoriesPacket() {
		return revenueByReportingCategoriesPacket;
	}
	public void setRevenueByReportingCategoriesPacket(
			RevenueByReportingCategoryPacket revenueByReportingCategoriesPacket) {
		this.revenueByReportingCategoriesPacket = revenueByReportingCategoriesPacket;
	}
	@Override
	public String toString() {
		return "RestaurantDetails [name=" + name + ", address1=" + address1
				+ ", address2=" + address2 + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", printingTime=" + printingTime
				+ ", openOrderCount=" + openOrderCount + ", orderCount="
				+ orderCount + ", guestCount=" + guestCount + ", cashIn="
				+ cashIn + ", cashOut=" + cashOut + ", cashReceived="
				+ cashReceived + ", cardReceived=" + cardReceived
				+ ", cashTips=" + cashTips + ", balanceDue=" + balanceDue
				+ ", totalCashDeposit=" + totalCashDeposit
				+ ", totalCardDeposit=" + totalCardDeposit + ", totalCardTips="
				+ totalCardTips + ", totalTaxes=" + totalTaxes + ", total="
				+ total + ", subtotal=" + subtotal + ", priceDiscount="
				+ priceDiscount + ", gratuity=" + gratuity + ", amountPaid="
				+ amountPaid + ", changeDue=" + changeDue
				+ ", totalCreditTerm=" + totalCreditTerm
				+ ", paymentMethodTypes=" + paymentMethodTypes
				+ ", paidInOutCash=" + paidInOutCash + ", cashRegister="
				+ cashRegister + ", userLedger=" + userLedger
				+ ", revenueByDiscountPacket=" + revenueByDiscountPacket
				+ ", revenueByDiscountItemPacket="
				+ revenueByDiscountItemPacket
				+ ", revenueByGrossCategoriesPacket="
				+ revenueByGrossCategoriesPacket
				+ ", revenueByNetCategoriesPacket="
				+ revenueByNetCategoriesPacket
				+ ", revenueByGrossReportingCategoriesPacket="
				+ revenueByGrossReportingCategoriesPacket
				+ ", revenueByNetReportingCategoriesPacket="
				+ revenueByNetReportingCategoriesPacket
				+ ", revenueByVoidOrdersPacket=" + revenueByVoidOrdersPacket
				+ ", revenueByCancelItemPacket=" + revenueByCancelItemPacket
				+ ", revenueByTax=" + revenueByTax
				+ ", revenueByReportingCategoriesPacket="
				+ revenueByReportingCategoriesPacket + "]";
	}
	
	
	 
}
