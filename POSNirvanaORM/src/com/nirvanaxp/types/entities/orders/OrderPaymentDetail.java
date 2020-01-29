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
import java.util.List;

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
import com.nirvanaxp.types.entities.user.UsersToPaymentHistory;

/**
 * The persistent class for the order_payment_details database table.
 * 
 */
@Entity
@Table(name = "order_payment_details")
@XmlRootElement(name = "order_payment_details")
public class OrderPaymentDetail implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(name = "amount_paid", precision = 10, scale = 2)
	private BigDecimal amountPaid;

	@Column(name = "auth_amount", precision = 10, scale = 2)
	private BigDecimal authAmount;

	@Column(name = "auth_code", length = 64)
	private String authCode;

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
	
	@Column(name = "device_to_pinpad_id")
	private String deviceToPinPadId;
	
	@Column(name = "discount_code")
	private String discountCode;
		
	private transient UsersToPaymentHistory usersToPaymentHistory;
	
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
	
	@Column(name = "opd_id")
	private String opdId;
	
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

	public String getProcessData() {
		return processData;
	}

	public void setProcessData(String processData) {
		this.processData = processData;
	}

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	private transient List<OrderPaymentDetailsToSalesTax> orderPaymentDetailsToSalesTax;
	
	public BigDecimal getPriceDiscount() {
		return priceDiscount;
	}

	public void setPriceDiscount(BigDecimal priceDiscount) {
		this.priceDiscount = priceDiscount;
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

	public BigDecimal getCreditTermTip()
	{
		return creditTermTip;
	}

	public void setCreditTermTip(BigDecimal creditTermTip)
	{
		this.creditTermTip = creditTermTip;
	}

	public String getCardType()
	{
		return cardType;
	}

	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	public OrderPaymentDetail()
	{
	}


	 

	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
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
		if(priceGratuity == null)
		{
			new BigDecimal(0);
		}
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

	

	public String getOpdId() {
		 if(opdId != null && (opdId.length()==0 || opdId.equals("0"))){return null;}else{	return opdId;}
	}

	public void setOpdId(String opdId) {
		this.opdId = opdId;
	}

	public void setOrderPaymentDetailsResultSet(Object[] objRow)
	{

		int index = 0;

		if (objRow[index] != null)
			setId((String) objRow[index]);
		if (objRow[++index] != null)
			setOrderHeaderId((String) objRow[index]);
		++index; // skipping 3rd column
		++index; // skipping 4th column

		if (objRow[++index] != null)
			setSeatId(((String) objRow[index]));
		if (objRow[++index] != null)
			setPnRef(((String) objRow[index]));
		if (objRow[++index] != null)
			setHostRef((Integer) objRow[index]);
		if (objRow[++index] != null)
			setDate(((String) objRow[index]));
		if (objRow[++index] != null)
			setTime(((String) objRow[index]));
		if (objRow[++index] != null)
			setRegister((String) objRow[index]);
		if (objRow[++index] != null)
			setAmountPaid(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setBalanceDue(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setTotalAmount(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setCardNumber(((String) objRow[index]));
		if (objRow[++index] != null)
			setExpiryMonth(((String) objRow[index]));
		if (objRow[++index] != null)
			setExpiryYear(((String) objRow[index]));
		if (objRow[++index] != null)
			setSecurityCode(((String) objRow[index]));
		if (objRow[++index] != null)
			setAuthAmount(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setSettledAmount(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setTipAmount(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setAuthCode(((String) objRow[index]));
		if (objRow[++index] != null)
			setPosEntry(((String) objRow[index]));
		if (objRow[++index] != null)
			setBatchNumber((Integer) objRow[index]);
		if (objRow[++index] != null)
			setAvsResponse(((String) objRow[index]));
		if (objRow[++index] != null)
			setCvResult(((String) objRow[index]));
		if (objRow[++index] != null)
			setCvMessage(((String) objRow[index]));
		if (objRow[++index] != null)
			setResult(((String) objRow[index]));
		if (objRow[++index] != null)
			setMessage(((String) objRow[index]));
		if (objRow[++index] != null)
			setComments(((String) objRow[index]));

		if (objRow[++index] != null)
			setIsRefunded(((Integer) objRow[index]));
		if ((objRow[++index] != null))
		{
			if (((Timestamp) objRow[index]) != null)
			{
				setCreated(new Date(((Timestamp) objRow[index]).getTime()));
			}
		}
		if (objRow[++index] != null)
			setCreatedBy((String) objRow[index]);
		if (objRow[++index] != null)
		{
			if (((Timestamp) objRow[index]) != null)
			{
				setUpdated(new Date(((Timestamp) objRow[index]).getTime()));
			}
		}
		if (objRow[++index] != null)
			setUpdatedBy((String) objRow[index]);
		if (objRow[++index] != null)
			setSignatureUrl(((String) objRow[index]));
		++index; // skiiping for Transaction status
		if (objRow[++index] != null)
			setCashTipAmt(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setCreditcardTipAmt(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setChangeDue(((BigDecimal) objRow[index]));
		if (objRow[++index] != null)
			setCardType(((String) objRow[index]));
		if (objRow[++index] != null)
			setAcqRefData(((String) objRow[index]));
		if (objRow[++index] != null)
			setOrderSourceGroupToPaymentGatewayTypeId((Integer) objRow[index]);
		if (objRow[++index] != null)
			setOrderSourceToPaymentGatewayTypeId((Integer) objRow[index]);
		
		if (objRow[++index] != null)
			setDiscountsName((String) objRow[index]);
		if (objRow[++index] != null)
			setDiscountId((String) objRow[index]);
		if (objRow[++index] != null)
			setDiscountsValue((BigDecimal) objRow[index]);
		if (objRow[++index] != null)
			setCalculatedDiscountValue((BigDecimal) objRow[index]);
		if (objRow[++index] != null)
			setPriceDiscount((BigDecimal) objRow[index]);
		
		PaymentMethod method = new PaymentMethod();
		setValueToPaymentMethod(method, objRow, ++index);
		setPaymentMethod(method);

		PaymentTransactionType transactionType = new PaymentTransactionType();
		setValueToTransactionType(transactionType, objRow, 61);
		setPaymentTransactionType(transactionType);

		TransactionStatus status = null;
		if (objRow[35] != null)
		{
			if ((Integer) objRow[35] != 0)
			{ // checking transactional status id
				status = new TransactionStatus();
				setValueToStatus(status, objRow, 71);
			}
		}

		setTransactionStatus(status);
		if (objRow[91] != null)
			setPayementGatewayId((Integer) objRow[91]);
		if (objRow[92] != null)
			setHostRefStr(((String) objRow[92]));
		if (objRow[93] != null)
			setInvoiceNumber(((String) objRow[93]));
		if (objRow[94] != null)
		{
			setNirvanaXpBatchNumber((String) objRow[94]);
		}
		if (objRow[95] != null)
			setPriceTax1((BigDecimal) objRow[95]);
		if (objRow[96] != null)
			setPriceTax2((BigDecimal) objRow[96]);
		if (objRow[97] != null)
			setPriceTax3((BigDecimal) objRow[97]);
		if (objRow[98] != null)
			setPriceTax4((BigDecimal) objRow[98]);
		if (objRow[99] != null)
			setTaxName1((String) objRow[99]);
		if (objRow[100] != null)
			setTaxName2((String) objRow[100]);
		if (objRow[101] != null)
			setTaxName3((String) objRow[101]);
		if (objRow[102] != null)
			setTaxName4((String) objRow[102]);
		if (objRow[103] != null)
			setTaxDisplayName1((String) objRow[103]);
		if (objRow[104] != null)
			setTaxDisplayName2((String) objRow[104]);
		if (objRow[105] != null)
			setTaxDisplayName3((String) objRow[105]);
		if (objRow[106] != null)
			setTaxDisplayName4((String) objRow[106]);
		if (objRow[107] != null)
			setTaxRate1((BigDecimal) objRow[107]);
		if (objRow[108] != null)
			setTaxRate2((BigDecimal) objRow[108]);
		if (objRow[109] != null)
			setTaxRate3((BigDecimal) objRow[109]);
		if (objRow[110] != null)
			setTaxRate4((BigDecimal) objRow[110]);
		if (objRow[111] != null)
			setPriceGratuity((BigDecimal) objRow[111]);
		if (objRow[112] != null)
			setGratuity((BigDecimal) objRow[112]);
		if (objRow[113] != null)
			setProcessData((String) objRow[113]);
		if (objRow[114] != null)
			setSequenceNo((String) objRow[114]);
		if (objRow[115] != null)
			setCreditTermTip((BigDecimal) objRow[115]);
		if (objRow[116] != null)
			setDeviceToPinPadId((String) objRow[116]);
		if (objRow[117] != null)
			setDiscountCode((String) objRow[117]);
		
		if (objRow[118] != null)
			setCustomerFirstName((String) objRow[118]);
		if (objRow[119] != null)
			setCustomerLastName((String) objRow[119]);
		if (objRow[120] != null)
			setAccountNumber((String) objRow[120]);
		if (objRow[121] != null)
			setChequeNumber((String) objRow[121]);
		if (objRow[122] != null)
			setBankName((String) objRow[122]);
		
		if (objRow[123] != null)
			setOpdId((String) objRow[123]);
		
		if (objRow[124] != null)
			setChequeTip((BigDecimal) objRow[124]);
		

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
	

	private void setValueToStatus(TransactionStatus status, Object[] objRow, int index)
	{
		if (objRow[index] != null)
			status.setId((Integer) objRow[index]);
		if (objRow[++index] != null)
			status.setName(((String) objRow[index]));
		if (objRow[++index] != null)
			status.setDisplayName(((String) objRow[index]));
		if (objRow[++index] != null)
			status.setDisplaySequence((Integer) objRow[index]);
		++index; // skipping 6
		if (objRow[++index] != null)
			if (((Timestamp) objRow[index]) != null)
			{
				status.setCreated(((Timestamp) objRow[index]).getTime());
			}
		if (objRow[++index] != null)
			status.setCreatedBy((String) objRow[index]);
		if (objRow[++index] != null)
		{
			if (((Timestamp) objRow[index]) != null)
			{
				status.setUpdated(((Timestamp) objRow[index]).getTime());
			}
		}
		if (objRow[++index] != null)
			status.setUpdatedBy((String) objRow[index]);
		if (objRow[++index] != null)
			status.setStatus((objRow[index].toString()));

		if ( objRow[70] !=null && (String) objRow[70] != null)
		{
			PaymentGatewayType paymentGatewayType = new PaymentGatewayType();
			if (objRow[++index] != null)
				paymentGatewayType.setId((Integer) objRow[index]);
			if (objRow[++index] != null)
				paymentGatewayType.setName(((String) objRow[index]));
			if (objRow[++index] != null)
				paymentGatewayType.setDisplayName(((String) objRow[index]));
			if (((Timestamp) objRow[++index]) != null)
			{
				paymentGatewayType.setCreated(new Date(((Timestamp) objRow[index]).getTime()));
			}
			if (objRow[++index] != null)
				paymentGatewayType.setCreatedBy((String) objRow[index]);
			if (((Timestamp) objRow[++index]) != null)
			{
				paymentGatewayType.setUpdated(new Date(((Timestamp) objRow[index]).getTime()));
			}
			if (objRow[++index] != null)
				paymentGatewayType.setUpdatedBy((String) objRow[index]);
			if (objRow[++index] != null)
				paymentGatewayType.setStatus((objRow[index].toString()));
			if (objRow[++index] != null)
				paymentGatewayType.setLocationsId((String) objRow[index]);
			if (objRow[++index] != null)
				paymentGatewayType.setDisplaySequence((Integer) objRow[index]);
			status.setPaymentGatewayType(paymentGatewayType);

		}
	}

	private void setValueToTransactionType(PaymentTransactionType transactionType, Object[] objRow, int index)
	{
		if (objRow[index] != null)
			transactionType.setId((Integer) objRow[index]);
		if (objRow[++index] != null)
			transactionType.setName(((String) objRow[index]));
		if (objRow[++index] != null)
			transactionType.setDisplayName(((String) objRow[index]));
		if (objRow[++index] != null)
			transactionType.setDisplaySequence((Integer) objRow[index]);
		if (objRow[++index] != null)
			transactionType.setStatus((objRow[index].toString()));
		if (objRow[++index] != null)
			transactionType.setLocationsId((String) objRow[index]);
		if (objRow[++index] != null)
		{
			if (((Timestamp) objRow[index]) != null)
			{
				transactionType.setCreated(new Date(((Timestamp) objRow[index]).getTime()));
			}
		}
		if (objRow[++index] != null)
			transactionType.setCreatedBy((String) objRow[index]);
		if (objRow[++index] != null)
		{
			if (((Timestamp) objRow[index]) != null)
			{
				transactionType.setUpdated(new Date(((Timestamp) objRow[index]).getTime()));
			}
		}
		if (objRow[++index] != null)
			transactionType.setUpdatedBy((String) objRow[index]);
	}

	private void setValueToPaymentMethod(PaymentMethod method, Object[] objRow, int index)
	{
		if (objRow[index] != null)
			method.setId((String) objRow[index]);
		if (objRow[++index] != null)
			method.setPaymentMethodTypeId((String) objRow[index]);
		if (objRow[++index] != null)
			method.setName(((String) objRow[index]));
		if (objRow[++index] != null)
			method.setDisplayName(((String) objRow[index]));
		if (objRow[++index] != null)
			method.setDescription(((String) objRow[index]));
		if (objRow[++index] != null)
			method.setLocationsId((String) objRow[index]);
		if (objRow[++index] != null)
			method.setStatus((objRow[index]).toString());
		if (objRow[++index] != null)
			// method.setIsActive((Integer)objRow[index]);
			if (objRow[++index] != null)
				method.setDisplaySequence((Integer) objRow[index]);
		if (objRow[++index] != null)
		{
			if (((Timestamp) objRow[index]) != null)
			{
				method.setCreated(new Date(((Timestamp) objRow[index]).getTime()));
			}
		}
		if (objRow[++index] != null)
			method.setCreatedBy((String) objRow[index]);
		if (objRow[++index] != null)
		{
			if (((Timestamp) objRow[index]) != null)
			{
				method.setUpdated(new Date(((Timestamp) objRow[index]).getTime()));
			}
		}
		if (objRow[++index] != null)
			method.setUpdatedBy((String) objRow[index]);
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

	
	public List<OrderPaymentDetailsToSalesTax> getOrderPaymentDetailsToSalesTax() {
		return orderPaymentDetailsToSalesTax;
	}

	public void setOrderPaymentDetailsToSalesTax(
			List<OrderPaymentDetailsToSalesTax> orderPaymentDetailsToSalesTax) {
		this.orderPaymentDetailsToSalesTax = orderPaymentDetailsToSalesTax;
	}

	public OrderPaymentDetail setOrderPaymentDetail(OrderPaymentDetail detail)
	{
		OrderPaymentDetail paymentDetail = new OrderPaymentDetail();
		paymentDetail.setNirvanaXpBatchNumber(detail.getNirvanaXpBatchNumber());
		paymentDetail.setAcqRefData(detail.getAcqRefData());
		paymentDetail.setAmountPaid(detail.getAmountPaid());
		paymentDetail.setAuthAmount(detail.getAuthAmount());
		paymentDetail.setAvsResponse(detail.getAvsResponse());
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
		// paymentDetail.setId(d);
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
		paymentDetail.setCalculatedDiscountValue(detail.getCalculatedDiscountValue());
		paymentDetail.setDiscountsName(detail.getDiscountsName());
		paymentDetail.setDiscountId(detail.getDiscountId());
		paymentDetail.setDiscountsValue(detail.getDiscountsValue());
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
		paymentDetail.setDiscountCode(detail.getDiscountCode());	
		paymentDetail.setLocalTime(detail.getLocalTime());
		paymentDetail.setCustomerFirstName(detail.getCustomerFirstName());
		paymentDetail.setCustomerLastName(detail.getCustomerLastName());
		paymentDetail.setAuthCode(detail.getAuthCode());
		paymentDetail.setProcessData(detail.getProcessData());
		paymentDetail.setSequenceNo(detail.getSequenceNo());
		paymentDetail.setChequeTip(detail.getChequeTip());

		if(detail.getDeviceToPinPadId()!=null){
			paymentDetail.setAcqRefData(detail.getAcqRefData());
			paymentDetail.setAuthCode(detail.getAuthCode());
			paymentDetail.setProcessData(detail.getProcessData());
			paymentDetail.setHostRefStr(detail.getHostRefStr());
			paymentDetail.setPnRef(detail.getPnRef());
			paymentDetail.setHostRef(detail.getHostRef());
			paymentDetail.setSequenceNo(detail.getSequenceNo());
			paymentDetail.setDiscountCode(detail.getDiscountCode());
			paymentDetail.setLocalTime(detail.getLocalTime());
			paymentDetail.setCustomerFirstName(detail.getCustomerFirstName());
			paymentDetail.setCustomerLastName(detail.getCustomerLastName());
			paymentDetail.setCreditTermTip(detail.getCreditTermTip());
			paymentDetail.setDeviceToPinPadId(detail.getDeviceToPinPadId());
			
		}
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
	public BigDecimal getCalculatedDiscountValue() {
		return calculatedDiscountValue;
	}

	public void setCalculatedDiscountValue(BigDecimal calculatedDiscountValue) {
		this.calculatedDiscountValue = calculatedDiscountValue;
	}

	public String getDeviceToPinPadId()
	{
		 if(deviceToPinPadId != null && (deviceToPinPadId.length()==0 || deviceToPinPadId.equals("0"))){return null;}else{	return deviceToPinPadId;}
	}

	public void setDeviceToPinPadId(String deviceToPinPadId)
	{
		this.deviceToPinPadId = deviceToPinPadId;
	}

	public UsersToPaymentHistory getUsersToPaymentHistory() {
		return usersToPaymentHistory;
	}

	public void setUsersToPaymentHistory(UsersToPaymentHistory usersToPaymentHistory) {
		this.usersToPaymentHistory = usersToPaymentHistory;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getBankName()
	{
		return bankName;
	}

	public void setBankName(String bankName)
	{
		this.bankName = bankName;
	}

	

	@Override
	public String toString()
	{
		return "OrderPaymentDetail [id=" + id + ", amountPaid=" + amountPaid + ", authAmount=" + authAmount + ", authCode=" + authCode + ", avsResponse=" + avsResponse + ", balanceDue=" + balanceDue
				+ ", batchNumber=" + batchNumber + ", cardNumber=" + cardNumber + ", comments=" + comments + ", created=" + created + ", createdBy=" + createdBy + ", cvMessage=" + cvMessage
				+ ", cvResult=" + cvResult + ", date=" + date + ", expiryMonth=" + expiryMonth + ", expiryYear=" + expiryYear + ", hostRef=" + hostRef + ", message=" + message + ", pnRef=" + pnRef
				+ ", posEntry=" + posEntry + ", register=" + register + ", result=" + result + ", seatId=" + seatId + ", securityCode=" + securityCode + ", settledAmount=" + settledAmount + ", time="
				+ time + ", tipAmount=" + tipAmount + ", totalAmount=" + totalAmount + ", updated=" + updated + ", updatedBy=" + updatedBy + ", orderHeaderId=" + orderHeaderId + ", paymentMethod="
				+ paymentMethod + ", paymentTransactionType=" + paymentTransactionType + ", isRefunded=" + isRefunded + ", signatureUrl=" + signatureUrl + ", changeDue=" + changeDue
				+ ", transactionStatus=" + transactionStatus + ", cashTipAmt=" + cashTipAmt + ", creditcardTipAmt=" + creditcardTipAmt + ", payementGatewayId=" + payementGatewayId + ", cardType="
				+ cardType + ", acqRefData=" + acqRefData + ", hostRefStr=" + hostRefStr + ", invoiceNumber=" + invoiceNumber + ", nirvanaXpBatchNumber=" + nirvanaXpBatchNumber
				+ ", orderSourceGroupToPaymentGatewayTypeId=" + orderSourceGroupToPaymentGatewayTypeId + ", orderSourceToPaymentGatewayTypeId=" + orderSourceToPaymentGatewayTypeId
				+ ", discountsName=" + discountsName + ", discountId=" + discountId + ", discountsValue=" + discountsValue + ", calculatedDiscountValue=" + calculatedDiscountValue
				+ ", priceDiscount=" + priceDiscount + ", priceTax1=" + priceTax1 + ", priceTax2=" + priceTax2 + ", priceTax3=" + priceTax3 + ", priceTax4=" + priceTax4 + ", taxName1=" + taxName1
				+ ", taxName2=" + taxName2 + ", taxName3=" + taxName3 + ", taxName4=" + taxName4 + ", taxDisplayName1=" + taxDisplayName1 + ", taxDisplayName2=" + taxDisplayName2
				+ ", taxDisplayName3=" + taxDisplayName3 + ", taxDisplayName4=" + taxDisplayName4 + ", taxRate1=" + taxRate1 + ", taxRate2=" + taxRate2 + ", taxRate3=" + taxRate3 + ", taxRate4="
				+ taxRate4 + ", priceGratuity=" + priceGratuity + ", gratuity=" + gratuity + ", processData=" + processData + ", sequenceNo=" + sequenceNo + ", creditTermTip=" + creditTermTip
				+ ", deviceToPinPadId=" + deviceToPinPadId + ", discountCode=" + discountCode + ", localTime=" + localTime + ", customerFirstName=" + customerFirstName + ", customerLastName="
				+ customerLastName + ", accountNumber=" + accountNumber + ", chequeNumber=" + chequeNumber + ", bankName=" + bankName + ", opdId=" + opdId + ", chequeTip=" + chequeTip + "]";
	}

	public String getChequeNumber()
	{
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber)
	{
		this.chequeNumber = chequeNumber;
	}

	 
		
	
	
		

}