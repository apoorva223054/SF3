package com.nirvanaxp.types.entities.orders;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedIdWithoutStatus_;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T10:15:10.608+0530")
@StaticMetamodel(OrderHeaderToSeatDetail.class)
public class OrderHeaderToSeatDetail_ extends POSNirvanaBaseClassWithBigIntWithGeneratedIdWithoutStatus_ {
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> seatId;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> discountId;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> userId;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> balanceDue;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> amountPaid;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> total;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> totalTax;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> priceDiscount;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> discountDisplayName;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> discountName;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> discountValue;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> calculatedDiscountValue;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> subTotal;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, BigDecimal> priceGratuity;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> orderHeaderId;
	public static volatile SingularAttribute<OrderHeaderToSeatDetail, String> localTime;
}
