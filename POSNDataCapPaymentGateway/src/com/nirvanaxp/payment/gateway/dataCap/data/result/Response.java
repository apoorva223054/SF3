/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.data.result;

public class Response
{

	private String rspCode;

	private String rspText;

	private String authCode;

	private String avsRsltCode;

	private String cvvRsltCode;

	private String refNbr;

	private String cvvResultCodeAction;

	private String cardType;

	private String cvvRsltText;

	private String avsRsltText;

	private String batchId;

	private String txnCnt;

	private String totalAmt;

	private String batchSeqNbr;

	public String getRspCode()
	{
		return rspCode;
	}

	public void setRspCode(String rspCode)
	{
		this.rspCode = rspCode;
	}

	public String getRspText()
	{
		return rspText;
	}

	public void setRspText(String rspText)
	{
		this.rspText = rspText;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(String authCode)
	{
		this.authCode = authCode;
	}

	public String getAvsRsltCode()
	{
		return avsRsltCode;
	}

	public void setAvsRsltCode(String avsRsltCode)
	{
		this.avsRsltCode = avsRsltCode;
	}

	public String getCvvRsltCode()
	{
		return cvvRsltCode;
	}

	public void setCvvRsltCode(String cvvRsltCode)
	{
		this.cvvRsltCode = cvvRsltCode;
	}

	public String getRefNbr()
	{
		return refNbr;
	}

	public void setRefNbr(String refNbr)
	{
		this.refNbr = refNbr;
	}

	public String getCvvResultCodeAction()
	{
		return cvvResultCodeAction;
	}

	public void setCvvResultCodeAction(String cvvResultCodeAction)
	{
		this.cvvResultCodeAction = cvvResultCodeAction;
	}

	public String getCardType()
	{
		return cardType;
	}

	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	public String getCvvRsltText()
	{
		return cvvRsltText;
	}

	public void setCvvRsltText(String cvvRsltText)
	{
		this.cvvRsltText = cvvRsltText;
	}

	public String getAvsRsltText()
	{
		return avsRsltText;
	}

	public void setAvsRsltText(String avsRsltText)
	{
		this.avsRsltText = avsRsltText;
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

	public String getTotalAmt()
	{
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt)
	{
		this.totalAmt = totalAmt;
	}

	public String getBatchSeqNbr()
	{
		return batchSeqNbr;
	}

	public void setBatchSeqNbr(String batchSeqNbr)
	{
		this.batchSeqNbr = batchSeqNbr;
	}

}
