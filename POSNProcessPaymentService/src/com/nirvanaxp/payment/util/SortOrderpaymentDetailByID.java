/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment.util;

import java.util.Comparator;

import com.nirvanaxp.types.entities.orders.OrderPaymentDetail;

public class SortOrderpaymentDetailByID implements Comparator<OrderPaymentDetail>
{

	@Override
	public int compare(OrderPaymentDetail orderPaymentDetailAdapter1, OrderPaymentDetail orderPaymentDetailAdapter2)
	{
		if (orderPaymentDetailAdapter1 != null && orderPaymentDetailAdapter2 != null)
		{

			return orderPaymentDetailAdapter1.getId().compareTo(orderPaymentDetailAdapter2.getId());

		}
		return 0;
	}
}