package com.nirvanaxp.types.entities.orders;

import java.math.BigInteger;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-25T12:45:38.920+0530")
@StaticMetamodel(OrderStatusHistory.class)
public class OrderStatusHistory_ {
	public static volatile SingularAttribute<OrderStatusHistory, BigInteger> id;
	public static volatile SingularAttribute<OrderStatusHistory, Date> created;
	public static volatile SingularAttribute<OrderStatusHistory, String> createdBy;
	public static volatile SingularAttribute<OrderStatusHistory, String> orderHeaderId;
	public static volatile SingularAttribute<OrderStatusHistory, String> orderStatusId;
	public static volatile SingularAttribute<OrderStatusHistory, Date> updated;
	public static volatile SingularAttribute<OrderStatusHistory, String> updatedBy;
	public static volatile SingularAttribute<OrderStatusHistory, String> localTime;
}
