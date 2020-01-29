/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.parser;

public class PaymentGatewayResponse
{

	private String authCode;

	private String extData;

	private String getAvsResult;

	private String getAvsResultTxt;

	private String getCommercialCard;

	private String getCVResult;

	private String getCVResultTxt;

	private String getStreetMatchTxt;

	private String getZipMatchTxt;

	private String hostCode;

	private String message;

	private String message1;

	private String message2;

	private String pnrRef;

	private String responseMsg;

	private String result;

	private String theResponseXmlStr;

	private String hostURL;

	private String receiptURL;

	private String getGetOrigResult;

	private String workingKey;

	private String keyPointer;

	private String invNum;

	private String cardName;

	private String expiryDate;

	private String nameOnCard;

	private String cardNumber;

	private String batchId;

	private String txnCnt;

	private String batchSeqNbr;

	private String prevPnrRef;

	private String batchItemCount;

	private String netBatchTotal;

	private String creditPurchaseCount;

	private String creditPurchaseAmount;

	private String creditReturnCount;

	private String creditReturnAmount;

	private String debitPurchaseCount;

	private String debitPurchaseAmount;

	private String debitReturnCount;

	private String debitReturnAmount;

	private String acqRefData;

	private String invoicePaidAmount;

	private String invoicePaidTip;

	private String invoiceAmtWithTip;

	public String getInvoicePaidAmount()
	{
		return invoicePaidAmount;
	}

	public void setInvoicePaidAmount(String invoicePaidAmount)
	{
		this.invoicePaidAmount = invoicePaidAmount;
	}

	public String getInvoicePaidTip()
	{
		return invoicePaidTip;
	}

	public void setInvoicePaidTip(String invoicePaidTip)
	{
		this.invoicePaidTip = invoicePaidTip;
	}

	public String getBatchItemCount()
	{
		return batchItemCount;
	}

	public void setBatchItemCount(String batchItemCount)
	{
		this.batchItemCount = batchItemCount;
	}

	public String getNetBatchTotal()
	{
		return netBatchTotal;
	}

	public void setNetBatchTotal(String netBatchTotal)
	{
		this.netBatchTotal = netBatchTotal;
	}

	public String getCreditPurchaseCount()
	{
		return creditPurchaseCount;
	}

	public void setCreditPurchaseCount(String creditPurchaseCount)
	{
		this.creditPurchaseCount = creditPurchaseCount;
	}

	public String getCreditPurchaseAmount()
	{
		return creditPurchaseAmount;
	}

	public void setCreditPurchaseAmount(String creditPurchaseAmount)
	{
		this.creditPurchaseAmount = creditPurchaseAmount;
	}

	public String getCreditReturnCount()
	{
		return creditReturnCount;
	}

	public void setCreditReturnCount(String creditReturnCount)
	{
		this.creditReturnCount = creditReturnCount;
	}

	public String getCreditReturnAmount()
	{
		return creditReturnAmount;
	}

	public void setCreditReturnAmount(String creditReturnAmount)
	{
		this.creditReturnAmount = creditReturnAmount;
	}

	public String getDebitPurchaseCount()
	{
		return debitPurchaseCount;
	}

	public void setDebitPurchaseCount(String debitPurchaseCount)
	{
		this.debitPurchaseCount = debitPurchaseCount;
	}

	public String getDebitPurchaseAmount()
	{
		return debitPurchaseAmount;
	}

	public void setDebitPurchaseAmount(String debitPurchaseAmount)
	{
		this.debitPurchaseAmount = debitPurchaseAmount;
	}

	public String getDebitReturnCount()
	{
		return debitReturnCount;
	}

	public void setDebitReturnCount(String debitReturnCount)
	{
		this.debitReturnCount = debitReturnCount;
	}

	public String getDebitReturnAmount()
	{
		return debitReturnAmount;
	}

	public void setDebitReturnAmount(String debitReturnAmount)
	{
		this.debitReturnAmount = debitReturnAmount;
	}

	public String getBatchId()
	{
		return batchId;
	}

	public void setBatchId(String batchId)
	{
		this.batchId = batchId;
	}

	public String getTxnCnt()
	{
		return txnCnt;
	}

	public void setTxnCnt(String txnCnt)
	{
		this.txnCnt = txnCnt;
	}

	public String getBatchSeqNbr()
	{
		return batchSeqNbr;
	}

