package com.nirvanaxp.types.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T12:08:25.874+0530")
@StaticMetamodel(OrderLocking.class)
public class OrderLocking_ extends POSNirvanaBaseClassWithBigInt_ {
	public static volatile SingularAttribute<OrderLocking, String> userId;
	public static volatile SingularAttribute<OrderLocking, String> sessionId;
	public static volatile SingularAttribute<OrderLocking, String> locationId;
	public static volatile SingularAttribute<OrderLocking, String> orderId;
	public static volatile SingularAttribute<OrderLocking, String> orderNumber;
}
