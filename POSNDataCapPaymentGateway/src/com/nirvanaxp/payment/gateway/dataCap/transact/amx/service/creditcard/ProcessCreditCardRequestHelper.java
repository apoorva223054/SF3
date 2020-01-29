/**
 * Copyright (c) 2012 - 2015 by NirvanaXP Inc. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.dataCap.transact.amx.service.creditcard;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nirvanaxp.server.util.JSONUtility;

public class ProcessCreditCardRequestHelper
{

	public static String createBatchSummaryRequest(String merchantId, String terminalID, String operatorID, String userTrace, String tranType, String tranCode,
			String secureDevice	,String sequenceNo, String tranDeviceID, String pinPadIpAddress, String pinPadIpPort)
	{
		Map tStream = new LinkedHashMap<String, String>();
		Map admin = new LinkedHashMap<String, String>();
		Map child = new LinkedHashMap<String, String>();
		tStream.put("TStream", admin);
		child.put("MerchantID", merchantId);
		child.put("TerminalID", terminalID	);
		child.put("OperatorID", operatorID);
		child.put("UserTrace", userTrace);
		child.put("TranType", tranType);
		child.put("TranCode", tranCode);
		child.put("SecureDevice", secureDevice);
		child.put("SequenceNo", sequenceNo);
		child.put("TranDeviceID", tranDeviceID);
		child.put("PinPadIpAddress", pinPadIpAddress);
		child.put("PinPadIpPort", pinPadIpPort);
		admin.put("Admin", child);
		tStream.put("TStream", admin);
 
		String json = new JSONUtility().convertToJsonString(tStream);
		System.out.println(json);
		return json;
	}
	
	public static String createBatchCloseRequest(String merchantId, String terminalID, String operatorID, String userTrace, String tranType, String tranCode,
			String secureDevice	,String sequenceNo, String tranDeviceID, String pinPadIpAddress, String pinPadIpPort,String netBatchTotal,String batchItemCount,String batchNo)
	{
		Map tStream = new LinkedHashMap<String, String>();
		Map admin = new LinkedHashMap<String, String>();
		Map child = new LinkedHashMap<String, String>();
		tStream.put("TStream", admin);
		child.put("MerchantID", merchantId);
		child.put("TerminalID", terminalID	);
		child.put("OperatorID", operatorID);
		child.put("UserTrace", userTrace);
		child.put("TranType", tranType);
		child.put("TranCode", "BatchClose");
		child.put("SecureDevice", secureDevice);
		child.put("SequenceNo", sequenceNo);
		child.put("TranDeviceID", tranDeviceID);
		child.put("PinPadIpAddress", pinPadIpAddress);
		child.put("PinPadIpPort", pinPadIpPort);
		child.put("BatchItemCount", batchItemCount);
		child.put("NetBatchTotal", netBatchTotal);
		child.put("BatchNo", batchNo);
		admin.put("Admin", child);
		tStream.put("TStream", admin);
 
		String json = new JSONUtility().convertToJsonString(tStream);
		System.out.println(json);
		return json;
	}
	public static String createTipSaveRequest(String gratuity, String purchase, String acqRefData, String authCode, String merchantId, String tranCode,
			String operatorID	,String partialAuth, String tranDeviceID, String pinPadIpAddress,
			String pinPadIpPort,String processData,String recordNo, 
			String refNo,String secureDevice,String sequenceNo,String terminalID,
			 String userTrace)
	{
		Map tStream = new LinkedHashMap<String, String>();
		 
		Map transaction = new LinkedHashMap<String, String>();
		Map transactionAmount = new LinkedHashMap<String, String>();
		Map transactionChild = new LinkedHashMap<String, String>();
		Map child = new LinkedHashMap<String, String>();
	
		transactionChild.put("AcqRefData", acqRefData);
		transactionChild.put("Amount", transactionAmount);
		
		transactionAmount.put("Gratuity", gratuity);
		transactionAmount.put("Purchase", purchase);
		
		transactionChild.put("AuthCode", authCode);
		transactionChild.put("InvoiceNo",refNo);
		transactionChild.put("Frequency", "OneTime");
		transactionChild.put("MerchantID", merchantId);
		transactionChild.put("OperatorID", "TEST");
		transactionChild.put("PartialAuth", partialAuth);
		transactionChild.put("PinPadIpAddress", pinPadIpAddress);
		transactionChild.put("PinPadIpPort", pinPadIpPort);
		transactionChild.put("ProcessData", processData);
		transactionChild.put("RecordNo", recordNo);
		transactionChild.put("RefNo", refNo);
		transactionChild.put("SecureDevice", secureDevice);
		transactionChild.put("SequenceNo", sequenceNo);
		transactionChild.put("TerminalID", terminalID);
		transactionChild.put("TranCode", "AdjustByRecordNo");
		transactionChild.put("TranDeviceID", tranDeviceID);
		transactionChild.put("UserTrace", userTrace);
		 
		 
		transaction.put("Transaction",transactionChild );
		 
		tStream.put("TStream", transaction);
 
		String json = new JSONUtility().convertToJsonString(tStream);
		System.out.println(json);
		return json;
	}
 
	public static String createPreCaptureRequest(String gratuity, String purchase, String acqRefData, String authCode, String merchantId,  
			 String processData,String recordNo,String refNo,String secureDevice,String sequenceNo,String tranDeviceID ,String PinPadIpAddress,String PinPadIpPort)
	{
		Map tStream = new LinkedHashMap<String, String>();
		 
		Map transaction = new LinkedHashMap<String, String>();
		Map transactionAmount = new LinkedHashMap<String, String>();
		Map transactionChild = new LinkedHashMap<String, String>();
		Map child = new LinkedHashMap<String, String>();
		transactionChild.put("MerchantID", merchantId);
		transactionChild.put("TranType", "Credit"); 	
		transactionChild.put("TranCode", "PreAuthCaptureByRecordNo");
		transactionChild.put("SecureDevice", secureDevice);
	//	transactionChild.put("ComPort", "1");
		transactionChild.put("InvoiceNo",refNo);
		transactionChild.put("RefNo", refNo);
		transactionChild.put("AuthCode", authCode);
		transactionChild.put("Amount", transactionAmount);
		
		transactionAmount.put("Gratuity", gratuity);
		transactionAmount.put("Purchase", purchase);
		
		transactionChild.put("SequenceNo", sequenceNo);
		transactionChild.put("RecordNo", recordNo);
		transactionChild.put("Frequency", "OneTime");
		transactionChild.put("AcqRefData", acqRefData);
		transactionChild.put("ProcessData", processData);
		transactionChild.put("TranDeviceID", tranDeviceID);
		transactionChild.put("PinPadIpAddress", PinPadIpAddress);
		transactionChild.put("PinPadIpPort", PinPadIpPort);
		
		transaction.put("Transaction",transactionChild );
		 
		tStream.put("TStream", transaction);

		String json = new JSONUtility().convertToJsonString(tStream);
		System.out.println(json);
		return json;
	}
}
