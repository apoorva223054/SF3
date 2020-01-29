package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-01-08T12:02:26.124+0530")
@StaticMetamodel(OrderType.class)
public class OrderType_ {
	public static volatile SingularAttribute<OrderType, Integer> id;
	public static volatile SingularAttribute<OrderType, String> name;
	public static volatile SingularAttribute<OrderType, Date> created;
	public static volatile SingularAttribute<OrderType, String> createdBy;
	public static volatile SingularAttribute<OrderType, String> status;
	public static volatile SingularAttribute<OrderType, Date> updated;
	public static volatile SingularAttribute<OrderType, String> updatedBy;
}
