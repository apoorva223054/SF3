/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.gateway.data;

public class SupportedCardType
{

	public static SupportedCardType CARD_TYPE_CREDIT = new SupportedCardType("CreditCard");
	public static SupportedCardType CARD_TYPE_DEBITCARD = new SupportedCardType("DebitCard");

	public String supportedCard;

	/**
	 * @param name
	 */
	public SupportedCardType(String supportedCard)
	{
		super();
		this.supportedCard = supportedCard;
	}

	public String getName()
	{
		return supportedCard;
	}

	public void setName(String supportedCard)
	{
		this.supportedCard = supportedCard;
	}

	public boolean equals(Object object)
	{

		if (object != null && object instanceof SupportedCardType)
		{

			SupportedCardType supportedCardTypeObj = (SupportedCardType) object;
			if (supportedCard.equals(supportedCardTypeObj.getName()))
			{
				return true;
			}
			return false;
		}

		return false;
	}

}
