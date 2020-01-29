/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.braintree.data;

public class TransactionType
{

	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sale to make a purchase on a credit card
	 */
	public static final TransactionType TRANSACTION_TYPE_SALE = new TransactionType("Sale");

	/**
	 * Adjustment is used to modify an existing tip amount for an original
	 * sale.This applies to the processors that support restaurant adjustment
	 * transactions
	 */
	public static final TransactionType TRANSACTION_TYPE_ADJUSTMENT = new TransactionType("Adjustment");

	/**
	 * Auth to authorize an amount on a credit card
	 */
	public static final TransactionType TRANSACTION_TYPE_AUTH = new TransactionType("Auth");

	/**
	 * Return to credit the cardholder's account
	 */
	public static final TransactionType TRANSACTION_TYPE_RETURN = new TransactionType("Return");

	/**
	 * Void to undo an unsettled transaction. Note: pass the Card Number and
	 * ExpDate with null values on voids
	 */
	public static final TransactionType TRANSACTION_TYPE_VIOD = new TransactionType("Void");

	/**
	 * Force to place an Auth transaction into the current batch (PostAuth) or
	 * to place a transaction not processed through the payment server into the
	 * current batch (ForceAuth)
	 */
	public static final TransactionType TRANSACTION_TYPE_FORCE = new TransactionType("Force");

	/**
	 * Capture to settle a single transaction in the current batch; only for
	 * terminalbased processors
	 */
	public static final TransactionType TRANSACTION_TYPE_CATURE = new TransactionType("Capture");

	/**
	 * CaptureAll to settle all transactions in the current batch; only for
	 * terminalbased processors or host-based processors that support a batch
	 * release feature
	 */
	public static final TransactionType TRANSACTION_TYPE_CAPTURE_ALL = new TransactionType("CaptureAll");

	/**
	 * RepeatSale to perform a recurring billing or installment payment
	 * transaction
	 */
	public static final TransactionType TRANSACTION_TYPE_REPEAT_SALE = new TransactionType("RepeatSale");

	/**
	 * Reversal to perform a manual full reversal on a credit card sale or
	 * repeat sale within 24 hours of sending the original transaction. Its
	 * expected behavior is defined by the type of the payment processor the
	 * merchant account is configured for. Currently, this is only supported
	 * with Global, Tsys and First Data North.
	 */
	public static final TransactionType TRANSACTION_TYPE_REVERSAL = new TransactionType("Reversal");

	public static final TransactionType TRANSACTION_TYPE_CREDIT_AUTH = new TransactionType("credit-auth");

	/**
	 * @param name
	 */
	public TransactionType(String name)
	{
		super();
		this.name = name;
	}

}
