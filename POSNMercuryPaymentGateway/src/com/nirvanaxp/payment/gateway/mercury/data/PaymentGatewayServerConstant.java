/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.mercury.data;

public class PaymentGatewayServerConstant
{

	public static String CREDIT_PREAUTH = "/Credit/PreAuth";

	public static String CREDIT_PREAUTHCAPTURE = "/Credit/PreAuthCaptureByRecordNo";

	public static String CREDIT_VOID = "/Credit/VoidSaleByRecordNo";

	public static String CREDIT_BATCHSUMMERY = "/Admin/BatchSummary";

	public static String CREDIT_BATCHCLOSE = "/Admin/BatchClose";

	public static String GIFT_ISSUE = "/PrePaid/Issue";

	public static String GIFT_VOID_ISSUE = "/PrePaid/VoidIssue";

	public static String GIFT_SALE = "/PrePaid/Sale";

	public static String GIFT_VOID_SALE = "/PrePaid/VoidSale";

	public static String GIFT_NONS_SALE = "/PrePaid/NoNSFSale";

	public static String GIFT_VOID_RELOAD = "/PrePaid/VoidReload";

	public static String GIFT_VOID_RETURN = "/PrePaid/VoidReturn";

	public static String GIFT_BALANCE = "/PrePaid/Balance";

}
