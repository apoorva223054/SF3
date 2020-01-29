/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.data.response;

/**
 * @author Pos Nirvana
 * 
 */
public class PaymentGatewayDataCapResponse
{

	/**
	 * Returns the transaction result code from the payment processor.This value
	 * can be either an approval code, for approved transactions, or an error
	 * code, for declined transactions
	 */
	private String authCode;

	/**
	 * Returns extra data from the processed transaction.The value of ExtData
	 * will be in a specific format. The format typically consists of the name
	 * of the data field, an equal sign, and then the value for the data field.
	 * Multiple data fields are separated with a comma. See the 'Web Service
	 * ExtData Response Field Data Elements' for full description of data
	 * elements that can be returned. The following is an example of the format:
	 * ExtName1=ExtValue1,ExtName2=ExtValue2
	 */
	private String extData;

	/**
	 * Returns the overall address verification result code from the payment
	 * processor.When programmatically validating an AVS Result, this value
	 * should ALWAYS be used instead of any formatted response message
	 * describing the result
	 */
	private String getAvsResult;

	/**
	 * Returns the formatted response message when address verification is
	 * performed.Do NOT use this when programmatically validating a
	 * transaction's AVS result; please see GetAVSResult field
	 */
	private String getAvsResultTxt;

	/**
	 * Returns the payment processor's response indicator that specifies if the
	 * card is a commercial card.This value is only applicable to credit card
	 * transactions. The card verification number is typically printed on the
	 * back of the card and not embossed on the front. It is used as an extra
	 * authentication method for "card not present" transactions. When
	 * programmatically validating a CV Result, this value should ALWAYS be used
	 * instead of any formatted response message describing the result
	 */
	private String getCommercialCard;

	/**
	 * Returns the card verification result code from the payment processor.This
	 * value is only applicable to credit card transactions. The card
	 * verification number is typically printed on the back of the card and not
	 * embossed on the front. It is used as an extra authentication method for
	 * "card not present" transactions. When programmatically validating a CV
	 * Result, this value should ALWAYS be used instead of any formatted
	 * response message describing the result
	 */
	private String getCVResult;

	/**
	 * Returns the formatted response message when card verification is
	 * performed.This value is only applicable to credit card transactions. Do
	 * NOT use this when programmatically validating a transaction's CV result;
	 * please see GetCVResult field
	 */
	private String getCVResultTxt;

	/**
	 * Returns the formatted response message when street number address
	 * verification is performed.This value will typically be 'Match', for
	 * correctly matching the street address, or 'No Match', for an incorrect
	 * street address
	 */
	private String getStreetMatchTxt;

	/**
	 * Returns the formatted response message when zip code address verification
	 * is performed.This value will typically be 'Match', for correctly matching
	 * the zip code, or 'No Match', for an incorrect zip code
	 */
	private String getZipMatchTxt;
	/**
	 * Typically returns a number which uniquely identifies the transaction in
	 * the payment processor.This value may not be returned for all payment
	 * processors
	 */
	private String hostCode;
	/**
	 * Returns a formatted response message concerning the processed
	 * transaction.This value will typically be 'APPROVAL', for approved
	 * transactions, or an error message, for declined transactions. Do NOT use
	 * this when programmatically validating a transaction's result; please see
	 * Result field below
	 */
	private String message;
	/**
	 * Returns an extra formatted response message giving more information about
	 * the processed transaction.The Payment Server will only populate this
	 * field when there is applicable information from the payment processor to
	 * return
	 */
	private String message1;
	/**
	 * Returns an extra formatted response message giving more information about
	 * the processed transaction.The Payment Server will only populate this
	 * field when there is applicable information from the payment processor to
	 * return
	 */
	private String message2;
	/**
	 * Returns a number which uniquely identifies the transaction in the payment
	 * gateway
	 */
	private String pnrRef;

	/**
	 * Returns the response message concerning the processed transaction.This
	 * value is typically either Approved or Declined. Do NOT use this when
	 * programmatically validating a transaction's result; please see Result
	 * field below
	 */
	private String responseMsg;
	/**
	 * Returns the transaction result code from the payment gateway which
	 * signifies the result of the transaction (i.e. approved, decline,
	 * etc.)When programmatically validating a transaction's result, this value
	 * should ALWAYS be used instead of any response message describing the
	 * result. See the 'Result Response Fields Definitions' section for a full
	 * list of result values and descriptions
	 */
	private String result;

	/**
	 * keeps the entire response in xml format obtained from server
	 */
	private String theResponseXmlStr;

	/**
	 * url to which request was made
	 */
	private String hostURL;

	private String receiptURL;

	private String getGetOrigResult;

	private String workingKey;

	private String keyPointer;

	private String invNum;

	private String cardName;

	/**
     * 
     */
	private String expiryDate;

	/**
     * 
     */
	private String nameOnCard;

	private String cardNumber;

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

	/*
	 * public Response(String responseXmlStr) {
	 * 
	 * try { theResponseXmlStr = responseXmlStr; result =
	 * Util.retriveFromXMLTag(responseXmlStr, "Result"); responseMsg =
	 * Util.retriveFromXMLTag(responseXmlStr, "RespMSG"); message =
	 * Util.retriveFromXMLTag(responseXmlStr, "Message"); message1 =
	 * Util.retriveFromXMLTag(responseXmlStr, "Message1"); message2 =
	 * Util.retriveFromXMLTag(responseXmlStr, "Message2"); authCode =
	 * Util.retriveFromXMLTag(responseXmlStr, "AuthCode"); pnrRef =
	 * Util.retriveFromXMLTag(responseXmlStr, "PNRef"); hostCode =
	 * Util.retriveFromXMLTag(responseXmlStr, "HostCode"); hostURL =
	 * Util.retriveFromXMLTag(responseXmlStr, "HostURL"); receiptURL =
	 * Util.retriveFromXMLTag(responseXmlStr, "ReceiptURL"); getAvsResult =
	 * Util.retriveFromXMLTag(responseXmlStr, "GetAVSResult"); getAvsResultTxt =
	 * Util.retriveFromXMLTag(responseXmlStr, "GetAVSResultTXT");
	 * getStreetMatchTxt = Util.retriveFromXMLTag(responseXmlStr,
	 * "GetStreetMatchTXT"); getZipMatchTxt =
	 * Util.retriveFromXMLTag(responseXmlStr, "GetZipMatchTXT"); getCVResult =
	 * Util.retriveFromXMLTag(responseXmlStr, "GetCVResult"); getCVResultTxt =
	 * Util.retriveFromXMLTag(responseXmlStr, "GetCVResultTXT");
	 * getGetOrigResult = Util.retriveFromXMLTag(responseXmlStr,
	 * "GetGetOrigResult"); getCommercialCard =
	 * Util.retriveFromXMLTag(responseXmlStr, "GetCommercialCard"); workingKey =
	 * Util.retriveFromXMLTag(responseXmlStr, "WorkingKey"); keyPointer =
	 * Util.retriveFromXMLTag(responseXmlStr, "KeyPointer"); invNum =
	 * Util.retriveFromXMLTag(responseXmlStr, "InvNum"); String cardType =
	 * Util.retriveFromXMLTag(responseXmlStr, "CardType"); extData =
	 * Util.retriveFromXMLTag(responseXmlStr, "ExtData"); } catch (Exception e)
	 * { 
	 * DataErrorUtil.appendLog(e.toString()) ; logger.severe(httpRequest, e); }
	 */
	// }

}
