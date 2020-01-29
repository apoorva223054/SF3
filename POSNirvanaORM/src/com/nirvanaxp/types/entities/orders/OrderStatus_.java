package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-26T17:28:50.709+0530")
@StaticMetamodel(OrderStatus.class)
public class OrderStatus_ {
	public static volatile SingularAttribute<OrderStatus, String> id;
	public static volatile SingularAttribute<OrderStatus, Date> created;
	public static volatile SingularAttribute<OrderStatus, String> createdBy;
	public static volatile SingularAttribute<OrderStatus, String> description;
	public static volatile SingularAttribute<OrderStatus, String> displayName;
	public static volatile SingularAttribute<OrderStatus, Integer> displaySequence;
	public static volatile SingularAttribute<OrderStatus, Integer> isServerDriven;
	public static volatile SingularAttribute<OrderStatus, Integer> isOrderTracking;
	public static volatile SingularAttribute<OrderStatus, String> locationsId;
	public static volatile SingularAttribute<OrderStatus, String> name;
	public static volatile SingularAttribute<OrderStatus, String> orderSourceGroupId;
	public static volatile SingularAttribute<OrderStatus, String> statusColour;
	public static volatile SingularAttribute<OrderStatus, Date> updated;
	public static volatile SingularAttribute<OrderStatus, String> updatedBy;
	public static volatile SingularAttribute<OrderStatus, String> status;
	public static volatile SingularAttribute<OrderStatus, String> imageUrl;
	public static volatile SingularAttribute<OrderStatus, Integer> isSendSms;
	public static volatile SingularAttribute<OrderStatus, Integer> templateId;
}
