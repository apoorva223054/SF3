package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutStatus_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T12:52:20.915+0530")
@StaticMetamodel(OrderPaymentDetailsToSalesTax.class)
public class OrderPaymentDetailsToSalesTax_ extends POSNirvanaBaseClassWithoutStatus_ {
	public static volatile SingularAttribute<OrderPaymentDetailsToSalesTax, BigDecimal> taxRate;
	public static volatile SingularAttribute<OrderPaymentDetailsToSalesTax, String> taxName;
	public static volatile SingularAttribute<OrderPaymentDetailsToSalesTax, String> taxDisplayName;
	public static volatile SingularAttribute<OrderPaymentDetailsToSalesTax, String> orderPaymentDetailsId;
	public static volatile SingularAttribute<OrderPaymentDetailsToSalesTax, BigDecimal> taxValue;
	public static volatile SingularAttribute<OrderPaymentDetailsToSalesTax, String> taxId;
}
