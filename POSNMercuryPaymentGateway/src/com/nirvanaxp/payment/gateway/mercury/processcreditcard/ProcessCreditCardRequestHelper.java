/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mercury.processcreditcard;

import java.util.HashMap;
import java.util.Map;

public class ProcessCreditCardRequestHelper
{

	public static Map<String, String> createObjectForCreditPreAuth(String invoiceNum, String memo, String invoiceAmount, String frequency, String recordNum, String encryptedFormat, String source,
			String encryptedBlock, String encryptedKey, String operatorId)
	{

		Map<String, String> map = new HashMap<String, String>();
		map.put("InvoiceNo", invoiceNum);
		map.put("RefNo", invoiceNum);
		map.put("Memo", memo);
		map.put("Purchase", invoiceAmount);
		map.put("Authorize", invoiceAmount);

		map.put("Frequency", frequency);
		map.put("RecordNo", recordNum);
		map.put("EncryptedFormat", encryptedFormat);
		map.put("AccountSource", source);
		map.put("EncryptedBlock", encryptedBlock);
		map.put("EncryptedKey", encryptedKey);
		map.put("OperatorID", operatorId);
		return map;

	}

	public static Map<String, String> createObjectForCreditManualPreAuth(String invoiceNum, String memo, String invoiceAmount, String frequency, String source, String address, String zip,
			String cvvdata, String operatorId)
	{

		Map<String, String> map = new HashMap<String, String>();
		map.put("InvoiceNo", invoiceNum);
		map.put("RefNo", invoiceNum);
		map.put("Memo", memo);
		map.put("Purchase", invoiceAmount);
		map.put("Authorize", invoiceAmount);
		map.put("Frequency", frequency);
		map.put("AccountSource", source);

		if (address != null && !(address.equals("")))
		{
			map.put("Address", address);
		}
		if (zip != null && !(zip.equals("")))
		{
			map.put("Zip", zip);
		}

		if (cvvdata != null && !(cvvdata.equals("")))
		{
			map.put("CVVData", cvvdata);
		}
		map.put("OperatorID", operatorId);
		return map;

	}

	public static Map<String, String> createObjectForCreditPreAuthCapture(String invoiceNum, String refNo, String memo, String invoiceAmount, String invoiceTipAmount, String frequency,
			String recordNum, String acqRefData, String authCode)
	{

		Map<String, String> map = new HashMap<String, String>();
		map.put("InvoiceNo", invoiceNum);
		map.put("RefNo", invoiceNum);
		map.put("Memo", memo);
		map.put("Purchase", invoiceAmount);
		map.put("Authorize", invoiceAmount);
		map.put("Gratuity", invoiceTipAmount);
		map.put("Frequency", frequency);
		map.put("RecordNo", recordNum);
		map.put("AcqRefData", acqRefData);
		map.put("AuthCode", authCode);

		return map;

	}

	public static Map<String, String> createObjectForCreditVoidSale(String invoiceNum, String pnRef, String memo, String invoiceAmount, String invoiceTipAmount, String frequency, String recordNum,
			String authCode)
	{

		Map<String, String> map = new HashMap<String, String>();
		map.put("InvoiceNo", invoiceNum);
		map.put("RefNo", pnRef);
		map.put("Memo", memo);
		map.put("Purchase", invoiceAmount);
		map.put("Authorize", invoiceAmount);
		// map.put("Gratuity", invoiceTipAmount);
		map.put("Frequency", frequency);
		map.put("RecordNo", recordNum);
		map.put("AuthCode", authCode);

		return map;

	}

	public static Map<String, String> createObjectForBatchSummary(String memo)
	{

		Map<String, String> map = new HashMap<String, String>();
		map.put("Memo", memo);

		return map;
	}

	public static Map<String, String> createObjectForBatchClose(String memo, String batchNo, String batchItemCount, String netBatchTotal, String creditPurchaseCount, String creditPurchaseAmount,
			String creditReturnCount, String creditReturnAmount, String debitPurchaseCount, String debitPurchaseAmount, String debitReturnCount, String debitReturnAmount)
	{

		Map<String, String> map = new HashMap<String, String>();
		map.put("Memo", memo);
		map.put("BatchNo", batchNo);
		map.put("BatchItemCount", batchItemCount);
		map.put("NetBatchTotal", netBatchTotal);
		map.put("CreditPurchaseCount", creditPurchaseCount);
		map.put("CreditPurchaseAmount", creditPurchaseAmount);
		map.put("CreditReturnCount", creditReturnCount);
		map.put("CreditReturnAmount", creditReturnAmount);
		map.put("DebitPurchaseCount", debitPurchaseCount);
		map.put("DebitPurchaseAmount", debitPurchaseAmount);
		map.put("DebitReturnCount", debitReturnCount);
		map.put("DebitReturnAmount", debitReturnAmount);

		return map;

	}
}
