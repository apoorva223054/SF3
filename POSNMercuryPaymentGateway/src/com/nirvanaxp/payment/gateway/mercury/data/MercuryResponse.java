/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mercury.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MercuryResponse
{

	private String ResponseOrigin;
	private String DSIXReturnCode;
	private String CmdStatus;
	private String TextResponse;
	private String UserTraceData;
	private String MerchantID;
	private String AcctNo;
	private String ExpDate;
	private String CardType;
	private String TranCode;
	private String AuthCode;
	private String CaptureStatus;
	private String RefNo;
	private String InvoiceNo;
	private String OperatorID;
	private String Memo;
	private String Purchase;
	private String Authorize;
	private String AcqRefData;
	private String RecordNo;
	private String ProcessData;
	private String BatchNo;
	private String BatchItemCount;
	private String NetBatchTotal;
	private String CreditPurchaseCount;
	private String CreditPurchaseAmount;
	private String CreditReturnCount;
	private String CreditReturnAmount;
	private String DebitPurchaseCount;
	private String DebitPurchaseAmount;
	private String DebitReturnCount;
	private String DebitReturnAmount;

	private String Gratuity;

	public MercuryResponse()
	{
		super();
	}

	public String getResponseOrigin()
	{
		return ResponseOrigin;
	}

	public String getDSIXReturnCode()
	{
		return DSIXReturnCode;
	}

	public String getCmdStatus()
	{
		return CmdStatus;
	}

	public String getTextResponse()
	{
		return TextResponse;
	}

	public String getUserTraceData()
	{
		return UserTraceData;
	}

	public String getMerchantID()
	{
		return MerchantID;
	}

	public String getAcctNo()
	{
		return AcctNo;
	}

	public String getExpDate()
	{
		return ExpDate;
	}

	public String getCardType()
	{
		return CardType;
	}

	public String getTranCode()
	{
		return TranCode;
	}

	public String getAuthCode()
	{
		return AuthCode;
	}

	public String getCaptureStatus()
	{
		return CaptureStatus;
	}

	public String getRefNo()
	{
		return RefNo;
	}

	public String getInvoiceNo()
	{
		return InvoiceNo;
	}

	public String getOperatorID()
	{
		return OperatorID;
	}

	public String getMemo()
	{
		return Memo;
	}

	public String getPurchase()
	{
		return Purchase;
	}

	public String getAuthorize()
	{
		return Authorize;
	}

	public String getAcqRefData()
	{
		return AcqRefData;
	}

	public String getRecordNo()
	{
		return RecordNo;
	}

	public String getProcessData()
	{
		return ProcessData;
	}

	public String getBatchNo()
	{
		return BatchNo;
	}

	public String getBatchItemCount()
	{
		return BatchItemCount;
	}

	public String getNetBatchTotal()
	{
		return NetBatchTotal;
	}

	public String getCreditPurchaseCount()
	{
		return CreditPurchaseCount;
	}

	public String getCreditPurchaseAmount()
	{
		return CreditPurchaseAmount;
	}

	public String getCreditReturnCount()
	{
		return CreditReturnCount;
	}

	public String getCreditReturnAmount()
	{
		return CreditReturnAmount;
	}

	public String getDebitPurchaseCount()
	{
		return DebitPurchaseCount;
	}

	public String getDebitPurchaseAmount()
	{
		return DebitPurchaseAmount;
	}

	public String getDebitReturnCount()
	{
		return DebitReturnCount;
	}

	public String getDebitReturnAmount()
	{
		return DebitReturnAmount;
	}

	public String getGratuity()
	{
		return Gratuity;
	}

	public void setResponseOrigin(String responseOrigin)
	{
		ResponseOrigin = responseOrigin;
	}

	public void setDSIXReturnCode(String dSIXReturnCode)
	{
		DSIXReturnCode = dSIXReturnCode;
	}

	public void setCmdStatus(String cmdStatus)
	{
		CmdStatus = cmdStatus;
	}

	public void setTextResponse(String textResponse)
	{
		TextResponse = textResponse;
	}

	public void setUserTraceData(String userTraceData)
	{
		UserTraceData = userTraceData;
	}

	public void setMerchantID(String merchantID)
	{
		MerchantID = merchantID;
	}

	public void setAcctNo(String acctNo)
	{
		AcctNo = acctNo;
	}

	public void setExpDate(String expDate)
	{
		ExpDate = expDate;
	}

	public void setCardType(String cardType)
	{
		CardType = cardType;
	}

	public void setTranCode(String tranCode)
	{
		TranCode = tranCode;
	}

	public void setAuthCode(String authCode)
	{
		AuthCode = authCode;
	}

	public void setCaptureStatus(String captureStatus)
	{
		CaptureStatus = captureStatus;
	}

	public void setRefNo(String refNo)
	{
		RefNo = refNo;
	}

	public void setInvoiceNo(String invoiceNo)
	{
		InvoiceNo = invoiceNo;
	}

	public void setOperatorID(String operatorID)
	{
		OperatorID = operatorID;
	}

	public void setMemo(String memo)
	{
		Memo = memo;
	}

	public void setPurchase(String purchase)
	{
		Purchase = purchase;
	}

	public void setAuthorize(String authorize)
	{
		Authorize = authorize;
	}

	public void setAcqRefData(String acqRefData)
	{
		AcqRefData = acqRefData;
	}

	public void setRecordNo(String recordNo)
	{
		RecordNo = recordNo;
	}

	public void setProcessData(String processData)
	{
		ProcessData = processData;
	}

	public void setBatchNo(String batchNo)
	{
		BatchNo = batchNo;
	}

	public void setBatchItemCount(String batchItemCount)
	{
		BatchItemCount = batchItemCount;
	}

	public void setNetBatchTotal(String netBatchTotal)
	{
		NetBatchTotal = netBatchTotal;
	}

	public void setCreditPurchaseCount(String creditPurchaseCount)
	{
		CreditPurchaseCount = creditPurchaseCount;
	}

	public void setCreditPurchaseAmount(String creditPurchaseAmount)
	{
		CreditPurchaseAmount = creditPurchaseAmount;
	}

	public void setCreditReturnCount(String creditReturnCount)
	{
		CreditReturnCount = creditReturnCount;
	}

	public void setCreditReturnAmount(String creditReturnAmount)
	{
		CreditReturnAmount = creditReturnAmount;
	}

	public void setDebitPurchaseCount(String debitPurchaseCount)
	{
		DebitPurchaseCount = debitPurchaseCount;
	}

	public void setDebitPurchaseAmount(String debitPurchaseAmount)
	{
		DebitPurchaseAmount = debitPurchaseAmount;
	}

	public void setDebitReturnCount(String debitReturnCount)
	{
		DebitReturnCount = debitReturnCount;
	}

	public void setDebitReturnAmount(String debitReturnAmount)
	{
		DebitReturnAmount = debitReturnAmount;
	}

	public void setGratuity(String gratuity)
	{
		Gratuity = gratuity;
	}

}
