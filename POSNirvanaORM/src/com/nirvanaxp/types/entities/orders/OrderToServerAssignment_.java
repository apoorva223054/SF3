package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T17:25:46.140+0530")
@StaticMetamodel(OrderToServerAssignment.class)
public class OrderToServerAssignment_ {
	public static volatile SingularAttribute<OrderToServerAssignment, Integer> id;
	public static volatile SingularAttribute<OrderToServerAssignment, String> orderId;
	public static volatile SingularAttribute<OrderToServerAssignment, Integer> server_id;
	public static volatile SingularAttribute<OrderToServerAssignment, Date> created;
	public static volatile SingularAttribute<OrderToServerAssignment, String> createdBy;
	public static volatile SingularAttribute<OrderToServerAssignment, Date> updated;
	public static volatile SingularAttribute<OrderToServerAssignment, String> updatedBy;
	public static volatile SingularAttribute<OrderToServerAssignment, String> status;
}
