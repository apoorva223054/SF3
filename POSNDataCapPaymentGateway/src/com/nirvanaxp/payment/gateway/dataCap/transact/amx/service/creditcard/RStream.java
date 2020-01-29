package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RStream
{

	@JsonProperty("BatchItemCount")
	private String batchItemCount;

	@JsonProperty("BatchNo")
	private String batchNo;

	@JsonProperty("CmdStatus")
	private String cmdStatus;

	@JsonProperty("CreditPurchaseAmount")
	private String creditPurchaseAmount;

	@JsonProperty("CreditPurchaseCount")
	private String creditPurchaseCount;

	@JsonProperty("CreditReturnAmount")
	private String creditReturnAmount;

	@JsonProperty("CreditReturnCount")
	private String creditReturnCount;

	@JsonProperty("DSIXReturnCode")
	private String dSIXReturnCode;

	@JsonProperty("DebitPurchaseAmount")
	private String debitPurchaseAmount;

	@JsonProperty("DebitPurchaseCount")
	private String debitPurchaseCount;

	@JsonProperty("DebitReturnAmount")
	private String debitReturnAmount;

	@JsonProperty("DebitReturnCount")
	private String debitReturnCount;

	@JsonProperty("MerchantID")
	private String merchantID;

	@JsonProperty("NetBatchTotal")
	private String netBatchTotal;

	@JsonProperty("OperatorID")
	private String operatorID;

	@JsonProperty("ResponseOrigin")
	private String responseOrigin;

	@JsonProperty("SequenceNo")
	private String sequenceNo;

	@JsonProperty("TextResponse")
	private String textResponse;

	@JsonProperty("UserTrace")
	private String userTrace;

	@JsonProperty("AcctNo")
	private String acctNo;

	@JsonProperty("AuthCode")
	private String authCode;

	@JsonProperty("Authorize")
	private String authorize;

	@JsonProperty("CaptureStatus")
	private String captureStatus;

	@JsonProperty("CardType")
	private String cardType;

	@JsonProperty("ExpDate")
	private String expDate;

	@JsonProperty("Gratuity")
	private String gratuity;

	@JsonProperty("InvoiceNo")
	private String invoiceNo;

	@JsonProperty("ProcessData")
	private String processData;

	@JsonProperty("Purchase")
	private String purchase;

	@JsonProperty("RecordNo")
	private String recordNo;

	@JsonProperty("RefNo")
	private String refNo;

	@JsonProperty("TranCode")
	private String tranCode;

	public String getBatchItemCount()
	{
		return batchItemCount;
	}

	public void setBatchItemCount(String batchItemCount)
	{
		this.batchItemCount = batchItemCount;
	}

	public String getBatchNo()
	{
		return batchNo;
	}

	public void setBatchNo(String batchNo)
	{
		this.batchNo = batchNo;
	}

	public String getCmdStatus()
	{
		return cmdStatus;
	}

	public void setCmdStatus(String cmdStatus)
	{
		this.cmdStatus = cmdStatus;
	}

	public String getCreditPurchaseAmount()
	{
		return creditPurchaseAmount;
	}

	public void setCreditPurchaseAmount(String creditPurchaseAmount)
	{
		this.creditPurchaseAmount = creditPurchaseAmount;
	}

	public String getCreditPurchaseCount()
	{
		return creditPurchaseCount;
	}

	public void setCreditPurchaseCount(String creditPurchaseCount)
	{
		this.creditPurchaseCount = creditPurchaseCount;
	}

	public String getCreditReturnAmount()
	{
		return creditReturnAmount;
	}

	public void setCreditReturnAmount(String creditReturnAmount)
	{
		this.creditReturnAmount = creditReturnAmount;
	}

	public String getCreditReturnCount()
	{
		return creditReturnCount;
	}

	public void setCreditReturnCount(String creditReturnCount)
	{
		this.creditReturnCount = creditReturnCount;
	}

	public String getdSIXReturnCode()
	{
		return dSIXReturnCode;
	}

	public void setdSIXReturnCode(String dSIXReturnCode)
	{
		this.dSIXReturnCode = dSIXReturnCode;
	}

	public String getDebitPurchaseAmount()
	{
		return debitPurchaseAmount;
	}

	public void setDebitPurchaseAmount(String debitPurchaseAmount)
	{
		this.debitPurchaseAmount = debitPurchaseAmount;
	}

	public String getDebitPurchaseCount()
	{
		return debitPurchaseCount;
	}

	public void setDebitPurchaseCount(String debitPurchaseCount)
	{
		this.debitPurchaseCount = debitPurchaseCount;
	}

	public String getDebitReturnAmount()
	{
		return debitReturnAmount;
	}

	public void setDebitReturnAmount(String debitReturnAmount)
	{
		this.debitReturnAmount = debitReturnAmount;
	}

	public String getDebitReturnCount()
	{
		return debitReturnCount;
	}

	public void setDebitReturnCount(String debitReturnCount)
	{
		this.debitReturnCount = debitReturnCount;
	}

	public String getMerchantID()
	{
		return merchantID;
	}

	public void setMerchantID(String merchantID)
	{
		this.merchantID = merchantID;
	}

	public String getNetBatchTotal()
	{
		return netBatchTotal;
	}

	public void setNetBatchTotal(String netBatchTotal)
	{
		this.netBatchTotal = netBatchTotal;
	}

	public String getOperatorID()
	{
		return operatorID;
	}

	public void setOperatorID(String operatorID)
	{
		this.operatorID = operatorID;
	}

	public String getResponseOrigin()
	{
		return responseOrigin;
	}

	public void setResponseOrigin(String responseOrigin)
	{
		this.responseOrigin = responseOrigin;
	}

	public String getSequenceNo()
	{
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo)
	{
		this.sequenceNo = sequenceNo;
	}

	public String getTextResponse()
	{
		return textResponse;
	}

	public void setTextResponse(String textResponse)
	{
		this.textResponse = textResponse;
	}

	public String getUserTrace()
	{
		return userTrace;
	}

	public void setUserTrace(String userTrace)
	{
		this.userTrace = userTrace;
	}

	public String getAcctNo()
	{
		return acctNo;
	}

	public void setAcctNo(String acctNo)
	{
		this.acctNo = acctNo;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getAuthorize()
	{
		return authorize;
	}

	public void setAuthorize(String authorize)
	{
		this.authorize = authorize;
	}

	public String getCaptureStatus()
	{
		return captureStatus;
	}

	public void setCaptureStatus(String captureStatus)
	{
		this.captureStatus = captureStatus;
	}

	public String getCardType()
	{
		return cardType;
	}

	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	public String getExpDate()
	{
		return expDate;
	}

	public void setExpDate(String expDate)
	{
		this.expDate = expDate;
	}

	public String getGratuity()
	{
		return gratuity;
	}

	public void setGratuity(String gratuity)
	{
		this.gratuity = gratuity;
	}

	public String getInvoiceNo()
	{
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo)
	{
		this.invoiceNo = invoiceNo;
	}

	public String getProcessData()
	{
		return processData;
	}

	public void setProcessData(String processData)
	{
		this.processData = processData;
	}

	public String getPurchase()
	{
		return purchase;
	}

	public void setPurchase(String purchase)
	{
		this.purchase = purchase;
	}

	public String getRecordNo()
	{
		return recordNo;
	}

	public void setRecordNo(String recordNo)
	{
		this.recordNo = recordNo;
	}

	public String getRefNo()
	{
		return refNo;
	}

	public void setRefNo(String refNo)
	{
		this.refNo = refNo;
	}

	public String getTranCode()
	{
		return tranCode;
	}

	public void setTranCode(String tranCode)
	{
		this.tranCode = tranCode;
	}

	@Override
	public String toString()
	{
		return "RStream [batchItemCount=" + batchItemCount + ", batchNo=" + batchNo + ", cmdStatus=" + cmdStatus + ", creditPurchaseAmount=" + creditPurchaseAmount + ", creditPurchaseCount="
				+ creditPurchaseCount + ", creditReturnAmount=" + creditReturnAmount + ", creditReturnCount=" + creditReturnCount + ", dSIXReturnCode=" + dSIXReturnCode + ", debitPurchaseAmount="
				+ debitPurchaseAmount + ", debitPurchaseCount=" + debitPurchaseCount + ", debitReturnAmount=" + debitReturnAmount + ", debitReturnCount=" + debitReturnCount + ", merchantID="
				+ merchantID + ", netBatchTotal=" + netBatchTotal + ", operatorID=" + operatorID + ", responseOrigin=" + responseOrigin + ", sequenceNo=" + sequenceNo + ", textResponse="
				+ textResponse + ", userTrace=" + userTrace + ", acctNo=" + acctNo + ", authCode=" + authCode + ", authorize=" + authorize + ", captureStatus=" + captureStatus + ", cardType="
				+ cardType + ", expDate=" + expDate + ", gratuity=" + gratuity + ", invoiceNo=" + invoiceNo + ", processData=" + processData + ", purchase=" + purchase + ", recordNo=" + recordNo
				+ ", refNo=" + refNo + ", tranCode=" + tranCode + "]";
	}
	
	

}
