package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigInt_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T12:53:42.062+0530")
@StaticMetamodel(OrderHeaderToSalesTax.class)
public class OrderHeaderToSalesTax_ extends POSNirvanaBaseClassWithBigInt_ {
	public static volatile SingularAttribute<OrderHeaderToSalesTax, BigDecimal> taxRate;
	public static volatile SingularAttribute<OrderHeaderToSalesTax, String> taxName;
	public static volatile SingularAttribute<OrderHeaderToSalesTax, String> taxDisplayName;
	public static volatile SingularAttribute<OrderHeaderToSalesTax, String> orderHeaderId;
	public static volatile SingularAttribute<OrderHeaderToSalesTax, String> taxId;
	public static volatile SingularAttribute<OrderHeaderToSalesTax, BigDecimal> taxValue;
}
