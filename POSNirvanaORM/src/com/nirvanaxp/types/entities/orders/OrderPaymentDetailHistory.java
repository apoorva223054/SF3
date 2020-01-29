/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.payment.PaymentGatewayType;
import com.nirvanaxp.types.entities.payment.PaymentMethod;
import com.nirvanaxp.types.entities.payment.PaymentTransactionType;
import com.nirvanaxp.types.entities.payment.TransactionStatus;

/**
 * The persistent class for the order_payment_details database table.
 * 
 */
@Entity
@Table(name = "order_payment_details_history")
@XmlRootElement(name = "order_payment_details_history")
public class OrderPaymentDetailHistory implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private BigInteger id;

	@Column(name = "amount_paid", precision = 10, scale = 2)
	private BigDecimal amountPaid;

	@Column(name = "auth_amount", precision = 10, scale = 2)
	private BigDecimal authAmount;

	@Column(name = "auth_code", length = 64)
	private String authCode;
	
	@Column(name = "order_payment_details_id")
	private String orderPaymentDetailsId;

	@Column(name = "avs_response", length = 64)
	private String avsResponse;

	@Column(name = "balance_due", precision = 10, scale = 2)
	private BigDecimal balanceDue;

	@Column(name = "batch_number")
	private Integer batchNumber;

	@Column(name = "card_number", length = 80)
	private String cardNumber;

	@Column(length = 64)
	private String comments;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "cv_message", length = 64)
	private String cvMessage;

	@Column(name = "cv_result", length = 64)
	private String cvResult;

	@Column(length = 10)
	private String date;

	@Column(name = "expiry_month", length = 12)
	private String expiryMonth;

	@Column(name = "expiry_year", length = 4)
	private String expiryYear;

	@Column(name = "host_ref")
	private Integer hostRef;

	@Column(length = 64)
	private String message;

	@Column(name = "pn_ref")
	private String pnRef;

	@Column(name = "pos_entry", length = 64)
	private String posEntry;

	private String register;

	@Column(length = 64)
	private String result;

	@Column(name = "seat_id")
	private String seatId;

	@Column(name = "security_code", length = 10)
	private String securityCode;

	@Column(name = "settled_amount", precision = 10, scale = 2)
	private BigDecimal settledAmount;

	@Column(length = 10)
	private String time;

	@Column(name = "tip_amount", precision = 10, scale = 2)
	private BigDecimal tipAmount;

	@Column(name = "total_amount", precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	// bi-directional many-to-one association to OrderHeader
	@Column(name = "order_header_id", nullable = false)
	private String orderHeaderId;

	// uni-directional many-to-one association to PaymentMethod
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_method_id")
	private PaymentMethod paymentMethod;

	// uni-directional many-to-one association to PaymentTransactionType
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "payment_transaction_type_id")
	private PaymentTransactionType paymentTransactionType;

	@Column(name = "is_refunded")
	private int isRefunded;

	@Column(name = "signature_url")
	private String signatureUrl;

	@Column(name = "change_due", precision = 10, scale = 2)
	private BigDecimal changeDue;

	@ManyToOne
	@JoinColumn(name = "transaction_status_id")
	private TransactionStatus transactionStatus;

	@Column(name = "cash_tip_amt", precision = 10, scale = 2)
	private BigDecimal cashTipAmt;

	@Column(name = "creditcard_tip_amt", precision = 10, scale = 2)
	private BigDecimal creditcardTipAmt;

	@Column(name = "payementgateway_id")
	private int payementGatewayId;

	@Column(name = "card_type")
	private String cardType;

	@Column(name = "acq_ref_data")
	private String acqRefData;

	@Column(name = "host_ref_str")
	private String hostRefStr;

	@Column(name = "invoice_number")
	private String invoiceNumber;

	@Column(name = "nirvanaxp_batch_number")
	private String nirvanaXpBatchNumber;

	@Column(name = "order_source_group_to_paymentgatewaytype_id")
	private int orderSourceGroupToPaymentGatewayTypeId;

	@Column(name = "order_source_to_paymentgatewaytype_id")
	private int orderSourceToPaymentGatewayTypeId;

	@Column(name = "discounts_name")
	private String discountsName;

	@Column(name = "discount_id")
	private String discountId;
	
	@Column(name = "discounts_value", precision = 10, scale = 2)
	private BigDecimal discountsValue;
	
	@Column(name = "calculated_discount_value", precision = 10, scale = 2)
	private BigDecimal calculatedDiscountValue;
	
	@Column(name = "price_discount", precision = 10, scale = 2)
	private BigDecimal priceDiscount;
	
	@Column(name = "price_tax_1", precision = 10, scale = 2)
	private BigDecimal priceTax1;

	@Column(name = "price_tax_2", precision = 10, scale = 2)
	private BigDecimal priceTax2;

	@Column(name = "price_tax_3", precision = 10, scale = 2)
	private BigDecimal priceTax3;

	@Column(name = "price_tax_4", precision = 10, scale = 2)
	private BigDecimal priceTax4;

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

	@Column(name = "price_gratuity", precision = 10, scale = 2)
	private BigDecimal priceGratuity;

	@Column(precision = 10, scale = 2)
	private BigDecimal gratuity;
	
	@Column(name = "process_data")
	private String processData;
	
	@Column(name = "sequence_no")
	private String sequenceNo;
	
	@Column(name = "credit_term_tip")
	private BigDecimal creditTermTip;
	
	@Column(name = "discount_code")
	private String discountCode;
	
		
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "customer_first_name")
	private String customerFirstName;
	
	@Column(name = "customer_last_name")
	private String customerLastName;
	
	@Column(name = "account_number")
	private String accountNumber;
	
	@Column(name = "cheque_number")
	private String chequeNumber;
	
	@Column(name = "bank_name")
	private String bankName;
	
	@Column(name = "cheque_tip")
	private BigDecimal chequeTip;

	public BigDecimal getChequeTip()
	{
		return chequeTip;
	}

	public void setChequeTip(BigDecimal chequeTip)
	{
		this.chequeTip = chequeTip;
	}
	
	public String getCustomerFirstName()
	{
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName)
	{
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName()
	{
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName)
	{
		this.customerLastName = customerLastName;
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public String getDiscountCode()
	{
		return discountCode;
	}

	public void setDiscountCode(String discountCode)
	{
		this.discountCode = discountCode;
	}
	

	public String getProcessData()
	{
		return processData;
	}

	public void setProcessData(String processData)
	{
		this.processData = processData;
	}

	public String getSequenceNo()
	{
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo)
	{
		this.sequenceNo = sequenceNo;
	}

	public BigDecimal getCreditTermTip()
	{
		return creditTermTip;
	}

	public void setCreditTermTip(BigDecimal creditTermTip)
	{
		this.creditTermTip = creditTermTip;
	}

	public BigDecimal getPriceTax1() {
		return priceTax1;
	}

	public void setPriceTax1(BigDecimal priceTax1) {
		this.priceTax1 = priceTax1;
	}

	public BigDecimal getPriceTax2() {
		return priceTax2;
	}

	public void setPriceTax2(BigDecimal priceTax2) {
		this.priceTax2 = priceTax2;
	}

	public BigDecimal getPriceTax3() {
		return priceTax3;
	}

	public void setPriceTax3(BigDecimal priceTax3) {
		this.priceTax3 = priceTax3;
	}

	public BigDecimal getPriceTax4() {
		return priceTax4;
	}

	public void setPriceTax4(BigDecimal priceTax4) {
		this.priceTax4 = priceTax4;
	}

	public String getTaxName1() {
		return taxName1;
	}

	public void setTaxName1(String taxName1) {
		this.taxName1 = taxName1;
	}

	public String getTaxName2() {
		return taxName2;
	}

	public void setTaxName2(String taxName2) {
		this.taxName2 = taxName2;
	}

	public String getTaxName3() {
		return taxName3;
	}

	public void setTaxName3(String taxName3) {
		this.taxName3 = taxName3;
	}

	public String getTaxName4() {
		return taxName4;
	}

	public void setTaxName4(String taxName4) {
		this.taxName4 = taxName4;
	}

	public String getTaxDisplayName1() {
		return taxDisplayName1;
	}

	public void setTaxDisplayName1(String taxDisplayName1) {
		this.taxDisplayName1 = taxDisplayName1;
	}

	public String getTaxDisplayName2() {
		return taxDisplayName2;
	}

	public void setTaxDisplayName2(String taxDisplayName2) {
		this.taxDisplayName2 = taxDisplayName2;
	}

	public String getTaxDisplayName3() {
		return taxDisplayName3;
	}

	public void setTaxDisplayName3(String taxDisplayName3) {
		this.taxDisplayName3 = taxDisplayName3;
	}

	public String getTaxDisplayName4() {
		return taxDisplayName4;
	}

	public void setTaxDisplayName4(String taxDisplayName4) {
		this.taxDisplayName4 = taxDisplayName4;
	}

	public BigDecimal getTaxRate1() {
		return taxRate1;
	}

	public void setTaxRate1(BigDecimal taxRate1) {
		this.taxRate1 = taxRate1;
	}

	public BigDecimal getTaxRate2() {
		return taxRate2;
	}

	public void setTaxRate2(BigDecimal taxRate2) {
		this.taxRate2 = taxRate2;
	}

	public BigDecimal getTaxRate3() {
		return taxRate3;
	}

	public void setTaxRate3(BigDecimal taxRate3) {
		this.taxRate3 = taxRate3;
	}

	public BigDecimal getTaxRate4() {
		return taxRate4;
	}

	public void setTaxRate4(BigDecimal taxRate4) {
		this.taxRate4 = taxRate4;
	}

	public BigDecimal getPriceGratuity() {
		return priceGratuity;
	}

	public void setPriceGratuity(BigDecimal priceGratuity) {
		this.priceGratuity = priceGratuity;
	}

	public BigDecimal getGratuity() {
		return gratuity;
	}

	public void setGratuity(BigDecimal gratuity) {
		this.gratuity = gratuity;
	}

	public String getCardType()
	{
		return cardType;
	}

	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	public OrderPaymentDetailHistory()
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

	public BigDecimal getAuthAmount()
	{
		return this.authAmount;
	}

	public void setAuthAmount(BigDecimal authAmount)
	{
		this.authAmount = authAmount;
	}

	public String getAuthCode()
	{
		return this.authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getAvsResponse()
	{
		return this.avsResponse;
	}

	public void setAvsResponse(String avsResponse)
	{
		this.avsResponse = avsResponse;
	}

	public BigDecimal getBalanceDue()
	{
		return this.balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue)
	{
		this.balanceDue = balanceDue;
	}

	public Integer getBatchNumber()
	{
		return this.batchNumber;
	}

	public void setBatchNumber(Integer batchNumber)
	{
		this.batchNumber = batchNumber;
	}

	public String getCardNumber()
	{
		return this.cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getComments()
	{
		return this.comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public String getCvMessage()
	{
		return this.cvMessage;
	}

	public void setCvMessage(String cvMessage)
	{
		this.cvMessage = cvMessage;
	}

	public BigDecimal getChangeDue()
	{
		return changeDue;
	}

	public void setChangeDue(BigDecimal changeDue)
	{
		this.changeDue = changeDue;
	}

	public String getCvResult()
	{
		return this.cvResult;
	}

	public void setCvResult(String cvResult)
	{
		this.cvResult = cvResult;
	}

	public BigDecimal getCashTipAmt()
	{
		return cashTipAmt;
	}

	public void setCashTipAmt(BigDecimal cashTipAmt)
	{
		this.cashTipAmt = cashTipAmt;
	}

	public BigDecimal getCreditcardTipAmt()
	{
		return creditcardTipAmt;
	}

	public void setCreditcardTipAmt(BigDecimal creditcardTipAmt)
	{
		this.creditcardTipAmt = creditcardTipAmt;
	}

	public String getDate()
	{
		return this.date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getExpiryMonth()
	{
		return this.expiryMonth;
	}

	public void setExpiryMonth(String expiryMonth)
	{
		this.expiryMonth = expiryMonth;
	}

	public String getExpiryYear()
	{
		return this.expiryYear;
	}

	public void setExpiryYear(String expiryYear)
	{
		this.expiryYear = expiryYear;
	}

	public String getMessage()
	{
		return this.message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getPosEntry()
	{
		return this.posEntry;
	}

	public void setPosEntry(String posEntry)
	{
		this.posEntry = posEntry;
	}

	public String getRegister()
	{
		 if(register != null && (register.length()==0 || register.equals("0"))){return null;}else{	return register;}
	}

	public void setRegister(String register)
	{
		this.register = register;
	}

	public String getResult()
	{
		return this.result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getSeatId()
	{
		return this.seatId;
	}

	public void setSeatId(String seatId)
	{
		this.seatId = seatId;
	}

	public String getSecurityCode()
	{
		return this.securityCode;
	}

	public void setSecurityCode(String securityCode)
	{
		this.securityCode = securityCode;
	}

	public BigDecimal getSettledAmount()
	{
		return this.settledAmount;
	}

	public void setSettledAmount(BigDecimal settledAmount)
	{
		this.settledAmount = settledAmount;
	}

	public String getTime()
	{
		return this.time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public BigDecimal getTipAmount()
	{
		return this.tipAmount;
	}

	public void setTipAmount(BigDecimal tipAmount)
	{
		this.tipAmount = tipAmount;
	}

	public BigDecimal getTotalAmount()
	{
		return this.totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public PaymentMethod getPaymentMethod()
	{
		return this.paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}

	public PaymentTransactionType getPaymentTransactionType()
	{
		return this.paymentTransactionType;
	}

	public void setPaymentTransactionType(PaymentTransactionType paymentTransactionType)
	{
		this.paymentTransactionType = paymentTransactionType;
	}

	public int getIsRefunded()
	{
		return isRefunded;
	}

	public void setIsRefunded(int isRefunded)
	{
		this.isRefunded = isRefunded;
	}

	public String getSignatureUrl()
	{
		return signatureUrl;
	}

	public void setSignatureUrl(String signatureUrl)
	{
		this.signatureUrl = signatureUrl;
	}

	public TransactionStatus getTransactionStatus()
	{
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus)
	{
		this.transactionStatus = transactionStatus;
	}
	
	

	
	
	/**
	 * @return the orderSourceGroupToPaymentGatewayTypeId
	 */
	public int getOrderSourceGroupToPaymentGatewayTypeId()
	{
		return orderSourceGroupToPaymentGatewayTypeId;
	}

	/**
	 * @param orderSourceGroupToPaymentGatewayTypeId
	 *            the orderSourceGroupToPaymentGatewayTypeId to set
	 */
	public void setOrderSourceGroupToPaymentGatewayTypeId(int orderSourceGroupToPaymentGatewayTypeId)
	{
		this.orderSourceGroupToPaymentGatewayTypeId = orderSourceGroupToPaymentGatewayTypeId;
	}

	/**
	 * @return the orderSourceToPaymentGatewayTypeId
	 */
	public int getOrderSourceToPaymentGatewayTypeId()
	{
		return orderSourceToPaymentGatewayTypeId;
	}

	/**
	 * @param orderSourceToPaymentGatewayTypeId
	 *            the orderSourceToPaymentGatewayTypeId to set
	 */
	public void setOrderSourceToPaymentGatewayTypeId(int orderSourceToPaymentGatewayTypeId)
	{
		this.orderSourceToPaymentGatewayTypeId = orderSourceToPaymentGatewayTypeId;
	}

	

	public int getPayementGatewayId()
	{
		return payementGatewayId;
	}

	public void setPayementGatewayId(int payementGatewayId)
	{
		this.payementGatewayId = payementGatewayId;
	}

	/**
	 * @return the hostRef
	 */
	public Integer getHostRef()
	{
		return hostRef;
	}

	/**
	 * @param hostRef
	 *            the hostRef to set
	 */
	public void setHostRef(Integer hostRef)
	{
		this.hostRef = hostRef;
	}

	public String getPnRef()
	{
		return pnRef;
	}

	public void setPnRef(String pnRef)
	{
		this.pnRef = pnRef;
	}

	/**
	 * @return the acqRefData
	 */
	public String getAcqRefData()
	{
		return acqRefData;
	}

	/**
	 * @param acqRefData
	 *            the acqRefData to set
	 */
	public void setAcqRefData(String acqRefData)
	{
		this.acqRefData = acqRefData;
	}

	public String getHostRefStr()
	{
		return hostRefStr;
	}

	public void setHostRefStr(String hostRefStr)
	{
		this.hostRefStr = hostRefStr;
	}

	/**
	 * @return the invoiceNumber
	 */
	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	/**
	 * @param invoiceNumber
	 *            the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getOrderPaymentDetailsId() {
		if(orderPaymentDetailsId != null && (orderPaymentDetailsId.length()==0 || orderPaymentDetailsId.equals("0"))){return null;}else{	return orderPaymentDetailsId;}
	}

	public void setOrderPaymentDetailsId(String orderPaymentDetailsId) {
		this.orderPaymentDetailsId = orderPaymentDetailsId;
	}

	public OrderPaymentDetailHistory setOrderPaymentDetailHistory(OrderPaymentDetail detail)
	{
		OrderPaymentDetailHistory paymentDetail = new OrderPaymentDetailHistory();
		paymentDetail.setNirvanaXpBatchNumber(detail.getNirvanaXpBatchNumber());
		paymentDetail.setOrderPaymentDetailsId(detail.getId());
		paymentDetail.setAcqRefData(detail.getAcqRefData());
		paymentDetail.setAmountPaid(detail.getAmountPaid());
		paymentDetail.setAuthAmount(detail.getAuthAmount());
		paymentDetail.setAvsResponse(detail.getAvsResponse());
		paymentDetail.setAuthCode(detail.getAuthCode());
		paymentDetail.setBalanceDue(detail.getBalanceDue());
		paymentDetail.setBatchNumber(detail.getBatchNumber());
		paymentDetail.setCardNumber(detail.getCardNumber());
		paymentDetail.setCardType(detail.getCardType());
		paymentDetail.setCashTipAmt(detail.getCashTipAmt());
		paymentDetail.setChangeDue(detail.getChangeDue());
		paymentDetail.setComments(detail.getComments());
		paymentDetail.setCreated(detail.getCreated());
		paymentDetail.setCreatedBy(detail.getCreatedBy());
		paymentDetail.setCreditcardTipAmt(detail.getCreditcardTipAmt());
		paymentDetail.setCvMessage(detail.getCvMessage());
		paymentDetail.setCvResult(detail.getCvResult());
		paymentDetail.setDate(detail.getDate());
		paymentDetail.setExpiryMonth(detail.getExpiryMonth());
		paymentDetail.setExpiryYear(detail.getExpiryYear());
		paymentDetail.setHostRef(detail.getHostRef());
		paymentDetail.setHostRefStr(detail.getHostRefStr());
		paymentDetail.setOrderPaymentDetailsId(detail.getId());
		paymentDetail.setInvoiceNumber(detail.getInvoiceNumber());
		paymentDetail.setIsRefunded(detail.getIsRefunded());
		paymentDetail.setMessage(detail.getMessage());
		paymentDetail.setOrderHeaderId(detail.getOrderHeaderId());
		paymentDetail.setOrderSourceGroupToPaymentGatewayTypeId(detail.getOrderSourceGroupToPaymentGatewayTypeId());
		paymentDetail.setOrderSourceToPaymentGatewayTypeId(detail.getOrderSourceToPaymentGatewayTypeId());
		paymentDetail.setPayementGatewayId(detail.getPayementGatewayId());
		paymentDetail.setPaymentMethod(detail.getPaymentMethod());
		paymentDetail.setPaymentTransactionType(detail.getPaymentTransactionType());
		paymentDetail.setPnRef(detail.getPnRef());
		paymentDetail.setPosEntry(detail.getPosEntry());
		paymentDetail.setRegister(detail.getRegister());
		paymentDetail.setResult(detail.getResult());
		paymentDetail.setSeatId(detail.getSeatId());
		paymentDetail.setSecurityCode(detail.getSecurityCode());
		paymentDetail.setSettledAmount(detail.getSettledAmount());
		paymentDetail.setSignatureUrl(paymentDetail.getSignatureUrl());
		paymentDetail.setTime(detail.getTime());
		paymentDetail.setTipAmount(detail.getTipAmount());
		paymentDetail.setTotalAmount(detail.getTotalAmount());
		paymentDetail.setTransactionStatus(detail.getTransactionStatus());
		paymentDetail.setUpdated(detail.getUpdated());
		paymentDetail.setUpdatedBy(detail.getUpdatedBy());
		paymentDetail.setDiscountId(detail.getDiscountId());
		paymentDetail.setDiscountsName(detail.getDiscountsName());
		paymentDetail.setDiscountsValue(detail.getDiscountsValue());
		paymentDetail.setCalculatedDiscountValue(detail.getCalculatedDiscountValue());
		paymentDetail.setPriceDiscount(detail.getPriceDiscount());
		paymentDetail.setPriceGratuity(detail.getPriceGratuity());
		paymentDetail.setGratuity(detail.getGratuity());
		paymentDetail.setPriceTax1(detail.getPriceTax1());
		paymentDetail.setPriceTax2(detail.getPriceTax2());
		paymentDetail.setPriceTax3(detail.getPriceTax3());
		paymentDetail.setPriceTax4(detail.getPriceTax4());
		paymentDetail.setTaxDisplayName1(detail.getTaxDisplayName1());
		paymentDetail.setTaxDisplayName2(detail.getTaxDisplayName2());
		paymentDetail.setTaxDisplayName3(detail.getTaxDisplayName3());
		paymentDetail.setTaxDisplayName4(detail.getTaxDisplayName4());
		paymentDetail.setTaxName1(detail.getTaxName1());
		paymentDetail.setTaxName2(detail.getTaxName2());
		paymentDetail.setTaxName3(detail.getTaxName3());
		paymentDetail.setTaxName4(detail.getTaxName4());
		paymentDetail.setTaxRate1(detail.getTaxRate1());
		paymentDetail.setTaxRate2(detail.getTaxRate2());
		paymentDetail.setTaxRate3(detail.getTaxRate3());
		paymentDetail.setTaxRate4(detail.getTaxRate4());
		paymentDetail.setProcessData(detail.getProcessData());
		paymentDetail.setSequenceNo(detail.getSequenceNo());
		paymentDetail.setCreditTermTip(detail.getCreditTermTip());
		paymentDetail.setDiscountCode(detail.getDiscountCode());
		paymentDetail.setProcessData(detail.getProcessData());
		paymentDetail.setSequenceNo(detail.getSequenceNo());
		paymentDetail.setCreditTermTip(detail.getCreditTermTip());
		paymentDetail.setDiscountCode(detail.getDiscountCode());
		paymentDetail.setLocalTime(detail.getLocalTime());
		paymentDetail.setCustomerFirstName(detail.getCustomerFirstName());
		paymentDetail.setCustomerLastName(detail.getCustomerLastName());
		paymentDetail.setBankName(detail.getBankName());
		paymentDetail.setChequeNumber(detail.getChequeNumber());
		paymentDetail.setAccountNumber(detail.getAccountNumber());
		paymentDetail.setChequeTip(detail.getChequeTip());
		return paymentDetail;
	}

	public String getNirvanaXpBatchNumber()
	{
		return nirvanaXpBatchNumber;
	}

	public void setNirvanaXpBatchNumber(String nirvanaXpBatchNumber)
	{
		this.nirvanaXpBatchNumber = nirvanaXpBatchNumber;
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


	public String getDiscountsName() {
		return discountsName;
	}

	public void setDiscountsName(String discountsName) {
		this.discountsName = discountsName;
	}

	public String getDiscountId() {
		 if(discountId != null && (discountId.length()==0 || discountId.equals("0"))){return null;}else{	return discountId;}
	}

	public void setDiscountId(String discountId) {
		this.discountId = discountId;
	}

	public BigDecimal getDiscountsValue() {
		return discountsValue;
	}

	public void setDiscountsValue(BigDecimal discountsValue) {
		this.discountsValue = discountsValue;
	}

	public BigDecimal getCalculatedDiscountValue() {
		return calculatedDiscountValue;
	}

	public void setCalculatedDiscountValue(BigDecimal calculatedDiscountValue) {
		this.calculatedDiscountValue = calculatedDiscountValue;
	}

	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getChequeNumber()
	{
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber)
	{
		this.chequeNumber = chequeNumber;
	}

	public String getBankName()
	{
		return bankName;
	}

	public void setBankName(String bankName)
	{
		this.bankName = bankName;
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

	@Override
	public String toString()
	{
		return "OrderPaymentDetailHistory [id=" + id + ", amountPaid=" + amountPaid + ", authAmount=" + authAmount + ", authCode=" + authCode + ", orderPaymentDetailsId=" + orderPaymentDetailsId
				+ ", avsResponse=" + avsResponse + ", balanceDue=" + balanceDue + ", batchNumber=" + batchNumber + ", cardNumber=" + cardNumber + ", comments=" + comments + ", created=" + created
				+ ", createdBy=" + createdBy + ", cvMessage=" + cvMessage + ", cvResult=" + cvResult + ", date=" + date + ", expiryMonth=" + expiryMonth + ", expiryYear=" + expiryYear + ", hostRef="
				+ hostRef + ", message=" + message + ", pnRef=" + pnRef + ", posEntry=" + posEntry + ", register=" + register + ", result=" + result + ", seatId=" + seatId + ", securityCode="
				+ securityCode + ", settledAmount=" + settledAmount + ", time=" + time + ", tipAmount=" + tipAmount + ", totalAmount=" + totalAmount + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", orderHeaderId=" + orderHeaderId + ", paymentMethod=" + paymentMethod + ", paymentTransactionType=" + paymentTransactionType + ", isRefunded=" + isRefunded
				+ ", signatureUrl=" + signatureUrl + ", changeDue=" + changeDue + ", transactionStatus=" + transactionStatus + ", cashTipAmt=" + cashTipAmt + ", creditcardTipAmt=" + creditcardTipAmt
				+ ", payementGatewayId=" + payementGatewayId + ", cardType=" + cardType + ", acqRefData=" + acqRefData + ", hostRefStr=" + hostRefStr + ", invoiceNumber=" + invoiceNumber
				+ ", nirvanaXpBatchNumber=" + nirvanaXpBatchNumber + ", orderSourceGroupToPaymentGatewayTypeId=" + orderSourceGroupToPaymentGatewayTypeId + ", orderSourceToPaymentGatewayTypeId="
				+ orderSourceToPaymentGatewayTypeId + ", discountsName=" + discountsName + ", discountId=" + discountId + ", discountsValue=" + discountsValue + ", calculatedDiscountValue="
				+ calculatedDiscountValue + ", priceDiscount=" + priceDiscount + ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2 + ", priceTax3=" + priceTax3 + ", priceTax4=" + priceTax4
				+ ", taxName1=" + taxName1 + ", taxName2=" + taxName2 + ", taxName3=" + taxName3 + ", taxName4=" + taxName4 + ", taxDisplayName1=" + taxDisplayName1 + ", taxDisplayName2="
				+ taxDisplayName2 + ", taxDisplayName3=" + taxDisplayName3 + ", taxDisplayName4=" + taxDisplayName4 + ", taxRate1=" + taxRate1 + ", taxRate2=" + taxRate2 + ", taxRate3=" + taxRate3
				+ ", taxRate4=" + taxRate4 + ", priceGratuity=" + priceGratuity + ", gratuity=" + gratuity + ", processData=" + processData + ", sequenceNo=" + sequenceNo + ", creditTermTip="
				+ creditTermTip + ", discountCode=" + discountCode + ", localTime=" + localTime + ", customerFirstName=" + customerFirstName + ", customerLastName=" + customerLastName
				+ ", accountNumber=" + accountNumber + ", chequeNumber=" + chequeNumber + ", bankName=" + bankName + "]";
	}

	
	

}