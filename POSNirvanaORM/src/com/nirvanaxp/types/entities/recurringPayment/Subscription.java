/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.recurringPayment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;



public class Subscription {
	
	private String planId;
	private String paymentMethodToken;
	private BigDecimal price;
	private String merchantAccountId;
	private int trialDuration;
	private int numberOfBillingCycle;
	private Date firstBillingDate;
	private Date billingDayOfMonth;
	private boolean startImmediately;
	private List<AddOns> addOnsList;
	private List<Discounts> discountList;
	
	/**
	 * @return the planId
	 */
	public String getPlanId() {
		return planId;
	}
	/**
	 * @param planId the planId to set
	 */
	public void setPlanId(String planId) {
		this.planId = planId;
	}
	/**
	 * @return the paymentMethodToken
	 */
	public String getPaymentMethodToken() {
		return paymentMethodToken;
	}
	/**
	 * @param paymentMethodToken the paymentMethodToken to set
	 */
	public void setPaymentMethodToken(String paymentMethodToken) {
		this.paymentMethodToken = paymentMethodToken;
	}
	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * @return the merchantAccountId
	 */
	public String getMerchantAccountId() {
		return merchantAccountId;
	}
	/**
	 * @param merchantAccountId the merchantAccountId to set
	 */
	public void setMerchantAccountId(String merchantAccountId) {
		this.merchantAccountId = merchantAccountId;
	}
	/**
	 * @return the trialDuration
	 */
	public int getTrialDuration() {
		return trialDuration;
	}
	/**
	 * @param trialDuration the trialDuration to set
	 */
	public void setTrialDuration(int trialDuration) {
		this.trialDuration = trialDuration;
	}
	/**
	 * @return the numberOfBillingCycle
	 */
	public int getNumberOfBillingCycle() {
		return numberOfBillingCycle;
	}
	/**
	 * @param numberOfBillingCycle the numberOfBillingCycle to set
	 */
	public void setNumberOfBillingCycle(int numberOfBillingCycle) {
		this.numberOfBillingCycle = numberOfBillingCycle;
	}
	/**
	 * @return the firstBillingDate
	 */
	public Date getFirstBillingDate() {
		return firstBillingDate;
	}
	/**
	 * @param firstBillingDate the firstBillingDate to set
	 */
	public void setFirstBillingDate(Date firstBillingDate) {
		this.firstBillingDate = firstBillingDate;
	}
	/**
	 * @return the billingDayOfMonth
	 */
	public Date getBillingDayOfMonth() {
		return billingDayOfMonth;
	}
	/**
	 * @param billingDayOfMonth the billingDayOfMonth to set
	 */
	public void setBillingDayOfMonth(Date billingDayOfMonth) {
		this.billingDayOfMonth = billingDayOfMonth;
	}
	/**
	 * @return the startImmediately
	 */
	public boolean isStartImmediately() {
		return startImmediately;
	}
	/**
	 * @param startImmediately the startImmediately to set
	 */
	public void setStartImmediately(boolean startImmediately) {
		this.startImmediately = startImmediately;
	}
	/**
	 * @return the addOnsList
	 */
	public List<AddOns> getAddOnsList() {
		return addOnsList;
	}
	/**
	 * @param addOnsList the addOnsList to set
	 */
	public void setAddOnsList(List<AddOns> addOnsList) {
		this.addOnsList = addOnsList;
	}
	/**
	 * @return the discountList
	 */
	public List<Discounts> getDiscountList() {
		return discountList;
	}
	/**
	 * @param discountList the discountList to set
	 */
	public void setDiscountList(List<Discounts> discountList) {
		this.discountList = discountList;
	}
	@Override
	public String toString() {
		return "Subscription [planId=" + planId + ", paymentMethodToken="
				+ paymentMethodToken + ", price=" + price
				+ ", merchantAccountId=" + merchantAccountId
				+ ", trialDuration=" + trialDuration
				+ ", numberOfBillingCycle=" + numberOfBillingCycle
				+ ", firstBillingDate=" + firstBillingDate
				+ ", billingDayOfMonth=" + billingDayOfMonth
				+ ", startImmediately=" + startImmediately + ", addOnsList="
				+ addOnsList + ", discountList=" + discountList + "]";
	}
}
