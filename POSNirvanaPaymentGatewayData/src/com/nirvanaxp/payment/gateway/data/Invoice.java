/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

import java.math.BigDecimal;

public class Invoice
{

	/**
	 * Purchase invoice number
	 */
	private String invoiceNumber;

	/**
	 * Date of invoice in YYMMDD format for Concord
	 */
	private String date;

	/**
	 * Description of purchase
	 */
	private String discription;

	/**
	 * Possible total discount for invoice
	 */
	private Double discountAmount;

	/**
	 * > Possible shipping amount for invoice
	 */
	private Double dutyAmount;

	/**
	 * Possible tax amount for invoice
	 */
	private Double taxAmount;

	/**
	 * Possible additional tax amount included in invoice total
	 */
	private Double nationalTaxInc;

	/**
	 * Total amount of the transaction on the invoice
	 */
	private Double totalAmount;
	
	private BigDecimal totalAmountWithoutTip;
	
	private BigDecimal tipAmountWithFirstData;

	private String tipAmount;

	private BillTo billTo;

	private String pnrRef;

	private String recordNumber;

	private boolean isSettle;

	private String beginDate;

	private String endDate;

	private int orderPaymentDetailId;

	private String userId;
	
	private String processData;

	public String getAcqRefData()
	{
		return acqRefData;
	}

	public void setAcqRefData(String acqRefData)
	{
		this.acqRefData = acqRefData;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	private String acqRefData;

	private String authCode;

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getDiscription()
	{
		return discription;
	}

	public void setDiscription(String discription)
	{
		this.discription = discription;
	}

	public Double getDiscountAmount()
	{
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount)
	{
		this.discountAmount = discountAmount;
	}

	public Double getDutyAmount()
	{
		return dutyAmount;
	}

	public void setDutyAmount(Double dutyAmount)
	{
		this.dutyAmount = dutyAmount;
	}

	public Double getTaxAmount()
	{
		return taxAmount;
	}

	public void setTaxAmount(Double taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	public Double getNationalTaxInc()
	{
		return nationalTaxInc;
	}

	public void setNationalTaxInc(Double nationalTaxInc)
	{
		this.nationalTaxInc = nationalTaxInc;
	}

	public Double getTotalAmount()
	{
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public BillTo getBillTo()
	{
		return billTo;
	}

	public void setBillTo(BillTo billTo)
	{
		this.billTo = billTo;
	}

	public String getTipAmount()
	{
		return tipAmount;
	}

	public void setTipAmount(String tipAmount)
	{
		this.tipAmount = tipAmount;
	}

	public String getPnrRef()
	{
		return pnrRef;
	}

	public void setPnrRef(String pnrRef)
	{
		this.pnrRef = pnrRef;
	}

	/**
	 * @return the recordNumber
	 */
	public String getRecordNumber()
	{
		return recordNumber;
	}

	/**
	 * @param recordNumber
	 *            the recordNumber to set
	 */
	public void setRecordNumber(String recordNumber)
	{
		this.recordNumber = recordNumber;
	}

	/**
	 * @return the isSettle
	 */
	public boolean isSettle()
	{
		return isSettle;
	}

	/**
	 * @param isSettle
	 *            the isSettle to set
	 */
	public void setSettle(boolean isSettle)
	{
		this.isSettle = isSettle;
	}

	/**
	 * @return the beginDate
	 */
	public String getBeginDate()
	{
		return beginDate;
	}

	/**
	 * @param beginDate
	 *            the beginDate to set
	 */
	public void setBeginDate(String beginDate)
	{
		this.beginDate = beginDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return the orderPaymentDetailId
	 */
	public int getOrderPaymentDetailId()
	{
		return orderPaymentDetailId;
	}

	/**
	 * @param orderPaymentDetailId
	 *            the orderPaymentDetailId to set
	 */
	public void setOrderPaymentDetailId(int orderPaymentDetailId)
	{
		this.orderPaymentDetailId = orderPaymentDetailId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getProcessData() {
		return processData;
	}

	public void setProcessData(String processData) {
		this.processData = processData;
	}

	public BigDecimal getTotalAmountWithoutTip() {
		return totalAmountWithoutTip;
	}

	public void setTotalAmountWithoutTip(BigDecimal totalAmountWithoutTip) {
		this.totalAmountWithoutTip = totalAmountWithoutTip;
	}

	public BigDecimal getTipAmountWithFirstData() {
		return tipAmountWithFirstData;
	}

	public void setTipAmountWithFirstData(BigDecimal tipAmountWithFirstData) {
		this.tipAmountWithFirstData = tipAmountWithFirstData;
	}

	@Override
	public String toString() {
		return "Invoice [invoiceNumber=" + invoiceNumber + ", date=" + date + ", discription=" + discription
				+ ", discountAmount=" + discountAmount + ", dutyAmount=" + dutyAmount + ", taxAmount=" + taxAmount
				+ ", nationalTaxInc=" + nationalTaxInc + ", totalAmount=" + totalAmount + ", totalAmountWithoutTip="
				+ totalAmountWithoutTip + ", tipAmountWithFirstData=" + tipAmountWithFirstData + ", tipAmount="
				+ tipAmount + ", billTo=" + billTo + ", pnrRef=" + pnrRef + ", recordNumber=" + recordNumber
				+ ", isSettle=" + isSettle + ", beginDate=" + beginDate + ", endDate=" + endDate
				+ ", orderPaymentDetailId=" + orderPaymentDetailId + ", userId=" + userId + ", processData="
				+ processData + ", acqRefData=" + acqRefData + ", authCode=" + authCode + "]";
	}
	
	
}
