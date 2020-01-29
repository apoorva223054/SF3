package com.nirvanaxp.types.entities.orders;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2019-03-27T16:42:52.293+0530")
@StaticMetamodel(OrderSourceGroup.class)
public class OrderSourceGroup_ {
	public static volatile SingularAttribute<OrderSourceGroup, String> id;
	public static volatile SingularAttribute<OrderSourceGroup, Date> created;
	public static volatile SingularAttribute<OrderSourceGroup, String> createdBy;
	public static volatile SingularAttribute<OrderSourceGroup, String> description;
	public static volatile SingularAttribute<OrderSourceGroup, String> displayName;
	public static volatile SingularAttribute<OrderSourceGroup, Integer> displaySequence;
	public static volatile SingularAttribute<OrderSourceGroup, String> locationsId;
	public static volatile SingularAttribute<OrderSourceGroup, String> name;
	public static volatile SingularAttribute<OrderSourceGroup, Date> updated;
	public static volatile SingularAttribute<OrderSourceGroup, String> updatedBy;
	public static volatile SingularAttribute<OrderSourceGroup, String> status;
	public static volatile SingularAttribute<OrderSourceGroup, Integer> isItemizedPrinting;
	public static volatile SingularAttribute<OrderSourceGroup, String> globalOrderSourceGroupId;
	public static volatile SingularAttribute<OrderSourceGroup, Integer> avgWaitTime;
	public static volatile SingularAttribute<OrderSourceGroup, Integer> showAvgWaitTime;
	public static volatile SingularAttribute<OrderSourceGroup, String> avgWaitTimeDisplayName;
}
