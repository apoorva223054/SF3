/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mosambee.transact.amx.parser;

import com.nirvanaxp.payment.gateway.mosambee.service.Util;
import com.nirvanaxp.payment.gateway.mosambee.transact.amx.data.response.PaymentGatewayMosambeeResponse;

public class ParseCreditCardResponse
{

	public PaymentGatewayMosambeeResponse Parse(String responseXmlStr)
	{

		PaymentGatewayMosambeeResponse response = new PaymentGatewayMosambeeResponse();
		response.setTheResponseXmlStr(responseXmlStr);
		response.setResult(Util.retriveFromXMLTag(responseXmlStr, "Result"));
		response.setResponseMsg(Util.retriveFromXMLTag(responseXmlStr, "RespMSG"));
		response.setMessage(Util.retriveFromXMLTag(responseXmlStr, "Message"));
		response.setMessage1(Util.retriveFromXMLTag(responseXmlStr, "Message1"));
		response.setMessage2(Util.retriveFromXMLTag(responseXmlStr, "Message2"));
		response.setAuthCode(Util.retriveFromXMLTag(responseXmlStr, "AuthCode"));
		response.setPnrRef(Util.retriveFromXMLTag(responseXmlStr, "PNRef"));
		response.setHostCode(Util.retriveFromXMLTag(responseXmlStr, "HostCode"));
		response.setHostURL(Util.retriveFromXMLTag(responseXmlStr, "HostURL"));
		response.setReceiptURL(Util.retriveFromXMLTag(responseXmlStr, "ReceiptURL"));
		response.setGetAvsResult(Util.retriveFromXMLTag(responseXmlStr, "GetAVSResult"));
		response.setGetAvsResultTxt(Util.retriveFromXMLTag(responseXmlStr, "GetAVSResultTXT"));
		response.setGetStreetMatchTxt(Util.retriveFromXMLTag(responseXmlStr, "GetStreetMatchTXT"));
		response.setGetZipMatchTxt(Util.retriveFromXMLTag(responseXmlStr, "GetZipMatchTXT"));
		response.setGetCVResult(Util.retriveFromXMLTag(responseXmlStr, "GetCVResult"));
		response.setGetCVResultTxt(Util.retriveFromXMLTag(responseXmlStr, "GetCVResultTXT"));
		response.setGetGetOrigResult(Util.retriveFromXMLTag(responseXmlStr, "GetGetOrigResult"));
		response.setGetCommercialCard(Util.retriveFromXMLTag(responseXmlStr, "GetCommercialCard"));
		response.setWorkingKey(Util.retriveFromXMLTag(responseXmlStr, "WorkingKey"));
		response.setKeyPointer(Util.retriveFromXMLTag(responseXmlStr, "KeyPointer"));
		response.setInvNum(Util.retriveFromXMLTag(responseXmlStr, "InvNum"));
		// String cardType = Util.retriveFromXMLTag(responseXmlStr, "CardType");
		response.setExtData(Util.retriveFromXMLTag(responseXmlStr, "ExtData"));
		if (response.getExtData() != null)
		{
			String cardName = Util.getCardNameFromExtData(response.getExtData());
			if (cardName != null)
			{
				response.setCardName(cardName);
			}
		}

		return response;

	}
}
