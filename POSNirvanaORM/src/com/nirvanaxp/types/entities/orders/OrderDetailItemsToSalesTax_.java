package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigInt_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-04-05T12:51:44.173+0530")
@StaticMetamodel(OrderDetailItemsToSalesTax.class)
public class OrderDetailItemsToSalesTax_ extends POSNirvanaBaseClassWithBigInt_ {
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, BigDecimal> taxRate;
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, String> taxName;
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, String> taxDisplayName;
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, String> orderDetailItemsId;
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, BigDecimal> taxValue;
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, String> taxId;
	public static volatile SingularAttribute<OrderDetailItemsToSalesTax, Integer> isOrderLevelTax;
}