	public void setBatchSeqNbr(String batchSeqNbr)
	{
		this.batchSeqNbr = batchSeqNbr;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getExtData()
	{
		return extData;
	}

	public void setExtData(String extData)
	{
		this.extData = extData;
	}

	public String getGetAvsResult()
	{
		return getAvsResult;
	}

	public void setGetAvsResult(String getAvsResult)
	{
		this.getAvsResult = getAvsResult;
	}

	public String getGetAvsResultTxt()
	{
		return getAvsResultTxt;
	}

	public void setGetAvsResultTxt(String getAvsResultTxt)
	{
		this.getAvsResultTxt = getAvsResultTxt;
	}

	public String getGetCommercialCard()
	{
		return getCommercialCard;
	}

	public void setGetCommercialCard(String getCommercialCard)
	{
		this.getCommercialCard = getCommercialCard;
	}

	public String getGetCVResult()
	{
		return getCVResult;
	}

	public void setGetCVResult(String getCVResult)
	{
		this.getCVResult = getCVResult;
	}

	public String getGetCVResultTxt()
	{
		return getCVResultTxt;
	}

	public void setGetCVResultTxt(String getCVResultTxt)
	{
		this.getCVResultTxt = getCVResultTxt;
	}

	public String getGetStreetMatchTxt()
	{
		return getStreetMatchTxt;
	}

	public void setGetStreetMatchTxt(String getStreetMatchTxt)
	{
		this.getStreetMatchTxt = getStreetMatchTxt;
	}

	public String getGetZipMatchTxt()
	{
		return getZipMatchTxt;
	}

	public void setGetZipMatchTxt(String getZipMatchTxt)
	{
		this.getZipMatchTxt = getZipMatchTxt;
	}

	public String getHostCode()
	{
		return hostCode;
	}

	public void setHostCode(String hostCode)
	{
		this.hostCode = hostCode;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getMessage1()
	{
		return message1;
	}

	public void setMessage1(String message1)
	{
		this.message1 = message1;
	}

	public String getMessage2()
	{
		return message2;
	}

	public void setMessage2(String message2)
	{
		this.message2 = message2;
	}

	public String getPnrRef()
	{
		return pnrRef;
	}

	public void setPnrRef(String pnrRef)
	{
		this.pnrRef = pnrRef;
	}

	public String getResponseMsg()
	{
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg)
	{
		this.responseMsg = responseMsg;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getTheResponseXmlStr()
	{
		return theResponseXmlStr;
	}

	public void setTheResponseXmlStr(String theResponseXmlStr)
	{
		this.theResponseXmlStr = theResponseXmlStr;
	}

	public String getHostURL()
	{
		return hostURL;
	}

	public void setHostURL(String hostURL)
	{
		this.hostURL = hostURL;
	}

	public String getReceiptURL()
	{
		return receiptURL;
	}

	public void setReceiptURL(String receiptURL)
	{
		this.receiptURL = receiptURL;
	}

	public String getGetGetOrigResult()
	{
		return getGetOrigResult;
	}

	public void setGetGetOrigResult(String getGetOrigResult)
	{
		this.getGetOrigResult = getGetOrigResult;
	}

	public String getWorkingKey()
	{
		return workingKey;
	}

	public void setWorkingKey(String workingKey)
	{
		this.workingKey = workingKey;
	}

	public String getKeyPointer()
	{
		return keyPointer;
	}

	public void setKeyPointer(String keyPointer)
	{
		this.keyPointer = keyPointer;
	}

	public String getInvNum()
	{
		return invNum;
	}

	public void setInvNum(String invNum)
	{
		this.invNum = invNum;
	}

	public String getCardName()
	{
		return cardName;
	}

	public void setCardName(String cardName)
	{
		this.cardName = cardName;
	}

	public String getExpiryDate()
	{
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate)
	{
		this.expiryDate = expiryDate;
	}

	public String getNameOnCard()
	{
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard)
	{
		this.nameOnCard = nameOnCard;
	}

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getPrevPnrRef()
	{
		return prevPnrRef;
	}

	public void setPrevPnrRef(String prevPnrRef)
	{
		this.prevPnrRef = prevPnrRef;
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

	/**
	 * @return the invoiceAmtWithTip
	 */
	public String getInvoiceAmtWithTip()
	{
		return invoiceAmtWithTip;
	}

	/**
	 * @param invoiceAmtWithTip
	 *            the invoiceAmtWithTip to set
	 */
	public void setInvoiceAmtWithTip(String invoiceAmtWithTip)
	{
		this.invoiceAmtWithTip = invoiceAmtWithTip;
	}

	@Override
	public String toString() {
		return "PaymentGatewayResponse [authCode=" + authCode + ", extData=" + extData + ", getAvsResult="
				+ getAvsResult + ", getAvsResultTxt=" + getAvsResultTxt + ", getCommercialCard=" + getCommercialCard
				+ ", getCVResult=" + getCVResult + ", getCVResultTxt=" + getCVResultTxt + ", getStreetMatchTxt="
				+ getStreetMatchTxt + ", getZipMatchTxt=" + getZipMatchTxt + ", hostCode=" + hostCode + ", message="
				+ message + ", message1=" + message1 + ", message2=" + message2 + ", pnrRef=" + pnrRef
				+ ", responseMsg=" + responseMsg + ", result=" + result + ", theResponseXmlStr=" + theResponseXmlStr
				+ ", hostURL=" + hostURL + ", receiptURL=" + receiptURL + ", getGetOrigResult=" + getGetOrigResult
				+ ", workingKey=" + workingKey + ", keyPointer=" + keyPointer + ", invNum=" + invNum + ", cardName="
				+ cardName + ", expiryDate=" + expiryDate + ", nameOnCard=" + nameOnCard + ", cardNumber=" + cardNumber
				+ ", batchId=" + batchId + ", txnCnt=" + txnCnt + ", batchSeqNbr=" + batchSeqNbr + ", prevPnrRef="
				+ prevPnrRef + ", batchItemCount=" + batchItemCount + ", netBatchTotal=" + netBatchTotal
				+ ", creditPurchaseCount=" + creditPurchaseCount + ", creditPurchaseAmount=" + creditPurchaseAmount
				+ ", creditReturnCount=" + creditReturnCount + ", creditReturnAmount=" + creditReturnAmount
				+ ", debitPurchaseCount=" + debitPurchaseCount + ", debitPurchaseAmount=" + debitPurchaseAmount
				+ ", debitReturnCount=" + debitReturnCount + ", debitReturnAmount=" + debitReturnAmount
				+ ", acqRefData=" + acqRefData + ", invoicePaidAmount=" + invoicePaidAmount + ", invoicePaidTip="
				+ invoicePaidTip + ", invoiceAmtWithTip=" + invoiceAmtWithTip + "]";
	}
	
	
}
